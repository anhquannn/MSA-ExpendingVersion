// File: src/services/userService.ts
import {api} from './apiService'; // Import ApiService đã được cấu hình sẵn
import { PagedResponse } from './categoryService';

export interface Permission {
  permissionId: number;
  name: string;
  description: string;
}

export interface Role {
  roleId: number;
  name: string;
  description: string;
  permissions: Permission[];
}

export interface User {
  userId: number;
  fullName: string;
  email: string;
  phoneNumber: string | null;
  birthday: string | null;
  password?: string;
  address: string | null;
  image: string | null;
  deviceId: string | null;
  googleId: string | null;
  roles: Role[] | null; // Cập nhật để dùng Role interface
  branches: any[] | null;
}

export interface PagingParams {
  page?: number;
  size?: number;
}

export interface UserUpdatePayload {
  fullName?: string;
  email?: string;
  phoneNumber?: string;
  birthday?: string | null;
  password?: string; 
  address?: string;
  googleId?: string | null;
  roles?: number[];
}

export const userService = {
  
  updateUser: async (userId: number, payload: UserUpdatePayload): Promise<User> => {
    type FullApiResponse = {
      code: number;
      message: string;
      result: User;
    };
    const response = await api.put<FullApiResponse>(`user/${userId}`, payload);
    return response.result;
  },

   getUsersByRole: async (
    role: 'admin' | 'customer' | 'manager_2', 
    params: PagingParams
  ): Promise<PagedResponse<User>> => {
    type FullApiResponse = { result: PagedResponse<User> };
    const endpoint = `user/role/${role}/page`;
    const response = await api.get<FullApiResponse>(endpoint, params);
    return response.result;
  },

   deleteUser: (userId: number): Promise<void> => {
    return api.delete<void>(`user/${userId}`);
  },

  getUserById: async (userId: number): Promise<User> => {
    type FullApiResponse = { result: User };
    const response = await api.get<FullApiResponse>(`user/${userId}`);
    return response.result;
  },

    getInfoUsers: async (): Promise<User[]> => {
    type FullApiResponse = { result: User[] };
    const response = await api.get<FullApiResponse>('user');
    return response.result;
  },

};