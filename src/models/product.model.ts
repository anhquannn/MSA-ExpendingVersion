// Wang Code Web: Bắt đầu bằng việc định nghĩa các interface phụ thuộc trước.
// Điều này giúp code rõ ràng và có cấu trúc.

export interface Supplier {
  supplierId: number;
  name: string;
  address: string;
  contact: string;
  image: string | null;
}

export interface Category {
  categoryId: number;
  name: string;
  description: string;
  parentCategory: Category | null; // Một danh mục có thể có danh mục cha
}

// Wang Code Web: Đây là interface chính cho Product, được xây dựng
// dựa trên JSON bạn đã cung cấp.
export interface Product {
  productId: number;
  name: string;
  price: number;
  discountPercentage: number;
  discountTriggerDays: number;
  unit: string;
  netWeight: number | null; // Dùng `| null` vì API có thể trả về null
  specification: string;
  description: string;
  createdAt: string | null; // Thường là một chuỗi ISO date, ví dụ: "2025-06-18T14:30:00Z"
  totalRevenue: number;
  supplier: Supplier; // Sử dụng interface Supplier đã định nghĩa ở trên
  category: Category; // Sử dụng interface Category đã định nghĩa
  
  // Wang Code Web: Đối với các trường ...Responses có thể là null hoặc một mảng,
  // chúng ta định nghĩa chúng một cách linh hoạt.
  // Trong tương lai, bạn nên tạo interface chi tiết cho từng loại response này.
  inventoryProductResponses: any[] | null;
  orderDetails: any[] | null;
  feedbackResponses: any[] | null;
  productImageResponses: any[] | null;
  userBehaviorResponses: any[] | null;
  notificationResponses: any[] | null;
  trendingProductResponses: any[] | null;
}

// Wang Code Web: Đôi khi API trả về dữ liệu được bọc trong một object khác,
// ví dụ như `result`. Ta cũng có thể định nghĩa nó.
export interface ApiResponse<T> {
  result: T;
  // Thêm các thuộc tính khác của response nếu có, ví dụ: message, statusCode...
}