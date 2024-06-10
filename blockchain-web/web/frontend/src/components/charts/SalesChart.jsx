// import {
//   CategoryScale,
//   Chart as ChartJS,
//   Legend,
//   LineElement,
//   LinearScale,
//   PointElement,
//   Title,
//   Tooltip,
// } from "chart.js";
// import React from "react";
// import { Line } from "react-chartjs-2";

// ChartJS.register(
//   CategoryScale,
//   LinearScale,
//   PointElement,
//   LineElement,
//   Title,
//   Tooltip,
//   Legend
// );

// export default function SalesChart({ salesData }) {
//   const options = {
//     responsive: true,
//     interaction: {
//       mode: "index",
//       intersect: false,
//     },
//     stacked: false,
//     plugins: {
//       title: {
//         display: true,
//         text: "Sales & Order Data",
//       },
//     },
//     scales: {
//       y: {
//         type: "linear",
//         display: true,
//         position: "left",
//       },
//       y1: {
//         type: "linear",
//         display: true,
//         position: "right",
//         grid: {
//           drawOnChartArea: false,
//         },
//       },
//     },
//   };

//   const labels = salesData?.map((data) => data?.date);

//   const data = {
//     labels,
//     datasets: [
//       {
//         label: "Orders",
//         data: salesData?.map((data) => data?.sales),
//         borderColor: "#198753",
//         backgroundColor: "rgba(42, 117, 83, 0.5)",
//         yAxisID: "y",
//       },
//       {
//         label: "Transactions",
//         data: salesData?.map((data) => data?.numOrders),
//         borderColor: "rgb(220, 52 , 69)",
//         backgroundColor: "rgba(201, 68, 82, 0.5)",
//         yAxisID: "y1",
//       },
//       {
//         label: "Withdraws",
//         data: salesData?.map((data) => data?.numWithdraws),
//         borderColor: "rgb(220, 152, 69)",
//         backgroundColor: "rgba(201, 168, 82, 0.5)",
//         yAxisID: "y2",
//       },
//     ],
//   };
//   return <Line options={options} data={data} />;
// }
