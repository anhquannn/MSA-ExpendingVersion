// src/models/delivery.mapper.ts
import { DeliveryInfo, DeliveryDetail, Payment } from '../delivery.model';

export const fromJsonToDeliveryInfo = (json: any): DeliveryInfo => {
  return {
    DeliveryInfo_Id: json.DeliveryInfo_Id ?? 0,
    DeliveryDate: json.DeliveryDate ?? '',
    Status: json.Status ?? 'Pending',
    Order_Id: json.Order_Id ?? 0,
    User_Id: json.User_Id ?? 0,
  };
};

export const fromJsonToDeliveryDetail = (json: any): DeliveryDetail => {
  return {
    DeliveryDetail_Id: json.DeliveryDetail_Id ?? 0,
    Description: json.Description ?? '',
    ShipCode: json.ShipCode ?? '',
    Weight: json.Weight ?? 0,
    DeliviryFee: json.DeliviryFee ?? 0,
    DeliveryName: json.DeliveryName ?? '',
    DeliveryAddress: json.DeliveryAddress ?? '',
    DeliveryContact: json.DeliveryContact ?? '',
    Delivery_Id: json.Delivery_Id ?? 0,
  };
};

export const fromJsonToPayment = (json: any): Payment => {
  return {
    Payment_Id: json.Payment_Id ?? 0,
    PaymentMethod: json.PaymentMethod ?? '',
    PaymentDate: json.PaymentDate ?? '',
    Status: json.Status ?? 'Pending',
    GrandTotal: json.GrandTotal ?? 0,
    User_Id: json.User_Id ?? 0,
    Order_Id: json.Order_Id ?? 0,
  };
};