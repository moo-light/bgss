import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { BASE_PATH } from "../../constants/constants";

export const ciCardApi = createApi({
  reducerPath: "ciCardApi",
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
  endpoints: (builder) => ({
    verifyCiCardImage: builder.mutation({
      query: ({ frontImage, backImage }) => {
        const userInfoId = localStorage.getItem("user-id");
        const formData = new FormData();
        formData.append("frontImage", frontImage);
        formData.append("backImage", backImage);

        return {
          url: `/verify/ciCard/image-verify/${userInfoId}`,
          method: "POST",
          body: formData,
        };
      },
    }),
    showSecretKey: builder.query({
      query: (userInfoId) => ({
        url: `/show-secret-key/${userInfoId}`,
        method: "POST",
      }),
    }),
  }),
});

export const { useVerifyCiCardImageMutation, useShowSecretKeyQuery } =
  ciCardApi;
