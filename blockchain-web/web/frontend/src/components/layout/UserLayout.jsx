import React from "react";
import { useSelector } from "react-redux";
import "../../css/userlayout.css";
import SideMenu from "./SideMenu";

const UserLayout = ({ children }) => {
  const { roles } = useSelector((state) => state.auth);
  const menuItems = [
    {
      name: "Profile",
      url: "/me/profile",
      icon: "fas fa-user",
    },
    {
      name: "Update Profile",
      url: "/me/update_profile",
      icon: "fas fa-user",
    },
    {
      name: "Update Password",
      url: "/me/update_password",
      icon: "fas fa-lock",
    },
  ];
  if (roles?.includes("ROLE_CUSTOMER")) {
    menuItems.splice(
      3,
      0,
      {
        name: "VerifyCICard",
        url: "/me/verifyCICard",
        icon: "fa fa-id-card",
      },
      {
        name: "My Discount",
        url: "/me/discounts",
        icon: "fas fa-tag",
      }
      // {
      //   name: "Balance",
      //   url: "/me/balance",
      //   icon: "fas fa-dollar-sign",
      // },
      // {
      //   name: "Inventory",
      //   url: "/me/inventory",
      //   icon: "fas fa-coins    ",
      // }
    );
  }
  return (
    <div>
      <div className="mt-2 py-4">
        <h2 className="text-center fw-bolder">User Settings</h2>
      </div>

      <div>
        <div className="row justify-content-around ">
          <div className="col-12 col-lg-3 mb-5">
            <SideMenu menuItems={menuItems} />
          </div>
          <div className="col-12 col-lg-8 user-dashboard position-relative">
            {children}
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserLayout;
