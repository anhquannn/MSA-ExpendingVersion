import React, { useState, useEffect, useMemo, useCallback } from 'react';
import { FiSearch, FiFilter, FiCalendar, FiChevronDown, FiX } from 'react-icons/fi';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { paymentService } from '../../services/paymentService';
import { orderDetailService, OrderDetail } from '../../services/orderDetailService';
import { orderService, OrderFilterRequest, OrderStatus, SimpleOrder} from '../../services/orderService';
import { shipmentService } from '../../services/shipmentService';

// Helper function to format date
const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
};

// Helper function to get status label
const getStatusLabel = (status: OrderStatus): string => {
  const statusMap: Record<OrderStatus, string> = {
    [OrderStatus.PENDING]: 'Chờ xử lý',
    [OrderStatus.PAYING]: 'Đang thanh toán',
    [OrderStatus.PAID]: 'Đã thanh toán',
    [OrderStatus.DELIVERING]: 'Đang giao',
    [OrderStatus.SHIPPED]: 'Đã vận chuyển',
    [OrderStatus.CANCELLING]: 'Đang hủy',
    [OrderStatus.CANCELLED]: 'Đã hủy',
    [OrderStatus.COMPLETED]: 'Hoàn thành',
    [OrderStatus.FAILED]: 'Thất bại',
  };
  return statusMap[status] || status;
};

// Helper function to get status CSS classes
const getStatusClasses = (status: OrderStatus): string => {
  const statusClasses: Record<OrderStatus, string> = {
    [OrderStatus.PENDING]: 'bg-yellow-100 text-yellow-800',
    [OrderStatus.PAYING]: 'bg-blue-100 text-blue-800',
    [OrderStatus.PAID]: 'bg-indigo-100 text-indigo-800',
    [OrderStatus.DELIVERING]: 'bg-purple-100 text-purple-800',
    [OrderStatus.SHIPPED]: 'bg-purple-100 text-purple-800',
    [OrderStatus.CANCELLING]: 'bg-orange-100 text-orange-800',
    [OrderStatus.CANCELLED]: 'bg-red-100 text-red-800',
    [OrderStatus.COMPLETED]: 'bg-green-100 text-green-800',
    [OrderStatus.FAILED]: 'bg-gray-100 text-gray-800',
  };
  return statusClasses[status] || 'bg-gray-100 text-gray-800';
};

// Helper function to check if status can be updated
const canUpdateStatus = (status: OrderStatus): boolean => {
  return [
    OrderStatus.PENDING,
    OrderStatus.PAYING,
    OrderStatus.PAID,
    OrderStatus.DELIVERING,
    OrderStatus.SHIPPED,
  ].includes(status);
};

// Helper function to get next status
const getNextStatus = (currentStatus: OrderStatus): OrderStatus | null => {
  const statusFlow: Record<OrderStatus, OrderStatus | null> = {
    [OrderStatus.PENDING]: OrderStatus.PAYING,
    [OrderStatus.PAYING]: OrderStatus.PAID,
    [OrderStatus.PAID]: OrderStatus.DELIVERING,
    [OrderStatus.DELIVERING]: OrderStatus.SHIPPED,
    [OrderStatus.SHIPPED]: OrderStatus.COMPLETED,
    [OrderStatus.CANCELLING]: OrderStatus.CANCELLED,
    [OrderStatus.CANCELLED]: null,
    [OrderStatus.COMPLETED]: null,
    [OrderStatus.FAILED]: null,
  };
  return statusFlow[currentStatus] || null;
};

interface PaginationState {
  currentPage: number;
  pageSize: number;
  totalItems: number;
  totalPages: number;
}

// Định nghĩa các trạng thái đơn hàng
const ORDER_STATUSES = [
  { value: 'all', label: 'Tất cả' },
  { value: OrderStatus.PENDING, label: 'Chờ xử lý' },
  { value: OrderStatus.PAYING, label: 'Đang thanh toán' },
  { value: OrderStatus.PAID, label: 'Đã thanh toán' },
  { value: OrderStatus.DELIVERING, label: 'Đang giao' },
  { value: OrderStatus.SHIPPED, label: 'Đã vận chuyển' },
  { value: OrderStatus.CANCELLING, label: 'Đang hủy' },
  { value: OrderStatus.CANCELLED, label: 'Đã hủy' },
  { value: OrderStatus.COMPLETED, label: 'Hoàn thành' },
  { value: OrderStatus.FAILED, label: 'Thất bại' },
];

// @ts-ignore
const SearchIcon = (props: React.SVGProps<SVGSVGElement>) => <FiSearch {...props} />;

const OrdersPage = () => {
  // Modal for order details
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [detailLoading, setDetailLoading] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState<SimpleOrder | null>(null);
  const [orderDetails, setOrderDetails] = useState<OrderDetail[]>([]);

  // Modal for editing status
  const [showStatusModal, setShowStatusModal] = useState(false);
  const [orderToEdit, setOrderToEdit] = useState<SimpleOrder | null>(null);
  const [newStatus, setNewStatus] = useState<OrderStatus>(OrderStatus.PENDING);

  // State for orders and loading
  const [orders, setOrders] = useState<SimpleOrder[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  
  // State for filters
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [phoneNumber, setPhoneNumber] = useState<string>('');
  const [statusFilter, setStatusFilter] = useState<OrderStatus | 'all'>('all');
  const [dateFilter, setDateFilter] = useState<{
    startDate: Date | null;
    endDate: Date | null;
  }>({ startDate: null, endDate: null });
  
  // State for pagination
  const [pagination, setPagination] = useState<PaginationState>({
    currentPage: 1,
    pageSize: 10,
    totalItems: 0,
    totalPages: 1,
  });
  
  // State for cancel order dialog
  const [showCancelDialog, setShowCancelDialog] = useState<boolean>(false);
  const [orderToCancel, setOrderToCancel] = useState<SimpleOrder | null>(null);
  const [cancelReason, setCancelReason] = useState<string>('');
  const [isLoading, setIsLoading] = useState(false);

  // Map orderId -> payment status
  const [paymentStatuses, setPaymentStatuses] = useState<Record<number, OrderStatus | null>>({});

  // Calculate pagination values
  const { totalPages, totalItems, currentPage, pageSize } = pagination;

  // Format tiền tệ
  const formatCurrency = (amount: number): string => {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
    }).format(amount);
  };

    // Fetch orders from API
  // Lấy trạng thái thanh toán một cách batch
  const fetchPaymentStatuses = useCallback(async (ordersList: SimpleOrder[]) => {
    const map: Record<number, OrderStatus | null> = {};
    try {
        const mapFromApi = await paymentService.getLatestPaymentStatusesForOrders(
          ordersList.map((o) => o.orderId),
        );
        Object.assign(map, mapFromApi);
      } catch (err) {
        console.error('Could not fetch payment statuses', err);
      }
    console.log('Fetched payment status map', map);
    setPaymentStatuses(map);
  }, []);

  // Fetch orders from API
  const fetchOrders = useCallback(async () => {
    try {
      setIsLoading(true);
      const response = await orderService.getOrdersWithPaging({
        status: statusFilter === 'all' ? undefined : statusFilter,
        fromDate: dateFilter.startDate,
        toDate: dateFilter.endDate,
        phoneNumber: phoneNumber || undefined,

        page: pagination.currentPage,
        pageSize: pagination.pageSize,
      });

      const orderList = response.content || [];
      setOrders(orderList);
      fetchPaymentStatuses(orderList);
      setPagination(prev => ({
        ...prev,
        totalPages: response.totalPages || 1,
      }));
    } catch (error) {
      console.error('Error fetching orders:', error);
      setOrders([]);
      // TODO: Show error toast
    } finally {
      setIsLoading(false);
    }
  }, [statusFilter, dateFilter, searchTerm, pagination.currentPage, pagination.pageSize]);

  // Fetch orders when filters or pagination changes
  useEffect(() => {
    fetchOrders();
  }, [fetchOrders]);

  // Format ngày tháng
  const formatDate = (dateString: string | Date): string => {
    const date = new Date(dateString);
    return date.toLocaleDateString('vi-VN', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

    // Hiển thị nhãn tình trạng thanh toán
  const renderPaymentStatus = (status?: OrderStatus | null | undefined): string => {
    switch (status) {
      case OrderStatus.PAID:
        return 'Đã thanh toán';
      case OrderStatus.PAYING:
        return 'Đang thanh toán';
      case OrderStatus.FAILED:
        return 'Thanh toán thất bại';
      case undefined:
      default:
        return 'Chưa thanh toán';
    }
  };

  // Lấy dữ liệu đơn hàng
  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setLoading(true);
        const response = await orderService.getOrdersWithPaging({
          page: pagination.currentPage,
          pageSize: pagination.pageSize,
          status: statusFilter !== 'all' ? statusFilter : undefined,
          fromDate: dateFilter.startDate ? dateFilter.startDate.toISOString() : undefined,
          toDate: dateFilter.endDate ? dateFilter.endDate.toISOString() : undefined,
          phoneNumber: phoneNumber || undefined,
        });

        setOrders(response.content || []);
        setPagination(prev => ({
          ...prev,
          totalPages: response.totalPages || 1,
        }));
      } catch (error) {
        console.error('Error fetching orders:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, [pagination.currentPage, pagination.pageSize, statusFilter, dateFilter, searchTerm]);

  // Hàm đóng dialog mà không hủy
  const closeCancelDialog = () => {
    setOrderToCancel(null);
    setShowCancelDialog(false);
  };

  // Handle page change with boundary checks
  const handlePageChange = useCallback((page: number) => {
    if (page < 1 || page > pagination.totalPages) return;

    setPagination(prev => ({
      ...prev,
      currentPage: page
    }));
    window.scrollTo(0, 0);
  }, [pagination.totalPages]);

  // Generate page numbers for pagination
  const pageNumbers = useMemo(() => {
    const totalPages = pagination.totalPages;
    const currentPage = pagination.currentPage;
    const pages = [];

    // Always show first page
    pages.push(1);

    // Show ellipsis if needed
    if (currentPage > 3) {
      pages.push(-1); // -1 represents ellipsis
    }

    // Show current page and adjacent pages
    for (let i = Math.max(2, currentPage - 1); i <= Math.min(totalPages - 1, currentPage + 1); i++) {
      if (i > 1 && i < totalPages) {
        pages.push(i);
      }
    }

    // Show ellipsis if needed
    if (currentPage < totalPages - 2) {
      pages.push(-1); // -1 represents ellipsis
    }

    // Always show last page if there is more than one page
    if (totalPages > 1) {
      pages.push(totalPages);
    }

    return pages;
  }, [pagination.currentPage, pagination.totalPages]);

  // Handle page size change with validation
  const handlePageSizeChange = useCallback((e: React.ChangeEvent<HTMLSelectElement>) => {
    const newSize = Math.max(1, Math.min(100, Number(e.target.value) || 10));
    setPagination(prev => ({
      ...prev,
      pageSize: newSize,
      currentPage: 1, // Reset to first page when changing page size
    }));
  }, []);

  // Handle status filter change
  const handleStatusChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const value = e.target.value as OrderStatus | 'all';
    setStatusFilter(value);
    setPagination(prev => ({ ...prev, currentPage: 1 }));
  };

  // Handle search
  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    setPagination(prev => ({
      ...prev,
      currentPage: 1,
    }));
  };

  // Open order detail modal
  const openDetailModal = async (order: SimpleOrder) => {
    setSelectedOrder(order);
    setShowDetailModal(true);
    setDetailLoading(true);
    try {
      const details = await orderDetailService.getByOrderId(order.orderId);
      setOrderDetails(details);
    } catch (error) {
      console.error('Error fetching order details:', error);
    } finally {
      setDetailLoading(false);
    }
  };

  // Quick next-status update (retain existing behaviour)
  const handleQuickUpdateStatus = async (orderId: number, currentStatus: OrderStatus) => {
    const nextStatus = getNextStatus(currentStatus);
    if (!nextStatus) return;

    try {
      setIsLoading(true);
      await orderService.updateOrderStatus(orderId, nextStatus);
      await fetchOrders();
      // TODO: Show success toast
    } catch (error) {
      console.error('Error updating order status:', error);
      // TODO: Show error toast
    } finally {
      setIsLoading(false);
    }
  };

  // Open modal to edit any status
  const openStatusModal = (order: SimpleOrder) => {
    setOrderToEdit(order);
    setNewStatus(order.status);
    setShowStatusModal(true);
  };

  const handleConfirmStatusChange = async () => {
    if (!orderToEdit) return;
    try {
      setIsLoading(true);
      await orderService.updateOrderStatus(orderToEdit.orderId, newStatus);
      await fetchOrders();
      setShowStatusModal(false);
    } catch (error) {
      console.error('Error updating status:', error);
    } finally {
      setIsLoading(false);
    }
  };

  // Handle cancel order
    const handleConfirmCancelOrder = async () => {
    if (!orderToCancel) return;
    if (!cancelReason.trim()) {
      alert('Vui lòng nhập lý do hủy');
      return;
    }
    try {
      setIsLoading(true);
      await orderService.createCancelOrder(orderToCancel.orderId, cancelReason.trim());
      await fetchOrders();
      // TODO: Show success toast
    } catch (error) {
      console.error('Error cancelling order:', error);
      // TODO: Show error toast
    } finally {
      setIsLoading(false);
      setOrderToCancel(null);
      setShowCancelDialog(false);
      setCancelReason('');
    }
  };

  return (
    <div className="bg-white p-6 rounded-lg shadow-md">
      <h2 className="text-2xl font-semibold text-gray-700 mb-4">Quản Lý Đơn Hàng</h2>
      <p className="text-gray-600 mb-6">Danh sách các đơn hàng gần đây.</p>

      {/* --- Phần Tìm kiếm và Lọc --- */}
      <div className="mb-6 p-4 border border-gray-200 rounded-lg bg-gray-50">
        <h3 className="text-lg font-semibold text-gray-700 mb-3">Tìm kiếm & Lọc</h3>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {/* Tìm kiếm theo mã đơn hàng / khách hàng */}
          <div>
            <label htmlFor="search-order" className="block text-sm font-medium text-gray-700 mb-1">Tìm kiếm đơn hàng:</label>
            <input
              type="text"
              id="search-order"
              placeholder="Mã đơn hàng hoặc Tên khách hàng"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>

          {/* Lọc theo trạng thái */}
          <div>
            <label htmlFor="filter-status" className="block text-sm font-medium text-gray-700 mb-1">Trạng thái:</label>
            <select
              id="filter-status"
              value={statusFilter}
              onChange={handleStatusChange}
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              <option value="all">Tất cả trạng thái</option>
              {ORDER_STATUSES.map(status => (
                <option key={status.value} value={status.value}>{status.label}</option>
              ))}
            </select>
          </div>

          {/* Lọc theo số điện thoại */}
          <div>
            <label htmlFor="phone-number" className="block text-sm font-medium text-gray-700 mb-1">SĐT Khách hàng:</label>
            <input
              type="text"
              id="phone-number"
              placeholder="Số điện thoại"
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
            />
          </div>

          {/* Lọc theo thởi gian đặt hàng */}
          <div className="md:col-span-2 lg:col-span-3 grid grid-cols-2 gap-px">
            <div>
              <label htmlFor="start-date" className="block text-sm font-medium text-gray-700 mb-1">Ngày đặt từ:</label>
              <DatePicker
                selected={dateFilter.startDate}
                onChange={(date: Date | null) => setDateFilter(prev => ({ ...prev, startDate: date }))}
                selectsStart
                startDate={dateFilter.startDate}
                endDate={dateFilter.endDate}
                placeholderText="Chọn ngày"
                dateFormat="dd/MM/yyyy"
                className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
              />
            </div>
            <div>
              <label htmlFor="end-date" className="block text-sm font-medium text-gray-700 mb-1">Ngày đặt đến:</label>
              <DatePicker
                selected={dateFilter.endDate}
                onChange={(date: Date | null) => setDateFilter(prev => ({ ...prev, endDate: date }))}
                selectsEnd
                startDate={dateFilter.startDate}
                endDate={dateFilter.endDate}
                minDate={dateFilter.startDate ?? undefined}
                placeholderText="Chọn ngày"
                dateFormat="dd/MM/yyyy"
                className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
              />
            </div>
          </div>
        </div>
        {/* Nút reset filter */}
        <div className="mt-4 flex justify-end">
          <button
            onClick={() => {
              setSearchTerm('');
              setStatusFilter('all');
              setDateFilter({ startDate: null, endDate: null });
            }}
            className="px-4 py-2 bg-gray-400 text-white rounded-md hover:bg-gray-500 transition duration-200"
          >
            Reset Lọc
          </button>
        </div>
      </div>

      {/* Tùy chọn số lượng đơn hàng trên mỗi trang */}
      <div className="mb-4 flex justify-end items-center">
        <label htmlFor="orders-per-page" className="text-gray-700 mr-2">Đơn hàng mỗi trang:</label>
        <select
          id="orders-per-page"
          value={pagination.pageSize}
          onChange={handlePageSizeChange}
          className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
        >
          <option value={5}>5</option>
          <option value={10}>10</option>
          <option value={20}>20</option>
          <option value={50}>50</option>
        </select>
      </div>

      {/* --- Bảng danh sách đơn hàng --- */}
      <div className="overflow-x-auto">
        {loading ? (
          <div className="flex justify-center items-center h-64">
            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-green-500"></div>
          </div>
        ) : (
          <>
            <table className="min-w-full bg-white border border-gray-200">
              <thead>
                <tr className="bg-gray-100">
                  <th className="py-3 px-4 border-b text-left text-sm font-medium text-gray-700">Mã đơn hàng</th>
                  <th className="py-3 px-4 border-b text-left text-sm font-medium text-gray-700">Chi nhánh</th>
                  <th className="py-3 px-4 border-b text-left text-sm font-medium text-gray-700">Ngày đặt</th>
                  <th className="py-3 px-4 border-b text-left text-sm font-medium text-gray-700">Tổng tiền</th>
                  <th className="py-3 px-4 border-b text-left text-sm font-medium text-gray-700">Thanh toán</th>
                  <th className="py-3 px-4 border-b text-left text-sm font-medium text-gray-700">Trạng thái</th>
                  <th className="py-3 px-4 border-b text-right text-sm font-medium text-gray-700">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                {orders.length > 0 ? (
                  orders.map((order) => {
                    const nextStatus = getNextStatus(order.status);
                    return (
                      <tr key={order.orderId} className="hover:bg-gray-50">
                        <td className="py-3 px-4 border-b">#{order.orderId}</td>
                        <td className="py-3 px-4 border-b">{(order as any).branch?.name || order.branchName || 'N/A'}</td>
                        <td className="py-3 px-4 border-b">{formatDate(order.orderDate)}</td>
                        <td className="py-3 px-4 border-b">{formatCurrency(order.grandTotal)}</td>
                         <td className="py-3 px-4 border-b">{
                             renderPaymentStatus(paymentStatuses[order.orderId] ?? undefined)
                           }</td>
                        <td className="py-3 px-4 border-b">
                          <span className={`px-2 py-1 text-xs font-medium rounded-full ${getStatusClasses(order.status)}`}>
                            {getStatusLabel(order.status)}
                          </span>
                        </td>
                        <td className="py-3 px-4 border-b text-right">
                          <div className="flex space-x-2 group">
                              <button
                                onClick={() => openStatusModal(order)}
                                className="px-2 py-1 text-xs text-white bg-green-600 rounded hover:bg-green-700"
                              >
                                Sửa
                              </button>
                            {canUpdateStatus(order.status) && (
                              <button
                                onClick={() => handleQuickUpdateStatus(order.orderId, order.status)}
                                className="px-2 py-1 text-xs text-white bg-blue-600 rounded hover:bg-blue-700 mr-2"
                              >
                                {getStatusLabel(getNextStatus(order.status)!)}
                              </button>
                            )}
                            {canUpdateStatus(order.status) && (
                              <button
                                onClick={() => {
                                  setOrderToCancel(order);
                                  setCancelReason('');
                                  setShowCancelDialog(true);
                                }}
                                className="px-2 py-1 text-xs text-white bg-red-600 rounded hover:bg-red-700 mr-2"
                              >
                                Hủy đơn
                              </button>
                            )}
                            <button
                              className="p-1 text-gray-500 hover:text-gray-700"
                              onClick={() => openDetailModal(order)}
                            >
                              <SearchIcon className="w-4 h-4" />
                            </button>
                            {/* Hidden webhook test button */}
                            {(() => {
                              const shipmentCode = (order as any).shipmentCode || (order as any).deliveryInfo?.shipmentCode;
                              if (!shipmentCode) return null;
                              return (
                                <button
                                  onClick={() => shipmentService.mockWebhook(shipmentCode, 913).then(()=>fetchOrders())}
                                  title="Test Webhook"
                                  className="px-1 text-xs text-purple-600 border border-purple-600 rounded hidden group-hover:inline-block"
                                >
                                  Webhook
                                </button>
                              );
                            })()}
                          </div>
                        </td>
                      </tr>
                    );
                  })
                ) : (
                  <tr>
                    <td colSpan={6} className="py-8 text-center text-gray-500">
                      Không có đơn hàng nào được tìm thấy.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>

            {/* Status Edit Modal */}
            {showStatusModal && orderToEdit && (
              <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
                <div className="bg-white rounded-lg p-6 w-80">
                  <h3 className="text-lg font-semibold mb-4">Cập nhật trạng thái đơn #{orderToEdit.orderId}</h3>
                  <select
                    value={newStatus}
                    onChange={(e) => setNewStatus(e.target.value as OrderStatus)}
                    className="w-full p-2 border rounded-md mb-4"
                  >
                    {Object.values(OrderStatus).map((status) => (
                      <option key={status} value={status}>{getStatusLabel(status as OrderStatus)}</option>
                    ))}
                  </select>
                  <div className="flex justify-end gap-2">
                    <button
                      onClick={() => setShowStatusModal(false)}
                      className="px-3 py-1 rounded-md border"
                    >
                      Hủy
                    </button>
                    <button
                      onClick={handleConfirmStatusChange}
                      className="px-3 py-1 rounded-md bg-blue-600 text-white"
                    >
                      Lưu
                    </button>
                  </div>
                </div>
              </div>
            )}

            {/* Order Detail Modal */}
            {showDetailModal && selectedOrder && (
              <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
                <div className="bg-white rounded-lg p-6 w-full max-w-2xl max-h-[80vh] overflow-y-auto">
                  <h3 className="text-xl font-semibold mb-4">Chi tiết đơn hàng #{selectedOrder.orderId}</h3>
                  {/* User info */}
                  <div className="mb-4 grid grid-cols-1 sm:grid-cols-3 gap-4 text-sm">
                    <div><span className="font-medium">Tên KH:</span> { (selectedOrder as any).user?.fullName || (selectedOrder as any).fullName || 'N/A' }</div>
                    <div><span className="font-medium">Email:</span> { (selectedOrder as any).user?.email || (selectedOrder as any).email || 'N/A' }</div>
                    <div><span className="font-medium">SĐT:</span> { (selectedOrder as any).user?.phoneNumber || (selectedOrder as any).phoneNumber || 'N/A' }</div>
                  </div>

                  {/* Order details list */}
                  {detailLoading ? (
                    <div className="flex justify-center items-center py-8">
                      <div className="animate-spin w-6 h-6 border-2 border-t-transparent border-green-600 rounded-full" />
                    </div>
                  ) : orderDetails.length > 0 ? (
                    <table className="min-w-full bg-white border border-gray-200 text-sm">
                      <thead className="bg-gray-100">
                        <tr>
                          <th className="py-2 px-3 border-b text-left">Sản phẩm</th>
                          <th className="py-2 px-3 border-b text-left">Số lượng</th>
                        </tr>
                      </thead>
                      <tbody>
                        {orderDetails.map((d) => (
                          <tr key={d.orderDetailId}>
                            <td className="py-2 px-3 border-b">{d.name}</td>
                            <td className="py-2 px-3 border-b">{d.quantity}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  ) : (
                    <p className="text-gray-500 text-center py-4">Không có chi tiết đơn hàng.</p>
                  )}

                  <div className="flex justify-end mt-4">
                    <button
                      onClick={() => setShowDetailModal(false)}
                      className="px-4 py-2 bg-blue-600 text-white rounded-md"
                    >
                      Đóng
                    </button>
                  </div>
                </div>
              </div>
            )}

            {/* Cancel Order Dialog */}
            {showCancelDialog && orderToCancel && (
              <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
                <div className="bg-white rounded-lg p-6 w-96">
                  <h3 className="text-lg font-semibold mb-4">Hủy đơn hàng #{orderToCancel.orderId}</h3>
                  <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-1">Lý do hủy:</label>
                    <textarea
                      value={cancelReason}
                      onChange={(e) => setCancelReason(e.target.value)}
                      className="w-full p-2 border rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
                      rows={3}
                      placeholder="Nhập lý do hủy..."
                    />
                  </div>
                  <div className="flex justify-end gap-2">
                    <button
                      onClick={() => {
                        setShowCancelDialog(false);
                        setOrderToCancel(null);
                      }}
                      className="px-3 py-1 rounded-md border"
                    >
                      Đóng
                    </button>
                    <button
                      onClick={handleConfirmCancelOrder}
                      className="px-3 py-1 rounded-md bg-red-600 text-white"
                    >
                      Xác nhận hủy
                    </button>
                  </div>
                </div>
              </div>
            )}

            {/* Phân trang */}
            {pagination.totalPages > 1 && (
              <div className="flex items-center justify-between mt-4">
                <div className="text-sm text-gray-600">
                  Hiển thị <span className="font-medium">{(currentPage - 1) * pageSize + 1}</span> đến{' '}
                  <span className="font-medium">{Math.min(currentPage * pageSize, totalItems)}</span> trong tổng số{' '}
                  <span className="font-medium">{totalItems}</span> đơn hàng
                </div>
                <div className="flex items-center space-x-2">
                  <button
                    onClick={() => handlePageChange(1)}
                    disabled={currentPage === 1}
                    className="px-3 py-1 border rounded-md disabled:opacity-50"
                  >
                    Đầu
                  </button>
                  <button
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 1}
                    className="px-3 py-1 border rounded-md disabled:opacity-50"
                  >
                    Trước
                  </button>

                  {Array.from({ length: totalPages }, (_, i) => i + 1).map((page) => (
                    <button
                      key={page}
                      onClick={() => handlePageChange(page)}
                      className={`px-3 py-1 border rounded-md ${
                        page === currentPage ? 'bg-blue-500 text-white' : ''
                      }`}
                    >
                      {page}
                    </button>
                  ))}

                  <button
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage === totalPages}
                    className="px-3 py-1 border rounded-md disabled:opacity-50"
                  >
                    Tiếp
                  </button>
                  <button
                    onClick={() => handlePageChange(totalPages)}
                    disabled={currentPage === totalPages}
                    className="px-3 py-1 border rounded-md disabled:opacity-50"
                  >
                    Cuối
                  </button>
                </div>
                <div className="flex items-center">
                  <span className="mr-2 text-sm text-gray-600">Số dòng mỗi trang:</span>
                  <select
                    value={pageSize}
                    onChange={handlePageSizeChange}
                    className="p-1 border rounded-md"
                  >
                    <option value={5}>5</option>
                    <option value={10}>10</option>
                    <option value={20}>20</option>
                    <option value={50}>50</option>
                  </select>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
    );
};

export default OrdersPage;