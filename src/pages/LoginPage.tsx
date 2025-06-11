// src/pages/LoginPage.tsx
import React, { useState } from 'react';
import Input from '../components/common//Input';
import Button from '../components/common/Button';
import './css/LoginPage.css'; // Giả sử bạn đã tạo file CSS cho trang đăng nhập

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (event: React.FormEvent) => {
    event.preventDefault();
    // Đây là nơi bạn sẽ gọi API sau này
    console.log('Đăng nhập với:', { email, password });
    alert(`Đăng nhập với Email: ${email}`);
  };

  return (
    <div className="login-page">
      <div className="login-form-container">
        <h1>Đăng Nhập</h1>
        <form onSubmit={handleSubmit}>
          <Input
            label="Email"
            type="email"
            placeholder="Nhập email của bạn"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
          <Input
            label="Mật khẩu"
            type="password"
            placeholder="Nhập mật khẩu"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <Button type="submit">Đăng Nhập</Button>
        </form>
      </div>
    </div>
  );
};

export default LoginPage;