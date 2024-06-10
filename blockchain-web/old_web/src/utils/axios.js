import axios from 'axios';

import { HOST_API } from 'src/config-global';

// ----------------------------------------------------------------------

const axiosInstance = axios.create({
  baseURL: HOST_API,
});
axiosInstance.defaults.headers = {
  ...axiosInstance.defaults.headers,
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Credentials': 'true',
  'Access-Control-Allow-Headers': 'content-type',
  'Access-Control-Allow-Methods': 'PUT, POST, GET, DELETE, PATCH, OPTIONS',
};
axiosInstance.interceptors.response.use(
  (res) => res,
  (error) => Promise.reject((error.response && error.response.data) || 'Something went wrong')
);

export default axiosInstance;

// ----------------------------------------------------------------------

export const fetcher = async (args) => {
  const [url, config] = Array.isArray(args) ? args : [args];

  const res = await axiosInstance.get(url, { ...config });

  return res.data;
};

// ----------------------------------------------------------------------

export const endpoints = {
  chat: '/api/chat',
  kanban: '/api/kanban',
  calendar: '/api/calendar',
  auth: {
    me: (id) => `https://www.bgss-company.tech/api/auth/show-user-info/${id}`,
    login: 'https://www.bgss-company.tech/api/auth/sign-in-v2',
    register: '/api/auth/sign-up',
  },
  user: {
    updateAvatar: (id) => `/api/auth/update-avatar/${id}`,
    updateInfo: `/api/auth/update-user-info`,
    updateUserName: (id) => `/api/auth/update-user-name/${id}`,
    register: '/api/auth/sign-up',
  },
  mail: {
    list: '/api/mail/list',
    details: '/api/mail/details',
    labels: '/api/mail/labels',
  },
  post: {
    list: '/api/post/list',
    details: '/api/post/details',
    latest: '/api/post/latest',
    search: '/api/post/search',
  },
  product: {
    list: 'https://www.bgss-company.tech/api/auth/product/show-list-product',
    // details: '/api/product/details',
    details: '/api/auth/product/get-product-by-id',
    search: '/api/auth/product/get-product-by-id',
  },
};
