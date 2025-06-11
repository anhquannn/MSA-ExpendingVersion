// src/models/cart.model.ts

export interface Cart {
  Cart_Id: number;
  Status: 'Active' | 'Checked Out'; // Dựa trên ràng buộc 
  User_Id: number;
}

export interface CartItem {
  CartItem_Id: number;
  Status: string;
  Price: number;
  Quantity: number;
  Product_Id: number;
  Cart_Id: number;
}