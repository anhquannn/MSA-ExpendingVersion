// src/pages/Dashboard/BranchManagementPage.tsx

import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient, keepPreviousData } from '@tanstack/react-query';

import { 
  branchService, 
  Branch, 
  BranchPagingParams,
} from '../../services/branchService';

// --- Custom Hook để Debounce ---
function useDebounce(value: string, delay: number) {
  const [debouncedValue, setDebouncedValue] = useState(value);
  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);
    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);
  return debouncedValue;
}

const BranchManagementPage: React.FC = () => {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  // --- 1. MỞ RỘNG STATE CHO BỘ LỌC ---
  const [filters, setFilters] = useState<BranchPagingParams>({
    keyword: '',
    productId: undefined,
    sortBy: 'branchId',    // Sắp xếp mặc định
    sortDirection: 'ASC', // Hướng sắp xếp mặc định
    page: 1,
    pageSize: 10,
  });
  const debouncedKeyword = useDebounce(filters.keyword || '', 500);
  
  // --- DATA FETCHING VỚI useQuery ---
  const { 
    data: pagedData, 
    isLoading, 
    isError, 
    error 
  } = useQuery({
    // `queryKey` sẽ bao gồm tất cả các filter để tự động fetch lại khi có thay đổi
    queryKey: ['branches', { ...filters, keyword: debouncedKeyword }],
    queryFn: () => branchService.getAllBranchesWithPaging({ ...filters, keyword: debouncedKeyword }),
    placeholderData: keepPreviousData,
  });

  const branches = pagedData?.content || [];
  const totalPages = pagedData?.totalPages || 1;

  // --- MUTATION CHO VIỆC XÓA (Không đổi) ---
  const deleteBranchMutation = useMutation({
    mutationFn: (branchId: number) => branchService.deleteBranch(branchId),
    onSuccess: () => {
      alert('Xóa chi nhánh thành công!');
      queryClient.invalidateQueries({ queryKey: ['branches'] });
    },
    onError: (err: Error) => {
      alert(`Lỗi khi xóa chi nhánh: ${err.message}`);
    }
  });

  // --- EVENT HANDLERS ---
  const handleFilterChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    // Chuyển đổi giá trị rỗng thành undefined để không gửi lên API
    const finalValue = value === '' ? undefined : value;
    
    setFilters(prev => ({
      ...prev,
      [name]: name === 'productId' ? Number(finalValue) || undefined : finalValue,
      page: 1, // Reset về trang 1 mỗi khi thay đổi filter
    }));
  };
  
  const goToPage = (page: number) => {
    if (page > 0 && page <= totalPages) {
        setFilters(prev => ({ ...prev, page }));
    }
  };

  const handleDelete = (branch: Branch) => {
    if (window.confirm(`Bạn có chắc muốn xóa chi nhánh "${branch.name}"?`)) {
      deleteBranchMutation.mutate(branch.branchId);
    }
  };

  // 3. HÀM MỚI ĐỂ XỬ LÝ SẮP XẾP KHI NHẤN VÀO TIÊU ĐỀ
  const handleSort = (sortField: keyof Branch) => {
      const newDirection = filters.sortBy === sortField && filters.sortDirection === 'ASC' ? 'DESC' : 'ASC';
      setFilters(prev => ({
          ...prev,
          sortBy: sortField,
          sortDirection: newDirection,
          page: 1,
      }));
  };

  // --- RENDER ---
  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-semibold text-gray-700 mb-4">Quản lý Chi Nhánh</h2>
      
      {/* --- 2. GIAO DIỆN BỘ LỌC ĐẦY ĐỦ --- */}
      <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-6 p-4 border rounded-md bg-gray-50">
          <input
              type="text"
              name="keyword"
              placeholder="Tìm theo tên..."
              className="p-2 border rounded-md"
              value={filters.keyword}
              onChange={handleFilterChange}
          />
          <input
              type="number"
              name="productId"
              placeholder="Tìm theo ID sản phẩm..."
              className="p-2 border rounded-md"
              value={filters.productId || ''}
              onChange={handleFilterChange}
          />
          <select name="sortBy" value={filters.sortBy} onChange={handleFilterChange} className="p-2 border rounded-md">
              <option value="branchId">Sắp xếp theo: ID</option>
              <option value="name">Sắp xếp theo: Tên</option>
              <option value="city">Sắp xếp theo: Thành phố</option>
          </select>
          <select name="sortDirection" value={filters.sortDirection} onChange={handleFilterChange} className="p-2 border rounded-md">
              <option value="ASC">Thứ tự: Tăng dần</option>
              <option value="DESC">Thứ tự: Giảm dần</option>
          </select>
      </div>

      <div className="flex justify-end mb-6">
        <button
          onClick={() => navigate('/dashboard/branches/add')}
          className="bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded-md transition"
        >
          Thêm Chi Nhánh Mới
        </button>
      </div>

      {isLoading && <div className="text-center py-4">Đang tải...</div>}
      {isError && <div className="text-center py-4 text-red-500">Lỗi: {error.message}</div>}

      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border">
          <thead className="bg-gray-100 text-gray-600 uppercase text-sm leading-normal">
            <tr>
              {/* 4. TIÊU ĐỀ CỘT CÓ THỂ NHẤN ĐỂ SẮP XẾP */}
              <th className="py-3 px-6 text-left cursor-pointer" onClick={() => handleSort('branchId')}>
                Mã CN {filters.sortBy === 'branchId' && (filters.sortDirection === 'ASC' ? '▲' : '▼')}
              </th>
              <th className="py-3 px-6 text-left cursor-pointer" onClick={() => handleSort('name')}>
                Tên Chi Nhánh {filters.sortBy === 'name' && (filters.sortDirection === 'ASC' ? '▲' : '▼')}
              </th>
              <th className="py-3 px-6 text-left">Số Điện Thoại</th>
              <th className="py-3 px-6 text-left cursor-pointer" onClick={() => handleSort('city')}>
                Thành phố {filters.sortBy === 'city' && (filters.sortDirection === 'ASC' ? '▲' : '▼')}
              </th>
              <th className="py-3 px-6 text-center">Hành Động</th>
            </tr>
          </thead>
          <tbody className="text-gray-600 text-sm font-light">
            {branches.map((branch) => (
                <tr key={branch.branchId} className="border-b hover:bg-gray-50">
                    <td className="py-3 px-6 font-medium">{branch.branchId}</td>
                    <td className="py-3 px-6">{branch.name}</td>
                    <td className="py-3 px-6">{branch.phone}</td>
                    <td className="py-3 px-6">{branch.city}</td>
                    <td className="py-3 px-6 text-center">
                        <button onClick={() => navigate(`/dashboard/branches/edit/${branch.branchId}`)} className="text-yellow-600 hover:underline mr-4">Sửa</button>
                        <button onClick={() => handleDelete(branch)} disabled={deleteBranchMutation.isPending} className="text-red-600 hover:underline disabled:text-gray-400">
                          {deleteBranchMutation.isPending ? 'Đang xóa...' : 'Xóa'}
                        </button>
                    </td>
                </tr>
            ))}
             {branches.length === 0 && !isLoading && (
                <tr>
                    <td colSpan={5} className="text-center py-4">Không tìm thấy chi nhánh nào khớp với bộ lọc.</td>
                </tr>
            )}
          </tbody>
        </table>
      </div>

      <div className="flex justify-center mt-6">
        {/* ... Pagination controls ... */}
      </div>
    </div>
  );
};

export default BranchManagementPage;