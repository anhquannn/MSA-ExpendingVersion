import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

// Giả định các component này đã được tạo và nằm trong thư mục tương ứng
import Input from '../../components/common/Input'; // Điều chỉnh đường dẫn
import Button from '../../components/common/Button'; // Điều chỉnh đường dẫn

// Đảm bảo đường dẫn đến logo là chính xác
import logo from '../../assets/images/icon_app.png'; // Điều chỉnh đường dẫn

const SignupPage: React.FC = () => {
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    setLoading(true);
    setError(null);

    if (password !== confirmPassword) {
      setError('Mật khẩu và xác nhận mật khẩu không khớp.');
      setLoading(false);
      return;
    }

    try {
      // Thay thế 'https://api.yourdomain.com/signup' bằng API endpoint thực tế của bạn
      const response = await axios.post('https://api.yourdomain.com/signup', {
        fullName, // Tên người dùng
        email,
        password,
      });

      // Nếu đăng ký thành công, bạn có thể tự động đăng nhập hoặc chuyển hướng đến trang đăng nhập
      alert('Đăng ký thành công! Vui lòng đăng nhập.');
      navigate('/login'); // Chuyển hướng về trang đăng nhập sau khi đăng ký thành công

    } catch (err: any) {
      const errorMessage = err.response?.data?.message || 'Có lỗi xảy ra khi đăng ký. Vui lòng thử lại.';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center relative overflow-hidden p-4">
      {/* Lớp hình nền - sử dụng class 'bg-login-background' đã khai báo trong tailwind.config.js */}
      <div 
        className="absolute inset-0 bg-login-background bg-cover bg-center filter blur-sm scale-105"
      ></div>

      {/* Lớp phủ mờ */}
      <div className="absolute inset-0 bg-black opacity-30"></div>

      {/* Container chính cho form đăng ký */}
      <div className="
        relative z-10
        w-full max-w-md md:max-w-lg p-8
        bg-white/80 backdrop-blur-md 
        shadow-2xl rounded-3xl 
        transform transition-all duration-700 ease-out scale-95 hover:scale-100 
        border border-gray-100 overflow-hidden
      ">
        
        {/* Hiệu ứng hình học nhẹ nhàng ở góc (cần định nghĩa animation trong tailwind.config.js) */}
        <div className="absolute -top-10 -left-10 w-24 h-24 bg-green-200/50 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob"></div>
        <div className="absolute -bottom-10 -right-10 w-24 h-24 bg-blue-200/50 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob animation-delay-2000"></div>

        {/* Header của form */}
        <div className="flex flex-col items-center mb-7 relative z-10">
          <img
            src={logo}
            alt="MSA GROCERY Logo"
            className="w-24 h-24 rounded-full shadow-lg ring-4 ring-green-100 transform transition-transform duration-500 hover:scale-105"
          />
          <h1 className="text-3xl font-bold text-gray-800 tracking-tight mt-4">Đăng Ký</h1>
          <p className="text-base text-gray-500 mt-1">Tạo tài khoản mới</p>
        </div>

        {/* Form đăng ký */}
        <form onSubmit={handleSubmit} className="space-y-5 relative z-10">
          <Input
            label="Họ và Tên"
            type="text"
            placeholder="Nguyen Van A"
            value={fullName}
            onChange={(e) => setFullName(e.target.value)}
            required
            disabled={loading}
          />
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
          <Input
            label="Xác nhận Mật khẩu"
            type="password"
            placeholder="••••••••"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
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
            {loading ? 'Đang đăng ký...' : 'Đăng Ký'}
          </Button>
        </form>

        {/* Footer của form */}
        <div className="text-center mt-7 space-y-2 relative z-10">
          <p className="text-sm text-gray-600">
            Đã có tài khoản? <a href="/login" className="text-blue-600 hover:underline font-medium">Đăng nhập</a>
          </p>
        </div>
      </div>
    </div>
  );
};

export default SignupPage;