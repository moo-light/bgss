import React from "react";
import TradingViewWidget from "./TradingViewWidget";
// registerLicense(CHART_KEY);

const GoldPriceCharts = ({ ...other }) => {
  return (
    <div className="control-section col " style={{minHeight:500}}>
      <TradingViewWidget></TradingViewWidget>
    </div>
  );
};

export { GoldPriceCharts };

