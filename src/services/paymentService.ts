import { api } from './apiService';
import { PagedResponse } from './categoryService';
import { OrderStatus } from './orderService';

export interface Payment {
  paymentId: number;
  paymentDate: string;
  amount: number;
  status: OrderStatus;
  paymentMethod: string;
  transactionId: string;
  orderId: number;
}

// Các tham số filter tối thiểu cần dùng
export interface PaymentFilterRequest {
  orderId?: number;
  userId?: number;
  status?: OrderStatus;
  paymentMethod?: string;
  fromDate?: Date | string | null;
  toDate?: Date | string | null;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  page?: number;
  pageSize?: number;
}

// Simple in-memory cache to avoid duplicate network calls within the same session
const _paymentStatusCache: Map<number, OrderStatus | null> = new Map();

export const paymentService = {
  // Lấy danh sách thanh toán với phân trang
  getPaymentsWithPaging: async (
    filter: PaymentFilterRequest
  ): Promise<PagedResponse<Payment>> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: PagedResponse<Payment>;
    };

    const response = await api.post<FullApiResponse>('payment/paging', filter);
    return response.result;
  },

  // Lấy trạng thái thanh toán mới nhất của một danh sách đơn hàng (trả về Map<orderId, status>)
  getLatestPaymentStatusesForOrders: async (
    orderIds: number[],
  ): Promise<Record<number, OrderStatus | null>> => {
    // Loại bỏ những ID đã có trong cache
    // map để lưu trạng thái mới nhất
    const latestMap: Record<number, OrderStatus | null> = {};
    const idsToFetch = orderIds.filter((id) => {
      const cached = _paymentStatusCache.get(id);
      return cached === undefined || cached !== OrderStatus.PAID; // refetch nếu chưa có hoặc chưa PAID
    });
    if (idsToFetch.length) {
      try {
        /*
         * BE implementation đề xuất:
         * POST `payment/list` với body { orderIds, pageSize: 1000, sortBy: 'paymentDate', sortDirection: 'DESC' }
         * Trả về danh sách Payment (đã sắp xếp)
         */
        const response = await paymentService.getPaymentsWithPaging({
          // custom payload – backend cần hỗ trợ truyền mảng orderIds
          orderIds,
          page: 1,
          pageSize: orderIds.length, // lấy đủ
          sortBy: 'paymentDate',
          sortDirection: 'DESC',
        } as any);

        console.log('payment paging response', response);
        // Lấy payment đầu tiên (mới nhất) cho mỗi orderId
        
        (response.content || []).forEach((p: any) => {
          const oid: number | undefined = p.orderId ?? p.orderResponse?.orderId ?? Number(p.transactionId);
          if (oid !== undefined && !(oid in latestMap)) {
            latestMap[oid] = p.status as OrderStatus;
          }
        });
        // Update cache
        Object.entries(latestMap).forEach(([id, status]) => {
          _paymentStatusCache.set(Number(id), status as OrderStatus | null);
        });
      } catch (err) {
        console.error('Failed to batch fetch payment statuses', err);
      }
    }

    // Build result map from cache (undefined -> null)
    const result: Record<number, OrderStatus | null> = {};
    console.log('latestMap built', latestMap);
    orderIds.forEach((id) => {
      result[id] = _paymentStatusCache.get(id) ?? null;
    });
    return result;
  },

  // Lấy trạng thái thanh toán mới nhất của 1 đơn hàng
  getLatestPaymentStatusForOrder: async (
    orderId: number
  ): Promise<OrderStatus | null> => {
    // Kiểm tra cache trước
    const cached = _paymentStatusCache.get(orderId);
    if (cached !== undefined && cached === OrderStatus.PAID) {
      return cached;
    }
    const result = await paymentService.getPaymentsWithPaging({
      orderId,
      page: 1,
      pageSize: 1,
      sortBy: 'paymentDate',
      sortDirection: 'DESC',
    });
    if (result.content && result.content.length > 0) {
      const status = result.content[0].status;
      _paymentStatusCache.set(orderId, status);
      return status;
    }
    return null;
  },
};
