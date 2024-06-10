import React, { useEffect } from "react";
import { FaBox, FaHome, FaNewspaper, FaShoppingCart } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { BASE_AVATAR } from "../../constants/constants";
import { authApi } from "../../redux/api/authApi";
import { useGetMeQuery } from "../../redux/api/userApi";
import { setCurrentOrder } from "../../redux/features/orderSlice";
import {
  setIsAuthenticated,
  setRoles,
  setUser,
} from "../../redux/features/userSlice";
const Header = () => {
  const navigate = useNavigate();
  const { isLoading } = useGetMeQuery();
  const dispatch = useDispatch();
  const location = useLocation();
  const { user, roles } = useSelector((state) => state.auth);
  const { cartItems } = useSelector((state) => state.cart);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (
      token !== null
      // && location.href.includes("/login")
    ) {
      const expiresAt = JSON.parse(atob(token.split(".")[1])).exp * 1000;
      const currentTime = new Date().getTime();
      // const timeLeft = 5000; // set Time out at 10 second
      const timeLeft = expiresAt - currentTime;
      if (timeLeft > 0) {
        const timer = setTimeout(() => {
          alert("Token Expired");
          logoutHandler();
        }, timeLeft);
        return () => clearTimeout(timer);
      } else {
        logoutHandler();
      }
    }
  }, [user]);
  
  function logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("user-roles");
    localStorage.removeItem("user-id");
    dispatch(setUser(null));
    dispatch(authApi.util.invalidateTags(["Users", "Auth"]));
    dispatch(setRoles(null));
    dispatch(setIsAuthenticated(false));
    dispatch(setCurrentOrder(null));
  }
  const logoutHandler = () => {
    logout();
    navigate(0);
  };

  return (
    <header className="navbar row">
      <div className="col-12 col-md-3  ">
        <div className="navbar-brand">
          <Link
            className="d-flex justify-content-center justify-content-lg-start "
            to="/"
            tabIndex={-1}
          >
            <img
              style={{
                width: "100px",
              }}
              src="/images/BGSS_logo_large.png"
              alt="Bgss Logo"
            />
          </Link>
        </div>
      </div>
      <div className="col-auto col-md-4 mt-2 mt-md-0 d-flex gap-3 mx-auto justify-content-lg-center me-md-0 ">
        <Link to="/" className="text-decoration-none d-flex" tabIndex={-1}>
          <div
            className={`gap-1 d-flex align-items-center ${
              document.location.pathname === "/" ? "orange" : "text"
            } `}
          >
            <FaHome className="text" />
            <div>Home</div>
          </div>
        </Link>
        <div className="vr "></div>
        <Link
          to="/products"
          className="text-decoration-none d-flex"
          tabIndex={-1}
        >
          <div
            id="productList"
            className={`gap-1 d-flex align-items-center ${
              document.location.pathname.startsWith("/products")
                ? "orange"
                : "text"
            } `}
          >
            <FaBox className="text" />
            <div>Shop </div>
          </div>
        </Link>
        <div className="vr"></div>
        <Link
          to="/forums"
          className="text-decoration-none d-flex"
          tabIndex={-1}
        >
          <div
            id="forum"
            className={`gap-1 d-flex align-items-center  ${
              document.location.pathname.includes("forums") ? "orange " : "text"
            } `}
          >
            <FaNewspaper className="text" />
            <div>Forums</div>
          </div>
        </Link>
      </div>

      <div className="mt-4 mt-md-0 justify-content-center justify-content-md-end ms-auto text-center d-flex col-12 col-md-5 col-lg-3  ">
        {/* {console.log(roles != null && roles?.includes("ROLE_CUSTOMER"),roles)} */}
        {roles != null && roles?.includes("ROLE_CUSTOMER") && (
          <Link
            to="/cart"
            className="text-decoration-none d-flex"
            tabIndex={-1}
          >
            <span id="cart" className="ms-3 gap-1 d-flex align-items-center ">
              <FaShoppingCart />
              <div>Cart</div>
            </span>
            <span className="ms-1 m-auto" id="cart_count">
              {cartItems?.length}
            </span>
          </Link>
        )}

        {user ? (
          <div className=" dropdown ms-2">
            <button
              className="btn dropdown-toggle  d-flex align-items-center"
              type="button"
              id="dropDownMenuButton"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              <figure className="avatar avatar-nav">
                <img
                  src={user?.userInfo?.avatarUrl || BASE_AVATAR}
                  alt=""
                  className="rounded-circle"
                />
              </figure>
              <div
                className="overflow-hidden "
                title={user?.name}
                style={{ width: 100, textOverflow: "hidden" }}
              >
                {user?.name}
              </div>
            </button>
            <div
              className="dropdown-menu w-100"
              aria-labelledby="dropDownMenuButton"
            >
              {(roles?.includes("ROLE_ADMIN") ||
                roles?.includes("ROLE_STAFF")) && (
                <Link
                  className="dropdown-item"
                  to="/admin/dashboard"
                  tabIndex={-1}
                >
                  Dashboard
                </Link>
              )}
              {/* {roles?.includes("ROLE_ADMIN") && (
                <Link className="dropdown-item" to="/admin/blockchain">
                  {" "}
                  Blockchain{" "}
                </Link>
              )} */}
              <Link className="dropdown-item" to="/me/profile" tabIndex={-1}>
                {" "}
                Profile{" "}
              </Link>
              {roles?.includes("ROLE_CUSTOMER") && (
                <Link className="dropdown-item" to="/me/orders" tabIndex={-1}>
                  {" "}
                  Orders{" "}
                </Link>
              )}

              {roles?.includes("ROLE_CUSTOMER") && (
                <Link className="dropdown-item" to="/me/historyDeposit">
                  {" "}
                  History Deposit{" "}
                </Link>
              )}

              <Link
                className="dropdown-item text-danger"
                to="/"
                onClick={logoutHandler}
              >
                Logout{" "}
              </Link>
            </div>
          </div>
        ) : (
          !isLoading && (
            <Link
              to="/login"
              className="btn ms-0 ms-lg-4"
              id="login_btn"
              tabIndex={-1}
            >
              {" "}
              Login{" "}
            </Link>
          )
        )}
      </div>
    </header>
  );
};

export default Header;
