// src/types/inventory.ts (Tạo file này để định nghĩa các kiểu dữ liệu)

export interface BranchStock {
  branchId: string; // ID chi nhánh (ví dụ: "B001", "B002")
  branchName: string; // Tên chi nhánh (ví dụ: "Chi nhánh Quận 1")
  stock: number;      // Số lượng tồn kho tại chi nhánh đó
}

export interface Product {
  id: string; // Mã sản phẩm
  name: string; // Tên sản phẩm
  category: string; // Loại sản phẩm (ví dụ: "Thực phẩm tươi sống", "Đồ uống")
  unit: string; // Đơn vị (ví dụ: "kg", "hộp")
  lowStockThreshold: number; // Ngưỡng cảnh báo sắp hết hàng
  branchStocks: BranchStock[]; // Số lượng tồn kho tại từng chi nhánh
}

// Giả định các chi nhánh có sẵn
export const mockBranches = [
  { id: 'B001', name: 'Chi nhánh Quận 1' },
  { id: 'B002', name: 'Chi nhánh Quận 3' },
  { id: 'B003', name: 'Chi nhánh Thủ Đức' },
  { id: 'B004', name: 'Chi nhánh Gò Vấp' }, // Chi nhánh mới 1
  { id: 'B005', name: 'Chi nhánh Bình Thạnh' }, // Chi nhánh mới 2
  { id: 'B006', name: 'Chi nhánh Quận 7' }, // Chi nhánh mới 3
];
// Dữ liệu kiểm kho giả (mock data)
export const mockProducts: Product[] = [
  {
    id: 'P001',
    name: 'Táo Gala',
    category: 'Trái cây',
    unit: 'kg',
    lowStockThreshold: 50,
    branchStocks: [
      { branchId: 'B001', branchName: 'Chi nhánh Quận 1', stock: 150 },
      { branchId: 'B002', branchName: 'Chi nhánh Quận 3', stock: 70 },
      { branchId: 'B003', branchName: 'Chi nhánh Thủ Đức', stock: 30 },
      { branchId: 'B004', branchName: 'Chi nhánh Gò Vấp', stock: 100 }, // Thêm stock cho chi nhánh mới
      { branchId: 'B005', branchName: 'Chi nhánh Bình Thạnh', stock: 80 },
      { branchId: 'B006', branchName: 'Chi nhánh Quận 7', stock: 60 },
    ],
    // imageUrl: 'https://via.placeholder.com/150/FF0000/FFFFFF?text=Táo+Gala',
    // description: 'Táo tươi ngon từ nông trại.',
    // price: 30000,
  },
  {
    id: 'P002',
    name: 'Sữa tươi Vinamilk 1L',
    category: 'Sữa & Sản phẩm từ sữa',
    unit: 'hộp',
    lowStockThreshold: 30,
    branchStocks: [
      { branchId: 'B001', branchName: 'Chi nhánh Quận 1', stock: 10 },
      { branchId: 'B002', branchName: 'Chi nhánh Quận 3', stock: 25 },
      { branchId: 'B003', branchName: 'Chi nhánh Thủ Đức', stock: 5 },
      { branchId: 'B004', branchName: 'Chi nhánh Gò Vấp', stock: 35 },
      { branchId: 'B005', branchName: 'Chi nhánh Bình Thạnh', stock: 18 },
      { branchId: 'B006', branchName: 'Chi nhánh Quận 7', stock: 15 },
    ],
    // imageUrl: 'https://via.placeholder.com/150/0000FF/FFFFFF?text=Sữa+Vinamilk',
    // description: 'Sữa tươi tiệt trùng 100% nguyên chất.',
    // price: 35000,
  },
  {
    id: 'P003',
    name: 'Gạo ST25 5kg',
    category: 'Gạo & Ngũ cốc',
    unit: 'bao',
    lowStockThreshold: 20,
    branchStocks: [
      { branchId: 'B001', branchName: 'Chi nhánh Quận 1', stock: 80 },
      { branchId: 'B002', branchName: 'Chi nhánh Quận 3', stock: 40 },
      { branchId: 'B003', branchName: 'Chi nhánh Thủ Đức', stock: 15 },
      { branchId: 'B004', branchName: 'Chi nhánh Gò Vấp', stock: 50 },
      { branchId: 'B005', branchName: 'Chi nhánh Bình Thạnh', stock: 22 },
      { branchId: 'B006', branchName: 'Chi nhánh Quận 7', stock: 10 },
    ],
    // imageUrl: 'https://via.placeholder.com/150/008000/FFFFFF?text=Gạo+ST25',
    // description: 'Gạo thơm đặc sản, chất lượng cao.',
    // price: 120000,
  },
  {
    id: 'P004',
    name: 'Bánh mì sandwich',
    category: 'Bánh kẹo',
    unit: 'gói',
    lowStockThreshold: 10,
    branchStocks: [
      { branchId: 'B001', branchName: 'Chi nhánh Quận 1', stock: 5 },
      { branchId: 'B002', branchName: 'Chi nhánh Quận 3', stock: 12 },
      { branchId: 'B003', branchName: 'Chi nhánh Thủ Đức', stock: 8 },
      { branchId: 'B004', branchName: 'Chi nhánh Gò Vấp', stock: 15 },
      { branchId: 'B005', branchName: 'Chi nhánh Bình Thạnh', stock: 7 },
      { branchId: 'B006', branchName: 'Chi nhánh Quận 7', stock: 3 },
    ],
    // imageUrl: 'https://via.placeholder.com/150/FFFF00/000000?text=Bánh+Mì',
    // description: 'Bánh mì mềm mịn, thơm ngon.',
    // price: 25000,
  },
  {
    id: 'P005',
    name: 'Nước ngọt Coca Cola 330ml',
    category: 'Đồ uống',
    unit: 'lon',
    lowStockThreshold: 100,
    branchStocks: [
      { branchId: 'B001', branchName: 'Chi nhánh Quận 1', stock: 200 },
      { branchId: 'B002', branchName: 'Chi nhánh Quận 3', stock: 150 },
      { branchId: 'B003', branchName: 'Chi nhánh Thủ Đức', stock: 90 },
      { branchId: 'B004', branchName: 'Chi nhánh Gò Vấp', stock: 180 },
      { branchId: 'B005', branchName: 'Chi nhánh Bình Thạnh', stock: 110 },
      { branchId: 'B006', branchName: 'Chi nhánh Quận 7', stock: 70 },
    ],
    // imageUrl: 'https://via.placeholder.com/150/FF4500/FFFFFF?text=Coca+Cola',
    // description: 'Nước giải khát có ga.',
    // price: 10000,
  },
];
