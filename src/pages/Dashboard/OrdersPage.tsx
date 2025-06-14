import React, { useState, useMemo } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

// Định nghĩa các trạng thái đơn hàng
const ORDER_STATUSES = [
  'Chờ xử lý',
  'Xử lý thành công',
  'Đang vận chuyển',
  'Đã hoàn thành',
  'Đã hủy'
];

// Định nghĩa kiểu dữ liệu cho Order (để dễ quản lý)
interface Order {
  id: string;
  customer: string;
  total: string; // Giữ nguyên string cho mock data, trong thực tế nên là number
  status: string;
  date: string; // Định dạng 'YYYY-MM-DD' để dễ so sánh
}

// Dữ liệu giả cho đơn hàng
const initialOrders: Order[] = [
  { id: 'ORD001', customer: 'Nguyễn Văn A', total: '150,000 VNĐ', status: 'Đã hoàn thành', date: '2025-06-14' },
  { id: 'ORD002', customer: 'Trần Thị B', total: '250,000 VNĐ', status: 'Chờ xử lý', date: '2025-06-14' },
  { id: 'ORD003', customer: 'Lê Văn C', total: '80,000 VNĐ', status: 'Đã hủy', date: '2025-06-13' },
  { id: 'ORD004', customer: 'Phạm Thị D', total: '320,000 VNĐ', status: 'Đang vận chuyển', date: '2025-06-13' },
  { id: 'ORD005', customer: 'Võ Thị E', total: '150,000 VNĐ', status: 'Xử lý thành công', date: '2025-06-14' },
  { id: 'ORD006', customer: 'Hoàng Văn F', total: '250,000 VNĐ', status: 'Chờ xử lý', date: '2025-06-12' },
  { id: 'ORD007', customer: 'Nguyễn Thị G', total: '80,000 VNĐ', status: 'Đã hủy', date: '2025-06-12' },
  { id: 'ORD008', customer: 'Lý Văn H', total: '320,000 VNĐ', status: 'Đã hoàn thành', date: '2025-06-11' },
  { id: 'ORD009', customer: 'Đào Thị K', total: '150,000 VNĐ', status: 'Chờ xử lý', date: '2025-06-11' },
  { id: 'ORD0010', customer: 'Mai Văn L', total: '250,000 VNĐ', status: 'Đang vận chuyển', date: '2025-06-10' },
  { id: 'ORD0011', customer: 'Ngô Thị M', total: '80,000 VNĐ', status: 'Đã hủy', date: '2025-06-10' },
  { id: 'ORD0012', customer: 'Đặng Văn N', total: '320,000 VNĐ', status: 'Đã hoàn thành', date: '2025-06-09' },
  { id: 'ORD0013', customer: 'Bùi Thị P', total: '150,000 VNĐ', status: 'Chờ xử lý', date: '2025-06-09' },
  { id: 'ORD0014', customer: 'Cao Văn Q', total: '250,000 VNĐ', status: 'Đang vận chuyển', date: '2025-06-08' },
  { id: 'ORD0015', customer: 'Dương Thị R', total: '80,000 VNĐ', status: 'Đã hủy', date: '2025-06-08' },
  { id: 'ORD0016', customer: 'Hoàng Văn S', total: '320,000 VNĐ', status: 'Đã hoàn thành', date: '2025-06-07' },
  { id: 'ORD017', customer: 'Khách hàng 17', total: '100,000 VNĐ', status: 'Chờ xử lý', date: '2025-06-07' },
  { id: 'ORD018', customer: 'Khách hàng 18', total: '200,000 VNĐ', status: 'Đang vận chuyển', date: '2025-06-06' },
  { id: 'ORD019', customer: 'Khách hàng 19', total: '300,000 VNĐ', status: 'Xử lý thành công', date: '2025-06-06' },
  { id: 'ORD020', customer: 'Khách hàng 20', total: '400,000 VNĐ', status: 'Đã hoàn thành', date: '2025-06-05' },
];

const OrdersPage: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>(initialOrders);

  // --- State cho tìm kiếm và lọc ---
  const [searchTerm, setSearchTerm] = useState<string>('');
  const [filterStatus, setFilterStatus] = useState<string>('all');
  const [filterMinPrice, setFilterMinPrice] = useState<string>('');
  const [filterMaxPrice, setFilterMaxPrice] = useState<string>('');
  const [filterStartDate, setFilterStartDate] = useState<Date | null>(null);
  const [filterEndDate, setFilterEndDate] = useState<Date | null>(null);

  // --- State cho phân trang ---
  const [currentPage, setCurrentPage] = useState(1);
  const [ordersPerPage, setOrdersPerPage] = useState(10);

  // --- State mới cho Dialog xác nhận hủy ---
  const [showCancelDialog, setShowCancelDialog] = useState(false);
  const [orderToCancel, setOrderToCancel] = useState<Order | null>(null);

  const parseCurrencyToNumber = (currencyString: string): number => {
    return parseFloat(currencyString.replace(/[^0-9,-]+/g, "").replace(",", "."));
  };

  const filteredAndSearchedOrders = useMemo(() => {
    let tempOrders = [...orders];

    if (filterStatus !== 'all') {
      tempOrders = tempOrders.filter(order => order.status === filterStatus);
    }

    if (filterMinPrice) {
      const minPriceNum = parseCurrencyToNumber(filterMinPrice);
      tempOrders = tempOrders.filter(order => parseCurrencyToNumber(order.total) >= minPriceNum);
    }
    if (filterMaxPrice) {
      const maxPriceNum = parseCurrencyToNumber(filterMaxPrice);
      tempOrders = tempOrders.filter(order => parseCurrencyToNumber(order.total) <= maxPriceNum);
    }

    if (filterStartDate) {
      tempOrders = tempOrders.filter(order => {
        const orderDate = new Date(order.date);
        return orderDate >= filterStartDate;
      });
    }
    if (filterEndDate) {
      tempOrders = tempOrders.filter(order => {
        const orderDate = new Date(order.date);
        const endOfDay = new Date(filterEndDate);
        endOfDay.setHours(23, 59, 59, 999);
        return orderDate <= endOfDay;
      });
    }

    if (searchTerm) {
      const lowerCaseSearchTerm = searchTerm.toLowerCase();
      tempOrders = tempOrders.filter(order =>
        order.id.toLowerCase().includes(lowerCaseSearchTerm) ||
        order.customer.toLowerCase().includes(lowerCaseSearchTerm)
      );
    }

    return tempOrders;
  }, [orders, filterStatus, filterMinPrice, filterMaxPrice, filterStartDate, filterEndDate, searchTerm]);

  const totalPages = Math.ceil(filteredAndSearchedOrders.length / ordersPerPage);

  const currentOrders = useMemo(() => {
    const indexOfLastOrder = currentPage * ordersPerPage;
    const indexOfFirstOrder = indexOfLastOrder - ordersPerPage;
    return filteredAndSearchedOrders.slice(indexOfFirstOrder, indexOfLastOrder);
  }, [currentPage, ordersPerPage, filteredAndSearchedOrders]);


  const getStatusClasses = (status: string) => {
    switch (status) {
      case 'Chờ xử lý':
        return 'bg-blue-200 text-blue-800';
      case 'Xử lý thành công':
        return 'bg-green-200 text-green-800';
      case 'Đang vận chuyển':
        return 'bg-purple-200 text-purple-800';
      case 'Đã hoàn thành':
        return 'bg-gray-200 text-gray-800';
      case 'Đã hủy':
        return 'bg-red-200 text-red-800';
      default:
        return 'bg-gray-200 text-gray-800';
    }
  };

  const handleNextStatus = (orderId: string) => {
    setOrders(prevOrders => {
      return prevOrders.map(order => {
        if (order.id === orderId) {
          const currentIndex = ORDER_STATUSES.indexOf(order.status);

          if (order.status === 'Đã hoàn thành' || order.status === 'Đã hủy') {
            return order;
          }

          const nextIndex = currentIndex + 1;
          const newStatus = ORDER_STATUSES[nextIndex] || order.status;

          console.log(`Updating order ${order.id} status to ${newStatus}`);
          // Trong thực tế, bạn sẽ gửi yêu cầu PUT/PATCH đến API ở đây
          return { ...order, status: newStatus };
        }
        return order;
      });
    });
  };

  // --- Hàm hiển thị dialog xác nhận hủy ---
  const handleCancelOrderClick = (order: Order) => {
    setOrderToCancel(order); // Lưu đơn hàng cần hủy vào state
    setShowCancelDialog(true); // Hiển thị dialog
  };

  // --- Hàm xác nhận hủy đơn hàng trong dialog ---
  const confirmCancelOrder = () => {
    if (orderToCancel) {
      setOrders(prevOrders => {
        return prevOrders.map(order => {
          if (order.id === orderToCancel.id) {
            console.log(`Confirming cancellation for order ${order.id}`);
            // Trong thực tế, bạn sẽ gửi yêu cầu API để hủy đơn hàng
            // Ví dụ: axios.put(`/api/orders/${order.id}/status`, { status: 'Đã hủy' });
            return { ...order, status: 'Đã hủy' }; // Cập nhật trạng thái
          }
          return order;
        });
      });
      setOrderToCancel(null); // Xóa đơn hàng khỏi state
      setShowCancelDialog(false); // Ẩn dialog
    }
  };

  // Hàm đóng dialog mà không hủy
  const closeCancelDialog = () => {
    setOrderToCancel(null);
    setShowCancelDialog(false);
  };


  const paginate = (pageNumber: number) => {
    setCurrentPage(pageNumber);
  };

  const pageNumbers = [];
  for (let i = 1; i <= totalPages; i++) {
    pageNumbers.push(i);
  }

  React.useEffect(() => {
    setCurrentPage(1);
  }, [searchTerm, filterStatus, filterMinPrice, filterMaxPrice, filterStartDate, filterEndDate]);


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
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value)}
              className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
            >
              <option value="all">Tất cả trạng thái</option>
              {ORDER_STATUSES.map(status => (
                <option key={status} value={status}>{status}</option>
              ))}
            </select>
          </div>

          {/* Lọc theo khoảng giá */}
          <div className="grid grid-cols-2 gap-2">
            <div>
              <label htmlFor="min-price" className="block text-sm font-medium text-gray-700 mb-1">Giá từ:</label>
              <input
                type="number"
                id="min-price"
                placeholder="Tối thiểu (VNĐ)"
                value={filterMinPrice}
                onChange={(e) => setFilterMinPrice(e.target.value)}
                className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
              />
            </div>
            <div>
              <label htmlFor="max-price" className="block text-sm font-medium text-gray-700 mb-1">Giá đến:</label>
              <input
                type="number"
                id="max-price"
                placeholder="Tối đa (VNĐ)"
                value={filterMaxPrice}
                onChange={(e) => setFilterMaxPrice(e.target.value)}
                className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
              />
            </div>
          </div>

          {/* Lọc theo thời gian đặt hàng */}
          <div className="md:col-span-2 lg:col-span-3 grid grid-cols-2 gap-px">
            <div>
              <label htmlFor="start-date" className="block text-sm font-medium text-gray-700 mb-1">Ngày đặt từ:</label>
              <DatePicker
                selected={filterStartDate}
                onChange={(date: Date | null) => setFilterStartDate(date)}
                selectsStart
                startDate={filterStartDate}
                endDate={filterEndDate}
                placeholderText="Chọn ngày"
                dateFormat="dd/MM/yyyy"
                className="w-full p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
              />
            </div>
            <div>
              <label htmlFor="end-date" className="block text-sm font-medium text-gray-700 mb-1">Ngày đặt đến:</label>
              <DatePicker
                selected={filterEndDate}
                onChange={(date: Date | null) => setFilterEndDate(date)}
                selectsEnd
                startDate={filterStartDate}
                endDate={filterEndDate}
                minDate={filterStartDate ?? undefined}
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
              setFilterStatus('all');
              setFilterMinPrice('');
              setFilterMaxPrice('');
              setFilterStartDate(null);
              setFilterEndDate(null);
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
          value={ordersPerPage}
          onChange={(e) => {
            setOrdersPerPage(Number(e.target.value));
            setCurrentPage(1);
          }}
          className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-green-500"
        >
          <option value={5}>5</option>
          <option value={10}>10</option>
          <option value={20}>20</option>
          <option value={50}>50</option>
        </select>
      </div>

      {/* Bảng đơn hàng */}
      <div className="overflow-x-auto">
        <table className="min-w-full bg-white border border-gray-200">
          <thead>
            <tr className="bg-gray-100 text-gray-600 uppercase text-sm leading-normal">
              <th className="py-3 px-6 text-left">Mã Đơn Hàng</th>
              <th className="py-3 px-6 text-left">Khách Hàng</th>
              <th className="py-3 px-6 text-left">Tổng Cộng</th>
              <th className="py-3 px-6 text-left">Trạng Thái</th>
              <th className="py-3 px-6 text-left">Ngày</th>
              <th className="py-3 px-6 text-center">Hành Động</th>
            </tr>
          </thead>
          <tbody className="text-gray-600 text-sm font-light">
            {currentOrders.length === 0 ? (
              <tr>
                <td colSpan={6} className="py-4 text-center text-gray-500">Không tìm thấy đơn hàng nào khớp với tiêu chí lọc.</td>
              </tr>
            ) : (
              currentOrders.map((order) => (
                <tr key={order.id} className="border-b border-gray-200 hover:bg-gray-50">
                  <td className="py-3 px-6 text-left whitespace-nowrap">{order.id}</td>
                  <td className="py-3 px-6 text-left">{order.customer}</td>
                  <td className="py-3 px-6 text-left">{order.total}</td>
                  <td className="py-3 px-6 text-left">
                    <span
                      className={`px-3 py-1 rounded-full text-xs font-semibold ${getStatusClasses(order.status)}`}
                    >
                      {order.status}
                    </span>
                  </td>
                  <td className="py-3 px-6 text-left">{order.date}</td>
                  <td className="py-3 px-6 text-center whitespace-nowrap">
                    {(order.status !== 'Đã hoàn thành' && order.status !== 'Đã hủy') && (
                      <button
                        onClick={() => handleNextStatus(order.id)}
                        className="bg-blue-500 text-white px-3 py-1 rounded-md text-xs hover:bg-blue-600 transition duration-200"
                      >
                        {order.status === 'Chờ xử lý' && 'Xử lý'}
                        {order.status === 'Xử lý thành công' && 'Vận chuyển'}
                        {order.status === 'Đang vận chuyển' && 'Đã nhận'}
                      </button>
                    )}
                    {/* Nút Hủy đơn hàng - GỌI HÀM HIỂN THỊ DIALOG */}
                    {order.status !== 'Đã hủy' && order.status !== 'Đã hoàn thành' && (
                      <button
                        onClick={() => handleCancelOrderClick(order)} // <-- Đã thay đổi
                        className="ml-2 bg-red-500 text-white px-3 py-1 rounded-md text-xs hover:bg-red-600 transition duration-200"
                      >
                        Hủy
                      </button>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* --- Phần phân trang --- */}
      <div className="mt-6 flex justify-between items-center flex-wrap">
        <div className="text-sm text-gray-600 mb-2 md:mb-0">
          Hiển thị {Math.min((currentPage - 1) * ordersPerPage + 1, filteredAndSearchedOrders.length)} - {Math.min(currentPage * ordersPerPage, filteredAndSearchedOrders.length)} trên tổng số {filteredAndSearchedOrders.length} đơn hàng
        </div>
        <nav className="flex items-center space-x-1" aria-label="Pagination">
          <button
            onClick={() => paginate(currentPage - 1)}
            disabled={currentPage === 1}
            className="px-3 py-1 rounded-md bg-white text-gray-700 border border-gray-300 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition duration-200"
          >
            Trước
          </button>

          {pageNumbers.map(number => (
            <button
              key={number}
              onClick={() => paginate(number)}
              className={`px-3 py-1 rounded-md transition duration-200
                ${currentPage === number
                  ? 'bg-green-600 text-white shadow-md'
                  : 'bg-white text-gray-700 border border-gray-300 hover:bg-gray-100'
                }`}
            >
              {number}
            </button>
          ))}

          <button
            onClick={() => paginate(currentPage + 1)}
            disabled={currentPage === totalPages}
            className="px-3 py-1 rounded-md bg-white text-gray-700 border border-gray-300 hover:bg-gray-100 disabled:opacity-50 disabled:cursor-not-allowed transition duration-200"
          >
            Sau
          </button>
        </nav>
      </div>

      {/* --- DIALOG XÁC NHẬN HỦY ĐƠN HÀNG --- */}
            {showCancelDialog && orderToCancel && (
  <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
    <div className="bg-white p-6 rounded-3xl shadow-2xl w-full max-w-sm transform transition-all duration-300 ease-out animate-scale-in border border-gray-100"> {/* ĐÃ BỎ opacity-0 và scale-95 */}
      <h3 className="text-2xl font-bold text-gray-800 mb-4 text-center">Xác nhận Hủy Đơn Hàng</h3>
      <p className="text-gray-700 mb-6 text-center">Bạn có chắc chắn muốn hủy đơn hàng <span className="font-semibold text-blue-600">{orderToCancel.id}</span> này không?</p>
      
      <div className="mb-6 bg-gray-50 p-4 rounded-lg border border-gray-200">
          <p className="mb-2"><span className="font-semibold text-gray-800">Mã Đơn Hàng:</span> {orderToCancel.id}</p>
          <p className="mb-2"><span className="font-semibold text-gray-800">Khách Hàng:</span> {orderToCancel.customer}</p>
          <p className="mb-2"><span className="font-semibold text-gray-800">Tổng Cộng:</span> {orderToCancel.total}</p>
          <p><span className="font-semibold text-gray-800">Trạng Thái:</span> 
              <span className={`ml-2 px-3 py-1 rounded-full text-xs font-semibold ${getStatusClasses(orderToCancel.status)}`}>
                  {orderToCancel.status}
              </span>
          </p>
      </div>
      
      <div className="flex justify-end space-x-3">
          <button
              onClick={closeCancelDialog}
              className="px-5 py-2 bg-gray-300 text-gray-800 rounded-lg hover:bg-gray-400 transition duration-200 font-medium"
          >
              Không
          </button>
          <button
              onClick={confirmCancelOrder}
              className="px-5 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition duration-200 font-medium"
          >
              Xác nhận Hủy
          </button>
      </div>
    </div>
  </div>
)}F
        </div>
    );
};

export default OrdersPage;