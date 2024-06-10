import html2canvas from "html2canvas";
import { jsPDF } from "jspdf";
import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import { useSelector } from "react-redux";
import { useParams } from "react-router-dom";
import { myDateFormat, phoneFormat } from "../../helpers/helpers";
import {
  useMyWithdrawListQuery,
  useWithdrawListQuery,
} from "../../redux/api/transactionApi";
import Loader from "../layout/Loader";
import MetaData from "../layout/MetaData";
import "./invoice.css";
import Box from "@mui/material/Box";
import Avatar from "@mui/material/Avatar";
import Rating from "@mui/material/Rating";
import Typography from "@mui/material/Typography";
import { BASE_PATH } from "../../constants/constants";
import { useGetUserDetailsQuery } from "../../redux/api/userApi";

const WithdrawInvoice = () => {
  const params = useParams();
  const { data, isLoading, error } = useWithdrawListQuery();
  // const { user } = useSelector((state) => state.auth);
  const [withdraw, setWithdraw] = useState(null);
  const userResult = useGetUserDetailsQuery(withdraw?.userInfoId);
  const HOST = BASE_PATH;

  const user = userResult.data?.data;

  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }
  }, [error]);
  useEffect(() => {
    if (withdraw) userResult.refetch(withdraw.userInfoId);
  }, [withdraw]);
  useEffect(() => {
    console.log("Data:", data);
    console.log("Params ID:", params.id);
    if (data && Array.isArray(data.data)) {
      const selectedWithdraw = data.data.find(
        (item) => item.id === parseInt(params.id)
      );
      console.log("Selected Withdraw:", selectedWithdraw);
      if (selectedWithdraw) {
        setWithdraw(selectedWithdraw);
        console.log("Selected Withdraw:", selectedWithdraw);
      } else {
        console.log("No matching withdrawal found");
      }
    }
  }, [data, params.id]);

  if (!user) return null;
  const { userInfo } = user;
  const handleDownload = () => {
    const input = document.getElementById("withdraw_invoice");
    html2canvas(input, { useCORS: true }).then((canvas) => {
      const imgData = canvas.toDataURL("image/png");
      const pdf = new jsPDF();
      const pdfWidth = pdf.internal.pageSize.getWidth();
      const pdfHeight = (canvas.height * pdfWidth) / canvas.width;
      pdf.addImage(imgData, "PNG", 0, 0, pdfWidth, pdfHeight);
      pdf.save(`BGSS_Withdraw_${withdraw?.id}.pdf`);
    });
  };
  if (isLoading || withdraw === null) {
    return <Loader />;
  }
  const isStatusPaid = (value) => {
    switch (value) {
      case "PENDING":
        return "greenColor";
      case "CANCELLED":
        return "redColor";
      default:
        return "yellowColor";
    }
  };

  if (isLoading) return <Loader />;
  console.log(withdraw);

  return (
    <div>
      <MetaData title={"Withdraw Invoice"} />
      <div className="withdraw-invoice my-5">
        <div className="row d-flex justify-content-center mb-5">
          {withdraw?.status === "APPROVED" && (
            <button
              className="btn btn-success col-md-5"
              onClick={handleDownload}
            >
              <i className="fa fa-print"></i> Download Invoice
            </button>
          )}
        </div>
        {withdraw && (
          <div id="withdraw_invoice" className="p-3 border border-secondary">
            <header className="clearfix">
              <div id="logo">
                <img src="/images/BGSS_logo_large.png" alt="Company Logo" />
              </div>
              <h1>WITHDRAW INVOICE # {withdraw?.id}</h1>
              <div className="container">
                <h3 className="mb-4 mt-2 ml-3">User Info</h3>
                <div className="row justify-content-center">
                  <div className="col-md-8">
                    <div className="card">
                      <div className="card-header bg-primary text-white">
                        Withdraw Information
                      </div>
                      <div className="card-body">
                        <div className="table-responsive">
                          <table className="table table-bordered">
                            <tbody>
                              <tr>
                                <th scope="row" className="text-start">
                                  Name
                                </th>
                                <td>
                                  {user?.firstName + " " + user?.lastName}
                                </td>
                              </tr>
                              <tr>
                                <th scope="row" className="text-start">
                                  Email
                                </th>
                                <td>{user?.email}</td>
                              </tr>
                              <tr>
                                <th scope="row" className="text-start">
                                  Phone No
                                </th>
                                <td>
                                  {phoneFormat(user?.userInfo?.phoneNumber)}
                                </td>
                              </tr>
                              <tr>
                                <th scope="row" className="text-start">
                                  Address
                                </th>
                                <td>{user?.userInfo?.address}</td>
                              </tr>
                              <tr>
                                <th scope="row" className="text-start">
                                  Created Date
                                </th>
                                <td>
                                  {myDateFormat(withdraw?.transactionDate)}
                                </td>
                              </tr>
                              {/* <tr>
                              <th scope="row" className="text-start">
                                Status
                              </th>
                              <td>{order?.statusReceived}</td>
                            </tr> */}
                            </tbody>
                          </table>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div className="container">
                <h3 className="mt-5 mb-4">Transaction Items</h3>
                <table className="table table-striped table-bordered ">
                  <tbody>
                    <tr>
                      <th scope="row">Withdrawal Status</th>
                      <td className={withdraw?.status}>
                        <strong>{withdraw?.status ?? "none"}</strong>
                      </td>
                    </tr>
                    <tr>
                      <th scope="row">Amount</th>
                      <td>{withdraw?.amount ?? "none"}</td>
                    </tr>
                    <tr>
                      <th scope="row">GoldUnit</th>
                      <td>{withdraw?.goldUnit ?? "none"}</td>
                    </tr>
                    <tr>
                      <th scope="row">Product</th>
                      <td>
                        <Box
                          mt={2}
                          display="flex"
                          alignItems="center"
                          marginBottom={3}
                          marginTop={3}
                          sx={{ justifyContent: "flex-end" }}
                        >
                          <Avatar
                            key={withdraw.productDTO?.id}
                            alt={withdraw.productDTO?.productName}
                            src={`${HOST}/${withdraw.productDTO?.productImages?.[0]?.imgUrl}`}
                            variant="rounded"
                            sx={{
                              width: 80,
                              height: 80,
                              flexShrink: 0,
                              mr: 1.5,
                              borderRadius: 1,
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
                              {withdraw.productDTO?.typeGold?.typeName}
                            </Typography>
                            <Box display="flex" alignItems="center">
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
                            </Box>
                          </Box>
                        </Box>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </header>
            <man>
              <div className="row mt-4 signature-container">
                <div className="col-md-6">
                  <div className="border border-dark p-3 text-center">
                    <h4 className="mb-4">Customer's Signature</h4>
                    <div className="signature-box">
                      {/* Chữ kí của khách hàng */}
                    </div>
                    <div className="signature-info">
                      <p className="mt-3">
                        <b>Signature:</b> ___________________
                      </p>
                      <p>
                        <b>Date:</b> ___________________
                      </p>
                    </div>
                  </div>
                </div>
                <div className="col-md-6">
                  <div className="border border-dark p-3 text-center">
                    <h4 className="mb-4">Company's Stamp</h4>
                    <div className="stamp-box">
                      {/* Con dấu của công ty */}
                      <img
                        src="/images/BGSS110.png"
                        alt="Company Stamp"
                        className="stamp-img"
                      />
                    </div>
                    <div className="stamp-info">
                      <p className="mt-3">
                        <b>Signature:</b> ___________________
                      </p>
                      <p>
                        <b>Date:</b> ___________________
                      </p>
                    </div>
                  </div>
                </div>
              </div>
            </man>
          </div>
        )}
      </div>
    </div>
  );
};

export default WithdrawInvoice;
