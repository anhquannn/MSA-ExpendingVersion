// src/models/delivery.model.ts

export interface DeliveryInfo {
  DeliveryInfo_Id: number;
  DeliveryDate: string;
  Status: 'Pending' | 'Shipped' | 'Delivered'; // Dựa trên ràng buộc 
  Order_Id: number;
  User_Id: number;
}

export interface DeliveryDetail {
  DeliveryDetail_Id: number;
  Description: string;
  ShipCode: string;
  Weight: number;
  DeliviryFee: number; // Sửa lỗi chính tả từ "DeliviryFee"
  DeliveryName: string;
  DeliveryAddress: string;
  DeliveryContact: string;
  Delivery_Id: number; // Đây là DeliveryInfo_Id
}

export interface Payment {
  Payment_Id: number;
  PaymentMethod: string;
  PaymentDate: string;
  Status: 'Paid' | 'Pending' | 'Failed'; // Dựa trên ràng buộc 
  GrandTotal: number;
  User_Id: number;
  Order_Id: number;
}