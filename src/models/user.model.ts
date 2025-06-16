// src/models/user.model.ts

export interface User {
  User_Id: number;
  Fullname: string;
  Brithday: string; // Sử dụng string cho Date để dễ dàng xử lý (vd: 'YYYY-MM-DD')
  PhoneNumber: string;
  Email: string;
  Password?: string; // Mật khẩu thường không được gửi về từ API
  Address: string;
  Role: 'admin' | 'customer'; // Dựa trên mô tả ràng buộc
}

export interface Feedback {
  Feedback_Id: number;
  Rating: number; // Điểm đánh giá từ 1-5 
  Comments: string;
  User_Id: number;
  Product_Id: number;
}

export interface LoginPayload {
  email: string;
  password: string;
}