import React, { useEffect, useRef, useState } from "react";
// import { Loader } from "react-bootstrap-typeahead";
import { Button, Dropdown, Modal } from "react-bootstrap";
import toast from "react-hot-toast";
import {
  FaCaretDown,
  FaCaretUp,
  FaEdit,
  FaEye,
  FaPlus,
  FaTrash,
} from "react-icons/fa";
import { PhotoProvider, PhotoView } from "react-photo-view";
import { useSelector } from "react-redux";
import { BASE_AVATAR } from "../../constants/constants";
import { clone, myDateFormat } from "../../helpers/helpers";
import { getServerImgUrl } from "../../helpers/image-handler";
import {
  useDeleteRateMutation,
  useEditRateMutation,
  useLazyGetAllRateQuery,
} from "../../redux/api/rateApi";
import Loader from "../layout/Loader";
const ListRatings = ({ params }) => {
  const [getRateData, { loading: rateLoading, data }] =
    useLazyGetAllRateQuery();
  const rates = data?.data;
  useEffect(() => {
    getRateData({
      postId: params?.id,
    });
  }, [getRateData, params]);
  const [opens, setOpens] = useState([]);
  const isOverflown = (element) => {
    return element?.scrollHeight > element?.clientHeight;
  };
  const { roles, user } = useSelector((state) => state.auth);
  const userId = user?.userId;
  const ref = useRef();
  useEffect(() => {
    const contents = ref.current.querySelectorAll(".review_comment");
    Array.from(contents).forEach((content) => {
      if (isOverflown(content.querySelector("p"))) {
        content.ariaHidden = "true";
      }
    });
    rates && setOpens(Array.from(rates?.map((x) => false)));
  }, [rates]);

  const toggleOpen = (index) => {
    const newOpens = [...opens];
    newOpens[index] = !newOpens[index];
    setOpens(newOpens);
  };

  //Modal
  const [currentReview, setCurrentReview] = useState(null);
  const [show, setShow] = useState(false);
  const handleClose = () => setShow(false);
  const [imgReview, setImgReview] = useState(null);
  const handleImageChange = (e) => {
    const file = Array.from(e.target.files)[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      setImgReview({ file, preview: reader.result });
    };
    reader.readAsDataURL(file);
  };
  // Endmodal

  const [editReview, editReviewResult] = useEditRateMutation();
  const [deleteReview, deleteReviewResult] = useDeleteRateMutation();
  const handleEditReview = (review, isOpenModal) => {
    if (isOpenModal) {
      setCurrentReview(clone(review));
      setShow(true);
    } else {
      const request = {
        id: review.id,
        content: currentReview.content,
        imageRate: imgReview?.file,
        userId: review.user?.userId,
        postId: params.id,
      };
      editReview(request);
      setCurrentReview(null);
    }
  };
  useEffect(() => {
    if (editReviewResult.isSuccess) {
      toast.success(editReviewResult?.data?.message);
    }
    if (editReviewResult.isError) {
      toast.error(editReviewResult?.error?.message);
    }
  }, [editReviewResult.isSuccess, editReviewResult.isError]);

  const handleDeleteReview = (review) => {
    const request = {
      id: review.id,
      postId: params.id,
      userId: review.user?.userId,
    };
    deleteReview(request);
  };

  return (
    <div className="col-12 col-lg-8 m-auto">
      <div className="rates m-auto" ref={ref}>
        <h3>Other's Rates:</h3>
        <hr />
        {rateLoading ? (
          <Loader />
        ) : (
          rates?.map((review) => {
            const index = rates.indexOf(review);
            const isMyReview = review?.user?.userId === userId;
            const isAllowedDelete =
              isMyReview || roles?.match(/ADMIN|STAFF/g)?.length > 0; //check if is Admin or staff or is My review
            return (
              <div
                key={review?.id}
                className="review-card my-3 position-relative "
              >
                <div className="row">
                  <div className="col-auto">
                    <img
                      src={getServerImgUrl(
                        review?.user?.userInfo?.avatarUrl,
                        BASE_AVATAR
                      )}
                      alt={`${review?.user?.username}`}
                      width="45"
                      className="rounded-circle"
                    />
                  </div>
                  {isAllowedDelete && (
                    <div className="col-auto position-absolute end-0">
                      <Dropdown>
                        <Dropdown.Toggle
                          variant="light"
                          id={`dropdown-${review.id}`}
                          className="custom-dropdown-toggle no-caret border-1 border"
                          tabIndex={-1}
                          align="end"
                        >
                          ...
                        </Dropdown.Toggle>
                        <Dropdown.Menu
                          style={{ width: "auto", minWidth: "100%" }}
                          className="dropdown-menu-custom"
                        >
                          {isMyReview && (
                            <Dropdown.Item
                              onClick={() => handleEditReview(review, true)}
                              className="text"
                            >
                              <FaEdit /> Edit
                            </Dropdown.Item>
                          )}
                          {isAllowedDelete && (
                            <Dropdown.Item
                              onClick={() => handleDeleteReview(review)}
                              className="text-danger"
                            >
                              <FaTrash /> Delete
                            </Dropdown.Item>
                          )}
                          {/* Add more dropdown items here if needed */}
                        </Dropdown.Menu>
                      </Dropdown>
                    </div>
                  )}
                  <div className="col">
                    <div className="user-select-none ">
                      {review.user?.userInfo?.firstName +
                        " " +
                        review.user?.userInfo?.lastName +
                        " "}
                      <span className=" text-50 user-select-none small">
                        {review?.user?.roles
                          ?.map((e) => e.name)
                          .join(",")
                          .includes("ADMIN") && "Administator"}
                      </span>
                    </div>
                    <p className="review_user">
                      {myDateFormat(review?.createDate)}
                    </p>
                    <div className="review_comment d-flex flex-column">
                      <pre
                        className={`
                          ${
                            ref?.current?.ariaHidden !== "true" &&
                            "line-clamp-4"
                          }
                        `}
                      >
                        {review?.content}
                      </pre>
                      <label
                        className="mt-auto bg-transparent btn border-0 col col-lg-6 p-0 link-primary  "
                        for={`rate-${index}`}
                      >
                        {!opens[index] ? (
                          <>
                            See more <FaCaretDown />
                          </>
                        ) : (
                          <>
                            Close <FaCaretUp />
                          </>
                        )}
                      </label>
                      <input
                        type="checkbox"
                        id={`rate-${index}`}
                        className="d-none"
                        onChange={(e) => toggleOpen(index)}
                      />
                    </div>
                    <div className="review_image col-2 ">
                      {review?.imgUrl && (
                        <PhotoProvider speed={() => 300}>
                          <PhotoView
                            src={getServerImgUrl(review?.imgUrl)}
                            key={review.id}
                          >
                            <div className="ratio ratio-1x1">
                              <img
                                src={getServerImgUrl(review?.imgUrl)}
                                alt=""
                                className="object-fit-cover rounded "
                              />
                            </div>
                          </PhotoView>
                        </PhotoProvider>
                      )}
                    </div>
                  </div>
                </div>
                <hr />
              </div>
            );
          })
        )}
        <Modal show={show} onHide={handleClose} size={"lg"}>
          <Modal.Header closeButton>
            <Modal.Title>Edit Review</Modal.Title>
          </Modal.Header>

          <div
            className="modal-body position-relative "
            style={
              {
                // backgroundColor: "var(--background-color)",
              }
            }
          >
            <label
              className={`label d-flex list-group-item pe-auto fw-bold m-0 align-items-center justify-content-between rounded-top-2 orange-2 p-3 `}
              onClick={setShow}
            >
              <div>Comment</div>
            </label>
            <textarea
              name="review"
              id="review"
              className="form-control rounded-0 mb-2"
              placeholder="Enter your review"
              value={currentReview?.content}
              maxLength={2000}
              style={{ height: "100px", boxShadow: "none" }}
              onChange={(e) =>
                setCurrentReview({ ...currentReview, content: e.target.value })
              }
            />
            <div className="mt-2 d-none">
              <input
                type="file"
                id="image-e"
                name="review-image"
                onChange={handleImageChange}
              />
            </div>
            <section className="col-4 d-flex">
              <div className=" position-relative border-2 w-100">
                <label
                  for="image-e"
                  className={`w-100 ${
                    imgReview?.preview && "ratio ratio-1x1"
                  } `}
                >
                  <img
                    className="object-fit-cover img-fluid col-12 rounded  "
                    src={imgReview?.preview || currentReview?.imgUrl}
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
            </section>
            <button
              className="btn btn-danger ms-auto d-block"
              aria-label="delete"
              onClick={handleDeleteReview}
              tabIndex={-1}
            >
              <FaTrash></FaTrash>
            </button>
          </div>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleClose}>
              Close
            </Button>
            <Button
              variant="primary"
              onClick={(e) => {
                handleEditReview(currentReview, false);
                handleClose();
              }}
            >
              Save
            </Button>
          </Modal.Footer>
        </Modal>
      </div>
    </div>
  );
};

export default ListRatings;
