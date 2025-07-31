import { api } from './apiService';
import { PagedResponse } from './categoryService'; // hoặc bạn có thể tách riêng vào file types

// Trạng thái đơn hàng
export enum OrderStatus {
  PENDING = 'PENDING',
  PAYING = 'PAYING',
  PAID = 'PAID',
  DELIVERING = 'DELIVERING',
  SHIPPED = 'SHIPPED',
  CANCELLING = 'CANCELLING',
  CANCELLED = 'CANCELLED',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
}

// Đơn hàng hiển thị (rút gọn theo yêu cầu)
export interface SimpleOrder {
  orderId: number;
  orderDate: string;
  grandTotal: number;
  status: OrderStatus;
  branchName: string;
  shipmentCode?: string;
}

// Dữ liệu lọc (theo OrderFilterRequest bên backend)
export interface OrderFilterRequest {
  branchId?: number;
  userId?: number;
  status?: OrderStatus | 'all';
  fromDate?: Date | string | null;
  toDate?: Date | string | null;
  phoneNumber?: string;

  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  page?: number;
  pageSize?: number;
}

// Payload cập nhật trạng thái
export interface OrderUpdateStatusPayload {
  status: OrderStatus;
}

// Service chính
export const orderService = {
  // Lấy danh sách đơn hàng (có phân trang)
  getOrdersWithPaging: async (
    filter: OrderFilterRequest
  ): Promise<PagedResponse<SimpleOrder>> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: PagedResponse<SimpleOrder>;
    };

    const { fromDate, toDate, status, phoneNumber, ...rest } = filter;

    const payload: any = {
      ...rest,
      phoneNumber,
      sortBy: filter.sortBy || 'orderDate',
      sortDirection: filter.sortDirection || 'DESC',
    };

    // Helper to format to 'yyyy-MM-dd HH:mm:ss'
    const formatLocalDateTime = (d: Date) => {
      const pad = (n: number) => String(n).padStart(2, '0');
      return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;
    };

    // Xử lý ngày nếu có
    if (fromDate) {
      const date = new Date(fromDate);
      if (!isNaN(date.getTime())) {
        // Nếu chỉ chọn ngày thì set về 00:00:00
        if (typeof fromDate !== 'string') {
          date.setHours(0, 0, 0, 0);
        }
        payload.fromDate = formatLocalDateTime(date);
      }
    }

    if (toDate) {
      const date = new Date(toDate);
      if (!isNaN(date.getTime())) {
        // Nếu chỉ chọn ngày thì set cuối ngày 23:59:59
        if (typeof toDate !== 'string') {
          date.setHours(23, 59, 59, 0);
        }
        payload.toDate = formatLocalDateTime(date);
      }
    }

    // Nếu status là 'all' thì bỏ qua
    if (status && status !== 'all') {
      payload.status = status;
    }
    // Nếu phoneNumber rỗng thì bỏ
    if (!phoneNumber) {
      delete payload.phoneNumber;
    }

    const response = await api.post<FullApiResponse>('order/paging', payload);
    // Ensure branchName is populated for each order
    const mappedResult: PagedResponse<SimpleOrder & { branch?: { name: string } }> = {
      ...response.result,
      content: (response.result.content || []).map((order: any) => ({
        ...order,
        branchName: order.branchName || order.branch?.name || '',
      })),
    } as any;
    return mappedResult as unknown as PagedResponse<SimpleOrder>;
  },

  // Cập nhật trạng thái đơn hàng
  updateOrderStatus: async (
    orderId: number,
    status: OrderStatus
  ): Promise<void> => {
    await api.put<void>(`order/${orderId}/status?status=${status}`, undefined);
  },

    // Hủy đơn hàng (tạo yêu cầu trả/hủy)
  createCancelOrder: async (
    orderId: number,
    reason: string
  ): Promise<void> => {
    const payload = { orderId, reason };
    await api.post<void>('return-order', payload);
  },
};
