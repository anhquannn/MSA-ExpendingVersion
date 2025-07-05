import React, { useEffect, useState } from 'react';
import {
  inventoryCheckService,
  InventoryCheckRequestFilter,
  InventoryCheckRequestResponse,
  ProductStatus,
} from '../../services/inventoryCheckService';
import Modal from '../../components/common/Modal';


const statusOptions: ProductStatus[] = ['PENDING', 'IN_PROGRESS', 'RECEIVED', 'CANCELLED'];

const InventoryCheckListPage: React.FC = () => {
  /*** STATE ***/
  const [data, setData] = useState<InventoryCheckRequestResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [filter, setFilter] = useState<InventoryCheckRequestFilter>({ page: 1, pageSize: 20 });
  const [selectedIcr, setSelectedIcr] = useState<InventoryCheckRequestResponse | null>(null);
  const branchId = 1; // head warehouse

  /*** METHODS ***/
  const fetchData = async () => {
    setLoading(true);
    try {
      const res = await inventoryCheckService.getInventoryChecks({
        ...filter,
        inventoryId: branchId,
      });
      setData(res.content);
    } catch (err: any) {
      alert(err.message || 'Tải dữ liệu thất bại');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [JSON.stringify(filter)]);

  const handleStatusChange = async (icr: InventoryCheckRequestResponse) => {
    if (icr.status === 'RECEIVED') {
      alert('Đã ở trạng thái RECEIVED');
      return;
    }
    if (!window.confirm('Xác nhận chuyển trạng thái thành RECEIVED?')) return;
    try {
      await inventoryCheckService.updateStatusToReceived(icr.icrId);
      fetchData();
    } catch (err: any) {
      alert(err.message || 'Cập nhật thất bại');
    }
  };

  /*** RENDER ***/
  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold mb-4">Lịch kiểm kho</h1>

      {/* FILTERS */}
      <div className="flex flex-wrap gap-3 mb-4 items-end">
        <input
          type="text"
          placeholder="Từ khóa..."
          className="border p-2 rounded"
          value={filter.keyword ?? ''}
          onChange={(e) =>
            setFilter((f: InventoryCheckRequestFilter) => ({
              ...f,
              keyword: e.target.value,
            }))}
        />
        <select
          className="border p-2 rounded"
          value={filter.status ?? ''}
          onChange={(e) =>
            setFilter((f: InventoryCheckRequestFilter) => ({
              ...f,
              status: (e.target.value as ProductStatus) || undefined,
            }))
          }
        >
          <option value="">Tất cả trạng thái</option>
          {statusOptions.map((st) => (
            <option key={st} value={st}>
              {st}
            </option>
          ))}
        </select>
        <button
          className="px-3 py-2 bg-blue-600 text-white rounded"
          onClick={() => fetchData()}
        >
          Lọc
        </button>
      </div>

      {/* TABLE */}
      {loading ? (
        <p>Đang tải...</p>
      ) : (
        <table className="w-full border text-sm">
          <thead>
            <tr className="bg-gray-100">
              <th className="p-2 border">ID</th>
              <th className="p-2 border">Kho</th>
              <th className="p-2 border">Người kiểm</th>
              <th className="p-2 border">Ghi chú</th>
              <th className="p-2 border">Ngày tạo</th>
              <th className="p-2 border">Trạng thái</th>
              <th className="p-2 border">Hành động</th>
            </tr>
          </thead>
          <tbody>
            {data.map((row: InventoryCheckRequestResponse) => (
              <tr key={row.icrId}>
                <td className="p-2 border text-center">{row.icrId}</td>
                <td className="p-2 border text-center">{row.inventoryResponse?.name}</td>
                <td className="p-2 border text-center">{row.surveyorResponse?.fullName}</td>
                <td className="p-2 border text-center max-w-xs truncate" title={row.note}>
                  {row.note}
                </td>
                <td className="p-2 border text-center">
                  {new Date(row.createdAt).toLocaleString()}
                </td>
                <td className="p-2 border text-center">{row.status}</td>
                <td className="p-2 border text-center">
                  <button
                    className="px-3 py-1 bg-green-600 text-white rounded"
                    onClick={() => handleStatusChange(row)}
                    disabled={row.status === 'RECEIVED'}
                  >
                    Đánh dấu RECEIVED
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {/* future detail modal */}
      {selectedIcr && (
        <Modal isOpen={!!selectedIcr} onClose={() => setSelectedIcr(null)} title="Chi tiết">
          {/* show details if needed */}
        </Modal>
      )}
    </div>
  );
};

export default InventoryCheckListPage;
