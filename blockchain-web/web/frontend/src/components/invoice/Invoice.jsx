import html2canvas from "html2canvas";
import { jsPDF } from "jspdf";
import React, { useEffect } from "react";
import { toast } from "react-hot-toast";
import { Link, useParams } from "react-router-dom";
import { BASE_PRODUCTIMG } from "../../constants/constants";
import {
  currencyFormat,
  myDateFormat,
  phoneFormat,
} from "../../helpers/helpers";
import { getServerImgUrl } from "../../helpers/image-handler";
import { useOrderDetailsQuery } from "../../redux/api/orderApi";
import Loader from "../layout/Loader";
import MetaData from "../layout/MetaData";
import "./invoice.css";
import { BASE_HOST } from "../../constants/constants";

const Invoice = () => {
  const params = useParams();
  const HOST = BASE_HOST;
  const { data, isLoading, error } = useOrderDetailsQuery(params.id);
  const order = data?.data || {};
  const {
    orderDetails,
    status: paymentStatus,
    productType,
    statusReceived,
    totalAmount,
    qrCode,
  } = order;
  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }
  }, [error]);
  // console.log("Order data:", order);

  // const handleDownload = () => {
  //   const input = document.getElementById("order_invoice");
  //   html2canvas(input).then((canvas) => {
  //     const imgData = canvas.toDataURL("image/png");
  //     const pdf = new jsPDF();
  //     const pdfWidth = pdf.internal.pageSize.getWidth();
  //     pdf.addImage(imgData, "PNG", 0, 0, pdfWidth, 0);
  //     pdf.save(`invoice_${order?.id}.pdf`);
  //   });
  // };
  const handleDownload = () => {
    const input = document.getElementById("order_invoice");
    html2canvas(input, { useCORS: true }).then((canvas) => {
      const imgData = canvas.toDataURL("image/png");
      const pdf = new jsPDF();
      const pdfWidth = pdf.internal.pageSize.getWidth();
      const pdfHeight = (canvas.height * pdfWidth) / canvas.width;
      pdf.addImage(imgData, "PNG", 0, 0, pdfWidth, pdfHeight);
      pdf.save(`BGSS_Order_${order?.id}.pdf`);
    });
  };
  const isPaid = ((value) => {
    switch (value) {
      case "PAID":
        return "greenColor";
      case "CANCELLED":
        return "redColor";
        case "NOT_PAID": 
        return "redColor";
        case "COMPLETE": 
        return "greenColor";
      default:
        return "yellowColor";
    }
  })(paymentStatus);



  const isPaidStatus = ((value) => {
    switch (value) {
      case "PAID":
        return "greenColor";
        case "NOT_PAID": 
        return "redColor";
      default:
        return "yellowColor";
    }
  })(order?.paymentStatus);

  if (isLoading) return <Loader />;

  return (
    <div>
      <MetaData title={"Order Invoice"} />
      <div className="order-invoice my-5">
        <div className="row d-flex justify-content-center mb-5">
          <button className="btn btn-success col-md-5" onClick={handleDownload}>
            <i className="fa fa-print"></i> Download Invoice
          </button>
        </div>
        <div id="order_invoice" className="p-3 border border-secondary">
          <header className="clearfix">
            <div id="logo">
              <img src="/images/BGSS_logo_large.png" alt="Company Logo" />
            </div>
            {/* Thêm tiêu đề và thông tin đơn hàng */}
            <h1>INVOICE # {order?.id}</h1>
            <div className="container">
              <h3 className="mb-4 mt-2 ml-3">Custommer's Info</h3>
              <div className="row justify-content-center">
                <div className="col-md-8">
                  <div className="card">
                    <div className="card-header bg-primary text-white">
                      Order Information
                    </div>
                    <div className="card-body">
                      <div className="table-responsive">
                        <table className="table table-bordered">
                          <tbody>
                            <tr>
                              <th scope="row" className="text-start">
                                Name
                              </th>
                              <td>
                                {order?.firstName + " " + order?.lastName}
                              </td>
                            </tr>
                            <tr>
                              <th scope="row" className="text-start">
                                Email
                              </th>
                              <td>{order?.email}</td>
                            </tr>
                            <tr>
                              <th scope="row" className="text-start">
                                Phone No
                              </th>
                              <td>{phoneFormat(order?.phoneNumber)}</td>
                            </tr>
                            <tr>
                              <th scope="row" className="text-start">
                                Address
                              </th>
                              <td>{order?.address}</td>
                            </tr>
                            <tr>
                              <th scope="row" className="text-start">
                                Created Date
                              </th>
                              <td>{myDateFormat(order?.createDate)}</td>
                            </tr>
                          </tbody>
                        </table>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div className="container">
              <h3 className="mt-5 mb-4">Payment Info</h3>
              <table className="table table-striped table-bordered ">
                <tbody>
                  <tr>
                    <th scope="row">Status</th>
                    <td className={isPaid}>
                      <b>{statusReceived}</b>
                    </td>
                  </tr>
                  <tr>
                    <th scope="row">Discount</th>
                    <td>{(order?.percentageDiscount ?? 0) + " %"}</td>
                  </tr>
                  <tr>
                    <th scope="row">Amount</th>
                    <td>{currencyFormat(totalAmount)}</td>
                  </tr>
                  <tr>
                    <th scope="row">Payment Status</th>
                    <td className={isPaidStatus}>
                      <b>{order?.paymentStatus}</b>
                      </td>
                  </tr>
                </tbody>
              </table>
            </div>
          </header>
          <main>
            <h3 className="mt-5 my-4">Order Items:</h3>
            <hr />
            <div className="cart-item my-1">
              {orderDetails?.map((item) => {
                const product = item.product;
                console.log(product?.productImages?.[0]?.imgUrl);
                return (
                  <div className="row my-3 border rounded p-2 align-items-center">
                    <div className="col-2 d-flex">
                      <img
                        className="img-fluid my-auto"
                        src={getServerImgUrl(
                          product?.productImages?.[0]?.imgUrl,
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
                    <div className="col-4 d-flex align-items-center">
                      <div>
                        <Link to={`/product/${product.id}`}>
                          {product?.productName}
                        </Link>
                      </div>
                    </div>
                    <div className="col-6 gap-2 d-flex align-content-center m-auto justify-content-end">
                      <b>{currencyFormat(item?.price)} </b>
                      <span>
                        x {item?.quantity} Piece{item?.quantity > 0 && "(s)"}{" "}
                      </span>
                      <span className="fw-bold ">
                        =
                        <span className="orange">
                          {" "}
                          {currencyFormat(item?.amount)}
                        </span>
                      </span>
                        <p style={{ 
                          fontWeight: "bold", 
                          color: item?.processReceiveProduct === "COMPLETE" ? "green" : "yellow" 
                        }}>
                          {item?.processReceiveProduct}
                        </p>

                    </div>
                  </div>
                );
              })}
            </div>
            <div className="row mt-4 signature-container">
              <div className="col-md-6">
                <div className="border border-dark p-3 text-center">
                  <h4 className="mb-4">Customer's Signature</h4>
                  <div className="signature-box">
                    {/* Chữ kí của khách hàng */}
                  </div>
                  <div className="signature-info">
                    <p className="mt-3">
                      <b>Signature:</b> ___________________
                    </p>
                    <p>
                      <b>Date:</b> ___________________
                    </p>
                  </div>
                </div>
              </div>
              <div className="col-md-6">
                <div className="border border-dark p-3 text-center">
                  <h4 className="mb-4">Company's Stamp</h4>
                  <div className="stamp-box">
                    {/* Con dấu của công ty */}
                    <img
                      src="/images/BGSS110.png"
                      alt="Company Stamp"
                      className="stamp-img"
                    />
                  </div>
                  <div className="stamp-info">
                    <p className="mt-3">
                      <b>Signature:</b> ___________________
                    </p>
                    <p>
                      <b>Date:</b> ___________________
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* <p className="mt-4">
              Invoice was created on a computer and is valid without the
              signature.
            </p> */}
          </main>
        </div>
      </div>
    </div>
  );
};

export default Invoice;



// import React, { useEffect } from "react";
// import "./invoice.css";

// import html2canvas from "html2canvas";
// import { jsPDF } from "jspdf";
// import { toast } from "react-hot-toast";
// import { useParams } from "react-router-dom";
// import { myDateFormat, phoneFormat } from "../../helpers/helpers";
// import { useOrderDetailsQuery } from "../../redux/api/orderApi";
// import Loader from "../layout/Loader";
// import MetaData from "../layout/MetaData";

// const Invoice = () => {
//   const params = useParams();
//   const { data, isLoading, error } = useOrderDetailsQuery(params?.id);
//   const order = data?.order || {};

//   const { shippingInfo, orderItems, paymentInfo, user } = order;

//   useEffect(() => {
//     if (error) {
//       toast.error(error?.data?.message);
//     }
//   }, [error]);
//   console.log(data?.order);
//   const handleDownload = () => {
//     const input = document.getElementById("order_invoice");
//     html2canvas(input).then((canvas) => {
//       const imgData = canvas.toDataURL("image/png");

//       const pdf = new jsPDF();

//       const pdfWidth = pdf.internal.pageSize.getWidth();
//       pdf.addImage(imgData, "PNG", 0, 0, pdfWidth, 0);
//       pdf.save(`invoice_${order?._id}.pdf`);
//     });
//   };

//   if (isLoading) return <Loader />;

//   return (
//     <div>
//       <MetaData title={"Order Invoice"} />
//       <div className="order-invoice my-5">
//         <div className="row d-flex justify-content-center mb-5">
//           <button className="btn btn-success col-md-5" onClick={handleDownload}>
//             <i className="fa fa-print"></i> Download Invoice
//           </button>
//         </div>
//         <div id="order_invoice" className="p-3 border border-secondary">
//           <header className="clearfix">
//             <div id="logo">
//               <img src="/images/BGSS_logo_large.png" alt="Company Logo" />
//             </div>
//             <h1>INVOICE # {order?._id}</h1>
//             <div id="company" className="clearfix">
//               <div>ShopIT</div>
//               <div>
//                 455 Foggy Heights,
//                 <br />
//                 AZ 85004, US
//               </div>
//               <div>(602) 519-0450</div>
//               <div>
//                 <a href="mailto:info@shopit.com">info@shopit.com</a>
//               </div>
//             </div>
//             <div id="project">
//               <div>
//                 <span>Name</span> {user?.name}
//               </div>
//               <div>
//                 <span>EMAIL</span> {user?.email}
//               </div>
//               <div>
//                 <span>PHONE</span> {phoneFormat(shippingInfo?.phoneNo)}
//               </div>
//               <div>
//                 <span>ADDRESS</span>
//                 {shippingInfo?.address}, {shippingInfo?.city},{" "}
//                 {shippingInfo?.zipCode}, {shippingInfo?.country}
//               </div>
//               <div>
//                 <span>DATE</span> {myDateFormat(order?.createdAt)}
//               </div>
//               <div>
//                 <span>Status</span> {paymentInfo?.status}
//               </div>
//             </div>
//           </header>
//           <main>
//             <table className="mt-5">
//               <thead>
//                 <tr>
//                   <th className="service">ID</th>
//                   <th className="desc">NAME</th>
//                   <th>PRICE</th>
//                   <th>QTY</th>
//                   <th>TOTAL</th>
//                 </tr>
//               </thead>
//               <tbody>
//                 {orderItems?.map((item) => (
//                   <tr>
//                     <td className="service">{item?.product}</td>
//                     <td className="desc">{item?.name}</td>
//                     <td className="unit">${item?.price}</td>
//                     <td className="qty">{item?.quantity}</td>
//                     <td className="total">${item?.price * item?.quantity}</td>
//                   </tr>
//                 ))}

//                 <tr>
//                   <td colspan="4">
//                     <b>SUBTOTAL</b>
//                   </td>
//                   <td className="total">${order?.itemsPrice}</td>
//                 </tr>

//                 <tr>
//                   <td colspan="4">
//                     <b>TAX 15%</b>
//                   </td>
//                   <td className="total">${order?.taxAmount}</td>
//                 </tr>

//                 <tr>
//                   <td colspan="4">
//                     <b>SHIPPING</b>
//                   </td>
//                   <td className="total">${order?.shippingAmount}</td>
//                 </tr>

//                 <tr>
//                   <td colspan="4" className="grand total">
//                     <b>GRAND TOTAL</b>
//                   </td>
//                   <td className="grand total">${order?.totalAmount}</td>
//                 </tr>
//               </tbody>
//             </table>
//             <div id="notices">
//               <div>NOTICE:</div>
//               <div className="notice">
//                 A finance charge of 1.5% will be made on unpaid balances after
//                 30 days.
//               </div>
//             </div>
//           </main>
//           <footer>
//             Invoice was created on a computer and is valid without the
//             signature.
//           </footer>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default Invoice;
