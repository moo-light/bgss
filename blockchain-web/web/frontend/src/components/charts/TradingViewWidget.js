import React, { memo, useEffect, useRef } from "react";

function TradingViewWidget() {
  const container = useRef();
  const scriptId = ".tradingview-chart-script"; // ID duy nhất cho script
  useEffect(() => {
    if (!container.current) {
      return;
    }

    // Xác định script cũ nếu có và xóa bỏ
    const existingScript = container.current.querySelector("script");
    const existingScriptTV = container.current.querySelector(scriptId);
    if (existingScript) {
      // existingScript.remove();
      return () => {};
    }
    if (existingScriptTV) {
      existingScriptTV.remove();
    }
    const script = document.createElement("script");
    script.src =
      "https://s3.tradingview.com/external-embedding/embed-widget-advanced-chart.js";
    script.async = true;
    script.type = "text/javascript";
    script.id = scriptId; // Thiết lập ID cho script

    // Thành phần chuỗi JSON nên được thiết lập escape chuẩn và sử dụng gạch dưới cho key
    script.innerHTML = JSON.stringify({
      autosize: true,
      symbol: "FOREXCOM:XAUUSD",
      timezone: "Asia/Ho_Chi_Minh",
      style: "1",
      locale: "en",
      enable_publishing: false,
      withdateranges: true,
      range: "YTD",
      hide_side_toolbar: false,
      allow_symbol_change: false,
      details: true,
      hotlist: true,
      calendar: false,
      support_host: "https://www.tradingview.com",
    });

    container.current.appendChild(script);

    // Không cần phải đặt cleanup nếu logic xóa bỏ đã chính xác
  }, []);

  return (
    <div
      className="tradingview-widget-container"
      ref={container}
      style={{ height: "100%", width: "100%" }}
    >
      <div className="tradingview-widget-container__widget" />
    </div>
  );
}

export default memo(TradingViewWidget);
// // TradingViewWidget.jsx
// import React, { memo, useEffect, useRef } from "react";

// function TradingViewWidget() {
//   const container = useRef();

//   useEffect(() => {
//     // Check if the script is already appended
//     const existingScript = container.current.querySelector("script");
//     if (existingScript) {
//       return () => {};
//     }

//     const script = document.createElement("script");
//     script.src =
//       "https://s3.tradingview.com/external-embedding/embed-widget-advanced-chart.js";
//     script.type = "text/javascript";
//     script.async = true;
//     script.innerHTML = `
//         {
//           "autosize": true,
//           "symbol": "FOREXCOM:XAUUSD",
//           "interval": "60",
//           "timezone": "Etc/UTC",
//           "theme": "light",
//           "style": "1",
//           "locale": "en",
//           "range": "YTD",
//           "enable_publishing": false,
//           "hide_side_toolbar": false,
//           "allow_symbol_change": true,
//           "calendar": false,
//           "support_host": "https://www.tradingview.com"
//         }`;
//     container.current.appendChild(script);
//   }, []);

//   return (
//     <div
//       className="tradingview-widget-container"
//       ref={container}
//       style={{ height: "100%", width: "100%" }}
//     >
//       <div
//         className="tradingview-widget-container__widget"
//         style={{ height: "calc(100% - 32px)", width: "100%" }}
//       ></div>
//       {/* <div className="tradingview-widget-copyright">
//         <a
//           href="https://www.tradingview.com/"
//           rel="noopener nofollow noreferrer"
//           target="_blank"
//         >
//           <span className="blue-text">Track all markets on TradingView</span>
//         </a>
//       </div> */}
//     </div>
//   );
// }

// export default memo(TradingViewWidget);
