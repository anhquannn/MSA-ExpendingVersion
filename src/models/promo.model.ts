// src/models/promo.model.ts

export interface PromoCode {
  PromoCode_Id: number;
  Name: string;
  Code: string;
  Description: string;
  StartDate: string;
  EndDate: string;
  Status: 'Active' | 'Expired'; // Dựa trên ràng buộc 
  DiscountType: 'Percentage' | 'Fixed Amount'; // Dựa trên ràng buộc 
  DiscountPercentTage?: number;
  MinimumOrderValue?: number;
}

export interface OrderPromoCode {
    OrderPromoCode_Id: number;
    PromoCode_Id: number;
    Order_Id: number;
}