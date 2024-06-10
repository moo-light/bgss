import React, { useEffect, useState } from "react";
import { useForgotPasswordMutation } from "../../redux/api/userApi";
import { useSelector } from "react-redux";
import toast from "react-hot-toast";
import { useNavigate } from "react-router-dom";
import MetaData from "../layout/MetaData";
import "./Login.css"; // Import file CSS

const ForgotPassword = () => {
  const [email, setEmail] = useState("");

  const navigate = useNavigate();

  const [forgotPassword, { isLoading, error, isSuccess }] =
    useForgotPasswordMutation();

  const { isAuthenticated } = useSelector((state) => state.auth);

  useEffect(() => {
    if (isAuthenticated) {
      navigate("/");
    }
    if (error) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success("Email Sent. Please check your inbox");
    }
  }, [error, isAuthenticated, isSuccess]);

  const submitHandler = (e) => {
    e.preventDefault();

    forgotPassword({ email });
  };

  return (
    <>
      <MetaData title={"Forgot Password"} />
      <div className="row wrapper">
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
        <div className="col-10 col-lg-5">
          <form
            className="shadow rounded bg-body form-container"
            onSubmit={submitHandler}
          >
            <h2 className="form-title">Forgot Password</h2>
            <div className="mt-3">
              <label htmlFor="email_field" className="form-label">
                Enter Email
              </label>
              <input
                type="email"
                id="email_field"
                className="form-control"
                name="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>

            <button
              id="forgot_password_button"
              type="submit"
              className="btn w-100 py-2"
              disabled={isLoading}
            >
              {isLoading ? "Sending..." : "Send Email"}
            </button>
          </form>
        </div>
      </div>
    </>
  );
};

export default ForgotPassword;
