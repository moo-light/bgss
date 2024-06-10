import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { BASE_PATH } from "../../constants/constants";

export const rateApi = createApi({
  reducerPath: "rateApi",
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
  tagTypes: ["Rate"],
  //TODO: DO Cart due tomorrownpm
  endpoints: (builder) => ({
    getAllRate: builder.query({
      query({ userId, search, postId, showHiding = false }) {
        return {
          url: `/show-all-rate`,
          params: { userId, search, postId, showHiding },
          method: "GET",
        };
      },
      transformResponse: (result) => {
        result.data = result?.data?.map((item) => modifyResponse(item));
        return result;
      },
      providesTags: ["Rate"],
    }),
    getRateDetails: builder.query({
      query(id) {
        return {
          url: `/get-rate-by-id/${id}`,
          method: "GET",
        };
      },
      transformResponse: (result) => {
        result.data = modifyResponse(result?.data);
        return result;
      },
      providesTags: ["Rate"],
    }),
    submitRate: builder.mutation({
      query(body) {
        const uid = localStorage.getItem("user-id");
        var form = new FormData();
        form.append("userId", uid);
        form.append("postId", body.postId);
        form.append("content", body.content);
        body.imageRate && form.append("imageRate", body.imageRate);
        return {
          url: "/create-rate",
          method: "POST",
          body: form,
        };
      },
      invalidatesTags: ["Product", "Rate"],
    }),
    editRate: builder.mutation({
      query(body) {
        var form = new FormData();
        form.append("userId", body.userId);
        form.append("postId", body.postId);
        form.append("content", body.content);
        body.imageRate && form.append("imageRate", body.imageRate);
        return {
          url: `/update-rate/${body.id}`,
          method: "PUT",
          body: form,
        };
      },
      invalidatesTags: ["Product", "Rate"],
    }),
    deleteRate: builder.mutation({
      query(body) {
        var form = new FormData();
        form.append("userId", body.userId);
        form.append("postId", body.postId);
        return {
          url: `/delete-rate/${body.id}`,
          method: "DELETE",
          body: form,
        };
      },
      invalidatesTags: ["Product", "Rate"],
    }),
  }),
});

export const {
  useGetAllRateQuery,
  useLazyGetAllRateQuery,
  useGetRateDetailsQuery,
  useDeleteRateMutation,
  useEditRateMutation,
  useSubmitRateMutation,
  useUpdateReviewRateMutation,
  useGetUserReviewQuery,
} = rateApi;

function modifyResponse(item) {
  return {
    ...item,
    // textImg: getServerImgUrl(item?.textImg),
    // user: {
    //   ...item?.user?.userInfo,
    //   imgUrl: getServerImgUrl(item?.user?.userInfo?.imgUrl),
    // },
  };
}
