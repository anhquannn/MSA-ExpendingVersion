import { api } from './apiService';
import { PagedResponse } from './apiService';

// --- INTERFACES ---
export interface UserResponse {
  userId: number;
  fullName: string;
  email: string;
  phoneNumber: string;
  image?: string;
  roles?: Array<{
    roleId: number;
    name: string;
    description: string;
    permissions: Array<{
      permissionId: number;
      name: string;
      description: string;
    }>;
  }>;
}

export interface InventoryResponse {
  inventoryId: number;
  name: string;
  address: string;
  contact: string;
  totalRevenue: number;
  inventoryProductResponses?: any[];
}

export interface TransferResponse {
  transferRequestId: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED' | 'COMPLETED';
  note?: string;
  createdAt: string;
  updatedAt: string | null;
  requesterResponse: UserResponse;
  approverResponse: UserResponse | null;
  fromInventoryResponse: InventoryResponse;
  toInventoryResponse: InventoryResponse;
  transferItems: any[]; // Simplified for now
}

export interface TransferRequest {
  fromInventoryId: number;
  toInventoryId: number;
  requesterId: number;
  note?: string;
  transferItems: { productId: number; quantityRequested: number }[];
}

export interface TransferRequestFilter {
  page?: number;
  pageSize?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  requesterId?: number;
  approverId?: number;
  fromInventoryId?: number;
  toInventoryId?: number;
  status?: string;
  fromDate?: string;
  toDate?: string;
}

// --- SERVICE METHODS ---
export const transferService = {
  // Get paged list of transfer requests
  getTransferRequests: async (filter: TransferRequestFilter): Promise<PagedResponse<TransferResponse>> => {
    type FullApiResponse = { result: PagedResponse<TransferResponse> };
    const response = await api.post<FullApiResponse>('tr/paging', filter);
    return response.result;
  },

  // Get a single transfer request by ID
  getTransferRequestById: async (id: number): Promise<TransferResponse> => {
    type FullApiResponse = { result: TransferResponse };
    const response = await api.get<FullApiResponse>(`tr/${id}`);
    return response.result;
  },

  // Create a new transfer request
  createTransferRequest: async (request: TransferRequest): Promise<TransferResponse> => {
    type FullApiResponse = { result: TransferResponse };
    const response = await api.post<FullApiResponse>('tr', request);
    return response.result;
  },

  // Approve a transfer request
  approveTransferRequest: async (id: number): Promise<TransferResponse> => {
    type FullApiResponse = { result: TransferResponse };
    const response = await api.post<FullApiResponse>(`tr/${id}/approve`, {});
    return response.result;
  },

  // Reject a transfer request
  rejectTransferRequest: async (id: number, note: string): Promise<TransferResponse> => {
    type FullApiResponse = { result: TransferResponse };
    const response = await api.post<FullApiResponse>(`tr/${id}/reject?note=${encodeURIComponent(note)}`, {});
    return response.result;
  },

  // Update a transfer request (e.g., for admin to set approver)
  updateTransferRequest: async (id: number, request: Partial<TransferRequest>): Promise<TransferResponse> => {
    type FullApiResponse = { result: TransferResponse };
    const response = await api.put<FullApiResponse>(`tr/${id}`, request);
    return response.result;
  }
};
