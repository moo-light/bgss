import { createSlice } from "@reduxjs/toolkit";
import { format } from "date-fns";
localStorage.setItem(
  "live-rate",
  '{ "endpoint": "live", "ask": 2327.8, "symbol": "XAUUSD", "bid": 2327.46, "mid": 2327.63, "changedPrice":0.0,     "changedPercentage":0.0, "lastUpdated":"23:06:42", "ts": 1717258002 }'
); // this is closed data from sat 6/1/2024
let liveRate = localStorage.getItem("live-rate");
if (liveRate) {
  try {
    liveRate = JSON.parse(liveRate);
  } catch (e) {
    liveRate = null;
  }
}
const isMarketClose = false;
// !isMarketOpen()
const initialState = {
  data: liveRate,
  isLoading: false,
  historyData: null,
  tradeData: null,
  marketClosed: isMarketClose,
};
const liveRateSlice = createSlice({
  name: "liveRateSlice",
  initialState,
  reducers: {
    setLiveRate: (state, actions) => {
      const newLiveRate = actions.payload ?? {};
      if (newLiveRate?.ts === state.data?.ts) return;
      // Compare the old live rate and set changedPrice and changedPercentage
      if (state.data) {
        const changedPrice = newLiveRate.mid - state.data.mid;
        const changedPercentage = changedPrice / state.data.mid;
        newLiveRate.changedPrice = changedPrice;
        newLiveRate.changedPercentage = changedPercentage;
      } else {
        newLiveRate.changedPrice = 0.0;
        newLiveRate.changedPercentage = 0.0;
      }
      newLiveRate.lastUpdated =
        newLiveRate.ts && format(new Date(Number(newLiveRate.ts*1000)), "HH:mm:ss");
      // store final live Rate
      state.data = newLiveRate;
      localStorage.setItem("live-rate", JSON.stringify(newLiveRate));
      state.marketClosed = false;
      state.isLoading = false;
    },
    setIsLoading: (state, action) => {
      state.isLoading = action.payload;
    },
    setTradeData: (state, action) => {
      state.tradeData = action.payload;
    },
    setHistoryData: (state, action) => {
      state.historyData = action.payload;
    },
  },
});

export default liveRateSlice.reducer;

export const { setLiveRate, setTradeData, setIsLoading, setHistoryData } =
  liveRateSlice.actions;
