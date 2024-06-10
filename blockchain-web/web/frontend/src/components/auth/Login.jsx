import React, { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { useLoginMutation } from "../../redux/api/authApi";
import MetaData from "../layout/MetaData";
import "./Login.css"; // Import file CSS
const Yup = require("yup");
const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const navigate = useNavigate();

  const [login, { isLoading, error }] = useLoginMutation();
  const { isAuthenticated, user, roles, loading } = useSelector(
    (state) => state.auth
  );
  useEffect(() => {
    if (isAuthenticated) {
      if (roles?.includes("ROLE_ADMIN") || roles?.includes("ROLE_STAFF")) {
        navigate("/admin/dashboard");
      } else {
        navigate("/");
      }
    }
    if (error) {
      if (error.data) {
        if (typeof error.data === "string") {
          toast.error(error.data);
        } else {
          if (error.data.status === 401) {
            toast.error("username or password are incorrect!");
            return;
          }
          toast.error(error.data.message);
        }
      } else {
        toast.error("An error occurred. Please try again later.");
      }
    }
  }, [error, isAuthenticated, navigate]);

  const submitHandler = (e) => {
    e.preventDefault();
    const loginData = {
      username,
      password,
    };

    login(loginData);
  };
  const loginSchema = Yup.object().shape({
    username: Yup.string()
      .required("Username is required")
      .max(20, "username must be at most 20 characters"),
    password: Yup.string()
      .required("Password is required")
      .min(6, "Password must be at least 6 characters")
      .max(100, "Password must be at most 100 characters"),
  });

  return (
    <>
      <MetaData title={"Login"} />
      <div style={{ marginTop: "9rem" }}>
        <style jsx>{`
          .App {
            background-image: url(/images/bannerBGSS2.png);
            background-size: cover;
            border-radius: 8px;
            background-repeat: no-repeat;
            background-position: center;
            min-height: 100vh;
          }
        `}</style>
        <div className="col-12 col-lg-4 justify-content-center align-items-center float-end text">
          <form
            className="shadow rounded bg-body form-container"
            onSubmit={submitHandler}
          >
            <h2 className="form-title">Login</h2>
            <div className="mb-3 form-input">
              <label htmlFor="username_field" className="form-label">
                Username
              </label>
              <input
                type="text"
                id="username_field"
                className="form-control"
                style={{ color: "#ffa500" }}
                name="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
            </div>

            <div className="mb-3 form-input">
              <label htmlFor="password_field" className="form-label">
                Password
              </label>
              <input
                type="password"
                id="password_field"
                className="form-control"
                name="password"
                style={{ color: "#ffa500" }}
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>

            {/* <Link to="/password/forgot" className="float-end mb-4 form-link">
              Forgot Password?
            </Link> */}

            <button
              id="login_button"
              type="submit"
              className="btn w-100 py-2 form-button"
              disabled={isLoading}
              style={{
                backgroundColor: "#ffa500",
                color: "#000",
                borderRadius: "10px",
              }}
            >
              {isLoading ? "Authenticating..." : "LOGIN"}
            </button>

            <div className="my-3 mb-5">
              <Link to="/register" className="float-end form-link">
                New User?
              </Link>
            </div>
          </form>
        </div>
      </div>
    </>
  );
};

export default Login;
