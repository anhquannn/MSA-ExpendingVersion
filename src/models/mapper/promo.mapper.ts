// src/models/promo.mapper.ts
import { PromoCode, OrderPromoCode } from '../promo.model';

export const fromJsonToPromoCode = (json: any): PromoCode => {
  return {
    PromoCode_Id: json.PromoCode_Id ?? 0,
    Name: json.Name ?? '',
    Code: json.Code ?? '',
    Description: json.Description ?? '',
    StartDate: json.StartDate ?? '',
    EndDate: json.EndDate ?? '',
    Status: json.Status ?? 'Expired',
    DiscountType: json.DiscountType ?? 'Fixed Amount',
    DiscountPercentTage: json.DiscountPercentTage,
    MinimumOrderValue: json.MinimumOrderValue,
  };
};

export const fromJsonToOrderPromoCode = (json: any): OrderPromoCode => {
    return {
        OrderPromoCode_Id: json.OrderPromoCode_Id ?? 0,
        PromoCode_Id: json.PromoCode_Id ?? 0,
        Order_Id: json.Order_Id ?? 0,
    }
}