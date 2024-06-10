import { MDBDataTable } from "mdbreact";
import { useEffect } from "react";
import { Form } from "react-bootstrap";
import toast from "react-hot-toast";
import { Link, useSearchParams } from "react-router-dom";
import { GOLD_UNIT_CONVERT_2 } from "../../../helpers/converters";
import { currencyFormat } from "../../../helpers/helpers";
import { useTransactionListQuery } from "../../../redux/api/transactionApi";
import Loader from "../../layout/Loader";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Avatar from "@mui/material/Avatar";
import Rating from "@mui/material/Rating";
import Label from "../../../helpers/components/customMUI/label/label";

const ListTransactions = ({}) => {
  const [searchParams] = useSearchParams();
  const { data, isLoading, error } = useTransactionListQuery();
  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }
  }, [error]);
  const setTransactions = () => {
    const transactions = {
      columns: [
        {
          label: "ID",
          field: "id",
          sort: "asc",
          attributes: { className: "text-center" },
        },
        {
          label: "Transaction Type",
          field: "transactionType",
          sort: "asc",
          attributes: { className: "text-center" },
        },
        {
          label: "Bid/Ask",
          field: "pricePerOunce",
          sort: "asc",
          attributes: { className: "text-center" },
        },
        {
          label: "Quantity",
          field: "quantity",
          sort: "asc",
          attributes: { className: "text-center" },
        },
        // {
        //   label: "Gold Unit",
        //   field: "goldUnit",
        //   sort: "asc",
        //   attributes: { className: "text-center" },
        // },
        {
          label: "total Cost/Profit",
          field: "totalCostOrProfit",
          sort: "asc",
          attributes: { className: "text-center" },
        },
        {
          label: "Signature Status",
          field: "transactionSignature",
          sort: "asc",
          attributes: { className: "text-center" },
        },
        {
          label: "Transaction  Status",
          field: "status",
          sort: "asc",
          attributes: { className: "text-center" },
        },
        {
          label: "Confirming Party",
          field: "confirmingParty",
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
    data?.forEach((transaction) => {
      transactions.rows.push({
        id: transaction?.id,
        transactionType: (
          <Label
            variant="soft"
            color={
              (transaction?.transactionType === "BUY" && "success") ||
              (transaction?.transactionType === "SELL" && "error")
            }
            sx={{ mx: "auto" }}
          >
            {transaction?.transactionType}
          </Label>
        ),
        transactionSignature: (
          // <div
          //   className={`fw-bold ${
          //     transaction?.transactionSignature === "SIGNED"
          //       ? "text-success"
          //       : "text-danger"
          //   }`}
          // >
          //   {transaction?.transactionSignature}
          // </div>
          <Label
            variant="soft"
            color={
              (transaction?.transactionSignature === "UNSIGNED" && "error") ||
              (transaction?.transactionSignature === "SIGNED" && "success")
            }
            sx={{ mx: "auto" }}
          >
            {transaction?.transactionSignature}
          </Label>
        ),
        totalCostOrProfit: (
          <div
            className={`fw-bold
                ${transaction?.transactionType === "SELL" && "text-success"}
                ${transaction?.transactionType === "BUY" && "text-danger"}`}
          >
            {currencyFormat(transaction?.totalCostOrProfit)}
          </div>
        ),
        goldUnit: transaction?.goldUnit,
        status: (
          <div>
            <Label
              variant="soft"
              color={
                (transaction?.transactionVerification === "UNVERIFIED" &&
                  "error") ||
                (transaction?.transactionVerification === "VERIFIED" &&
                  "success")
              }
              sx={{ mx: "auto" }}
            >
              {transaction?.transactionVerification}
            </Label>
          </div>
        ),
        quantity: `${transaction?.quantity} ${
          GOLD_UNIT_CONVERT_2[transaction?.goldUnit]
        }`,
        pricePerOunce: currencyFormat(transaction?.pricePerOunce),
        confirmingParty: transaction?.confirmingParty,
        actions: (
          <>
            {/* <Link
              to={`/me/transaction/${transaction?.id}`}
              className="btn btn-primary"
            >
              <i className="fa fa-eye"></i>
            </Link> */}
            {transaction?.transactionSignature === "SIGNED" && (
              <Link
                to={`/admin/invoice/transaction/${transaction?.id}`}
                className="btn btn-success ms-2"
              >
                <i className="fa fa-print"></i>
              </Link>
            )}
          </>
        ),
      });
    });

    return transactions;
  };
  if (isLoading) return <Loader />;

  return (
    <>
      <p>
        <Form>
          <Form.Group></Form.Group>
        </Form>
      </p>
      <h1 className="my-2 px-5">{data?.length} Transactions</h1>
      <div className="mt-5 content">
        <p className="text-50">
          Total prices are calculated using Troy Ounces (tOz)
        </p>

        <MDBDataTable
          data={setTransactions()}
          className="px-5"
          bordered
          striped
          hover
          responsive
        />
      </div>
      <style jsx>{`
        .dataTables_wrapper .dataTables_length,
        .dataTables_wrapper .dataTables_filter,
        .dataTables_wrapper .dataTables_info,
        .dataTables_wrapper .dataTables_paginate {
          margin-bottom: 20px; /* Điều chỉnh giá trị theo ý muốn của bạn */
        }
        th,
        td {
          text-align: center;
          vertical-align: middle;
        }
      `}</style>
    </>
  );
};
export default ListTransactions;
