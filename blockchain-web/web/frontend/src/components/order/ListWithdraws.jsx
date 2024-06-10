import Avatar from "@mui/material/Avatar";
import Box from "@mui/material/Box";
import Typography from "@mui/material/Typography";
import { Popconfirm } from "antd";
import { MDBDataTable } from "mdbreact";
import { useEffect, useState } from "react";
import { Button, Collapse, Form, Modal } from "react-bootstrap";
import toast from "react-hot-toast";
import { FaCaretDown, FaCheckCircle } from "react-icons/fa";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import axios, { endpoints } from "../../../src/utils/axios";
import { BASE_PATH } from "../../constants/constants";
import Label from "../../helpers/components/customMUI/label/label";
import { FormError } from "../../helpers/components/form-error";
import { myDateFormat } from "../../helpers/helpers";
import { generateWithdrawQRImg } from "../../helpers/qrcode-helper";
import {
  useCancelWithdrawalMutation,
  useHandleWithdrawalMutation,
  useMyWithdrawListQuery,
} from "../../redux/api/transactionApi";
import Loader from "../layout/Loader";
import OTPPage from "../otp/OTPCheckFormWIthdraw";

const ListWithdraws = () => {
  const { user, roles } = useSelector((state) => state.auth);
  const HOST = BASE_PATH;
  const withdrawList = useMyWithdrawListQuery();
  const { data, isLoading, error } = withdrawList;
  const [otpModalShow, setOtpModalShow] = useState(false);
  const [orderId, setOrderId] = useState(null);
  const [cancelOrder, { isLoading: canceling, error: cancelError, isError }] =
    useCancelWithdrawalMutation();
  const [completeWithDraw, { isLoading: confirming }] =
    useHandleWithdrawalMutation();
  const handleResendOTP = (orderId) => {
    // Hiển thị modal OTP khi nhấn nút Resend OTP
    setOrderId(orderId);
    setOtpModalShow(true);
  };

  const handleCloseOTPModal = () => {
    setOtpModalShow(false);
  };
  const actionStr = "COMPLETE_WITHDRAWAL";
  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }
  }, [error]);

  const getStatusColor = (status) => {
    switch (status) {
      case "UNVERIFIED":
        return "error";
      case "CANCELLED":
        return "default";
      case "APPROVE":
      case "COMPLETED":
        return "success";
      case "PENDING":
        return "warning";
      case "APPROVED":
        return "success";
      case "CONFIRMED":
        return "info";
      case "REJECTED":
        return "error";
      default:
        return "default";
    }
  };

  const setWithDraw = () => {
    const orders = {
      columns: [
        {
          label: "ID",
          field: "id",
          sort: "asc",
          attributes: { className: "text-center" },
        },
        {
          label: "Product",
          field: "productDTO",
          sort: "asc",
          attributes: { className: "text-center" },
        },

        {
          label: "Transaction Date",
          field: "transactionDate",
          sort: "asc",
          attributes: { className: "text-center" },
        },
        {
          label: "Updated Date",
          field: "updateDate",
          sort: "asc",
          attributes: { className: "text-center" },
        },
        {
          label: "Amount",
          field: "totalAmount",
          sort: "asc",
          attributes: { className: "text-center" },
        },

        {
          label: "QrCode",
          field: "withdrawQrCode",
          sort: "asc",
          attributes: { className: "text-center" },
        },
        {
          label: "Withdraw Status",
          field: "status",
          sort: "asc",
          attributes: { className: "text-center" },
        },
        {
          label: "Order Status",
          field: "userConfirm",
          sort: "asc",
          attributes: { className: "text-center" },
        },
        {
          label: "Actions",
          field: "actions",
          sort: "asc",
          attributes: { className: "text-center" },
        },
      ],
      rows: [],
    };
    data?.data?.forEach((withdraw) => {
      orders.rows.push({
        id: withdraw?.id,
        productDTO: (
          <Box display="flex" alignItems="center" sx={{ maxWidth: 250 }}>
            <Avatar
              // key={withdraw.productDTO?.id}
              // alt={withdraw.productDTO?.productName}
              src={`${HOST}/${withdraw.productDTO?.productImages?.[0]?.imgUrl}`}
              variant="rounded"
              sx={{
                width: 60,
                height: 60,
                flexShrink: 0,
                mr: 1.5,
                borderRadius: 5,
              }}
            />
            <Box display="flex" flexDirection="column">
              <Typography
                component="span"
                sx={{
                  typography: "body1",
                  fontWeight: "fontWeightMedium",
                }}
              >
                {withdraw.productDTO?.productName}{" "}
                {/* {withdraw.productDTO?.typeGold?.typeName} */}
              </Typography>
              {/* <Box display="flex" alignItems="center">
                <Rating
                  name="read-only"
                  value={withdraw.productDTO?.avgReview}
                  readOnly
                />
                <Typography
                  component="span"
                  sx={{
                    typography: "body2",
                    fontWeight: "fontWeightMedium",
                    color: "text.secondary",
                    ml: 1,
                  }}
                >
                  {withdraw.productDTO?.avgReview.toFixed(1)}
                </Typography>
              </Box> */}
            </Box>
          </Box>
        ),
        status: (
          // <div
          //   className={`${colorFormatWithdraw(
          //     withdraw?.status?.toUpperCase()
          //   )} fw-bold`}
          // >
          //   {withdraw?.status?.toUpperCase()}
          // </div>
          <div>
            <Label
              variant="soft"
              color={
                (withdraw?.status === "UNVERIFIED" && "error") ||
                (withdraw?.status === "PENDING" && "warning") ||
                (withdraw?.status === "CONFIRMED" && "info") ||
                (withdraw?.status === "COMPLETED" && "success") ||
                (withdraw?.status === "CANCELLED" && "default") ||
                (withdraw?.status === "APPROVED" && "success") ||
                (withdraw?.status === "REJECTED" && "error")
              }
              sx={{ mx: "auto" }}
            >
              {withdraw?.status}
            </Label>
          </div>
        ),
        userConfirm: (
          <div>
            <Label
              variant="soft"
              color={
                (withdraw?.userConfirm === "NOT_RECEIVED" && "default") ||
                (withdraw?.userConfirm === "RECEIVED" && "success")
              }
              sx={{ mx: "auto" }}
            >
              {withdraw?.userConfirm}
            </Label>
          </div>
        ),
        transactionDate: myDateFormat(withdraw?.transactionDate),
        updateDate: myDateFormat(withdraw?.updateDate),
        // totalAmount: GOLD_UNIT_CONVERT_2[withdraw?.amount] + "Mace",
        totalAmount: withdraw?.amount + " " + withdraw?.goldUnit,
        withdrawQrCode: withdraw?.withdrawQrCode,
        userInfoId: withdraw?.userInfoId || "none",
        actions: (
          <div className="d-flex">
            <button
              aria-label="View withdraw"
              className="btn btn-light border "
              onClick={(e) => showModal(withdraw)}
            >
              <i className="fa fa-eye"></i>
            </button>
            {(withdraw?.status === "APPROVED" ||
              (withdraw?.status === "COMPLETED" &&
                withdraw?.userConfirm === "RECEIVED")) && (
              <Link
                to={`/invoice/withdraw/${withdraw?.id}`}
                className="btn btn-success ms-2"
              >
                <i className="fa fa-print"></i>
              </Link>
            )}
            {/* Thêm nút resend OTP */}
            {withdraw?.status === "UNVERIFIED" && (
              <>
                <button
                  className="btn btn-primary ms-2"
                  onClick={() => handleResendOTP(withdraw.id)}
                >
                  Verify
                </button>
              </>
            )}
          </div>
        ),
      });
    });

    return orders;
  };

  //Modal Box
  const [show, setShow] = useState(false);
  const [modal, setModal] = useState({});
  const handleClose = () => {
    setShow(false);
  };
  const showModal = (value) => {
    setShow(!!value);
    setModal(value);
  };

  const [reason, setReason] = useState("");
  const [errors, setErrors] = useState({});
  const [open, setOpen] = useState(false);
  const onChange = (e) => {
    setReason(e.target.value);
  };

  const onSubmit = async (e) => {
    e.preventDefault();
    if (reason.length === 0)
      setErrors({
        ...errors,
        reason: "Please add a reason",
      });
    try {
      const request = {
        withdrawalId: modal.id,
        reason: reason,
      };
      await cancelOrder(request).unwrap();
      withdrawList.refetch();
      setShow(false);
      // Handle successful cancellation response (e.g., show success message)
    } catch (error) {
      console.log(error);
      toast.error(error?.data?.message);
      // Handle error response (e.g., show error message)
    }
  };
  // const handleConfirm = async () => {
  //   await completeWithDraw({
  //     id: modal?.id,
  //     actionStr,
  //   })
  //     .unwrap()
  //     .catch((error) => {
  //       console.error(error);
  //       toast.error(error?.data?.message);
  //     });
  //   withdrawList.refetch();
  //   setShow(false);
  // };
  // if (isLoading) return <Loader />;

  const onSubmitWithdrawalComplete = async () => {
    const withdrawId = modal?.id;
    // const token = localStorage.getItem('token');
    // console.log(token);
    const url = `${endpoints.withdraw.compete_withdrawal}/${withdrawId}`;
    try {
      const res = await axios.put(url, {
        // headers: {
        //   Authorization: `Bearer ${token}`,
        //   'Content-Type': 'application/json',
        // }
      });
      setShow(false);
      withdrawList.refetch();
      return res?.data;
    } catch (error) {
      console.error("Failed to api call", error);
    }
  };
  if (isLoading) return <Loader />;
  return (
    <>
      <h1 className="my-2 px-5">{data?.length} Withdraws</h1>

      <MDBDataTable
        data={setWithDraw()}
        className="px-5 content mt-5"
        bordered
        striped
        hover
        responsive
      />

      <style jsx>{`
        .dataTables_wrapper .dataTables_length,
        .dataTables_wrapper .dataTables_filter,
        .dataTables_wrapper .dataTables_info,
        .dataTables_wrapper .dataTables_paginate {
          margin-bottom: 20px;
        }
        .table th,
        .table td {
          vertical-align: middle;
          text-align: center; /* Thêm thuộc tính này để căn giữa nội dung */
        }
        .table th {
          background-color: #f8f9fa;
        }
        .table td img {
          display: block;
          margin: auto;
        }
      `}</style>

      <Modal show={show} onHide={handleClose} size={"lg"}>
        <Modal.Header closeButton>
          <Modal.Title>Withdraw Detail</Modal.Title>
        </Modal.Header>
        <Modal.Body className="position-relative">
          {/* <p>
            <b>ID:</b> {modal?.id}
          </p> */}
          <p>
            <b>Amount:</b> {modal?.amount} {modal?.goldUnit}
          </p>
          <p>
            <b>Transaction Date:</b> {myDateFormat(modal?.transactionDate)}
          </p>
          <p>
            <b>Updated Date:</b> {myDateFormat(modal?.updateDate)}
          </p>
          <p>
            <b>Customer's:</b> {user?.userInfo?.firstName}{" "}
            {user?.userInfo?.lastName}
          </p>
          <p>
            <Label color={getStatusColor(modal?.status)}>
              {modal?.status}
              {modal?.status === "REJECTED" &&
                ": Product out of stock or inventory not Enough"}
            </Label>
          </p>
          <div className="position-absolute bg-white rounded-2 end-0 top-0 mx-5 my-3 p-3 shadow user-select-none ">
            <img
              src={generateWithdrawQRImg(modal?.withdrawQrCode)}
              alt={`${modal?.withdrawQrCode}`}
            />
            <div className="text-center user-select-all mt-2">
              {modal?.withdrawQrCode}
            </div>
          </div>
          {modal?.cancellationMessages?.length > 0 ? (
            <div>
              <hr></hr>
              <b>Cancellation Messages:</b>
              {/* {JSON.stringify(modal.cancellationMessages)} */}
              {modal.cancellationMessages.map((message, index) => (
                <p className="ps-3">
                  <div>
                    <b>Reason:</b> {message.reason}
                  </div>
                  <div>
                    <b>Sender:</b>{" "}
                    {message.sender === user?.username ? (
                      <b>You</b>
                    ) : (
                      message.sender
                    )}
                  </div>
                  <div>
                    <b>Receiver:</b>{" "}
                    {message.receiver === user?.username ? (
                      <b>You</b>
                    ) : (
                      message.receiver
                    )}
                  </div>
                </p>
              ))}
            </div>
          ) : (
            <>
              {modal?.status === "APPROVED" &&
                modal?.userConfirm !== "RECEIVED" && (
                  <>
                    <Button
                      onClick={() => setOpen(!open)}
                      variant="danger"
                      aria-controls="example-collapse-text"
                      aria-expanded={open}
                      className="mb-2"
                    >
                      Cancel Withdraw <FaCaretDown />
                    </Button>
                    <Collapse
                      in={open}
                      value={modal?.id}
                      title="Cancel Withdraws"
                    >
                      <Form onSubmit={onSubmit}>
                        <textarea
                          className="col-12 form-control"
                          rows="8"
                          name="reason"
                          placeholder="reason"
                          onChange={onChange}
                          style={{ minHeight: "200px", resize: "vertical" }}
                        />
                        <FormError errorData={errors} name={"reason"} />
                        <button className="btn btn-primary mt-2">Submit</button>
                      </Form>
                    </Collapse>
                  </>
                )}
            </>
          )}
        </Modal.Body>
        <Modal.Footer>
          {/* {modal?.status === "PENDING" && (
            <Popconfirm
              placement="bottom"
              icon={<FaCheckCircle className="mx-2 text-success" />}
              title="Confirmation"
              description="Are you sure to confirm this order?"
              onConfirm={handleConfirm}
              okText="Yes"
              cancelText="No"
            >
              <Button variant="success" disabled={confirming}>
                {confirming ? (
                  <>Confirming...</>
                ) : (
                  <>
                    Confirm Withdraw <FaCheckCircle />
                  </>
                )}
              </Button>
            </Popconfirm>
          )} */}
          {/* {modal?.status === "COMPLETED" &&
          modal?.userConfirm === "RECEIVED" ? (
            <Button variant="success" disabled>
              RECEIVED <FaCheckCircle />
            </Button>
          ) : (
            <Popconfirm
              placement="topRight"
              icon={<FaCheckCircle className="mx-2 text-success" />}
              title="Confirmation"
              description="Are you sure to complete withdraw?"
              onConfirm={onSubmitWithdrawalComplete}
              okText="Yes"
              cancelText="No"
            >
              <Button variant="success" disabled={confirming}>
                {confirming ? (
                  <>Confirming...</>
                ) : (
                  <>
                    RECEIVED <FaCheckCircle />
                  </>
                )}
              </Button>
            </Popconfirm>
          )}

          <Button variant="secondary" onClick={handleClose}>
            Close
          </Button> */}
          {modal?.status === "APPROVED" &&
          modal?.userConfirm === "NOT_RECEIVED" ? (
            <Popconfirm
              placement="topRight"
              icon={<FaCheckCircle className="mx-2 text-success" />}
              title="Confirmation"
              description="Are you sure to complete withdraw?"
              onConfirm={onSubmitWithdrawalComplete}
              okText="Yes"
              cancelText="No"
            >
              <Button variant="success" disabled={confirming}>
                {confirming ? (
                  <>Confirming...</>
                ) : (
                  <>
                    RECEIVED <FaCheckCircle />
                  </>
                )}
              </Button>
            </Popconfirm>
          ) : modal?.status === "COMPLETED" &&
            modal?.userConfirm === "RECEIVED" ? (
            <Button variant="success" disabled>
              RECEIVED <FaCheckCircle />
            </Button>
          ) : null}

          <Button variant="secondary" onClick={handleClose}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
      {/* Modal OTP */}
      {otpModalShow && (
        <OTPPage withdrawId={orderId} onClose={handleCloseOTPModal} />
      )}
    </>
  );
};

export default ListWithdraws;
