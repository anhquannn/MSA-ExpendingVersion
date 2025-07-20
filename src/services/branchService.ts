// File: src/services/branchService.ts

import {api} from './apiService';
import { PagedResponse } from './categoryService';


export interface Branch {
  branchId: number;
  name: string;
  phone: string;
  street: string;
  ward: string;
  wardCode: string;
  district: string;
  districtCode: string;
  city: string;
  cityCode: string;
  inventory: {
    inventoryId: number;
    name: string;
    address: string;
    contact: string;
    totalRevenue: number;
    branch: {
      branchId: number;
      name: string;
      phone: string;
      street: string;
      ward: string;
      wardCode: string;
      district: string;
      districtCode: string;
      city: string;
      cityCode: string;
    } | null;
  } | null;

}

export interface BranchPagingParams {
  keyword?: string;
  productId?: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  page?: number;
  pageSize?: number;
}

export interface CreateBranchWithManagerPayload {
  branchName: string;
  branchPhone: string;
  branchStreet: string;
  branchWard: string;
  branchWardCode: string;
  branchDistrict: string;
  branchDistrictCode: string;
  branchCity: string;
  branchCityCode: string;

  inventoryName: string;
  inventoryAddress: string;
  inventoryContact: string;

  managerFullName: string;
  managerEmail: string;
  managerPhoneNumber: string;
  managerPassword?: string;
}

export const branchService = {

  getAllBranchesWithPaging: async (params: BranchPagingParams): Promise<PagedResponse<Branch>> => {
    type FullApiResponse = { result: PagedResponse<Branch> };
    const response = await api.post<FullApiResponse>('branch/paging', params);
    return response.result;
  },

  getBranchById: async (branchId: number): Promise<Branch> => {
    type FullApiResponse = { result: Branch };
    const response = await api.get<FullApiResponse>(`branch/${branchId}`);
    return response.result;
  },

  createBranchWithManager: async (payload: CreateBranchWithManagerPayload): Promise<Branch> => {
    type FullApiResponse = { result: Branch };
    // Endpoint đặc biệt là 'branch/admin'
    const response = await api.post<FullApiResponse>('branch/admin', payload);
    return response.result;
  },

  updateBranch: async (branchId: number, payload: Partial<Omit<Branch, 'branchId' | 'inventory'>>): Promise<Branch> => {
    type FullApiResponse = { result: Branch };
    const response = await api.put<FullApiResponse>(`branch/${branchId}`, payload);
    return response.result;
  },

  deleteBranch: (branchId: number): Promise<void> => {
    return api.delete<void>(`branch/${branchId}`);
  },
};