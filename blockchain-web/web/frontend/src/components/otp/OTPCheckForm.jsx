import React, { useEffect, useRef, useState } from "react";
import { Button, Modal } from "react-bootstrap";
import toast from "react-hot-toast";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import "../../components/cart/OTP.css";
import {
  useResendOTPMutation,
  useVerifyOrderMutation,
} from "../../redux/api/otpApi";
import { useDispatch, useSelector } from "react-redux";

const OTPPage = ({ orderId, onClose }) => {
  const { user } = useSelector((state) => state.auth);
  console.log(user);
  const navigate = useNavigate();
  const [otp, setOtp] = useState([]);
  const inputRefs = useRef([]);
  const [modalShow, setModalShow] = useState(true);

  const [verifyOTP] = useVerifyOrderMutation();
  const [resendOTP] = useResendOTPMutation();

  useEffect(() => {
    // Hiển thị modal khi component được render
    setModalShow(true);
  }, []);

  const handleOTPChange = (e, index) => {
    const newOTP = [...otp];
    newOTP[index] = e.target.value;
    setOtp(newOTP);
    if (e.target.value.length === 1 && index < 5) {
      inputRefs.current[index + 1].focus();
    }
  };
  const handlePaste = (e) => {
    e.preventDefault();
    const pastedValue = e.clipboardData.getData("Text");
    setOtp(pastedValue);
    inputRefs.current[5].focus();
  };
  const handleClearOTP = () => {
    setOtp([]);
    inputRefs.current[0].focus();
  };

  const handleConfirmOrder = async () => {
    try {
      const otpRequest = Array.isArray(otp) ? otp.join("") : otp;
      if (otpRequest.length === 0) toast.error("please type your OTP!");

      const response = await verifyOTP({
        // userId: user.userId,
        otp: otpRequest,
        orderId: orderId,
      });

      if (response?.data?.status === "OK") {
        // Đóng modal khi nhập OTP thành công
        setModalShow(false);
        Swal.fire({
          icon: "success",
          title: "OTP confirmed successfully!",
        });
        // Load lại trang khi OTP được xác nhận thành công
        window.location.reload();
      } else {
        Swal.fire({
          icon: "error",
          title: "Oops...",
          // text: "Incorrect OTP. Please try again.",
          text: response.error.data.message,
        });
      }
    } catch (error) {
      console.error("Error verifying OTP:", error);
    }
  };

  const handleResendOTP = async () => {
    try {
      await resendOTP({ orderId: orderId });
      Swal.fire({
        icon: "success",
        title: "OTP resent successfully",
      });
    } catch (error) {
      console.error("Error resending OTP:", error);
      Swal.fire({
        icon: "error",
        title: "Oops...",
        text: "Failed to resend OTP. Please try again.",
      });
    }
  };

  return (
    <Modal show={modalShow} onHide={onClose}>
      <Modal.Header closeButton>
        <Modal.Title>Enter OTP</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <p style={{ marginBottom: "30px" }}>
          Please check your email to receive the OTP code
        </p>
        <div className="d-flex justify-content-between">
          {[...Array(6)].map((_, index) => (
            <input
              ref={(el) => (inputRefs.current[index] = el)}
              key={index}
              type="text"
              onPaste={handlePaste}
              maxLength="1"
              className="otp-input user-select-none "
              value={otp[index] || ""}
              onChange={(e) => handleOTPChange(e, index)}
              onFocus={(e) => e.target.select()}
            />
          ))}
        </div>
      </Modal.Body>
      <Modal.Footer>
        <Button variant="danger" onClick={handleClearOTP}>
          Clear
        </Button>
        <Button variant="info" onClick={handleResendOTP}>
          Resend OTP
        </Button>{" "}
        <Button
          variant="primary"
          onClick={handleConfirmOrder}
          disabled={otp.length === 0}
        >
          Confirm OTP
        </Button>
      </Modal.Footer>
    </Modal>
  );
};

export default OTPPage;
