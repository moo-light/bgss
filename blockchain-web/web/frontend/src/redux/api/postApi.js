import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { BASE_PATH } from "../../constants/constants";
import { getServerImgUrl } from "../../helpers/image-handler";

export const postApi = createApi({
  reducerPath: "postApi",
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
  tagTypes: ["Posts"],
  endpoints: (builder) => ({
    getAllPost: builder.query({
      query(params) {
        return {
          url: `/get-all-post`,
          params,
          method: "GET",
        };
      },
      providesTags: ["Posts"],
      transformResponse: (result) => {
        result.data = result?.data?.map((item) => modifyResponse(item));
        return result;
      },
    }),
    getPostDetails: builder.query({
      query(id) {
        return {
          url: `/get-post-by-id/${id}`,
          method: "GET",
        };
      },
      providesTags: ["Posts"],
      transformResponse: (result) => {
        result.data = modifyResponse(result?.data);
        return result;
      },
    }),
    deletePost: builder.mutation({
      query(id) {
        return {
          url: `/delete-post/${id}`,
          method: "DELETE",
        };
      },
      invalidatesTags: ["Posts"],
    }),
    createPost: builder.mutation({
      query(body) {
        var uid = localStorage.getItem("user-id");
        var form = new FormData();
        form.append("title", body.title);
        form.append("content", body.content);
        form.append("categoryPostId", body.categoryPostId);
        form.append("pinned", body.pinned);
        body?.textImg && form.append("imgUrl", body?.textImg);
        return {
          url: `/create-post?userId=${uid}`,
          method: "POST",
          body: form,
        };
      },
      invalidatesTags: ["Posts"],
      onQueryStarted: async (args, { dispatch, queryFulfilled }) => {
        try {
          await queryFulfilled;
          dispatch(postApi.endpoints.getAllPost.initiate());
        } catch (e) {}
      },
    }),
    updatePost: builder.mutation({
      query({ id, body }) {
        var form = new FormData();
        form.append("title", body.title);
        form.append("content", body.content);
        form.append("categoryPostId", body.categoryPostId);
        form.append("pinned", body.pinned);
        body?.textImg && form.append("imgUrl", body?.textImg);
        return {
          url: `/update-post/${id}`,
          method: "PUT",
          body: form,
        };
      },
      invalidatesTags: ["Posts"],
    }),
    getCategories: builder.query({
      query() {
        return {
          url: `/get-categories`,
          method: "GET",
        };
      },
    }),
  }),
});

export const {
  useGetAllPostQuery,
  useGetPostDetailsQuery,
  useDeletePostMutation,
  useUpdatePostMutation,
  useCreatePostMutation,
  useGetCategoriesQuery,
} = postApi;

function modifyResponse(item) {
  return {
    ...item,
    textImg: getServerImgUrl(item?.textImg),
    user: {
      ...item?.user?.userInfo,
      imgUrl: getServerImgUrl(item?.user?.userInfo?.imgUrl),
    },
  };
}
