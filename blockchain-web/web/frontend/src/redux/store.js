import { configureStore } from "@reduxjs/toolkit";

import cartReducer from "./features/cartSlice";
import liveRateReducer from "./features/liveRateSlice";
import orderReducer from "./features/orderSlice";
import themeReducer from "./features/themeSlice";
import transferR from "./features/transferSlice";
import userReducer from "./features/userSlice";

import { authApi } from "./api/authApi";
import { cartApi } from "./api/cartApi";
import { categoryProductApi } from "./api/categoryProductApi";
import { ciCardApi } from "./api/ciCardApi";
import { dashboardApi } from "./api/dashBoardApi";
import { discountApi } from "./api/discountApi";
import { orderApi } from "./api/orderApi";
import { otpApi } from "./api/otpApi";
import { paymentApi } from "./api/paymentApi";
import { postApi } from "./api/postApi";
import { postCategoryApi } from "./api/postCategoryApi";
import { productApi } from "./api/productsApi";
import { rateApi } from "./api/rateApi";
import { transactionApi } from "./api/transactionApi";
import { transferGoldApi } from "./api/transfersGoldApi";
import { typeGoldApi } from "./api/typeGoldApi";
import { userApi } from "./api/userApi";

export const store = configureStore({
  reducer: {
    auth: userReducer,
    cart: cartReducer,
    order: orderReducer,
    theme: themeReducer,
    liveRate: liveRateReducer,
    transfer: transferR,
    [productApi.reducerPath]: productApi.reducer,
    [authApi.reducerPath]: authApi.reducer,
    [userApi.reducerPath]: userApi.reducer,
    [discountApi.reducerPath]: discountApi.reducer,
    [categoryProductApi.reducerPath]: categoryProductApi.reducer,
    [otpApi.reducerPath]: otpApi.reducer,
    [ciCardApi.reducerPath]: ciCardApi.reducer,
    [orderApi.reducerPath]: orderApi.reducer,
    [cartApi.reducerPath]: cartApi.reducer,
    [paymentApi.reducerPath]: paymentApi.reducer,
    [postApi.reducerPath]: postApi.reducer,
    [postCategoryApi.reducerPath]: postCategoryApi.reducer,
    [rateApi.reducerPath]: rateApi.reducer,
    [transactionApi.reducerPath]: transactionApi.reducer,
    [dashboardApi.reducerPath]: dashboardApi.reducer,
    [typeGoldApi.reducerPath]: typeGoldApi.reducer,
    [transferGoldApi.reducerPath]: transferGoldApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat([
      productApi.middleware,
      authApi.middleware,
      userApi.middleware,
      discountApi.middleware,
      categoryProductApi.middleware,
      otpApi.middleware,
      ciCardApi.middleware,
      orderApi.middleware,
      typeGoldApi.middleware,
      cartApi.middleware,
      paymentApi.middleware,
      postCategoryApi.middleware,
      postApi.middleware,
      rateApi.middleware,
      transactionApi.middleware,
      transferGoldApi.middleware,
      dashboardApi.middleware,
    ]),
});
