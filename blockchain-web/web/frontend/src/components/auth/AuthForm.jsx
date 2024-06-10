import React, { useEffect, useState } from "react";
import { createPortal } from "react-dom";
import toast from "react-hot-toast";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import * as Yup from "yup";
import "../../css/authform.css";
import { FormError } from "../../helpers/components/form-error";
import { useLoginMutation, useRegisterMutation } from "../../redux/api/authApi";

const AuthForm = () => {
  const [isRightPanelActive, setIsRightPanelActive] = useState(false);
  const [isUpPanelActive, setIsUpPanelActive] = useState(false);
  const [usernameLogin, setUsername] = useState("");
  const [passwordLogin, setPassword] = useState("");
  const [user, setUser] = useState({
    username: "",
    email: "",
    password: "",
    phoneNumber: "",
    firstName: "",
    lastName: "",
    address: "",
  });
  const {
    username,
    email,
    password,
    phoneNumber,
    firstName,
    lastName,
    address,
  } = user;
  const { isAuthenticated } = useSelector((state) => state.auth);
  const navigate = useNavigate();
  const [login, { isLoadingLogin, error: errorLogin }] = useLoginMutation();
  const [register, { isLoading: isLoadingRegister, error: errorRegister }] =
    useRegisterMutation();

  const [acceptedTerms, setAcceptedTerms] = useState(false);
  const [showTermsModal, setShowTermsModal] = useState(false);

  const handleTermsModal = () => {
    setShowTermsModal(!showTermsModal);
  };

  const closeModal = () => {
    setShowTermsModal(false);
  };

  // Handle login logic
  useEffect(() => {
    if (isAuthenticated) navigate("/");
    if (errorLogin) toast.error("Wrong username or password");
  }, [errorLogin, isAuthenticated]);

  const submitHandlerSignIn = (e) => {
    e.preventDefault();
    const loginData = {
      username: usernameLogin,
      password: passwordLogin,
    };

    login(loginData);
  };

  // Handle registration logic
  useEffect(() => {
    if (isAuthenticated) {
      navigate("/login");
    }
    if (errorRegister) {
      console.log(errorRegister);
      toast.error(errorRegister?.data?.message);
    }
  }, [isAuthenticated, errorRegister, navigate]);

  const registerSchema = Yup.object().shape({
    username: Yup.string()
      .min(2, "Username must be at least 2 characters") // Xác định độ dài tối thiểu là 2
      .required("Username is required"),
    email: Yup.string()
      .email("Must be a valid email")
      .test(
        "dot",
        "The domain extension must be 2 characters or more",
        (value = "") => {
          const array = value.split(".");
          return array[array.length - 1].length >= 2;
        }
      )
      .test(
        "at",
        "Invalid format. Please make sure your email " +
          "contains @ character followed by domain and domain extension",
        (value = "") => {
          return /[^@ \t\r\n]+@[^@ \t\r\n]+\.[^@ \t\r\n]+/.test(value);
        }
      )
      .required("Email is required"),
    password: Yup.string()
      .required("Password is required")
      .min(6, "Password must be at least 6 characters"),
    phoneNumber: Yup.string()
      .required("Phone number is required")
      .matches(
        /^0[0-9]{9}$/,
        "Phone number must start with 0 and contain only digits."
      )
      .max(12, "Phone number must be at most 12 characters"),
    firstName: Yup.string()
      .min(1, "Your first name must be at least 1 characters")
      .required("First name is required"),
    lastName: Yup.string()
      .min(2, "Your last name must be at least 2 characters")
      .required("Last name is required"),
  });

  const validateField = async (name, value) => {
    try {
      // Clear any previous errors
      setErrors((prevErrors) => ({ ...prevErrors, [name]: "" }));

      // Test the field value against the schema
      await Yup.reach(registerSchema, name).validate(value);
    } catch (error) {
      // Update errors state with the error message from exception
      setErrors((prevErrors) => ({ ...prevErrors, [name]: error.message }));
    }
  };

  const submitHandlerSignUp = async (e) => {
    e.preventDefault();
    if (!acceptedTerms) {
      toast.error("You must accept the terms and conditions to register.");
      return;
    }
    try {
      await registerSchema.validate(user, { abortEarly: false });

      // If no error is thrown, registration is successful
      register(user);
      // registerSchema
      //     .validate(user, {abortEarly: false})
      //     .then(() => {
      //         register(user);
      //     })
    } catch (yupError) {
      const newErrors = {};
      if (yupError.inner) {
        yupError.inner.forEach((error) => {
          newErrors[error.path] = error.message;
        });
      }

      setErrors(newErrors);
      // Show error toast if needed
      toast.error("Please fix the validation errors.");
    }
  };

  const [errors, setErrors] = useState("");

  const onRegisterChange = (e) => {
    const { name, value } = e.target;
    setUser({ ...user, [e.target.name]: e.target.value });
    setUser((prev) => ({ ...prev, [name]: value }));
    // Optionally reset the error state when the user starts typing
    if (errors[e.target.name]) {
      setErrors({ ...errors, [e.target.name]: undefined });
    }
    validateField(name, value);
  };

  // Handle changing panel
  const showSignUpPanel = () => setIsRightPanelActive(true);
  const showSignInPanel = () => setIsRightPanelActive(false);
  const showForgotPanel = () => setIsUpPanelActive(true);
  const hideForgotPanel = () => setIsUpPanelActive(false);

  return (
    <>
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
      <div className="auth-form">
        <div
          className={`container ${
            isRightPanelActive ? "right-panel-active" : ""
          } ${isUpPanelActive ? "up-panel-active" : ""}`}
          id="container"
        >
          <div className="form-container sign-up-container">
            <form onSubmit={submitHandlerSignUp}>
              <h1>Sign Up</h1>
              <input
                type="text"
                placeholder="User name"
                className={`form-control ${
                  errors.username ? "input-error" : ""
                }`}
                name="username"
                value={username}
                onChange={onRegisterChange}
              />
              <FormError name="username" errorData={errors} />
              <input
                type="email"
                placeholder="Email"
                className={`form-control ${errors.email ? "input-error" : ""}`}
                name="email"
                value={email}
                onChange={onRegisterChange}
              />
              <FormError name="email" errorData={errors} />
              <input
                type="number"
                placeholder="Phone number"
                className={`form-control ${
                  errors.phoneNumber ? "input-error" : ""
                }`}
                name="phoneNumber"
                value={phoneNumber}
                onChange={onRegisterChange}
              />
              <FormError name="phoneNumber" errorData={errors} />
              <input
                type="password"
                placeholder="Password"
                className={`form-control ${
                  errors.password ? "input-error" : ""
                }`}
                name="password"
                value={password}
                onChange={onRegisterChange}
              />
              <FormError name="password" errorData={errors} />
              <input
                type="text"
                placeholder="Your first name"
                className={`form-control ${
                  errors.firstName ? "input-error" : ""
                }`}
                name="firstName"
                value={firstName}
                onChange={onRegisterChange}
              />
              <FormError name="firstName" errorData={errors} />
              <input
                type="text"
                placeholder="Your last name"
                className={`form-control ${
                  errors.lastName ? "input-error" : ""
                }`}
                name="lastName"
                value={lastName}
                onChange={onRegisterChange}
              />
              <FormError name="lastName" errorData={errors} />

              <div className="terms-conditions">
                <input
                  type="checkbox"
                  id="acceptTerms"
                  name="acceptTerms"
                  checked={acceptedTerms}
                  onChange={(e) => setAcceptedTerms(e.target.checked)}
                />
                <label htmlFor="acceptTerms">
                  I accept the{" "}
                  <a href="#" onClick={() => handleTermsModal()}>
                    Terms and Conditions
                  </a>
                </label>
              </div>

              <button
                disabled={isLoadingRegister || !acceptedTerms}
                type="submit"
              >
                {isLoadingRegister ? "Signing Up..." : "Sign Up"}
              </button>
            </form>
          </div>
          <div className="form-container sign-in-container">
            <form onSubmit={submitHandlerSignIn}>
              <h1>Sign in</h1>
              <input
                type="text"
                placeholder="User name"
                name="usernameLogin"
                value={usernameLogin}
                onChange={(e) => setUsername(e.target.value)}
              />
              <input
                type="password"
                placeholder="Password"
                name="passwordLogin"
                value={passwordLogin}
                onChange={(e) => setPassword(e.target.value)}
              />
              <a href="#" className="forgot-password" onClick={showForgotPanel}>
                Forgot your password?
              </a>
              <button disabled={isLoadingLogin} type="submit">
                {isLoadingLogin ? "Authenticating..." : "Sign In"}
              </button>
            </form>
          </div>
          <div
            className="form-container forgot-password-container"
            id="forgot-password-form"
          >
            <form>
              <h1>Forgot Password</h1>
              <input
                className="forgot-input"
                type="email"
                placeholder="Email"
              />
              <button type="button">Reset Password</button>
              <a className="back-to-login" onClick={hideForgotPanel}>
                Back to Login
              </a>
            </form>
          </div>
          <div className="overlay-container">
            <div className="overlay">
              <div className="overlay-panel overlay-left">
                <h1>Welcome Back!</h1>
                <p>
                  To keep connected with us please login with your personal info
                </p>
                <button className="ghost" id="signIn" onClick={showSignInPanel}>
                  Sign In
                </button>
              </div>
              <div className="overlay-panel overlay-right">
                <h1>Hello, Friend!</h1>
                <p>Enter your personal details and start journey with us</p>
                <button className="ghost" id="signUp" onClick={showSignUpPanel}>
                  Sign Up
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
      {showTermsModal &&
        createPortal(
          <div className="terms-modal" onClick={closeModal}>
            <div className="terms-content" onClick={(e) => e.stopPropagation()}>
              <h2>Terms and Conditions</h2>
              <p>
                Website Terms and Conditions General Terms Welcome to BGSS's
                Blockchain Gold Selling System. These terms and conditions
                ("Terms") govern your access to and use of the Platform. By
                accessing or using the Platform, you agree to be bound by these
                Terms. Product and Service Offerings Gold Savings: Customers can
                purchase gold for savings purposes. This gold is stored securely
                by BGSS and can be redeemed in physical form at any of our
                designated stores. Crafted Gold: Customers can purchase crafted
                gold products. The items available include jewelry and other
                gold crafts which can be shipped to the customer or picked up
                in-store. Account Registration Customers must create an account
                to use the Platform. You agree to provide accurate, current, and
                complete information during the registration process and to
                update such information to keep it accurate, current, and
                complete. Purchases and Payment Gold prices fluctuate according
                to market conditions. The final price will be confirmed at the
                time of purchase. Payment must be completed using one of our
                accepted payment methods. Redemption of Physical Gold Gold
                savings can be redeemed in-store based on the current market
                value of gold, less any applicable fees and charges. Proper
                identification must be provided for in-store redemptions, and
                customers will be required to sign a receipt acknowledging the
                withdrawal. Shipping and Delivery for Crafted Gold Crafted gold
                products ordered through the Platform will be shipped to the
                address specified by the customer. [Your Company Name] is not
                responsible for any delays or damages incurred during shipping.
                Cancellations and Refunds All purchases of gold savings are
                considered final and cannot be refunded. For crafted gold
                products, customers may cancel their orders within 24 hours
                without penalty. After this period, cancellations may incur fees
                or may not be accepted. Limitation of Liability [Your Company
                Name] shall not be liable for any direct, indirect, incidental,
                special, or consequential damages that result from the use or
                inability to use the Platform. Changes to Terms We reserve the
                right to modify these Terms at any time. Your continued use of
                the Platform after such changes have been notified will
                constitute your consent to those changes. Contact Us If you have
                any questions about these Terms, please contact us at [Your
                Contact Information].
              </p>
              <button onClick={closeModal}>Close</button>
            </div>
          </div>,
          document.body
        )}
    </>
  );
};

export default AuthForm;
