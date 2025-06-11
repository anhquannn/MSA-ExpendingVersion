// src/models/order.model.ts

export interface Order {
  Ordere_Id: number;
  OrderDate: string;
  GrandTotal: number;
  User_Id: number;
  Cart_Id: number;
}

export interface OrderDetail {
  OrdeDetails_Id: number;
  Name: string;
  Quantity: number;
  UnitPrice: number;
  TotalPrice: number;
  Order_Id: number;
  Product_Id: number;
}

export interface ReturnOrder {
  ReturnOrder_Id: number;
  DateReturn: string;
  Status: string;
  Reason: string;
  RefundAmount: number;
  Order_Id: number;
}