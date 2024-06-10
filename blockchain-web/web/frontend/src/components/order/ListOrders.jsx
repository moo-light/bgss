import { MDBDataTable } from "mdbreact";
import React, { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { FaEye } from "react-icons/fa";
import { Link } from "react-router-dom";
import { orderTable } from "../../constants/constants";
import { currencyFormat, myDateFormat } from "../../helpers/helpers";
import { useMyOrdersQuery } from "../../redux/api/orderApi";
import Loader from "../layout/Loader";
import OTPPage from "../otp/OTPCheckForm";
import Label from "../../helpers/components/customMUI/label/label";


const ListOrders = () => {
  const { data, isLoading, error } = useMyOrdersQuery();
  const [otpModalShow, setOtpModalShow] = useState(false);
  const [orderId, setOrderId] = useState(null);
  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }
  }, [error]);

  const handleOpenOTPModel = (orderId) => {
    // Hiển thị modal OTP khi nhấn nút Resend OTP
    setOrderId(orderId);
    setOtpModalShow(true);
  };

  const setOrders = () => {
    const orders = orderTable();
    data?.forEach((order) => {
      const totalPriceCalc = order.orderDetails.reduce((acc, detail) => acc + detail.amount, 0);
      orders.rows.push({
        id: order?.id,
        statusReceived: (
          <div
          // className={`fw-bold
          //   ${
          //     order?.statusReceived?.toUpperCase() === "RECEIVED" &&
          //     "text-success"
          //   }
          //   ${
          //     order?.statusReceived?.toUpperCase() === "NOT_RECEIVED" &&
          //     "gold"
          //   }
          //   `}
          >
            {/* {order?.statusReceived?.toUpperCase()} */}
            <Label
              variant="soft"
              color={
                (order?.statusReceived === "NOT_RECEIVED" && "warning") ||
                (order?.statusReceived === "RECEIVED" && "success") ||
                (order?.statusReceived === "UNVERIFIED" && "default")
              }
              sx={{ mx: "auto" }}
            >
              {order?.statusReceived}
            </Label>
          </div>
        ),
        paymentStatus: (
          <div
          // className={`fw-bold
          //   ${
          //     order?.statusReceived?.toUpperCase() === "RECEIVED" &&
          //     "text-success"
          //   }
          //   ${
          //     order?.statusReceived?.toUpperCase() === "NOT_RECEIVED" &&
          //     "gold"
          //   }
          //   `}
          >
            {/* {order?.statusReceived?.toUpperCase()} */}
            <Label
              variant="soft"
              color={
                (order?.paymentStatus === "NOT_PAID" && "warning") ||
                (order?.paymentStatus === "PAID" && "success")
              }
              sx={{ mx: "auto" }}
            >
              {order?.paymentStatus}
            </Label>
          </div>
        ),
        createDate: myDateFormat(order?.createDate),
        totalAmount: (
          <span className={`fw-bold text-danger`}>
            <b>{order?.totalAmount ? `-${currencyFormat(order?.totalAmount)}` : ''}</b>
          </span>
        ),
        discount: (order?.percentageDiscount ?? 0) + " %",

        price: (
          <span className="fw-bold text-success">
            <b>{currencyFormat(totalPriceCalc)}</b>
          </span>
        ),


        isConsignment: (
          <div>
            <Label
              variant="soft"
              color={order?.consignment ? "primary" : "secondary"}
              sx={{ mx: "auto" }}
            >
              {order?.consignment ? "Consignment" : "Not Consignment"}
            </Label>
          </div>
        ),

        // order?.consignment ? "true" : "false",
        actions: (
          <>
            <Link
              to={`/me/order/${order?.id}`}
              className="btn border border btn-light"
            >
              <FaEye />
            </Link>
            {order?.statusReceived === "RECEIVED" && (
              <Link
                to={`/invoice/order/${order?.id}`}
                className="btn btn-success ms-2"
              >
                <i className="fa fa-print"></i>
              </Link>
            )}
            {/* Thêm nút resend OTP */}
            {order?.statusReceived === "UNVERIFIED" && (
              <>
                <button
                  className="btn btn-primary ms-2"
                  onClick={() => handleOpenOTPModel(order.id)}
                >
                  Verify
                </button>
              </>
            )}
          </>
        ),
      });
    });

    return orders;
  };

  const handleCloseOTPModal = () => {
    setOtpModalShow(false);
  };
  if (isLoading) return <Loader />;

  return (
    <>
      <h1 className="my-2 px-5">{data?.length} Orders</h1>
      <MDBDataTable
        data={setOrders()}
        className="px-5 content mt-5"
        bordered
        striped
        hover
      />
      <style jsx>{`
        .dataTables_wrapper .dataTables_length,
        .dataTables_wrapper .dataTables_filter,
        .dataTables_wrapper .dataTables_info,
        .dataTables_wrapper .dataTables_paginate {
          margin-bottom: 20px;
        }
      `}</style>
      {/* Modal OTP */}
      {otpModalShow && (
        <OTPPage
          orderId={orderId}
          onClose={handleCloseOTPModal}
          // Cập nhật trạng thái khi đóng modal
        />
      )}
    </>
  );
};

export default ListOrders;
