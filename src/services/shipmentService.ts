// src/services/shipmentService.ts
// Service dùng để gọi các API lấy dữ liệu thành phố, quận, phường từ GoshipController
import { api } from './apiService';

export interface CityResponse {
  code: string;
  name: string;
}
export interface DistrictResponse {
  code: string;
  name: string;
}
export interface WardResponse {
  code: string;
  name: string;
}

export const shipmentService = {
  getCities: async (): Promise<CityResponse[]> => {
    type Raw = { id: string; name: string };
    type FullApiResponse = { result: Raw[] };
    const res = await api.get<FullApiResponse>('shipment/cities');
    return res.result.map(r => ({ code: r.id, name: r.name }));
  },
  getDistricts: async (cityCode: string | number): Promise<DistrictResponse[]> => {
    type Raw = { id: string; name: string };
    type FullApiResponse = { result: Raw[] };
    const res = await api.get<FullApiResponse>(`shipment/districts/${cityCode}`);
    return res.result.map(r => ({ code: r.id, name: r.name }));
  },
  getWards: async (districtCode: string | number): Promise<WardResponse[]> => {
    type Raw = { id: string; name: string };
    type FullApiResponse = { result: Raw[] };
    const res = await api.get<FullApiResponse>(`shipment/wards/${districtCode}`);
    return res.result.map(r => ({ code: r.id, name: r.name }));
  },
  /**
   * Gửi webhook giả lập cập nhật trạng thái.
   */
  mockWebhook: async (code: string, status: number = 913): Promise<void> => {
    await api.post<void>('shipment/webhook', { code, status });
  },
};
