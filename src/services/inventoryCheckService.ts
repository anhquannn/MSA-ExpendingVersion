import { api, PagedResponse } from './apiService';
import { UserResponse, InventoryResponse } from './transferService';

/*** INTERFACES ***/
export type ProductStatus = 'PENDING' | 'IN_PROGRESS' | 'RECEIVED' | 'CANCELLED';

export interface InventoryCheckRequestResponse {
  icrId: number;
  note?: string;
  status: ProductStatus;
  createdAt: string;
  updatedAt: string | null;
  inventoryResponse: InventoryResponse;
  surveyorResponse: UserResponse;
}

export interface InventoryCheckRequestFilter {
  keyword?: string;
  inventoryId?: number;
  surveyorId?: number;
  status?: ProductStatus;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  page?: number;
  pageSize?: number;
}

/*** SERVICE METHODS ***/
export const inventoryCheckService = {
  // Get paged list of inventory check requests with filters
  getInventoryChecks: async (
    filter: InventoryCheckRequestFilter
  ): Promise<PagedResponse<InventoryCheckRequestResponse>> => {
    type FullApiResponse = { result: PagedResponse<InventoryCheckRequestResponse> };
    const response = await api.post<FullApiResponse>('inventory-check-requests/paging', filter);
    return response.result;
  },

  // Update status to RECEIVED
  updateStatusToReceived: async (
    icrId: number
  ): Promise<InventoryCheckRequestResponse> => {
    type FullApiResponse = { result: InventoryCheckRequestResponse };
    const response = await api.put<FullApiResponse>(`inventory-check-requests/${icrId}/received`, {});
    return response.result;
  },
};
