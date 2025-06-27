import React from 'react';
import { useQueries } from '@tanstack/react-query';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { dashboardService } from '../../services/dashboardService';
import { AlertTriangle, BarChart2, DollarSign, LoaderCircle, ShoppingCart } from 'lucide-react';

// Helper to format currency
const formatCurrency = (value: number) => {
  if (typeof value !== 'number') return 'N/A';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(value);
};

// Helper to generate the last 6 months
const getLastSixMonths = () => {
  const months = [];
  const date = new Date();
  for (let i = 0; i < 6; i++) {
    months.push({ year: date.getFullYear(), month: date.getMonth() + 1 });
    date.setMonth(date.getMonth() - 1);
  }
  return months.reverse(); // Oldest month first
};

// Stat Card Component
const StatCard = ({ title, value, icon: Icon, change, isLoading }: { title: string, value: string, icon: React.ElementType, change?: number | null, isLoading: boolean }) => {
  const changeColor = change === undefined || change === null ? 'text-gray-500' : change >= 0 ? 'text-green-500' : 'text-red-500';
  const changeText = change === undefined || change === null ? '' : change >= 0 ? `+${change.toFixed(1)}%` : `${change.toFixed(1)}%`;

  return (
    <div className="bg-white p-6 rounded-lg shadow-sm flex items-center border border-gray-200">
      <div className={`p-3 rounded-full mr-4 bg-blue-100 text-blue-600`}>
        <Icon className="w-6 h-6" />
      </div>
      <div>
        <p className="text-sm font-medium text-gray-500">{title}</p>
        {isLoading ? (
          <div className="h-8 w-32 bg-gray-200 rounded animate-pulse mt-1"></div>
        ) : (
          <p className="text-2xl font-bold text-gray-800">{value}</p>
        )}
        {!isLoading && change !== undefined && change !== null && (
          <p className={`text-xs mt-1 ${changeColor}`}>{changeText} so với tháng trước</p>
        )}
      </div>
    </div>
  );
};


// Main Dashboard Component
const DashboardHomeNew = ({ branchId }: { branchId: number }) => {
  const sixMonths = getLastSixMonths();

  const results = useQueries({
    queries: sixMonths.map(({ year, month }) => ({
      queryKey: ['revenueStats', year, month, branchId],
      queryFn: () => dashboardService.getRevenueStatistics({ year, month, branchId }),
      staleTime: 1000 * 60 * 5, // 5 minutes
      refetchInterval: 1000 * 60 * 5, // Refetch every 5 minutes
    })),
  });

  const isLoading = results.some(r => r.isLoading);
  const isError = results.some(r => r.isError);

  const chartData = results
    .map((result, index) => {
      if (result.isSuccess && result.data) {
        const monthName = new Date(sixMonths[index].year, sixMonths[index].month - 1).toLocaleString('vi-VN', { month: 'long' });
        return {
          name: `${monthName} '${String(sixMonths[index].year).slice(2)}`,
          Revenue: result.data.totalMonthlyRevenue || 0,
        };
      }
      return null;
    })
    .filter((item): item is { name: string; Revenue: number } => item !== null);

  const currentMonthQuery = results[results.length - 1];
  const currentMonthData = currentMonthQuery?.data;

  if (isError) {
    return (
      <div className="flex items-center justify-center h-64 bg-red-50 text-red-700 rounded-lg p-6">
        <AlertTriangle className="w-8 h-8 mr-3" />
        <div>
          <h2 className="font-bold">Lỗi khi tải dữ liệu</h2>
          <p>Không thể tải dữ liệu thống kê. Vui lòng thử lại sau.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="p-4 md:p-6 bg-gray-50 min-h-screen font-sans">
      <h1 className="text-3xl font-bold text-gray-800 mb-6">Bảng điều khiển</h1>

      {/* Stat Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mb-8">
        <StatCard
          title="Doanh thu tháng này"
          value={formatCurrency(currentMonthData?.totalMonthlyRevenue ?? 0)}
          icon={DollarSign}
          change={currentMonthData?.revenueChangePercent}
          isLoading={currentMonthQuery?.isLoading ?? true}
        />
        <StatCard
          title="Đơn hàng tháng này"
          value={currentMonthData?.totalOrders?.toString() ?? '0'}
          icon={ShoppingCart}
          change={currentMonthData?.orderCountChangePercent}
          isLoading={currentMonthQuery?.isLoading ?? true}
        />
        <StatCard
          title="Doanh thu năm nay"
          value={formatCurrency(currentMonthData?.totalYearlyRevenue ?? 0)}
          icon={BarChart2}
          isLoading={currentMonthQuery?.isLoading ?? true}
        />
      </div>

      {/* Revenue Chart */}
      <div className="bg-white p-4 sm:p-6 rounded-lg shadow-sm border border-gray-200">
        <h2 className="text-xl font-semibold text-gray-700 mb-4">Doanh thu (6 tháng gần nhất)</h2>
        {isLoading && chartData.length === 0 ? (
            <div className="flex items-center justify-center h-96">
                <LoaderCircle className="w-12 h-12 animate-spin text-blue-500" />
            </div>
        ) : (
            <ResponsiveContainer width="100%" height={400}>
            <BarChart data={chartData} margin={{ top: 5, right: 20, left: -10, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" vertical={false} />
                <XAxis dataKey="name" tick={{ fontSize: 12 }} stroke="#6b7280" />
                <YAxis tickFormatter={(value) => `${Number(value) / 1000000}M`} tick={{ fontSize: 12 }} stroke="#6b7280" />
                <Tooltip
                  contentStyle={{ backgroundColor: 'rgba(255, 255, 255, 0.8)', backdropFilter: 'blur(4px)', border: '1px solid #ddd', borderRadius: '8px' }}
                  formatter={(value: number) => [formatCurrency(value), 'Doanh thu']}
                  labelStyle={{ fontWeight: 'bold' }}
                />
                <Legend wrapperStyle={{ fontSize: '14px', paddingTop: '20px' }} />
                <Bar dataKey="Revenue" fill="#3b82f6" barSize={30} radius={[4, 4, 0, 0]} />
            </BarChart>
            </ResponsiveContainer>
        )}
      </div>
    </div>
  );
};

export default DashboardHomeNew;
