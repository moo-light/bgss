import React from "react";
import { useLocation } from "react-router-dom";

const Footer = () => {
  const location = useLocation();
  return (
    <footer className="py-3" style={location.pathname.includes("admin") ? {backgroundColor:'var(--header-color)'}:null}>
      <div className="text-center mt-1 fw-bold">
        BGSS Shop - 2024, All Rights Reserved
      </div>
    </footer>
  );
};

export default Footer;
