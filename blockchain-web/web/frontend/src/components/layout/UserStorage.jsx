import { LoadingButton } from "@mui/lab";
import InputLabel from "@mui/material/InputLabel";
import MenuItem from "@mui/material/MenuItem";
import Select from "@mui/material/Select";
import React, { useCallback, useEffect, useState } from "react";
import {
  Button,
  Form,
  FormControl,
  FormLabel,
  InputGroup,
  Modal,
} from "react-bootstrap";
import InputGroupText from "react-bootstrap/esm/InputGroupText";
import toast from "react-hot-toast";
import { FaCoins, FaDollarSign } from "react-icons/fa";
import { useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import * as Yup from "yup";
import axios, { endpoints } from "../../../src/utils/axios";
import { GOLD_UNIT_CONVERT } from "../../helpers/converters";
import { currencyFormat } from "../../helpers/helpers";
import { useDebounce } from "../../hook/use-debounce";
import { useGetPaymentUrlMutation } from "../../redux/api/paymentApi";
import { useSearchProducts } from "../../redux/api/product-search-api";
import { useRequestWithdrawGoldMutation } from "../../redux/api/transactionApi";
import { weightConverter } from "../../redux/features/transferSlice";
import OTPWithdrawPage from "../otp/OTPCheckFormWIthdraw"; // Import Modal OTP component
import ProductSearch from "./ProductSearch";
import styles from "./UserStorage.css"; // Import CSS module
import FullScreenDialog from "./dialog/fullscreen-dialog";

function UserStorage({ separate, options }) {
  const { user } = useSelector((state) => state.auth);
  const { conversionFactors } = useSelector((state) => state.transfer);

  const [showDeposit, setShowDeposit] = useState(false);
  const [showWithdraw, setShowWithdraw] = useState(false);
  const [showOTP, setShowOTP] = useState(false);
  const [withdrawId, setWithdrawId] = useState(null); // State to store withdraw ID
  const [withdrawSuccess, setWithdrawSuccess] = useState(false);
  const [getPaymentUrl, { error: paymentError }] = useGetPaymentUrlMutation();
  const [paymentRequest, setPaymentRequest] = useState({
    price: 0,
    userInfoId: user?.userId,
    username: user?.username,
  });

  const [openDialog, setOpenDialog] = useState(false);

  const handleOpenDialog = () => {
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
  };

  const [requestWithdrawGold, { error: withdrawError }] =
    useRequestWithdrawGoldMutation();

  const [weightToWithdraw, setWeightToWithdraw] = useState(null);

  const [unit, setUnit] = useState("Mace");

  const tempPrice = paymentRequest.price / 24000;

  const navigate = useNavigate();

  const handleClose = () => {
    setShowDeposit(false);
    setShowWithdraw(false);
    setShowOTP(false); // Close OTP modal if open
  };

  const [value, selectValue] = useState("tOz");

  const [convertedWeight, setConvertedWeight] = useState(0);

  const [isLoading, setIsLoading] = useState(false);

  const [searchQuery, setSearchQuery] = useState("");

  const debouncedQuery = useDebounce(searchQuery);

  const { searchResults, searchLoading } = useSearchProducts(debouncedQuery);

  const [withdrawRequirement, setWithdrawRequirement] =
    React.useState("AVAILABLE");

  const [selectedProductId, setSelectedProductId] = useState(null);

  const handleChange = (event) => {
    setWithdrawRequirement(event.target.value);
  };

  const handleSearch = useCallback((inputValue) => {
    setSearchQuery(inputValue);
  }, []);

  const fetchSearchResults = () => {
    handleSearch(debouncedQuery); // Gọi hàm search khi dialog mở
  };

  //   const unitOptions = Object.keys(GOLD_UNIT_CONVERT).map(key => ({
  //   label: GOLD_UNIT_CONVERT[key],
  //   value: key
  // }));

  const unitOptions = Object.keys(conversionFactors).map((key) => ({
    label: conversionFactors[key].fromUnit || key, // Sử dụng label nếu có, nếu không thì dùng key
    value: key,
  }));

  const renderOption = (option) => {
    const convertedWeight = weightConverter(
      inventory?.totalWeightOz,
      "tOz",
      option.value,
      conversionFactors
    );
    return `${option.label}`;
  };

  const [selectedUnit, setSelectedUnit] = useState(unitOptions[0]);

  useEffect(() => {
    const newWeight = weightConverter(
      weightToWithdraw,
      selectedUnit?.value,
      "tOz",
      conversionFactors
    );
    setConvertedWeight(newWeight);
  }, [selectedUnit, weightToWithdraw]);

  if (!user) return null;
  const {
    userInfo: { inventory, balance },
  } = user;

  // Validation schema for deposit amount
  const depositSchema = Yup.object().shape({
    price: Yup.number()
      .typeError("Price must be a number")
      .min(10000, "Minimum quantity is 10,000")
      .max(100000000, "Maximum quantity is 100,000,000")
      .positive("The price must be positive")
      .required("Please enter price"),
  });

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      // Thực hiện xác thực trước khi đặt trạng thái tải là true
      await depositSchema.validate(
        { price: paymentRequest.price },
        { abortEarly: false }
      );

      setIsLoading(true);

      const result = await getPaymentUrl(paymentRequest).unwrap();
      document.location.href = result;
    } catch (error) {
      if (error instanceof Yup.ValidationError) {
        error.errors.forEach((errorMessage) => toast.error(errorMessage));
      } else {
        toast.error("Error: Can't get payment url");
      }
    } finally {
      setIsLoading(false);
    }
  };

  // Function to handle withdrawal submission
  const handleSubmitWithdraw = async (e) => {
    e.preventDefault();
    try {
      await withdrawSchema.validate(
        { weightToWithdraw },
        { abortEarly: false }
      );
      console.log(
        "Withdrawal request data:",
        weightToWithdraw,
        GOLD_UNIT_CONVERT[unit.toUpperCase()]
      );
      const response = await requestWithdrawGold({
        weightToWithdraw,
        unit: GOLD_UNIT_CONVERT[unit.toUpperCase()],
      }).unwrap();
    } catch (error) {
      if (error instanceof Yup.ValidationError) {
        error.errors.forEach((errorMessage) => toast.error(errorMessage));
      } else {
        toast.error(error.data.message);
      }
    }
  };

  const handleProductSelect = (productId) => {
    setSelectedProductId(productId);
  };

  const onSubmitWithdrawal = async () => {
    setIsLoading(true);
    const token = localStorage.getItem("token");
    console.log(token);

    let url = `${endpoints.withdraw.withdrawal_request}/${user?.userId}?unit=MACE&withdrawRequirement=${withdrawRequirement}&productId=${selectedProductId}`;
    if (withdrawRequirement !== "AVAILABLE") {
      url += `&weightToWithdraw=${weightToWithdraw}`;
    }

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
      setWithdrawId(res?.data?.data?.id); // Store the withdraw ID
      setWithdrawSuccess(true);
      setShowOTP(true);
      setIsLoading(false);
      return res?.data;
    } catch (error) {
      console.error("Failed to api call", error);
      setIsLoading(false);
    }
  };

  const handleOTPSubmit = async (otp) => {
    try {
      // Handle OTP submission logic here
      // If OTP is verified successfully, you can close the OTP modal
      // and show a success message
      console.log("OTP submitted:", otp);
      // For demo purpose, close the OTP modal and show success message
      setShowOTP(false);
      toast.success("OTP submitted successfully!");
    } catch (error) {
      console.error("Error submitting OTP:", error);
      toast.error("Error submitting OTP");
    }
  };
  const keys = Object.keys(conversionFactors);
  // keys?.sort((a, b) => a === value && -1);
  const minMace = 0;
  const maxMace = Math.floor(
    weightConverter(inventory?.totalWeightOz, "tOz", "Mace", conversionFactors)
  );
  let min = Math.ceil(
    weightConverter(minMace, "Mace", unit, conversionFactors)
  );
  const max = Math.floor(
    weightConverter(maxMace, "Mace", unit, conversionFactors)
  );
  if (min < 1 && max != 0) min = 1;
  // Validation schema for withdraw amount
  const withdrawSchema = Yup.object().shape({
    weightToWithdraw: Yup.number()
      .integer("withdraw amount must be a non decimal number")
      .max(maxMace, `Not enough gold to withdraw`)
      // .moreThan(0, `Invalid withdraw amount`)
      .positive("The quantity must be positive")
      .required("Please enter quantity"),
  });

  return (
    <>
      <p className="text d-flex flex-column ">
        <div className="position-relative">
          Balance: <b className=" gold ">{currencyFormat(balance?.amount)}</b>
        </div>

        <div className="position-relative">
          Inventory: <b>{inventory?.totalWeightOz + " TOZ"}</b>
        </div>
      </p>
      <div className="btn-group d-flex justify-content-center">
        <Link
          className="btn-secondary btn btn-sm ms-auto"
          style={{ fontSize: "0.7em" }}
          onClick={() => setShowDeposit(true)}
        >
          <FaDollarSign /> Deposit
        </Link>
        <Button
          className="btn-secondary btn btn-sm"
          style={{
            fontSize: "0.7em",
            // backgroundColor: "#ffa500 !important",
            backgroundColor: "#ffa500 !important", // Màu cam nhạt
            color: "#fff",
            margin: "0 auto", // Căn giữa theo chiều ngang
          }}
          // onClick={() => setShowWithdraw(true)}
          onClick={handleOpenDialog}
        >
          <FaCoins /> Withdraw Gold
        </Button>
      </div>
      <Modal show={showDeposit} onHide={handleClose}>
        <Modal.Header closeButton>
          <Modal.Title>Deposit</Modal.Title>
        </Modal.Header>
        <div className="modal-body">
          <div className="position-relative mb-3">
            Balance: <b className=" gold ">{currencyFormat(balance?.amount)}</b>
          </div>
          <Form id="deposit" onSubmit={handleSubmit}>
            <Form.Group>
              <FormLabel>Amount to Deposit</FormLabel>
              <InputGroup>
                <FormControl
                  name="price"
                  type="number"
                  min={10000}
                  value={paymentRequest?.price}
                  onChange={(e) =>
                    setPaymentRequest({
                      ...paymentRequest,
                      price: e.target.value,
                    })
                  }
                />
                <InputGroupText>VND</InputGroupText>
              </InputGroup>
              <Form.Label>Temp Price: {currencyFormat(tempPrice)} </Form.Label>
            </Form.Group>
          </Form>
        </div>
        <Modal.Footer>
          <LoadingButton
            variant="outlined"
            form="deposit"
            type="submit"
            loading={isLoading}
            sx={{
              backgroundColor: "orange",
              color: "white",
              "&:hover": {
                backgroundColor: "darkorange",
              },
            }}
          >
            Deposit
          </LoadingButton>
          <Button variant="secondary" onClick={handleClose}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
      {/* Withdraw */}

      <Modal
        show={showWithdraw}
        onHide={handleClose}
        className={styles.customModal} // Sử dụng class từ CSS module
      >
        {/* <div className="modal-dialog" style={{ marginLeft: '150px' }}> */}
        <div
          className={`modal-content ${styles.customModalContent}`} // Sử dụng class từ CSS module
        >
          <Modal.Header closeButton>
            <Modal.Title>Withdraw</Modal.Title>
          </Modal.Header>
          <div className="modal-body" style={{ overflowY: "auto" }}>
            <div className="position-relative mb-3">
              Inventory:{" "}
              <b className="me-1">{inventory?.totalWeightOz} TROY OUNCES</b>
              {/* <Autocomplete
              options={unitOptions}
              getOptionLabel={renderOption}
              renderInput={(params) => <TextField {...params} label="Select Unit" variant="outlined" />}
              value={selectedUnit}
              onChange={(event, newValue) => setSelectedUnit(newValue)}
              sx={{ width: 200, marginTop: 2 }}
              size="small"
            /> */}
              <InputLabel
                id="demo-simple-select-label"
                sx={{
                  marginTop: 2,
                }}
              >
                Withdraw Optional
              </InputLabel>
              <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                value={withdrawRequirement}
                label="Withdraw Optional"
                onChange={handleChange}
                sx={{
                  width: 200,
                }}
              >
                <MenuItem value={"AVAILABLE"}>AVAILABLE</MenuItem>
                <MenuItem value={"CRAFT"}>CRAFT</MenuItem>
              </Select>
            </div>

            <ProductSearch
              query={debouncedQuery}
              results={searchResults}
              onSearch={handleSearch}
              loading={searchLoading}
              onProductSelect={handleProductSelect}
              sx={{
                mb: "10px",
              }}
            />
            {withdrawRequirement === "CRAFT" && (
              <Form id="withdraw">
                <Form.Group>
                  <FormLabel>Amount to Withdraw</FormLabel>
                  <InputGroup className="m-0">
                    <FormControl
                      name="amount"
                      type="number"
                      onKeyDownCapture={(e) => {
                        e.key === "e" && e.preventDefault();
                        e.key === "." && e.preventDefault();
                        e.key === "," && e.preventDefault();
                      }}
                      onChange={(e) => setWeightToWithdraw(e.target.value)}
                    />
                    <select
                      className="input-group-text"
                      value={unit}
                      onChange={(e) => setUnit(e.target.value)}
                    >
                      <option value={"Mace"}>Mace</option>
                      {/* <option value={"Tael"}>Tael</option> */}
                    </select>
                  </InputGroup>
                  <div className="small text-end me-5">{`${min} ${unit} - ${max} ${unit}`}</div>
                </Form.Group>
              </Form>
            )}
          </div>
          <Modal.Footer>
            <LoadingButton
              type="submit"
              variant="contained"
              onClick={onSubmitWithdrawal}
              loading={isLoading}
              sx={{
                backgroundColor: "orange",
                color: "white",
                "&:hover": {
                  backgroundColor: "darkorange",
                },
              }}
            >
              Withdraw
            </LoadingButton>
            <Button variant="secondary" onClick={handleClose}>
              Close
            </Button>
          </Modal.Footer>
        </div>
        {/* </div> */}
      </Modal>

      <FullScreenDialog
        open={openDialog}
        onClose={handleCloseDialog}
        fetchSearchResults={fetchSearchResults}
      />

      {/* Modal OTP */}

      {showOTP && withdrawSuccess && (
        <OTPWithdrawPage
          withdrawId={withdrawId}
          onClose={handleClose}
          show={showOTP}
          onSubmit={handleOTPSubmit}
        />
      )}
    </>
  );
}

export default UserStorage;
