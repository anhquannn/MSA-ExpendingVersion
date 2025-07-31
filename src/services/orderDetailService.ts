import { api } from './apiService';

export interface OrderDetail {
  orderDetailId: number;
  name: string;
  quantity: number;
  price?: number;
  rated?: boolean;
}

export const orderDetailService = {
  // Lấy danh sách chi tiết đơn hàng theo orderId
  getByOrderId: async (orderId: number): Promise<OrderDetail[]> => {
    type ApiResponse = {
      code: number;
      message: string;
      result: OrderDetail[];
    };
    const response = await api.get<ApiResponse>(`order-detail/order/${orderId}`);
    return response.result || [];
  },
};
