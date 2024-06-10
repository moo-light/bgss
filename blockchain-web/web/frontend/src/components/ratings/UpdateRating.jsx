import React, { useEffect, useState } from "react";
import { Button, Modal, Spinner } from "react-bootstrap";
import { toast } from "react-hot-toast";
import { FaStar } from "react-icons/fa";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import StarRatings from "react-star-ratings";
import { useUpdateReviewRateMutation } from "../../redux/api/rateApi";

const UpdateRating = ({ postId }) => {
  const dispatch = useDispatch();
  const [rating, setRating] = useState(0);
  const [content, setContent] = useState("");
  const [imgReview, setImgReview] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [updateRate, { isLoading, error, isSuccess }] =
    useUpdateReviewRateMutation();
  const navigate = useNavigate();

  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success("Rating Updated");
      setShowModal(false); // Đóng modal sau khi cập nhật thành công
    }
  }, [error, isSuccess]);

  const submitHandler = () => {
    const formData = new FormData();
    formData.append("postId", postId);
    formData.append("content", content);
    formData.append("imageRate", imgReview);

    updateRate(formData);
  };

  const onChange = (e) => {
    const file = e.target.files[0];
    setImgReview(file);
  };

  return (
    <div className="mb-2">
      <span
        className="label d-flex list-group-item pe-auto fw-bold m-0 align-items-center justify-content-between rounded-2 orange-2 p-3"
        onClick={() => setShowModal(true)}
      >
        <div>Update Your Rating</div>
      </span>

      <Modal
        show={showModal}
        onHide={() => setShowModal(false)}
        backdrop="static"
        keyboard={false}
      >
        <Modal.Header closeButton>
          <Modal.Title>Update Your Rating</Modal.Title>
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
          <div className="mt-2">
            <input
              type="file"
              id="image"
              name="review-image"
              onChange={onChange}
            />
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

export default UpdateRating;
