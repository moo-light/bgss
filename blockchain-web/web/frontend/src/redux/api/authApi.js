import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { BASE_PATH } from "../../constants/constants";
import { setRoles } from "../features/userSlice";
import { userApi } from "./userApi";
export const authApi = createApi({
  reducerPath: "authApi",
  baseQuery: fetchBaseQuery({
    baseUrl: BASE_PATH,
    prepareHeaders: (headers, { getState }) => {
      // Use this area to add headers dynamically.
      // For example, if you need to add an 'Authorization' token:
      const token = localStorage.getItem("token");
      if (token) {
        headers.set("Authorization", `Bearer ${token}`);
      }
      headers.set("Access-Control-Allow-Origin", `*`);
      return headers;
    },
  }),
  tagTypes: ["Users", "Auth"],
  endpoints: (builder) => ({
    register: builder.mutation({
      query(body) {
        return {
          url: "/sign-up",
          method: "POST",

          body,
        };
      },
      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        try {
          const { data } = await queryFulfilled;
          if (data.status === "BAD_REQUEST") {
            throw data.message;
          }

          await dispatch(
            authApi.endpoints.login.initiate({
              username: args?.username,
              password: args?.password,
            })
          );
        } catch (error) {}
      },
      providesTags: ["Auth"],
    }),
    login: builder.mutation({
      query(body) {
        return {
          url: "/sign-in-v2",
          method: "POST",
          body,
        };
      },
      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        try {
          const { data } = await queryFulfilled;
          localStorage.setItem("token", data.token);
          localStorage.setItem("user-id", data.id.toString());
          localStorage.setItem("user-roles", data.roles.join("|"));
          dispatch(setRoles(data.roles));
          await dispatch(userApi.endpoints.getMe.initiate());
        } catch (error) {
          // console.error(error.response.data); // In ra thông báo lỗi từ error.data
        }
      },
      providesTags: ["Auth"],
    }),
    logout: builder.query({
      query: () => "/signout",
      transformResponse: (result) => {
        alert();
        return result.data;
      },
      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        try {
          const data = await queryFulfilled;

          if (data) {
            localStorage.removeItem("token");
            localStorage.removeItem("user-id");
            localStorage.removeItem("user-roles");
          }
        } catch (error) {
          console.log(error);
        }
      },
      invalidatesTags: ["Auth", "User", "Carts", "Orders"],
    }),
    getImageURL: builder.mutation({ query: (url) => ({ url, method: "GET" }) }),
  }),
});

export const {
  useLoginMutation,
  useRegisterMutation,
  useLazyLogoutQuery,
  useGetImageURLMutation,
} = authApi;
