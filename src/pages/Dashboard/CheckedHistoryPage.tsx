import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';
import { inventoryService, CheckedHistory } from '../../services/inventoryService';
import { PagedResponse } from '../../services/categoryService';

export const CheckedHistoryPage: React.FC = () => {
  const { id } = useParams();
  const inventoryId = Number(id);

  const [filters, setFilters] = useState({ keyword: '', page: 1, pageSize: 10 });

  const { data, isLoading } = useQuery<PagedResponse<CheckedHistory>>({
    queryKey: ['checkedHistories', inventoryId, filters],
    queryFn: () =>
      inventoryService.getCheckedHistoriesPaging({
        inventoryId,
        keyword: filters.keyword,
        page: filters.page,
        pageSize: filters.pageSize,
      }),
  });

  const totalPages = data ? data.totalPages : 0;

  const gotoPage = (page: number) => {
    setFilters(prev => ({ ...prev, page }));
  };

  const handleKeyword = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFilters(prev => ({ ...prev, keyword: e.target.value, page: 1 }));
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <h2 className="text-xl font-semibold mb-4">Lịch sử kiểm kho – Kho {inventoryId}</h2>

      <div className="mb-4 flex gap-4">
        <input
          placeholder="Tìm ghi chú..."
          value={filters.keyword}
          onChange={handleKeyword}
          className="p-2 border rounded-md flex-1"
        />
      </div>

      {isLoading && <p>Đang tải...</p>}
      {data && (
        <table className="min-w-full text-sm border">
          <thead className="bg-gray-100">
            <tr>
              <th className="py-2 px-3 text-left">Ngày</th>
              <th className="py-2 px-3 text-left">Ghi chú</th>
              <th className="py-2 px-3 text-left">Người kiểm</th>
            </tr>
          </thead>
          <tbody>
            {data.content.length === 0 ? (
              <tr>
                <td colSpan={3} className="py-3 text-center">Không có dữ liệu</td>
              </tr>
            ) : (
              data.content.map(h => (
                <tr key={h.checkedHistoryId} className="border-b">
                  <td className="py-2 px-3">{new Date(h.checkedDate).toLocaleString()}</td>
                  <td className="py-2 px-3">{h.note}</td>
                  <td className="py-2 px-3">{h.user?.fullName || '-'}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      )}
      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex items-center justify-center mt-4 space-x-2">
          <button
            disabled={filters.page === 1}
            onClick={() => gotoPage(filters.page - 1)}
            className="px-3 py-1 border rounded disabled:opacity-40"
          >
            Prev
          </button>
          <span>
            Trang {filters.page} / {totalPages}
          </span>
          <button
            disabled={filters.page === totalPages}
            onClick={() => gotoPage(filters.page + 1)}
            className="px-3 py-1 border rounded disabled:opacity-40"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
};
