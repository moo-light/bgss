import React from "react";
import { Route } from "react-router-dom";

import Home from "../Home";
import ForgotPassword from "../auth/ForgotPassword";
import Login from "../auth/Login";
import ProtectedRoute from "../auth/ProtectedRoute";
import Register from "../auth/Register";
import ResetPassword from "../auth/ResetPassword";
import TradeOrder from "../buy-n-sell/TradeOrder";
import Cart from "../cart/Cart";
import ConfirmOrder from "../cart/ConfirmOrder";
import PaymentMethod from "../cart/PaymentMethod";
import Shipping from "../cart/Shipping";
import ForumPage from "../forum/ForumPage";
import PostDetails from "../forum/PostDetails";
import Invoice from "../invoice/Invoice";
import TransactionInvoice from "../invoice/TransactionInvoice";
import WithdrawInvoice from "../invoice/WithdrawInvoice";
import MyOrders from "../order/MyOrders";
import OrderDetails from "../order/OrderDetails";
import ProductDetails from "../product/ProductDetails";
import ProductListPage from "../product/ProductListPage";
import ListHistoryDeposit from "../user/HistoryDeposit";
import Profile from "../user/Profile";
import UpdatePassword from "../user/UpdatePassword";
import UpdateProfile from "../user/UpdateProfile";
import UserDiscount from "../user/UserDiscount";
import VerifyCIForm from "../user/VerifyCIForm";

const userRoutes = () => {
  return (
    <>
      <Route path="/" element={<Home />} />

      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/password/forgot" element={<ForgotPassword />} />
      <Route path="/password/reset/:token" element={<ResetPassword />} />

      <Route
        path="/me/profile"
        element={
          <ProtectedRoute>
            <Profile />
          </ProtectedRoute>
        }
      />
      <Route
        path="/products"
        element={
          <>
            
            <ProductListPage />
          </>
        }
      />
      <Route path="/product/:id" element={<ProductDetails />} />
      <Route path="/forums" element={<ForumPage />} />
      <Route path="/forums/:id" element={<PostDetails />} />

      <Route
        path="/me/update_profile"
        element={
          <ProtectedRoute>
            <UpdateProfile />
          </ProtectedRoute>
        }
      />

      <Route
        path="/me/verifyCICard"
        element={
          <ProtectedRoute>
            <VerifyCIForm />
          </ProtectedRoute>
        }
      />
      <Route
        path="/me/discounts"
        element={
          <ProtectedRoute>
            <UserDiscount />
          </ProtectedRoute>
        }
      />
      <Route
        path="/me/historyDeposit"
        element={
          <ProtectedRoute>
            <ListHistoryDeposit />
          </ProtectedRoute>
        }
      />
      <Route
        path="/me/update_password"
        element={
          <ProtectedRoute>
            <UpdatePassword />
          </ProtectedRoute>
        }
      />

      <Route
        path="/cart"
        element={
          <ProtectedRoute>
            <Cart />
          </ProtectedRoute>
        }
      />
      <Route
        path="/shipping"
        element={
          <ProtectedRoute>
            <Shipping />
          </ProtectedRoute>
        }
      />
      <Route
        path="/confirm_order"
        element={
          <ProtectedRoute>
            <ConfirmOrder />
          </ProtectedRoute>
        }
      />
      <Route
        path="/trade_order"
        element={
          <ProtectedRoute>
            <TradeOrder />
          </ProtectedRoute>
        }
      />
      <Route
        path="/payment_method"
        element={
          <ProtectedRoute>
            <PaymentMethod />
          </ProtectedRoute>
        }
      />

      <Route
        path="/me/orders"
        element={
          <ProtectedRoute>
            <MyOrders />
          </ProtectedRoute>
        }
      />

      <Route
        path="/me/order/:id"
        element={
          <ProtectedRoute>
            <OrderDetails />
          </ProtectedRoute>
        }
      />

      <Route
        path="/invoice/order/:id"
        element={
          <ProtectedRoute>
            <Invoice />
          </ProtectedRoute>
        }
      />
      <Route
        path="/invoice/transaction/:id"
        element={
          <ProtectedRoute>
            <TransactionInvoice />
          </ProtectedRoute>
        }
      />
      <Route
        path="/invoice/withdraw/:id"
        element={
          <ProtectedRoute>
            <WithdrawInvoice />
          </ProtectedRoute>
        }
      />
    </>
  );
};

export default userRoutes;
