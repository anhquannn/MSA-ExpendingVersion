// src/models/order.mapper.ts
import { Order, OrderDetail, ReturnOrder } from '../order.model';

export const fromJsonToOrder = (json: any): Order => {
  return {
    Ordere_Id: json.Ordere_Id ?? 0,
    OrderDate: json.OrderDate ?? '',
    GrandTotal: json.GrandTotal ?? 0,
    User_Id: json.User_Id ?? 0,
    Cart_Id: json.Cart_Id ?? 0,
  };
};

export const fromJsonToOrderDetail = (json: any): OrderDetail => {
  return {
    OrdeDetails_Id: json.OrdeDetails_Id ?? 0,
    Name: json.Name ?? '',
    Quantity: json.Quantity ?? 0,
    UnitPrice: json.UnitPrice ?? 0,
    TotalPrice: json.TotalPrice ?? 0,
    Order_Id: json.Order_Id ?? 0,
    Product_Id: json.Product_Id ?? 0,
  };
};

export const fromJsonToReturnOrder = (json: any): ReturnOrder => {
  return {
    ReturnOrder_Id: json.ReturnOrder_Id ?? 0,
    DateReturn: json.DateReturn ?? '',
    Status: json.Status ?? '',
    Reason: json.Reason ?? '',
    RefundAmount: json.RefundAmount ?? 0,
    Order_Id: json.Order_Id ?? 0,
  };
};