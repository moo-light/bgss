import React, { useEffect, useRef, useState } from "react";
import { Button, Form, FormControl, Modal, Spinner } from "react-bootstrap";
import toast from "react-hot-toast";
import { FaAngleLeft, FaCheck, FaSearch } from "react-icons/fa";
import Lottie from "react-lottie";
import { useDispatch, useSelector } from "react-redux";
import { Link, Navigate, useNavigate } from "react-router-dom";
import Swal from "sweetalert2";
import loadingAnimation from "../../Animation.json";
import "../../components/cart/OTP.css";
import { BASE_PRODUCTIMG } from "../../constants/constants";
import DiscountCard from "../../helpers/components/discount-card";
import { caluclateOrderCost, currencyFormat } from "../../helpers/helpers";
import { getServerImgUrl } from "../../helpers/image-handler";
import { useGetMyDiscountQuery } from "../../redux/api/discountApi";
import { useCreateNewOrderMutation } from "../../redux/api/orderApi";
import {
  useResendOTPMutation,
  useVerifyOrderMutation,
} from "../../redux/api/otpApi";
import Loader from "../layout/Loader";
import MetaData from "../layout/MetaData";
import CheckoutSteps from "./CheckoutSteps";

const ConfirmOrder = () => {
  const { cartItems } = useSelector((state) => state.cart);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { user } = useSelector((state) => state.auth);
  const inputRefs = useRef([]);
  const [verifyOTP, { isSuccess: isOTPVerified }] = useVerifyOrderMutation();
  const [createNewOrder, { isSuccess: orderCreated }] =
    useCreateNewOrderMutation();
  const [resendOTP] = useResendOTPMutation(); // Thêm hàm resendOTP

  const [orderData, setOrderData] = useState({
    listCartId: cartItems.map((c) => c.id),
    discountCode: null,
    isConsignment: false,
  });

  const [show, setShow] = useState(false);
  const [showBalanceAlert, setShowBalanceAlert] = useState(false);
  const [otpModalShow, setOtpModalShow] = useState(false);
  const [otp, setOtp] = useState([]);
  const [focusedIndex, setFocusedIndex] = useState(0);

  const [showLoadingAnimation, setShowLoadingAnimation] = useState(false); // Thêm state cho animation loading

  const { data: discountData, isLoading: discountLoading } =
    useGetMyDiscountQuery();
  const [currentDiscountCode, setDiscountCode] = useState("");
  const [isConsignmentChecked, setIsConsignmentChecked] = useState(false);

  const discounts = discountData?.data?.map((e) => e.discount) ?? [];
  const { itemsPrice, reducedPrice, taxPrice, totalPrice } = caluclateOrderCost(
    cartItems,
    orderData?.selectedDiscount
  );
  const selectedDiscount = discounts.find(
    (discount) => discount.code === currentDiscountCode
  );

  const { currentOrder } = useSelector((state) => state.order);
  const handleSaveDiscount = () => {
    if (selectedDiscount) {
      setOrderData({
        ...orderData,
        discountCode: selectedDiscount.code,
        // percentage: selectedDiscount.percentage,
        isConsignment: isConsignmentChecked,
        selectedDiscount,
      });

      setShow(false);
    }
  };
  useEffect(() => {
    handleSaveDiscount();
  }, [selectedDiscount]);

  if (currentOrder == null)
    if (cartItems == null || cartItems.length === 0)
      return <Navigate to={"/"} />;

  const handleToPayment = async () => {
    if (totalPrice > balance.amount) {
      setShowBalanceAlert(true);
    } else {
      // Hiển thị animation loading khi đang tạo đơn hàng
      setShowLoadingAnimation(true);

      // Kiểm tra xem đơn hàng đã được tạo thành công hay chưa
      if (orderCreated) {
        // Nếu đã tạo đơn hàng thành công, hiển thị modal nhập OTP
        setOtpModalShow(true);
        // Ẩn animation loading khi hiển thị modal OTP
        setShowLoadingAnimation(false);
      } else {
        try {
          // Nếu chưa tạo đơn hàng, thực hiện tạo đơn hàng trước
          await createNewOrder(orderData);
          // Ẩn animation loading khi đã tạo đơn hàng thành công
          setShowLoadingAnimation(false);
          // Sau đó hiển thị modal nhập OTP
          setOtpModalShow(true);
        } catch (error) {
          console.error("Error creating order:", error);
          // Ẩn animation loading nếu có lỗi khi tạo đơn hàng
          setShowLoadingAnimation(false);
        }
      }
    }
  };

  const handleOTPChange = (e, index) => {
    const newOTP = [...otp];
    newOTP[index] = e.target.value;
    setOtp(newOTP);

    // Kiểm tra nếu độ dài của ô hiện tại là 1 và không phải là ô cuối cùng
    if (e.target.value.length === 1 && index < 5) {
      // Tăng index lên 1 để chuyển qua ô tiếp theo
      inputRefs.current[index + 1].focus();
    }
  };

  const handleClearOTP = () => {
    setOtp([]);
    // Focus vào ô đầu tiên khi xoá hết OTP
    inputRefs.current[0].focus();
  };
  const handleCloseOtpModal = () => {
    setOtpModalShow(false);
    navigate("/me/orders"); // Chuyển hướng khi đóng modal OTP
  };
  const handleCloseDiscountModal = () => {
    setShow(false);
  };

  const handleConfirmOrder = async () => {
    if (currentOrder && currentOrder.id) {
      const otpRequest = Array.isArray(otp) ? otp.join("") : otp;
      if (otpRequest.length === 0) toast.error("please type your OTP!");
      const response = await verifyOTP({
        otp: otpRequest,
        orderId: currentOrder.id,
      });
      if (response?.data?.status === "OK") {
        // Chuyển hướng đến trang thanh toán nếu mã OTP đúng
        navigate("/me/orders");
      } else if (response.error?.status === "FETCH_ERROR") {
        toast.error("Falied to fetch");
      } else {
        // console.error("Error sending OTP:", response.error.data.message);
        Swal.fire({
          icon: "error",
          title: "Oops...",
          text: response.error?.data?.message,
        });
      }
    } else {
      // Xử lý khi currentOrder không tồn tại hoặc là null
      console.error("Error: currentOrder is null or does not have an id");
    }
  };

  // Hàm gửi lại OTP
  const handleResendOTP = async () => {
    try {
      // Gọi API gửi lại OTP
      await resendOTP({ orderId: currentOrder.id });
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

  if (!user) return null;
  const {
    userInfo: { balance },
  } = user;

  const handlePaste = (e) => {
    e.preventDefault();
    const pastedValue = e.clipboardData.getData("Text");
    setOtp(pastedValue);
    inputRefs.current[5].focus();
  };
  const clearDiscount = () => {
    setOrderData((prev) => {
      prev.discountCode = "";
      return prev;
    });
  };
  const handleShow = () => {
    setShow(true);
  };
  let cartItemCount = 0;
  if (cartItems.length > 0) {
    cartItemCount = cartItems
      ?.map((item) => item.quantity)
      ?.reduce((a, b) => a + b);
  }
  return (
    <>
      <MetaData title={"Confirm Order Info"} />
      <CheckoutSteps shipping confirmOrder />
      <div className="row d-flex justify-content-between">
        <div className="col-12 col-lg-8 mt-5 order-confirm">
          <h4 className="mb-3">Shipping Info</h4>
          <p>
            <b>Name:</b> {user?.name}
          </p>
          <p className="mb-3">
            <b>Email:</b> {user?.email}
          </p>
          <p className="mb-3">
            <b>Phone:</b> {user?.userInfo?.phoneNumber}
          </p>
          <p className="mb-3">
            <b>Address:</b> {user?.userInfo?.address}
          </p>
          <h4 className="mt-5">Your Cart Items: {cartItemCount}</h4>

          {cartItems?.map((item) => {
            const product = item.product;
            return (
              <div
                key={item}
                className="row my-3 border rounded p-2 row-gap-2  "
              >
                <div className="col-2 d-flex">
                  <img
                    className="img-fluid my-auto"
                    src={getServerImgUrl(
                      product?.productImages[0]?.imgUrl,
                      BASE_PRODUCTIMG
                    )}
                    alt={product?.productName}
                    style={{
                      backgroundColor: "var(--background-color)",
                    }}
                    height="45"
                    width="65"
                  />
                </div>
                <div className="col-4   d-flex align-items-center gap-2 ">
                  <Link to={`/product/${product.id}`} className="text-50">
                    {product?.productName}
                  </Link>
                </div>
                <div className="col-6 gap-2  align-content-center text-end m-auto mt-3  ">
                  <b>{currencyFormat(item?.price)} </b>
                  <span>
                    x {item?.quantity} Piece{item?.quantity > 0 && "(s)"}{" "}
                  </span>
                  <span className="fw-bold ">
                    ={" "}
                    <span className="orange">
                      {currencyFormat(item?.amount)}
                    </span>
                  </span>
                  <p style={{ fontWeight: "bold", marginLeft: "100px" }}>
                    {item?.processReceiveProduct}
                  </p>
                </div>
                <div className="col-3 col-lg-3 m-auto fw-bold orange d-flex justify-content-center  "></div>
              </div>
            );
          })}
        </div>

        <div className="col-12 col-lg-4 col-xl-3 my-4">
          <div id="order_summary">
            <div className="position-relative">
              Balance:{" "}
              <b className=" gold ">{currencyFormat(balance?.amount)}</b>
            </div>
            <hr />
            <h4>Order Summary</h4>
            <hr />
            <div className="form-check">
              <input
                className="form-check-input"
                type="checkbox"
                id="isConsignmentCheckbox"
                checked={orderData.isConsignment}
                onChange={(e) =>
                  setOrderData({
                    ...orderData,
                    isConsignment: e.target.checked,
                  })
                }
              />
              <label
                className="form-check-label"
                htmlFor="isConsignmentCheckbox"
                style={{ fontWeight: "bold" }}
              >
                Keep Gold in Store
              </label>
            </div>
            <hr />
            <p className="input-group d-flex align-items-center gap-2 justify-content-between  w-100">
              <label className="form-label m-0">Discount:</label>
              <button type="button" className="btn" onClick={handleShow}>
                <span className={`${orderData.discountCode && "fw-bold"}`}>
                  {orderData.discountCode || "Select Discount"}
                  <FaAngleLeft />
                </span>
              </button>
            </p>
            <hr />
            <p>
              Subtotal:{" "}
              <span className="order-summary-values">${itemsPrice}</span>
            </p>
            {/* <p>
              Tax: <span className="order-summary-values">${taxPrice}</span>
            </p> */}

            {orderData.discountCode && (
              <p className="input-group d-flex align-items-center gap-2 justify-content-between  w-100">
                <label className="form-label m-0">Discount:</label>
                <b className="ms-auto">
                  - $
                  {reducedPrice > selectedDiscount?.maxReduce
                    ? selectedDiscount.maxReduce
                    : reducedPrice}
                </b>
              </p>
            )}
            <hr />
            <div className="float-right small text-end my-2 text-50">
              Included tax if applicable
            </div>
            <p>
              Total: <span className="order-summary-values">${totalPrice}</span>
            </p>
            <hr />
            {showBalanceAlert && (
              <div className="alert alert-danger mt-3" role="alert">
                Total amount exceeds your balance. Please deposit some money.
              </div>
            )}
            <button
              href="/payment_method"
              id="checkout_btn"
              onClick={handleToPayment}
              className="btn btn-primary w-100"
              disabled={totalPrice > balance}
            >
              Confirm Order
            </button>
          </div>
        </div>
      </div>
      {/* Discount Modal */}
      <Modal show={show} onHide={handleCloseDiscountModal} size="md">
        <Modal.Header closeButton>
          <Modal.Title>Select Discount</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <DiscountScreen
            isLoading={discountLoading}
            discounts={discounts}
            subTotal={itemsPrice}
          />
        </Modal.Body>
        <Modal.Footer>
          <Button
            className="ms-auto my-3 d-flex"
            variant="danger"
            disabled={currentDiscountCode === ""}
            onClick={(e) => {
              setDiscountCode("");
              clearDiscount();
              handleCloseDiscountModal();
            }}
          >
            Remove Discount
          </Button>
          <Button variant="secondary" onClick={handleCloseDiscountModal}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
      {/* OTP Modal */}
      <Modal show={otpModalShow} onHide={handleCloseOtpModal}>
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
                maxLength="1"
                className="otp-input"
                value={otp[index] || ""}
                onPaste={handlePaste}
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

      {/* Hiển thị animation loading khi đang tạo đơn hàng */}
      {showLoadingAnimation && (
        <div
          className="loading-animation"
          style={{
            position: "fixed",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            zIndex: 9999, // Set high z-index to make sure it's on top
          }}
        >
          <Lottie
            options={{
              loop: true,
              autoplay: true,
              animationData: loadingAnimation,
            }}
            height={200} // Adjust the height and width as needed
            width={200}
          />
        </div>
      )}

      {/* Mờ đi nền phía sau animation */}
      <div
        style={{
          position: "fixed",
          top: 0,
          left: 0,
          width: "100%",
          height: "100%",
          backgroundColor: "rgba(0, 0, 0, 0.5)", // Màu đen với độ trong suốt 0.5
          zIndex: showLoadingAnimation ? 9998 : -1, // Sử dụng z-index để điều chỉnh hiển thị
          opacity: showLoadingAnimation ? 1 : 0, // Điều chỉnh độ trong suốt
          transition: "opacity 0.3s ease", // Thêm hiệu ứng mờ dần
        }}
      ></div>
    </>
  );
  function DiscountScreen({ isLoading, subTotal }) {
    const [input, setInput] = useState("");
    const searchedDiscounts =
      (input?.length ?? false) === 0
        ? discounts
        : discounts.filter((d) =>
            d.code.toLowerCase().includes(input.toLowerCase())
          );

    if (isLoading) return <Loader></Loader>;
    return (
      <>
        {discounts?.length > 0 ? (
          <Form>
            <Form.Group>
              <Form.Label>Search Discount </Form.Label>
              <FormControl
                name="discount"
                className="my-3"
                value={input}
                onChange={(e) => setInput(e.target.value)}
              />
            </Form.Group>

            {isLoading ? (
              <div className="col-1 d-inline-block mx-auto">
                <Spinner className="orange small"></Spinner>
              </div>
            ) : searchedDiscounts.length > 0 ? (
              <div className="overflow-y-scroll  " style={{ maxHeight: 500 }}>
                {searchedDiscounts.map((d) => {
                  const isQuantityInvalid = cartItemCount < d.quantityMin;
                  const isPriceMinInvalid = subTotal < d.minPrice;
                  let hintMessage = null;
                  if (isQuantityInvalid) {
                    hintMessage = `You must have at least ${d.quantityMin} items to apply this discount`;
                  }
                  if (isPriceMinInvalid) {
                    hintMessage = `You must buy at least ${currencyFormat(
                      d.minPrice
                    )} to apply this discount`;
                  }
                  if (isQuantityInvalid && isPriceMinInvalid) {
                    hintMessage = `You must have at least ${
                      d.quantityMin
                    } items and you must buy at least ${currencyFormat(
                      d.minPrice
                    )} to apply this discount`;
                  }
                  return (
                    <DiscountCard
                      discount={d}
                      isDisabled={
                        isQuantityInvalid ||
                        isPriceMinInvalid ||
                        d === selectedDiscount
                      }
                      isApplied={d === selectedDiscount}
                      applyMessage={
                        <>
                          Apply <FaCheck></FaCheck>
                        </>
                      }
                      hintMessage={hintMessage}
                      onApply={(e) => {
                        setDiscountCode((prev) => (prev = d.code));
                      }}
                    />
                  );
                })}
              </div>
            ) : (
              <div className="text-center">
                No discount found <FaSearch />
              </div>
            )}
          </Form>
        ) : (
          <p className="h3">User doesn't have any discount</p>
        )}
      </>
    );
  }
};
export default ConfirmOrder;

// import React, { useEffect, useRef, useState } from "react";
// import { Button, Form, FormControl, Modal, Spinner } from "react-bootstrap";
// import toast from "react-hot-toast";
// import { FaAngleLeft, FaCheck, FaSearch } from "react-icons/fa";
// import Lottie from "react-lottie";
// import { useDispatch, useSelector } from "react-redux";
// import { Link, Navigate, useNavigate } from "react-router-dom";
// import Swal from "sweetalert2";
// import loadingAnimation from "../../Animation.json";
// import "../../components/cart/OTP.css";
// import { BASE_PRODUCTIMG } from "../../constants/constants";
// import DiscountCard from "../../helpers/components/discount-card";
// import { caluclateOrderCost, currencyFormat } from "../../helpers/helpers";
// import { getServerImgUrl } from "../../helpers/image-handler";
// import { useGetMyDiscountQuery } from "../../redux/api/discountApi";
// import { useCreateNewOrderMutation } from "../../redux/api/orderApi";
// import {
//   useResendOTPMutation,
//   useVerifyOrderMutation,
// } from "../../redux/api/otpApi";
// import Loader from "../layout/Loader";
// import MetaData from "../layout/MetaData";
// import CheckoutSteps from "./CheckoutSteps";

// const ConfirmOrder = () => {
//   const { cartItems } = useSelector((state) => state.cart);
//   const navigate = useNavigate();
//   const dispatch = useDispatch();
//   const { user } = useSelector((state) => state.auth);
//   const inputRefs = useRef([]);
//   const [verifyOTP, { isSuccess: isOTPVerified }] = useVerifyOrderMutation();
//   const [createNewOrder, { isSuccess: orderCreated }] =
//     useCreateNewOrderMutation();
//   const [resendOTP] = useResendOTPMutation(); // Thêm hàm resendOTP

//   const [orderData, setOrderData] = useState({
//     listCartId: cartItems.map((c) => c.id),
//     discountCode: null,
//     isConsignment: false,
//   });

//   const [show, setShow] = useState(false);
//   const [showBalanceAlert, setShowBalanceAlert] = useState(false);
//   const [otpModalShow, setOtpModalShow] = useState(false);
//   const [otp, setOtp] = useState([]);
//   const [focusedIndex, setFocusedIndex] = useState(0);

//   const [showLoadingAnimation, setShowLoadingAnimation] = useState(false); // Thêm state cho animation loading

//   const { data: discountData, isLoading: discountLoading } =
//     useGetMyDiscountQuery();
//   const [currentDiscountCode, setDiscountCode] = useState("");
//   const [isConsignmentChecked, setIsConsignmentChecked] = useState(false);

//   const discounts = discountData?.data?.map((e) => e.discount) ?? [];
//   const { itemsPrice, reducedPrice, taxPrice, totalPrice } = caluclateOrderCost(
//     cartItems,
//     orderData?.percentage
//   );
//   const selectedDiscount = discounts.find(
//     (discount) => discount.code === currentDiscountCode
//   );

//   const { currentOrder } = useSelector((state) => state.order);
//   const handleSaveDiscount = () => {
//     if (selectedDiscount) {
//       setOrderData({
//         ...orderData,
//         discountCode: selectedDiscount.code,
//         percentage: selectedDiscount.percentage,
//         isConsignment: isConsignmentChecked,
//       });

//       setShow(false);
//     }
//   };
//   useEffect(() => {
//     handleSaveDiscount();
//   }, [selectedDiscount]);

//   if (currentOrder == null)
//     if (cartItems == null || cartItems.length === 0)
//       return <Navigate to={"/"} />;

//   const handleToPayment = async () => {
//     if (totalPrice > balance.amount) {
//       setShowBalanceAlert(true);
//     } else {
//       // Hiển thị animation loading khi đang tạo đơn hàng
//       setShowLoadingAnimation(true);

//       // Kiểm tra xem đơn hàng đã được tạo thành công hay chưa
//       if (orderCreated) {
//         // Nếu đã tạo đơn hàng thành công, hiển thị modal nhập OTP
//         setOtpModalShow(true);
//         // Ẩn animation loading khi hiển thị modal OTP
//         setShowLoadingAnimation(false);
//       } else {
//         try {
//           // Nếu chưa tạo đơn hàng, thực hiện tạo đơn hàng trước
//           await createNewOrder(orderData);
//           // Ẩn animation loading khi đã tạo đơn hàng thành công
//           setShowLoadingAnimation(false);
//           // Sau đó hiển thị modal nhập OTP
//           setOtpModalShow(true);
//         } catch (error) {
//           console.error("Error creating order:", error);
//           // Ẩn animation loading nếu có lỗi khi tạo đơn hàng
//           setShowLoadingAnimation(false);
//         }
//       }
//     }
//   };

//   const handleOTPChange = (e, index) => {
//     const newOTP = [...otp];
//     newOTP[index] = e.target.value;
//     setOtp(newOTP);

//     // Kiểm tra nếu độ dài của ô hiện tại là 1 và không phải là ô cuối cùng
//     if (e.target.value.length === 1 && index < 5) {
//       // Tăng index lên 1 để chuyển qua ô tiếp theo
//       inputRefs.current[index + 1].focus();
//     }
//   };

//   const handleClearOTP = () => {
//     setOtp([]);
//     // Focus vào ô đầu tiên khi xoá hết OTP
//     inputRefs.current[0].focus();
//   };
//   const handleClose = () => {
//     setShow(false);
//     setOtpModalShow(false);
//   };

//   const handleConfirmOrder = async () => {
//     if (currentOrder && currentOrder.id) {
//       const otpRequest = Array.isArray(otp) ? otp.join("") : otp;
//       if (otpRequest.length === 0) toast.error("please type your OTP!");
//       const response = await verifyOTP({
//         otp: otpRequest,
//         orderId: currentOrder.id,
//       });
//       if (response?.data?.status === "OK") {
//         // Chuyển hướng đến trang thanh toán nếu mã OTP đúng
//         navigate("/payment_method");
//       } else if (response.error?.status === "FETCH_ERROR") {
//         toast.error("Falied to fetch");
//       } else {
//         // console.error("Error sending OTP:", response.error.data.message);
//         Swal.fire({
//           icon: "error",
//           title: "Oops...",
//           text: response.error?.data?.message,
//         });
//       }
//     } else {
//       // Xử lý khi currentOrder không tồn tại hoặc là null
//       console.error("Error: currentOrder is null or does not have an id");
//     }
//   };

//   // Hàm gửi lại OTP
//   const handleResendOTP = async () => {
//     try {
//       // Gọi API gửi lại OTP
//       await resendOTP({ orderId: currentOrder.id });
//       Swal.fire({
//         icon: "success",
//         title: "OTP resent successfully",
//       });
//     } catch (error) {
//       console.error("Error resending OTP:", error);
//       Swal.fire({
//         icon: "error",
//         title: "Oops...",
//         text: "Failed to resend OTP. Please try again.",
//       });
//     }
//   };

//   if (!user) return null;
//   const {
//     userInfo: { balance },
//   } = user;

//   const handlePaste = (e) => {
//     e.preventDefault();
//     const pastedValue = e.clipboardData.getData("Text");
//     setOtp(pastedValue);
//     inputRefs.current[5].focus();
//   };
//   const clearDiscount = () => {
//     setOrderData((prev) => {
//       prev.discountCode = "";
//       return prev;
//     });
//   };
//   const handleShow = () => {
//     setShow(true);
//   };
//   let cartItemCount = 0;
//   if (cartItems.length > 0) {
//     cartItemCount = cartItems
//       ?.map((item) => item.quantity)
//       ?.reduce((a, b) => a + b);
//   }
//   return (
//     <>
//       <MetaData title={"Confirm Order Info"} />
//       <CheckoutSteps shipping confirmOrder />
//       <div className="row d-flex justify-content-between">
//         <div className="col-12 col-lg-8 mt-5 order-confirm">
//           <h4 className="mb-3">Shipping Info</h4>
//           <p>
//             <b>Name:</b> {user?.name}
//           </p>
//           <p className="mb-3">
//             <b>Email:</b> {user?.email}
//           </p>
//           <p className="mb-3">
//             <b>Phone:</b> {user?.userInfo?.phoneNumber}
//           </p>
//           <p className="mb-3">
//             <b>Address:</b> {user?.userInfo?.address}
//           </p>
//           <h4 className="mt-5">Your Cart Items: {cartItemCount}</h4>

//           {cartItems?.map((item) => {
//             const product = item.product;
//             return (
//               <div
//                 key={item}
//                 className="row my-3 border rounded p-2 row-gap-2  "
//               >
//                 <div className="col-2 d-flex">
//                   <img
//                     className="img-fluid my-auto"
//                     src={getServerImgUrl(
//                       product?.productImages[0]?.imgUrl,
//                       BASE_PRODUCTIMG
//                     )}
//                     alt={product?.productName}
//                     style={{
//                       backgroundColor: "var(--background-color)",
//                     }}
//                     height="45"
//                     width="65"
//                   />
//                 </div>
//                 <div className="col-4   d-flex align-items-center gap-2 ">
//                   <Link to={`/product/${product.id}`} className="text-50">
//                     {product?.productName}
//                   </Link>
//                 </div>
//                 <div className="col-6 gap-2  align-content-center text-end m-auto mt-3  ">
//                   <b>{currencyFormat(item?.price)} </b>
//                   <span>
//                     x {item?.quantity} Piece{item?.quantity > 0 && "(s)"}{" "}
//                   </span>
//                   <span className="fw-bold ">
//                     ={" "}
//                     <span className="orange">
//                       {currencyFormat(item?.amount)}
//                     </span>
//                   </span>
//                   <p style={{ fontWeight: "bold", marginLeft: "100px" }}>
//                     {item?.processReceiveProduct}
//                   </p>
//                 </div>
//                 <div className="col-3 col-lg-3 m-auto fw-bold orange d-flex justify-content-center  "></div>
//               </div>
//             );
//           })}
//         </div>

//         <div className="col-12 col-lg-4 col-xl-3 my-4">
//           <div id="order_summary">
//             <div className="position-relative">
//               Balance:{" "}
//               <b className=" gold ">{currencyFormat(balance?.amount)}</b>
//             </div>
//             <hr />
//             <h4>Order Summary</h4>
//             <hr />
//             <div className="form-check">
//               <input
//                 className="form-check-input"
//                 type="checkbox"
//                 id="isConsignmentCheckbox"
//                 checked={orderData.isConsignment}
//                 onChange={(e) =>
//                   setOrderData({
//                     ...orderData,
//                     isConsignment: e.target.checked,
//                   })
//                 }
//               />
//               <label
//                 className="form-check-label"
//                 htmlFor="isConsignmentCheckbox"
//                 style={{ fontWeight: "bold" }}
//               >
//                 Keep Gold in Store
//               </label>
//             </div>
//             <hr />
//             <p className="input-group d-flex align-items-center gap-2 justify-content-between  w-100">
//               <label className="form-label m-0">Discount:</label>
//               <button type="button" className="btn" onClick={handleShow}>
//                 <span className={`${orderData.discountCode && "fw-bold"}`}>
//                   {orderData.discountCode || "Select Discount"}
//                   <FaAngleLeft />
//                 </span>
//               </button>
//             </p>
//             <hr />
//             <p>
//               Subtotal:{" "}
//               <span className="order-summary-values">${itemsPrice}</span>
//             </p>
//             {/* <p>
//               Tax: <span className="order-summary-values">${taxPrice}</span>
//             </p> */}

//             {orderData.discountCode && (
//               <p className="input-group d-flex align-items-center gap-2 justify-content-between  w-100">
//                 <label className="form-label m-0">Discount:</label>
//                 <b className="ms-auto">
//                   - $
//                   {reducedPrice > selectedDiscount?.maxReduce
//                     ? selectedDiscount.maxReduce
//                     : reducedPrice}
//                 </b>
//               </p>
//             )}
//             <hr />
//             <div className="float-right small text-end my-2 text-50">
//               Included tax if applicable
//             </div>
//             <p>
//               Total: <span className="order-summary-values">${totalPrice}</span>
//             </p>
//             <hr />
//             {showBalanceAlert && (
//               <div className="alert alert-danger mt-3" role="alert">
//                 Total amount exceeds your balance. Please deposit some money.
//               </div>
//             )}
//             <button
//               href="/payment_method"
//               id="checkout_btn"
//               onClick={handleToPayment}
//               className="btn btn-primary w-100"
//               disabled={totalPrice > balance}
//             >
//               Confirm Order
//             </button>
//           </div>
//         </div>
//       </div>
//       {/* Discount Modal */}
//       <Modal show={show} onHide={handleClose} size="md">
//         <Modal.Header closeButton>
//           <Modal.Title>Select Discount</Modal.Title>
//         </Modal.Header>
//         <Modal.Body>
//           <DiscountScreen
//             isLoading={discountLoading}
//             discounts={discounts}
//             subTotal={itemsPrice}
//           />
//         </Modal.Body>
//         <Modal.Footer>
//           <Button
//             className="ms-auto my-3 d-flex"
//             variant="danger"
//             disabled={currentDiscountCode === ""}
//             onClick={(e) => {
//               setDiscountCode("");
//               clearDiscount();
//               handleClose();
//             }}
//           >
//             Remove Discount
//           </Button>
//           <Button variant="secondary" onClick={handleClose}>
//             Close
//           </Button>
//         </Modal.Footer>
//       </Modal>
//       {/* OTP Modal */}
//       <Modal show={otpModalShow} onHide={handleClose}>
//         <Modal.Header closeButton>
//           <Modal.Title>Enter OTP</Modal.Title>
//         </Modal.Header>
//         <Modal.Body>
//           <p style={{ marginBottom: "30px" }}>
//             Please check your email to receive the OTP code
//           </p>
//           <div className="d-flex justify-content-between">
//             {[...Array(6)].map((_, index) => (
//               <input
//                 ref={(el) => (inputRefs.current[index] = el)}
//                 key={index}
//                 type="text"
//                 maxLength="1"
//                 className="otp-input"
//                 value={otp[index] || ""}
//                 onPaste={handlePaste}
//                 onChange={(e) => handleOTPChange(e, index)}
//                 onFocus={(e) => e.target.select()}
//               />
//             ))}
//           </div>
//         </Modal.Body>
//         <Modal.Footer>
//           <Button variant="danger" onClick={handleClearOTP}>
//             Clear
//           </Button>
//           <Button variant="info" onClick={handleResendOTP}>
//             Resend OTP
//           </Button>{" "}
//           <Button
//             variant="primary"
//             onClick={handleConfirmOrder}
//             disabled={otp.length === 0}
//           >
//             Confirm OTP
//           </Button>
//         </Modal.Footer>
//       </Modal>

//       {/* Hiển thị animation loading khi đang tạo đơn hàng */}
//       {showLoadingAnimation && (
//         <div
//           className="loading-animation"
//           style={{
//             position: "fixed",
//             top: "50%",
//             left: "50%",
//             transform: "translate(-50%, -50%)",
//             zIndex: 9999, // Set high z-index to make sure it's on top
//           }}
//         >
//           <Lottie
//             options={{
//               loop: true,
//               autoplay: true,
//               animationData: loadingAnimation,
//             }}
//             height={200} // Adjust the height and width as needed
//             width={200}
//           />
//         </div>
//       )}

//       {/* Mờ đi nền phía sau animation */}
//       <div
//         style={{
//           position: "fixed",
//           top: 0,
//           left: 0,
//           width: "100%",
//           height: "100%",
//           backgroundColor: "rgba(0, 0, 0, 0.5)", // Màu đen với độ trong suốt 0.5
//           zIndex: showLoadingAnimation ? 9998 : -1, // Sử dụng z-index để điều chỉnh hiển thị
//           opacity: showLoadingAnimation ? 1 : 0, // Điều chỉnh độ trong suốt
//           transition: "opacity 0.3s ease", // Thêm hiệu ứng mờ dần
//         }}
//       ></div>
//     </>
//   );
//   function DiscountScreen({ isLoading, subTotal }) {
//     const [input, setInput] = useState("");
//     const searchedDiscounts =
//       (input?.length ?? false) === 0
//         ? discounts
//         : discounts.filter((d) =>
//             d.code.toLowerCase().includes(input.toLowerCase())
//           );

//     if (isLoading) return <Loader></Loader>;
//     return (
//       <>
//         {discounts?.length > 0 ? (
//           <Form>
//             <Form.Group>
//               <Form.Label>Search Discount </Form.Label>
//               <FormControl
//                 name="discount"
//                 className="my-3"
//                 value={input}
//                 onChange={(e) => setInput(e.target.value)}
//               />
//             </Form.Group>

//             {isLoading ? (
//               <div className="col-1 d-inline-block mx-auto">
//                 <Spinner className="orange small"></Spinner>
//               </div>
//             ) : searchedDiscounts.length > 0 ? (
//               <div className="overflow-y-scroll  " style={{ maxHeight: 500 }}>
//                 {searchedDiscounts.map((d) => {
//                   const isQuantityInvalid = cartItemCount < d.quantityMin;
//                   const isPriceMinInvalid = subTotal < d.minPrice;
//                   let hintMessage = null;
//                   if (isQuantityInvalid) {
//                     hintMessage = `You must have at least ${d.quantityMin} items to apply this discount`;
//                   }
//                   if (isPriceMinInvalid) {
//                     hintMessage = `You must buy at least ${currencyFormat(
//                       d.minPrice
//                     )} to apply this discount`;
//                   }
//                   if (isQuantityInvalid && isPriceMinInvalid) {
//                     hintMessage = `You must have at least ${
//                       d.quantityMin
//                     } items and you must buy at least ${currencyFormat(
//                       d.minPrice
//                     )} to apply this discount`;
//                   }
//                   return (
//                     <DiscountCard
//                       discount={d}
//                       isDisabled={
//                         isQuantityInvalid ||
//                         isPriceMinInvalid ||
//                         d === selectedDiscount
//                       }
//                       isApplied={d === selectedDiscount}
//                       applyMessage={
//                         <>
//                           Apply <FaCheck></FaCheck>
//                         </>
//                       }
//                       hintMessage={hintMessage}
//                       onApply={(e) => {
//                         setDiscountCode((prev) => (prev = d.code));
//                       }}
//                     />
//                   );
//                 })}
//               </div>
//             ) : (
//               <div className="text-center">
//                 No discount found <FaSearch />
//               </div>
//             )}
//           </Form>
//         ) : (
//           <p className="h3">User doesn't have any discount</p>
//         )}
//       </>
//     );
//   }
// };
// export default ConfirmOrder;
