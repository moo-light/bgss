import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { BASE_PATH } from "../../constants/constants";
import {
  removeCartItem,
  setCartItem,
  setInitialCartItems,
} from "../features/cartSlice";

export const cartApi = createApi({
  reducerPath: "cartApi",
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
  
  tagTypes: ["Carts"],
  endpoints: (builder) => ({
    getCartList: builder.mutation({
      query() {
        return {
          url: `/get-list-cart-fellow-userid`,
          params: {
            userId: localStorage.getItem("user-id"),
          },
          method: "GET",
        };
      },
      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        try {
          const { data } = await queryFulfilled;
          dispatch(setInitialCartItems(data));
        } catch {
          dispatch(setInitialCartItems([]));
        }
      },
      transformResponse: (response) => {
        response.data =
          response?.data?.map((item) => modifyCartItem(item)) ?? [];
        return response.data;
      },
      providesTags: ["Carts"],
    }),
    addToCart: builder.mutation({
      query(body) {
        if (!body) throw new Error("Empty Cart Body");
        const uid = localStorage.getItem("user-id");
        body.userId = uid;
        return {
          url: "/add-product-to-cart",
          method: "POST",
          body,
        };
      },
      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        try {
          const { data } = await queryFulfilled;
          await dispatch(setCartItem(data));
        } catch (error) {
          console.log(error);
        } finally {
        }
      },
      transformResponse: (response) => modifyCartItem(response.data),
      invalidatesTags: ["Carts"],
    }),
    updateQuantity: builder.mutation({
      query({ cartItemId, quantity }) {
        return {
          url: `/update-quantity/${cartItemId}`,
          params: {
            quantity,
          },
          method: "PUT",
        };
      },
      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        try {
          const { data } = await queryFulfilled;
          dispatch(setCartItem(data));
        } catch (error) {
          console.log(error);
        } finally {
        }
      },
      transformResponse: (response) => modifyCartItem(response.data),
      invalidatesTags: ["Carts"],
    }),
    removeCartItem: builder.mutation({
      query(cartItemId) {
        return {
          url: `/remove-product-from-cart/${cartItemId}`,
          method: "DELETE",
        };
      },
      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        try {
          const { data } = await queryFulfilled;
          if (data?.data === true) {
            dispatch(removeCartItem(args));
          }
        } catch (e) {
          console.log(e);
        }
      },
      invalidatesTags: ["Carts"],
    }),
  }),
});

export const {
  useGetCartListMutation,
  useAddToCartMutation,
  useUpdateQuantityMutation,
  useRemoveCartItemMutation,
} = cartApi;

function modifyCartItem(item) {
  return {
    ...item,
    stock: item?.product?.unitOfStock,
    name: item?.product?.productName,
  };
}
