import React from "react";
import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";
import Loader from "../layout/Loader";
const ProtectedRoute = ({ admin, staff, children }) => {
  const { isAuthenticated, user, roles, loading } = useSelector(
    (state) => state.auth
  );
  if (loading) return <Loader />;

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  const isAdminOrStaff =
    roles?.includes("ROLE_ADMIN") || roles?.includes("ROLE_STAFF");

  if (admin && !roles?.includes("ROLE_ADMIN")) {
    //admin only
    return <Navigate to="/" replace />;
  }
  if (staff && !isAdminOrStaff) {
    //staff and admin
    return <Navigate to="/" replace />;
  }
  return children;
};

export default ProtectedRoute;
