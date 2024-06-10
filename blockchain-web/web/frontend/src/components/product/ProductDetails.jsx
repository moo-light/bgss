import {
  DownloadOutlined,
  RotateLeftOutlined,
  RotateRightOutlined,
  SwapOutlined,
  ZoomInOutlined,
  ZoomOutOutlined,
} from "@ant-design/icons";
import { LoadingButton } from "@mui/lab";
import { Image, Space } from "antd";
import DOMPurify from "dompurify";
import React, { useEffect, useState } from "react";
import { OverlayTrigger, Tooltip } from "react-bootstrap";
import { toast } from "react-hot-toast";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import StarRatings from "react-star-ratings";
import { useAddToCartMutation } from "../../redux/api/cartApi";
import { useGetProductDetailsQuery } from "../../redux/api/productsApi";
import Loader from "../layout/Loader";
import MetaData from "../layout/MetaData";
import NotFound from "../layout/NotFound";
import ListReviews from "../reviews/ListReviews";
import NewReview from "../reviews/NewReview";
// import UpdateReview from "../reviews/UpdateReview";
const ProductDetails = () => {
  const params = useParams();
  const dispatch = useDispatch();

  const [quantity, setQuantity] = useState(1);
  const [activeImg, setActiveImg] = useState("");
  const [isLoadingAdd, setIsLoadingAdd] = useState(false);

  const { data, isSuccess, isLoading, error, isError } =
    useGetProductDetailsQuery(params?.id);
  const product = data?.data;
  const { isAuthenticated, roles } = useSelector((state) => state.auth);
  const navigate = useNavigate();
  useEffect(() => {
    if (isSuccess) {
      setActiveImg(product?.productImages[0] || "/images/default_product.png");
    }
    if (isError) {
      toast.error(error?.data?.message);
    }
  }, [isError, isSuccess]);

  const increaseQty = () => {
    const count = document.querySelector(".count");

    if (count.valueAsNumber >= product?.stock) return;

    const qty = count.valueAsNumber + 1;
    setQuantity(qty);
  };
  const decreaseQty = () => {
    const count = document.querySelector(".count");

    if (count.valueAsNumber <= 1) return;

    const qty = count.valueAsNumber - 1;
    setQuantity(qty);
  };
  const [addProductToCart, { isLoading: isAdding }] = useAddToCartMutation();
  const onDownload = () => {
    fetch(activeImg)
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
  const setItemToCart = async () => {
    if (!isAuthenticated) {
      navigate("/login");
      return;
    }
    if (!roles?.includes("ROLE_CUSTOMER")) {
      navigate(`/admin/products/${params.id}`);
      return;
    }
    const cartItem = {
      productId: product?.id,
      selected: true,
      quantity,
    };

    try {
      setIsLoadingAdd(true);
      await addProductToCart(cartItem).unwrap();
      toast.success("Item added to Cart");
      setIsLoadingAdd(false);
    } catch (error) {
      setIsLoadingAdd(false);
    }
  };

  if (isLoading) return <Loader />;

  if (error && error?.status === 404) {
    return <NotFound />;
  }
  return (
    <>
      <MetaData title={product?.productName} />
      <div className="row d-flex justify-content-around">
        <div
          className="col-12 col-lg img-fluid d-flex flex-column  justify-content-center align-items-center "
          id="product_image"
        >
          <section className="ratio ratio-1x1">
            <Image
              style={{ objectFit: "cover" }}
              className="card-img-top "
              src={activeImg}
              alt={product.productName}
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
                  <Space
                    size={12}
                    className="toolbar-wrapper"
                    style={{ color: "orange" }}
                  >
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
          </section>
          <aside className="row gap-2 justify-content-center mt-4 ">
            {product?.productImages?.map((img, index) => (
              <>
                <div className="col-2 mt-2" key={index}>
                  <div role="button">
                    <img
                      className={`d-block border rounded p-3 cursor-pointer ${
                        img === activeImg ? "border-warning" : ""
                      }`}
                      height="100"
                      width="100"
                      src={img}
                      alt={img}
                      onClick={(e) => setActiveImg(img)}
                    />
                  </div>
                </div>
              </>
            ))}
          </aside>
        </div>

        <div className="col-12 col-lg-6 mt-5">
          <h3>{product?.productName}</h3>
          <hr />
          <div className="d-flex">
            <StarRatings
              rating={product?.avgReview}
              starRatedColor="#ffb829"
              numberOfStars={5}
              name="review"
              starDimension="24px"
              starSpacing="1px"
            />
            <span id="no-of-reviews" className="pt-1 ps-2">
              ({product?.avgReview.toFixed(1)} Reviews)
            </span>
          </div>
          <hr />

          {product?.percentageReduce && (
            <>
              <div
                className="text-decoration-line-through text-secondary"
                id="product_price"
              >
                <span style={{ fontWeight: "bold" }}>
                  ${product?.priceProduct}
                </span>
              </div>
              <div className="mb-2">
                <span style={{ fontWeight: "bold" }} id="product_price">
                  ${product?.secondPrice}
                </span>{" "}
                (<span style={{ fontWeight: "bold" }}>Discount</span>{" "}
                <span style={{ fontWeight: "bold", color: "orange" }}>
                  {product?.percentageReduce}%
                </span>
                )
              </div>
            </>
          )}
          {!product?.percentageReduce && (
            <p id="product_price">
              <span style={{ color: "orange", fontWeight: "bold" }}>
                ${product?.priceProduct}
              </span>
            </p>
          )}

          <div className="stockCounter d-inline">
            <span className="btn btn-secondary minus" onClick={decreaseQty}>
              -
            </span>
            <input
              type="number"
              className="form-control count d-inline"
              value={quantity}
              readOnly
            />
            <span className="btn btn-primary plus" onClick={increaseQty}>
              +
            </span>
          </div>
          {roles?.includes("ROLE_ADMIN") ||
            (roles?.includes("ROLE_CUSTOMER") && (
              <LoadingButton
                type="submit"
                // id="cart_btn"
                // className="btn btn-primary d-inline ms-4"
                disabled={product?.unitOfStock <= 0}
                onClick={setItemToCart}
                loading={isLoadingAdd}
                sx={{
                  marginLeft: 1,
                  borderRadius: "20px",
                  backgroundColor: "darkorange",
                  color: "white",
                  "&:hover": {
                    color: "black",
                    backgroundColor: "darkorange",
                  },
                }}
              >
                {isAuthenticated
                  ? !roles?.includes("ROLE_CUSTOMER")
                    ? "Edit Product"
                    : "Add to Cart"
                  : "Sign in"}
              </LoadingButton>
            ))}
          <hr />

          <div className="d-flex">
            <p className="me-4">
              <span style={{ fontWeight: "bold" }}>Type:</span>{" "}
              <span style={{ fontWeight: "bold", color: "orange" }}>
                {product?.typeGold?.typeName}
              </span>
            </p>
            <p>
              <span style={{ fontWeight: "bold", marginLeft: "50px" }}>
                Category:
              </span>{" "}
              <span style={{ fontWeight: "bold", color: "orange" }}>
                {product?.category?.categoryName}
              </span>
            </p>
            {product?.weight && (
              <div>
                <p>
                  <span style={{ fontWeight: "bold", marginLeft: "50px" }}>
                    Weight:
                  </span>{" "}
                  <span style={{ fontWeight: "bold", color: "orange" }}>
                    {product?.weight} MACE
                  </span>
                </p>
              </div>
            )}
          </div>
          <hr></hr>
          <span className="col">
            Status: {product.unitOfStock}
            <OverlayTrigger
              placement="right-end"
              overlay={<Tooltip>{product?.unitOfStock}</Tooltip>}
            >
              <span
                id="stock_status"
                className={product?.unitOfStock > 0 ? "greenColor" : "redColor"}
              >
                {product?.unitOfStock > 0 ? "In Stock" : "Out of Stock"}
              </span>
            </OverlayTrigger>
          </span>
          <hr />

          <h4 className="mt-2">Description:</h4>
          <pre
            className=""
            dangerouslySetInnerHTML={{
              __html: DOMPurify.sanitize(product?.description),
            }}
          />
          <hr />

          {isAuthenticated ? (
            <NewReview productId={params?.id} />
          ) : (
            <div className="alert alert-danger" type="alert">
              Please sign in to post your review.
            </div>
          )}
        </div>
      </div>
      {/* {product?.reviews?.length > 0 && (
        <ListReviews reviews={product?.reviews} />
      )} */}
      <div className="mt-4">
        <ListReviews params={params} />
      </div>
    </>
  );
};

export default ProductDetails;
