import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { BASE_PATH } from "../../constants/constants";
import { createPhotoURL } from "../../helpers/image-handler";

export const discountApi = createApi({
  reducerPath: "discountApi",
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
  tagTypes: ["AdminDiscounts", "Discount"],
  endpoints: (builder) => ({
    getDiscounts: builder.query({
      query: () => ({
        url: "/get-all-discount-code",
      }),
      transformResponse: (response) => {
        response.data.sort((a, b) => (a.id > b.id ? -1 : 1));
        return response;
      },
      providesTags: ["AdminDiscounts"],
    }),
    createDiscount: builder.mutation({
      query: ({
        discountPercentage,
        minPrice,
        maxReduce,
        quantityMin,
        defaultQuantity,
        dateExpire,
      }) => {
        // typeof dateExpire === 'Date' &&
        dateExpire = new Date(dateExpire).toISOString();
        return {
          url: "/create-discount-code",
          method: "POST",
          body: {
            discountPercentage,
            minPrice,
            maxReduce,
            quantityMin,
            defaultQuantity,
            dateExpire,
          },
        };
      },
      invalidatesTags: ["AdminDiscounts", "Discount"],
    }),
    updateDiscount: builder.mutation({
      query: ({ id, body }) => ({
        url: `/get-discount-code-by-id?discountCodeId=${id}`,
        method: "PUT",
        body,
      }),
      invalidatesTags: ["AdminDiscounts", "Discount"],
    }),
    deleteDiscount: builder.mutation({
      query: (id) => ({
        url: `/delete-discount-code/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: ["Discount"],
    }),
    deleteMyDiscount: builder.mutation({
      query: (id) => ({
        url: `/delete-discount-code-of-user/${id}`,
        params: { userId: localStorage.getItem("user-id") },
        method: "DELETE",
      }),
      invalidatesTags: ["Discount"],
    }),
    getMyDiscount: builder.query({
      query(hide) {
        const uid = localStorage.getItem("user-id");
        return {
          url: `/get-all-discount-code-of-user-by-userId?userId=${uid}`,
          method: "GET",
        };
      },
      transformResponse: (response, meta, hide) => {
        if (!hide) {
          response.data = response.data.filter(
            (discount) => discount.available
          );
        }
        return response;
      },
      providesTags: ["Discount"],
    }),
    addToMyDiscount: builder.mutation({
      query(discountId) {
        const uid = localStorage.getItem("user-id");
        return {
          url: `/create-discount-code-of-user?discountId=${discountId}&userId=${uid}`,
          method: "POST",
        };
      },
      invalidatesTags: ["Discount"],
    }),
    getMyDiscountId: builder.mutation({
      query(discountId) {
        return {
          url: `/get-discount-code-of-user-by-id?discountCodeOfUserId=${discountId}`,
          method: "GET",
        };
      },
      providesTags: ["Discount"],
    }),
    getAllDiscount: builder.query({
      query() {
        return {
          url: `/get-all-discount-code`,
          method: "GET",
        };
      },

      providesTags: ["Discount"],
    }),
    getAllRandomDiscount: builder.query({
      query() {
        return {
          url: `/get-all-discount-code`,
          method: "GET",
        };
      },
      transformResponse: (response, meta, args) => {
        const data = response.data.filter((e) => e.expire === false);
        const seed = new Date().toDateString();
        var rand = require("random-seed").create(seed);
        var i = args;
        const randomData = [];
        if (data.length === 0) return;

        while (i !== 0) {
          i--;
          const j = rand.intBetween(0, data.length - 1);
          randomData.push(data[j]);
          data.splice(j, 1);
          if (data.length === 0) break;
        }
        response.data = randomData;
        return response;
      },
    }),
    getDiscountId: builder.mutation({
      query(discountId) {
        return {
          url: `/get-discount-code-by-id?discountCodeId=${discountId}`,
          method: "GET",
        };
      },
      providesTags: ["Discount"],
    }),
  }),
});

export const {
  useGetDiscountsQuery,
  useCreateDiscountMutation,
  useUpdateDiscountMutation,
  useDeleteDiscountMutation,
  useDeleteMyDiscountMutation,
  useAddToMyDiscountMutation,
  useGetMyDiscountQuery,
  useGetAllDiscountQuery,
  useGetAllRandomDiscountQuery,
  useGetDiscountIdMutation,
  useGetMyDiscountIdMutation,
} = discountApi;

function modifyResponse(item) {
  return {
    ...item,
    image:
      item.product.productImage && createPhotoURL(item.product.productImage),
    stock: item.product.unitOfStock,
    name: item.product.productName,
  };
}
