import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { BASE_PATH } from "../../constants/constants";
import { clearCart } from "../features/cartSlice";
import { setCurrentOrder } from "../features/orderSlice";
import { authApi } from "./authApi";
import { cartApi } from "./cartApi";

export const orderApi = createApi({
  reducerPath: "orderApi",
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
  tagTypes: [
    "Order",
    "AdminOrders",
    "Transaction",
    "AdminTransaction",
    "LiveRate",
    "Withdraw",
  ],
  endpoints: (builder) => ({
    createNewOrder: builder.mutation({
      query(body) {
        const uid = localStorage.getItem("user-id");
        return {
          url: `/create-order/${uid}`,
          method: "POST",
          body,
        };
      },
      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        try {
          const { data } = await queryFulfilled;
          dispatch(setCurrentOrder(data?.data));
          dispatch(clearCart());
          dispatch(authApi.util.invalidateTags(["Users", "Auth"]));
          dispatch(cartApi.util.invalidateTags(["Carts"]));
        } catch (error) {
          console.log(error);
        } finally {
        }
      },
      transformResponse: (response) => {
        response.data = modifyOrderItem(response.data);
        let order = response.data;
        let currentDate = new Date();
        currentDate.setMinutes(currentDate.getMinutes() + 15);
        order.expirer = currentDate;
        return response;
      },
      invalidatesTags: ["Carts", "Order"],
    }),
    myOrders: builder.query({
      query: () => `/get-order-list?userId=${localStorage.getItem("user-id")}`,
      transformResponse: (response) => {
        response.data =
          response?.data
            ?.map((item) => modifyOrderItem(item))
            .sort((x) => x.id) ?? [];
        return response.data;
      },
      providesTags: ["Order"],
    }),
    orderDetails: builder.query({
      query: (id) => `/get-order-by-id/${id}`,
      providesTags: ["Order"],
    }),
    findOrderByQrCode: builder.query({
      query: (qrCode) => `/search-order-by-qr_code?qr_Code=${qrCode}`,
      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        try {
          const { data } = await queryFulfilled;
          orderApi.util.invalidateTags("Order");
          orderApi.util.invalidateTags("AdminOrders");
          orderApi.endpoints.orderDetails.initiate(data.data.id);
        } catch (error) {
          console.log(error);
        }
      },
      invalidatesTags: ["AdminOrders", "Order"],
    }),
    stripeCheckoutSession: builder.mutation({
      query(body) {
        return {
          url: "/payment/checkout_session",
          method: "POST",
          body,
        };
      },
    }),
    getDashboardSales: builder.query({
      query: ({ startDate, endDate }) =>
        `/admin/get_sales/?startDate=${startDate}&endDate=${endDate}`,
    }),
    getAdminOrders: builder.query({
      query: (params) => {
        return {
          url: `/get-order-list`,
          params: {
            userId: params?.userId,
            startDate: params?.startDate,
            endDate: params?.endDate,
          },
        };
      },
      transformResponse: (response) => {
        try {
          response = {
            ...response,
            orders: response.data,
          };
          return response;
        } catch (error) {
          return error;
        }
      },
      providesTags: ["AdminOrders"],
      invalidatesTags: [
        "Transaction",
        "AdminTransaction",
        "LiveRate",
        "Withdraw",
      ],
    }),
    updateOrder: builder.mutation({
      query({ id, body }) {
        return {
          url: `update-status-received/${id}`,
          method: "PUT",
        };
      },
      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        await queryFulfilled;
        dispatch(authApi.util.invalidateTags(["Users", "Auth"]));
      },
      invalidatesTags: ["AdminOrders", "Order"],
    }),
    orderRecieved: builder.mutation({
      query(id) {
        return {
          url: `/user-confirm/${id}`,
          method: "PUT",
        };
      },
      invalidatesTags: ["AdminOrders", "Order"],
    }),
    deleteOrder: builder.mutation({
      query(id) {
        return {
          url: `/admin/orders/${id}`,
          method: "DELETE",
        };
      },
      invalidatesTags: ["AdminOrders", "Order"],
    }),
  }),
});

export const {
  useCreateNewOrderMutation,
  useStripeCheckoutSessionMutation,
  useMyOrdersQuery,
  useLazyFindOrderByQrCodeQuery,
  useOrderDetailsQuery,
  useLazyGetDashboardSalesQuery,
  useGetAdminOrdersQuery,
  useOrderRecievedMutation,
  useUpdateOrderMutation,
  useDeleteOrderMutation,
} = orderApi;

function modifyOrderItem(order) {
  return order;
}
