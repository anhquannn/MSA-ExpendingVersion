// src/pages/Dashboard/BranchDetailPage.tsx

import React, { useState, useEffect, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import branchService from '../../services/branchService';
import BranchModal from '../../components/Branch/BranchModal'; // Sẽ sử dụng modal chỉnh sửa
import { Branch, BranchFormData } from '../../types/branch';
const BranchDetailPage: React.FC = () => {
  const { branchId } = useParams<{ branchId: string }>(); // Lấy branch id từ URL
  const navigate = useNavigate();

  const [branch, setBranch] = useState<Branch | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentBranchFormData, setCurrentBranchFormData] = useState<BranchFormData | null>(null);

  // Hàm để fetch chi tiết chi nhánh
  const fetchBranchDetails = useCallback(async () => {
    if (!branchId) {
      setError('Không tìm thấy ID chi nhánh.');
      setLoading(false);
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const fetchedBranch = await branchService.getBranchById(branchId);
      if (fetchedBranch) {
        setBranch(fetchedBranch);
      } else {
        setError('Chi nhánh không tồn tại.');
      }
    } catch (err: any) {
      setError('Lỗi khi tải chi tiết chi nhánh: ' + err.message);
    } finally {
      setLoading(false);
    }
  }, [branchId]);

  useEffect(() => {
    fetchBranchDetails();
  }, [fetchBranchDetails]);

  // --- Hàm xử lý mở modal chỉnh sửa ---
  const handleEditClick = () => {
    if (branch) {
      setCurrentBranchFormData(branch); // Branch và BranchFormData có thể tương thích
      setIsModalOpen(true);
    }
  };

  // --- Hàm xử lý lưu chi nhánh sau khi chỉnh sửa từ modal ---
  const handleSaveBranch = async (formData: BranchFormData) => {
    setLoading(true);
    try {
      if (formData.id) {
        const updatedBranch = await branchService.updateBranch(formData.id, formData);
        setBranch(updatedBranch); // Cập nhật lại UI
        console.log(`Đã cập nhật chi nhánh: ${formData.name}`);
      }
      setIsModalOpen(false); // Đóng modal
    } catch (err: any) {
      setError('Lỗi khi cập nhật chi nhánh: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  // --- Hàm xử lý xóa chi nhánh ---
  const handleDeleteBranch = async () => {
    if (!branchId) return;
    if (window.confirm(`Bạn có chắc chắn muốn xóa chi nhánh ${branch?.name} (${branchId}) không?`)) {
      setLoading(true);
      try {
        await branchService.deleteBranch(branchId);
        console.log(`Đã xóa chi nhánh: ${branchId}`);
        navigate('/dashboard/branches'); // Chuyển hướng về trang quản lý chi nhánh sau khi xóa
      } catch (err: any) {
        setError('Lỗi khi xóa chi nhánh: ' + err.message);
      } finally {
        setLoading(false);
      }
    }
  };

  // --- Hàm xử lý cập nhật trạng thái chi nhánh ---
  const handleUpdateStatus = async (currentStatus: 'active' | 'inactive') => {
    if (!branchId) return;
    const newStatus = currentStatus === 'active' ? 'inactive' : 'active';
    if (window.confirm(`Bạn có chắc chắn muốn chuyển trạng thái chi nhánh ${branch?.name} thành ${newStatus} không?`)) {
      setLoading(true);
      try {
        const updatedBranch = await branchService.updateBranchStatus(branchId, newStatus);
        setBranch(updatedBranch); // Cập nhật trạng thái trên UI
        console.log(`Đã cập nhật trạng thái chi nhánh ${branchId} thành ${newStatus}`);
      } catch (err: any) {
        setError('Lỗi khi cập nhật trạng thái chi nhánh: ' + err.message);
      } finally {
        setLoading(false);
      }
    }
  };


  if (loading) {
    return <div className="text-center py-8 text-gray-700">Đang tải chi tiết chi nhánh...</div>;
  }

  if (error) {
    return <div className="text-center py-8 text-red-600">Lỗi: {error}</div>;
  }

  if (!branch) {
    return <div className="text-center py-8 text-gray-500">Không tìm thấy chi nhánh.</div>;
  }

  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-3xl font-semibold text-gray-800">Chi Tiết Chi Nhánh: {branch.name}</h2>
        <div>
          <button
            onClick={handleEditClick}
            className="bg-yellow-500 hover:bg-yellow-600 text-white font-bold py-2 px-4 rounded-md mr-2 transition duration-300"
          >
            Chỉnh Sửa
          </button>
          <button
            onClick={() => handleUpdateStatus(branch.status)}
            className={`font-bold py-2 px-4 rounded-md mr-2 transition duration-300 ${branch.status === 'active' ? 'bg-gray-500 hover:bg-gray-600' : 'bg-green-500 hover:bg-green-600'} text-white`}
          >
            {branch.status === 'active' ? 'Ngừng Hoạt Động' : 'Kích Hoạt'}
          </button>
          <button
            onClick={handleDeleteBranch}
            className="bg-red-500 hover:bg-red-600 text-white font-bold py-2 px-4 rounded-md transition duration-300"
          >
            Xóa Chi Nhánh
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Thông tin chi tiết */}
        <div>
          <div className="mb-4">
            <p className="text-gray-600">
              <strong className="text-gray-800">Mã CN:</strong> {branch.id}
            </p>
            <p className="text-gray-600">
              <strong className="text-gray-800">Tên CN:</strong> {branch.name}
            </p>
            <p className="text-gray-600">
              <strong className="text-gray-800">Email:</strong> {branch.email}
            </p>
            <p className="text-gray-600">
              <strong className="text-gray-800">Số Điện Thoại:</strong> {branch.phone}
            </p>
            <p className="text-gray-600">
              <strong className="text-gray-800">Địa Chỉ:</strong> {`${branch.street}, ${branch.ward ? branch.ward + ', ' : ''}${branch.district}, ${branch.city}`}
            </p>
            <p className="text-gray-600">
              <strong className="text-gray-800">Trạng Thái:</strong>{' '}
              <span className={`px-2 py-1 rounded-full text-sm font-semibold ${
                branch.status === 'active' ? 'bg-green-200 text-green-800' : 'bg-red-200 text-red-800'
              }`}>
                {branch.status === 'active' ? 'Đang Hoạt Động' : 'Ngừng Hoạt Động'}
              </span>
            </p>
          </div>
        </div>
      </div>

      {/* Modal chỉnh sửa chi nhánh */}
      {isModalOpen && (
        <BranchModal
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          onSave={handleSaveBranch}
          initialData={currentBranchFormData}
          isEditing={true}
        />
      )}
    </div>
  );
};

export default BranchDetailPage;