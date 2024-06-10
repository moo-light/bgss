import React from "react";
import { Route } from "react-router-dom";
// import Blockchain from "../admin/Blockchain";
import Dashboard from "../admin/Dashboard";
import ListUsers from "../admin/ListUsers";
import ProcessOrder from "../admin/ProcessOrder";
import UploadImages from "../admin/ProductUpdateImages";
import UpdateUser from "../admin/UpdateUser";
import ListDiscounts from "../admin/discount/ListDiscounts";
import NewDiscount from "../admin/discount/NewDiscount";
import ManageOrders from "../admin/order/ManageOrders";
import ListCategoryPosts from "../admin/post/ListCategoryPost";
import ListPosts from "../admin/post/ListPosts";
import NewCategoryPost from "../admin/post/NewCategoryPost";
import NewPost from "../admin/post/NewPost";
import UpdateCategoryPost from "../admin/post/UpdateCategoryPost";
import UpdatePost from "../admin/post/UpdatePost";
import ListCategoryProducts from "../admin/product/ListCategoryProducts";
import ListProducts from "../admin/product/ListProducts";
import NewCategoryProduct from "../admin/product/NewCategoryProduct";
import NewProduct from "../admin/product/NewProduct";
import ProductReviews from "../admin/product/ProductReviews";
import UpdateCategoryProduct from "../admin/product/UpdateCategoryProduct";
import UpdateProduct from "../admin/product/UpdateProduct";
import ListGoldTransfer from "../admin/transfer-gold-unit/ListGoldTransfer";
import ListTypeGold from "../admin/typeGold/ListTypeGold";
import NewTypeGold from "../admin/typeGold/NewTypeGold";
import UpdateTypeGold from "../admin/typeGold/UpdateTypeGold";
import ProtectedRoute from "../auth/ProtectedRoute";
import TransactionInvoice from "../invoice-admin/TransactionInvoice";
import WithdrawInvoice from "../invoice-admin/WithdrawInvoice";

const adminRoutes = () => {
  return (
    <>
      <Route
        path="/admin/dashboard"
        element={
          <ProtectedRoute staff={true}>
            <Dashboard />
          </ProtectedRoute>
        }
      />
      {/* <Route
        path="/admin/blockchain"
        element={
          <ProtectedRoute staff={true}>
            <Blockchain />
          </ProtectedRoute>
        }
      /> */}
      <Route
        path="/admin/products"
        element={
          <ProtectedRoute staff={true}>
            <ListProducts />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/weight-conversions"
        element={
          <ProtectedRoute staff={true}>
            <ListGoldTransfer />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/categoryProducts"
        element={
          <ProtectedRoute staff={true}>
            <ListCategoryProducts />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/categoryPosts"
        element={
          <ProtectedRoute staff={true}>
            <ListCategoryPosts />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/categoryPosts/:id"
        element={
          <ProtectedRoute staff={true}>
            <UpdateCategoryPost />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/categoryProducts/new"
        element={
          <ProtectedRoute staff={true}>
            <NewCategoryProduct />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/typeGolds"
        element={
          <ProtectedRoute staff={true}>
            <ListTypeGold />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/typeGolds/:id"
        element={
          <ProtectedRoute staff={true}>
            <UpdateTypeGold />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/typeGolds/new"
        element={
          <ProtectedRoute staff={true}>
            <NewTypeGold />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/products/new"
        element={
          <ProtectedRoute staff={true}>
            <NewProduct />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/products/:id"
        element={
          <ProtectedRoute staff={true}>
            <UpdateProduct />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/categoryProducts/:id"
        element={
          <ProtectedRoute staff={true}>
            <UpdateCategoryProduct />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/products/:id/upload_images"
        element={
          <ProtectedRoute staff={true}>
            <UploadImages />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/posts"
        element={
          <ProtectedRoute staff={true}>
            <ListPosts />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/categoryPost/new"
        element={
          <ProtectedRoute staff={true}>
            <NewCategoryPost />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/posts/new"
        element={
          <ProtectedRoute staff={true}>
            <NewPost />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/categoryPosts"
        element={
          <ProtectedRoute staff={true}>
            <ListCategoryPosts />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/posts/:id"
        element={
          <ProtectedRoute staff={true}>
            <UpdatePost />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/discounts"
        element={
          <ProtectedRoute staff={true}>
            <ListDiscounts />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/discount/new"
        element={
          <ProtectedRoute staff={true}>
            <NewDiscount />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/orders"
        element={
          <ProtectedRoute staff={true}>
            <ManageOrders />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/invoice/transaction/:id"
        element={
          <ProtectedRoute staff={true}>
            <TransactionInvoice />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/invoice/withdraw/:id"
        element={
          <ProtectedRoute staff={true}>
            <WithdrawInvoice />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/orders/:id"
        element={
          <ProtectedRoute staff={true}>
            <ProcessOrder />
          </ProtectedRoute>
        }
      />
      <Route
        path="/admin/users"
        element={
          <ProtectedRoute staff={true}>
            <ListUsers />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/users/:id"
        element={
          <ProtectedRoute staff={true}>
            <UpdateUser />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin/reviews"
        element={
          <ProtectedRoute staff={true}>
            <ProductReviews />
          </ProtectedRoute>
        }
      />
    </>
  );
};

export default adminRoutes;
