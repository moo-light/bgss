import { useEffect } from "react";
import toast from "react-hot-toast";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useGetMeQuery } from "../../../redux/api/userApi";
import Loader from "../../layout/Loader";

function CheckValidPay() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const result = useGetMeQuery();
  const codes = {
    "00": "Transaction successful",
    "07": "Transaction suspected of fraud. Please contact customer support for assistance.",
    "09": "Transaction unsuccessful. Your card/account is not registered for Internet Banking service. Please contact your bank.",
    10: "Transaction unsuccessful. Please check the information entered and try again.",
    11: "Transaction unsuccessful. Payment waiting period has expired. Please retry the transaction.",
    12: "Transaction unsuccessful. Your card/account is locked. Please contact your bank.",
    13: "Transaction unsuccessful. Please enter the correct transaction authentication password (OTP) and try again.",
    24: "Transaction canceled by customer.",
    51: "Transaction unsuccessful. Insufficient balance in your account.",
    65: "Transaction unsuccessful. Your account has exceeded the daily transaction limit.",
    75: "Payment bank is currently under maintenance. Please try again later.",
    79: "Transaction unsuccessful. Please ensure you enter the payment password correctly and try again.",
    99: "Unknown error occurred.",
  };

  useEffect(() => {
    const vnp_ResponseCode = searchParams.get("vnp_ResponseCode");
    if (vnp_ResponseCode === "00") {
      toast.success(codes[vnp_ResponseCode]);
      result.refetch();
    } else if (Object.keys(codes).includes(vnp_ResponseCode)) {
      toast.error(codes[vnp_ResponseCode]);
    }
  }, []);
  navigate("/me/profile", { replace: true });

  return <Loader />;
}

export default CheckValidPay;
