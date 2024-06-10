import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  user: null,
  isAuthenticated: false,
  loading: true,
  roles: localStorage.getItem('user-roles'),
};
export const userSlice = createSlice({
  initialState,
  name: "userSlice",
  reducers: {
    setUser(state, action) {
      state.user = action.payload;
    },
    setIsAuthenticated(state, action) {
      state.isAuthenticated = action.payload;
    },
    setLoading(state, action) {
      state.loading = action.payload;
    },
    setRoles(state, action) {
      state.roles = action.payload?.join("|") ;
    },
  },
});

export default userSlice.reducer;

export const { setIsAuthenticated, setUser, setLoading, setRoles } =
  userSlice.actions;
