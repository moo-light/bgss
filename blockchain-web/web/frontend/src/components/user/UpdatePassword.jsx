import React, { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { authApi } from "../../redux/api/authApi";
import { useUpdatePasswordMutation } from "../../redux/api/userApi";
import { setCurrentOrder } from "../../redux/features/orderSlice";
import {
  setIsAuthenticated,
  setRoles,
  setUser,
} from "../../redux/features/userSlice";
import MetaData from "../layout/MetaData";
import UserLayout from "../layout/UserLayout";

const UpdatePassword = () => {
  const [oldPwd, setOldPwd] = useState("");
  const [newPwd, setNewPwd] = useState("");

  const [confirmNewPwd, setConfirmNewPwd] = useState("");

  const [updatePassword, { isLoading, error, isSuccess }] =
    useUpdatePasswordMutation();

  useEffect(() => {
    if (error) {
      console.log(error);
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      logout();
      toast.success("Password Updated");
      document.location.replace("/login");
    }
  }, [error, isSuccess]);

  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("user-roles");
    localStorage.removeItem("user-id");
    setUser(null);
    setRoles(null);
    authApi.util.invalidateTags(["Users", "Auth"]);
    setIsAuthenticated(false);
    setCurrentOrder(null);
  }

  const submitHandler = async (e) => {
    e.preventDefault();

    const userData = {
      oldPwd,
      newPwd,
      confirmNewPwd,
    };

    updatePassword(userData);
  };
  return (
    <UserLayout>
      <MetaData title={"Update Password"} />
      <div className="row wrapper">
        <div className="col-10 col-lg-8">
          <form className="shadow rounded bg-body" onSubmit={submitHandler}>
            <h2 className="mb-4 text-center">Update Password</h2>
            <div className="mb-3">
              <label htmlFor="old_password_field" className="form-label">
                Old Password
              </label>
              <input
                type="password"
                id="old_password_field"
                className="form-control"
                value={oldPwd}
                onChange={(e) => setOldPwd(e.target.value)}
              />
            </div>

            <div className="mb-3">
              <label htmlFor="new_password_field" className="form-label">
                New Password
              </label>
              <input
                type="password"
                id="new_password_field"
                className="form-control"
                value={newPwd}
                onChange={(e) => setNewPwd(e.target.value)}
              />
            </div>

            <div className="mb-3">
              <label htmlFor="password_field" className="form-label">
                Confirm Password
              </label>
              <input
                type="password"
                id="password_field"
                className="form-control"
                value={confirmNewPwd}
                onChange={(e) => setConfirmNewPwd(e.target.value)}
              />
            </div>

            <button
              type="submit"
              className="btn update-btn w-100"
              disabled={isLoading}
            >
              {isLoading ? "Updating..." : "Update Password"}
            </button>
          </form>
        </div>
      </div>
    </UserLayout>
  );
};

export default UpdatePassword;
