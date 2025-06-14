import React from 'react';

const InventoryPage: React.FC = () => {
  // Dữ liệu kiểm kho giả
  const mockInventory = [
    { id: 'P001', name: 'Táo Gala', stock: 150, unit: 'kg', lowStockThreshold: 50 },
    { id: 'P002', name: 'Sữa tươi Vinamilk 1L', stock: 20, unit: 'hộp', lowStockThreshold: 30 }, // Sắp hết hàng
    { id: 'P003', name: 'Gạo ST25 5kg', stock: 80, unit: 'bao', lowStockThreshold: 20 },
    { id: 'P004', name: 'Bánh mì sandwich', stock: 5, unit: 'gói', lowStockThreshold: 10 }, // Cần nhập thêm
  ];

  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-semibold text-gray-700 mb-4">Kiểm Kho</h2>
      <p className="text-gray-600 mb-6">Quản lý và theo dõi tồn kho sản phẩm.</p>

      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-200">
          <thead>
            <tr className="bg-gray-100 text-gray-600 uppercase text-sm leading-normal">
              <th className="py-3 px-6 text-left">Mã SP</th>
              <th className="py-3 px-6 text-left">Tên Sản Phẩm</th>
              <th className="py-3 px-6 text-left">Tồn Kho</th>
              <th className="py-3 px-6 text-left">Đơn Vị</th>
              <th className="py-3 px-6 text-left">Trạng Thái</th>
            </tr>
          </thead>
          <tbody className="text-gray-600 text-sm font-light">
            {mockInventory.map((item) => (
              <tr key={item.id} className="border-b border-gray-200 hover:bg-gray-50">
                <td className="py-3 px-6 text-left whitespace-nowrap">{item.id}</td>
                <td className="py-3 px-6 text-left">{item.name}</td>
                <td className="py-3 px-6 text-left">{item.stock}</td>
                <td className="py-3 px-6 text-left">{item.unit}</td>
                <td className="py-3 px-6 text-left">
                  <span 
                    className={`px-3 py-1 rounded-full text-xs font-semibold
                      ${item.stock > item.lowStockThreshold ? 'bg-green-200 text-green-800' :
                         item.stock > 0 ? 'bg-yellow-200 text-yellow-800' :
                         'bg-red-200 text-red-800'}`
                    }
                  >
                    {item.stock > item.lowStockThreshold ? 'Đủ Hàng' :
                     item.stock > 0 ? 'Sắp Hết' :
                     'Hết Hàng'}
                  </span>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default InventoryPage;