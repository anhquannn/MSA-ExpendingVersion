import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { transferService, TransferRequestFilter, TransferResponse } from '../../services/transferService';
import { Link } from 'react-router-dom';
import { Eye } from 'lucide-react';
import Pagination from '../../components/common/Pagination';

const TransferRequestListPage: React.FC = () => {
  const [filters, setFilters] = useState<TransferRequestFilter>({ page: 1, pageSize: 10, sortBy: 'createdAt', sortDirection: 'DESC' });

  const { data, isLoading, isError, error } = useQuery({
    queryKey: ['transferRequests', filters],
    queryFn: () => transferService.getTransferRequests(filters),
    placeholderData: (previousData) => previousData,
  });

  const handleFilterChange = (newFilters: Partial<TransferRequestFilter>) => {
    setFilters(prev => ({ ...prev, ...newFilters, page: 1 }));
  };

  const totalPages = (data?.totalPages !== undefined && data?.totalPages !== null)
    ? data.totalPages
    : Math.ceil((data?.totalElements ?? 0) / (filters.pageSize ?? 10)) || 1;

  const handlePageChange = (newPage: number) => {
    setFilters(prev => ({ ...prev, page: newPage }));
  };

  const getStatusChip = (status: string) => {
    switch (status) {
      case 'PENDING': return 'bg-yellow-100 text-yellow-800';
      case 'APPROVED': return 'bg-green-100 text-green-800';
      case 'REJECTED': return 'bg-red-100 text-red-800';
      case 'COMPLETED': return 'bg-blue-100 text-blue-800';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <header className="mb-6">
        <h1 className="text-3xl font-bold text-gray-800">Yêu Cầu Vận Chuyển Kho</h1>
        <p className="text-gray-500 mt-1">Quản lý và theo dõi các yêu cầu vận chuyển giữa các chi nhánh.</p>
      </header>

      {/* TODO: Add filter inputs here */}

      <div className="bg-white rounded-lg shadow-md overflow-hidden">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Từ Kho</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tới Kho</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Người Yêu Cầu</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Trạng Thái</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Ngày Tạo</th>
              <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Hành Động</th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {isLoading && <tr><td colSpan={7} className="text-center py-4">Đang tải...</td></tr>}
            {isError && <tr><td colSpan={7} className="text-center py-4 text-red-500">Lỗi: {error.message}</td></tr>}
            {data?.content.map((req) => (
              <tr key={req.transferRequestId}>
                <td className="px-6 py-4 whitespace-nowrap">{req.transferRequestId}</td>
                <td className="px-6 py-4 whitespace-nowrap">
                  {req.fromInventoryResponse?.name || 'N/A'}
                  <div className="text-xs text-gray-500">{req.fromInventoryResponse?.address}</div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  {req.toInventoryResponse?.name || 'N/A'}
                  <div className="text-xs text-gray-500">{req.toInventoryResponse?.address}</div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <div>{req.requesterResponse?.fullName || 'N/A'}</div>
                  <div className="text-xs text-gray-500">{req.requesterResponse?.email}</div>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusChip(req.status)}`}>
                    {req.status.toUpperCase()}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap">{new Date(req.createdAt).toLocaleString('vi-VN')}</td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <Link to={`/dashboard/transfer-requests/${req.transferRequestId}`} className="text-blue-600 hover:text-blue-900">
                    <Eye className="w-5 h-5" />
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        {totalPages > 1 && (
            <div className="flex justify-center my-6">
              <Pagination currentPage={filters.page ?? 1} totalPages={totalPages} onPageChange={handlePageChange} />
            </div>
          )}
      </div>
    </div>
  );
};

export default TransferRequestListPage;
