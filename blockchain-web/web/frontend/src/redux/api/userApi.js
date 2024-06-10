import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { BASE_AVATAR, BASE_PATH } from "../../constants/constants";
import { getServerImgUrl } from "../../helpers/image-handler";
import { setIsAuthenticated, setLoading, setUser } from "../features/userSlice";
import { authApi } from "./authApi";
import { cartApi } from "./cartApi";

export const userApi = createApi({
  reducerPath: "userApi",
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
  tagTypes: ["User", "AdminUsers", "AdminUser"],

  endpoints: (builder) => ({
    getMe: builder.query({
      query: () => {
        const id = localStorage.getItem("user-id");
        if (!id) return;
        return `/show-user-info/${id}`;
      },
      transformResponse: (result) => {
        result.data = modifyResponse(result.data);
        return result.data;
      },
      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        try {
          const result = await queryFulfilled;
          const data = result?.data;
          if (!data) return;
          const user = data;
          dispatch(setUser(user));
          dispatch(setIsAuthenticated(true));
          const roles = localStorage.getItem("user-roles");
          if (roles?.includes("ROLE_CUSTOMER"))
            await dispatch(cartApi.endpoints.getCartList.initiate());
        } catch (error) {
          console.log(error);
        } finally {
          dispatch(setLoading(false));
        }
      },
      providesTags: ["User", "Auth"],
    }),
    updateProfile: builder.mutation({
      query: (body) => {
        const id = localStorage.getItem("user-id");
        return {
          url: `/update-user-info/${id}`,
          method: "PUT",
          body,
        };
      },
      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        try {
          await queryFulfilled;
          dispatch(authApi.getMe());
        } catch (e) {
          console.log(e?.error?.data ?? e);
        }
      },
      invalidatesTags: ["User", "Auth"],
    }),
    uploadAvatar: builder.mutation({
      query: (body) => {
        // Assuming `file` is the image file to upload
        const formData = new FormData();
        const id = localStorage.getItem("user-id");
        formData.append("imageData", body.avatar, `user${id}_avatar`);
        return {
          url: `/update-avatar/${id}`,
          method: "PUT",
          body: formData,
        };
      },
      invalidatesTags: ["User", "Auth"],
    }),
    updatePassword: builder.mutation({
      query(body) {
        const id = localStorage.getItem("user-id");
        return {
          url: `/user/change-password/${id}?oldPwd=${body.oldPwd}&newPwd=${body.newPwd}&confirmNewPwd=${body.confirmNewPwd}`,
          method: "PUT",
        };
      },
    }),
    forgotPassword: builder.mutation({
      query(body) {
        return {
          url: "/password/forgot",
          method: "POST",
          body,
        };
      },
    }),
    resetPassword: builder.mutation({
      query({ token, body }) {
        return {
          url: `/password/reset/${token}`,
          method: "PUT",
          body,
        };
      },
    }),
    getHistoryDeposit: builder.query({
      query() {
        const id = localStorage.getItem("user-id");
        return {
          url: `/history-deposit/${id}`,
          method: "GET",
        };
      },
    }),
    getAdminUsers: builder.query({
      query: () => `/show-list-user`,
      transformResponse: (result) => {
        if (result.data) result.data = result.data.map(modifyResponse);
        return result;
      },
      providesTags: ["AdminUsers"],
    }),
    getUserDetails: builder.query({
      query: (id) => `/user/get-user/${id}`,

      providesTags: ["AdminUser"],
    }),
    lockUser: builder.mutation({
      query({ id, body }) {
        return {
          url: `/user/lock-user/${id}`,
          method: "PUT",
          params: body,
        };
      },
      invalidatesTags: ["AdminUsers", "AdminUser"],
    }),
    deleteUser: builder.mutation({
      query(id) {
        return {
          url: `/admin/users/${id}`,
          method: "DELETE",
        };
      },
      invalidatesTags: ["AdminUsers"],
    }),
  }),
});
function modifyResponse(user) {
  if (!user) return null;
  const userInfo = user?.userInfo;
  if (userInfo?.avatarData) {
    userInfo.avatarUrl = getServerImgUrl(
      userInfo?.avatarData?.imgUrl,
      BASE_AVATAR
    );
  }
  return {
    ...user,
    userInfo: {
      ...user?.userInfo,
      avatarUrl: userInfo?.avatarUrl || BASE_AVATAR,
    },
    name:
      !!user?.userInfo &&
      user?.userInfo?.firstName + " " + user?.userInfo?.lastName,
    balance: user?.userInfo?.balance?.amount,
  };
}
export const {
  useGetMeQuery,
  useUpdateProfileMutation,
  useUploadAvatarMutation,
  useUpdatePasswordMutation,
  useForgotPasswordMutation,
  useResetPasswordMutation,
  useGetAdminUsersQuery,
  useGetUserDetailsQuery,
  useGetHistoryDepositQuery,
  useLockUserMutation,
  useDeleteUserMutation,
} = userApi;
