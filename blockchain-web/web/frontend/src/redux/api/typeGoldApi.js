import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { BASE_PATH } from "../../constants/constants";

export const typeGoldApi = createApi({
  reducerPath: "typeGoldApi",
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
  tagTypes: ["TypeGolds"],
  endpoints: (builder) => ({
    getTypeGold: builder.query({
      query: () => ({
        url: "/get-all-type-gold",
      }),
      providesTags: ["TypeGolds"],
    }),
    getTypeGoldId: builder.query({
      query: (id) => ({
        url: `/get-type-gold-id/${id}`,
      }),
      providesTags: ["TypeGolds"],
    }),
    createTypeGold: builder.mutation({
      query: ({ typeName, price, goldUnit }) => ({
        url: `/create-type-gold`,
        method: "POST",
        body: { typeName, price, goldUnit },
      }),
      invalidatesTags: ["TypeGolds"],
    }),

    updateTypeGold: builder.mutation({
      query({ id, body }) {
        return {
          url: `/update-type-gold/${id}`,
          method: "PUT",
          body: body, // Sử dụng body dạng JSON
          headers: {
            "Content-Type": "application/json",
          },
        };
      },
      invalidatesTags: ["TypeGolds"],
    }),

    deleteTypeGold: builder.mutation({
      query: (id) => ({
        url: `/delete-type-gold-id/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: ["TypeGolds"],
    }),

    getStatisticProductByTypeGoldId: builder.query({
      query: ({ typeGoldId, goldOptionType }) => ({
        url: `/statistic-product-by-type-gold`,
        params: {
          typeGoldId,
          goldOptionType,
        },
      }),
      providesTags: ["TypeGolds"],
    }),
  }),
});

export const {
  useGetTypeGoldQuery,
  useCreateTypeGoldMutation,
  useUpdateTypeGoldMutation,
  useDeleteTypeGoldMutation,
  useGetTypeGoldIdQuery,
  useGetStatisticProductByTypeGoldIdQuery,
} = typeGoldApi;
