// src/models/product.mapper.ts
import { Product, Category, Manufacturer } from '../product.model';

export const fromJsonToProduct = (json: any): Product => {
  return {
    Product_Id: json.Product_Id ?? 0,
    Name: json.Name ?? '',
    Image: json.Image ?? '',
    Price: json.Price ?? 0,
    Size: json.Size,
    Color: json.Color,
    Specification: json.Specification,
    Description: json.Description ?? '',
    Expiry: json.Expiry,
    StockNumber: json.StockNumber ?? 0,
    StockLevel: json.StockLevel,
    Sales: json.Sales,
    Category_Id: json.Category_Id ?? 0,
    Manufacturer_Id: json.Manufacturer_Id ?? 0,
  };
};

export const fromJsonToCategory = (json: any): Category => {
  return {
    Category_Id: json.Category_Id ?? 0,
    Name: json.Name ?? '',
    Description: json.Description ?? '',
    ParentCategory_id: json.ParentCategory_id,
  };
};

export const fromJsonToManufacturer = (json: any): Manufacturer => {
  return {
    Manufacturer_Id: json.Manufacturer_Id ?? 0,
    Name: json.Name ?? '',
    Address: json.Address ?? '',
    Contact: json.Contact ?? '',
  };
};