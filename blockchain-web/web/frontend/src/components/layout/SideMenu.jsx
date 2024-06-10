import React, { useState } from "react";
import { Link, useLocation } from "react-router-dom";
const SideMenu = ({ menuItems }) => {
  const location = useLocation();

  const [activeMenuItem, setActiveMenuItem] = useState(location.pathname);

  const handleMenuItemClick = (menuItemUrl) => {
    setActiveMenuItem(menuItemUrl);
  };

  return (
    <div className="list-group admin-side-menu ms-2">
      {menuItems?.map((menuItem, index) => (
        <div className="position-relative">
          <aside className="before"></aside>
          <Link
            key={index}
            to={menuItem.url}
            className={`fw-bold list-group-item list-group-item-action  ${
              activeMenuItem.includes(menuItem.url) ? "active" : ""
            }`}
            onClick={() => handleMenuItemClick(menuItem.url)}
            aria-current={
              activeMenuItem.includes(menuItem.url) ? "true" : "false"
            }
          >
            <i className={`${menuItem.icon} fa-fw pe-2`}></i> {menuItem.name}
          </Link>
        </div>
      ))}
    </div>
  );
};

export default SideMenu;
