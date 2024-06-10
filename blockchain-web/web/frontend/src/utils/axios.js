import axios from 'axios';

import { BASE_HOST } from '../constants/constants';

const axiosInstance = axios.create({ baseURL: BASE_HOST });

axiosInstance.interceptors.response.use(
    (res) => res,
    (error) => Promise.reject((error.response && error.response.data) || 'Something went wrong')
  );

  export default axiosInstance;

  export const fetcher = async (args) => {
    const [url, config] = Array.isArray(args) ? args : [args];
  
    const res = await axiosInstance.get(url, { ...config });
  
    return res.data;
  };

  export const endpoints = {
    productsearch: '/api/auth/get-all-product-by-type-gold-name',
    withdraw: {
      withdrawal_request: '/api/auth/request-withdraw-gold', 
      compete_withdrawal: '/api/auth/user-confirm-withdraw',
      otp_request: '/api/auth/request-withdraw-gold/verify',
    }
  
  }