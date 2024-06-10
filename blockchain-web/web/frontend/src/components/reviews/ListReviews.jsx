import React, { useEffect } from "react";
import { Dropdown } from "react-bootstrap";
import { toast } from "react-hot-toast";
import { FaTrash } from "react-icons/fa";
import { PhotoProvider, PhotoView } from "react-photo-view";
import { useSelector } from "react-redux";
import StarRatings from "react-star-ratings";
import { BASE_AVATAR } from "../../constants/constants";
import { myDateFormat } from "../../helpers/helpers";
import { getServerImgUrl } from "../../helpers/image-handler";
import {
  useDeleteReviewProductMutation,
  useLazyGetReviewsQuery,
} from "../../redux/api/productsApi";

const ListReviews = ({ params }) => {
  const [getProductReviews, { data }] = useLazyGetReviewsQuery();
  const { user, roles } = useSelector((state) => state.auth);
  const [deleteReviewProduct] = useDeleteReviewProductMutation();

  useEffect(() => {
    getProductReviews({ productId: params?.id });
  }, [getProductReviews]);

  const reviews = data?.data;

  const handleDeleteReview = async (reviewId) => {
    try {
      await deleteReviewProduct(reviewId);
      toast.success("Review deleted successfully.");
      window.location.reload();
    } catch (error) {
      toast.error("Error deleting review:", error);
    }
  };
  // const {
  //   userInfo: { id },
  // } = user;
  // console.log(id);
  const currentUserId = user && user.userInfo ? user.userInfo.id : null;
  return (
    <div>
      <div className="reviews w-75 m-auto ">
        <h3>Other's Reviews:</h3>
        <hr />
        {!reviews && <div className="h5">Reviews Empty</div>}
        {reviews?.map((review) => {
          return (
            <div key={review?.id} className="review-card my-3">
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
                <div className="col">
                  <div className="user-select-none d-flex align-items-center ">
                    {review.user?.userInfo?.firstName +
                      " " +
                      review.user?.userInfo.lastName}
                    <span className="ms-2 text-50 user-select-none small">
                      {review?.user?.roles
                        ?.map((e) => e.name)
                        .join("|")
                        .includes("ADMIN") && "Administator"}
                    </span>
                    <StarRatings
                      rating={review?.numOfReviews}
                      starRatedColor="#ffb829"
                      numberOfStars={5}
                      name="rating"
                      starDimension="15px"
                      starSpacing="1px"
                    />
                  </div>
                  <p className="review_user m-0">
                    {myDateFormat(review?.createDate)}
                  </p>
                  <div className="review_comment d-flex flex-column">
                    <pre className="">{review?.content}</pre>
                  </div>
                  <div className="review_image col-2">
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
                              className="object-fit-cover rounded"
                            />
                          </div>
                        </PhotoView>
                      </PhotoProvider>
                    )}
                  </div>
                </div>
                {currentUserId === review.user?.userId && (
                  <div className="col-auto">
                    <Dropdown>
                      <Dropdown.Toggle
                        variant="light"
                        id={`dropdown-${review.id}`}
                        className="custom-dropdown-toggle border-1  border"
                        tabIndex={-1}
                        align="start"
                      >
                        ...
                      </Dropdown.Toggle>
                      <Dropdown.Menu
                        style={{ width: "auto", minWidth: "100%" }}
                        className="dropdown-menu-custom"
                      >
                        <Dropdown.Item
                          onClick={() => handleDeleteReview(review.id)}
                          className="text-danger"
                        >
                          <FaTrash /> Delete
                        </Dropdown.Item>
                      </Dropdown.Menu>
                    </Dropdown>
                  </div>
                )}
              </div>
              <hr />
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default ListReviews;
