import { LoadingButton } from "@mui/lab";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@mui/material";
import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import { FaCheck, FaCopy } from "react-icons/fa";
import { Link, useNavigate, useParams } from "react-router-dom";
import { BASE_PRODUCTIMG } from "../../constants/constants";
import { orderColors } from "../../helpers/colors";
import {
  currencyFormat,
  myDateFormat,
  phoneFormat,
} from "../../helpers/helpers";
import { getServerImgUrl } from "../../helpers/image-handler";
import { generateOrderQRImg } from "../../helpers/qrcode-helper";
import {
  useOrderDetailsQuery,
  useOrderRecievedMutation,
} from "../../redux/api/orderApi";
import Loader from "../layout/Loader";
import MetaData from "../layout/MetaData";

const OrderDetails = () => {
  const params = useParams();
  const navigate = useNavigate();

  const { data, isLoading, error } = useOrderDetailsQuery(params?.id);
  const [
    setRecieved,
    { isSuccess, isError, isLoading: orderRecievedLoading, ...orderRecievied },
  ] = useOrderRecievedMutation();
  const order = data?.data || {};

  const {
    orderDetails,
    status: paymentStatus,
    userConfirm,
    statusReceived,
    totalAmount,
    qrCode,
    
  } = order;

  const [dialogOpen, setDialogOpen] = useState(false);

  const handleDialogClose = () => {
    setDialogOpen(false);
  };

  const handleProductRecieved = () => {
    setDialogOpen(true);
  };

  const handleDelete = () => {
    setRecieved(params?.id);
    setDialogOpen(false);
  };

  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
      navigate("/me/orders");
    }
  }, [error, navigate]);

  useEffect(() => {
    if (isError) {
      toast.error(orderRecievied.error?.data?.message);
    }
    if (isSuccess) {
      toast.success("Order Updated!");
    }
  }, [isSuccess, isError]);

  if (isLoading) return <Loader />;
  const isUnverified = statusReceived === "UNVERIFIED";
  const user = {
    firstName: order.firstName,
    lastName: order.lastName,
    email: order.email,
    phoneNumber: order.phoneNumber,
    address: order.address,
  };


  const isPaidStatus = ((value) => {
    switch (value) {
      case "PAID":
        return "greenColor";
        case "NOT_PAID": 
        return "redColor";
      default:
        return "yellowColor";
    }
  })(order?.paymentStatus);

    const isPaid = ((value) => {
    switch (value) {
      case "PAID":
        return "greenColor";
      case "CANCELLED":
        return "redColor";
        case "NOT_PAID": 
        return "redColor";
        case "COMPLETE": 
        return "greenColor";
      default:
        return "yellowColor";
    }
  })(paymentStatus);

  return (
    <>
      <MetaData title={"Order Details"} />
      <div className="row d-flex justify-content-center">
        <div className="col-12 col-lg-8 order-details d-flex flex-column ">
          {/* Order Detail */}
          <h3 className="mt-5 mb-4">Order Details</h3>
          <table className="table mt-3 table-striped table-bordered">
            <tbody>
              <tr>
                <th scope="row" className="col-3">
                  ID
                </th>
                <td>{order?.id}</td>
              </tr>
              <tr>
                <th scope="row">Date</th>
                <td>{myDateFormat(order?.createDate)}</td>
              </tr>
            </tbody>
          </table>
          <div className="align-items-center d-inline-flex mx-auto flex-column  gap-2 ">
            <img
              className={`m-auto bg-white p-3 rounded shadow ${
                isUnverified && "blur-img"
              }`}
              src={generateOrderQRImg(isUnverified ? "" : qrCode)}
              alt={"QrCode"}
            />
            <div
              className={`m-0 mt-auto bg-white shadow rounded p-3 ${
                isUnverified && "d-none "
              }`}
            >
              <b>Code: </b>
              <span className="user-select-all m-1 ">
                {isUnverified || qrCode}
              </span>
              <FaCopy
                className="icon clickable"
                onClick={(e) => {
                  navigator.clipboard.writeText(qrCode);
                  toast.success("Copied to Clipboard");
                }}
              />
            </div>
            {isUnverified && (
              <div className="text">
                <span>Please Verify your Order for Qr code</span>
              </div>
            )}
          </div>
          <h3 className="mb-4">User Info</h3>
          <table className="table table-striped table-bordered">
            <tbody>
              <tr>
                <th scope="row" className="col-3">
                  Name
                </th>
                <td>{user?.firstName + " " + user?.lastName}</td>
              </tr>
              <tr>
                <th scope="row">Email</th>
                <td>{user?.email}</td>
              </tr>

              <tr>
                <th scope="row">Phone No</th>
                <td>{phoneFormat(user?.phoneNumber)}</td>
              </tr>
              <tr>
                <th scope="row">Address</th>
                <td>{user?.address}</td>
              </tr>
            </tbody>
          </table>
          <h3 className="mt-5 mb-4">Payment Info</h3>
          <table className="table table-striped table-bordered">
            <tbody>
              <tr>
                <th scope="row" className="col-3">
                  Status
                </th>
                <td className={isPaid}>
                  <b>{statusReceived}</b>
                </td>
              </tr>

              <tr>
                <th scope="row" className="col-3">
                  Payment Status
                </th>
                <td className={isPaidStatus}>
                  <b>{order?.paymentStatus}</b>
                </td>
              </tr>
              <tr>
                <th scope="row">Discount</th>
                <td>
                  {order?.percentageDiscount != null
                    ? order.percentageDiscount + " %"
                    : "0 %"}
                </td>
              </tr>
              <tr>
                <th scope="row">Total Amount</th>
                <td>{currencyFormat(totalAmount)}</td>
              </tr>
            </tbody>
          </table>
          <h3 className="mt-5 my-4">Order Items:</h3>
          <hr />
          <div className="cart-item my-1 ">
            {orderDetails?.map((item) => {
              const product = item.product;
              return (
                <div
                  key={product.id}
                  className="row my-3 border rounded p-2 align-items-center bg-light"
                >
                  <div className="col-auto d-flex">
                    <img
                      className="img-fluid my-auto"
                      src={getServerImgUrl(
                        product?.productImages[0]?.imgUrl,
                        BASE_PRODUCTIMG
                      )}
                      alt={product?.productName}
                      style={{
                        backgroundColor: "var(--background-color)",
                      }}
                      height="45"
                      width="65"
                    />
                  </div>
                  <div className="col-4 gap-2 my-auto">
                    {product.active ? (
                      <Link to={`/product/${product.id}`}>
                        {product.productName}
                      </Link>
                    ) : (
                      <div
                        onClick={() =>
                          toast.error("The product has been discontinued!")
                        }
                      >
                        {product.productName}
                      </div>
                    )}
                  </div>
                  <div className="col-4 gap-2  my-auto">
                    <b>{currencyFormat(item?.price)} </b>
                    <span>
                      x {item?.quantity} Piece{item?.quantity > 0 && "(s)"}{" "}
                    </span>
                    <span className="fw-bold ">
                      ={" "}
                      <span className="orange">
                        {currencyFormat(item?.amount)}
                      </span>
                    </span>
                  </div>
                  <div className="col-auto gap-2 ">
                    <span style={{ 
                          fontWeight: "bold", 
                          color: item?.processReceiveProduct === "COMPLETE" ? "green" : "red" 
                        }}>
                      {item?.processReceiveProduct}
                    </span>
                  </div>
                </div>
              );
            })}
          </div>
          {/* {statusReceived === "RECEIVED" && userConfirm !== "RECEIVED" && (
            <LoadingButton
              onClick={handleProductRecieved}
              loading={orderRecievedLoading}
              sx={{
                width: "20%",
                marginLeft: "auto",
                display: "flex",
                gap: 1,
                borderRadius: "20px",
                backgroundColor: "darkorange",
                color: "white",
                "&:hover": {
                  color: "black",
                  backgroundColor: "darkorange",
                },
              }}
            >
              {"Order Recieved "} <FaCheck />
            </LoadingButton>
          )} */}
{statusReceived === "RECEIVED" && (
  <LoadingButton
    onClick={handleProductRecieved}
    loading={orderRecievedLoading}
    disabled={userConfirm === "RECEIVED"}
    sx={{
      width: "20%",
      marginLeft: "auto",
      display: "flex",
      gap: 1,
      borderRadius: "20px",
      backgroundColor: userConfirm === "RECEIVED" ? "green" : "darkorange",
      color: "white !important",
      pointerEvents: userConfirm === "RECEIVED" ? "none" : "auto",
      opacity: userConfirm === "RECEIVED" ? 1 : undefined,
      "&:hover": {
        color: "white",
        backgroundColor: userConfirm === "RECEIVED" ? "green" : "darkorange",
      },
    }}
  >
    {"Order Received "} <FaCheck />
  </LoadingButton>
)}




          <hr />
        </div>
      </div>

      <Dialog open={dialogOpen} onClose={handleDialogClose}>
        <DialogTitle>Confirm Received</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to mark this order as received?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDialogClose} color="primary">
            Cancel
          </Button>
          <Button
            onClick={handleDelete}
            color="primary"
            disabled={orderRecievedLoading}
          >
            Confirm
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default OrderDetails;
