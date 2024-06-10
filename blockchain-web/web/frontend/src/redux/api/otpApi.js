import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { BASE_PATH } from "../../constants/constants";
import { clearCart } from "../features/cartSlice";
import { authApi } from "./authApi";

export const otpApi = createApi({
  reducerPath: "verificationOrderApi",
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
  }),
  endpoints: (builder) => ({
    
    verifyOrder: builder.mutation({
      
      query({ otp, orderId }) {
        const id = localStorage.getItem("user-id");
        return {
          url: `/verification-order/${id}/${otp}/${orderId}`,
          method: "GET",
        };
      },
      onQueryStarted: async (args, { dispatch, queryFulfilled }) => {
        try {
          await queryFulfilled;
          dispatch(authApi.util.invalidateTags(["Users", "Auth"]));
          dispatch(clearCart());
        } catch (e) {
          console.log(e);
        }
      },
    }),
    resendOTP: builder.mutation({
      query({ orderId }) {
        return {
          url: `/resent-otp/${orderId}`,
          method: "GET",
        };
      },
    }),
    verifyWithdraw: builder.mutation({
      query({userInfoId, otp, withdrawId }) {
        return {
          url: `/request-withdraw-gold/verify/${userInfoId}/${otp}/${withdrawId}`,
          method: "POST",
        };
      },
    }),
    resendOTPWithdraw: builder.mutation({
      query({ withdrawId }) {
        return {
          url: `/resent-otp-withdraw/${withdrawId}`,
          method: "GET",
        };
      },
    }),
  }),
});

export const {
  useVerifyOrderMutation,
  useResendOTPMutation,
  useVerifyWithdrawMutation,
  useResendOTPWithdrawMutation,
} = otpApi;
