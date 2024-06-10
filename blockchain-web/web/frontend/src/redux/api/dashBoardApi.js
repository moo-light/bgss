import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { BASE_PATH } from "../../constants/constants";

export const dashboardApi = createApi({
  reducerPath: "dashboardApi",
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
  tagTypes: ["Dashboard"],
  endpoints: (builder) => ({
    calculateOrderSales: builder.mutation({
      query({ type, monthChose, quarterChoose, yearChoose }) {
        return {
          url: `/calculate-order-sales`,
          params: { type, monthChose, quarterChoose, yearChoose },
          method: "GET",
        };
      },
      providesTags: ["Dashboard"],
    }),
    calculateTransactionBuySales: builder.mutation({
      query({ type, monthChose, quarterChoose, yearChoose }) {
        return {
          url: `/calculate-transaction-buy-sales`,
          params: { type, monthChose, quarterChoose, yearChoose },
          method: "GET",
        };
      },
      providesTags: ["Dashboard"],
    }),
    calculateTransactionSellSales: builder.mutation({
      query({ type, monthChose, quarterChoose, yearChoose }) {
        return {
          url: `/calculate-transaction-sell-sales`,
          params: { type, monthChose, quarterChoose, yearChoose },
          method: "GET",
        };
      },
      providesTags: ["Dashboard"],
    }),
    calculateWithdraw: builder.mutation({
      query({ type, monthChose, quarterChoose, yearChoose }) {
        return {
          url: `/calculate-withdraw-request`,
          params: { type, monthChose, quarterChoose, yearChoose },
          method: "GET",
        };
      },
      providesTags: ["Dashboard"],
    }),
    quantityStatistics: builder.mutation({
      query() {
        return {
          url: `/quantity-statistic`,
          method: "GET",
        };
      },
      providesTags: ["Dashboard"],
      transformResponse: (response) => {
        response.data.quantityProductReviews = [
          response.data.quantityReviewOneStar,
          response.data.quantityReviewTwoStar,
          response.data.quantityReviewThreeStar,
          response.data.quantityReviewFourStar,
          response.data.quantityReviewFiveStar,
        ];
        return response;
      },
    }),
    getAnalyzeDataSet: builder.query({
      query() {
        return {
          url: `/analyze-data-set`,
          method: "GET",
        };
      },
      providesTags: ["Dashboard"],
    }),
    calculateWithdrawRequestFromAndTo: builder.mutation({
      query({ from, to }) {
        return {
          url: `/calculate-withdraw-request-from-and-to`,
          body: { from, to },
          method: "POST",
        };
      },
      providesTags: ["Dashboard"],
    }),
    calculateTransactionSellSalesFromAndTo: builder.mutation({
      query({ from, to }) {
        return {
          url: `/calculate-transaction-sell-sales-from-and-to`,
          body: { from, to },
          method: "POST",
        };
      },
      providesTags: ["Dashboard"],
    }),
    calculateTransactionBuySalesFromAndTo: builder.mutation({
      query({ from, to }) {
        return {
          url: `/calculate-transaction-buy-sales-from-and-to`,
          body: { from, to },
          method: "POST",
        };
      },
      providesTags: ["Dashboard"],
    }),
    calculateOderSalesFromAndTo: builder.mutation({
      query({ from, to }) {
        return {
          url: `/calculate-order-sales-from-and-to`,
          body: { from, to },
          method: "POST",
        };
      },
      providesTags: ["Dashboard"],
    }),
  }),
});

export const {
  useCalculateOrderSalesMutation,
  useCalculateTransactionBuySalesMutation,
  useCalculateTransactionSellSalesMutation,
  useCalculateWithdrawMutation,
  useQuantityStatisticsMutation,
  useGetAnalyzeDataSetQuery,
  useCalculateWithdrawRequestFromAndToMutation,
  useCalculateTransactionSellSalesFromAndToMutation,
  useCalculateTransactionBuySalesFromAndToMutation,
  useCalculateOderSalesFromAndToMutation,
} = dashboardApi;

export const DashBoardDateType = Object.freeze({
  DAY: "DAY",
  WEEK: "WEEK",
  MONTH: "MONTH",
  QUARTER: "QUARTER",
  YEAR: "YEAR",
});

// import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
// import { BASE_PATH } from "../../constants/constants";

// export const dashboardApi = createApi({
//   reducerPath: "dashboardApi",
//   baseQuery: fetchBaseQuery({
//     baseUrl: BASE_PATH,
//     prepareHeaders: (headers, { getState }) => {
//       const token = localStorage.getItem("token");
//       if (token) {
//         headers.set("Authorization", `Bearer ${token}`);
//       }
//       headers.set("Access-Control-Allow-Origin", `*`);
//       return headers;
//     },
//   }),
//   tagTypes: ["Dashboard"],
//   endpoints: (builder) => ({
//     calculateOrderSales: builder.mutation({
//       query({ type, monthChose, quarterChoose, yearChoose }) {
//         return {
//           url: `/calculate-order-sales`,
//           params: { type, monthChose, quarterChoose, yearChoose },
//           method: "GET",
//         };
//       },
//       providesTags: ["Dashboard"],
//     }),
//     calculateTransactionBuySales: builder.mutation({
//       query({ type, monthChose, quarterChoose, yearChoose }) {
//         return {
//           url: `/calculate-transaction-buy-sales`,
//           params: { type, monthChose, quarterChoose, yearChoose },
//           method: "GET",
//         };
//       },
//       providesTags: ["Dashboard"],
//     }),
//     calculateTransactionSellSales: builder.mutation({
//       query({ type, monthChose, quarterChoose, yearChoose }) {
//         return {
//           url: `/calculate-transaction-sell-sales`,
//           params: { type, monthChose, quarterChoose, yearChoose },
//           method: "GET",
//         };
//       },
//       providesTags: ["Dashboard"],
//     }),
//     calculateWithdraw: builder.mutation({
//       query({ type, monthChose, quarterChoose, yearChoose }) {
//         return {
//           url: `/calculate-withdraw-request`,
//           params: { type, monthChose, quarterChoose, yearChoose },
//           method: "GET",
//         };
//       },
//       providesTags: ["Dashboard"],
//     }),
//     quantityStatistics: builder.mutation({
//       query() {
//         return {
//           url: `/quantity-statistic`,
//           method: "GET",
//         };
//       },
//       providesTags: ["Dashboard"],
//       transformResponse: (response) => {
//         response.data.quantityProductReviews = [
//           response.data.quantityReviewOneStar,
//           response.data.quantityReviewTwoStar,
//           response.data.quantityReviewThreeStar,
//           response.data.quantityReviewFourStar,
//           response.data.quantityReviewFiveStar,
//         ];
//         return response;
//       },
//     }),
//     getAnalyzeDataSet: builder.query({
//       query() {
//         return {
//           url: `/analyze-data-set`,
//           method: "GET",
//         };
//       },
//       providesTags: ["Dashboard"],
//       // transformResponse: (response) => {
//       //   response.data.quantityProductReviews = [
//       //     response.data.quantityReviewOneStar,
//       //     response.data.quantityReviewTwoStar,
//       //     response.data.quantityReviewThreeStar,
//       //     response.data.quantityReviewFourStar,
//       //     response.data.quantityReviewFiveStar,
//       //   ];
//       //   return response;
//       // },
//     }),
//   }),
// });

// export const {
//   useCalculateOrderSalesMutation,
//   useCalculateTransactionBuySalesMutation,
//   useCalculateTransactionSellSalesMutation,
//   useCalculateWithdrawMutation,
//   useQuantityStatisticsMutation,
//   useGetAnalyzeDataSetQuery,
// } = dashboardApi;
// export const DashBoardDateType = Object.freeze({
//   DAY: "DAY",
//   WEEK: "WEEK",
//   MONTH: "MONTH",
//   QUARTER: "QUARTER",
//   YEAR: "YEAR",
// });
