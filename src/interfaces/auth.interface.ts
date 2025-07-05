// src/interfaces/auth.interface.ts

export enum Platform {
  WEB = 'WEB',
  MOBILE = 'MOBILE'
}

export interface LoginCredentials {
  email: string; 
  password: string;
  fcmToken?: string;
  platform?: Platform; // Make platform optional
}

// Kiểu dữ liệu cho phản hồi đăng nhập từ API của bạn
 export interface LoginApiResponse {
  code: number;
  message: string;
  result: {
    authenticated: boolean;
    access_token: string;
    refresh_token: string;
    token_type: string;
    expires_in: number;
    user?: {
      userId: number;
      fullName: string;
      email: string;
      phoneNumber: string | null;
      birthday: string | null;
      address: string | null;
      roles: string[];
    };
  };
}

export interface UserProfileApiResponse {
  code: number;
  message: string;
  result: {
    userId: number; // API của bạn trả về userId là number
    fullName: string;
    email: string;
    phoneNumber: string | null; // Có thể null
    birthday: string | null; // Có thể null
    password: null; // Không cần lưu
    address: string | null; // Có thể null
    image: string | null; // Có thể null
    deviceId: string | null; // Có thể null
    googleId: string | null; // Có thể null
    roles: string | null; // Hoặc một mảng string[] nếu là nhiều vai trò
    branches: string | null; // Hoặc một mảng string[]
  };
}

// Kiểu dữ liệu cho phản hồi refresh token (nếu cần dùng ở ngoài AuthTokenManager)
export interface RefreshTokenApiResponse {
  accessToken: string;
  refreshToken: string;
  // ... các trường khác nếu có
}