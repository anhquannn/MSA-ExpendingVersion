import 'package:msa/feature/domain/entities/branch_model.dart';
import 'package:msa/feature/domain/entities/cart_item.dart';
import 'package:msa/feature/domain/entities/cart_model.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/entities/user_model.dart';

String messageError = '';
UserModel? userModelGlobal;
CartModel? cartModelGlobal;

final mockProduct = ProductModel.fromJson({
  "productId": 3,
  "name": "Sản phẩm Supabase",
  "images":
      "https://lmtqwglnnbgsrxhelpxz.supabase.co/storage/v1/object/sign/msa/sua_do_hop.png,https://lmtqwglnnbgsrxhelpxz.supabase.co/storage/v1/object/sign/msa/sua_tam.png?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InN0b3JhZ2UtdXJsLXNpZ25pbmcta2V5XzZjZDFmNTA3LWMxZGUtNGY3ZC1iY2FhLWFhYzU1YTllYWZjNiJ9.eyJ1cmwiOiJtc2Evc3VhX3RhbS5wbmciLCJpYXQiOjE3NDgxNDk0MDEsImV4cCI6MTc3OTY4NTQwMX0.rgImGsLmvEhr0xoWXVbJBoi_JRESYJhqIA2L0T7FuD0,https://lmtqwglnnbgsrxhelpxz.supabase.co/storage/v1/object/sign/msa/sua_do_hop.png,https://lmtqwglnnbgsrxhelpxz.supabase.co/storage/v1/object/sign/msa/sua_tam.png?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InN0b3JhZ2UtdXJsLXNpZ25pbmcta2V5XzZjZDFmNTA3LWMxZGUtNGY3ZC1iY2FhLWFhYzU1YTllYWZjNiJ9.eyJ1cmwiOiJtc2Evc3VhX3RhbS5wbmciLCJpYXQiOjE3NDgxNDk0MDEsImV4cCI6MTc3OTY4NTQwMX0.rgImGsLmvEhr0xoWXVbJBoi_JRESYJhqIA2L0T7FuD0,https://lmtqwglnnbgsrxhelpxz.supabase.co/storage/v1/object/sign/msa/sua_do_hop.png,https://lmtqwglnnbgsrxhelpxz.supabase.co/storage/v1/object/sign/msa/sua_tam.png?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InN0b3JhZ2UtdXJsLXNpZ25pbmcta2V5XzZjZDFmNTA3LWMxZGUtNGY3ZC1iY2FhLWFhYzU1YTllYWZjNiJ9.eyJ1cmwiOiJtc2Evc3VhX3RhbS5wbmciLCJpYXQiOjE3NDgxNDk0MDEsImV4cCI6MTc3OTY4NTQwMX0.rgImGsLmvEhr0xoWXVbJBoi_JRESYJhqIA2L0T7FuD0,https://lmtqwglnnbgsrxhelpxz.supabase.co/storage/v1/object/sign/msa/sua_do_hop.png?https://lmtqwglnnbgsrxhelpxz.supabase.co/storage/v1/object/sign/msa/sua_tam.png?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InN0b3JhZ2UtdXJsLXNpZ25pbmcta2V5XzZjZDFmNTA3LWMxZGUtNGY3ZC1iY2FhLWFhYzU1YTllYWZjNiJ9.eyJ1cmwiOiJtc2Evc3VhX3RhbS5wbmciLCJpYXQiOjE3NDgxNDk0MDEsImV4cCI6MTc3OTY4NTQwMX0.rgImGsLmvEhr0xoWXVbJBoi_JRESYJhqIA2L0T7FuD0",
  "price": 50000.0,
  "currentPrice": 50000.0,
  "unit": "chai",
  "color": "trắng",
  "specification": "330ml",
  "description": "Nước suối đóng chai Supabase",
  "expiry": 1767225600000, // timestamp
  "createAt": 1747396800000, // timestamp
  "totalRevenue": 1000000,
  "manufacturer": {
    "manufacturerId": 3,
    "name": "ABC Electronics",
    "address": "123 Main St, Hanoi",
    "contact": "0123456789",
  },
  "category": {
    "categoryId": 2,
    "name": "Electronics",
    "description": "Category for electronic devices",
    "parentCategory": null,
  },
  "inventoryProductResponses": null,
  "orderDetails": null,
});

final mockCartItem = CartItemModel.fromJson({
  "cartItemId": 3,
  "price": 1000.0,
  "quantity": 2,
  "product": {
    "productId": 5,
    "name": "Sản phẩm Supabase",
    "images":
        "https://lmtqwglnnbgsrxhelpxz.supabase.co/storage/v1/object/sign/msa/sua_do_hop.png?https://lmtqwglnnbgsrxhelpxz.supabase.co/storage/v1/object/sign/msa/sua_tam.png?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InN0b3JhZ2UtdXJsLXNpZ25pbmcta2V5XzZjZDFmNTA3LWMxZGUtNGY3ZC1iY2FhLWFhYzU1YTllYWZjNiJ9.eyJ1cmwiOiJtc2Evc3VhX3RhbS5wbmciLCJpYXQiOjE3NDgxNDk0MDEsImV4cCI6MTc3OTY4NTQwMX0.rgImGsLmvEhr0xoWXVbJBoi_JRESYJhqIA2L0T7FuD0",
    "price": 50000.0,
    "currentPrice": 50000.0,
    "unit": "chai",
    "color": "trắng",
    "specification": "330ml",
    "description": "Nước suối đóng chai Supabase",
    "expiry": 1767225600000,
    "createAt": 1747396800000,
    "totalRevenue": 1000000,
    "manufacturer": {
      "manufacturerId": 3,
      "name": "ABC Electronics",
      "address": "123 Main St, Hanoi",
      "contact": "0123456789",
    },
    "category": {
      "categoryId": 2,
      "name": "Electronics",
      "description": "Category for electronic devices",
      "parentCategory": null,
    },
    "inventoryProductResponses": null,
    "orderDetails": null,
  },
  "cart": {
    "cartId": 1,
    "status": "active",
    "user": {
      "userId": 4,
      "fullName": "wang tèo",
      "email": "minhquang03082003@gmail.com",
      "phoneNumber": "0912345678",
      "birthday": "",
      "address": "Nguyen Van Tao, Long Thoi, Nha Be, Ho Chi Minh",
      "image": null,
      "deviceId": null,
      "googleId": null,
      "roles": [],
      "branches": null,
    },
  },
  "selected": false,
});

final mockBranch = BranchModel.fromJson({
  "branchId": 3,
  "name": "Branch Đà Nẵng",
  "phone": "02363888888",
  "street": "51 Bạch Đằng",
  "ward": "Phường Hải Châu 1",
  "district": "Hải Châu",
  "city": "Đà Nẵng",
  "inventory": null,
});
