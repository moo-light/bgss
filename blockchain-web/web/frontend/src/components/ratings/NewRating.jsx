import React, { useEffect, useRef, useState } from "react";
import { Spinner } from "react-bootstrap";
import { toast } from "react-hot-toast";
import {
  FaCaretDown,
  FaCaretRight,
  FaEye,
  FaPlus,
  FaTrash,
} from "react-icons/fa";
import { PhotoProvider, PhotoView } from "react-photo-view";
import { useNavigate } from "react-router-dom";
import {
  useLazyGetAllRateQuery,
  useSubmitRateMutation,
} from "../../redux/api/rateApi.js";

const NewRating = ({ postId }) => {
  const [comment, setComment] = useState("");
  const [image, setImage] = useState(null);
  const [submitRate, { isLoading, error, isSuccess }] = useSubmitRateMutation();
  const [getRateData] = useLazyGetAllRateQuery();
  const [reviewCreated, setReviewCreated] = useState(false);
  const [show, setShow] = useState(false);
  // const { data } = useCanUserReviewQuery(postId);
  // const canReview = true;
  const navigate = useNavigate();
  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success("Rate Posted");
      setComment("");
      setImage(null);
      navigate(document.location);
      getRateData(postId);
    }
  }, [error, isSuccess]);

  const submitHandler = (e) => {
    e.preventDefault();
    const reviewData = {
      content: comment.trim(),
      postId: postId,
      imageRate: image?.file,
    };
    submitRate(reviewData);
  };
  const collapseRef = useRef();
  useEffect(() => {}, [collapseRef]);
  const onChange = (e) => {
    const file = Array.from(e.target.files)[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      setImage({ file, preview: reader.result });
    };
    reader.readAsDataURL(file);
  };
  return (
    <div className="mb-2">
      <>
        <span className="d-none">{collapseRef?.current?.ariaExpanded}</span>
        <label
          className={`label d-flex list-group-item pe-auto fw-bold m-0 align-items-center justify-content-between ${
            collapseRef?.current?.ariaExpanded === "true"
              ? "rounded-top-2"
              : "rounded-2"
          } orange-2 p-3 `}
          data-bs-toggle="collapse"
          data-bs-target="#ratingcardLabel"
          onClick={setShow}
          ref={collapseRef}
        >
          <div>Comment</div>
          {collapseRef?.current?.ariaExpanded === "true" ? (
            <FaCaretDown />
          ) : (
            <FaCaretRight />
          )}
        </label>
      </>

      <div
        className="collapse"
        id="ratingcardLabel"
        aria-labelledby="ratingcardLabel"
      >
        <div className="" tabIndex="-1">
          <form className="card-body " onSubmit={submitHandler}>
            <textarea
              name="review"
              id="review"
              className="form-control rounded-0"
              placeholder="Enter your comment"
              value={comment}
              maxLength={2000}
              style={{ height: "100px", boxShadow: "none" }}
              onChange={(e) => {
                setComment(e.target.value);
              }}
            />
            {/* Submit Section */}
            <div className="position-relative">
              <button
                id="new_review_btn"
                className="btn w-100 rounded-top-0 px-4"
                data-bs-dismiss="card"
                aria-label="Close"
                onClick={submitHandler}
              >
                Submit
              </button>
              {isLoading && (
                <Spinner
                  className="ms-2 end-0 position-absolute mt-1 text-light "
                  style={{ scale: "0.5" }}
                >
                  <span className="visually-hidden">Loading...</span>
                </Spinner>
              )}
            </div>
            {/* End Submit Section */}

            <div className="col-4 d-flex  ">
              <div className=" position-relative border-2 w-100">
                <label
                  for="image"
                  className={`w-100 ${image?.preview && "ratio ratio-1x1"} `}
                >
                  <img
                    className="object-fit-cover img-fluid col-12 rounded  "
                    src={image?.preview}
                    alt=""
                  />
                  <div
                    className={` rounded-2  start-0 m-2 top-0 hover btn bg-body shadow border-0 text-nowrap ${
                      image?.preview && "d-none"
                    }`}
                    onClick={(e) => setImage(null)}
                  >
                    <FaPlus className={`m-auto  `} /> Add Image
                  </div>
                </label>

                {image?.preview && (
                  <div className="d-flex top-0 bottom-0 start-100 m-2 position-absolute flex-column justify-content-between ">
                    <PhotoProvider speed={() => 300}>
                      <PhotoView src={image?.preview}>
                        <div className=" rounded-circle   hover btn shadow bg-body border-0   ">
                          <FaEye className="icon user-select-none " />
                        </div>
                      </PhotoView>
                    </PhotoProvider>
                    <div
                      className=" rounded-circle start-100   hover btn bg-body shadow border-0 "
                      onClick={(e) => setImage(null)}
                    >
                      <FaTrash className="icon  user-select-none text-danger" />
                    </div>
                  </div>
                )}
              </div>
            </div>

            <input
              type="file"
              id="image"
              name="rate-image"
              className="d-none"
              onChange={onChange}
            />
          </form>
        </div>
      </div>
    </div>
  );
};

export default NewRating;
