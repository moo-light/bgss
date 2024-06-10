import { Popconfirm } from "antd";
import { MDBDataTable } from "mdbreact";
import { useEffect, useState } from "react";
import { Button, Collapse, Form, Modal } from "react-bootstrap";
import toast from "react-hot-toast";
import { FaCaretDown, FaCheck, FaCheckCircle } from "react-icons/fa";
import { useSearchParams } from "react-router-dom";
import { FormError } from "../../../helpers/components/form-error";
import { colorFormatWithdraw } from "../../../helpers/format-color";
import { myDateFormat } from "../../../helpers/helpers";
import { generateWithdrawQRImg } from "../../../helpers/qrcode-helper";
import {
  useCancelWithdrawalMutation,
  useHandleWithdrawalMutation,
  useWithdrawListQuery,
} from "../../../redux/api/transactionApi";
import Loader from "../../layout/Loader";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Avatar from "@mui/material/Avatar";
import Rating from "@mui/material/Rating";
import Label from "../../../helpers/components/customMUI/label/label";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import { BASE_PATH } from "../../../constants/constants";
import { Link } from "react-router-dom";

const ListWithdraws = ({ params }) => {
  const [searchParams] = useSearchParams();
  const withdrawList = useWithdrawListQuery();
  const HOST = BASE_PATH;
  const { data, isLoading, error } = withdrawList;
  const [cancelOrder, { isLoading: canceling }] = useCancelWithdrawalMutation();
  const [confirmWithDraw, { isLoading: confirming }] =
    useHandleWithdrawalMutation();
  const actionStr = "CONFIRM_WITHDRAWAL";
  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }
  }, [error]);

  const setWithDraw = () => {
    const orders = {
      columns: [
        {
          label: "ID",
          field: "id",
          sort: "asc",
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
        },
        {
          label: "Update Date",
          field: "updateDate",
          sort: "asc",
        },
        {
          label: "Total Amount",
          field: "totalAmount",
          sort: "asc",
        },
        {
          label: "WithdrawQrCode",
          field: "withdrawQrCode",
          sort: "asc",
        },
        {
          label: "Status",
          field: "status",
          sort: "asc",
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
        totalAmount: withdraw?.amount + " " + withdraw?.goldUnit,
        withdrawQrCode: withdraw?.withdrawQrCode,
        userInfoId: withdraw?.userInfoId || "none",
        actions: (
          <>
            <button
              aria-label="View withdraw"
              className="btn btn-light  "
              onClick={(e) => showModal(withdraw)}
            >
              <i className="fa fa-eye"></i>
            </button>
            {(withdraw?.status === "APPROVED" ||
              (withdraw?.status === "COMPLETED" &&
                withdraw?.userConfirm === "RECEIVED")) && (
              <Link
                to={`/admin/invoice/withdraw/${withdraw?.id}`}
                className="btn btn-success ms-2"
              >
                <i className="fa fa-print"></i>
              </Link>
            )}
          </>
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
      withdrawList.refetch(params);
      // Handle successful cancellation response (e.g., show success message)
      setShow(false);
    } catch (error) {
      console.log(error);
      toast.error(error?.data?.message);
      // Handle error response (e.g., show error message)
    }
  };
  const handleConfirm = async () => {
    await confirmWithDraw({
      id: modal?.id,
      actionStr,
    })
      .unwrap()
      .catch((error) => {
        console.error(error);
        toast.error(error?.data?.message);
      });
    withdrawList.refetch();
    setShow(false);
  };
  const actionComplete = "COMPLETE_WITHDRAWAL";
  const handleComplete = async () => {
    await confirmWithDraw({
      id: modal?.id,
      actionStr: actionComplete,
      withdrawQrCode: modal?.withdrawQrCode,
    })
      .unwrap()
      .catch((error) => {
        console.error(error);
        toast.error(error?.data?.message);
      });
    withdrawList.refetch();
    setShow(false);
  };

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

  if (isLoading) return <Loader />;
  return (
    <>
      <h1 className="my-2 px-5">{data?.data?.length} Withdraws</h1>

      <MDBDataTable data={setWithDraw()} bordered striped hover responsive />
      <style jsx>{`
        .dataTables_wrapper .dataTables_length,
        .dataTables_wrapper .dataTables_filter,
        .dataTables_wrapper .dataTables_info,
        .dataTables_wrapper .dataTables_paginate {
          margin-bottom: 20px; /* Điều chỉnh giá trị theo ý muốn của bạn */
        }
        .table th,
        .table td {
          vertical-align: middle;
        }
        .table th {
          background-color: #f8f9fa;
        }
        .table th,
        .table td {
          text-align: center;
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
        <Modal.Body>
          <p>
            <b>ID:</b> {modal?.id}
          </p>
          <p>
            <b>Amount:</b> {modal?.amount} tOz
          </p>
          <p>
            <b>Transaction Date:</b> {myDateFormat(modal?.transactionDate)}
          </p>
          <p>
            <b>Updated Date:</b> {myDateFormat(modal?.updateDate)}
          </p>
          <p>
            <p>
              <b>QrCode:</b> {modal?.withdrawQrCode}
            </p>
            <b>User ID:</b> {modal?.userInfoId}
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
              {modal.cancellationMessages.map((message, index) => (
                <p className="ps-3" key={index}>
                  <div>
                    <b>Sender:</b> {message.sender}
                  </div>
                  <div>
                    <b>Receiver:</b> {message.receiver}
                  </div>
                  <div>
                    <b>Reason:</b> {message.reason}
                  </div>
                </p>
              ))}
            </div>
          ) : (
            <>
              {modal?.status === "APPROVED" &&
              modal?.userConfirm !== "RECEIVED" &&
              modal?.status !== "COMPLETED" ? (
                <Button
                  onClick={() => setOpen(!open)}
                  variant="danger"
                  aria-controls="example-collapse-text"
                  aria-expanded={open}
                  className="mb-2"
                >
                  Cancel Withdraw <FaCaretDown />
                </Button>
              ) : (
                <div>
                  <Label color={getStatusColor(modal?.status)}>
                    {modal?.status}
                    {modal?.status === "REJECTED" &&
                      ": Product out of stock or inventory not Enough"}
                  </Label>
                  {/* <CheckCircleIcon color="success" /> */}
                </div>
              )}
              <Collapse in={open} value={modal?.id} title="Cancel Withdraws">
                <Form onSubmit={onSubmit}>
                  <textarea
                    className="col-12 form-control  "
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
        </Modal.Body>
        <Modal.Footer>
          {modal?.status === "PENDING" && (
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
          )}
          {modal?.status === "CONFIRMED" && (
            <Popconfirm
              placement="topRight"
              icon={<FaCheckCircle className="mx-2 text-success" />}
              title="Confirmation"
              description="Are you sure to complete withdraw?"
              onConfirm={handleComplete}
              okText="Yes"
              cancelText="No"
            >
              <Button variant="success" disabled={confirming}>
                {confirming ? (
                  <>Confirming...</>
                ) : (
                  <>
                    Complete Withdraw <FaCheckCircle />
                  </>
                )}
              </Button>
            </Popconfirm>
          )}
          <Button variant="secondary" onClick={handleClose}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
    </>
  );
};
export default ListWithdraws;
