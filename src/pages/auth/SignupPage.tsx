// import React, { useState } from 'react';
// import { useNavigate } from 'react-router-dom';
// import axios from 'axios';

// // Giả định các component này đã được tạo và nằm trong thư mục tương ứng
// import Input from '../../components/common/Input'; // Điều chỉnh đường dẫn
// import Button from '../../components/common/Button'; // Điều chỉnh đường dẫn

// // Đảm bảo đường dẫn đến logo là chính xác
// import logo from '../../assets/images/icon_app.png'; // Điều chỉnh đường dẫn

// const SignupPage: React.FC = () => {
//   const [fullName, setFullName] = useState('');
//   const [email, setEmail] = useState('');
//   const [password, setPassword] = useState('');
//   const [confirmPassword, setConfirmPassword] = useState('');
//   const [error, setError] = useState<string | null>(null);
//   const [loading, setLoading] = useState(false);
//   const navigate = useNavigate();

//   const handleSubmit = async (event: React.FormEvent) => {
//     event.preventDefault();
//     setLoading(true);
//     setError(null);

//     if (password !== confirmPassword) {
//       setError('Mật khẩu và xác nhận mật khẩu không khớp.');
//       setLoading(false);
//       return;
//     }

//     try {
//       // Thay thế 'https://api.yourdomain.com/signup' bằng API endpoint thực tế của bạn
//       const response = await axios.post('https://api.yourdomain.com/signup', {
//         fullName, // Tên người dùng
//         email,
//         password,
//       });

//       // Nếu đăng ký thành công, bạn có thể tự động đăng nhập hoặc chuyển hướng đến trang đăng nhập
//       alert('Đăng ký thành công! Vui lòng đăng nhập.');
//       navigate('/login'); // Chuyển hướng về trang đăng nhập sau khi đăng ký thành công

//     } catch (err: any) {
//       const errorMessage = err.response?.data?.message || 'Có lỗi xảy ra khi đăng ký. Vui lòng thử lại.';
//       setError(errorMessage);
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <div className="min-h-screen flex items-center justify-center relative overflow-hidden p-4">
//       {/* Lớp hình nền - sử dụng class 'bg-login-background' đã khai báo trong tailwind.config.js */}
//       <div 
//         className="absolute inset-0 bg-login-background bg-cover bg-center filter blur-sm scale-105"
//       ></div>

//       {/* Lớp phủ mờ */}
//       <div className="absolute inset-0 bg-black opacity-30"></div>

//       {/* Container chính cho form đăng ký */}
//       <div className="
//         relative z-10
//         w-full max-w-md md:max-w-lg p-8
//         bg-white/80 backdrop-blur-md 
//         shadow-2xl rounded-3xl 
//         transform transition-all duration-700 ease-out scale-95 hover:scale-100 
//         border border-gray-100 overflow-hidden
//       ">
        
//         {/* Hiệu ứng hình học nhẹ nhàng ở góc (cần định nghĩa animation trong tailwind.config.js) */}
//         <div className="absolute -top-10 -left-10 w-24 h-24 bg-green-200/50 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob"></div>
//         <div className="absolute -bottom-10 -right-10 w-24 h-24 bg-blue-200/50 rounded-full mix-blend-multiply filter blur-xl opacity-70 animate-blob animation-delay-2000"></div>

//         {/* Header của form */}
//         <div className="flex flex-col items-center mb-7 relative z-10">
//           <img
//             src={logo}
//             alt="MSA GROCERY Logo"
//             className="w-24 h-24 rounded-full shadow-lg ring-4 ring-green-100 transform transition-transform duration-500 hover:scale-105"
//           />
//           <h1 className="text-3xl font-bold text-gray-800 tracking-tight mt-4">Đăng Ký</h1>
//           <p className="text-base text-gray-500 mt-1">Tạo tài khoản mới</p>
//         </div>

//         {/* Form đăng ký */}
//         <form onSubmit={handleSubmit} className="space-y-5 relative z-10">
//           <Input
//             label="Họ và Tên"
//             type="text"
//             placeholder="Nguyen Van A"
//             value={fullName}
//             onChange={(e) => setFullName(e.target.value)}
//             required
//             disabled={loading}
//           />
//           <Input
//             label="Email"
//             type="email"
//             placeholder="your.email@example.com"
//             value={email}
//             onChange={(e) => setEmail(e.target.value)}
//             required
//             disabled={loading}
//           />
//           <Input
//             label="Mật khẩu"
//             type="password"
//             placeholder="••••••••"
//             value={password}
//             onChange={(e) => setPassword(e.target.value)}
//             required
//             disabled={loading}
//           />
//           <Input
//             label="Xác nhận Mật khẩu"
//             type="password"
//             placeholder="••••••••"
//             value={confirmPassword}
//             onChange={(e) => setConfirmPassword(e.target.value)}
//             required
//             disabled={loading}
//           />

//           {error && <p className="text-red-500 text-sm text-center mt-2 animate-fade-in">{error}</p>}

//           <Button
//             type="submit"
//             disabled={loading}
//             className={`w-full py-3 rounded-xl font-bold transition-all duration-300 ease-in-out
//               ${loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-green-600 hover:bg-green-700 text-white shadow-lg transform hover:-translate-y-0.5'}
//             `}
//           >
//             {loading ? 'Đang đăng ký...' : 'Đăng Ký'}
//           </Button>
//         </form>

//         {/* Footer của form */}
//         <div className="text-center mt-7 space-y-2 relative z-10">
//           <p className="text-sm text-gray-600">
//             Đã có tài khoản? <a href="/login" className="text-blue-600 hover:underline font-medium">Đăng nhập</a>
//           </p>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default SignupPage;

// src/pages/auth/SignupPage.tsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
// import axios from 'axios'; // KHÔNG CẦN AXIOS NỮA, CHÚNG TA SẼ DÙNG apiService

import api from '../../services/api_service'; // <-- Import instance api của bạn
import { routeConstants } from '../../constants/routeConstants'; // Import hằng số route
import Input from '../../components/common/Input'; // Điều chỉnh đường dẫn
import Button from '../../components/common/Button'; // Điều chỉnh đường dẫn

import logo from '../../assets/images/icon_app.png'; // Điều chỉnh đường dẫn

// Định nghĩa interface cho dữ liệu đăng ký mà API mong đợi
interface RegisterPayload {
  fullName: string;
  email: string;
  phoneNumber: string;
  birthday: string; // Định dạng "YYYY-MM-DD HH:MM:SS" theo cURL mẫu
  password: string;
  address: string;
}

// Định nghĩa interface cho phản hồi từ API đăng ký
// API mẫu của bạn không hiển thị phản hồi thành công, nhưng thường có code, message
interface RegisterApiResponse {
  code: number;
  message: string;
  // Nếu có thêm dữ liệu sau khi đăng ký thành công (ví dụ: user id), thêm vào đây
}

const SignupPage: React.FC = () => {
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [birthday, setBirthday] = useState(''); // Giá trị từ input type="datetime-local"
  const [address, setAddress] = useState('');
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

    // Kiểm tra các trường cần thiết khác
    if (!fullName || !email || !phoneNumber || !birthday || !address || !password) {
      setError('Vui lòng điền đầy đủ tất cả các trường.');
      setLoading(false);
      return;
    }

    // --- Định dạng Ngày sinh ---
    let formattedBirthday = birthday;
    // Nếu birthday từ input là "YYYY-MM-DDTHH:MM" (từ datetime-local)
    if (birthday.includes('T')) {
      formattedBirthday = birthday.replace('T', ' ') + ':00'; // Chuyển thành "YYYY-MM-DD HH:MM:00"
    } else if (!birthday.includes(' ')) {
      // Trường hợp chỉ nhập "YYYY-MM-DD"
      formattedBirthday = `${birthday} 00:00:00`;
    }
    // API của bạn yêu cầu định dạng "YYYY-MM-DD HH:MM:SS"

    // --- Kiểm tra tuổi (trên 18) ---
    const dob = new Date(formattedBirthday); // Tạo đối tượng Date từ ngày sinh đã định dạng
    const today = new Date();
    const age = today.getFullYear() - dob.getFullYear();
    const m = today.getMonth() - dob.getMonth();

    // Nếu tháng hiện tại nhỏ hơn tháng sinh, hoặc cùng tháng nhưng ngày hiện tại nhỏ hơn ngày sinh
    // thì tuổi thực tế ít hơn một năm so với phép trừ năm đơn thuần
    if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) {
      // age--; // Logic này đã được tính toán trong cách so sánh dưới đây
    }

    // Kiểm tra nếu tuổi nhỏ hơn 18
    // Tạo một ngày giả định 18 năm trước từ ngày hiện tại
    const minAgeDate = new Date();
    minAgeDate.setFullYear(today.getFullYear() - 18);

    if (dob > minAgeDate) {
      setError('Bạn phải đủ 18 tuổi để đăng ký.');
      setLoading(false);
      return;
    }

    try {
      const payload: RegisterPayload = {
        fullName,
        email,
        phoneNumber,
        birthday: formattedBirthday, // Sử dụng giá trị đã định dạng
        password,
        address,
      };

      const response = await api.post<RegisterApiResponse>('user/register', payload, undefined, false);

      if (response.code === 200) {
        alert('Đăng ký thành công! Vui lòng đăng nhập.');
        navigate(routeConstants.login);
      } else {
        setError(response.message || 'Đăng ký thất bại. Vui lòng thử lại.');
      }

    } catch (err: any) {
      const errorMessage = err.message || 'Có lỗi xảy ra khi đăng ký. Vui lòng thử lại.';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  // ... (phần còn lại của component)
  // src/pages/auth/SignupPage.tsx (Tiếp tục từ phần trên)

  return (
    <div className="min-h-screen flex items-center justify-center relative overflow-hidden p-4">
      {/* Lớp hình nền và lớp phủ mờ */}
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
          <h1 className="text-3xl font-bold text-gray-800 tracking-tight mt-4">Đăng Ký</h1>
          <p className="text-base text-gray-500 mt-1">Tạo tài khoản mới</p>
        </div>

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
            label="Số điện thoại" // <-- THÊM INPUT NÀY
            type="tel" // Sử dụng type="tel" cho số điện thoại
            placeholder="0912345678"
            value={phoneNumber}
            onChange={(e) => setPhoneNumber(e.target.value)}
            required
            disabled={loading}
          />
          <Input
            label="Ngày sinh" // <-- THÊM INPUT NÀY
            type="datetime-local" // Sử dụng datetime-local để nhập ngày và giờ
            // Hoặc type="date" nếu bạn chỉ cần ngày (sau đó cần format thêm " 00:00:00")
            value={birthday}
            onChange={(e) => setBirthday(e.target.value)}
            required
            disabled={loading}
          />
          <Input
            label="Địa chỉ" // <-- THÊM INPUT NÀY
            type="text" // Hoặc type="textarea" nếu bạn muốn một ô lớn hơn
            placeholder="123 Le Loi, District 1, Ho Chi Minh City"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
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