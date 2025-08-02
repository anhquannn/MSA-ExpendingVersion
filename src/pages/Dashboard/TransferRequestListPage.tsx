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

  const getStatusLabel = (status: string) => {
    switch (status) {
      case 'PENDING': return 'Chờ duyệt';
      case 'APPROVED': return 'Đã duyệt';
      case 'REJECTED': return 'Từ chối';
      case 'COMPLETED': return 'Hoàn thành';
      default: return status;
    }
  };

  return (
    <div className="p-6 bg-gray-50 min-h-screen">
      <header className="mb-6">
        <h1 className="text-3xl font-bold text-gray-800">Yêu Cầu Vận Chuyển Kho</h1>
        <p className="text-gray-500 mt-1">Quản lý và theo dõi các yêu cầu vận chuyển giữa các chi nhánh.</p>
      </header>

      {/* Filter Section */}
      <div className="bg-white p-4 rounded-lg shadow mb-6">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {/* Status Filter */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Trạng Thái</label>
            <select
              value={filters.status || ''}
              onChange={(e) => handleFilterChange({ status: e.target.value || undefined })}
              className="w-full p-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">Tất cả trạng thái</option>
              <option value="PENDING">Chờ duyệt</option>
              <option value="APPROVED">Đã duyệt</option>
              <option value="REJECTED">Từ chối</option>
              <option value="COMPLETED">Hoàn thành</option>
            </select>
          </div>

          {/* From Date Filter */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Từ ngày</label>
            <input
              type="date"
              value={filters.fromDate?.split('T')[0] || ''}
              onChange={(e) => handleFilterChange({ fromDate: e.target.value ? `${e.target.value}T00:00:00` : undefined })}
              className="w-full p-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          {/* To Date Filter */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Đến ngày</label>
            <input
              type="date"
              value={filters.toDate?.split('T')[0] || ''}
              onChange={(e) => handleFilterChange({ toDate: e.target.value ? `${e.target.value}T23:59:59` : undefined })}
              className="w-full p-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
            />
          </div>

          {/* Clear Filters Button */}
          <div className="flex items-end">
            <button
              onClick={() => setFilters({ page: 1, pageSize: 10, sortBy: 'createdAt', sortDirection: 'DESC' })}
              className="w-full bg-gray-200 hover:bg-gray-300 text-gray-700 font-medium py-2 px-4 rounded-md transition-colors"
            >
              Đặt lại bộ lọc
            </button>
          </div>
        </div>
      </div>

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
                    {getStatusLabel(req.status)}
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
        {/* Pagination and Stats */}
        <div className="bg-gray-50 px-6 py-3 border-t border-gray-200">
          <div className="flex items-center justify-between">
            <div className="text-sm text-gray-700">
              Hiển thị <span className="font-medium">{((filters.page ?? 1) - 1) * (filters.pageSize ?? 10) + 1}</span> đến{' '}
              <span className="font-medium">
                {Math.min((filters.page ?? 1) * (filters.pageSize ?? 10), data?.totalElements ?? 0)}
              </span>{' '}
              trong tổng số <span className="font-medium">{data?.totalElements ?? 0}</span> yêu cầu
            </div>
            <div className="flex justify-center">
              <Pagination 
                currentPage={filters.page ?? 1} 
                totalPages={totalPages} 
                onPageChange={handlePageChange} 
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default TransferRequestListPage;
