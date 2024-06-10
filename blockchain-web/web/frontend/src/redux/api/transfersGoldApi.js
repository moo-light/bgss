import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { BASE_PATH } from "../../constants/constants";
import { createConversionFactors } from "../features/transferSlice";

export const transferGoldApi = createApi({
  reducerPath: "transferGoldApi",
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
  tagTypes: ["TransferGolds"],
  endpoints: (builder) => ({
    getTransferGold: builder.query({
      query: () => ({
        url: "/get-all-information-transfer",
      }),
      onQueryStarted: async (args, { dispatch, queryFulfilled }) => {
        try {
          const { data } = await queryFulfilled;
          if (data.data) {
            dispatch(createConversionFactors(data.data));
          }
        } catch (error) {
          console.log(error);
        }
      },
      providesTags: ["TransferGolds"],
      transformResponse: (result) => {
        result?.data?.sort((current, item) => (current.id < item.id ? -1 : 1));
        return result;
      },
    }),
    createTransferGold: builder.mutation({
      query: (newTransfer) => ({
        url: "/create-information-transfer",
        method: "POST",
        body: newTransfer,
      }),
      invalidatesTags: ["TransferGolds"],
    }),
    updateTransferGold: builder.mutation({
      query: ({ id, ...updateTransfer }) => ({
        url: `/update-information-transfer/${id}`,
        method: "PUT",
        body: updateTransfer,
      }),
      invalidatesTags: ["TransferGolds"],
    }),
    deleteTransferGold: builder.mutation({
      query: (id) => ({
        url: `/delete-information-transfer/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: ["TransferGolds"],
    }),
  }),
});

export const {
  useGetTransferGoldQuery,
  useCreateTransferGoldMutation,
  useUpdateTransferGoldMutation,
  useDeleteTransferGoldMutation,
} = transferGoldApi;
