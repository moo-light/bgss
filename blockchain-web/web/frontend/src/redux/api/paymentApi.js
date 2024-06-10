import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { BASE_PATH } from "../../constants/constants";
import { authApi } from "./authApi";

export const paymentApi = createApi({
  reducerPath: "paymentApi",
  baseQuery: fetchBaseQuery({
    baseUrl: BASE_PATH,
    prepareHeaders: (headers, { getState }) => {
      const token = localStorage.getItem("token");
      if (token) {
        headers.set("Authorization", `Bearer ${token}`);
      }
      headers.set("Access-Control-Allow-Origin", `*`);
      return headers;
    },
    responseHandler: "text",
  }),
  tagTypes: ["Payment", "Users", "Auth"],
  endpoints: (builder) => ({
    getPaymentUrl: builder.mutation({
      query: (request) => {
        request.price = Number(request.price);
        return { url: `/payment`, params: request };
      },

      transformResponse: (result) => {
        // Assuming result is a string URL, not a JSON object
        return result;
      },
      providesTags: ["Payment"],
    }),
    checkValidPay: builder.query({
      query: (searchParams) => {
        return `/check-valid-pay?${searchParams.toString()}`;
      },

      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        await queryFulfilled;
        dispatch(authApi.util.invalidateTags(["Users", "Auth"]));
      },
      transformResponse: (result) => {
        result = JSON.parse(result);
        return result;
      },
      transformErrorResponse: (error) => {
        error.data = JSON.parse(error.data);
        return error;
      },
      invalidatesTags: ["User", "Auth"],
    }),
    getOrderDetails: builder.mutation({
      query: (id) => `/get-order-by-id/${id}`,
      transformResponse: (result) => {
        // Assuming result is a string URL, not a JSON object
        return JSON.parse(result);
      },
      providesTags: ["Order"],
    }),
  }),
});

export const {
  useGetPaymentUrlMutation,
  useLazyCheckValidPayQuery,
  useGetOrderDetailsMutation,
} = paymentApi;
