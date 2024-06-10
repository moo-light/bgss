import html2canvas from "html2canvas";
import { jsPDF } from "jspdf";
import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import { useParams } from "react-router-dom";
import { currencyFormat, myDateFormat } from "../../helpers/helpers";
import {
  useMyTransactionListQuery,
  useTransactionListQuery,
} from "../../redux/api/transactionApi";
import { useGetUserDetailsQuery } from "../../redux/api/userApi";
import Loader from "../layout/Loader";
import MetaData from "../layout/MetaData";
import "./invoice.css";

const TransactionInvoice = () => {
  const params = useParams();
  const { data, isLoading, error } = useTransactionListQuery();
  // const { data, isLoading, error, refetch } = useGetUserDetailsQuery();
  const [transaction, setTransaction] = useState(null);
  const userResult = useGetUserDetailsQuery(transaction?.actionPartyId);

  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }
  }, [error]);

  useEffect(() => {
    if (transaction) userResult.refetch();
  }, [transaction]);

  useEffect(() => {
    if (data) {
      const selectedTransaction = data.find(
        (item) => item.id === parseInt(params.id)
      );
      setTransaction(selectedTransaction);
    }
  }, [data, params.id]);

  const getTransactionTypeColor = (type) => {
    switch (type) {
      case "BUY":
        return "text-success"; // Màu xanh cho BUY
      case "SELL":
        return "text-danger"; // Màu đỏ cho SELL
      default:
        return ""; // Mặc định không có màu
    }
  };
  const handleDownload = () => {
    const input = document.getElementById("transaction_invoice");
    html2canvas(input, { useCORS: true }).then((canvas) => {
      const imgData = canvas.toDataURL("image/png");
      const pdf = new jsPDF();
      const pdfWidth = pdf.internal.pageSize.getWidth();
      const pdfHeight = (canvas.height * pdfWidth) / canvas.width;
      pdf.addImage(imgData, "PNG", 0, 0, pdfWidth, pdfHeight);
      pdf.save(`BGSS_Transaction_${transaction?.id}.pdf`);
    });
  };

  const isPaid = (value) => {
    switch (value) {
      case "PAID":
        return "greenColor";
      case "CANCELLED":
        return "redColor";
      default:
        return "yellowColor";
    }
  };
  const isContract = (value) => {
    switch (value) {
      case "DIGITAL_SIGNED":
        return "greenColor";
      case "DIGITAL_UNSIGNED":
        return "redColor";
      default:
        return "yellowColor";
    }
  };

  if (isLoading) return <Loader />;
  console.log(transaction);
  return (
    <div>
      <MetaData title={"Transaction Invoice"} />
      <div className="transaction-invoice my-5">
        <div className="row d-flex justify-content-center mb-5">
          {transaction?.contract?.contractStatus === "DIGITAL_SIGNED" && (
            <button
              className="btn btn-success col-md-5"
              onClick={handleDownload}
            >
              <i className="fa fa-print"></i> Download Invoice
            </button>
          )}
        </div>
        {transaction && (
          <div id="transaction_invoice" className="p-3 border border-secondary">
            <header className="clearfix">
              <div id="logo">
                <img src="/images/BGSS_logo_large.png" alt="Company Logo" />
              </div>
              <h1>INVOICE # {transaction.id}</h1>
              <div className="container">
                <h3 className="mb-4 mt-2 ml-3">User Info</h3>
                <div className="row justify-content-center">
                  <div className="col-md-8">
                    <div className="card">
                      <div className="card-header bg-primary text-white">
                        Transaction Information
                      </div>
                      <div className="card-body">
                        <div className="table-responsive">
                          <table className="table table-bordered">
                            <tbody>
                              <tr>
                                <th scope="row" className="text-start">
                                  Name
                                </th>
                                <td>{transaction?.contract?.fullName}</td>
                              </tr>
                              {/* <tr>
                                <th scope="row" className="text-start">
                                  Email
                                </th>
                                <td>{transaction.email}</td>
                              </tr>
                              <tr>
                                <th scope="row" className="text-start">
                                  Phone No
                                </th>
                                <td>{phoneFormat(transaction.phoneNumber)}</td>
                              </tr> */}
                              <tr>
                                <th scope="row" className="text-start">
                                  Address
                                </th>
                                <td>{transaction?.contract?.address}</td>
                              </tr>
                              <tr>
                                <th scope="row" className="text-start">
                                  Created Date
                                </th>
                                <td>
                                  {myDateFormat(
                                    transaction?.contract?.createdAt
                                  )}
                                </td>
                              </tr>
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
                      <th scope="row">CompanyName</th>
                      <td>
                        <strong>
                          {" "}
                          {transaction?.contract?.confirmingParty ??
                            "none"}{" "}
                        </strong>
                      </td>
                    </tr>
                    <tr>
                      <th scope="row">Status</th>
                      <td className={isPaid(transaction.statusReceived)}>
                        <b>{transaction.transactionStatus}</b>
                      </td>
                    </tr>
                    <tr>
                      <th scope="row">TransactionType</th>
                      <td
                        className={getTransactionTypeColor(
                          transaction?.contract?.transactionType
                        )}
                      >
                        <strong>
                          {transaction?.contract?.transactionType ?? "none"}
                        </strong>
                      </td>
                    </tr>
                    <tr>
                      <th scope="row">Quantity</th>
                      <td>{transaction?.quantity ?? "none"}</td>
                    </tr>
                    <tr>
                      <th scope="row">GoldUnit</th>
                      <td>{transaction?.goldUnit ?? "none"}</td>
                    </tr>
                    <tr>
                      <th scope="row">Amount</th>
                      <td>
                        {currencyFormat(
                          transaction?.contract?.totalCostOrProfit
                        )}
                      </td>
                    </tr>
                    <tr>
                      <th scope="row">StatusContract</th>
                      <td
                        className={isContract(
                          transaction?.contract?.contractStatus
                        )}
                      >
                        <b>{transaction?.contract?.contractStatus}</b>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </header>
            <main>
              <div className="row mt-4 signature-container">
                <div className="col-md-6">
                  <div className="border border-dark p-3 text-center">
                    <h4 className="mb-4">Customer's Signature</h4>
                    <div className="signature-box">
                      <img
                        src={`data:image/png;base64, ${transaction?.contract?.signatureActionParty}`}
                        alt="Customer's Signature"
                        className="signature-img"
                      />
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
                    <div className="signature-box">
                      <img
                        src={`data:image/png;base64, ${transaction?.contract?.signatureConfirmingParty}`}
                        alt="Customer's Signature"
                        className="signature-img"
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
            </main>
          </div>
        )}
      </div>
    </div>
  );
};

export default TransactionInvoice;
