import "./App.css";

import {
  Route,
  BrowserRouter as Router,
  Routes,
  useLocation,
} from "react-router-dom";

import { Toaster } from "react-hot-toast";
import Footer from "./components/layout/Footer";
import Header from "./components/layout/Header";

import { useSelector } from "react-redux";
import "./App.css";
import CheckValidPay from "./components/admin/check-valid-pay/CheckValidPay";
import AuthForm from "./components/auth/AuthForm";
import NotFound from "./components/layout/NotFound";
import useAdminRoutes from "./components/routes/adminRoutes";
import useStaffRoutes from "./components/routes/staffRoutes";
import useUserRoutes from "./components/routes/userRoutes";

function App() {
  const { mode, changing } = useSelector((state) => state.theme);
  return (
    <Router>
      <div className={`App ${mode} ${changing}`} data-bs-theme={mode}>
        <Toaster position="top-center" />
        <Header />
        <AppBody />
        <Footer />
      </div>
    </Router>
  );
}
function AppBody() {
  const userRoutes = useUserRoutes();
  const adminRoutes = useAdminRoutes();
  const staffRoutes = useStaffRoutes();
  const location = useLocation();

  return (
    <div className={`container-fluid ${!location.pathname.includes("admin")} `}>
      <Routes>
        {userRoutes}
        {adminRoutes}
        {staffRoutes}
        <Route path="/auth-form" element={<AuthForm />} />
        <Route path="/check-valid-pay" element={<CheckValidPay />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </div>
  );
}
export default App;
