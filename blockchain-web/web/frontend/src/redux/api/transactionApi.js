import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { BASE_PATH } from "../../constants/constants";
import { GOLD_UNIT_CONVERT } from "../../helpers/converters";
import { setHistoryData } from "../features/liveRateSlice";
import { authApi } from "./authApi";

export const transactionApi = createApi({
  reducerPath: "transactionApi",
  baseQuery: fetchBaseQuery({
    baseUrl: BASE_PATH,
    prepareHeaders: (headers, { getState }) => {
      const token = localStorage.getItem("token");

      if (token) {
        headers.set("Authorization", `Bearer ${token}`);
      }
      return headers;
    },
  }),
  tagTypes: ["Transaction", "AdminTransaction", "LiveRate", "Withdraw"],
  endpoints: (builder) => ({
    timeframe: builder.mutation({
      query: () => ({
        url: `/gold/timeframe`,
        method: "GET",
      }),
      transformResponse: modifyResponse,
      providesTags: ["LiveRate"],
    }),
    liveRate: builder.mutation({
      query: (type) => ({
        url: `/gold/live-rate?type=${type}`,
        method: "GET",
      }),
      onQueryStarted: async (args, { dispatch, queryFulfilled }) => {
        try {
          const { data } = await queryFulfilled;
          dispatch(setHistoryData(data));
        } catch (error) {
          console.log(error);
        } finally {
        }
      },
      transformResponse: (result) => result.map(modifyResponse),
      providesTags: ["LiveRate"],
    }),
    // Transactions
    processTransaction: builder.mutation({
      query: ({ quantityInOz, pricePerOz, type, goldUnit }) => ({
        url: `/transactions/${localStorage.getItem("user-id")}`,
        method: "PUT",
        params: {
          quantityInOz,
          pricePerOz,
          type,
          goldUnit: GOLD_UNIT_CONVERT[goldUnit],
        },
      }),
      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        try{
        await queryFulfilled;
        dispatch(authApi.util.invalidateTags(["Users", "Auth"]));
        }catch(e){
          console.log(e.error.data);
        }
      },
      invalidatesTags: ["Transaction"],
    }),
    transactionList: builder.query({
      query: (userId) => {
        if (userId) return `/user-transaction-list/${userId}`;
        return "/transaction-list";
      },
      transformResponse: (result) => result.data,
      providesTags: ["Transaction"],
      invalidatesTags: ["Transaction", "Order", "AdminOrders"],
    }),
    userTransactionList: builder.mutation({
      query: (userId) => ({
        url: `/user-transaction-list/${userId}`,
        method: "GET",
      }),
      transformResponse: (result) => result.data,
      providesTags: ["Transaction"],
    }),
    myTransactionList: builder.query({
      query: () => ({
        url: `/user-transaction-list/${localStorage.getItem("user-id")}`,
        method: "GET",
      }),
      transformResponse: (result) => result.data,
      providesTags: ["Transaction"],
    }),
    requestWithdrawGold: builder.mutation({
      query: ({ weightToWithdraw, unit }) => ({
        url: `/request-withdraw-gold/${localStorage.getItem("user-id")}`,
        method: "POST",
        params: {
          weightToWithdraw: Number(weightToWithdraw),
          unit,
        },
      }),
      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        await queryFulfilled;
        dispatch(authApi.util.invalidateTags(["Users", "Auth"]));
      },
      providesTags: ["Withdraw"],
    }),
    // Withdraw
    handleWithdrawal: builder.mutation({
      query: ({ id, actionStr, withdrawQrCode = null }) => ({
        url: `/handle-withdraw-gold/${id}`,
        method: "PATCH",
        params: {
          actionStr,
          withdrawQrCode,
        },
      }),
      invalidatesTags: ["Withdraw"],
    }),

    cancelWithdrawal: builder.mutation({
      query: ({ withdrawalId, reason }) => ({
        url: `/cancel/${withdrawalId}`,
        method: "POST",
        params: {
          reason,
        },
      }),
      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        try {
          await queryFulfilled;
          dispatch(authApi.util.invalidateTags(["Users", "Auth"]));
        } catch (error) {
          console.error(error.error.data);
        }
      },
      invalidatesTags: ["Withdraw", "Transaction"],
    }),
    withdrawList: builder.query({
      query: (userId) => {
        if (userId) return `/withdraw-list-userinfo/${userId}`;
        return "/withdraw-list";
      },
      method: "GET",
    }),
    myWithdrawList: builder.query({
      query: () => ({
        url: `/withdraw-list-userinfo/${localStorage.getItem("user-id")}`,
        method: "GET",
      }),
      providesTags: ["Withdraw"],
    }),
    acceptTransaction: builder.mutation({
      query: ({ transactionId, publicKey }) => ({
        url: `/transactions-accepted/web/${transactionId}`,
        method: "PUT",
        params: {
          publicKey,
        },
      }),
    }),
  }),
});
function modifyResponse(result) {
  return {
    ...result,
  };
}
export const {
  useLiveRateMutation,
  useTimeframeMutation,
  useProcessTransactionMutation,
  useRequestWithdrawGoldMutation,
  useHandleWithdrawalMutation,
  useCancelWithdrawalMutation,
  useTransactionListQuery,
  useMyTransactionListQuery,
  useWithdrawListQuery,
  useMyWithdrawListQuery,
  useAcceptTransactionMutation,
} = transactionApi;
