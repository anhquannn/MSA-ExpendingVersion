// src/pages/Dashboard/BranchManagementPage.tsx

import React, { useState, useEffect, useCallback, useMemo } from 'react';
import branchService from '../../services/branchService';
import { Branch, BranchFormData } from '../../types/branch';
import BranchModal from '../../components/Branch/BranchModal'; // Sẽ tạo ở bước tiếp theo
import { Link, useNavigate } from 'react-router-dom';

const BranchManagementPage: React.FC = () => {
  const navigate = useNavigate();

  const [branches, setBranches] = useState<Branch[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [inputSearchValue, setInputSearchValue] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [branchesPerPage] = useState(10); // Số chi nhánh trên mỗi trang
  const [totalBranches, setTotalBranches] = useState(0);

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [currentBranch, setCurrentBranch] = useState<BranchFormData | null>(null);
  const [isEditing, setIsEditing] = useState(false);

  // Hàm để fetch dữ liệu chi nhánh
  const fetchBranches = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await branchService.getBranches({
        page: currentPage,
        limit: branchesPerPage,
        search: searchTerm,
      });
      setBranches(response.data);
      setTotalBranches(response.total);
    } catch (err: any) {
      setError('Lỗi khi tải dữ liệu chi nhánh: ' + err.message);
    } finally {
      setLoading(false);
    }
  }, [currentPage, branchesPerPage, searchTerm]);

  useEffect(() => {
    fetchBranches();
  }, [fetchBranches]);

  // --- Hàm xử lý thay đổi input khi người dùng gõ ---
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInputSearchValue(e.target.value);
  };

  // --- Hàm xử lý khi nhấn phím (để bắt Enter) ---
  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      setSearchTerm(inputSearchValue);
      setCurrentPage(1);
    }
  };

  // --- Hàm mở/đóng Modal ---
  const openAddModal = () => {
    setCurrentBranch({
      name: '',
      email: '',
      phone: '',
      city: '',
      district: '',
      street: '',
      ward: '',
      status: 'active', // Mặc định là active khi thêm mới
    });
    setIsEditing(false);
    setIsModalOpen(true);
  };

  const openEditModal = (branch: Branch) => {
    setCurrentBranch(branch); // Branch và BranchFormData có thể tương thích
    setIsEditing(true);
    setIsModalOpen(true);
  };

  const closeBranchModal = () => {
    setIsModalOpen(false);
    setCurrentBranch(null);
    setIsEditing(false);
  };

  // --- Hàm xử lý thêm/chỉnh sửa chi nhánh ---
  const handleSaveBranch = async (formData: BranchFormData) => {
    setLoading(true);
    try {
      if (isEditing && formData.id) {
        await branchService.updateBranch(formData.id, formData);
        console.log(`Đã cập nhật chi nhánh: ${formData.name}`);
      } else {
        await branchService.addBranch(formData as Omit<BranchFormData, 'id'>); // Ép kiểu nếu formData có id
        console.log(`Đã thêm chi nhánh mới: ${formData.name}`);
      }
      closeBranchModal();
      setSearchTerm('');
      setInputSearchValue('');
      setCurrentPage(1);
      fetchBranches();
    } catch (err: any) {
      setError('Lỗi khi lưu chi nhánh: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  // --- Hàm xử lý xóa chi nhánh ---
  const handleDeleteBranch = async (branchId: string) => {
    if (window.confirm(`Bạn có chắc chắn muốn xóa chi nhánh ${branchId} không?`)) {
      setLoading(true);
      try {
        await branchService.deleteBranch(branchId);
        console.log(`Đã xóa chi nhánh: ${branchId}`);
        setSearchTerm('');
        setInputSearchValue('');
        setCurrentPage(1);
        fetchBranches();
      } catch (err: any) {
        setError('Lỗi khi xóa chi nhánh: ' + err.message);
      } finally {
        setLoading(false);
      }
    }
  };

  // --- Hàm xử lý cập nhật trạng thái chi nhánh ---
  const handleUpdateStatus = async (branchId: string, currentStatus: 'active' | 'inactive') => {
    const newStatus = currentStatus === 'active' ? 'inactive' : 'active';
    if (window.confirm(`Bạn có chắc chắn muốn chuyển trạng thái chi nhánh ${branchId} thành ${newStatus} không?`)) {
      setLoading(true);
      try {
        await branchService.updateBranchStatus(branchId, newStatus);
        console.log(`Đã cập nhật trạng thái chi nhánh ${branchId} thành ${newStatus}`);
        fetchBranches(); // Tải lại danh sách để cập nhật trạng thái
      } catch (err: any) {
        setError('Lỗi khi cập nhật trạng thái chi nhánh: ' + err.message);
      } finally {
        setLoading(false);
      }
    }
  };


  // --- Phân trang ---
  const totalPages = useMemo(() => Math.ceil(totalBranches / branchesPerPage), [totalBranches, branchesPerPage]);

  const goToPage = (page: number) => {
    if (page > 0 && page <= totalPages) {
      setCurrentPage(page);
    }
  };

  if (loading) {
    return <div className="text-center py-8">Đang tải dữ liệu chi nhánh...</div>;
  }

  if (error) {
    return <div className="text-center py-8 text-red-600">Lỗi: {error}</div>;
  }

  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-semibold text-gray-700 mb-4">Quản lý Chi Nhánh</h2>

      {/* Thanh tìm kiếm và nút Thêm Chi Nhánh */}
      <div className="flex justify-between items-center mb-6">
        <input
          type="text"
          placeholder="Tìm kiếm theo tên, email, sđt, địa chỉ..."
          className="p-2 border border-gray-300 rounded-md w-1/3"
          value={inputSearchValue}
          onChange={handleInputChange}
          onKeyDown={handleKeyDown}
        />
        <button
          onClick={openAddModal}
          className="bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded-md transition duration-300"
        >
          Thêm Chi Nhánh Mới
        </button>
      </div>

      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-200">
          <thead>
            <tr className="bg-gray-100 text-gray-600 uppercase text-sm leading-normal">
              <th className="py-3 px-6 text-left">Mã CN</th>
              <th className="py-3 px-6 text-left">Tên Chi Nhánh</th>
              <th className="py-3 px-6 text-left">Email</th>
              <th className="py-3 px-6 text-left">Số Điện Thoại</th>
              <th className="py-3 px-6 text-left">Địa Chỉ</th>
              <th className="py-3 px-6 text-left">Trạng Thái</th>
              <th className="py-3 px-6 text-center">Hành Động</th>
            </tr>
          </thead>
          <tbody className="text-gray-600 text-sm font-light">
            {branches.length === 0 ? (
              <tr>
                <td colSpan={7} className="py-6 text-center">Không tìm thấy chi nhánh nào.</td>
              </tr>
            ) : (
              branches.map((branch) => (
                <tr key={branch.id} className="border-b border-gray-200 hover:bg-gray-50">
                  <td className="py-3 px-6 text-left whitespace-nowrap">
                    <Link
                      to={`/dashboard/branches/${branch.id}`} // Đường dẫn đến trang chi tiết chi nhánh
                      className="text-blue-600 hover:text-blue-800 hover:underline font-medium"
                    >
                      {branch.id}
                    </Link>
                  </td>
                  <td className="py-3 px-6 text-left">{branch.name}</td>
                  <td className="py-3 px-6 text-left">{branch.email}</td>
                  <td className="py-3 px-6 text-left">{branch.phone}</td>
                  <td className="py-3 px-6 text-left">{`${branch.street}, ${branch.district}, ${branch.city}`}</td>
                  <td className="py-3 px-6 text-left">
                    <span
                      className={`px-3 py-1 rounded-full text-xs font-semibold ${
                        branch.status === 'active' ? 'bg-green-200 text-green-800' : 'bg-red-200 text-red-800'
                      }`}
                    >
                      {branch.status === 'active' ? 'Đang Hoạt Động' : 'Ngừng Hoạt Động'}
                    </span>
                  </td>
                  <td className="py-3 px-6 text-center">
                    <div className="flex item-center justify-center">
                      <button
                        onClick={() => openEditModal(branch)}
                        className="w-8 h-8 rounded-full bg-yellow-100 text-yellow-700 flex items-center justify-center mr-2 hover:bg-yellow-200"
                        title="Chỉnh sửa"
                      >
                        <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                          <path d="M17.414 2.586a2 2 0 00-2.828 0L7 10.172V13h2.828l7.586-7.586a2 2 0 000-2.828z" />
                          <path fillRule="evenodd" d="M2 6a2 2 0 012-2h4a1 1 0 010 2H4v10h10v-4a1 1 0 112 0v4a2 2 0 01-2 2H4a2 2 0 01-2-2V6z" clipRule="evenodd" />
                        </svg>
                      </button>
                      <button
                        onClick={() => handleDeleteBranch(branch.id)}
                        className="w-8 h-8 rounded-full bg-red-100 text-red-700 flex items-center justify-center mr-2 hover:bg-red-200"
                        title="Xóa"
                      >
                        <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                          <path fillRule="evenodd" d="M9 2a1 1 0 00-.894.553L7.382 4H4a1 1 0 000 2v10a2 2 0 002 2h8a2 2 0 002-2V6a1 1 0 100-2h-3.382l-.724-1.447A1 1 0 0011 2H9zM7 8a1 1 0 012 0v6a1 1 0 11-2 0V8zm5-1a1 1 0 00-1 1v6a1 1 0 102 0V8a1 1 0 00-1-1z" clipRule="evenodd" />
                        </svg>
                      </button>
                      <button
                        onClick={() => handleUpdateStatus(branch.id, branch.status)}
                        className={`w-8 h-8 rounded-full ${branch.status === 'active' ? 'bg-gray-100 text-gray-700' : 'bg-green-100 text-green-700'} flex items-center justify-center hover:${branch.status === 'active' ? 'bg-gray-200' : 'bg-green-200'}`}
                        title={branch.status === 'active' ? 'Ngừng hoạt động' : 'Kích hoạt'}
                      >
                        <svg className="w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg">
                          {branch.status === 'active' ? (
                            <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586l-1.293-1.293z" clipRule="evenodd" />
                          ) : (
                            <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                          )}
                        </svg>
                      </button>
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Phân trang */}
      {totalPages > 1 && (
        <div className="flex justify-center mt-6">
          <button
            onClick={() => goToPage(currentPage - 1)}
            disabled={currentPage === 1}
            className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-l disabled:opacity-50"
          >
            Trước
          </button>
          {[...Array(totalPages)].map((_, index) => (
            <button
              key={index}
              onClick={() => goToPage(index + 1)}
              className={`bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 mx-1 ${
                currentPage === index + 1 ? 'bg-blue-500 text-white' : ''
              }`}
            >
              {index + 1}
            </button>
          ))}
          <button
            onClick={() => goToPage(currentPage + 1)}
            disabled={currentPage === totalPages}
            className="bg-gray-200 hover:bg-gray-300 text-gray-800 font-bold py-2 px-4 rounded-r disabled:opacity-50"
          >
            Tiếp
          </button>
        </div>
      )}

      {/* Modal Thêm/Chỉnh sửa Chi Nhánh */}
      {isModalOpen && (
        <BranchModal
          isOpen={isModalOpen}
          onClose={closeBranchModal}
          onSave={handleSaveBranch}
          initialData={currentBranch}
          isEditing={isEditing}
        />
      )}
    </div>
  );
};

export default BranchManagementPage;