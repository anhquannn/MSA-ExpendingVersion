import axios, { AxiosRequestConfig, InternalAxiosRequestConfig } from 'axios';
import { storage } from '../utils/storage';
import { AxiosResponse } from 'axios';

const axiosClient = axios.create({
  baseURL: process.env.REACT_APP_API_URL, 
  headers: {
    'Content-Type': 'application/json',
  },
});

axiosClient.interceptors.request.use(
  async (config: InternalAxiosRequestConfig) => {
    
    const token = storage.getItem<string>('token'); 
    if (token) {
    config.headers['Authorization'] = 'Bearer ' + token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

axiosClient.interceptors.response.use(
  (response: AxiosResponse) => {
       
    if (response.data && response.data.code === 200) {
      return response.data.result; 
    }
         return response.data;
  },
  (error) => {
        console.error('Lỗi API:', error.response?.data?.message || error.message);
    const message = error.response?.data?.message || 'Đã có lỗi xảy ra';
    return Promise.reject(error);
  }
);
export default axiosClient;