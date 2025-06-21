// interface ApiConfig {
//   baseUrl: string;
// }

// interface RequestOptions {
//   headers?: { [key: string]: string };
//   params?: { [key: string]: string | number | boolean }; // Tham số cho GET request
//   body?: any; 
//   requiresAuth?: boolean; 
// }

// class MockLocalStorage {
//   private store: { [key: string]: string } = {};

//   setItem(key: string, value: string): void {
//     this.store[key] = value;
//     console.log(`[LocalStorage] Set ${key}: ${value}`);
//   }

//   getItem(key: string): string | null {
//     const value = this.store[key] || null;
//     console.log(`[LocalStorage] Get ${key}: ${value}`);
//     return value;
//   }

//   removeItem(key: string): void {
//     delete this.store[key];
//     console.log(`[LocalStorage] Removed ${key}`);
//   }
// }

// const appLocalStorage = typeof window !== 'undefined' ? window.localStorage : new MockLocalStorage();



// export class AuthTokenManager {
//   private static accessTokenKey = 'accessToken';
//   private static refreshTokenKey = 'refreshToken';
//   private static userKey = 'currentUser';
//   private static isRefreshing = false;
//   private static refreshPromise: Promise<string> | null = null; 

//   static getAccessToken(): string | null {
//     return appLocalStorage.getItem(AuthTokenManager.accessTokenKey);
//   }

//   static setAccessToken(token: string): void {
//     appLocalStorage.setItem(AuthTokenManager.accessTokenKey, token);
//   }

//   static getRefreshToken(): string | null {
//     return appLocalStorage.getItem(AuthTokenManager.refreshTokenKey);
//   }

//   static setRefreshToken(token: string): void {
//     appLocalStorage.setItem(AuthTokenManager.refreshTokenKey, token);
//   }

//   static clearTokens(): void {
//     appLocalStorage.removeItem(AuthTokenManager.accessTokenKey);
//     appLocalStorage.removeItem(AuthTokenManager.refreshTokenKey);
//   }

//     static getCurrentUser = (): any | null => {
//     const user = localStorage.getItem(this.userKey);
//     return user ? JSON.parse(user) : null;
//   };
//   static setCurrentUser = (user: any): void => localStorage.setItem(this.userKey, JSON.stringify(user));

//   static clear = (): void => {
//     localStorage.removeItem(this.accessTokenKey);
//     localStorage.removeItem(this.refreshTokenKey);
//     localStorage.removeItem(this.userKey);
//   };

//   static async refreshAuthToken(): Promise<string> {
//     if (AuthTokenManager.isRefreshing) {
//       return AuthTokenManager.refreshPromise!;
//     }

//     AuthTokenManager.isRefreshing = true;
//     AuthTokenManager.refreshPromise = new Promise(async (resolve, reject) => {
//       try {
//         const currentRefreshToken = AuthTokenManager.getRefreshToken();
//         if (!currentRefreshToken) {
//           console.error("No refresh token available to refresh.");
//           AuthTokenManager.clearTokens(); 
//           reject(new Error("No refresh token"));
//           return;
//         }

//         console.log("Refreshing token...");
//         const response = await fetch('https://api.example.com/auth/refresh-token', {
//           method: 'POST',
//           headers: {
//             'Content-Type': 'application/json',
//           },
//           body: JSON.stringify({ refreshToken: currentRefreshToken }),
//         });

//         if (!response.ok) {
//           console.error("Failed to refresh token:", response.status, response.statusText);
//           AuthTokenManager.clearTokens();
//           reject(new Error("Token refresh failed"));
//           return;
//         }

//         const data = await response.json();
//         const newAccessToken = data.accessToken;
//         const newRefreshToken = data.refreshToken; 

//         if (newAccessToken) {
//           AuthTokenManager.setAccessToken(newAccessToken);
//           if (newRefreshToken) {
//              AuthTokenManager.setRefreshToken(newRefreshToken);
//           }
//           console.log("Token refreshed successfully.");
//           resolve(newAccessToken);
//         } else {
//           AuthTokenManager.clearTokens();
//           reject(new Error("Refresh token response missing accessToken"));
//         }
//       } catch (error) {
//         console.error("Error during token refresh:", error);
//         AuthTokenManager.clearTokens();
//         reject(error);
//       } finally {
//         AuthTokenManager.isRefreshing = false;
//         AuthTokenManager.refreshPromise = null;
//       }
//     });
//     return AuthTokenManager.refreshPromise;
//   }
// }
// interface ApiConfig {
//   baseUrl: string;
// }


// class ApiService {
//   private config: ApiConfig;
// private isRefreshing = false;
//   private failedQueue: { resolve: (value: any) => void; reject: (reason?: any) => void; }[] = [];

//  constructor(config: ApiConfig) {
//     this.config = config;
//     if (!this.config.baseUrl.endsWith('/')) {
//       this.config.baseUrl += '/';
//     }
//   }

//   private processFailedQueue = (error: Error | null, token: string | null = null) => {
//     this.failedQueue.forEach(prom => {
//       if (error) {
//         prom.reject(error);
//       } else {
//         prom.resolve(token);
//       }
//     });
//     this.failedQueue = [];
//   };

//   private refreshToken = async (): Promise<string> => {
//     try {
//         const refreshToken = StorageService.getRefreshToken();
//         if (!refreshToken) throw new Error("No refresh token available");

//         const response = await fetch(new URL('user/refresh', this.config.baseUrl).toString(), {
//             method: 'POST',
//             headers: { 'Content-Type': 'application/json' },
//             body: JSON.stringify({ token: refreshToken }),
//         });

//         const data = await response.json();
//         if (!response.ok) throw new Error(data.message || 'Failed to refresh token');

//         const { access_token, refresh_token } = data.result;
//         StorageService.setAccessToken(access_token);
//         StorageService.setRefreshToken(refresh_token);

//         return access_token;
//     } catch (error) {
//         StorageService.clear(); // Xóa token cũ nếu refresh thất bại
//         throw error;
//     }
//   }

//   private getAuthHeaders(requiresAuth: boolean | undefined, customHeaders?: { [key: string]: string }): { [key: string]: string } {
//     const headers: { [key: string]: string } = {
//       'Content-Type': 'application/json',
//     };

//     if (requiresAuth !== false) {
//       const accessToken = AuthTokenManager.getAccessToken();
//       if (accessToken) {
//         headers['Authorization'] = `Bearer ${accessToken}`;
//       } else {
//         console.warn("Attempting to make authenticated request without an access token.");
//       }
//     }

//     return { ...headers, ...customHeaders };
//   }

//  private async request<T>(method: 'GET' | 'POST' | 'PUT' | 'DELETE', endpoint: string, options?: RequestOptions): Promise<T> {
//     const url = new URL(endpoint, this.config.baseUrl);

//     if (method === 'GET' && options?.params) {
//       Object.keys(options.params).forEach(key => url.searchParams.append(key, options.params![key]));
//     }

//     const headers: HeadersInit = { 'Content-Type': 'application/json', ...options?.headers };
//     if (options?.requiresAuth !== false) {
//       const token = StorageService.getAccessToken();
//       if (token) {
//         headers['Authorization'] = `Bearer ${token}`;
//       }
//     }

//     const fetchOptions: RequestInit = { method, headers };
//     if (options?.body) {
//       fetchOptions.body = JSON.stringify(options.body);
//     }

//     try {
//         let response = await fetch(url.toString(), fetchOptions);

//         if (response.status === 401 && options?.requiresAuth !== false) {
//             if (!this.isRefreshing) {
//                 this.isRefreshing = true;
//                 try {
//                     const newAccessToken = await this.refreshToken();
//                     this.processFailedQueue(null, newAccessToken);
//                     // Retry the original request with the new token
//                     headers['Authorization'] = `Bearer ${newAccessToken}`;
//                     fetchOptions.headers = headers;
//                     response = await fetch(url.toString(), fetchOptions);

//                 } catch (err) {
//                     this.processFailedQueue(err, null);
//                     throw err;
//                 } finally {
//                     this.isRefreshing = false;
//                 }
//             } else {
//                 // Nếu đang có một tiến trình refresh khác, hãy chờ nó
//                 return new Promise((resolve, reject) => {
//                     this.failedQueue.push({ resolve, reject });
//                 }).then(newToken => {
//                     headers['Authorization'] = `Bearer ${newToken}`;
//                     fetchOptions.headers = headers;
//                     return fetch(url.toString(), fetchOptions);
//                 }).then(res => res.json());
//             }
//         }

//         const data = await response.json();
//         if (!response.ok) {
//             throw new Error(data.message || `HTTP error! Status: ${response.status}`);
//         }

//         return data;

//     } catch (error) {
//       console.error(`API Request failed: ${method} ${url}`, error);
//       throw error;
//     }
//   }

//   // Phương thức GET
//   get<T>(endpoint: string, params?: { [key: string]: string | number | boolean }, headers?: { [key: string]: string }, requiresAuth: boolean = true): Promise<T> {
//     return this.request<T>('GET', endpoint, { params, headers, requiresAuth });
//   }

//   // Phương thức POST
//   post<T>(endpoint: string, body: any, headers?: { [key: string]: string }, requiresAuth: boolean = true): Promise<T> {
//     return this.request<T>('POST', endpoint, { body, headers, requiresAuth });
//   }

//   // Phương thức PUT
//   put<T>(endpoint: string, body: any, headers?: { [key: string]: string }, requiresAuth: boolean = true): Promise<T> {
//     return this.request<T>('PUT', endpoint, { body, headers, requiresAuth });
//   }

//   // Phương thức DELETE
//   delete<T>(endpoint: string, headers?: { [key: string]: string }, requiresAuth: boolean = true): Promise<T> {
//     return this.request<T>('DELETE', endpoint, { headers, requiresAuth });
//   }


//   async login<T>(credentials: any): Promise<T> {
//     const response = await this.request<T>('POST', 'auth/login', { body: credentials, requiresAuth: false });
//     if (response && typeof response === 'object' && 'accessToken' in response && 'refreshToken' in response) {
//         AuthTokenManager.setAccessToken((response as any).accessToken);
//         AuthTokenManager.setRefreshToken((response as any).refreshToken);
//     }
//     return response;
//   }

//   async logout(): Promise<void> {
//     AuthTokenManager.clearTokens();
//     console.log("Logged out. Tokens cleared.");
//   }
// }
// const apiConfig: ApiConfig = {
//   baseUrl: 'http://localhost:1081/msa/api',
// };

// const api = new ApiService(apiConfig);
// export default api;
// export type { ApiConfig};

// File: src/services/apiService.ts

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
        body: JSON.stringify({ token: refreshToken }),
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