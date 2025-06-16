// Định nghĩa một interface cơ bản cho cấu hình API
interface ApiConfig {
  baseUrl: string;
  // headers?: { [key: string]: string }; // Headers mặc định sẽ được quản lý bằng getAuthHeaders
  // Thêm các cấu hình khác nếu cần, ví dụ: timeout, credentials, v.v.
}

// Định nghĩa các tùy chọn cho từng request
interface RequestOptions {
  headers?: { [key: string]: string };
  params?: { [key: string]: string | number | boolean }; // Tham số cho GET request
  body?: any; // Dữ liệu cho POST/PUT request
  requiresAuth?: boolean; // Mặc định là true, đặt false nếu không cần token (ví dụ: login, register)
}

// Mock Local Storage (thay thế cho localStorage của trình duyệt)
class MockLocalStorage {
  private store: { [key: string]: string } = {};

  setItem(key: string, value: string): void {
    this.store[key] = value;
    console.log(`[LocalStorage] Set ${key}: ${value}`);
  }

  getItem(key: string): string | null {
    const value = this.store[key] || null;
    console.log(`[LocalStorage] Get ${key}: ${value}`);
    return value;
  }

  removeItem(key: string): void {
    delete this.store[key];
    console.log(`[LocalStorage] Removed ${key}`);
  }
}
// Sử dụng MockLocalStorage trong môi trường Node.js hoặc để thử nghiệm
// Trong trình duyệt thực, bạn sẽ dùng `window.localStorage`
const appLocalStorage = typeof window !== 'undefined' ? window.localStorage : new MockLocalStorage();


// Quản lý Token
export class AuthTokenManager {
  private static accessTokenKey = 'accessToken';
  private static refreshTokenKey = 'refreshToken';
  private static isRefreshing = false;
  private static refreshPromise: Promise<string> | null = null; // Để tránh gọi refresh nhiều lần

  static getAccessToken(): string | null {
    return appLocalStorage.getItem(AuthTokenManager.accessTokenKey);
  }

  static setAccessToken(token: string): void {
    appLocalStorage.setItem(AuthTokenManager.accessTokenKey, token);
  }

  static getRefreshToken(): string | null {
    return appLocalStorage.getItem(AuthTokenManager.refreshTokenKey);
  }

  static setRefreshToken(token: string): void {
    appLocalStorage.setItem(AuthTokenManager.refreshTokenKey, token);
  }

  static clearTokens(): void {
    appLocalStorage.removeItem(AuthTokenManager.accessTokenKey);
    appLocalStorage.removeItem(AuthTokenManager.refreshTokenKey);
  }

  // Hàm mock để gọi API refresh token
  // Trong thực tế, bạn sẽ gửi refreshToken đến backend và nhận lại accessToken mới
  static async refreshAuthToken(): Promise<string> {
    if (AuthTokenManager.isRefreshing) {
      return AuthTokenManager.refreshPromise!;
    }

    AuthTokenManager.isRefreshing = true;
    AuthTokenManager.refreshPromise = new Promise(async (resolve, reject) => {
      try {
        const currentRefreshToken = AuthTokenManager.getRefreshToken();
        if (!currentRefreshToken) {
          console.error("No refresh token available to refresh.");
          AuthTokenManager.clearTokens(); // Xóa token nếu không có refresh token
          // Có thể redirect về trang đăng nhập
          reject(new Error("No refresh token"));
          return;
        }

        console.log("Refreshing token...");
        // GIẢ LẬP GỌI API REFRESH TOKEN
        const response = await fetch('https://api.example.com/auth/refresh-token', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({ refreshToken: currentRefreshToken }),
        });

        if (!response.ok) {
          console.error("Failed to refresh token:", response.status, response.statusText);
          AuthTokenManager.clearTokens(); // Xóa token nếu refresh thất bại
          // Có thể redirect về trang đăng nhập
          reject(new Error("Token refresh failed"));
          return;
        }

        const data = await response.json();
        const newAccessToken = data.accessToken;
        const newRefreshToken = data.refreshToken; // Cập nhật cả refresh token nếu backend trả về

        if (newAccessToken) {
          AuthTokenManager.setAccessToken(newAccessToken);
          if (newRefreshToken) {
             AuthTokenManager.setRefreshToken(newRefreshToken);
          }
          console.log("Token refreshed successfully.");
          resolve(newAccessToken);
        } else {
          AuthTokenManager.clearTokens();
          reject(new Error("Refresh token response missing accessToken"));
        }
      } catch (error) {
        console.error("Error during token refresh:", error);
        AuthTokenManager.clearTokens();
        reject(error);
      } finally {
        AuthTokenManager.isRefreshing = false;
        AuthTokenManager.refreshPromise = null;
      }
    });
    return AuthTokenManager.refreshPromise;
  }
}

// Lớp cơ sở để xử lý các cuộc gọi API
class ApiService {
  private config: ApiConfig;

  constructor(config: ApiConfig) {
    this.config = config;
    if (!this.config.baseUrl.endsWith('/')) {
      this.config.baseUrl += '/';
    }
  }

  private getAuthHeaders(requiresAuth: boolean | undefined, customHeaders?: { [key: string]: string }): { [key: string]: string } {
    const headers: { [key: string]: string } = {
      'Content-Type': 'application/json',
    };

    // Chỉ thêm token nếu requiresAuth không được đặt rõ ràng là false
    if (requiresAuth !== false) {
      const accessToken = AuthTokenManager.getAccessToken();
      if (accessToken) {
        headers['Authorization'] = `Bearer ${accessToken}`;
      } else {
        console.warn("Attempting to make authenticated request without an access token.");
      }
    }

    return { ...headers, ...customHeaders };
  }

  private async request<T>(
    method: 'GET' | 'POST' | 'PUT' | 'DELETE',
    endpoint: string,
    options?: RequestOptions
  ): Promise<T> {
    const url = new URL(endpoint, this.config.baseUrl);

    if (method === 'GET' && options?.params) {
      Object.keys(options.params).forEach(key => {
        url.searchParams.append(key, String(options.params![key]));
      });
    }

    const headers = this.getAuthHeaders(options?.requiresAuth, options?.headers);

    const fetchOptions: RequestInit = {
      method,
      headers,
    };

    if (options?.body && (method === 'POST' || method === 'PUT')) {
      fetchOptions.body = JSON.stringify(options.body);
    }

    try {
      let response = await fetch(url.toString(), fetchOptions);

      // Xử lý refresh token nếu response là 401 Unauthorized và request yêu cầu xác thực
      if (response.status === 401 && options?.requiresAuth !== false) {
        console.warn(`401 Unauthorized for ${method} ${url}. Attempting to refresh token...`);
        try {
          const newAccessToken = await AuthTokenManager.refreshAuthToken();
          // Thử lại request với token mới
          const retryHeaders = this.getAuthHeaders(true, options?.headers); // Đảm bảo dùng token mới
          const retryFetchOptions: RequestInit = { ...fetchOptions, headers: retryHeaders };
          response = await fetch(url.toString(), retryFetchOptions);
          console.log(`Retried request for ${method} ${url} with new token.`);
        } catch (refreshError) {
          console.error("Failed to refresh token or retry request:", refreshError);
          // Nếu refresh token thất bại, hoặc retry request vẫn 401
          // Có thể redirect về trang đăng nhập hoặc báo lỗi cho người dùng
          AuthTokenManager.clearTokens(); // Xóa token cũ
          throw new Error("Authentication failed. Please log in again.");
        }
      }

      if (!response.ok) {
        let errorMessage = `HTTP error! Status: ${response.status}`;
        try {
          const errorData = await response.json();
          errorMessage = errorData.message || errorMessage;
        } catch (jsonError) {
          // Nếu không parse được JSON, sử dụng thông báo mặc định
        }
        throw new Error(errorMessage);
      }

      if (response.status === 204) {
        return null as T;
      }

      const data: T = await response.json();
      return data;
    } catch (error) {
      console.error(`API Request failed for ${method} ${url}:`, error);
      throw error;
    }
  }

  // Phương thức GET
  get<T>(endpoint: string, params?: { [key: string]: string | number | boolean }, headers?: { [key: string]: string }, requiresAuth: boolean = true): Promise<T> {
    return this.request<T>('GET', endpoint, { params, headers, requiresAuth });
  }

  // Phương thức POST
  post<T>(endpoint: string, body: any, headers?: { [key: string]: string }, requiresAuth: boolean = true): Promise<T> {
    return this.request<T>('POST', endpoint, { body, headers, requiresAuth });
  }

  // Phương thức PUT
  put<T>(endpoint: string, body: any, headers?: { [key: string]: string }, requiresAuth: boolean = true): Promise<T> {
    return this.request<T>('PUT', endpoint, { body, headers, requiresAuth });
  }

  // Phương thức DELETE
  delete<T>(endpoint: string, headers?: { [key: string]: string }, requiresAuth: boolean = true): Promise<T> {
    return this.request<T>('DELETE', endpoint, { headers, requiresAuth });
  }


  
  // Phương thức login (không cần token, sẽ nhận token)
  async login<T>(credentials: any): Promise<T> {
    const response = await this.request<T>('POST', 'auth/login', { body: credentials, requiresAuth: false });
    // Giả định response chứa accessToken và refreshToken
    if (response && typeof response === 'object' && 'accessToken' in response && 'refreshToken' in response) {
        AuthTokenManager.setAccessToken((response as any).accessToken);
        AuthTokenManager.setRefreshToken((response as any).refreshToken);
    }
    return response;
  }

  // Phương thức logout
  async logout(): Promise<void> {
    // Có thể gọi API logout để invalidate token trên server
    // await this.request<any>('POST', 'auth/logout', { requiresAuth: true });
    AuthTokenManager.clearTokens();
    console.log("Logged out. Tokens cleared.");
  }
}

// --- Cách sử dụng ---

// 1. Khởi tạo ApiService
const apiConfig: ApiConfig = {
  baseUrl: 'http://localhost:1081/msa/api',
};

const api = new ApiService(apiConfig);

// 2. Định nghĩa kiểu dữ liệu cho response
interface Product {
  id: number;
  name: string;
  price: number;
  description?: string;
}

interface AuthResponse {
    accessToken: string;
    refreshToken: string;
    // ... other user data
}

// 3. Gọi các API
async function simulateAppFlow() {
  try {
    console.log("\n--- SIMULATING LOGIN ---");
    // Giả lập đăng nhập để nhận và lưu token
    // (Trong thực tế, đây là kết quả từ server)
    const loginData: AuthResponse = {
        accessToken: "initial_access_token_123",
        refreshToken: "initial_refresh_token_abc"
    };
    // Sử dụng hàm login chuyên biệt
    await api.login<AuthResponse>({ username: 'testuser', password: 'password' }); // Sẽ tự động lưu token

    // Hoặc giả lập lưu thủ công nếu API login được gọi bên ngoài ApiService
    // AuthTokenManager.setAccessToken("initial_access_token_123");
    // AuthTokenManager.setRefreshToken("initial_refresh_token_abc");
    console.log("Access Token after login:", AuthTokenManager.getAccessToken());

    console.log("\n--- GETTING PRODUCTS (AUTHENTICATED) ---");
    // GET request (sẽ tự động thêm token)
    const products = await api.get<Product[]>('products', { category: 'electronics', limit: 10 });
    console.log('Products:', products);

    console.log("\n--- SIMULATING EXPIRED TOKEN AND REFRESH ---");
    // Giả lập token hết hạn (để kiểm tra refresh)
    AuthTokenManager.setAccessToken("expired_access_token");

    // Gọi một API request khác, mong đợi 401 và trigger refresh
    // Chúng ta sẽ mock fetch để nó trả về 401 lần đầu
    console.log("Calling API with expired token...");
    // Để test chức năng refresh, bạn cần một cách để mock `fetch` trả về 401.
    // Dưới đây là cách mô phỏng để minh họa. Trong thực tế, bạn sẽ dùng thư viện mock.
    // Ví dụ:
    (global as any).fetch = async (url: string, init?: RequestInit) => {
        if (url.includes('products') && init?.headers && (init.headers as any)['Authorization'] === 'Bearer expired_access_token') {
            console.log("MOCKING: Returning 401 Unauthorized for expired token.");
            return new Response(JSON.stringify({ message: "Unauthorized: Token expired" }), { status: 401 });
        }
        if (url.includes('auth/refresh-token') && init?.method === 'POST') {
            console.log("MOCKING: Returning new tokens from refresh API.");
            return new Response(JSON.stringify({ accessToken: "new_access_token_456", refreshToken: "new_refresh_token_def" }), { status: 200 });
        }
        // Giả lập cho các request khác hoặc retry
        console.log("MOCKING: Returning 200 OK for other/retried requests.");
        return new Response(JSON.stringify([{ id: 1, name: 'Refreshed Product', price: 99 }]), { status: 200 });
    };

    const productsAfterRefresh = await api.get<Product[]>('products');
    console.log('Products after refresh:', productsAfterRefresh);
    console.log("Access Token after refresh:", AuthTokenManager.getAccessToken());


    console.log("\n--- SIMULATING LOGOUT ---");
    await api.logout();
    console.log("Access Token after logout:", AuthTokenManager.getAccessToken());

    console.log("\n--- GETTING PRODUCTS (NO AUTH) ---");
    // Yêu cầu không cần xác thực
    try {
        const publicProducts = await api.get<Product[]>('public/products', undefined, undefined, false);
        console.log('Public Products:', publicProducts);
    } catch (e) {
        console.error("Error getting public products:", e);
    }


  } catch (error) {
    console.error('An unhandled error occurred:', error);
  }
}

// Chạy luồng mô phỏng
simulateAppFlow();
export default api;
export type { ApiConfig};
// Đặt lại fetch về trạng thái ban đầu sau khi mô phỏng nếu cần (chỉ trong môi trường test)
// delete (global as any).fetch;