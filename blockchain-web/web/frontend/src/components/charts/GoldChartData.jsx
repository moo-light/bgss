import React, { useEffect, useRef, useState } from "react";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import {
  FaInfoCircle,
  FaLongArrowAltDown,
  FaLongArrowAltUp,
} from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";
import SockJS from "sockjs-client";
import { BASE_HOST } from "../../constants/constants";
import WeightConversionModal from "../../helpers/components/weight-conversion-modal";
import { currencyFormat } from "../../helpers/helpers";
import { setLiveRate } from "../../redux/features/liveRateSlice";
import { BuyNSell } from "../layout/BuyNSell";
// import UserStorage from "../layout/UserStorage";

var Stomp = require("stompjs");

const GoldChartData = ({ className }) => {
  const dispatch = useDispatch();
  const { data, marketClosed } = useSelector((state) => {
    return state.liveRate;
  });
  const { user, roles, isAuthenticated } = useSelector((state) => state.auth);
  // Websocket
  const allowReconnect = useRef(true);
  const client = useRef(null);

  useEffect(() => {
    if (stompClient == null) connectToWebSocket();
    return () => {
      // Cleanup function when component unmounts
      closeWebSocket(); // Call the function to close WebSocket connection
      allowReconnect.current = false;
    };
  }, []);

  // Run this effect only once when the component mounts
  var stompClient = client.current;
  var reconnectAttempts = 0;
  var maxReconnectAttempts = 1000;
  var reconnectDelayMs = 10000;
  function connectToWebSocket() {
    var socket = new SockJS(`${BASE_HOST}/api/auth/ws`);

    stompClient = Stomp.over(socket);
    stompClient.debug = (f) => f;
    stompClient.connect(
      {},
      (frame) => {
        // Subcribe đến endpoint cung cấp updates
        if (stompClient.connected) {
          // console.log("Connected: " + frame);
          stompClient.subscribe("/api/auth/topic/forexsocket", (message) => {
            const liveRate = JSON.parse(message.body);
            // console.log(liveRate);
            dispatch(setLiveRate(liveRate));
          });
        }
      },
      function (error) {
        console.log("Connection failed: " + error);
        allowReconnect.current = true;
        attemptReconnect();
      }
    );
    client.current = stompClient;
  }
  function attemptReconnect() {
    if (!allowReconnect) return;
    if (reconnectAttempts < maxReconnectAttempts) {
      setTimeout(function () {
        console.log(
          `Attempting WebSocket reconnection (attempt ${reconnectAttempts + 1})`
        );
        reconnectAttempts++;
        connectToWebSocket();
      }, reconnectDelayMs);
    } else {
      console.log("Max WebSocket reconnection attempts reached.");
    }
  }

  function closeWebSocket() {
    if (stompClient?.connected) {
      stompClient.disconnect(() => {
        console.debug("WebSocket connection closed.");
      });
    }
    // Dispatch Redux action to handle WebSocket connection cleanup in your Redux store
  }
  //EndWebsocket
  // priceData
  const priceData = () => (
    <article
      className={`d-flex align-content-end ${!!marketClosed && "opacity-75"}`}
    >
      <span
        style={{
          color:
            data?.changedPrice < 0 ? "var(--loss-color)" : "var(--win-color)",
          fontSize: "32px",
          fontWeight: "bold",
        }}
      >
        {(data?.mid ?? 0).toFixed(2)}
      </span>
      {data?.changedPercentage != null && (
        <div className="mt-auto pb-2">
          {data.changedPrice < 0 ? (
            <FaLongArrowAltDown
              style={{
                fontSize: "20px",
                color: "var(--loss-color)",
              }}
            />
          ) : (
            <FaLongArrowAltUp
              style={{
                fontSize: "20px",
                color: "var(--win-color)",
              }}
            />
          )}
        </div>
      )}

      {data?.changedPercentage != null && (
        <div style={{ display: "flex", flexDirection: "column" }}>
          <div
            style={{
              color:
                data.changedPrice < 0
                  ? "var(--loss-color)"
                  : "var(--win-color)",
              fontSize: "14px",
            }}
          >
            <span style={{ width: "10px", textAlign: "center" }}>
              {data.changedPrice < 0 ? "-" : "+"}
            </span>
            <span>{Math.abs(data.changedPrice).toFixed(3)}</span>
          </div>
          <div
            style={{
              color:
                data.changedPercentage < 0
                  ? "var(--loss-color)"
                  : "var(--win-color)",
              fontSize: "14px",
            }}
          >
            <span style={{ width: "10px", textAlign: "center" }}>
              {data.changedPercentage < 0 ? "-" : "+"}
            </span>
            <span>{Math.abs(data.changedPercentage * 100).toFixed(2)}</span>
            <span style={{ width: "15px", textAlign: "end" }}>%</span>
          </div>
        </div>
      )}
    </article>
  );
  const [selected, setSelected] = useState("BUY");
  const [showModal, setShowModal] = useState(false);
  const handleSelect = (value) => () => {
    setSelected(value);
  };
  const toggleModal = () => {
    setShowModal(!showModal); // Đảo ngược trạng thái hiển thị của modal
  };
  const tradeInformation = () => {
    return (
      <article className={`row ${!!marketClosed && "opacity-75"}`}>
        <hr />

        <div
          className={`fw-bold col py-2 ${selected === "trade" ? "" : ""}`}
          style={{ backgroundColor: "var(--background-color)" }}
        >
          Trade
          <OverlayTrigger
            placement="left"
            overlay={
              <Tooltip className="position-fixed ">
                <b>Buy / Sell</b>{" "}
                {data?.ask?.toFixed(2) + " / " + data?.bid?.toFixed(2)}
              </Tooltip>
            }
          >
            <div className="d-inline-flex float-end justify-content-end gap-2 text-end  ">
              <b
                type="button"
                className={`user-select-none badge  rounded-pill  ${
                  selected === "BUY" ? "bg-success" : "bg-secondary"
                }`}
                onClick={handleSelect("BUY")}
              >
                Buy
              </b>
              <b
                type="button"
                onClick={handleSelect("SELL")}
                className={`user-select-none badge rounded-pill  ${
                  selected === "SELL" ? "bg-danger" : "bg-secondary"
                }`}
              >
                Sell
              </b>
            </div>
          </OverlayTrigger>
        </div>
        <hr></hr>

        {
          <BuyNSell
            data={data}
            marketClosed={!!marketClosed}
            selected={selected}
          />
        }
      </article>
    );
  };
  const {
    userInfo: { inventory, balance },
  } = user || { userInfo: {} }; // Mặc định user là một đối tượng rỗng nếu user không tồn tại

  const [show, setShow] = useState(false);
  const handleShow = () => setShow(true);
  const handleClose = () => setShow(false);
  return (
    <section className={className} aria-disabled={!!marketClosed}>
      {roles?.includes("ROLE_CUSTOMER") && (
        <>
          <div className="text d-flex flex-column ">
            <div className="position-relative">
              Balance:{" "}
              <b className=" gold ">{currencyFormat(balance?.amount)}</b>
            </div>

            <div className="position-relative">
              Inventory: <b>{inventory?.totalWeightOz + " tOz"}</b>
            </div>
          </div>
          <hr></hr>
        </>
      )}

      <div className="position-relative  w-100">
        <div
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
          }}
        >
          <strong>Gold</strong>
          {/* <FaInfoCircle
            className="ms-2"
            onClick={toggleModal}
            style={{ fontSize: "1.5em" }}
          /> */}
        </div>
        <article
          style={{ fontSize: "0.9rem", color: "var(--text-hint-color)" }}
        >
          {data?.symbol}
        </article>
        <article
          className="icon position-absolute d-flex gap-2 display top-0 end-0"
          style={{ fontSize: "1.1rem" }}
        >
          <FaInfoCircle className="clickable" onClick={handleShow} />
        </article>
        {priceData()}
        <OverlayTrigger
          overlay={
            <Tooltip>
              <div style={{ display: "flex", flexDirection: "column" }}>
                <span style={{ color: "green" }}>
                  Ask: {data?.ask?.toFixed(2)}
                </span>
                <span style={{ color: "red" }}>
                  Bid: {data?.bid?.toFixed(2)}
                </span>
              </div>
            </Tooltip>
          }
        >
          <span style={{ display: "flex" }}>
            <span
              style={{
                color: "green",
                marginRight: "10px",
                fontWeight: "bold",
                padding: "2px 5px",
                borderRadius: "3px",
              }}
            >
              Ask: {data?.ask?.toFixed(2)}
            </span>
            <span
              style={{
                color: "red",
                fontWeight: "bold",
                padding: "2px 5px",
                borderRadius: "3px",
              }}
            >
              Bid: {data?.bid?.toFixed(2)}
            </span>
          </span>
        </OverlayTrigger>
        <br></br>
        <div style={{ color: "orange" }}>
          <strong>Type Gold: 24k</strong>
        </div>
        <OverlayTrigger
          placement="bottom"
          overlay={
            <Tooltip>{new Date(data?.timestamp).toLocaleString()}</Tooltip>
          }
        >
          <span
            id="last-updated"
            style={{
              fontSize: "0.8rem",
              color: "var(--text-hint-color)",
            }}
            title={data?.timestamp}
            data-bs-toggle="tooltip"
          >
            Last Updated: {data?.lastUpdated}
          </span>
        </OverlayTrigger>

        {!!marketClosed && <div className="fw-bold my-3">Market is closed</div>}

        {tradeInformation()}
      </div>
      <WeightConversionModal show={show} handleClose={handleClose} />
    </section>
  );
};
export { GoldChartData };
