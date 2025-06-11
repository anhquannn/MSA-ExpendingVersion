// src/models/cart.mapper.ts
import { Cart, CartItem } from '../cart.model';

export const fromJsonToCart = (json: any): Cart => {
  return {
    Cart_Id: json.Cart_Id ?? 0,
    Status: json.Status ?? 'Active',
    User_Id: json.User_Id ?? 0,
  };
};

export const fromJsonToCartItem = (json: any): CartItem => {
  return {
    CartItem_Id: json.CartItem_Id ?? 0,
    Status: json.Status ?? '',
    Price: json.Price ?? 0,
    Quantity: json.Quantity ?? 0,
    Product_Id: json.Product_Id ?? 0,
    Cart_Id: json.Cart_Id ?? 0,
  };
};