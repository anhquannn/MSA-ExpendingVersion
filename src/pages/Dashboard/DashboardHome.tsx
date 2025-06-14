import React, { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer,
  BarChart, Bar
} from 'recharts';
import DatePicker from 'react-datepicker'; 
import 'react-datepicker/dist/react-datepicker.css'; 

const DashboardHome: React.FC = () => {
  const navigate = useNavigate();

  // State cho biểu đồ Doanh thu (sẽ là biểu đồ chính từ RevenueStatsPage)
  const [revenueTimeRange, setRevenueTimeRange] = useState<'monthly' | 'weekly' | 'daily' | 'yearly' | 'custom'>('monthly');
  const [revenueStartDate, setRevenueStartDate] = useState<Date | null>(null);
  const [revenueEndDate, setRevenueEndDate] = useState<Date | null>(null);

  // State cho biểu đồ Số lượng đơn hàng (giữ nguyên)
  const [orderCountTimeRange, setOrderCountTimeRange] = useState<'monthly' | 'weekly' | 'daily' | 'yearly' | 'custom'>('monthly');
  const [orderCountStartDate, setOrderCountStartDate] = useState<Date | null>(null);
  const [orderCountEndDate, setOrderCountEndDate] = useState<Date | null>(null);

  // --- Dữ liệu giả (Tất cả dữ liệu cho cả Doanh thu, Số lượng đơn hàng, và Sản phẩm hot) ---

  // Dữ liệu giả cho biểu đồ doanh thu
  const monthlyRevenueData = [
    { month: 'T1', doanhThu: 40000000 }, { month: 'T2', doanhThu: 45000000 },
    { month: 'T3', doanhThu: 42000000 }, { month: 'T4', doanhThu: 48000000 },
    { month: 'T5', doanhThu: 55000000 }, { month: 'T6', doanhThu: 60000000 },
    { month: 'T7', doanhThu: 58000000 }, { month: 'T8', doanhThu: 65000000 },
    { month: 'T9', doanhThu: 62000000 }, { month: 'T10', doanhThu: 70000000 },
    { month: 'T11', doanhThu: 68000000 }, { month: 'T12', doanhThu: 75000000 },
  ];
  const weeklyRevenueData = [
    { week: 'Tuần 1', doanhThu: 10000000 }, { week: 'Tuần 2', doanhThu: 12000000 },
    { week: 'Tuần 3', doanhThu: 9000000 }, { week: 'Tuần 4', doanhThu: 13000000 },
    { week: 'Tuần 5', doanhThu: 11000000 }, { week: 'Tuần 6', doanhThu: 14000000 },
  ];
  const dailyRevenueData = [
    { date: '1', doanhThu: 2000000 }, { date: '2', doanhThu: 2500000 },
    { date: '3', doanhThu: 1800000 }, { date: '4', doanhThu: 3000000 },
    { date: '5', doanhThu: 2200000 }, { date: '6', doanhThu: 2800000 },
    { date: '7', doanhThu: 3500000 },
  ];
  const yearlyRevenueData = [
    { year: '2021', doanhThu: 500000000 }, { year: '2022', doanhThu: 620000000 },
    { year: '2023', doanhThu: 700000000 }, { year: '2024', doanhThu: 850000000 },
    { year: '2025', doanhThu: 900000000 },
  ];

  // Dữ liệu giả cho biểu đồ độ hot của sản phẩm (giữ nguyên)
  const productHotnessData = [
    { name: 'Sữa tươi Vinamilk', sales: 1200 },
    { name: 'Gạo ST25', sales: 950 },
    { name: 'Táo Gala Mỹ', sales: 800 },
    { name: 'Mì gói Hảo Hảo', sales: 720 },
    { name: 'Bánh mì sandwich', sales: 600 },
  ];

  // Dữ liệu giả cho số lượng đơn hàng (giữ nguyên)
  const monthlyOrderCountData = [
    { month: 'T1', soLuongDon: 1500 }, { month: 'T2', soLuongDon: 1700 },
    { month: 'T3', soLuongDon: 1600 }, { month: 'T4', soLuongDon: 1800 },
    { month: 'T5', soLuongDon: 2000 }, { month: 'T6', soLuongDon: 2200 },
    { month: 'T7', soLuongDon: 2100 }, { month: 'T8', soLuongDon: 2400 },
    { month: 'T9', soLuongDon: 2300 }, { month: 'T10', soLuongDon: 2500 },
    { month: 'T11', soLuongDon: 2450 }, { month: 'T12', soLuongDon: 2700 },
  ];
  const weeklyOrderCountData = [
    { week: 'Tuần 1', soLuongDon: 300 }, { week: 'Tuần 2', soLuongDon: 350 },
    { week: 'Tuần 3', soLuongDon: 280 }, { week: 'Tuần 4', soLuongDon: 400 },
    { week: 'Tuần 5', soLuongDon: 320 }, { week: 'Tuần 6', soLuongDon: 380 },
  ];
  const dailyOrderCountData = [
    { date: '1', soLuongDon: 50 }, { date: '2', soLuongDon: 60 },
    { date: '3', soLuongDon: 45 }, { date: '4', soLuongDon: 70 },
    { date: '5', soLuongDon: 55 }, { date: '6', soLuongDon: 65 },
    { date: '7', soLuongDon: 75 },
  ];
  const yearlyOrderCountData = [
    { year: '2021', soLuongDon: 15000 }, { year: '2022', soLuongDon: 18000 },
    { year: '2023', soLuongDon: 20000 }, { year: '2024', soLuongDon: 22000 },
    { year: '2025', soLuongDon: 25000 },
  ];
  
  // Dữ liệu giả cho các widget thống kê doanh thu (từ RevenueStatsPage)
  const mockOverallRevenueData = {
    today: '1,500,000 VNĐ',
    week: '10,200,000 VNĐ',
    month: '45,800,000 VNĐ',
    yearlyGrowth: '+12%',
  };
  // Giá trị đơn hàng trung bình (dữ liệu giả)
  const averageOrderValue = '185,000 VNĐ';


  // Selector cho dữ liệu biểu đồ Doanh thu (đã được chỉnh sửa để sử dụng revenueTimeRange)
  const { data: revenueChartData, xAxisKey: revenueXAxisKey, title: revenueChartTitle } = useMemo(() => {
    switch (revenueTimeRange) {
      case 'daily':
        return { data: dailyRevenueData, xAxisKey: 'date', title: 'Doanh Thu Hàng Ngày (VNĐ)' };
      case 'weekly':
        return { data: weeklyRevenueData, xAxisKey: 'week', title: 'Doanh Thu Hàng Tuần (VNĐ)' };
      case 'yearly':
        return { data: yearlyRevenueData, xAxisKey: 'year', title: 'Doanh Thu Hàng Năm (VNĐ)' };
      case 'custom':
        const customRevenueData = dailyRevenueData.slice(0, Math.floor(Math.random() * dailyRevenueData.length) + 1); 
        return { data: customRevenueData.length ? customRevenueData : [{date: 'No Data', doanhThu: 0}], xAxisKey: 'date', title: `Doanh Thu Tùy Chỉnh (${revenueStartDate ? revenueStartDate.toLocaleDateString() : ''} - ${revenueEndDate ? revenueEndDate.toLocaleDateString() : ''})` };
      default: // 'monthly'
        return { data: monthlyRevenueData, xAxisKey: 'month', title: 'Doanh Thu Hàng Tháng (VNĐ)' };
    }
  }, [revenueTimeRange, revenueStartDate, revenueEndDate]);

  // Selector cho dữ liệu biểu đồ Số lượng đơn hàng (giữ nguyên)
  const { data: orderCountChartData, xAxisKey: orderCountXAxisKey, title: orderCountChartTitle } = useMemo(() => {
    switch (orderCountTimeRange) {
      case 'daily':
        return { data: dailyOrderCountData, xAxisKey: 'date', title: 'Số Lượng Đơn Hàng Hàng Ngày' };
      case 'weekly':
        return { data: weeklyOrderCountData, xAxisKey: 'week', title: 'Số Lượng Đơn Hàng Hàng Tuần' };
      case 'yearly':
        return { data: yearlyOrderCountData, xAxisKey: 'year', title: 'Số Lượng Đơn Hàng Hàng Năm' };
      case 'custom':
        const customOrderCountData = dailyOrderCountData.slice(0, Math.floor(Math.random() * dailyOrderCountData.length) + 1); 
        return { data: customOrderCountData.length ? customOrderCountData : [{date: 'No Data', soLuongDon: 0}], xAxisKey: 'date', title: `Số Lượng Đơn Hàng Tùy Chỉnh (${orderCountStartDate ? orderCountStartDate.toLocaleDateString() : ''} - ${orderCountEndDate ? orderCountEndDate.toLocaleDateString() : ''})` };
      default: // 'monthly'
        return { data: monthlyOrderCountData, xAxisKey: 'month', title: 'Số Lượng Đơn Hàng Hàng Tháng' };
    }
  }, [orderCountTimeRange, orderCountStartDate, orderCountEndDate]);


  return (
    <div className="bg-white rounded-lg shadow-md"> {/* Đã bỏ p-6 ở đây, sẽ thêm vào các phần tử con */}
      <h2 className="text-2xl font-semibold text-gray-700 pt-6 px-6 mb-4">Chào mừng, Manager!</h2>
      <p className="text-gray-600 px-6 mb-6">Đây là trang tổng quan dashboard của bạn. Bạn có thể xem nhanh các số liệu quan trọng tại đây.</p>
      
      {/* Các widget tóm tắt chính (từ DashboardHome trước đây) */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-8 px-6"> 
        <button 
          onClick={() => navigate('/dashboard/orders')}
          className="bg-green-100 p-4 rounded-lg shadow-md hover:shadow-lg transform hover:-translate-y-1 transition duration-300 ease-in-out cursor-pointer text-left focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-opacity-50"
        >
          <h3 className="font-bold text-lg text-green-800">Tổng Đơn Hàng Hôm Nay</h3>
          <p className="text-3xl text-green-600 mt-2">120</p>
        </button>

        <button 
          onClick={() => navigate('/dashboard/inventory')}
          className="bg-yellow-100 p-4 rounded-lg shadow-md hover:shadow-lg transform hover:-translate-y-1 transition duration-300 ease-in-out cursor-pointer text-left focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:ring-opacity-50"
        >
          <h3 className="font-bold text-lg text-yellow-800">Sản Phẩm Hết Hàng</h3>
          <p className="text-3xl text-yellow-600 mt-2">5</p>
        </button>
      </div>

      {/* Dòng hiển thị Giá đơn hàng trung bình */}
      <div className="bg-white p-6 rounded-lg mx-6 mb-8 text-center border border-gray-200 shadow-sm">
        <h3 className="text-xl font-bold text-purple-700 mb-2">Giá Đơn Hàng Trung Bình</h3>
        <p className="text-4xl font-extrabold text-purple-600">{averageOrderValue}</p>
      </div>

      {/* --- Phần Thống Kê Doanh Thu (Gộp từ RevenueStatsPage) --- */}
      <div className="px-6 mb-8"> {/* Thêm padding ngang */}
        <h3 className="text-2xl font-semibold text-gray-700 mb-4">Tổng quan Doanh Thu</h3> {/* Thay đổi tiêu đề */}
        <p className="text-gray-600 mb-6">Các số liệu chính về doanh thu.</p>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          <div className="bg-blue-100 p-4 rounded-lg shadow">
            <h3 className="font-bold text-lg text-blue-800">Doanh Thu Hôm Nay</h3>
            <p className="text-3xl text-blue-600 mt-2">{mockOverallRevenueData.today}</p>
          </div>
          <div className="bg-green-100 p-4 rounded-lg shadow">
            <h3 className="font-bold text-lg text-green-800">Doanh Thu Tuần Này</h3>
            <p className="text-3xl text-green-600 mt-2">{mockOverallRevenueData.week}</p>
          </div>
          <div className="bg-purple-100 p-4 rounded-lg shadow">
            <h3 className="font-bold text-lg text-purple-800">Doanh Thu Tháng Này</h3>
            <p className="text-3xl text-purple-600 mt-2">{mockOverallRevenueData.month}</p>
          </div>
          <div className="bg-yellow-100 p-4 rounded-lg shadow">
            <h3 className="font-bold text-lg text-yellow-800">Tăng Trưởng Năm</h3>
            <p className="text-3xl text-yellow-600 mt-2">{mockOverallRevenueData.yearlyGrowth}</p>
          </div>
        </div>

        {/* Biểu đồ Doanh thu (có tùy chọn thời gian) */}
        <div className="p-6 bg-gray-50 rounded-lg shadow-inner">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-xl font-semibold text-gray-700">{revenueChartTitle}</h3>
            <div className="flex items-center space-x-3">
              <select
                value={revenueTimeRange}
                onChange={(e) => {
                  setRevenueTimeRange(e.target.value as typeof revenueTimeRange);
                  if (e.target.value !== 'custom') {
                    setRevenueStartDate(null);
                    setRevenueEndDate(null);
                  }
                }}
                className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
              >
                <option value="monthly">Theo Tháng</option>
                <option value="weekly">Theo Tuần</option>
                <option value="daily">Theo Ngày</option>
                <option value="yearly">Theo Năm</option>
                <option value="custom">Chọn Ngày</option>
              </select>

              {revenueTimeRange === 'custom' && (
                <>
                  <DatePicker
                    selected={revenueStartDate}
                    onChange={(date: Date | null) => setRevenueStartDate(date)}
                    selectsStart
                    startDate={revenueStartDate}
                    endDate={revenueEndDate}
                    placeholderText="Ngày bắt đầu"
                    dateFormat="dd/MM/yyyy"
                    className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  />
                  <DatePicker
                    selected={revenueEndDate}
                    onChange={(date: Date | null) => setRevenueEndDate(date)}
                    selectsEnd
                    startDate={revenueStartDate}
                    endDate={revenueEndDate}
                    minDate={revenueStartDate ?? undefined} 
                    placeholderText="Ngày kết thúc"
                    dateFormat="dd/MM/yyyy"
                    className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                  />
                </>
              )}
            </div>
          </div>
          
          <ResponsiveContainer width="100%" height={300}>
            <LineChart
              data={revenueChartData} 
              margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
            >
              <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" />
              <XAxis dataKey={revenueXAxisKey} tickLine={false} axisLine={false} /> 
              <YAxis 
                tickFormatter={(value) => `${(value / 1000000).toFixed(0)}M`}
                axisLine={false} 
                tickLine={false}
              />
              <Tooltip 
                formatter={(value: number) => `${value.toLocaleString('vi-VN')} VNĐ`}
              />
              <Legend />
              <Line 
                type="monotone" 
                dataKey="doanhThu" 
                stroke="#22C55E" 
                activeDot={{ r: 8 }} 
                strokeWidth={2}
                name="Doanh Thu"
              />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>
      
      {/* Biểu đồ Số lượng đơn hàng (giữ nguyên) */}
      <div className="mb-8 p-6 bg-gray-50 rounded-lg shadow-inner mx-6"> 
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-xl font-semibold text-gray-700">{orderCountChartTitle}</h3>
          <div className="flex items-center space-x-3">
            <select
              value={orderCountTimeRange}
              onChange={(e) => {
                setOrderCountTimeRange(e.target.value as typeof orderCountTimeRange);
                if (e.target.value !== 'custom') {
                  setOrderCountStartDate(null);
                  setOrderCountEndDate(null);
                }
              }}
              className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="monthly">Theo Tháng</option>
              <option value="weekly">Theo Tuần</option>
              <option value="daily">Theo Ngày</option>
              <option value="yearly">Theo Năm</option>
              <option value="custom">Chọn Ngày</option>
            </select>

            {orderCountTimeRange === 'custom' && (
              <>
                <DatePicker
                  selected={orderCountStartDate}
                  onChange={(date: Date | null) => setOrderCountStartDate(date)}
                  selectsStart
                  startDate={orderCountStartDate}
                  endDate={orderCountEndDate}
                  placeholderText="Ngày bắt đầu"
                  dateFormat="dd/MM/yyyy"
                  className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
                <DatePicker
                  selected={orderCountEndDate}
                  onChange={(date: Date | null) => setOrderCountEndDate(date)}
                  selectsEnd
                  startDate={orderCountStartDate}
                  endDate={orderCountEndDate}
                  minDate={orderCountStartDate ?? undefined} 
                  placeholderText="Ngày kết thúc"
                  dateFormat="dd/MM/yyyy"
                  className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
              </>
            )}
          </div>
        </div>
        
        <ResponsiveContainer width="100%" height={300}>
          <BarChart
            data={orderCountChartData} 
            margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
          >
            <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" vertical={false} /> 
            <XAxis dataKey={orderCountXAxisKey} tickLine={false} axisLine={false} /> 
            <YAxis 
              tickFormatter={(value) => `${value}`} 
              axisLine={false} 
              tickLine={false}
            />
            <Tooltip 
              formatter={(value: number) => `${value} đơn`}
            />
            <Legend />
            <Bar 
              dataKey="soLuongDon" 
              fill="#8884d8" 
              name="Số Lượng Đơn Hàng"
              barSize={30}
            />
          </BarChart>
        </ResponsiveContainer>
      </div>

      {/* Biểu đồ Độ hot của Sản phẩm (giữ nguyên) */}
      <div className="p-6 bg-gray-50 rounded-lg shadow-inner mx-6"> 
        <h3 className="text-xl font-semibold text-gray-700 mb-4">Top Sản Phẩm Bán Chạy Nhất</h3>
        <ResponsiveContainer width="100%" height={300}>
          <BarChart
            data={productHotnessData}
            margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
            layout="vertical"
          >
            <CartesianGrid strokeDasharray="3 3" stroke="#e0e0e0" horizontal={false} />
            <XAxis type="number" tickLine={false} axisLine={false} />
            <YAxis type="category" dataKey="name" tickLine={false} axisLine={false} width={120} />
            <Tooltip 
              formatter={(value: number) => `${value} lượt bán`}
            />
            <Legend />
            <Bar 
              dataKey="sales" 
              fill="#3B82F6" 
              name="Lượt Bán"
              barSize={30}
            />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export default DashboardHome;