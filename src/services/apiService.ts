export interface ApiResponse<T> {
  code: number;
  message: string;
  result: T;
}
export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalPages: number;
  totalElements: number;
  last: boolean;
}

export interface ApiConfig {
  baseUrl: string;
}
interface RequestOptions {
  headers?: { [key: string]: string };
  params?: { [key: string]: string | number | boolean | null | undefined }; // Cho phép cả null và undefined
  body?: any;
  requiresAuth?: boolean;
}

// --- LỚP QUẢN LÝ LOCAL STORAGE ---
// Thống nhất sử dụng tên AuthTokenManager và appLocalStorage
class AuthTokenManager {
  private static accessTokenKey = 'accessToken';
  private static refreshTokenKey = 'refreshToken';
  private static userKey = 'currentUser';

  static clearTokens(): void {
    localStorage.removeItem(AuthTokenManager.accessTokenKey);
    localStorage.removeItem(AuthTokenManager.refreshTokenKey);
  }
  // --- Token Methods ---
  static getAccessToken = (): string | null => localStorage.getItem(this.accessTokenKey);
  static setAccessToken = (token: string): void => localStorage.setItem(this.accessTokenKey, token);
  static getRefreshToken = (): string | null => localStorage.getItem(this.refreshTokenKey);
  static setRefreshToken = (token: string): void => localStorage.setItem(this.refreshTokenKey, token);

  // --- User Methods ---
  static getCurrentUser = (): any | null => {
    const user = localStorage.getItem(this.userKey);
    return user ? JSON.parse(user) : null;
  };
  static setCurrentUser = (user: any): void => localStorage.setItem(this.userKey, JSON.stringify(user));

  // --- Clear All ---
  static clear = (): void => {
    localStorage.removeItem(this.accessTokenKey);
    localStorage.removeItem(this.refreshTokenKey);
    localStorage.removeItem(this.userKey);
  };
}


// --- LỚP DỊCH VỤ GỌI API ĐÃ ĐƯỢC NÂNG CẤP VÀ SỬA LỖI ---
class ApiService {
  private config: ApiConfig;
  private isRefreshing = false;
  private failedQueue: { resolve: (value: any) => void; reject: (reason?: any) => void; }[] = [];

  constructor(config: ApiConfig) {
    this.config = config;
    if (!this.config.baseUrl.endsWith('/')) {
      this.config.baseUrl += '/';
    }
  }

  private processFailedQueue = (error: Error | null, token: string | null = null) => {
    this.failedQueue.forEach(prom => {
      if (error) {
        prom.reject(error);
      } else {
        prom.resolve(token);
      }
    });
    this.failedQueue = [];
  };

  private refreshToken = async (): Promise<string> => {
    try {
      const refreshToken = AuthTokenManager.getRefreshToken();
      if (!refreshToken) throw new Error("No refresh token available");

      const response = await fetch(new URL('user/refresh', this.config.baseUrl).toString(), {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ refresh_token: refreshToken }),
      });

      const data = await response.json();
      if (!response.ok) throw new Error(data.message || 'Failed to refresh token');

      const { access_token, refresh_token } = data.result;
      AuthTokenManager.setAccessToken(access_token);
      if (refresh_token) { // Chỉ cập nhật nếu có refresh token mới
        AuthTokenManager.setRefreshToken(refresh_token);
      }

      return access_token;
    } catch (error) {
      AuthTokenManager.clear(); // Xóa token cũ nếu refresh thất bại
      throw error;
    }
  }

  private async request<T>(method: 'GET' | 'POST' | 'PUT' | 'DELETE', endpoint: string, options?: RequestOptions): Promise<T> {
    const url = new URL(endpoint, this.config.baseUrl);

    // if (method === 'GET' && options?.params) {
    //   Object.keys(options.params).forEach(key => {
    //     const value = options.params![key];
    //     // Chỉ thêm vào param nếu giá trị không phải null hoặc undefined
    //     if (value !== null && value !== undefined) {
    //       // SỬA LỖI 2: Chuyển đổi giá trị sang string
    //       url.searchParams.append(key, String(value));
    //     }
    //   });
    // }
    if (method === 'GET' && options?.params) {
      Object.keys(options.params).forEach(key => {
        const value = options.params![key];
        if (value !== null && value !== undefined) {
          // Vẫn giữ logic chuyển đổi sang string
          url.searchParams.append(key, String(value));
        }
      });
    }
    const headers: HeadersInit = { 'Content-Type': 'application/json', ...options?.headers };
    if (options?.requiresAuth !== false) {
      const token = AuthTokenManager.getAccessToken();
      if (token) {
        headers['Authorization'] = `Bearer ${token}`;
      }
    }

    const fetchOptions: RequestInit = { method, headers };
    if (options?.body) {
      fetchOptions.body = JSON.stringify(options.body);
    }

    try {
      let response = await fetch(url.toString(), fetchOptions);

      if (response.status === 401 && options?.requiresAuth !== false) {
        if (!this.isRefreshing) {
          this.isRefreshing = true;
          try {
            const newAccessToken = await this.refreshToken();
            this.processFailedQueue(null, newAccessToken);
            headers['Authorization'] = `Bearer ${newAccessToken}`;
            fetchOptions.headers = headers;
            response = await fetch(url.toString(), fetchOptions);

          } catch (err) {
            // SỬA LỖI 3: Kiểm tra kiểu của `err`
            const error = err instanceof Error ? err : new Error('An unknown error occurred during token refresh');
            this.processFailedQueue(error, null);
            throw error;
          } finally {
            this.isRefreshing = false;
          }
        } else {
          return new Promise((resolve, reject) => {
            this.failedQueue.push({ resolve, reject });
          }).then(newToken => {
            headers['Authorization'] = `Bearer ${String(newToken)}`;
            fetchOptions.headers = headers;
            return fetch(url.toString(), fetchOptions);
          }).then(async (res) => {
            // Cần xử lý response từ retry request ở đây
            if (!res.ok) {
              const errorData = await res.json().catch(() => ({ message: `HTTP error! Status: ${res.status}` }));
              throw new Error(errorData.message);
            }
            return res.status === 204 ? (null as T) : res.json();
          });
        }
      }

      // Trả về null nếu status là 204 No Content (thường cho DELETE)
      if (response.status === 204) {
        return null as T;
      }

      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.message || `HTTP error! Status: ${response.status}`);
      }

      return data;

    } catch (error) {
      console.error(`API Request failed: ${method} ${url.pathname}`, error);
      throw error;
    }
  }

  // Các phương thức public không đổi
  // get<T>(endpoint: string, params?: RequestOptions['params'], options?: Omit<RequestOptions, 'params' | 'body'>): Promise<T> {
  //   return this.request<T>('GET', endpoint, { params, ...options });
  // }
get<T>(endpoint: string, params?: Record<string, any>, options?: Omit<RequestOptions, 'params'|'body'>): Promise<T> {
    return this.request<T>('GET', endpoint, { params, ...options });
  }
  post<T>(endpoint: string, body: any, options?: Omit<RequestOptions, 'body' | 'params'>): Promise<T> {
    return this.request<T>('POST', endpoint, { body, ...options });
  }

  put<T>(endpoint: string, body: any, options?: Omit<RequestOptions, 'body' | 'params'>): Promise<T> {
    return this.request<T>('PUT', endpoint, { body, ...options });
  }

  delete<T>(endpoint: string, options?: Omit<RequestOptions, 'body' | 'params'>): Promise<T> {
    return this.request<T>('DELETE', endpoint, { ...options });
  }
}

// --- KHỞI TẠO VÀ EXPORT ---
const apiConfig: ApiConfig = { baseUrl: 'http://localhost:1081/msa/api' };
const api = new ApiService(apiConfig);

export { api, AuthTokenManager }; // Export cả hai để các service khác có thể sử dụng