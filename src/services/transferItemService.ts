import { api } from './apiService';
import { PagedResponse } from './apiService';

// --- INTERFACES ---
interface ProductResponse {
  productId: number;
  name: string;
  price: number;
  unit: string;
  netWeight: string;
  productImageResponses?: Array<{
    productImageId: number;
    imageUrl: string;
    sortOrder: number;
    primary: boolean;
  }>;
}

export interface TransferResponseItem {
  transferRequestItemId: number;
  quantityRequested: number;
  quantityTransferred: number;
  productResponse: ProductResponse;
  transferResponse: {
    transferRequestId: number;
    status: string;
    note: string;
    createdAt: string;
    updatedAt: string | null;
    requesterResponse: {
      userId: number;
      fullName: string;
      email: string;
    };
    approverResponse: {
      userId: number;
      fullName: string;
      email: string;
    };
    fromInventoryResponse: {
      inventoryId: number;
      name: string;
      address: string;
    };
    toInventoryResponse: {
      inventoryId: number;
      name: string;
      address: string;
    };
  };
}

export interface TransferItemUpdateRequest {
  quantityRequested: number;
  quantityTransferred: number;
  productId: number;
  transferRequestId: number;
}

export interface TransferItemFilter {
  page?: number;
  pageSize?: number;
  transferRequestId: number;
  productId?: number;
}

// --- SERVICE METHODS ---
export const transferItemService = {
  // Get paged list of items for a specific transfer request
  getTransferItems: async (filter: TransferItemFilter): Promise<PagedResponse<TransferResponseItem>> => {
    type FullApiResponse = { result: PagedResponse<TransferResponseItem> };
    const params = {
        page: filter.page ?? 1,
        pageSize: filter.pageSize ?? 10,
        transferRequestId: filter.transferRequestId,
        productId: filter.productId
    };
    
    console.log('Calling tri/paging API with params:', params);
    try {
      const response = await api.post<FullApiResponse>('tri/paging', params);
      console.log('Received response from tri/paging:', response);
      return response.result;
    } catch (error) {
      console.error('Error in getTransferItems:', error);
      throw error;
    }
  },

  // Update a transfer request item (e.g., to set quantityTransferred)
  updateTransferItem: async (id: number, payload: TransferItemUpdateRequest): Promise<TransferResponseItem> => {
    type FullApiResponse = { result: TransferResponseItem };
    const response = await api.put<FullApiResponse>(`tri/${id}`, payload);
    return response.result;
  }
};
