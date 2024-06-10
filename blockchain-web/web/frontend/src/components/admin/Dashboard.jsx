import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import dayjs from "dayjs";
import React, { useEffect, useState } from "react";
import { ToastContainer, toast } from "react-toastify";
import AdminLayout, { AdminLayoutLoader } from "../layout/AdminLayout";

import {
  DashBoardDateType,
  useCalculateOderSalesFromAndToMutation,
  useCalculateOrderSalesMutation,
  useCalculateTransactionBuySalesFromAndToMutation,
  useCalculateTransactionBuySalesMutation,
  useCalculateTransactionSellSalesFromAndToMutation,
  useCalculateTransactionSellSalesMutation,
  useCalculateWithdrawMutation,
  useCalculateWithdrawRequestFromAndToMutation,
  useQuantityStatisticsMutation,
} from "../../redux/api/dashBoardApi";
import MetaData from "../layout/MetaData";
import {
  LineChartCard,
  OrdersStatisticsCard,
  PostsStatisticsCard,
  ProductsStatisticsCard,
  StarReviewsDoughnutChart,
  TransactionsStatisticsCard,
  UsersStatisticsCard,
  WithdrawStatisticsCard
} from "./components/DashboardComponents";

const Dashboard = () => {
  const [getStatistic, { error, isLoading, data: quantityStatistic }] =
    useQuantityStatisticsMutation();
  const [getOrderStatistic, orderResult] = useCalculateOrderSalesMutation();
  const [getTransactionBuyStatistic, transactionBResult] =
    useCalculateTransactionBuySalesMutation();
  const [getTransactionSaleStatistic, transactionSResult] =
    useCalculateTransactionSellSalesMutation();
  const [getWithdrawStatistic, withdrawResult] = useCalculateWithdrawMutation();

  const [getWithdrawRequestFromAndTo] =
    useCalculateWithdrawRequestFromAndToMutation();
  const [getTransactionSellSalesFromAndTo] =
    useCalculateTransactionSellSalesFromAndToMutation();
  const [getTransactionBuySalesFromAndTo] =
    useCalculateTransactionBuySalesFromAndToMutation();
  const [getOrderSalesFromAndTo] = useCalculateOderSalesFromAndToMutation();

  const [withdrawData, setWithdrawData] = useState(null);
  const [transactionSellData, setTransactionSellData] = useState(null);
  const [transactionBuyData, setTransactionBuyData] = useState(null);
  const [orderData, setOrderData] = useState(null);

  const [searchWithdrawData, setSearchWithdrawData] = useState(null);
  const [searchTransactionSellData, setSearchTransactionSellData] =
    useState(null);
  const [searchTransactionBuyData, setSearchTransactionBuyData] =
    useState(null);
  const [searchOrderData, setSearchOrderData] = useState(null);

  const [options, setOptions] = useState({
    type: DashBoardDateType.DAY,
    monthChose: new Date().getMonth() + 1,
    quarterChoose: Math.floor(new Date().getMonth() / 3) + 1,
    yearChoose: new Date().getFullYear(),
  });
  const [startDate, setStartDate] = useState(dayjs());
  const [endDate, setEndDate] = useState(dayjs());

  const [isSearchActive, setIsSearchActive] = useState(false);

  useEffect(() => {
    getStatistic();
  }, []);

  useEffect(() => {
    if (!isSearchActive) {
      getOrderStatistic(options);
      getTransactionBuyStatistic(options);
      getTransactionSaleStatistic(options);
      getWithdrawStatistic(options);
    }
  }, [options]);

  const submitHandler = async () => {
    if (startDate && endDate) {
      if (startDate.isAfter(endDate)) {
        toast.error("Start date cannot be later than end date");
        return;
      }

      const from = new Date(startDate).toISOString();
      const to = new Date(endDate).toISOString();

      const withdrawResponse = await getWithdrawRequestFromAndTo({ from, to });
      setSearchWithdrawData(withdrawResponse.data);

      const transactionSellResponse = await getTransactionSellSalesFromAndTo({
        from,
        to,
      });
      setSearchTransactionSellData(transactionSellResponse.data);

      const transactionBuyResponse = await getTransactionBuySalesFromAndTo({
        from,
        to,
      });
      setSearchTransactionBuyData(transactionBuyResponse.data);

      const orderResponse = await getOrderSalesFromAndTo({ from, to });
      setSearchOrderData(orderResponse.data);

      setIsSearchActive(true);
    }
  };

  const resetSearch = () => {
    setIsSearchActive(false);
    setSearchWithdrawData(null);
    setSearchTransactionSellData(null);
    setSearchTransactionBuyData(null);
    setSearchOrderData(null);
  };

  const title = "Admin Products";
  if (isLoading) {
    return <AdminLayoutLoader title={title} />;
  }
  const quantityProductReviews = quantityStatistic?.data.quantityProductReviews;
  const colors = { backgroundColor: "#d4af375b", borderColor: "#d4af37" };
  const productPieChart = {
    datasets: [
      {
        label: "# of Stars",
        data: quantityProductReviews,
        backgroundColor: [
          colors.backgroundColor,
          colors.backgroundColor,
          colors.backgroundColor,
          colors.backgroundColor,
          colors.backgroundColor,
        ],
        borderColor: [
          colors.borderColor,
          colors.borderColor,
          colors.borderColor,
          colors.borderColor,
          colors.borderColor,
        ],
        borderWidth: 3,
      },
    ],
    labels: ["1", "2", "3", "4", "5"].map((e) => e + " Stars"),
  };
  return (
    <AdminLayout>
      <MetaData title={title} />
      <ToastContainer position="top-center" />
      <h2 className="mt-3">Application Statistic</h2>
      <div
        className="d-grid gap-2 justify-content-evenly content"
        style={{
          gridTemplateColumns: "1fr 1fr 1fr 1fr",
        }}
      >
        <StarReviewsDoughnutChart
          productPieChart={productPieChart}
          isLoading={isLoading}
        />
        <ProductsStatisticsCard
          quantityStatistic={quantityStatistic}
          quantityProductReviews={quantityProductReviews}
          isLoading={isLoading}
        />
        <UsersStatisticsCard
          quantityStatistic={quantityStatistic}
          isLoading={isLoading}
        />
        <PostsStatisticsCard
          quantityStatistic={quantityStatistic}
          isLoading={isLoading}
        />
      </div>
      <hr />
      <h2>Sales</h2>
      <div className="p-2 d-flex gap-2 align-items-center mb-3 justify-content-between">
        <div className="d-flex gap-2">
          {Object.keys(DashBoardDateType).map((e) => (
            <button
              key={e}
              className={`d-inline-block btn border border-3 rounded-5 p-2 ${
                e === options.type ? "btn-primary" : "hover active"
              }`}
              style={{ minWidth: 70 }}
              onClick={() => {
                setOptions({ ...options, type: e });
                resetSearch();
              }}
            >
              {e}
            </button>
          ))}
          {options.type === "MONTH" && (
            <div className="col-auto">
              <select
                className="form-select"
                defaultValue={options.monthChose}
                onChange={(e) => {
                  setOptions({ ...options, monthChose: e.target.value });
                  resetSearch();
                }}
              >
                {Array.from({ length: 12 }, (_, index) => (
                  <option
                    className={`${
                      new Date().getMonth() === index &&
                      "btn-primary text-light"
                    } `}
                    key={index}
                    value={index + 1}
                  >
                    {index + 1}
                  </option>
                ))}
              </select>
            </div>
          )}
          {options.type === "QUARTER" && (
            <div className="col-auto">
              <select
                className="form-select"
                defaultValue={options.quarterChoose}
                onChange={(e) => {
                  setOptions({
                    ...options,
                    quarterChoose: parseInt(e.target.value),
                  });
                  resetSearch();
                }}
              >
                {Array.from({ length: 4 }, (_, index) => (
                  <option
                    className={`${
                      Math.floor(new Date().getMonth() / 3) === index &&
                      "btn-primary text-light"
                    } `}
                    key={index}
                    value={index + 1}
                  >
                    The {index + 1} Quarter
                  </option>
                ))}
              </select>
            </div>
          )}
          {options.type === "YEAR" && (
            <div className="col-auto">
              <select
                className="form-select"
                defaultValue={options.yearChoose}
                onChange={(e) => {
                  setOptions({
                    ...options,
                    yearChoose: parseInt(e.target.value),
                  });
                  resetSearch();
                }}
              >
                {Array.from({ length: 10 }, (_, index) => (
                  <option
                    className={`${
                      new Date().getFullYear() === index + 2020 &&
                      "btn-primary text-light"
                    } `}
                    key={index}
                    value={index + 2020}
                  >
                    {index + 2020}
                  </option>
                ))}
              </select>
            </div>
          )}
        </div>
        <div
          className="d-flex gap-2 align-items-center"
          style={{ marginRight: "100px" }}
        >
          <LocalizationProvider dateAdapter={AdapterDayjs}>
            <DatePicker
              label="Start Date"
              value={startDate}
              onChange={(newValue) => setStartDate(newValue)}
              renderInput={(params) => (
                <input {...params.inputProps} className="form-control" />
              )}
            />
            <div> to </div>
            <DatePicker
              label="End Date"
              value={endDate}
              onChange={(newValue) => setEndDate(newValue)}
              renderInput={(params) => (
                <input {...params.inputProps} className="form-control" />
              )}
            />
          </LocalizationProvider>
          <button className="btn btn-primary" onClick={submitHandler}>
            Search
          </button>
        </div>
      </div>
      <div
        className="d-grid gap-2 justify-content-evenly content"
        style={{
          gridTemplateColumns: "1fr 1fr 1fr 1fr",
        }}
      >
        <OrdersStatisticsCard
          orderStatistic={searchOrderData || orderResult.data}
          isLoading={orderResult.isLoading}
        />
        <TransactionsStatisticsCard
          transactionBStatistic={
            searchTransactionBuyData || transactionBResult.data
          }
          transactionSStatistic={
            searchTransactionSellData || transactionSResult.data
          }
          isLoading={
            transactionSResult.isLoading || transactionBResult.isLoading
          }
        />
        <WithdrawStatisticsCard
          withdrawStatistic={searchWithdrawData || withdrawResult.data}
          isLoading={withdrawResult.isLoading}
        />
        <LineChartCard />
      </div>
      <div className="mb-5"></div>
    </AdminLayout>
  );
};

export default Dashboard;

// import React, { useEffect, useState } from "react";
// import AdminLayout, { AdminLayoutLoader } from "../layout/AdminLayout";
// import "react-datepicker/dist/react-datepicker.css";
// import DatePicker from "react-datepicker";

// import {
//   DashBoardDateType,
//   useCalculateOrderSalesMutation,
//   useCalculateTransactionBuySalesMutation,
//   useCalculateTransactionSellSalesMutation,
//   useCalculateWithdrawMutation,
//   useQuantityStatisticsMutation,
//   useCalculateWithdrawRequestFromAndToMutation,
//   useCalculateTransactionSellSalesFromAndToMutation,
//   useCalculateTransactionBuySalesFromAndToMutation,
//   useCalculateOderSalesFromAndToMutation,
// } from "../../redux/api/dashBoardApi";
// import MetaData from "../layout/MetaData";
// import {
//   GoldInventoryStatisticsCard,
//   LineChartCard,
//   OrdersStatisticsCard,
//   PostsStatisticsCard,
//   ProductsStatisticsCard,
//   StarReviewsDoughnutChart,
//   TransactionsStatisticsCard,
//   UsersStatisticsCard,
//   WithdrawStatisticsCard,
// } from "./components/DashboardComponents";

// const Dashboard = () => {
//   const [getStatistic, { error, isLoading, data: quantityStatistic }] =
//     useQuantityStatisticsMutation();
//   const [getOrderStatistic, orderResult] = useCalculateOrderSalesMutation();
//   const [getTransactionBuyStatistic, transactionBResult] =
//     useCalculateTransactionBuySalesMutation();
//   const [getTransactionSaleStatistic, transactionSResult] =
//     useCalculateTransactionSellSalesMutation();
//   const [getWithdrawStatistic, withdrawResult] = useCalculateWithdrawMutation();

//   const [getWithdrawRequestFromAndTo] =
//     useCalculateWithdrawRequestFromAndToMutation();
//   const [getTransactionSellSalesFromAndTo] =
//     useCalculateTransactionSellSalesFromAndToMutation();
//   const [getTransactionBuySalesFromAndTo] =
//     useCalculateTransactionBuySalesFromAndToMutation();
//   const [getOrderSalesFromAndTo] = useCalculateOderSalesFromAndToMutation();

//   const [withdrawData, setWithdrawData] = useState(null);
//   const [transactionSellData, setTransactionSellData] = useState(null);
//   const [transactionBuyData, setTransactionBuyData] = useState(null);
//   const [orderData, setOrderData] = useState(null);

//   const [searchWithdrawData, setSearchWithdrawData] = useState(null);
//   const [searchTransactionSellData, setSearchTransactionSellData] =
//     useState(null);
//   const [searchTransactionBuyData, setSearchTransactionBuyData] =
//     useState(null);
//   const [searchOrderData, setSearchOrderData] = useState(null);

//   const [options, setOptions] = useState({
//     type: DashBoardDateType.DAY,
//     monthChose: new Date().getMonth() + 1,
//     quarterChoose: Math.floor(new Date().getMonth() / 3) + 1,
//     yearChoose: new Date().getFullYear(),
//   });
//   const [dateRange, setDateRange] = useState([null, null]);
//   const [startDate, endDate] = dateRange;

//   useEffect(() => {
//     getStatistic();
//   }, []);

//   useEffect(() => {
//     getOrderStatistic(options);
//     getTransactionBuyStatistic(options);
//     getTransactionSaleStatistic(options);
//     getWithdrawStatistic(options);
//   }, [options]);

//   const submitHandler = async () => {
//     if (startDate && endDate) {
//       const from = new Date(startDate).toISOString();
//       const to = new Date(endDate).toISOString();

//       const withdrawResponse = await getWithdrawRequestFromAndTo({ from, to });
//       setSearchWithdrawData(withdrawResponse.data);

//       const transactionSellResponse = await getTransactionSellSalesFromAndTo({
//         from,
//         to,
//       });
//       setSearchTransactionSellData(transactionSellResponse.data);

//       const transactionBuyResponse = await getTransactionBuySalesFromAndTo({
//         from,
//         to,
//       });
//       setSearchTransactionBuyData(transactionBuyResponse.data);

//       const orderResponse = await getOrderSalesFromAndTo({ from, to });
//       setSearchOrderData(orderResponse.data);
//     }
//   };

//   const title = "Admin Products";
//   if (isLoading) {
//     return <AdminLayoutLoader title={title} />;
//   }
//   const quantityProductReviews = quantityStatistic?.data.quantityProductReviews;
//   const colors = { backgroundColor: "#d4af375b", borderColor: "#d4af37" };
//   const productPieChart = {
//     datasets: [
//       {
//         label: "# of Stars",
//         data: quantityProductReviews,
//         backgroundColor: [
//           colors.backgroundColor,
//           colors.backgroundColor,
//           colors.backgroundColor,
//           colors.backgroundColor,
//           colors.backgroundColor,
//         ],
//         borderColor: [
//           colors.borderColor,
//           colors.borderColor,
//           colors.borderColor,
//           colors.borderColor,
//           colors.borderColor,
//         ],
//         borderWidth: 3,
//       },
//     ],
//     labels: ["1", "2", "3", "4", "5"].map((e) => e + " Stars"),
//   };
//   return (
//     <AdminLayout>
//       <MetaData title={title} />
//       <h2 className="mt-3">Application Statistic</h2>
//       <div
//         className="d-grid gap-2 justify-content-evenly content"
//         style={{
//           gridTemplateColumns: "1fr 1fr 1fr 1fr",
//         }}
//       >
//         <StarReviewsDoughnutChart
//           productPieChart={productPieChart}
//           isLoading={isLoading}
//         />
//         <ProductsStatisticsCard
//           quantityStatistic={quantityStatistic}
//           quantityProductReviews={quantityProductReviews}
//           isLoading={isLoading}
//         />
//         <UsersStatisticsCard
//           quantityStatistic={quantityStatistic}
//           isLoading={isLoading}
//         />
//         <PostsStatisticsCard
//           quantityStatistic={quantityStatistic}
//           isLoading={isLoading}
//         />
//       </div>
//       <hr />
//       <h2>Sales</h2>
//       <div className="p-2 d-flex gap-2 align-items-center mb-3 justify-content-between">
//         <div className="d-flex gap-2">
//           {Object.keys(DashBoardDateType).map((e) => (
//             <button
//               key={e}
//               className={`d-inline-block btn border border-3 rounded-5 p-2 ${
//                 e === options.type ? "btn-primary" : "hover active"
//               }`}
//               style={{ minWidth: 70 }}
//               onClick={() => {
//                 setOptions({ ...options, type: e });
//               }}
//             >
//               {e}
//             </button>
//           ))}
//           {options.type === "MONTH" && (
//             <div className="col-auto">
//               <select
//                 className="form-select"
//                 defaultValue={options.monthChose}
//                 onChange={(e) =>
//                   setOptions({ ...options, monthChose: e.target.value })
//                 }
//               >
//                 {Array.from({ length: 12 }, (_, index) => (
//                   <option
//                     className={`${
//                       new Date().getMonth() === index &&
//                       "btn-primary text-light"
//                     } `}
//                     key={index}
//                     value={index + 1}
//                   >
//                     {index + 1}
//                   </option>
//                 ))}
//               </select>
//             </div>
//           )}
//           {options.type === "QUARTER" && (
//             <div className="col-auto">
//               <select
//                 className="form-select"
//                 defaultValue={options.quarterChoose}
//                 onChange={(e) =>
//                   setOptions({
//                     ...options,
//                     quarterChoose: parseInt(e.target.value),
//                   })
//                 }
//               >
//                 {Array.from({ length: 4 }, (_, index) => (
//                   <option
//                     className={`${
//                       Math.floor(new Date().getMonth() / 3) === index &&
//                       "btn-primary text-light"
//                     } `}
//                     key={index}
//                     value={index + 1}
//                   >
//                     The {index + 1} Quarter
//                   </option>
//                 ))}
//               </select>
//             </div>
//           )}
//           {options.type === "YEAR" && (
//             <div className="col-auto">
//               <select
//                 className="form-select"
//                 defaultValue={options.yearChoose}
//                 onChange={(e) =>
//                   setOptions({ ...options, yearChoose: e.target.value })
//                 }
//               >
//                 {Array.from({ length: 10 }, (_, index) => (
//                   <option
//                     className={`${
//                       new Date().getFullYear() === index + 2020 &&
//                       "btn-primary text-light"
//                     } `}
//                     key={index}
//                     value={index + 2020}
//                   >
//                     {index + 2020}
//                   </option>
//                 ))}
//               </select>
//             </div>
//           )}
//         </div>
//         <div
//           className="d-flex gap-2 align-items-center"
//           style={{ marginRight: "100px" }}
//         >
//           <DatePicker
//             selectsRange
//             startDate={startDate}
//             endDate={endDate}
//             onChange={(update) => {
//               setDateRange(update);
//             }}
//             isClearable={true}
//             className="form-control"
//           />
//           <button className="btn btn-primary" onClick={submitHandler}>
//             Search
//           </button>
//         </div>
//       </div>
//       <div
//         className="d-grid gap-2 justify-content-evenly content"
//         style={{
//           gridTemplateColumns: "1fr 1fr 1fr 1fr",
//         }}
//       >
//         <OrdersStatisticsCard
//           orderStatistic={searchOrderData || orderResult.data}
//           isLoading={orderResult.isLoading}
//         />
//         <TransactionsStatisticsCard
//           transactionBStatistic={
//             searchTransactionBuyData || transactionBResult.data
//           }
//           transactionSStatistic={
//             searchTransactionSellData || transactionSResult.data
//           }
//           isLoading={
//             transactionSResult.isLoading || transactionBResult.isLoading
//           }
//         />
//         <WithdrawStatisticsCard
//           withdrawStatistic={searchWithdrawData || withdrawResult.data}
//           isLoading={withdrawResult.isLoading}
//         />
//         <LineChartCard />
//       </div>
//       <div className="mb-5"></div>
//     </AdminLayout>
//   );
// };

// export default Dashboard;
