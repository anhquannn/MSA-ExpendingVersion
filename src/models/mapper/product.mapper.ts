// src/models/product.mapper.ts
import { Product, Category,  } from '../product.model';

export const fromJsonToProduct = (json: any): Product => {
  return {
    productId: json.Product_Id ?? 0,
    name: json.Name ?? '',
    price: json.Price ?? 0,
    specification: json.Specification ?? '',
    description: json.Description ?? '',
    discountPercentage: json.Discount_Percentage ?? 0,
    discountTriggerDays: json.Discount_Trigger_Days ?? 0,
    unit: json.Unit ?? '',
    netWeight: json.Net_Weight ?? 0,
    createdAt: json.Created_At ?? '',
    totalRevenue: json.Total_Revenue ?? 0,

    // Thay thế bằng logic ánh xạ đúng nếu supplier và category là object
    supplier: {
      supplierId: json.Supplier?.Supplier_Id ?? 0,
      name: json.Supplier?.Name ?? '',
      address: '',
      contact: '',
      image: null
    },
    category: {
      categoryId: json.Category?.Category_Id ?? 0,
      name: json.Category?.Name ?? '',
      description: '',
      parentCategory: null
    },

    inventoryProductResponses: null,
    orderDetails: null,
    feedbackResponses: null,
    productImageResponses: null,
    userBehaviorResponses: null,
    notificationResponses: null,
    trendingProductResponses: null
  };
};

