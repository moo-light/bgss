import { GoldOutlined } from "@ant-design/icons";
import { ArcElement, Chart as ChartJS, Legend, Tooltip } from "chart.js";
import React from "react";
import { Doughnut } from "react-chartjs-2";
import {
  FaArrowCircleDown,
  FaBox,
  FaCopyright,
  FaList,
  FaNewspaper,
  FaUser,
} from "react-icons/fa";
import StarRatings from "react-star-ratings";
import { currencyFormat, numberFormat } from "../../../helpers/helpers";
import Loader from "../../layout/Loader";
import DataSetChart from "./DataAnalyzeChart";
ChartJS.register(ArcElement, Tooltip, Legend);

const ProductsStatisticsCard = ({
  quantityStatistic,
  quantityProductReviews,
  isLoading,
}) => {
  return (
    <section
      className="card col-auto rounded-4 border-5"
      style={{ gridColumn: "span 2" }}
    >
      <div className="card-body">
        <h5 className="card-title h4 fw-bold">
          <FaBox size={30} /> Products
        </h5>
        <div className="card-text">
          <div className="d-flex justify-content-around">
            <p className={`display-2 text-center mt-auto me-5 `}>
              {isLoading ? (
                <div style={{ scale: "0.75" }}>
                  <Loader />
                </div>
              ) : (
                quantityStatistic?.data?.quantityProduct
              )}
            </p>
            <div
              className={`d-flex flex-column gap-1 ms-auto me-3 my-auto user-select-none  ${
                isLoading ? "opacity-0" : ""
              }`}
            >
              {quantityProductReviews
                ?.map((e, index) => (
                  <div className="ms-auto d-flex justify-content-between ">
                    <span className="position-relative " style={{ bottom: 4 }}>
                      <StarRatings
                        rating={index + 1}
                        starRatedColor="#ffb829"
                        numberOfStars={5}
                        name="rating"
                        starDimension="20px"
                        starSpacing="1px"
                      />
                    </span>
                    <span
                      id="no-of-reviews"
                      className="ms-2 h6"
                      style={{ width: 30 }}
                    >
                      {quantityStatistic.data.quantityProductReviews[index]}
                    </span>
                  </div>
                ))
                .reverse()}
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

const StarReviewsDoughnutChart = ({ productPieChart, isLoading }) => {
  return (
    <section
      className="card col-auto rounded-4 border-5"
      style={{ gridRow: "span 2  ", gridColumn: "span 2" }}
    >
      <div className="card-body mb-5">
        <div className="text-center h3 fw-bold ">Star Reviews</div>
        <div className="m-auto h-100 d-flex" style={{ maxWidth: 300 }}>
          {isLoading ? (
            <div className="m-auto">
              <Loader />
            </div>
          ) : (
            <Doughnut
              data={productPieChart}
              options={{
                plugins: {
                  // legend: {
                  //   display: false,
                  // },
                },
              }}
            />
          )}
        </div>
      </div>
    </section>
  );
};

const UsersStatisticsCard = ({ quantityStatistic, isLoading }) => {
  const userStatistic = [
    {
      label: "ACTIVE",
      number: quantityStatistic?.data?.quantityActiveUser,
      status: "active",
    },
    {
      label: "INACTIVE",
      number: quantityStatistic?.data?.quantityInactiveUser,
      seperate: true,
      status: "inactive",
    },
    {
      label: "VERIFIED",
      number: quantityStatistic?.data?.quantityVerifiedUser,
      status: "verified",
    },
    {
      label: "UNVERIFIED",
      number: quantityStatistic?.data?.quantityUnverifiedUser,
      status: "unverified",
    },
  ];

  const statusColors = {
    unverified: { label: "#757575", number: "#757575" }, // Dark Gray
    verified: { label: "#EF6C00", number: "#EF6C00" }, // Dark Orange
    inactive: { label: "#616161", number: "#616161" }, // Gray
    active: { label: "#4CAF50", number: "#4CAF50" }, // Dark Green
  };

  return (
    <section className="card col-auto rounded-4 border-5">
      <div className="card-body">
        <h5 className="card-title h4 fw-bold">
          <FaUser size={30} /> Users
        </h5>
        <div className="card-text">
          <div className="d-block">
            <section className="display-2 mt-auto mb-0">
              {isLoading ? (
                <div style={{ scale: "0.75" }}>
                  <Loader />
                </div>
              ) : (
                quantityStatistic?.data?.quantityUser
              )}
            </section>
            <div
              className={`d-flex flex-column gap-1 ms-auto me-3 my-auto ${
                isLoading ? "opacity-0" : ""
              }`}
            >
              {userStatistic?.map((e, index) => (
                <>
                  <section
                    className="d-flex justify-content-between gap-3"
                    key={index}
                  >
                    <div
                      className="fw-bold"
                      style={{ color: statusColors[e.status]?.label }}
                    >
                      {e.label}:{" "}
                    </div>
                    <div style={{ color: statusColors[e.status]?.number }}>
                      {e.number}
                    </div>
                  </section>
                  {e.seperate && (
                    <hr key={`${index}-hr`} style={{ borderColor: "#ccc" }} />
                  )}
                </>
              ))}
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};

const PostsStatisticsCard = ({ quantityStatistic, isLoading }) => {
  return (
    <section className="card col-auto rounded-4 border-5">
      <div className="card-body">
        <h5 className="card-title h4 fw-bold">
          <FaNewspaper size={30} /> Posts
        </h5>
        <div className="card-text">
          <div className="d-flex">
            <p className="display-2 mt-auto mb-0">
              {isLoading ? (
                <div style={{ scale: "0.75" }}>
                  <Loader />
                </div>
              ) : (
                quantityStatistic?.data?.quantityPost
              )}
            </p>
          </div>
        </div>
      </div>
    </section>
  );
};
const GoldInventoryStatisticsCard = ({ goldInventory, isLoading }) => {
  return (
    <section
      className="card col-auto rounded-4 border-5 "
      style={{ gridColumn: "span 2/ span 1" }}
    >
      <div className="card-body">
        <h5 className="card-title h4 fw-bold">
          <GoldOutlined size={30} /> Gold Inventory
        </h5>
        <div className="card-text">
          <div className="d-flex">
            <p className="display-2 mt-auto mb-0">
              {isLoading ? (
                <div style={{ scale: "0.75" }}>
                  <Loader />
                </div>
              ) : (
                goldInventory?.data?.quantityPost ?? 0
              )}
            </p>
          </div>
        </div>
      </div>
    </section>
  );
};
const OrdersStatisticsCard = ({ orderStatistic, isLoading }) => {
  const quantityTransaction = [
    {
      label: "UNVERIFIED",
      number: orderStatistic?.data?.unverifiedOrder,
      status: "unverified",
    },
    {
      label: "NOT_RECIEVED",
      number: orderStatistic?.data?.not_receivedOrder,
      status: "not_received",
    },
    {
      label: "RECIEVED",
      number: orderStatistic?.data?.receivedOrder,
      status: "received",
    },
  ];

  const statusColors = {
    unverified: { label: "#757575", number: "#757575" }, // Dark Gray
    not_received: { label: "#EF6C00", number: "#EF6C00" }, // Dark Orange
    received: { label: "#4CAF50", number: "#4CAF50" }, // Dark Green
  };

  const totalAmountColor = "green";
  return (
    <section
      className="card col-auto rounded-4 border-5"
      style={{ gridColumn: "span 2" }}
    >
      <div className="card-body">
        <h5 className="card-title h4 fw-bold">
          <FaList size={30} /> Orders
        </h5>
        <div className="card-text">
          <section className="d-flex justify-content-around">
            <div className={`display-2 text-center mt-auto me-5 `}>
              {isLoading ? (
                <div style={{ scale: "0.75" }}>
                  <Loader />
                </div>
              ) : (
                orderStatistic?.data?.totalOrders
              )}
            </div>
            <div
              className={`d-flex flex-column gap-1 ms-auto me-3 my-auto  ${
                isLoading ? "opacity-0" : ""
              }`}
            >
              {quantityTransaction?.map((e, index) => (
                <section
                  className="d-flex justify-content-between gap-3 "
                  key={index}
                >
                  <div
                    className="fw-bold"
                    style={{ color: statusColors[e.status]?.label }}
                  >
                    {e.label}:{" "}
                  </div>
                  <div style={{ color: statusColors[e.status]?.number }}>
                    {e.number}
                  </div>
                </section>
              ))}
            </div>
          </section>
          <hr />
          <section>
            <span className="h4 fw-bold">
              Total:{" "}
              <span style={{ color: totalAmountColor }}>
                {currencyFormat(orderStatistic?.data?.totalAmountOrdersPaid)}
              </span>
            </span>
          </section>
        </div>
      </div>
    </section>
  );
};

const TransactionsStatisticsCard = ({
  transactionBStatistic,
  transactionSStatistic,
  isLoading,
}) => {
  const quantityTransactionB = [
    {
      label: "UNVERIFIED",
      number: transactionBStatistic?.data?.unverifiedTransaction,
      status: "unverified",
    },
    {
      label: "VERIFIED",
      number: transactionBStatistic?.data?.verifiedTransaction,
      status: "verified",
    },
    {
      label: "PENDING",
      number: transactionBStatistic?.data?.pendingTransaction,
      status: "pending",
    },
    {
      label: "CONFIRMED",
      number: transactionBStatistic?.data?.confirmedTransaction,
      status: "confirmed",
    },
    {
      label: "REJECTED",
      number: transactionBStatistic?.data?.rejectedTransaction,
      status: "rejected",
    },
    {
      label: "COMPLETED",
      number: transactionBStatistic?.data?.completedTransaction,
      status: "completed",
    },
  ];

  const quantityTransactionS = [
    {
      label: "UNVERIFIED",
      number: transactionSStatistic?.data?.unverifiedTransaction,
      status: "unverified",
    },
    {
      label: "VERIFIED",
      number: transactionSStatistic?.data?.verifiedTransaction,
      status: "verified",
    },
    {
      label: "PENDING",
      number: transactionSStatistic?.data?.pendingTransaction,
      status: "pending",
    },
    {
      label: "CONFIRMED",
      number: transactionSStatistic?.data?.confirmedTransaction,
      status: "confirmed",
    },
    {
      label: "REJECTED",
      number: transactionSStatistic?.data?.rejectedTransaction,
      status: "rejected",
    },
    {
      label: "COMPLETED",
      number: transactionSStatistic?.data?.completedTransaction,
      status: "completed",
    },
  ];

  const statusColors = {
    unverified: { label: "#757575", number: "#757575" }, // Gray
    verified: { label: "#388E3C", number: "#388E3C" }, // Dark Green
    pending: { label: "#000000", number: "#000000" }, // Black
    confirmed: { label: "#1565C0", number: "#1565C0" }, // Dark Blue
    rejected: { label: "#EF6C00", number: "#EF6C00" }, // Dark Orange
    completed: { label: "#800000", number: "#800000" }, // Dark Maroon
  };

  return (
    <section
      className="card col-auto rounded-4 border-5"
      style={{ gridRow: "span 2", gridColumn: "span 2" }}
    >
      <div className="card-body">
        <h5 className="card-title h4 fw-bold">
          <FaCopyright size={30} /> Transactions <span className="">Buy</span>
        </h5>
        <div className="card-text">
          <div className="d-flex justify-content-around">
            <p className={`display-2 text-center my-auto me-5 `}>
              {isLoading ? (
                <div style={{ scale: "0.75" }}>
                  <Loader />
                </div>
              ) : (
                <>{transactionBStatistic?.data?.totalTransactionsBuy}</>
              )}
            </p>

            <div
              className={`d-flex flex-column gap-1 ms-auto me-3 my-auto  ${
                isLoading ? "opacity-0" : ""
              }`}
            >
              {quantityTransactionB?.map((e, index) => (
                <section
                  className="d-flex justify-content-between gap-3 "
                  key={index}
                >
                  <div
                    className="fw-bold"
                    style={{ color: statusColors[e.status]?.label }}
                  >
                    {e.label}:{" "}
                  </div>
                  <div style={{ color: statusColors[e.status]?.number }}>
                    {e.number}
                  </div>
                </section>
              ))}
            </div>
          </div>
          <br />
          <section>
            <span className="h4 fw-bold ">
              Total:{" "}
              <span className="greenColor fw-bold">
                {currencyFormat(
                  transactionBStatistic?.data?.totalAmountTransactionsPaid
                )}
              </span>
            </span>
          </section>
          <hr />
          <h5 className="card-title h4 fw-bold">
            <FaCopyright size={30} /> Transactions{" "}
            <span className="">Sell</span>
          </h5>
          <div className="d-flex justify-content-around">
            <p className={`display-2 text-center my-auto me-5 `}>
              {isLoading ? (
                <div style={{ scale: "0.75" }}>
                  <Loader />
                </div>
              ) : (
                <>{transactionSStatistic?.data?.totalTransactionsSell}</>
              )}
            </p>

            <div
              className={`d-flex flex-column gap-1 ms-auto me-3 my-auto  ${
                isLoading ? "opacity-0" : ""
              }`}
            >
              {quantityTransactionS?.map((e, index) => (
                <section
                  className="d-flex justify-content-between gap-3 "
                  key={index}
                >
                  <div
                    className="fw-bold"
                    style={{ color: statusColors[e.status]?.label }}
                  >
                    {e.label}:{" "}
                  </div>
                  <div style={{ color: statusColors[e.status]?.number }}>
                    {e.number}
                  </div>
                </section>
              ))}
            </div>
          </div>
          <br />
          <section>
            <span className="h4 fw-bold">
              Total:{" "}
              <span className="redColor">
                {currencyFormat(
                  transactionSStatistic?.data?.totalAmountTransactionsPaid
                )}
              </span>
            </span>
          </section>
        </div>
      </div>
    </section>
  );
};

const WithdrawStatisticsCard = ({ withdrawStatistic, isLoading }) => {
  const quantityTransaction = [
    {
      label: "UNVERIFIED",
      number: withdrawStatistic?.data?.unverifiedWithdraw,
      status: "unverified",
    },
    {
      label: "PENDING",
      number: withdrawStatistic?.data?.pendingWithdraw,
      status: "pending",
    },
    {
      label: "CONFIRMED",
      number: withdrawStatistic?.data?.confirmedWithdraw,
      status: "confirmed",
    },
    {
      label: "COMPLETED",
      number: withdrawStatistic?.data?.completedWithdraw,
      status: "completed",
    },
    {
      label: "CANCELED",
      number: withdrawStatistic?.data?.canceledWithdraw,
      status: "canceled",
    },
  ];

  const statusColors = {
    unverified: { label: "#616161", number: "#424242" }, // Dark Gray
    pending: { label: "#FFA726", number: "#FB8C00" }, // Dark Orange
    confirmed: { label: "#388E3C", number: "#2E7D32" }, // Dark Green
    completed: { label: "#388E3C", number: "#2E7D32" }, // Dark Green
    canceled: { label: "#C62828", number: "#B71C1C" }, // Dark Red
  };

  return (
    <section
      className="card col-auto rounded-4 border-5"
      style={{ gridColumn: "span 2" }}
    >
      <div className="card-body ">
        <h5 className="card-title h4 fw-bold">
          <FaArrowCircleDown size={30} /> Withdraws
        </h5>
        <main className="card-text ">
          <section className="d-flex justify-content-around">
            <p className={`display-2 text-center mt-auto me-5 `}>
              {isLoading ? (
                <div style={{ scale: "0.75" }}>
                  <Loader />
                </div>
              ) : (
                withdrawStatistic?.data?.totalRequestWithdraws
              )}
            </p>
            <div
              className={`d-flex flex-column gap-1 ms-auto me-3 my-auto  ${
                isLoading ? "opacity-0" : ""
              }`}
            >
              {quantityTransaction?.map((e, index) => (
                <section
                  className="d-flex justify-content-between gap-3 "
                  key={index}
                >
                  <div
                    className="fw-bold"
                    style={{ color: statusColors[e.status]?.label }}
                  >
                    {e.label}:{" "}
                  </div>
                  <div style={{ color: statusColors[e.status]?.number }}>
                    {e.number}
                  </div>
                </section>
              ))}
            </div>
          </section>
        </main>
        <section className="mt-auto">
          <hr />
          <div className="h4 fw-bold d-block ">
            Total:{" "}
            <span className="greenColor fw-bold">
              {numberFormat(withdrawStatistic?.data?.totalAmountWithdrawGold)}{" "}
            </span>
            <span className="">tOz</span> <GoldOutlined />
          </div>
          <div className="h4 fw-bold d-block  ">
            Confirmed:{" "}
            <span className="greenColor fw-bold">
              {numberFormat(
                withdrawStatistic?.data?.amountWithdrawGoldConfirmed
              )}{" "}
            </span>
            <span className="">tOz</span> <GoldOutlined />
          </div>
        </section>
      </div>
    </section>
  );
};

const LineChartCard = () => {
  return (
    <section
      className="card col-auto rounded-4 border-5"
      style={{ gridColumn: "1 / -1" }} // Chiếm toàn bộ cột
    >
      <div className="card-body">
        <h5 className="card-title h4 fw-bold">Data Analysis</h5>
        <div className="card-text">
          <DataSetChart />
        </div>
      </div>
    </section>
  );
};

export {
  GoldInventoryStatisticsCard,
  LineChartCard,
  OrdersStatisticsCard,
  PostsStatisticsCard,
  ProductsStatisticsCard,
  StarReviewsDoughnutChart,
  TransactionsStatisticsCard,
  UsersStatisticsCard,
  WithdrawStatisticsCard,
};
