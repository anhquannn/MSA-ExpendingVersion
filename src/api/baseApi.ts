import axios, { AxiosRequestConfig, InternalAxiosRequestConfig } from 'axios';
import { storage } from '../utils/storage';

const supabaseClient = axios.create({
  baseURL: process.env.REACT_APP_SUPABASE_URL, 
});

supabaseClient.interceptors.request.use(async (config: InternalAxiosRequestConfig) => {
 const token = storage.getItem<string>('token'); 
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token;
    config.headers['x-upsert'] = 'false'; 
  }
  return config;
});

export const baseApi = {
  uploadFile: async (file: File, bucketName: string) => {
    const formData = new FormData();
    formData.append('file', file); 

    const fileName = file.name;
    const url = `/storage/v1/object/${bucketName}/${fileName}`;

    try {
      const response = await supabaseClient.post(url, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      return response.data;
    } catch (error) {
      console.error('Lỗi upload file:', error);
      throw error;
    }
  },
};