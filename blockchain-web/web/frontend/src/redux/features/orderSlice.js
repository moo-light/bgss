import { createSlice } from "@reduxjs/toolkit";
import toast from "react-hot-toast";

const initialState = {
  currentOrder: JSON.parse(localStorage.getItem("currentOrder") ?? "null"),
};

export const orderSlice = createSlice({
  initialState,
  name: "orderSlice",
  reducers: {
    setCurrentOrder: (state, action) => {
      state.currentOrder = action.payload;
      localStorage.setItem("currentOrder", JSON.stringify(state.currentOrder));
    },
    checkExpirer: (state, action) => {
      if (!state.currentOrder) return;
      if (
        !state.currentOrder.expirer ||
        new Date(state.currentOrder.expirer) < new Date()
      ) {
        state.currentOrder = null; // Update the currentOrder directly
        localStorage.removeItem("currentOrder"); // Optionally remove from localStorage
        toast.call("order Expired");
      }
    },
  },
});

export default orderSlice.reducer;

export const { setCurrentOrder, checkExpirer } = orderSlice.actions;
