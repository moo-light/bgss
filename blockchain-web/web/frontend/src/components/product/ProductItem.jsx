import {
  DownloadOutlined,
  RotateLeftOutlined,
  RotateRightOutlined,
  SwapOutlined,
  ZoomInOutlined,
  ZoomOutOutlined,
} from "@ant-design/icons";
import { Image, Space } from "antd";
import DOMPurify from "dompurify";
import { default as React } from "react";
import { Link } from "react-router-dom";
import StarRatings from "react-star-ratings";
import { formatPostHTMLWithDiscount } from "../../helpers/helpers";
import "./ProductItem.css";

const ProductItem = ({ data, columnSize }) => {
  const product = {
    _id: data?.id ?? "",
    name: data?.productName ?? "",
    image: data?.imgUrl ?? null,
    description: data?.description ?? "",
    price: data?.priceProduct ?? 0,
    avgReview: data?.avgReview ?? 0,
    numOfReviews: data?.numOfReviews ?? 0,
    ...data,
  };
  const src = product.image || "/images/default_product.png";

  const onDownload = () => {
    fetch(src)
      .then((response) => response.blob())
      .then((blob) => {
        const url = URL.createObjectURL(new Blob([blob]));
        const link = document.createElement("a");
        link.href = url;
        link.download = "image.png";
        document.body.appendChild(link);
        link.click();
        URL.revokeObjectURL(url);
        link.remove();
      });
  };

  // Làm sạch mô tả bằng cách loại bỏ các thẻ <p>
  const cleanDescription = (description) => {
    // Sử dụng DOMPurify để làm sạch HTML
    const sanitizedHtml = DOMPurify.sanitize(description);
    // Loại bỏ các thẻ <p> bằng regex
    const cleanedDescription = sanitizedHtml.replace(/<\/?p>/g, "");
    return cleanedDescription;
  };

  return (
    <div
      key={product._id}
      className={`col-sm-12 col-md-6 col-lg-${columnSize} my-3`}
    >
      <div className="card p-3 rounded">
        <div className="product-image-container">
          {product?.percentageReduce && (
            <div className="discount-badge-container">
              <div>{product?.percentageReduce}%</div>
            </div>
          )}
          <Image
            style={{
              objectFit: "cover",
              boxShadow: "0 0 4px 2px var(--border-color)",
              borderRadius: "8px",
            }}
            className="card-img-top mx-auto img-fluid w-100 h-100"
            src={src}
            preview={{
              toolbarRender: (
                _,
                {
                  transform: { scale },
                  actions: {
                    onFlipY,
                    onFlipX,
                    onRotateLeft,
                    onRotateRight,
                    onZoomOut,
                    onZoomIn,
                  },
                }
              ) => (
                <Space size={12} className="toolbar-wrapper">
                  <DownloadOutlined
                    style={{ fontSize: "30px" }}
                    onClick={onDownload}
                  />
                  <SwapOutlined
                    rotate={90}
                    style={{ fontSize: "30px" }}
                    onClick={onFlipY}
                  />
                  <SwapOutlined
                    style={{ fontSize: "30px" }}
                    onClick={onFlipX}
                  />
                  <RotateLeftOutlined
                    style={{ fontSize: "30px" }}
                    onClick={onRotateLeft}
                  />
                  <RotateRightOutlined
                    style={{ fontSize: "30px" }}
                    onClick={onRotateRight}
                  />
                  <ZoomOutOutlined
                    disabled={scale === 1}
                    style={{ fontSize: "30px" }}
                    onClick={onZoomOut}
                  />
                  <ZoomInOutlined
                    disabled={scale === 50}
                    style={{ fontSize: "30px" }}
                    onClick={onZoomIn}
                  />
                </Space>
              ),
            }}
          />
        </div>
        <div className="card-body ps-3 d-flex justify-content-center flex-column">
          <h5 className="card-title">
            <Link to={`/product/${product._id}`}>{product.name}</Link>
          </h5>
          {product?.percentageReduce ? (
            <>
              <div className="text-decoration-line-through text-secondary">
                <span style={{ fontWeight: "bold" }}>
                  ${product?.priceProduct}
                </span>
              </div>
              <div className="mb-2">
                <span
                  style={{ color: "var(--primary-color)", fontWeight: "bold" }}
                >
                  ${product?.secondPrice}
                </span>
              </div>
            </>
          ) : (
            <p>
              <span style={{ color: "orange", fontWeight: "bold" }}>
                ${product?.priceProduct}
              </span>
            </p>
          )}
          <div className="ratings d-flex">
            <StarRatings
              rating={product.avgReview}
              starRatedColor="#ffb829"
              numberOfStars={5}
              name="rating"
              starDimension="16px"
              starSpacing="1px"
            />
            <span id="no_of_reviews" className="pt-2 ps-2">
              ({product.avgReview.toFixed(1)})
            </span>
          </div>
          <div
            className="card-description mb-auto pt-2"
            dangerouslySetInnerHTML={{
              __html: DOMPurify.sanitize(
                formatPostHTMLWithDiscount(product.description)
              ),
            }}
          />
          <Link
            to={`/product/${product._id}`}
            id="view_btn"
            className="btn btn-block mt-2"
          >
            View Details
          </Link>
        </div>
      </div>
    </div>
  );
};

export default ProductItem;

// import {
//   DownloadOutlined,
//   RotateLeftOutlined,
//   RotateRightOutlined,
//   SwapOutlined,
//   ZoomInOutlined,
//   ZoomOutOutlined,
// } from "@ant-design/icons";
// import { Image, Space } from "antd";
// import React from "react";
// import { Link } from "react-router-dom";
// import StarRatings from "react-star-ratings";
// import { formatHTMLToText } from "../../helpers/helpers";

// const ProductItem = ({ data, columnSize }) => {
//   const product = {
//     _id: data?.id ?? "",
//     name: data?.productName ?? "",
//     image: data?.imgUrl ?? null,
//     description: data?.description ?? "",
//     price: data?.price ?? 0,
//     avgReview: data?.avgReview ?? 0,
//     numOfReviews: data.numOfReviews ?? 0.0,
//     ...data,
//   };
//   const src = product.image || "/images/default_product.png";

//   const onDownload = () => {
//     fetch(src)
//       .then((response) => response.blob())
//       .then((blob) => {
//         const url = URL.createObjectURL(new Blob([blob]));
//         const link = document.createElement("a");
//         link.href = url;
//         link.download = "image.png";
//         document.body.appendChild(link);
//         link.click();
//         URL.revokeObjectURL(url);
//         link.remove();
//       });
//   };

//   return (
//     <div
//       key={product._id}
//       className={`col-sm-12 col-md-6 col-lg-${columnSize} my-3`}
//     >
//       <div className="card p-3 rounded">
//         <Image
//           style={{
//             objectFit: "cover",
//             boxShadow: "0 0 4px 2px var(--border-color)",
//             borderRadius: "8px",
//           }}
//           className="card-img-top mx-auto"
//           src={src}
//           preview={{
//             toolbarRender: (
//               _,
//               {
//                 transform: { scale },
//                 actions: {
//                   onFlipY,
//                   onFlipX,
//                   onRotateLeft,
//                   onRotateRight,
//                   onZoomOut,
//                   onZoomIn,
//                 },
//               }
//             ) => (
//               <Space size={12} className="toolbar-wrapper">
//                 <DownloadOutlined
//                   style={{ fontSize: "30px" }}
//                   onClick={onDownload}
//                 />
//                 <SwapOutlined
//                   rotate={90}
//                   style={{ fontSize: "30px" }}
//                   onClick={onFlipY}
//                 />
//                 <SwapOutlined style={{ fontSize: "30px" }} onClick={onFlipX} />
//                 <RotateLeftOutlined
//                   style={{ fontSize: "30px" }}
//                   onClick={onRotateLeft}
//                 />
//                 <RotateRightOutlined
//                   style={{ fontSize: "30px" }}
//                   onClick={onRotateRight}
//                 />
//                 <ZoomOutOutlined
//                   disabled={scale === 1}
//                   style={{ fontSize: "30px" }}
//                   onClick={onZoomOut}
//                 />
//                 <ZoomInOutlined
//                   disabled={scale === 50}
//                   style={{ fontSize: "30px" }}
//                   onClick={onZoomIn}
//                 />
//               </Space>
//             ),
//           }}
//         />
//         <div className="card-body ps-3 d-flex justify-content-center flex-column">
//           <h5 className="card-title">
//             <Link to={`/product/${product._id}`}>{product.name}</Link>
//           </h5>
//           <p
//             className="card-text mb-0"
//             style={{
//               color: "var(--primary-color)",
//               fontWeight: "bold",
//             }}
//           >
//             ${product.price}
//           </p>
//           <div className="ratings d-flex">
//             <StarRatings
//               rating={product.avgReview}
//               starRatedColor="#ffb829"
//               numberOfStars={5}
//               name="rating"
//               starDimension="16px"
//               starSpacing="1px"
//             />
//             <span id="no_of_reviews" className="pt-2 ps-2">
//               ({product.avgReview.toFixed(1)})
//             </span>
//           </div>
//           <div className="card-description mb-auto pt-2">
//             {formatHTMLToText(product.description)}
//           </div>
//           <Link
//             to={`/product/${product._id}`}
//             id="view_btn"
//             className="btn btn-block mt-2"
//           >
//             View Details
//           </Link>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default ProductItem;

// // import { Link } from "react-router-dom";

// // import StarRatings from "react-star-ratings";
// // import { createPhotoURL } from "../../helpers/image-handler";
// // const ProductItem = ({ data, columnSize }) => {
// //   const product = {
// //     _id: data?.id ?? "",
// //     name: data?.productName ?? "",
// //     image: data?.productImages ?? null,
// //     description: data?.description ?? "",
// //     price: data?.price ?? 0,
// //     unitOfStock: data?.unitOfStock ?? 0,
// //     numOfReviews: data.numOfReviews ?? 0.0,
// //   };
// // console.log(data?.productImages);
// //   return (
// //     <div className={`col-sm-12 col-md-6 col-lg-${columnSize} my-3`}>
// //       <div className="card p-3 rounded">
// //         <img
// //           style={{ objectFit: "cover" }}
// //           className="card-img-top mx-auto"
// //           src={
// //             product?.image?.length > 0
// //               ? createPhotoURL(product?.image[0])
// //               : "/images/default_product.png"
// //           }
// //           alt={product?.name}
// //         />
// //         <div className="card-body ps-3 d-flex justify-content-center flex-column">
// //           <h5 className="card-title">
// //             <Link to={`/product/${product?._id}`}>{product?.name}</Link>
// //           </h5>
// //           <p
// //             className="card-text mb-0"
// //             style={{
// //               color: "#ffa45b",
// //               fontWeight: "bold",
// //             }}
// //           >
// //             ${product?.price}
// //           </p>
// //           <div className="ratings d-flex">
// //             <StarRatings
// //               rating={product?.ratings}
// //               starRatedColor="#ffb829"
// //               numberOfStars={5}
// //               name="rating"
// //               starDimension="16px"
// //               starSpacing="1px"
// //             />
// //             <span id="no_of_reviews" className="pt-2 ps-2">
// //               ({product?.numOfReviews})
// //             </span>
// //           </div>
// //           <div className="card-description mb-auto pt-2">
// //             {product?.description}
// //           </div>
// //           <Link
// //             to={`/product/${product?._id}`}
// //             id="view_btn"
// //             className="btn btn-block mt-2"
// //           >
// //             View Details
// //           </Link>
// //         </div>
// //       </div>
// //     </div>
// //   );
// // };

// // export default ProductItem;
