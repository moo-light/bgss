import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { BASE_PATH, BASE_PRODUCTIMG } from "../../constants/constants";
import { cleanRequestParams } from "../../helpers/helpers";
import { getServerImgUrl } from "../../helpers/image-handler";

export const productApi = createApi({
  reducerPath: "productApi",
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
  tagTypes: ["Product", "AdminProducts", "Reviews", "Categories"],
  endpoints: (builder) => ({
    getProducts: builder.query({
      query: (params) => ({
        url: "/product/show-list-product",
        params: cleanRequestParams({
          page: params?.page,
          search: params?.keyword,
          asc: params?.asc,
          category: params?.category,
          typeGold: params?.typeGold,
          price_gte: params?.min,
          price_lte: params?.max,
          reviews: params?.reviews,
        }),
      }),
      transformResponse: (response) => {
        // Modify the data as needed
        const modifiedData = response.data?.map(modifyProduct) ?? [];
        response.data = modifiedData;
        // Return the modified data
        return response;
      },
      async onQueryStarted(args, { queryFulfilled, dispatch }) {
        try {
          await queryFulfilled;
        } catch (error) {
          console.log(error);
        }
      },
      providesTags: ["Product", "AdminProducts"],
    }),
    getProductDetails: builder.query({
      query: (id) => `/product/get-product-by-id/${id}`,
      transformResponse: (response) => {
        const product = modifyProduct(response?.data);
        return {
          ...response,
          data: product,
        };
      },
      providesTags: ["Product", "AdminProducts"],
    }),

    createReview: builder.mutation({
      query({ numOfReviews, productId, content, imgReview }) {
        var uid = localStorage.getItem("user-id");
        var formData = new FormData();
        formData.append("numOfReviews", numOfReviews);
        formData.append("content", content);
        imgReview && formData.append("imgReview", imgReview);
        return {
          url: `/create-review?userId=${uid}&productId=${productId}`,
          method: "POST",
          body: formData,
        };
      },
      invalidatesTags: ["Products", "UserReviews", "Reviews"],
    }),
    canUserReview: builder.query({
      query: ({ productId }) => {
        const userId = localStorage.getItem("user-id");
        return {
          url: `/product/${productId}/check-review`,
          method: "GET",
          params: {
            userId,
            productId,
          },
        };
      },
      providesTags: ["Product", "AdminProducts"],
    }),
    updateReview: builder.mutation({
      query({ numOfReviews, productId, content, imgReview }) {
        var uid = localStorage.getItem("user-id");
        var formData = new FormData();
        formData.append("numOfReviews", numOfReviews);
        formData.append("content", content);
        imgReview && formData.append("imgReview", imgReview);
        return {
          url: `/update-review?userId=${uid}&productId=${productId}`,
          method: "PUT",
          body: formData,
        };
      },
      providesTags: ["Products"],
    }),
    deleteReviewProduct: builder.mutation({
      query: (reviewProductId) => {
        var uid = localStorage.getItem("user-id");
        return {
          url: `delete-review/${reviewProductId}`,
          method: "DELETE",
          params: {
            userId: uid,
          },
        };
      },
      invalidatesTags: ["Reviews", "UserReviews"],
    }),

    getAdminProducts: builder.query({
      query: ({ typeOptionName }) => {
        if (typeOptionName === "CRAFT") {
          return `/product/show-list-product?typeOptionName=${typeOptionName}`;
        } else {
          return `/product/show-list-product`;
        }
      },
      transformResponse(response) {
        let products = response?.data ?? [];
        products = products.map(modifyProduct);
        return {
          ...response,
          data: products,
        };
      },
      providesTags: ["AdminProducts"],
    }),
    createProduct: builder.mutation({
      query(body) {
        return {
          url: "/add-product",
          method: "POST",
          body,
        };
      },
      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        try {
          const { data } = await queryFulfilled;
          const id = data?.data?.id;
          if (id) {
            await dispatch(
              productApi.endpoints.uploadProductImages.initiate({
                id,
                body: {
                  image: args.product_image,
                },
              })
            );
          }
        } catch (error) {
          console.log(error);
        }
      },
      invalidatesTags: ["Product", "AdminProducts"],
    }),

    updateProduct: builder.mutation({
      query({ id, body }) {
        return {
          url: `/product/update-product/${id}`,
          method: "PUT",
          body,
        };
      },
      async onQueryStarted(args, { dispatch, queryFulfilled }) {
        try {
          const { data } = await queryFulfilled;
          const id = data?.data?.id;
          if (id) {
            await dispatch(
              productApi.endpoints.uploadProductImages.initiate({
                id,
                body: {
                  image: args.body.product_image,
                },
              })
            );
          }
        } catch (error) {
          console.log(error);
        }
      },
      invalidatesTags: ["Product", "AdminProducts"],
    }),
    uploadProductImages: builder.mutation({
      query({ id, body }) {
        var bodyFormData = new FormData();
        if (Array.isArray(body.image)) {
          body.image.forEach((image) => {
            if (typeof image === "string") {
              bodyFormData.append("oldImgs", image);
              bodyFormData.append("indexes", "URL");
            } else {
              bodyFormData.append("newImgs", image);
              bodyFormData.append("indexes", "FILE");
            }
          });
          // bodyFormData.append("oldImgs", body.image.filter(image=> typeof image === "string"));
          // bodyFormData.append("newImgs", body.image.filter(image=> typeof image !== "string"));
        } else {
          bodyFormData.append("img", body.image);
        }
        return {
          url: `/product/update-product-image/${id}`,
          method: "PUT",
          body: bodyFormData,
        };
      },
      invalidatesTags: ["Product", "AdminProducts"],
    }),
    deleteProductImage: builder.mutation({
      query({ id, body }) {
        return {
          url: `/admin/products/${id}/delete_image`,
          method: "PUT",
          body,
        };
      },
      invalidatesTags: ["Product", "AdminProducts"],
    }),
    deleteProduct: builder.mutation({
      query(id) {
        return {
          url: `/product/delete-product/${id}`,
          method: "DELETE",
        };
      },
      invalidatesTags: ["AdminProducts"],
    }),
    getUserReview: builder.query({
      query: ({ productId }) => {
        const uid = localStorage.getItem("user-id");
        return `/product/${productId}/get-user-review/${uid}`;
      },
      transformResponse: (response) => {
        response.data = modifyReview(response.data);
        return response;
      },
      providesTags: ["UserReviews"],
    }),
    getProductReviews: builder.query({
      // query: (productId) => `/product/${productId}/get-all-review-all-product`,
      query: (productId) => `/product/${productId}/get-all-review`,
      providesTags: ["Reviews"],
      transformResponse: (response) => {
        response.data = response.data.map(modifyReview);
        return response;
      },
    }),
    getAdminProductReviews: builder.query({
      // query: (productId) => `/product/${productId}/get-all-review-all-product`,
      query: () => `/get-all-review-all-product`,
      providesTags: ["Reviews"],
      transformResponse: (response) => {
        response.data = response.data.map(modifyReview);
        return response;
      },
    }),
    getReviews: builder.query({
      query: ({ productId }) => {
        return {
          url: `/product/${productId}/get-all-review`,
          params: { productId },
        };
      },
      providesTags: ["Reviews"],
      transformResponse: (response) => {
        response.data = response.data.map(modifyReview);
        return response;
      },
    }),
    deleteReview: builder.mutation({
      query({ productId, id }) {
        return {
          url: `/admin/reviews?productId=${productId}&id=${id}`,
          method: "DELETE",
        };
      },
      invalidatesTags: ["Reviews", "UserReviews"],
    }),
    getCategories: builder.query({
      query: () => `/get-all-category`,
      invalidatesTags: ["Categories"],
    }),
  }),
});

export const {
  useGetProductsQuery,
  useGetProductDetailsQuery,
  useCreateReviewMutation,
  useCanUserReviewQuery,
  useGetUserReviewQuery,
  useUpdateReviewMutation,
  useDeleteReviewProductMutation,
  useGetAdminProductsQuery,
  useCreateProductMutation,
  useUpdateProductMutation,
  useUploadProductImagesMutation,
  useDeleteProductImageMutation,
  useDeleteProductMutation,
  useLazyGetProductReviewsQuery,
  useLazyGetReviewsQuery,
  useLazyGetAdminProductReviewsQuery,
  useDeleteReviewMutation,
  useGetCategoriesQuery,
} = productApi;
function modifyReview(review) {
  return {
    ...review,
    imgUrl: getServerImgUrl(review.imgUrl, null),
  };
}

function modifyProduct(product) {
  if (product == null) return null;

  const modifiedProduct = {
    ...product,
    productImages:
      product?.productImages?.map((p) => getServerImgUrl(p.imgUrl)) ?? [],
    // productImages: [
    //   getServerImgUrl(mockImgUrl),
    //   getServerImgUrl(mockImgUrl),
    //   getServerImgUrl(mockImgUrl),
    // ],
    // imgUrl: !!product?.imgUrl ? `${getServerImgUrl(product.imgUrl)}` : null,

    // imgUrl: `${getServerImgUrl(mockImgUrl)}`,
    numOfReviews: product.numOfReviews ?? 0,
  };
  modifiedProduct.imgUrl = modifiedProduct?.productImages[0] ?? BASE_PRODUCTIMG;
  return modifiedProduct;
}
