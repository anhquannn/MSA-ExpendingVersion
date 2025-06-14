import React from 'react';

const RevenueStatsPage: React.FC = () => {
  // Dữ liệu thống kê doanh thu giả
  const mockRevenueData = {
    today: '1,500,000 VNĐ',
    week: '10,200,000 VNĐ',
    month: '45,800,000 VNĐ',
    yearlyGrowth: '+12%',
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-semibold text-gray-700 mb-4">Thống Kê Doanh Thu</h2>
      <p className="text-gray-600 mb-6">Tổng quan về doanh thu của chi nhánh.</p>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="bg-blue-100 p-4 rounded-lg shadow">
          <h3 className="font-bold text-lg text-blue-800">Doanh Thu Hôm Nay</h3>
          <p className="text-3xl text-blue-600 mt-2">{mockRevenueData.today}</p>
        </div>
        <div className="bg-green-100 p-4 rounded-lg shadow">
          <h3 className="font-bold text-lg text-green-800">Doanh Thu Tuần Này</h3>
          <p className="text-3xl text-green-600 mt-2">{mockRevenueData.week}</p>
        </div>
        <div className="bg-purple-100 p-4 rounded-lg shadow">
          <h3 className="font-bold text-lg text-purple-800">Doanh Thu Tháng Này</h3>
          <p className="text-3xl text-purple-600 mt-2">{mockRevenueData.month}</p>
        </div>
        <div className="bg-yellow-100 p-4 rounded-lg shadow">
          <h3 className="font-bold text-lg text-yellow-800">Tăng Trưởng Năm</h3>
          <p className="text-3xl text-yellow-600 mt-2">{mockRevenueData.yearlyGrowth}</p>
        </div>
      </div>

      {/* Có thể thêm biểu đồ (ví dụ: dùng Recharts) hoặc bảng chi tiết hơn tại đây */}
      <div className="mt-8 bg-gray-50 p-4 rounded-lg border border-gray-200">
        <h3 className="font-semibold text-xl text-gray-700 mb-3">Biểu đồ doanh thu hàng tháng (Mock)</h3>
        <div className="h-64 flex items-center justify-center text-gray-400">
          (Không có biểu đồ hiển thị, chỉ là placeholder)
        </div>
      </div>
    </div>
  );
};

export default RevenueStatsPage;