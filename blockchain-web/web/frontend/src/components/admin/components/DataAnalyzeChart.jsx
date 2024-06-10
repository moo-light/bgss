"use client";

import { Button } from "antd";
import {
    CategoryScale,
    Chart as ChartJS,
    Legend,
    LineElement,
    LinearScale,
    PointElement,
    Title,
    Tooltip,
} from "chart.js";
import zoomPlugin from "chartjs-plugin-zoom";
import { format, parseISO, subMonths, subYears } from "date-fns";
import { useEffect, useMemo, useRef, useState } from "react";
import { Line } from "react-chartjs-2";
import toast from "react-hot-toast";
import { useGetAnalyzeDataSetQuery } from "../../../redux/api/dashBoardApi";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  zoomPlugin
);

export default function DataSetChart() {
  const { data, isSuccess, error, isLoading, isError } =
    useGetAnalyzeDataSetQuery();
  const [chartData, setChartData] = useState({ labels: [], datasets: [] });
  const [zoomRange, setZoomRange] = useState("1 month");
  const [propagate, setPropagate] = useState(false);
  const [smooth, setSmooth] = useState(false);
  const chartRef = useRef(null);

  const analyzeDataSet = data ?? {};

  useEffect(() => {
    if (isSuccess) {
      // Call transformData when data is successfully fetched
    //   transformData(analyzeDataSet, zoomRange);
    }
    if (isError) {
      toast.error(error.data);
    }
  }, [isSuccess, isError]);

  const processDataset = (dataset, dateFormat) => {
    const totals = {};
    Object.entries(dataset).forEach(([date, value]) => {
      const formattedDate = format(
        parseISO(date),
        dateFormat.replace("wo", "'week' wo")
      );
      totals[formattedDate] = (totals[formattedDate] || 0) + value;
    });
    return totals;
  };

  const filterData = (data, range) => {
    const endDate = new Date();
    let startDate;
    switch (range) {
      case "1 week":
        startDate = subMonths(endDate, 1 / 4);
        break;
      case "1 month":
        startDate = subMonths(endDate, 1);
        break;
      case "3 months":
        startDate = subMonths(endDate, 3);
        break;
      case "6 months":
        startDate = subMonths(endDate, 6);
        break;
      case "1 year":
        startDate = subYears(endDate, 1);
        break;
      case "3 years":
        startDate = subYears(endDate, 3);
        break;
      default:
        startDate = subMonths(endDate, 1);
    }
    const filteredData = {
      analyzeBalance: {
        data: Object.fromEntries(
          Object.entries(data.analyzeBalance?.data || {}).filter(
            ([date]) => parseISO(date) >= startDate
          )
        ),
      },
      analyzeOrder: {
        data: Object.fromEntries(
          Object.entries(data.analyzeOrder?.data || {}).filter(
            ([date]) => parseISO(date) >= startDate
          )
        ),
      },
      analyzeTransactions: {
        data: Object.fromEntries(
          Object.entries(data.analyzeTransactions?.data || {}).filter(
            ([date]) => parseISO(date) >= startDate
          )
        ),
      },
    };
    transformData(filteredData, "day");
  };

  useEffect(() => {
    if (analyzeDataSet) {
      filterData(analyzeDataSet, zoomRange);
    }
  }, [analyzeDataSet, zoomRange]);

  const transformData = (data, dateFormat) => {
    const balanceData = data.analyzeBalance?.data
      ? processDataset(data.analyzeBalance.data, dateFormat)
      : {};
    const orderData = data.analyzeOrder?.data
      ? processDataset(data.analyzeOrder.data, dateFormat)
      : {};
    const transactionData = data.analyzeTransactions?.data
      ? processDataset(data.analyzeTransactions.data, dateFormat)
      : {};

    const labels = Array.from(
      new Set([
        ...Object.keys(balanceData),
        ...Object.keys(orderData),
        ...Object.keys(transactionData),
      ])
    ).sort();
    const datasets = [
      {
        label: "Balance",
        data: labels.map((label) => balanceData[label] || 0),
        borderColor: "rgb(255, 99, 132)",
        backgroundColor: "rgba(255, 99, 132, 0.5)",
      },
      {
        label: "Order",
        data: labels.map((label) => orderData[label] || 0),
        borderColor: "rgb(75, 192, 192)",
        backgroundColor: "rgba(75, 192, 192, 0.5)",
      },
      {
        label: "Transaction",
        data: labels.map((label) => transactionData[label] || 0),
        borderColor: "rgb(255, 205, 86)",
        backgroundColor: "rgba(255, 205, 86, 0.5)",
      },
    ];
    setChartData({ labels, datasets });
  };

  const handleZoomRangeChange = (range) => {
    setZoomRange(range);
  };

  const options = useMemo(
    () => ({
      responsive: true,
      plugins: {
        zoom: {
          pan: {
            enabled: true,
            mode: "x",
          },
          zoom: {
            wheel: {
              enabled: true,
            },
            pinch: {
              enabled: true,
            },
            mode: "x",
            onZoom: ({ chart }) => {
              const tickCount = chart.scales.x.ticks.length;
              const zoomLevel =
                tickCount < 15 ? "day" : tickCount < 40 ? "week" : "month";
              transformData(analyzeDataSet, zoomLevel);
            },
          },
        },
      },
      scales: {
        y: {
          stacked: true,
        },
      },
      interaction: {
        intersect: false,
      },
      elements: {
        line: {
          tension: smooth ? 0.4 : 0,
        },
      },
      filler: {
        propagate: propagate,
      },
    }),
    [propagate, smooth]
  );

  const togglePropagate = () => {
    const newPropagate = !propagate;
    setPropagate(newPropagate);
    if (chartRef.current && chartRef.current.chart) {
      if (!chartRef.current.options.plugins) {
        chartRef.current.options.plugins = {};
      }
      if (!chartRef.current.options.plugins.filler) {
        chartRef.current.options.plugins.filler = {};
      }
      chartRef.current.options.plugins.filler.propagate = newPropagate;
      chartRef.current.update();
    }
  };

  const toggleSmooth = () => {
    const newSmooth = !smooth;
    setSmooth(newSmooth);
    if (chartRef.current) {
      chartRef.current.options.elements.line.tension = newSmooth ? 0.4 : 0;
      chartRef.current.update();
    }
  };

  return (
    <div>
      <div className="d-flex gap-2 flex-wrap ">
        <Button onClick={() => handleZoomRangeChange("1 week")}>1 Week</Button>
        <Button onClick={() => handleZoomRangeChange("1 month")}>
          1 Month
        </Button>
        <Button onClick={() => handleZoomRangeChange("3 months")}>
          3 Months
        </Button>
        <Button onClick={() => handleZoomRangeChange("6 months")}>
          6 Months
        </Button>
        <Button onClick={() => handleZoomRangeChange("1 year")}>1 Year</Button>
        <Button onClick={() => handleZoomRangeChange("3 years")}>
          3 Years
        </Button>
      </div>
      <Line ref={chartRef} data={chartData} options={options} />
      <div>
        <Button onClick={toggleSmooth}> Smooth</Button>
        <Button onClick={togglePropagate}> Propagate</Button>
      </div>
    </div>
  );
}
