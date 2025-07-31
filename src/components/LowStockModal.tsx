import React from 'react';
import { lowStockService, LowStockItem } from '../services/lowStockService';
import { useQuery } from '@tanstack/react-query';
import { X } from 'lucide-react';

interface Props {
  open: boolean;
  onClose: () => void;
}

const LowStockModal: React.FC<Props> = ({ open, onClose }) => {
  const { data, isLoading } = useQuery<LowStockItem[]>({
    queryKey: ['lowStock', open],
    queryFn: () => lowStockService.getLowStockList(),
    enabled: open,
  });

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className="bg-white w-full max-w-3xl rounded-lg shadow-lg p-6 relative">
        <button className="absolute top-4 right-4 text-gray-500" onClick={onClose}>
          <X className="w-5 h-5" />
        </button>
        <h2 className="text-xl font-semibold mb-4">Danh sách sản phẩm tồn kho thấp</h2>

        {isLoading && <p>Đang tải...</p>}

        {!isLoading && (
          <div className="overflow-x-auto max-h-[70vh]">
            <table className="min-w-full text-sm">
              <thead>
                <tr className="bg-gray-100">
                  <th className="px-4 py-2 text-left">Tên sản phẩm</th>
                  <th className="px-4 py-2 text-left">Số lượng HEAD</th>
                  <th className="px-4 py-2 text-left">Cấp độ</th>
                </tr>
              </thead>
              <tbody>
                {data?.map((item) => (
                  <tr key={item.productId} className="border-b">
                    <td className="px-4 py-2">{item.productName}</td>
                    <td className="px-4 py-2">{item.stockNumber.toLocaleString()}</td>
                    <td className="px-4 py-2">{item.stockLevel}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default LowStockModal;
