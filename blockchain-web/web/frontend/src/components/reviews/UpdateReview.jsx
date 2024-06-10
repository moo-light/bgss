import React, { useEffect, useRef, useState } from "react";
import { Button, Modal, Spinner } from "react-bootstrap";
import { toast } from "react-hot-toast";
import { FaEye, FaPlus, FaTrash } from "react-icons/fa";
import { PhotoProvider, PhotoView } from "react-photo-view";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import StarRatings from "react-star-ratings";
import {
  useGetUserReviewQuery,
  useLazyGetProductReviewsQuery,
  useUpdateReviewMutation,
} from "../../redux/api/productsApi";

const UpdateReview = ({ productId }) => {
  const dispatch = useDispatch();
  const [content, setContent] = useState("");
  const [rating, setRating] = useState(0);
  const [imgReview, setImgReview] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [updateReview, { isLoading, error, isSuccess }] =
    useUpdateReviewMutation();
  const getUserReview = useGetUserReviewQuery({
    productId,
  });
  const { data: userReview, isSuccess: userReviewSuccess } = getUserReview;
  const [getReviews] = useLazyGetProductReviewsQuery();
  const navigate = useNavigate();
  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success("Review Updated");
      setShowModal(false); // Đóng modal sau khi cập nhật thành công
      getReviews(productId);
      getUserReview.refetch();
    }
  }, [error, isSuccess]);

  useEffect(() => {
    if (userReviewSuccess) {
      setContent(userReview?.data?.content || "");
      setRating(userReview?.data?.numOfReviews || 0);
      setImgReview({ preview: userReview?.data?.imgUrl });
    }
  }, [userReviewSuccess, userReview]);

  const submitHandler = (e) => {
    e.preventDefault();
    const reviewData = {
      numOfReviews: rating,
      content: content,
      imgReview: imgReview?.file,
      productId,
    };
    updateReview(reviewData);
  };

  const collapseRef = useRef();

  const onChange = (e) => {
    const file = Array.from(e.target.files)[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      setImgReview({ file, preview: reader.result });
    };
    reader.readAsDataURL(file);
  };

  return (
    <div className="mb-2">
      {userReviewSuccess && (
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
            <div>Update Your Review</div>
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
          <Modal.Title>Update Your Review</Modal.Title>
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
            className="form-control rounded-0 mt-2"
            placeholder="Enter your review"
            value={content}
            maxLength={2000}
            style={{ height: "100px", boxShadow: "none" }}
            onChange={(e) => setContent(e.target.value)}
          />
          <div className="mt-2 d-none">
            <input
              type="file"
              id="image"
              name="review-image"
              onChange={onChange}
            />
          </div>
          <div className="col-4 d-flex  ">
            <div className=" position-relative border-2 w-100">
              <label
                for="image"
                className={`w-100 ${imgReview?.preview && "ratio ratio-1x1"} `}
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
                  onClick={(e) => setImgReview(null)}
                >
                  <FaPlus className={`m-auto  `} /> Add Image
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
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowModal(false)}>
            Close
          </Button>
          <Button
            variant="primary"
            onClick={submitHandler}
            disabled={isLoading}
          >
            {isLoading ? (
              <>
                <Spinner
                  as="span"
                  animation="border"
                  size="sm"
                  role="status"
                  aria-hidden="true"
                />{" "}
                Updating...
              </>
            ) : (
              "Update"
            )}
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default UpdateReview;
