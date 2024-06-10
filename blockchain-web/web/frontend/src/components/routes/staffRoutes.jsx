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
import ListOrders from "../admin/order/ManageOrders";
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
import ProtectedRoute from "../auth/ProtectedRoute";

const staffRoutes = () => {
  return (
    <>
      <Route
        path="/staff/dashboard"
        element={
          <ProtectedRoute staff={true}>
            <Dashboard />
          </ProtectedRoute>
        }
      />
      {/* <Route
        path="/staff/blockchain"
        element={
          <ProtectedRoute staff={true}>
            <Blockchain />
          </ProtectedRoute>
        }
      /> */}
      <Route
        path="/staff/products"
        element={
          <ProtectedRoute staff={true}>
            <ListProducts />
          </ProtectedRoute>
        }
      />

      <Route
        path="/staff/categoryProducts"
        element={
          <ProtectedRoute staff={true}>
            <ListCategoryProducts />
          </ProtectedRoute>
        }
      />
      <Route
        path="/staff/categoryPosts"
        element={
          <ProtectedRoute staff={true}>
            <ListCategoryPosts />
          </ProtectedRoute>
        }
      />
      <Route
        path="/staff/categoryPosts/:id"
        element={
          <ProtectedRoute staff={true}>
            <UpdateCategoryPost />
          </ProtectedRoute>
        }
      />
      <Route
        path="/staff/categoryProducts/new"
        element={
          <ProtectedRoute staff={true}>
            <NewCategoryProduct />
          </ProtectedRoute>
        }
      />
      <Route
        path="/staff/product/new"
        element={
          <ProtectedRoute staff={true}>
            <NewProduct />
          </ProtectedRoute>
        }
      />
      <Route
        path="/staff/products/:id"
        element={
          <ProtectedRoute staff={true}>
            <UpdateProduct />
          </ProtectedRoute>
        }
      />
      <Route
        path="/staff/categoryProducts/:id"
        element={
          <ProtectedRoute staff={true}>
            <UpdateCategoryProduct />
          </ProtectedRoute>
        }
      />
      <Route
        path="/staff/products/:id/upload_images"
        element={
          <ProtectedRoute staff={true}>
            <UploadImages />
          </ProtectedRoute>
        }
      />
      <Route
        path="/staff/posts"
        element={
          <ProtectedRoute staff={true}>
            <ListPosts />
          </ProtectedRoute>
        }
      />
      <Route
        path="/staff/categoryPost/new"
        element={
          <ProtectedRoute staff={true}>
            <NewCategoryPost />
          </ProtectedRoute>
        }
      />
      <Route
        path="/staff/posts/new"
        element={
          <ProtectedRoute staff={true}>
            <NewPost />
          </ProtectedRoute>
        }
      />
      <Route
        path="/staff/categoryPosts"
        element={
          <ProtectedRoute staff={true}>
            <ListCategoryPosts />
          </ProtectedRoute>
        }
      />
      <Route
        path="/staff/posts/:id"
        element={
          <ProtectedRoute staff={true}>
            <UpdatePost />
          </ProtectedRoute>
        }
      />
      <Route
        path="/staff/discounts"
        element={
          <ProtectedRoute staff={true}>
            <ListDiscounts />
          </ProtectedRoute>
        }
      />

      <Route
        path="/staff/discount/new"
        element={
          <ProtectedRoute staff={true}>
            <NewDiscount />
          </ProtectedRoute>
        }
      />

      <Route
        path="/staff/orders"
        element={
          <ProtectedRoute staff={true}>
            <ListOrders />
          </ProtectedRoute>
        }
      />

      <Route
        path="/staff/orders/:id"
        element={
          <ProtectedRoute staff={true}>
            <ProcessOrder />
          </ProtectedRoute>
        }
      />

      <Route
        path="/staff/users"
        element={
          <ProtectedRoute staff={true}>
            <ListUsers />
          </ProtectedRoute>
        }
      />

      <Route
        path="/staff/users/:id"
        element={
          <ProtectedRoute staff={true}>
            <UpdateUser />
          </ProtectedRoute>
        }
      />

      <Route
        path="/staff/reviews"
        element={
          <ProtectedRoute staff={true}>
            <ProductReviews />
          </ProtectedRoute>
        }
      />
    </>
  );
};

export default staffRoutes;
