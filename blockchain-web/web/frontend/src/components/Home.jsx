import React from "react";
import { Link } from "react-router-dom";
import "./auth/Login.css";
import { GoldChartData } from "./charts/GoldChartData";
import { GoldPriceCharts } from "./charts/GoldPriceChart";
import MetaData from "./layout/MetaData";
const Home = () => {
  return (
    <>
      {/* <Carousel></Carousel> */}
      <MetaData title={"BGSS"} />
      <div style={{ position: "relative" }}></div>
      <h1
        id="products_heading"
        className="text-secondary border-border-bottom "
      >
        {"Charts"}
      </h1>
      <div
        className="d-md-flex  p-3 pb-0 gap-1 d-block  justify-content-between  "
        id="home_chart"
      >
        <GoldPriceCharts className={"col"} />
        {/* <GoldPriceCharts className="col-12 col-md-8 col-lg-8 " /> */}
        <GoldChartData className="information col-12 col-md-4 col-lg-4 p-4" />
      </div>
      <hr></hr>
      <Link to="/products" className="btn orange">
        See Product List
      </Link>
    </>
  );
};

export default Home;
