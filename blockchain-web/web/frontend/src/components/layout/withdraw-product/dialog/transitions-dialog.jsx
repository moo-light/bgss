import { forwardRef, useState } from 'react';
import { createTheme, ThemeProvider } from '@mui/material/styles';
import Slide from '@mui/material/Slide';
import Button from '@mui/material/Button';
import Dialog from '@mui/material/Dialog';
import DialogTitle from '@mui/material/DialogTitle';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import PropTypes from 'prop-types';
import { LoadingButton } from "@mui/lab";
import { useSelector } from "react-redux";
import axios, { endpoints } from '../../../../utils/axios';
import OTPWithdrawPage from '../../../otp/OTPCheckFormWIthdraw';
import toast from "react-hot-toast";
import Alert from '@mui/material/Alert';

const theme = createTheme({
    palette: {
      primary: {
        main: '#000000',
      },
    },
    components: {
      MuiButton: {
        styleOverrides: {
          root: {
            color: '#ffffff',
          },
        },
      },
    },
  });
  
  const Transition = forwardRef((props, ref) => <Slide direction="up" ref={ref} {...props} />);
  
  function TransitionsDialog({ openTransition, closeTransition, productId, productWeightTroyOz, unit, weight }) {
    const { user } = useSelector((state) => state.auth);
    const [isloadingWithdraw, setIsLoadingWithdraw] = useState(false);
    const [withdrawId, setWithdrawId] = useState(null);
    const [showOTP, setShowOTP] = useState(false);
    const [withdrawSuccess, setWithdrawSuccess] = useState(false);
    const withdrawRequirement = 'AVAILABLE';
    const [openOTP, setOpenOTP] = useState(false);
    const [isLoadingOTP, setIsLoadingOTP] = useState(false);

    const handleClose = () => {
      closeTransition(false);
    };
  
    const handleCloseOTP = () => { 
        setShowOTP(false);
     }
  
    const onSubmitWithdrawal = async () => {
      setIsLoadingWithdraw(true);
      const token = localStorage.getItem("token");
      console.log(token);

      const url = `${endpoints.withdraw.withdrawal_request}/${user?.userId}?unit=MACE&withdrawRequirement=${withdrawRequirement}&productId=${productId}`;

      try {
        const res = await axios.post(
          url,
          {},
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          }
        );
        setWithdrawId(res?.data?.data?.id);
        setIsLoadingWithdraw(false);
        setWithdrawSuccess(true);
        closeTransition(true);
        toast.success("Withdraw Successs! Please check your email to verify!");
        setShowOTP(true);
        return res?.data;
      } catch (error) {
        console.error("Failed to api call", error);
        setIsLoadingWithdraw(false);
      }
    };


  
    return (
      <ThemeProvider theme={theme}>
        <div>
          <Dialog
            keepMounted
            open={openTransition}
            TransitionComponent={Transition}
            onClose={closeTransition}
            PaperProps={{
              sx: { borderRadius: 4 } // Adjust the value as needed
            }}
          >
            <DialogTitle>
              {`Request Withdraw Gold`}
            </DialogTitle>
  
            <DialogContent sx={{ color: 'text.secondary' }}>
                You will be deducted {productWeightTroyOz} TROY-OZ converted from {weight} {unit}
            </DialogContent>
  
            <DialogActions>
              <Button variant="outlined" sx={{ borderRadius: 2, color: '#000000'  }} onClick={closeTransition}>
                Disagree
              </Button>
              <LoadingButton variant="contained" sx={{ borderRadius: 2 }} color="primary" autoFocus onClick={onSubmitWithdrawal} loading={isloadingWithdraw} >
                Agree
              </LoadingButton>
            </DialogActions>
          </Dialog>
        </div>
        {showOTP && withdrawSuccess && (
        <OTPWithdrawPage
          withdrawId={withdrawId}
          onClose={handleCloseOTP}
        //   show={showOTP}
        />
      )}
      </ThemeProvider>
    );
  }
  
  TransitionsDialog.propTypes = {
    openTransition: PropTypes.bool,
    closeTransition: PropTypes.func,
    productId: PropTypes.number,
    productWeightTroyOz: PropTypes.string,
    unit: PropTypes.oneOf(['MACE', 'TAEL', 'KILOGRAM', 'TROY_OZ', 'GRAM']).isRequired,
  };
  
  export default TransitionsDialog;