import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";

import { Link, useParams } from "react-router-dom";
import MetaData from "../layout/MetaData";

import confirm from "antd/es/modal/confirm";
import { FormCheck, FormGroup, FormLabel } from "react-bootstrap";
import { FaCopy } from "react-icons/fa";
import { BASE_PRODUCTIMG } from "../../constants/constants";
import {
  currencyFormat,
  myDateFormat,
  phoneFormat,
} from "../../helpers/helpers";
import { getServerImgUrl } from "../../helpers/image-handler";
import { generateOrderQRImg } from "../../helpers/qrcode-helper";
import {
  useLazyFindOrderByQrCodeQuery,
  useOrderDetailsQuery,
  useUpdateOrderMutation,
} from "../../redux/api/orderApi";
import AdminLayout, { AdminLayoutLoader } from "../layout/AdminLayout";
import NotFound from "../layout/NotFound";

const ProcessOrder = () => {
  const params = useParams();
  const { data, isError, isLoading } = useOrderDetailsQuery(params?.id);
  const order = data?.data || {};
  const [updateOrder, { error, isSuccess }] = useUpdateOrderMutation();
  const [confirmOrder, { isSuccess: isConfirmed }] =
    useLazyFindOrderByQrCodeQuery();
  const {
    orderDetails,
    status: paymentStatus,
    productType,
    statusReceived,
    totalAmount,
    orderStatus,
    qrCode,
  } = order;
  const user = {
    firstName: order.firstName,
    lastName: order.lastName,
    email: order.email,
    phoneNumber: order.phoneNumber,
    address: order.address,
  };
  const isPaid = ((value) => {
    switch (value) {
      case "RECEIVED":
        return "greenColor";
      case "NOT_RECEIVED":
        return "yellowColor";
      default:
        return "yellowColor";
    }
  })(statusReceived);
  const [status, setStatus] = useState(statusReceived);
  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success("Order Recieved Success");
    }
  }, [error, isSuccess, isConfirmed]);

  const updateOrderHandler = (id) => {
    if (status === "RECEIVED") updateOrder({ id });
  };
  if (isError) return <NotFound />;
  const handleConfirmOrder = async (e) => {
    confirm({
      content: "Are you sure you want to confirm this Order?",
      cancelText: "Cancel",
      okText: "Confirm",
      onOk: () =>
        confirmOrder(order?.qrCode)
          .unwrap()
          .then((v) => toast.success("Order Confirmed!"))
          .catch((error) => toast.error("Something went wrong")),
    });
  };
  const isUnverified = statusReceived === "UNVERIFIED";
  const title = "Process Order";
  if (isLoading) return <AdminLayoutLoader title={title} />;
  return (
    <AdminLayout>
      <MetaData title={title} />
      <div className="row d-flex justify-content-around ">
        <div className="col-12 col-lg-8 order-details d-flex flex-column ">
          {/* Order Detail */}
          <h3 className="mt-5 mb-4">Order Details</h3>
          <table className="table mt-3 table-striped table-bordered">
            <tbody>
              <tr>
                <th scope="row">ID</th>
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
                isUnverified && "blur"
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
                  // navigator.clipboard.writeText(qrCode);
                  navigator.clipboard.writeText(qrCode);
                  toast.success("Copied to Clipboard");
                }}
              />
            </div>
            {isUnverified && (
              <div className="text">
                <span>Order isn't verified</span>
              </div>
            )}
          </div>
          <h3 className="mb-4">User Info</h3>
          <table className="table table-striped table-bordered">
            <tbody>
              <tr>
                <th scope="row">Name</th>
                <td>{user?.firstName + " " + user?.lastName}</td>
              </tr>
              <tr>
                <th scope="row">Email</th>
                <td>
                  {user?.email}
                  {/* {shippingInfo?.city},{" "}
                  {shippingInfo?.zipCode}, {shippingInfo?.country} */}
                </td>
              </tr>

              <tr>
                <th scope="row">Phone No</th>
                <td>{phoneFormat(user?.phoneNumber)}</td>
              </tr>
              <tr>
                <th scope="row">Address</th>
                <td>
                  {user?.address}
                  {/* {shippingInfo?.city},{" "}
                  {shippingInfo?.zipCode}, {shippingInfo?.country} */}
                </td>
              </tr>
            </tbody>
          </table>

          <h3 className="mt-5 mb-4">Payment Info</h3>
          <table className="table table-striped table-bordered">
            <tbody>
              <tr>
                <th scope="row">Status</th>
                <td className={isPaid}>
                  <b>{statusReceived}</b>
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

              {/* <tr>
                <th scope="row">Stripe ID</th>
                <td>{paymentInfo?.id || "Nill"}</td>
              </tr> */}
              <tr>
                <th scope="row">Amount</th>
                <td>{currencyFormat(totalAmount)}</td>
              </tr>
            </tbody>
          </table>

          <h3 className="mt-5 my-4">Order Items:</h3>

          <hr />

          <div className="cart-item my-1">
            {orderDetails?.map((item) => {
              const product = item.product;
              return (
                <div className="d-flex my-3 border rounded p-2 align-items-center bg-light gap-2">
                  <div className="col-auto d-flex">
                    <img
                      className="img-fluid my-auto"
                      src={getServerImgUrl(
                        product?.productImages[0].imgUrl,
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
                  <div className="col-4 gap-2 my-auto ">
                    <Link to={`/product/${product.id}`}>
                      {product?.productName}
                    </Link>
                  </div>
                  <div className="col-4 gap-2  my-auto">
                    <b>{currencyFormat(item?.price)} </b>
                    <span>
                      x {item?.quantity} Piece{item?.quantity > 0 && "(s)"}{" "}
                    </span>
                    <span className="fw-bold ">
                      =
                      <span className="orange">
                        {" "}
                        {currencyFormat(item?.amount)}
                      </span>
                    </span>
                  </div>
                  <div className="col-auto gap-2 ">
                    <span className="fw-bold">
                      {item?.processReceiveProduct}
                    </span>
                  </div>
                </div>
              );
            })}
          </div>
          <hr />
        </div>
        <div className="col-12 col-lg-3 mt-5">
          {statusReceived !== "RECEIVED" && (
            <>
              <h4 className="my-4">Status</h4>
              <FormGroup className="mb-3 form-group">
                {/* <select
              className="form-select"
              name="status"
              value={paymentStatus}
              onChange={(e) => setStatus(e.target.value)}
            >
              <option value="NOT_RECEIVED">NOT RECEIVED</option>
              <option value="RECEIVED">RECEIVED</option>
            </select> */}
                <FormCheck
                  id={"statusReceived"}
                  name="status"
                  type="radio"
                  className={`me-2 d-inline-block`}
                  value={status}
                  disabled={
                    order.orderDetails[0].processReceiveProduct !== "CONFIRM"
                  }
                  checked={status === "RECEIVED"}
                  onClick={(e) => setStatus("RECEIVED")}
                />
                <FormLabel for="paymentStatus" className="form-label">
                  RECEIVED
                </FormLabel>
              </FormGroup>
              <button
                className="btn btn-primary w-100"
                disabled={
                  order.orderDetails[0].processReceiveProduct !== "CONFIRM"
                }
                onClick={() => updateOrderHandler(order?.id)}
              >
                Update Status
              </button>
            </>
          )}
          {statusReceived === "RECEIVED" && (
            <>
              <h4 className="mt-5 mb-3">Order Invoice</h4>
              <Link
                to={`/invoice/order/${order?.id}`}
                className="btn btn-success w-100"
              >
                <i className="fa fa-print"></i> Generate Invoice
              </Link>
            </>
          )}
        </div>
        {/* {orderDetails
          ?.map((item) => item.processReceiveProduct)
          .join("|")
          .includes("PENDING") ? (
          <div>
            <Button variant="success" onClick={handleConfirmOrder}>
              Confirm Order <FaCheckCircle />
            </Button>
          </div>
        ) : (
          <span>
            Order Confirmed <FaCheck></FaCheck>
          </span>
        )} */}
      </div>
    </AdminLayout>
  );
};

export default ProcessOrder;
