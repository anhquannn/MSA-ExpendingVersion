// src/models/product.model.ts

export interface Product {
  Product_Id: number;
  Name: string;
  Image: string;
  Price: number;
  Size?: number;
  Color?: string;
  Specification?: string;
  Description: string;
  Expiry?: string;
  StockNumber: number;
  StockLevel?: 'High' | 'Medium' | 'Low';
  Sales?: number;
  Category_Id: number;
  Manufacturer_Id: number;
}

export interface Category {
  Category_Id: number;
  Name: string;
  Description: string;
  ParentCategory_id?: number; // Cho phép danh mục cha-con
}

export interface Manufacturer {
  Manufacturer_Id: number;
  Name: string;
  Address: string;
  Contact: string;
}