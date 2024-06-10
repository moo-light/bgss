import { MDBDataTable } from "mdbreact";
import React, { useEffect, useState } from "react";
import { Button, Modal } from "react-bootstrap";
import toast from "react-hot-toast";
import { FaInfoCircle, FaKey, FaPenAlt } from "react-icons/fa";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import { GOLD_UNIT_CONVERT_2 } from "../../helpers/converters";
import { currencyFormat } from "../../helpers/helpers";
import {
  useAcceptTransactionMutation,
  useMyTransactionListQuery,
} from "../../redux/api/transactionApi";
import Loader from "../layout/Loader";
import Label from "../../helpers/components/customMUI/label/label";


const ListTransactions = () => {
  const { data, isLoading, error, refetch } = useMyTransactionListQuery();
  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }
  }, [error]);

  const { user } = useSelector((state) => state.auth);

  const [showModal, setShowModal] = useState(false);
  const [transactionId, setTransactionId] = useState(null);
  const [publicKey, setPublicKey] = useState("");

  const [handleWithdrawal] = useAcceptTransactionMutation();

  const handleActionClick = (id) => {
    setTransactionId(id);
    setShowModal(true);
  };

  const handleConfirm = async () => {
    try {
      const response = await handleWithdrawal({
        transactionId,
        publicKey,
      }).unwrap();
      // Xử lý response nếu cần
      setShowModal(false);
      refetch(); // Load lại dữ liệu của bảng
      toast.success("Transaction successfully processed!");
      handleUnsignedClick();
    } catch (error) {
      console.error("Error handling Transaction:", error.data.message);
      toast.error(error.data.message);
    }
  };

  const handleClose = () => {
    setShowModal(false);
  };

  const handleUnsignedClick = () => {
    toast("Please Sign in with your mobile phone for signature confirmation!", {
      icon: <FaInfoCircle className="text-info" style={{ width: 25 }} />,
    });
  };



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
        {
          label: "Gold Unit",
          field: "goldUnit",
          sort: "asc",
          attributes: { className: "text-center" },
        },
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
          attributes: { className: "text-center" },
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
            (transaction?.transactionType === 'BUY' && 'success') ||
            (transaction?.transactionType === 'SELL' && 'error')
          }
          sx={{ mx: 'auto' }}
        >
          {transaction?.transactionType}
        </Label>
        ),
        totalCostOrProfit: (
          <div
            className={`fw-bold 
            ${transaction?.transactionType === "SELL" && "text-success"} 
            ${transaction?.transactionType === "BUY" && "text-danger"}`}
          >
            {transaction?.transactionType === "SELL" ? "+" : ""}
            {currencyFormat(transaction?.totalCostOrProfit)}
          </div>
        ),
        goldUnit: transaction?.goldUnit,
        status: (
          // <div
          //   className={`fw-bold ${
          //     transaction?.transactionVerification === "VERIFIED"
          //       ? "text-success"
          //       : "text-danger"
          //   }`}
          // >
          //   {transaction?.transactionVerification}
          // </div>
          <div>
          <Label
       variant="soft"
       color={
         (transaction?.transactionVerification === 'UNVERIFIED' && 'error') ||
         (transaction?.transactionVerification === 'VERIFIED' && 'success')
       }
       sx={{ mx: 'auto' }}
     >
       {transaction?.transactionVerification}
     </Label>
       </div>
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
            (transaction?.transactionSignature === 'UNSIGNED' && 'error') ||
            (transaction?.transactionSignature === 'SIGNED' && 'success')
          }
          sx={{ mx: 'auto' }}
        >
          {transaction?.transactionSignature}
        </Label>
        ),
        // transactionSignature: transaction?.transactionSignature,
        quantity: `${transaction?.quantity} ${
          GOLD_UNIT_CONVERT_2[transaction?.goldUnit]
        }`,
        pricePerOunce: currencyFormat(transaction?.pricePerOunce),
        confirmingParty: transaction?.confirmingParty,
        actions: (
          <div className="d-flex">
            {transaction?.transactionSignature === "UNSIGNED" && (
              <button
                className="btn btn-light border ms-2"
                onClick={handleUnsignedClick}
              >
                <FaPenAlt />
              </button>
            )}

            {transaction?.transactionVerification === "UNVERIFIED" && (
              <button
                onClick={() => handleActionClick(transaction?.id)}
                className="btn btn-primary ms-2"
              >
                Enter Key <FaKey />
              </button>
            )}

            {transaction?.transactionSignature === "SIGNED" && (
              <Link
                to={`/invoice/transaction/${transaction?.id}`}
                className="btn btn-success ms-2"
              >
                <i className="fa fa-print"></i>
              </Link>
            )}
          </div>
        ),
      });
    });

    return transactions;
  };

  if (isLoading) return <Loader />;

  return (
    <>
      {/* Modal */}
      <Modal show={showModal} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>Enter Your Secret Key</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div className="mb-3">
            <label htmlFor="secretKey" className="form-label">
              Secret Key:
            </label>
            <input
              type="text"
              className="form-control"
              id="secretKey"
              value={publicKey}
              onChange={(e) => setPublicKey(e.target.value)}
            />
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleClose}>
            Close
          </Button>
          <Button variant="primary" onClick={handleConfirm}>
            Confirm
          </Button>
        </Modal.Footer>
      </Modal>

      {/* ListTransactions component */}
      <h1 className="my-2 px-5">{data?.length} Transactions</h1>
      {/* <p className="text-50">
        Our total prices are calculated using Troy Ounces (t oz)
      </p> */}
      <MDBDataTable
        data={setTransactions()}
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
