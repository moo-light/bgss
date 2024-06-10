import React from "react";
import { useDispatch, useSelector } from "react-redux";

import { useNavigate, useSearchParams } from "react-router-dom";
import { GOLD_UNIT_CONVERT_2 } from "../../helpers/converters";
import {
  caluclateTradeCost,
  currencyFormat,
  myDateFormat,
  phoneFormat,
} from "../../helpers/helpers";
import { useMyTransactionListQuery } from "../../redux/api/transactionApi";
import MetaData from "../layout/MetaData";

const TradeOrder = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { user } = useSelector((state) => state.auth);
  const { conversionFactors } = useSelector((state) => state.transfer);
  const transactionId = searchParams.get("id");
  const { data: liveData } = useSelector((state) => state.liveRate);
  const { data, isSuccess, error, isError, isLoading } =
    useMyTransactionListQuery();

  const tradeData = data?.find((e) => e.id == transactionId);
  const { itemsPrice, taxPrice, totalPrice } = caluclateTradeCost({
    ...tradeData,
    rates: Number(liveData?.rates),
    conversionFactors,
  });
  // Can't create another order if there is currently an order
  const handleSubmit = async () => {};
  // if (!transactionId) return <Navigate to={"/"}></Navigate>;
  const {
    userInfo: { inventory, balance },
  } = user;
  return (
    <>
      <MetaData title={"Confirm Trade Info"} />
      <div className="row d-flex justify-content-between">
        <div className="col-12 col-lg-8 mt-5 order-confirm">
          <h4 className="mb-3">User Infomation</h4>
          <p>
            <b>Name:</b> {user?.name}
          </p>
          <p className="mb-3">
            <b>Email:</b> {user?.email}
          </p>
          <p className="mb-3">
            <b>Phone:</b> {phoneFormat(user?.userInfo?.phoneNumber)}
          </p>
          <p className="mb-3">
            <b>Address:</b> {user?.userInfo?.address}
          </p>

          <hr />
          <h4 className="mt-4">Your Trade Order:</h4>
          <p>
            <img
              className="col-1"
              src={"/images/gold-bar.png"}
              alt="Gold Bar"
            />
          </p>
          <p>
            <b>Created:</b> {myDateFormat(tradeData?.createdAt)}
          </p>
          <p>
            <b>Gold Unit:</b> {tradeData?.goldUnit?.toUpperCase()}
          </p>
          <p className="mb-3">
            <b>Trade Type:</b>{" "}
            <c
              className={
                tradeData?.transactionType === "BUY" ? "greenColor" : "redColor"
              }
            >
              {tradeData?.transactionType}
            </c>
          </p>
          <p className="mb-3">
            <b>Quantity:</b>{" "}
            {tradeData?.quantity +
              " " +
              GOLD_UNIT_CONVERT_2[tradeData?.goldUnit]}
          </p>
          <p className={`mb-3`}>
            <b>Price:</b> {currencyFormat(tradeData?.pricePerOunce)}
          </p>
          <hr></hr>
          <p className="mb-1">
            <b>Verification:</b> {tradeData?.transactionVerification}
          </p>
          {/* <p className="mb-3">
            <b>Signature:</b> {tradeData?.transactionSignature}
          </p> */}
        </div>

        <div className="col-12 col-lg-4 col-xl-3 my-4">
          <div id="order_summary">
            <div className="position-relative">
              Balance:{" "}
              <b className=" gold ">{currencyFormat(balance?.amount)}</b>
            </div>
            <div className="position-relative">
              Inventory: <b>{inventory?.totalWeightOz + " tOz"}</b>
            </div>
            <hr></hr>
            <h4>Trade Summary</h4>
            {/* <UserStorage seperate="true" /> */}
            <hr />
            <p>
              Subtotal:
              <span className={`order-summary-values position-relative`}>
                {currencyFormat(itemsPrice)}
              </span>
            </p>
            <p>
              Tax:{" "}
              <span className="order-summary-values">
                {currencyFormat(taxPrice)}
              </span>
            </p>
            <hr />
            <p>
              Total:{" "}
              <span className="order-summary-values">
                {currencyFormat(totalPrice)}
              </span>
            </p>
            <hr />
            <button
              id="checkout_btn"
              onClick={handleSubmit}
              disabled={isLoading}
              className="btn btn-primary w-100"
            >
              Confirm Trade
            </button>
          </div>
        </div>
      </div>
    </>
  );
};

export default TradeOrder;
