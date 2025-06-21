// src/pages/Dashboard/BranchDetailPage.tsx

import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

// Import các service và types đã được định nghĩa từ API thật
import { 
  branchService, 
  Branch,
  // Giả sử bạn sẽ tạo payload này để dùng trong Modal
  // import { UpdateBranchPayload } from '../../services/branchService'; 
} from '../../services/branchService';

// import BranchModal from '../../components/Branch/BranchModal'; // Tạm thời comment lại

// Component giả lập cho Modal để tránh lỗi
const FakeBranchEditModal = ({ isOpen, onClose, onSave, branch }: { isOpen: boolean, onClose: () => void, onSave: (data: any) => void, branch: Branch | null }) => {
    if (!isOpen) return null;
    // Form chỉnh sửa sẽ được xây dựng ở đây
    return (
      <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center z-50">
        <div className="bg-white p-6 rounded-lg shadow-xl w-full max-w-lg">
          <h2 className="text-xl font-bold mb-4">Sửa thông tin chi nhánh: {branch?.name}</h2>
          <p>Form chỉnh sửa chi tiết sẽ nằm ở đây...</p>
          <div className="flex justify-end mt-4">
            <button onClick={onClose} className="px-4 py-2 bg-gray-200 rounded-md mr-2">Hủy</button>
            <button onClick={() => onSave({ name: 'Tên đã cập nhật' })} className="px-4 py-2 bg-blue-500 text-white rounded-md">Lưu</button>
          </div>
        </div>
      </div>
    );
};


const BranchDetailPage: React.FC = () => {
  const { branchId } = useParams<{ branchId: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  // --- 1. DÙNG useQuery ĐỂ TẢI DỮ LIỆU ---
  // Toàn bộ useState(loading, error, branch) và useEffect/useCallback được thay thế bằng khối này
  const { 
    data: branch, // `data` chính là chi nhánh chúng ta cần
    isLoading, 
    isError, 
    error 
  } = useQuery({
    // queryKey động, bao gồm cả 'branch' và id của nó
    queryKey: ['branch', branchId], 
    // queryFn gọi đến service, ép kiểu branchId sang number
    queryFn: () => branchService.getBranchById(Number(branchId)),
    // Chỉ thực thi query khi branchId tồn tại
    enabled: !!branchId, 
  });

  // --- 2. DÙNG useMutation ĐỂ XỬ LÝ CÁC HÀNH ĐỘNG ---

  const updateBranchMutation = useMutation({
    mutationFn: ({ id, payload }: { id: number, payload: Partial<Omit<Branch, 'branchId'|'inventory'>> }) => 
      branchService.updateBranch(id, payload),
    onSuccess: () => {
      alert('Cập nhật chi nhánh thành công!');
      // Làm mới lại dữ liệu của chính trang này
      queryClient.invalidateQueries({ queryKey: ['branch', branchId] });
      // Và cả dữ liệu của trang danh sách
      queryClient.invalidateQueries({ queryKey: ['branches'] });
    },
    onError: (err: Error) => alert(`Lỗi: ${err.message}`)
  });

  const deleteBranchMutation = useMutation({
    mutationFn: (id: number) => branchService.deleteBranch(id),
    onSuccess: () => {
      alert('Xóa chi nhánh thành công!');
      queryClient.invalidateQueries({ queryKey: ['branches'] }); // Làm mới trang danh sách
      navigate('/dashboard/branches'); // Chuyển về trang danh sách
    },
    onError: (err: Error) => alert(`Lỗi: ${err.message}`)
  });

  // --- 3. CÁC HÀM HANDLER GỌN GÀNG HƠN ---

  const handleSaveBranch = (formData: any) => {
    if (!branch) return;
    // Logic lấy dữ liệu từ form và gọi mutation
    const payload = {
        name: formData.name,
        phone: formData.phone,
        // ... các trường khác
    };
    updateBranchMutation.mutate({ id: branch.branchId, payload });
  };

  const handleDeleteBranch = () => {
    if (!branch) return;
    if (window.confirm(`Bạn có chắc muốn xóa chi nhánh "${branch.name}"?`)) {
      deleteBranchMutation.mutate(branch.branchId);
    }
  };
  
  // --- RENDER ---

  if (isLoading) {
    return <div className="text-center p-8">Đang tải chi tiết chi nhánh...</div>;
  }

  if (isError) {
    return <div className="text-center p-8 text-red-500">Lỗi khi tải dữ liệu: {error.message}</div>;
  }

  if (!branch) {
    return <div className="text-center p-8">Không tìm thấy chi nhánh.</div>;
  }

  return (
    <div className="bg-white p-8 rounded-lg shadow-lg max-w-4xl mx-auto mt-10">
      <div className="flex justify-between items-start mb-6">
        <div>
          <h2 className="text-3xl font-bold text-gray-800">{branch.name}</h2>
          <p className="text-gray-500">ID: {branch.branchId}</p>
        </div>
        <div className="flex space-x-2">
           <button
            onClick={() => { /* Mở modal sửa */ }}
            disabled={updateBranchMutation.isPending}
            className="bg-yellow-500 hover:bg-yellow-600 text-white font-bold py-2 px-4 rounded-md transition"
          >
            {updateBranchMutation.isPending ? 'Đang lưu...' : 'Chỉnh Sửa'}
          </button>
          <button
            onClick={handleDeleteBranch}
            disabled={deleteBranchMutation.isPending}
            className="bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded-md transition disabled:bg-gray-400"
          >
            {deleteBranchMutation.isPending ? 'Đang xóa...' : 'Xóa'}
          </button>
        </div>
      </div>

      <div className="border-t pt-6 grid grid-cols-1 md:grid-cols-2 gap-6 text-lg">
        <div>
          <p className="mb-2"><strong className="font-semibold text-gray-700 w-32 inline-block">SĐT:</strong> {branch.phone}</p>
          <p className="mb-2"><strong className="font-semibold text-gray-700 w-32 inline-block">Thành phố:</strong> {branch.city}</p>
          <p className="mb-2"><strong className="font-semibold text-gray-700 w-32 inline-block">Quận/Huyện:</strong> {branch.district}</p>
        </div>
        <div>
          <p className="mb-2"><strong className="font-semibold text-gray-700 w-32 inline-block">Phường/Xã:</strong> {branch.ward}</p>
          <p className="mb-2"><strong className="font-semibold text-gray-700 w-32 inline-block">Đường:</strong> {branch.street}</p>
        </div>
      </div>

      {branch.inventory && (
        <div className="mt-6 border-t pt-6">
            <h3 className="text-2xl font-semibold text-gray-800 mb-4">Thông Tin Kho</h3>
            <p><strong className="font-semibold text-gray-700">Tên kho:</strong> {branch.inventory.name}</p>
            <p><strong className="font-semibold text-gray-700">ID kho:</strong> {branch.inventory.inventoryId}</p>
        </div>
      )}

      {/* <FakeBranchEditModal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} onSave={handleSaveBranch} branch={branch} /> */}
    </div>
  );
};

export default BranchDetailPage;