import React, { useState, useEffect } from 'react';
import { useQueries, useQuery } from '@tanstack/react-query';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, Legend, Area, AreaChart } from 'recharts';
import { dashboardService, BranchRevenue, TopCustomer } from '../../services/dashboardService';
import { productService, ProductSalesStatistics } from '../../services/productService';
import { branchService, Branch } from '../../services/branchService';
import { AlertTriangle, BarChart2, DollarSign, LoaderCircle, ShoppingCart, Grid, List, ChevronDown, AlertCircle, TrendingUp, Package, Calendar, Filter, Users, X } from 'lucide-react';
import { getSavedFCMToken } from '../../config/firebaseConfig';

const CheckFCMToken = () => {
  const checkToken = () => {
    const token = getSavedFCMToken();
    if (token) {
      console.log('FCM Token:', token);
      alert('Đã có token FCM');
    } else {
      console.log('Chưa có token FCM');
      alert('Chưa có token FCM');
    }
  };

  return (
    <button 
      onClick={checkToken}
      className="px-4 py-2 bg-gradient-to-r from-blue-500 to-purple-600 text-white rounded-lg hover:from-blue-600 hover:to-purple-700 transition-all duration-300 shadow-lg hover:shadow-xl transform hover:scale-105"
    >
      Kiểm tra FCM Token
    </button>
  );
};



// Helper to format currency
const formatCurrency = (value: number) => {
  if (typeof value !== 'number') return 'N/A';
  return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(value);
};

// Short number formatter: 1_000 -> 1 K, 1_000_000 -> 1 M, 1_000_000_000 -> 1 B
const formatShort = (v: number) => {
  if (v >= 1_000_000_000) return `${(v / 1_000_000_000).toFixed(1)} B`;
  if (v >= 1_000_000) return `${(v / 1_000_000).toFixed(1)} M`;
  if (v >= 1_000) return `${(v / 1_000).toFixed(1)} K`;
  return v.toString();
};

// Helper to generate recent months based on range
const getRecentMonths = (range: number) => {
  const months = [] as { year: number, month: number }[];
  const date = new Date();
  for (let i = 0; i < range; i++) {
    months.push({ year: date.getFullYear(), month: date.getMonth() + 1 });
    date.setMonth(date.getMonth() - 1);
  }
  return months.reverse();
};

// Enhanced Stat Card Component
const StatCard = ({ 
  title, 
  value, 
  icon: Icon, 
  change, 
  isLoading, 
  color = 'blue',
  onClick
}: { 
  title: string, 
  value: string, 
  icon: React.ElementType, 
  change?: number | null, 
  isLoading: boolean, 
  color?: 'blue' | 'green' | 'purple' | 'red' | 'yellow' | 'indigo' | 'pink' | 'cyan',
  onClick?: () => void
}) => {
  const changeColor = change === undefined || change === null ? 'text-gray-500' : change >= 0 ? 'text-emerald-500' : 'text-red-500';
  const colorMap: Record<string, { bg: string, text: string, gradient: string }> = {
    blue: { bg: 'bg-blue-50', text: 'text-blue-600', gradient: 'from-blue-500 to-blue-600' },
    green: { bg: 'bg-emerald-50', text: 'text-emerald-600', gradient: 'from-emerald-500 to-emerald-600' },
    purple: { bg: 'bg-purple-50', text: 'text-purple-600', gradient: 'from-purple-500 to-purple-600' },
    red: { bg: 'bg-red-50', text: 'text-red-600', gradient: 'from-red-500 to-red-600' },
    yellow: { bg: 'bg-yellow-50', text: 'text-yellow-600', gradient: 'from-yellow-500 to-yellow-600' },
    indigo: { bg: 'bg-indigo-50', text: 'text-indigo-600', gradient: 'from-indigo-500 to-indigo-600' },
    pink: { bg: 'bg-pink-50', text: 'text-pink-600', gradient: 'from-pink-500 to-pink-600' },
    cyan: { bg: 'bg-cyan-50', text: 'text-cyan-600', gradient: 'from-cyan-500 to-cyan-600' }
  };
  
  const changeText = change === undefined || change === null ? '' : change >= 0 ? `+${change.toFixed(1)}%` : `${change.toFixed(1)}%`;
  const colors = colorMap[color];

  return (
    <div 
      className={`group relative bg-white p-6 rounded-xl shadow-sm hover:shadow-lg transition-all duration-300 border border-gray-100 hover:border-gray-200 ${onClick ? 'cursor-pointer' : ''} overflow-hidden`}
      onClick={onClick}
    >
      {/* Decorative gradient background */}
      <div className={`absolute inset-0 bg-gradient-to-br ${colors.gradient} opacity-0 group-hover:opacity-5 transition-opacity duration-300`}></div>
      
      <div className="relative flex items-center justify-between">
        <div className="flex items-center">
          <div className={`p-3 rounded-xl ${colors.bg} group-hover:scale-110 transition-transform duration-300`}>
            <Icon className={`w-6 h-6 ${colors.text}`} />
          </div>
          <div className="ml-4">
            <p className="text-sm font-medium text-gray-600 mb-1">{title}</p>
            {isLoading ? (
              <div className="h-8 w-32 bg-gradient-to-r from-gray-200 to-gray-300 rounded-lg animate-pulse"></div>
            ) : (
              <p className="text-2xl font-bold text-gray-800 group-hover:text-gray-900 transition-colors">{value}</p>
            )}
            {!isLoading && change !== undefined && change !== null && (
              <div className="flex items-center mt-1">
                <TrendingUp className={`w-3 h-3 mr-1 ${changeColor}`} />
                <p className={`text-xs font-medium ${changeColor}`}>{changeText} so với tháng trước</p>
              </div>
            )}
          </div>
        </div>
        
        {onClick && (
          <div className="opacity-0 group-hover:opacity-100 transition-opacity duration-300">
            <ChevronDown className="w-5 h-5 text-gray-400 rotate-270" />
          </div>
        )}
      </div>
    </div>
  );
};

// Main Dashboard Component
interface DashboardHomeNewProps {
  branchId: number;
}

const DashboardHomeNew: React.FC<DashboardHomeNewProps> = ({ branchId }) => {
  const [productModalId, setProductModalId] = useState<number | null>(null);
  const [selectedProductBranchId, setSelectedProductBranchId] = useState<number | undefined>(undefined);
  const [selectedBranchId, setSelectedBranchId] = useState<number | undefined>(undefined);
  const [selectedPeriod, setSelectedPeriod] = useState<'ALL' | 'BRANCH'>('ALL');
  const [rangeMonths, setRangeMonths] = useState<3 | 6 | 12>(6);
  const [topLimit, setTopLimit] = useState<5 | 10 | 20 | 30>(10);
  const [branches, setBranches] = useState<Branch[]>([]);
  const [showExpiring, setShowExpiring] = useState(false);
  // Calculate months array and current year/month once per rangeMonths
  const monthsArray = getRecentMonths(rangeMonths);
  const { year: currYear, month: currMonth } = monthsArray[monthsArray.length - 1];
  // fetch top customers
  const topCustomerQuery = useQuery({
    queryKey: ['topCustomers', selectedBranchId, rangeMonths, topLimit, currYear, currMonth],
    queryFn: () => dashboardService.getTopCustomers({
      year: currYear,
      month: currMonth,
      branchId: selectedBranchId,
      topPeriod: rangeMonths,
      topLimit: topLimit,
    }),
  });
  const topCustomers = topCustomerQuery.data ?? [];
  const loadingTopCustomers = topCustomerQuery.isLoading;


  // Enhanced color palette
  const STAT_COLORS: Array<'blue' | 'green' | 'purple' | 'red' | 'yellow' | 'indigo' | 'pink' | 'cyan'> = ['blue', 'green', 'purple', 'red'];
  const BRANCH_COLORS = [
    '#3B82F6', '#10B981', '#8B5CF6', '#F59E0B', '#EF4444', 
    '#06B6D4', '#EC4899', '#6366F1', '#84CC16', '#F97316'
  ];

  // fetch branches once
  useEffect(() => {
    (async () => {
      try {
        const res = await branchService.getAllBranchesWithPaging({ page: 1, pageSize: 100 });
        setBranches(res.content);
      } catch (err) {
        console.error('Error fetching branches', err);
      }
    })();
  }, []);

  // monthsArray declared above

  // Branch revenue query
  // currYear and currMonth declared above
  const { data: branchRevenues = [], isLoading: branchLoading } = useQuery<BranchRevenue[]>({
    queryKey: ['branchRevenues', currYear, currMonth, rangeMonths],
    queryFn: () => dashboardService.getBranchRevenues({ year: currYear, month: currMonth, periodMonths: rangeMonths }),
    staleTime: 1000 * 60 * 5,
  });
  const branchChartData = (branchRevenues as BranchRevenue[]).map(b => ({ name: b.name, revenue: b.totalRevenue }));

  const results = useQueries({
    queries: monthsArray.map(({ year, month }) => ({
      queryKey: ['revenueStats', year, month, selectedBranchId, selectedPeriod, topLimit],
      queryFn: () => dashboardService.getRevenueStatistics({
        year,
        month,
        branchId: selectedBranchId,
        topPeriod: rangeMonths,
        topLimit: topLimit,
        expiringDays: 30
      }),
      staleTime: 1000 * 60 * 5,
      refetchInterval: 1000 * 60 * 5
    })),
  });

  const isLoading = results.some(r => r.isLoading);
  const isError = results.some(r => r.isError);

  const chartData = results
    .map((result, index) => {
      if (result.isSuccess && result.data) {
        const monthName = new Date(monthsArray[index].year, monthsArray[index].month - 1).toLocaleString('vi-VN', { month: 'long' });
        return {
          name: `${monthName} '${String(monthsArray[index].year).slice(2)}`,
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
      <div className="min-h-screen bg-gradient-to-br from-red-50 to-pink-50 flex items-center justify-center p-8">
        <div className="bg-white rounded-2xl shadow-xl p-8 max-w-md w-full text-center">
          <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <AlertTriangle className="w-8 h-8 text-red-600" />
          </div>
          <h2 className="text-xl font-bold text-gray-800 mb-2">Lỗi khi tải dữ liệu</h2>
          <p className="text-gray-600 mb-6">Không thể tải dữ liệu thống kê. Vui lòng thử lại sau.</p>
          <button className="px-6 py-3 bg-gradient-to-r from-red-500 to-pink-500 text-white rounded-lg hover:from-red-600 hover:to-pink-600 transition-all duration-300 shadow-lg hover:shadow-xl transform hover:scale-105">
            Thử lại
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50">
      {/* Header */}
      <div className="bg-white/80 backdrop-blur-sm border-b border-gray-200/50 sticky top-0 z-10">
        <div className="p-6">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h1 className="text-3xl font-bold bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent">
                Dashboard Analytics
              </h1>
              <p className="text-gray-600 mt-1">Tổng quan hiệu suất kinh doanh</p>
            </div>
            <div className="flex items-center space-x-2">
              <div className="w-2 h-2 bg-green-500 rounded-full animate-pulse"></div>
              <span className="text-sm text-gray-600">Đang cập nhật</span>
            </div>
          </div>

          {/* Enhanced Filter Section */}
          <div className="flex flex-wrap items-center gap-4 p-4 bg-white/60 backdrop-blur-sm rounded-xl border border-gray-200/50">
            <div className="flex items-center gap-2">
              <Filter className="w-4 h-4 text-gray-500" />
              <span className="text-sm font-medium text-gray-700">Bộ lọc:</span>
            </div>
            
            <div className="relative">
              <select
                value={selectedPeriod}
                onChange={(e) => setSelectedPeriod(e.target.value as 'ALL' | 'BRANCH')}
                className="appearance-none pr-10 px-4 py-2 bg-white/80 backdrop-blur-sm border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200"
              >
                <option value="ALL">🏢 Toàn bộ hệ thống</option>
                <option value="BRANCH">🏪 Theo chi nhánh</option>
              </select>
              <ChevronDown className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
            </div>

            {selectedPeriod === 'BRANCH' && (
              <div className="relative">
                <select
                  value={selectedBranchId || ''}
                  onChange={(e) => setSelectedBranchId(e.target.value ? parseInt(e.target.value) : undefined)}
                  className="appearance-none pr-10 px-4 py-2 bg-white/80 backdrop-blur-sm border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200"
                >
                  <option value="">Chọn chi nhánh</option>
                  {branches.map(b => (
                    <option key={b.branchId} value={b.branchId}>{b.name}</option>
                  ))}
                </select>
                <ChevronDown className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
              </div>
            )}

            <div className="relative">
              <select
                value={rangeMonths}
                onChange={e => setRangeMonths(Number(e.target.value) as 3 | 6 | 12)}
                className="appearance-none pr-10 px-4 py-2 bg-white/80 backdrop-blur-sm border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200"
              >
                <option value={3}>📅 3 tháng</option>
                <option value={6}>📅 6 tháng</option>
                <option value={12}>📅 1 năm</option>
              </select>
              <ChevronDown className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
            </div>

            <div className="relative">
              <select
                value={topLimit}
                onChange={e => setTopLimit(Number(e.target.value) as 5 | 10 | 20 | 30)}
                className="appearance-none pr-10 px-4 py-2 bg-white/80 backdrop-blur-sm border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-all duration-200"
              >
                <option value={5}>🏆 Top 5</option>
                <option value={10}>🏆 Top 10</option>
                <option value={20}>🏆 Top 20</option>
                <option value={30}>🏆 Top 30</option>
              </select>
              <ChevronDown className="absolute right-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
            </div>
          </div>
        </div>
      </div>

      <div className="p-6 space-y-8">
        {/* Enhanced Stats Grid */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          <StatCard
            title="Doanh thu tháng"
            color="blue"
            value={formatCurrency(currentMonthData?.totalMonthlyRevenue || 0)}
            icon={DollarSign}
            change={currentMonthData?.revenueChangePercent}
            isLoading={isLoading}
          />

          <StatCard
            title="Số đơn hàng"
            color="green"
            value={currentMonthData?.totalOrders?.toLocaleString() || '0'}
            icon={ShoppingCart}
            change={currentMonthData?.orderCountChangePercent}
            isLoading={isLoading}
          />

          <StatCard
            title="Doanh thu năm"
            color="purple"
            value={formatCurrency(currentMonthData?.totalYearlyRevenue || 0)}
            icon={BarChart2}
            isLoading={isLoading}
          />

          <StatCard
            title="Sản phẩm hết hạn"
            value={currentMonthData?.expiringLowStockProducts?.length?.toLocaleString() || '0'}
            icon={AlertTriangle}
            color="red"
            change={0}
            isLoading={isLoading}
            onClick={() => currentMonthData?.expiringLowStockProducts && setShowExpiring(true)}
          />
        </div>

        {/* Enhanced Charts Grid */}
        <div className="grid grid-cols-1 xl:grid-cols-2 gap-8">
          {/* Revenue Chart */}
          <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg p-6 border border-gray-200/50">
            <div className="flex items-center justify-between mb-6">
              <div>
                <h2 className="text-xl font-bold text-gray-800">Doanh thu theo tháng</h2>
                <p className="text-gray-600 text-sm mt-1">Xu hướng doanh thu {rangeMonths} tháng gần nhất</p>
              </div>
              <div className="p-2 bg-gradient-to-r from-blue-500 to-purple-500 rounded-lg">
                <BarChart2 className="w-5 h-5 text-white" />
              </div>
            </div>
            <ResponsiveContainer width="100%" height={350}>
              <AreaChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 50 }}>
                <defs>
                  <linearGradient id="colorRevenue" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#3B82F6" stopOpacity={0.8} />
                    <stop offset="95%" stopColor="#3B82F6" stopOpacity={0.1} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
                <XAxis 
                  dataKey="name" 
                  tick={{ fontSize: 12, fill: '#6B7280' }} 
                  angle={-25} 
                  textAnchor="end" 
                  interval={0}
                  stroke="#9CA3AF"
                />
                <YAxis 
                  tickFormatter={(value: number) => value === 0 ? '' : formatShort(value)} 
                  tick={{ fontSize: 12, fill: '#6B7280' }}
                  stroke="#9CA3AF"
                />
                <Tooltip 
                  formatter={(value) => [formatCurrency(Number(value)), 'Doanh thu']} 
                  labelFormatter={(v) => v}
                  contentStyle={{
                    backgroundColor: 'rgba(255, 255, 255, 0.95)',
                    border: 'none',
                    borderRadius: '12px',
                    boxShadow: '0 10px 25px rgba(0, 0, 0, 0.1)',
                  }}
                />
                <Area
                  type="monotone"
                  dataKey="Revenue"
                  stroke="#3B82F6"
                  fillOpacity={1}
                  fill="url(#colorRevenue)"
                  strokeWidth={3}
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>

          {/* Branch Revenue Pie Chart */}
          <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg p-6 border border-gray-200/50">
            <div className="flex items-center justify-between mb-6">
              <div>
                <h2 className="text-xl font-bold text-gray-800">Doanh thu theo chi nhánh</h2>
                <p className="text-gray-600 text-sm mt-1">Phân bố doanh thu các chi nhánh</p>
              </div>
              <div className="p-2 bg-gradient-to-r from-emerald-500 to-blue-500 rounded-lg">
                <Grid className="w-5 h-5 text-white" />
              </div>
            </div>
            {branchLoading ? (
              <div className="h-80 flex items-center justify-center">
                <div className="relative">
                  <LoaderCircle className="animate-spin w-8 h-8 text-blue-500" />
                  <div className="absolute inset-0 rounded-full border-2 border-blue-200 animate-pulse"></div>
                </div>
              </div>
            ) : (
              <ResponsiveContainer width="100%" height={350}>
                <PieChart>
                  <Tooltip 
                    formatter={(value) => [formatCurrency(Number(value)), 'Doanh thu']}
                    contentStyle={{
                      backgroundColor: 'rgba(255, 255, 255, 0.95)',
                      border: 'none',
                      borderRadius: '12px',
                      boxShadow: '0 10px 25px rgba(0, 0, 0, 0.1)',
                    }}
                  />
                  <Legend 
                    wrapperStyle={{ paddingTop: '20px' }}
                    iconType="circle"
                  />
                  <Pie
                    data={branchChartData}
                    dataKey="revenue"
                    nameKey="name"
                    cx="50%"
                    cy="50%"
                    outerRadius={120}
                    paddingAngle={1}
                    label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(1)}%`}
                  >
                    {branchChartData.map((entry, index) => (
                      <Cell 
                        key={`cell-${index}`} 
                        fill={BRANCH_COLORS[index % BRANCH_COLORS.length]}
                        stroke="rgba(255, 255, 255, 0.8)"
                        strokeWidth={2}
                      />
                    ))}
                  </Pie>
                </PieChart>
              </ResponsiveContainer>
            )}
          </div>
        </div>

        {/* Top Selling Products & Top Customers side-by-side */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg p-6 border border-gray-200/50">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h2 className="text-xl font-bold text-gray-800">Top sản phẩm bán chạy</h2>
              <p className="text-gray-600 text-sm mt-1">Danh sách {topLimit} sản phẩm bán chạy nhất</p>
            </div>
            <div className="p-2 bg-gradient-to-r from-orange-500 to-red-500 rounded-lg">
              <Package className="w-5 h-5 text-white" />
            </div>
          </div>
          
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {(currentMonthData?.topSellingProducts?.slice(0, topLimit) || []).map((p, idx) => (
              <div key={p.productId} onClick={() => setProductModalId(p.productId)} className="cursor-pointer group flex items-center p-4 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl hover:from-blue-50 hover:to-indigo-50 transition-all duration-300 border border-gray-200/50 hover:border-blue-200 hover:shadow-md">
                <div className={`flex-shrink-0 w-10 h-10 rounded-full flex items-center justify-center text-white font-bold text-sm mr-4 ${
                  idx === 0 ? 'bg-gradient-to-r from-yellow-400 to-orange-500' :
                  idx === 1 ? 'bg-gradient-to-r from-gray-300 to-gray-500' :
                  idx === 2 ? 'bg-gradient-to-r from-orange-400 to-red-500' :
                  'bg-gradient-to-r from-blue-400 to-purple-500'
                }`}>
                  {idx + 1}
                </div>
                <div className="flex-1 min-w-0">
                  <p className="font-medium text-gray-800 truncate group-hover:text-blue-800 transition-colors">
                    {p.name}
                  </p>
                  <p className="text-sm text-gray-600">
                    Đã bán: <span className="font-semibold text-blue-600">{p.totalQuantity.toLocaleString()}</span>
                  </p>
                </div>
              </div>
            ))}
            
            {(!currentMonthData?.topSellingProducts?.length) && (
              <div className="col-span-full text-center py-12">
                <Package className="w-16 h-16 text-gray-300 mx-auto mb-4" />
                <p className="text-gray-500 text-lg">Không có dữ liệu sản phẩm</p>
              </div>
            )}
          </div>
        </div>
        <div className="bg-white/80 backdrop-blur-sm rounded-2xl shadow-lg p-6 border border-gray-200/50">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h2 className="text-xl font-bold text-gray-800">Top khách hàng chi tiêu nhiều nhất</h2>
              <p className="text-gray-600 text-sm mt-1">Danh sách {topLimit} khách hàng chi tiêu cao</p>
            </div>
            <div className="p-2 bg-gradient-to-r from-emerald-500 to-teal-500 rounded-lg">
              <Users className="w-5 h-5 text-white" />
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {(topCustomers.slice(0, topLimit)).map((c, idx) => (
              <div key={c.customerId} className="group flex items-center p-4 bg-gradient-to-r from-gray-50 to-gray-100 rounded-xl hover:from-teal-50 hover:to-emerald-50 transition-all duration-300 border border-gray-200/50 hover:border-emerald-200 hover:shadow-md">
                <div className={`flex-shrink-0 w-10 h-10 rounded-full flex items-center justify-center text-white font-bold text-sm mr-4 ${
                  idx === 0 ? 'bg-gradient-to-r from-yellow-400 to-orange-500' :
                  idx === 1 ? 'bg-gradient-to-r from-gray-300 to-gray-500' :
                  idx === 2 ? 'bg-gradient-to-r from-orange-400 to-red-500' :
                  'bg-gradient-to-r from-emerald-400 to-teal-500'
                }`}>
                  {idx + 1}
                </div>
                <div className="flex-1 min-w-0">
                  <p className="font-medium text-gray-800 truncate group-hover:text-emerald-800 transition-colors">
                    {c.name}
                  </p>
                  <p className="text-sm text-gray-600">
                    Tổng chi: <span className="font-semibold text-emerald-600">{formatCurrency(c.totalSpent)}</span>
                  </p>
                </div>
              </div>
            ))}

            {(!topCustomers.length) && (
              <div className="col-span-full text-center py-12">
                <Users className="w-16 h-16 text-gray-300 mx-auto mb-4" />
                <p className="text-gray-500 text-lg">Không có dữ liệu khách hàng</p>
              </div>
            )}
          </div>
        </div>
      </div>
      {/* Product statistics modal */}
      {productModalId !== null && (
        <ProductStatsModal productId={productModalId} branchId={selectedBranchId} onClose={() => setProductModalId(null)} />
      )}
      </div>
    </div>
  );
};

// --- Modal hiển thị thống kê sản phẩm ---
interface ProductStatsModalProps {
  productId: number;
  branchId?: number;
  onClose: () => void;
}

const ProductStatsModal: React.FC<ProductStatsModalProps> = ({ productId, branchId, onClose }) => {
  const { data, isLoading } = useQuery({
    queryKey: ['productStats', productId, branchId],
    queryFn: () => productService.getProductSalesStatistics(productId, { branchId, months: 6 }),
  });

  // Ensure chart has proper labels even if API omits them
  const chartData = React.useMemo(() => {
    if (!data?.monthlySales) return [];
    return data.monthlySales.map((m: any) => ({
      ...m,
      monthLabel: m.monthLabel ?? `${m.month}/${String(m.year).slice(2)}`,
    }));
  }, [data]);

  if (isLoading) {
    return (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
        <div className="bg-white p-8 rounded-xl shadow-lg">
          <LoaderCircle className="animate-spin w-8 h-8 text-blue-500" />
        </div>
      </div>
    );
  }

  if (!data) return null;

  const result = data;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40">
      <div className="bg-white w-full max-w-3xl rounded-xl p-6 shadow-lg">
        <div className="flex justify-between items-center mb-4">
          <h3 className="text-lg font-semibold">Thống kê sản phẩm #{productId}</h3>
          <button onClick={onClose}><X className="w-5 h-5 text-gray-600"/></button>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
          <StatCard title="Tồn kho" value={result.stockNumber?.toLocaleString() || '0'} icon={Package} isLoading={false} />
          <StatCard title="Mức tồn kho" value={String(result.stockLevel || 'N/A')} icon={AlertCircle} isLoading={false} />
          <StatCard title="Ngày hết hạn sớm nhất" value={result.earliestExpDate ? new Date(result.earliestExpDate).toLocaleDateString('vi-VN') : 'N/A'} icon={Calendar} isLoading={false} />
        </div>

        <ResponsiveContainer width="100%" height={300}>
          <BarChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 20 }}>
            <CartesianGrid strokeDasharray="3 3" />
            <XAxis dataKey="monthLabel" />
            <YAxis />
            <Tooltip />
            <Bar dataKey="quantity" fill="#3B82F6" />
          </BarChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
};

export default DashboardHomeNew;