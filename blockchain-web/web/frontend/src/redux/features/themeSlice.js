import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  mode: localStorage.getItem("mode") ?? "light" ,
  changing: "",
};

export const themeSlice = createSlice({
  initialState,
  name: "themeSlice",
  reducers: {
    toggleDarkTheme:(state) => {
      state.changing = "changing";
      state.mode = state.mode === "light" ? "dark" : "light";
      localStorage.setItem("theme", state.mode);
    },
  },
});

export default themeSlice.reducer;

export const { toggleDarkTheme } = themeSlice.actions;
