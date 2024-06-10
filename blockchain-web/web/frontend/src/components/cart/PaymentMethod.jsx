import React, { useEffect, useState } from "react";
import { FaCheckCircle } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";
import { Link, Navigate, useNavigate, useSearchParams } from "react-router-dom";
import { setCurrentOrder } from "../../redux/features/orderSlice";
import MetaData from "../layout/MetaData";
import CheckoutSteps from "./CheckoutSteps";

const PaymentMethod = () => {
  const [method, setMethod] = useState("VNPAY");
  const [searchParams] = useSearchParams();

  const navigate = useNavigate();
  const dispatch = useDispatch();

  const { currentOrder } = useSelector((state) => state.order);

  const handleChangePage = () => dispatch(setCurrentOrder(null));
  useEffect(() => {
    handleChangePage();
  }, []);
  if (!currentOrder) return <Navigate to={"/"} />;
  const orderId = currentOrder?.id;
  return (
    <>
      <MetaData title={"Order Created"} />
      <CheckoutSteps shipping confirmOrder payment />
      <div className="row wrapper">
        <div className="col-10 col-lg-5">
          <div className="shadow rounded bg-body d-flex flex-column align-items-center p-5 ">
            <h2 className="mb-4">Order Created</h2>
            <FaCheckCircle className="display-1 greenColor my-5" />
            <div className="d-flex justify-content-between col-12">
              <Link to={`/`} className="btn btn-secondary me-auto">
                Back to Home
              </Link>
              <Link
                to={`/me/order/${orderId}`}
                className="btn btn-primary ms-auto"
              >
                View Order Details
              </Link>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default PaymentMethod;
