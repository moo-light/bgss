import React, { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import * as Yup from "yup";
import { FormError } from "../../helpers/components/form-error";
import { useRegisterMutation } from "../../redux/api/authApi";
import MetaData from "../layout/MetaData";
import "./Login.css"; // Import file CSS

const Register = () => {
  const [user, setUser] = useState({
    username: "",
    email: "",
    password: "",
    phoneNumber: "", // Thêm trường phoneNumber
    firstName: "", // Thêm trường firstName
    lastName: "", // Thêm trường lastName
    address: "", // Thêm trường lastName
    confirm_password: "", // Thêm trường lastName
  });

  const {
    username,
    email,
    password,
    confirm_password,
    phoneNumber,
    firstName,
    lastName,
    address,
  } = user;

  const navigate = useNavigate();

  const [register, { isLoading, isError, error, isSuccess, data }] =
    useRegisterMutation();

  const { isAuthenticated } = useSelector((state) => state.auth);

  useEffect(() => {
    if (isSuccess) {
      toast.success(
        "User registered successfully. Please check your email to verify your account."
      );
      navigate("/login");
    }
    if (isError) {
      toast.error(error?.data?.message);
    }
  }, [isError, isSuccess]);

  const registerSchema = Yup.object().shape({
    username: Yup.string()
      .required("Username is required")
      .max(20, "username must be at most 20 characters"),
    email: Yup.string()
      .email("Must be a valid email")
      .required("Email is required"),
    password: Yup.string()
      .required("Password is required")
      .min(6, "Password must be at least 6 characters")
      .max(100, "Password must be at most 100 characters"),
    confirm_password: Yup.string().required("Password is required"),
    // .equals(password),
    phoneNumber: Yup.string()
      .required("Phone number is required")
      .matches(/^[0-9]+$/, "Phone number must be only digits"),
    firstName: Yup.string()
      .required("First name is required")
      .max(20, "First Name too long"),
    lastName: Yup.string().required("Last name is required"),
    address: Yup.string()
      .required("Address is required")
      .min(10, "Address too short")
      .max(300, "Address too long"),
  });

  const [errors, setErrors] = useState("");
  const submitHandler = (e) => {
    e.preventDefault();

    registerSchema
      .validate(user, { abortEarly: false })
      .then(() => {
        register(user)
      })
      .catch((yupError) => {
        const newErrors = {};
        if (yupError.inner) {
          yupError.inner.forEach((error) => {
            newErrors[error.path] = error.message;
          });
        }
        setErrors(newErrors);
        toast.error("Please fix the validation errors.");
      });
  };

  const onChange = (e) => {
    setUser({ ...user, [e.target.name]: e.target.value });
    if (errors[e.target.name]) {
      setErrors({ ...errors, [e.target.name]: undefined });
    }
  };

  return (
    <>
      <MetaData title={"Register"} />
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
            <h2 className="form-title">Register</h2>

            <div className="mb-3">
              <label htmlFor="username_field" className="form-label">
                Username
              </label>
              <input
                type="text"
                id="username_field"
                className={`form-control ${
                  errors.username ? "input-error" : ""
                }`}
                name="username"
                value={username}
                onChange={onChange}
              />
              <FormError name="username" errorData={errors} />
            </div>

            <div className="mb-3">
              <label htmlFor="first_name_field" className="form-label">
                First Name
              </label>
              <input
                type="text"
                id="first_name_field"
                className={`form-control ${
                  errors.firstName ? "input-error" : ""
                }`}
                name="firstName"
                value={firstName}
                onChange={onChange}
              />
              <FormError name="firstName" errorData={errors} />
            </div>

            <div className="mb-3">
              <label htmlFor="last_name_field" className="form-label">
                Last Name
              </label>
              <input
                type="text"
                id="last_name_field"
                className={`form-control ${
                  errors.lastName ? "input-error" : ""
                }`}
                name="lastName"
                value={lastName}
                onChange={onChange}
              />
              <FormError name="lastName" errorData={errors} />
            </div>
            <div className="mb-3">
              <label htmlFor="phone_field" className="form-label">
                Phone Number
              </label>
              <input
                type="number"
                id="phone_field"
                className={`form-control ${
                  errors.phoneNumber ? "input-error" : ""
                }`}
                name="phoneNumber"
                value={phoneNumber}
                onChange={onChange}
              />
              <FormError name="phoneNumber" errorData={errors} />
            </div>
            <div className="mb-3">
              <label htmlFor="email_field" className="form-label">
                Email
              </label>
              <input
                type="email"
                id="email_field"
                className={`form-control ${errors.email ? "input-error" : ""}`}
                name="email"
                value={email}
                onChange={onChange}
              />
              <FormError name="email" errorData={errors} />
            </div>
            <div className="mb-3">
              <label htmlFor="address _field" className="form-label">
                Address
              </label>
              <input
                type="text"
                id="address_field"
                className={`form-control ${
                  errors.address ? "input-error" : ""
                }`}
                name="address"
                value={address}
                onChange={onChange}
              />
              <FormError name="address" errorData={errors} />
            </div>
            <div className="mb-3">
              <label htmlFor="password_field" className="form-label">
                Password
              </label>
              <input
                type="password"
                id="password_field"
                className={`form-control ${
                  errors.password ? "input-error" : ""
                }`}
                name="password"
                value={password}
                onChange={onChange}
              />
              <FormError name="password" errorData={errors} />
            </div>
            <div className="mb-3">
              <label htmlFor="confirm_password_field" className="form-label">
                Confirm Password
              </label>
              <input
                type="password"
                id="confirm_password_field"
                className={`form-control ${
                  errors.confirm_password ? "input-error" : ""
                }`}
                name="confirm_password"
                value={confirm_password}
                onChange={onChange}
              />
              <FormError name="confirm_password" errorData={errors} />
            </div>
            <button
              id="register_button"
              type="submit"
              className="btn w-100 py-2"
              disabled={isLoading}
              style={{
                backgroundColor: "#ffa500",
                color: "#000",
                borderRadius: "10px",
              }}
            >
              {isLoading ? "Loading..." : "REGISTER"}
            </button>
          </form>
        </div>
      </div>
    </>
  );
};

export default Register;
