import React, { useEffect } from "react";
import { useDispatch } from "react-redux";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { clearCart } from "../../redux/features/cartSlice";
import MetaData from "../layout/MetaData";
import ListOrders from "./ListOrders";
import ListTransactions from "./ListTransactions";
import ListWithdraws from "./ListWithdraws";

const MyOrders = () => {
  const [searchParams] = useSearchParams();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const orderSuccess = searchParams.get("order_success");
  const view = searchParams.get("view");

  useEffect(() => {
    if (orderSuccess) {
      dispatch(clearCart());
      navigate("/me/orders");
    }
  }, [orderSuccess]);

  const render = (view) => {
    switch (view) {
      case "orders":
        return <ListOrders />;
      case "transactions":
        return <ListTransactions />;
      case "withdraws":
        return <ListWithdraws />;
      default:
        return <ListOrders />;
    }
  };

  return (
    <div>
      <MetaData title={"My Orders"} />
      <div style={{ display: "flex", justifyContent: "center" }}>
        <nav
          className="navbar navbar-expand-lg mt-5"
          style={{ maxWidth: "350px", width: "100%" }}
        >
          <div className="container-fluid">
            <div
              className="navbar-nav gap-2"
              style={{ justifyContent: "center", width: "100%" }}
            >
              <Link
                className={`nav-link ${
                  view === "orders" || !view ? "active" : ""
                }`}
                to="?view=orders"
              >
                Orders
              </Link>
              <Link
                className={`nav-link ${
                  view === "transactions" ? "active" : ""
                }`}
                to="?view=transactions"
              >
                Transactions
              </Link>
              <Link
                className={`nav-link ${view === "withdraws" ? "active" : ""}`}
                to="?view=withdraws"
              >
                Withdraws
              </Link>
            </div>
          </div>
        </nav>
      </div>

      <div>{render(view)}</div>
    </div>
  );
};

export default MyOrders;
