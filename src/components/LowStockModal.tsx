import React from 'react';
import { lowStockService, LowStockItem } from '../services/lowStockService';
import { useQuery } from '@tanstack/react-query';
import { X, Package, AlertTriangle, LoaderCircle } from 'lucide-react';

interface Props {
  open: boolean;
  onClose: () => void;
  branchId?: number;
}

const LowStockModal: React.FC<Props> = ({ open, onClose, branchId }) => {
  const { data, isLoading, error } = useQuery<LowStockItem[]>({
    queryKey: ['lowStock', open, branchId],
    queryFn: () => lowStockService.getLowStockList(branchId),
    enabled: open,
    retry: 2,
  });

  if (!open) return null;

  const getStockLevelColor = (level: string) => {
    switch (level) {
      case 'LOW': 
        return 'bg-red-100 text-red-800 border-red-200';
      case 'MEDIUM': 
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'HIGH': 
        return 'bg-green-100 text-green-800 border-green-200';
      default: 
        return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const getStockAlert = (stockNumber: number, minThreshold: number) => {
    const percentage = (stockNumber / minThreshold) * 100;
    if (percentage <= 50) return { color: 'text-red-600', icon: '🚨' };
    if (percentage <= 80) return { color: 'text-orange-600', icon: '⚠️' };
    return { color: 'text-yellow-600', icon: '📊' };
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className="bg-white w-full max-w-5xl max-h-[90vh] rounded-lg shadow-lg relative flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-gray-200">
          <div className="flex items-center gap-3">
            <AlertTriangle className="w-6 h-6 text-red-500" />
            <div>
              <h2 className="text-xl font-semibold text-gray-800">
                Danh sách sản phẩm tồn kho thấp
              </h2>
              <p className="text-sm text-gray-500 mt-1">
                {data?.length || 0} sản phẩm cần chú ý
              </p>
            </div>
          </div>
          <button 
            className="p-2 hover:bg-gray-100 rounded-lg transition-colors" 
            onClick={onClose}
          >
            <X className="w-5 h-5 text-gray-500" />
          </button>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-6">
          {isLoading && (
            <div className="flex items-center justify-center h-40">
              <LoaderCircle className="w-8 h-8 animate-spin text-blue-500" />
              <span className="ml-3 text-gray-600">Đang tải dữ liệu...</span>
            </div>
          )}

          {error && (
            <div className="flex items-center justify-center h-40 text-red-600">
              <AlertTriangle className="w-8 h-8 mr-3" />
              <div>
                <p className="font-medium">Lỗi khi tải dữ liệu</p>
                <p className="text-sm text-gray-500">Vui lòng thử lại sau</p>
              </div>
            </div>
          )}

          {!isLoading && !error && (!data || data.length === 0) && (
            <div className="flex flex-col items-center justify-center h-40 text-gray-500">
              <Package className="w-16 h-16 mb-4 text-gray-300" />
              <p className="text-lg font-medium">Không có sản phẩm tồn kho thấp</p>
              <p className="text-sm">Tất cả sản phẩm đều có tồn kho ổn định</p>
            </div>
          )}

          {!isLoading && !error && data && data.length > 0 && (
            <div className="overflow-x-auto">
              <table className="min-w-full">
                <thead>
                  <tr className="bg-gray-50 border-b">
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Sản phẩm
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Tồn kho hiện tại
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Ngưỡng tối thiểu
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Mức độ
                    </th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                      Kho
                    </th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {data.map((item) => {
                    const alert = getStockAlert(item.stockNumber, item.minThreshold);
                    return (
                      <tr key={item.inventoryProductId} className="hover:bg-gray-50">
                        <td className="px-6 py-4">
                          <div>
                            <div className="text-sm font-medium text-gray-900">
                              {item.product?.name || 'N/A'}
                            </div>
                            <div className="text-sm text-gray-500">
                              ID: #{item.product?.productId} • {item.product?.unit || 'N/A'}
                            </div>
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <div className={`flex items-center gap-2 ${alert.color}`}>
                            <span className="text-lg">{alert.icon}</span>
                            <span className="text-lg font-bold">
                              {item.stockNumber?.toLocaleString() || '0'}
                            </span>
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <span className="text-sm text-gray-600">
                            {item.minThreshold?.toLocaleString() || 'N/A'}
                          </span>
                        </td>
                        <td className="px-6 py-4">
                          <span className={`inline-flex px-2 py-1 text-xs font-medium rounded-full border ${getStockLevelColor(item.stockLevel)}`}>
                            {item.stockLevel || 'N/A'}
                          </span>
                        </td>
                        <td className="px-6 py-4">
                          <div className="text-sm text-gray-900">
                            {item.inventory?.name || 'N/A'}
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>

        {/* Footer */}
        <div className="p-6 border-t border-gray-200 bg-gray-50">
          <div className="flex justify-between items-center">
            <div className="text-sm text-gray-600">
              💡 <strong>Lưu ý:</strong> Các sản phẩm có tồn kho ≤ ngưỡng tối thiểu
            </div>
            <button
              onClick={onClose}
              className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg transition-colors"
            >
              Đóng
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LowStockModal;
