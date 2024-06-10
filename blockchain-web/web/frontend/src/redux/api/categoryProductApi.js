import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { BASE_PATH } from "../../constants/constants";

export const categoryProductApi = createApi({
  reducerPath: "categoryProductApi",
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
  tagTypes: ["Categories"],
  endpoints: (builder) => ({
    getCategoryProducts: builder.query({
      query: () => ({
        url: "/get-all-category",
      }),
      providesTags: ["Categories"],
    }),
    getCategoryDetails: builder.query({
      query: (id) => ({
        url: `/get-category-by-id/${id}`,
      }),
      providesTags: ["Categories"],
    }),
    createCategoryProduct: builder.mutation({
      query: (categoryName) => ({
        url: `/create-product-category?categoryName=${categoryName}`,
        method: "POST",
        body: { categoryName },
      }),
      invalidatesTags: ["CategoryProduct"],
    }),
    updateCategoryProduct: builder.mutation({
      query({ id, body }) {
        const formData = new FormData();
        // Thêm các trường từ body vào formData
        for (const key in body) {
          formData.append(key, body[key]);
        }

        return {
          url: `/update-product-category/${id}`,
          method: "PUT",
          body: formData, // Sử dụng formData thay vì body dạng object
        };
      },
      invalidatesTags: ["Categories"],
    }),
    deleteCategoryProduct: builder.mutation({
      query: (id) => ({
        url: `/delete-product-category/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: ["Categories"],
    }),
  }),
});

export const {
  useGetCategoryProductsQuery,
  useCreateCategoryProductMutation,
  useUpdateCategoryProductMutation,
  useDeleteCategoryProductMutation,
  useGetCategoryDetailsQuery,
} = categoryProductApi;
