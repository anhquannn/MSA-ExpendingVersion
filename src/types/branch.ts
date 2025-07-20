// src/types/branch.ts

export interface Branch {
  id: string; // Mã chi nhánh (ví dụ: "B001")
  name: string; // Tên chi nhánh (ví dụ: "Chi nhánh Quận 1")
  city: string; // Thành phố
  district: string; // Quận/Huyện
  street: string; // Đường
  ward?: string; // Phường/Xã (có thể là optional)
  wardCode?: string;
  districtCode?: string;
  cityCode?: string;
  phone: string; // Số điện thoại
  email: string; // Email chi nhánh
  status: 'active' | 'inactive'; // Trạng thái hoạt động
}

// Dữ liệu mock ban đầu cho chi nhánh
export const mockBranchesData: Branch[] = [
  { id: 'B001', name: 'Chi nhánh Quận 1', city: 'TP.HCM', district: 'Quận 1', street: 'Nguyễn Huệ', ward: 'Phường Bến Nghé', wardCode: '76790', districtCode: '760', cityCode: '79', phone: '0281234567', email: 'q1@example.com', status: 'active' },
  { id: 'B002', name: 'Chi nhánh Quận 3', city: 'TP.HCM', district: 'Quận 3', street: 'Võ Văn Tần', ward: 'Phường 6', wardCode: '76791', districtCode: '761', cityCode: '79', phone: '0287654321', email: 'q3@example.com', status: 'active' },
  { id: 'B003', name: 'Chi nhánh Thủ Đức', city: 'TP.HCM', district: 'Thủ Đức', street: 'Võ Văn Ngân', ward: 'Phường Linh Xuân', wardCode: '76792', districtCode: '762', cityCode: '79', phone: '0289876543', email: 'thuduc@example.com', status: 'inactive' },
  { id: 'B004', name: 'Chi nhánh Gò Vấp', city: 'TP.HCM', district: 'Gò Vấp', street: 'Phan Văn Trị', ward: 'Phường 1', wardCode: '76793', districtCode: '763', cityCode: '79', phone: '0285556667', email: 'govap@example.com', status: 'active' },
  { id: 'B005', name: 'Chi nhánh Bình Thạnh', city: 'TP.HCM', district: 'Bình Thạnh', street: 'Điện Biên Phủ', ward: 'Phường 1', wardCode: '76794', districtCode: '764', cityCode: '79', phone: '0284443332', email: 'binhthanh@example.com', status: 'active' },
  { id: 'B002', name: 'Chi nhánh Quận 3', city: 'TP.HCM', district: 'Quận 3', street: 'Võ Văn Tần', phone: '0287654321', email: 'q3@example.com', status: 'active' },
  { id: 'B003', name: 'Chi nhánh Thủ Đức', city: 'TP.HCM', district: 'Thủ Đức', street: 'Võ Văn Ngân', phone: '0289876543', email: 'thuduc@example.com', status: 'inactive' },
  { id: 'B004', name: 'Chi nhánh Gò Vấp', city: 'TP.HCM', district: 'Gò Vấp', street: 'Phan Văn Trị', phone: '0285556667', email: 'govap@example.com', status: 'active' },
  { id: 'B005', name: 'Chi nhánh Bình Thạnh', city: 'TP.HCM', district: 'Bình Thạnh', street: 'Điện Biên Phủ', phone: '0284443332', email: 'binhthanh@example.com', status: 'active' },
];

// Interface cho dữ liệu form (id là optional vì khi thêm mới không có id)
export interface BranchFormData extends Omit<Branch, 'id' | 'status'> {
    id?: string; // Khi chỉnh sửa thì có id
    status?: 'active' | 'inactive'; // Khi tạo mới có thể không cần status, nhưng khi chỉnh sửa thì có
}