import CloseIcon from "@mui/icons-material/Close";
import LockIcon from "@mui/icons-material/Lock";
import { LoadingButton } from "@mui/lab";
import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  Typography,
} from "@mui/material";
import { useTheme } from "@mui/material/styles";
import React, { useEffect, useState } from "react";
import { FormProvider, useForm } from "react-hook-form";
import toast from "react-hot-toast";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import {
  useResendOTPWithdrawMutation,
  useVerifyWithdrawMutation,
} from "../../redux/api/otpApi";
import { useMyWithdrawListQuery } from "../../redux/api/transactionApi";
import RHFCode from "./rhf-code";

const OTPWithdrawPage = ({ withdrawId, onClose }) => {
  const { user } = useSelector((state) => state.auth);
  const navigate = useNavigate();
  const [modalShow, setModalShow] = useState(true);
  const withdrawList = useMyWithdrawListQuery();
  const [verifyOTP] = useVerifyWithdrawMutation();
  const [resendOTP] = useResendOTPWithdrawMutation();
  const [isLoading, setIsLoading] = useState(false);
  const [isLoadingResent, setIsLoadingResent] = useState(false);
  const theme = useTheme();

  const methods = useForm({
    defaultValues: {
      otp: "",
    },
  });

  const handleConfirmOrder = async (data) => {
    try {
      setIsLoading(true);
      if (!data.otp || data.otp.length === 0) {
        setModalShow(false);
        Swal.fire({
          icon: "error",
          title: "Required!",
          text: "Please type your OTP",
        });
        return;
      }
      const response = await verifyOTP({
        userInfoId: user?.userId,
        otp: data.otp,
        withdrawId: withdrawId,
      });

      if (response?.data?.status === "OK") {
        setModalShow(false);
        Swal.fire({
          icon: "success",
          title: "OTP confirmed successfully!",
        });
        // window.location.reload();
        withdrawList.refetch();
        setIsLoading(false);
        toast.success("OTP confirmed successfully");
      } else {
        Swal.fire({
          icon: "error",
          title: "Oops...",
          text: response.error.data.message,
        });
        setIsLoading(false);
        toast.error(response.error.data.message);
        // Không đóng form khi nhập sai OTP
        // setModalShow(false);
        withdrawList.refetch();
      }
    } catch (error) {
      console.error("Error verifying OTP:", error);
      toast.error("Something went wrong! Please try Again");
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    // it worked and i dont know why
    if (!modalShow) onClose();
  }, [modalShow, onClose]);

  const handleResendOTP = async () => {
    try {
      setIsLoadingResent(true);
      const res = await resendOTP({ withdrawId: withdrawId });
      Swal.fire({
        icon: "success",
        title: "OTP resent successfully",
      });
      setIsLoadingResent(false);
      toast.success(res?.data?.message);
    } catch (error) {
      console.error("Error resending OTP:", error);
      Swal.fire({
        icon: "error",
        title: "Oops...",
        text: "Failed to resend OTP. Please try again.",
      });
      setIsLoadingResent(false);
      toast.error("Something went wrong! Please try again");
    }
  };

  return (
    <FormProvider {...methods}>
      <Dialog
        open={modalShow}
        onClose={onClose}
        PaperProps={{
          sx: {
            borderRadius: 4,
          },
        }}
      >
        <Box
          display="flex"
          justifyContent="space-between"
          alignItems="center"
          padding={2}
        >
          <DialogTitle>Enter OTP</DialogTitle>
          <IconButton onClick={onClose}>
            <CloseIcon />
          </IconButton>
        </Box>
        <DialogContent>
          <Box display="flex" alignItems="center" marginBottom={2}>
            <LockIcon color="primary" />
            <Typography variant="h6" marginLeft={1}>
              Secure Verification
            </Typography>
          </Box>
          <Typography variant="body2" marginBottom={3}>
            Please check your email to receive the OTP code.
          </Typography>
          <RHFCode name="otp" />
        </DialogContent>
        <DialogActions>
          <Button
            variant="outlined"
            color="secondary"
            sx={{ color: "black", borderRadius: 2 }}
            onClick={() => methods.reset()}
          >
            Clear
          </Button>
          <LoadingButton
            variant="outlined"
            onClick={handleResendOTP}
            type="button"
            loading={isLoadingResent}
            sx={{
              backgroundColor: theme.palette.info.main,
              color: "white",
              borderRadius: 2,
              "&:hover": {
                backgroundColor: theme.palette.info.dark,
              },
            }}
          >
            Resend OTP
          </LoadingButton>

          <LoadingButton
            variant="outlined"
            onClick={methods.handleSubmit(handleConfirmOrder)}
            type="submit"
            loading={isLoading}
            disabled={methods.watch("otp").length === 0}
            sx={{
              backgroundColor: "orange",
              color: "white",
              borderRadius: 2,
              "&:hover": {
                backgroundColor: "darkorange",
              },
            }}
          >
            Confirm OTP
          </LoadingButton>
        </DialogActions>
      </Dialog>
    </FormProvider>
  );
};

export default OTPWithdrawPage;
