import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

// Giả định các component này đã được tạo và nằm trong thư mục tương ứng
import Input from '../../components/common/Input';
import Button from '../../components/common/Button';
import { AuthTokenManager, ApiConfig, api } from '../../services/apiService'; // <-- Sửa đường dẫn và import đúng cách
// Đảm bảo đường dẫn đến logo là chính xác
import logo from '../../assets/images/icon_app.png';
import { routeConstants } from '../../constants/routeConstants';
import { LoginApiResponse, LoginCredentials, UserProfileApiResponse } from '../../interfaces/auth.interface';
import { LocalStorageManager, User } from '../../utils/app_storage';
import { userService } from '../../services/userService';
import { getFCMToken } from '../../config/firebaseConfig';
import { NotificationService } from '../../services/notificationService';
import { decodeJwt} from '../../utils/jwt';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    try {
      // Lấy FCM token trước khi đăng nhập
      const fcmToken = await getFCMToken();
      
      const credentials: LoginCredentials = { 
        email, 
        password,
        fcmToken: fcmToken ?? undefined // Convert null to undefined
      };

      const loginResponse = await api.post<LoginApiResponse>('user/admin/login', credentials, undefined);

      if (loginResponse.code === 200 && loginResponse.result.authenticated) {
        AuthTokenManager.setAccessToken(loginResponse.result.access_token);
        AuthTokenManager.setRefreshToken(loginResponse.result.refresh_token);

        const payload = decodeJwt(loginResponse.result.access_token);
        const scopeStr = payload?.scope as string | undefined;
        if (!scopeStr || !scopeStr.includes('ROLE_ADMIN')) {
          setError('Bạn không có quyền ADMIN.');
          AuthTokenManager.clearTokens();
          return;
        }

        // --- Fetch user profile after login and cache it locally ---
        try {
          const [me] = await userService.getInfoUsers();
          if (me) {
            const userToSave: User = {
              User_Id: me.userId,
              Fullname: me.fullName,
              Email: me.email,
              PhoneNumber: me.phoneNumber || '',
              Address: '',
              Role: me.roles?.[0]?.name === 'ADMIN' ? 'admin' : 'customer',
              Brithday: me.birthday || '',
            };
            LocalStorageManager.saveUser(userToSave);
          } else if (loginResponse.result.user) {
            // Fallback if API still returns user object in login response (legacy)
            const legacy = loginResponse.result.user;
            const userToSave: User = {
              User_Id: legacy.userId,
              Fullname: legacy.fullName,
              Email: legacy.email,
              PhoneNumber: legacy.phoneNumber || '',
              Address: legacy.address || '',
              Role: (legacy.roles?.[0] as 'admin' | 'customer') || 'admin',
              Brithday: legacy.birthday || '',
            };
            LocalStorageManager.saveUser(userToSave);
          }
        } catch (e) {
          console.warn('Failed to fetch user info', e);
          // Fallback to legacy user data if available
          if (loginResponse.result.user) {
            const legacy = loginResponse.result.user;
            const userToSave: User = {
              User_Id: legacy.userId,
              Fullname: legacy.fullName,
              Email: legacy.email,
              PhoneNumber: legacy.phoneNumber || '',
              Address: legacy.address || '',
              Role: (legacy.roles?.[0] as 'admin' | 'customer') || 'admin',
              Brithday: legacy.birthday || '',
            };
            LocalStorageManager.saveUser(userToSave);
          }
        }
        try {
          const [me] = await userService.getInfoUsers();
          if (me) {
            const userToSave: User = {
              User_Id: me.userId,
              Fullname: me.fullName,
              Email: me.email,
              PhoneNumber: me.phoneNumber || '',
              Address: '',
              Role: me.roles?.[0]?.name === 'ADMIN' ? 'admin' : 'customer',
              Brithday: me.birthday || '',
            };
            LocalStorageManager.saveUser(userToSave);
          }
        } catch (e) {
          console.warn('Failed to fetch user info', e);
        }
        // Extra safeguard for legacy API response that still returns a user object
        if (loginResponse.result?.user) {
          const legacy = loginResponse.result.user;
          const userToSave: User = {
            User_Id: legacy.userId,
            Fullname: legacy.fullName,
            Email: legacy.email,
            PhoneNumber: legacy.phoneNumber || '',
            Address: legacy.address || '',
            Role: (legacy.roles?.[0] as 'admin' | 'customer') || 'admin',
            Brithday: legacy.birthday || '',
          };
          LocalStorageManager.saveUser(userToSave);
        }

        // Chuyển hướng đến trang dashboard
        // Đăng ký FCM token cho user
        if (fcmToken) {
          try {
            await NotificationService.registerToken(fcmToken, 'WEB');
          } catch (e) {
            console.warn('Không thể đăng ký FCM token:', e);
          }
        }

        navigate(routeConstants.dashboard);
      } else {
        setError(loginResponse.message || 'Đăng nhập thất bại. Vui lòng thử lại.');
      }
    } catch (err: any) {
      const errorMessage = err.message || 'Email hoặc mật khẩu không đúng. Vui lòng thử lại.';  
      setError(errorMessage);
      // Xóa token nếu có lỗi trong quá trình sau khi đăng nhập (đảm bảo sạch trạng thái)
      AuthTokenManager.clearTokens(); 
    } finally {
      setLoading(false);
    }
  };

  // Hàm xử lý đăng nhập bằng Google
  const handleGoogleLogin = () => {
    console.log('Đăng nhập bằng Google...');
    alert('Chức năng đăng nhập bằng Google đang được phát triển!');

  };

  return (
    <div className="min-h-screen flex items-center justify-center relative overflow-hidden p-4">
      <div
        className="absolute inset-0 bg-login-background bg-cover bg-center filter blur-sm scale-105"
      ></div>

      <div className="absolute inset-0 bg-black opacity-30"></div>
      <div className="
        relative z-10
        w-full max-w-md md:max-w-lg p-8
        bg-white/80 backdrop-blur-md 
        shadow-2xl rounded-3xl 
        transform transition-all duration-700 ease-out scale-95 hover:scale-100 
        border border-gray-100 overflow-hidden
      ">
        <div className="absolute -top-10 -left-10 w-24 h-24 bg-green-200/50 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob"></div>
        <div className="absolute -bottom-10 -right-10 w-24 h-24 bg-blue-200/50 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob animation-delay-2000"></div>

        <div className="flex flex-col items-center mb-7 relative z-10">
          <img
            src={logo}
            alt="MSA GROCERY Logo"
            className="w-24 h-24 rounded-full shadow-lg ring-4 ring-green-100 transform transition-transform duration-500 hover:scale-105"
          />
          <h1 className="text-3xl font-bold text-gray-800 tracking-tight mt-4">MSA GROCERY</h1>
          <p className="text-base text-gray-500 mt-1">Đăng nhập để tiếp tục</p>
        </div>
        <form onSubmit={handleSubmit} className="space-y-5 relative z-10">
          <Input
            label="Email"
            type="email"
            placeholder="your.email@example.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            disabled={loading}
          />
          <Input
            label="Mật khẩu"
            type="password"
            placeholder="••••••••"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            disabled={loading}
          />

          {error && <p className="text-red-500 text-sm text-center mt-2 animate-fade-in">{error}</p>}

          <Button
            type="submit"
            disabled={loading}
            className={`w-full py-3 rounded-xl font-bold transition-all duration-300 ease-in-out
              ${loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-green-600 hover:bg-green-700 text-white shadow-lg transform hover:-translate-y-0.5'}
            `}
          >
            {loading ? 'Đang xử lý...' : 'Đăng Nhập'}
          </Button>
        </form>

        <div className="relative flex py-5 items-center relative z-10">
          <div className="flex-grow border-t border-gray-300"></div>
          <span className="flex-shrink mx-4 text-gray-500">HOẶC</span>
          <div className="flex-grow border-t border-gray-300"></div>
        </div>

        <button
          onClick={handleGoogleLogin}
          disabled={loading}
          className={`w-full flex items-center justify-center gap-2 py-3 rounded-xl font-bold 
            border border-gray-300 bg-white text-gray-700 shadow-sm
            transition-all duration-300 ease-in-out transform hover:-translate-y-0.5 hover:shadow-md
            ${loading ? 'opacity-70 cursor-not-allowed' : ''}
          `}
        >
          <svg className="w-5 h-5 mr-2" viewBox="0 0 48 48" xmlns="http://www.w3.org/2000/svg">
            <path fill="#EA4335" d="M24 9.5c3.15 0 5.77 1.09 7.71 2.89l5.77-5.77C33.44 2.69 28.96 0 24 0 14.64 0 6.84 5.94 3.36 14.5l6.88 5.35C12.45 13.07 17.76 9.5 24 9.5z" />
            <path fill="#4285F4" d="M46.47 24.5c0-1.55-.14-3.05-.41-4.5H24v9h12.67c-.55 2.95-2.19 5.46-4.67 7.15l7.19 5.62c4.21-3.9 6.64-9.67 6.64-17.27z" />
            <path fill="#FBBC05" d="M10.24 28.65a14.47 14.47 0 0 1 0-9.3l-6.88-5.35a23.98 23.98 0 0 0 0 20l6.88-5.35z" />
            <path fill="#34A853" d="M24 48c6.48 0 11.93-2.14 15.9-5.8l-7.19-5.62c-2.01 1.35-4.56 2.12-8.71 2.12-6.24 0-11.55-3.57-13.76-8.85l-6.88 5.35C6.84 42.06 14.64 48 24 48z" />
          </svg>
          Đăng nhập với Google
        </button>
        <div className="text-center mt-7 space-y-2 relative z-10">
          <a href="/forgot-password" className="text-sm text-blue-600 hover:underline font-medium">Quên mật khẩu?</a>
          <p className="text-sm text-gray-600">
            Chưa có tài khoản? <a href="/signup" className="text-blue-600 hover:underline font-medium">Đăng ký</a>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;