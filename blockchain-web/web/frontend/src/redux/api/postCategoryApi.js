import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { BASE_PATH } from "../../constants/constants";
import { createPhotoURL } from "../../helpers/image-handler";

export const postCategoryApi = createApi({
  reducerPath: "postCategoryApi",
  baseQuery: fetchBaseQuery({
    baseUrl: BASE_PATH,
    prepareHeaders: (headers, { getState }) => {
      const token = localStorage.getItem("token");
      if (token) {
        headers.set("Authorization", `Bearer ${token}`);
      }
      headers.set("Access-Control-Allow-Origin", "*");
      return headers;
    },
  }),
  endpoints: (builder) => ({
    getAllCategoryPost: builder.query({
      query() {
        return {
          url: "/show-all-category-post",
          method: "GET",
        };
      },
      invalidatesTags: ["CategoryPost"],
    }),

    getCategoryPostDetails: builder.query({
      query: (categoryPostId) => ({
        url: `/get-category-post-by-id/${categoryPostId}`,
      }),
      providesTags: ["CategoryPost"],
    }),
    createCategoryPost: builder.mutation({
      query: ({ forumID, categoryName }) => ({
        url: `/create-category-post?forumId=${1}&categoryName=${categoryName}`,
        method: "POST",
        body: { forumID, categoryName },
      }),
      invalidatesTags: ["CategoryProduct"],
    }),
    updateCategoryPost: builder.mutation({
      query({ id, body }) {
        var form = new FormData();
        form.append("categoryName", body.categoryName); // Thêm categoryName vào FormData
        return {
          url: `/update-category-post/${id}`, // Thay đổi URL theo yêu cầu
          method: "PUT",
          body: form,
        };
      },
      invalidatesTags: ["CategoryPost"],
    }),
    deleteCategoryPost: builder.mutation({
      query(categoryPostId) {
        return {
          url: `/delete-category-post/${categoryPostId}`,
          method: "DELETE",
        };
      },
    }),
  }),
});

export const {
  useGetAllCategoryPostQuery,
  useUpdateCategoryPostMutation,
  useCreateCategoryPostMutation,
  useGetCategoryPostDetailsQuery,
  useDeleteCategoryPostMutation,
} = postCategoryApi;

function modifyResponse(item) {
  return {
    ...item,
    image:
      item.product.productImage && createPhotoURL(item.product.productImage),
    stock: item.product.unitOfStock,
    name: item.product.productName,
  };
}
