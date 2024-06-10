import React, { useEffect, useRef, useState } from "react";
import { Button, Modal, Spinner } from "react-bootstrap";
import { toast } from "react-hot-toast";
import { FaEye, FaPlus, FaTrash } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import StarRatings from "react-star-ratings";
import * as Yup from "yup";

import { PhotoProvider, PhotoView } from "react-photo-view";
import {
  productApi,
  useCanUserReviewQuery,
  useCreateReviewMutation,
} from "../../redux/api/productsApi";
import UpdateReview from "./UpdateReview";

const NewReview = ({ productId }) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const [content, setContent] = useState("");
  const [rating, setRating] = useState(0);
  const [imgReview, setImgReview] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [reviewPosted, setReviewPosted] = useState(false);
  const [createReview, { isLoading, error, isSuccess }] =
    useCreateReviewMutation();
  const { data, refetch: getUserReviewRefetch } = useCanUserReviewQuery({
    productId,
  });
  const { isAuthenticated } = useSelector((state) => state.auth);
  // State để theo dõi việc review đã được tạo thành công hay chưa
  const canReview = data?.data;
  const [reviewCreated, setReviewCreated] = useState(false);

  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (isSuccess && !reviewPosted) {
      toast.success("Review Posted");
      setRating(0);
      setContent("");
      setImgReview(null);
      navigate(document.location);
      setShowModal(false);
      setReviewCreated(true);
      dispatch(productApi.endpoints.getProductDetails.initiate(productId));
      dispatch(productApi.endpoints.getProductReviews.initiate(productId));
      getUserReviewRefetch();
      setReviewPosted(true); // Đánh dấu rằng review đã được đăng
    }
    if (data) {
      setReviewCreated(canReview === false);
    }
  }, [error, isSuccess, data, reviewPosted]);

  const submitHandler = (e) => {
    e.preventDefault();
    if (rating === 0) {
      toast.error("Please select Star rating");
      return;
    }
    const reviewData = {
      numOfReviews: rating,
      content: content.trim().replaceAll("\n", "<br>"),
      imgReview: imgReview?.file,
      productId,
    };

    createReview(reviewData);
  };
  const reviewSchema = Yup.object().shape({
    review: Yup.string()
      .required("review is required")
      .max(4000, "Review too long"),
    rating: Yup.number().moreThan(0, "Please select your review"),
  });

  const collapseRef = useRef();
  const [errors, setErrors] = useState({});
  const onChange = (e) => {
    const { name, value, type } = e.target;
    if (type === "file") {
      const file = Array.from(e.target.files)[0];
      if (!file) return;
      const reader = new FileReader();
      reader.onload = () => {
        setImgReview({ file, preview: reader.result });
      };
      reader.readAsDataURL(file);
    }
    if (name === "review") {
      setContent(value.substring(0, 10000));
      reviewSchema
        .validate({ [name]: value })
        .then((v) => setErrors({ ...errors, [name]: null }))
        .catch((error) => setErrors({ [name]: error.message }));
    }
  };

  return (
    <div className="mb-2">
      {canReview && (
        <>
          <span className="d-none">{collapseRef?.current?.ariaExpanded}</span>
          <label
            className={`label d-flex list-group-item pe-auto fw-bold m-0 align-items-center justify-content-between ${
              collapseRef?.current?.ariaExpanded === "true"
                ? "rounded-top-2"
                : "rounded-2"
            } orange-2 p-3 `}
            onClick={() => setShowModal(true)}
          >
            <div>Submit Your Review</div>
          </label>
        </>
      )}
      <Modal
        show={showModal}
        onHide={() => setShowModal(false)}
        backdrop="static"
        keyboard={false}
      >
        <Modal.Header closeButton>
          <Modal.Title>Write a Review</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div className="mt-2 d-flex align-items-center">
            <span className="me-2">Rating:</span>
            <StarRatings
              rating={rating}
              starRatedColor="orange"
              changeRating={setRating}
              numberOfStars={5}
              name="rating"
              starDimension="25px"
              starSpacing="5px"
            />
          </div>
          <textarea
            name="review"
            id="review"
            className={`form-control rounded-0 mt-2 ${
              errors.review && "border-danger"
            }`}
            placeholder="Enter your review"
            value={content}
            style={{ height: "100px", boxShadow: "none" }}
            onChange={onChange}
          />
          <div
            className={`text-end text-50 float-end ${
              errors.review && "text-danger"
            }`}
          >
            {content?.length} / 4000
          </div>
          <div className="form-error text-danger float-start">
            {errors.review}
          </div>
          <div className="col-4 mt-4 position-relative">
            <input
              type="file"
              className="d-none"
              id="image"
              name="review-image"
              onChange={onChange}
            />
            <label
              for="image"
              className={`${imgReview?.preview && "ratio ratio-1x1"} `}
            >
              <img
                className="object-fit-cover img-fluid col-12 rounded  "
                src={imgReview?.preview}
                alt=""
              />
              <div
                className={` rounded-2  start-0 m-2 top-0 hover btn bg-body shadow border-0 text-nowrap ${
                  imgReview?.preview && "d-none"
                }`}
              >
                <FaPlus className={`m-auto`} /> Add Image
              </div>
            </label>

            {imgReview?.preview && (
              <div className="d-flex top-0 bottom-0 start-100 m-2 position-absolute flex-column justify-content-between ">
                <PhotoProvider speed={() => 300}>
                  <PhotoView src={imgReview?.preview}>
                    <div className=" rounded-circle   hover btn shadow bg-body border-0   ">
                      <FaEye className="icon user-select-none " />
                    </div>
                  </PhotoView>
                </PhotoProvider>
                <div
                  className=" rounded-circle start-100   hover btn bg-body shadow border-0 "
                  onClick={(e) => setImgReview(null)}
                >
                  <FaTrash className="icon  user-select-none text-danger" />
                </div>
              </div>
            )}
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            Close
          </Button>
          <Button variant="primary" onClick={submitHandler}>
            {isLoading ? (
              <>
                <Spinner
                  as="span"
                  animation="border"
                  size="sm"
                  role="status"
                  aria-hidden="true"
                />{" "}
                Submitting...
              </>
            ) : (
              "Submit"
            )}
          </Button>
        </Modal.Footer>
      </Modal>
      {reviewCreated && <UpdateReview productId={productId} />}
    </div>
  );
};

export default NewReview;
