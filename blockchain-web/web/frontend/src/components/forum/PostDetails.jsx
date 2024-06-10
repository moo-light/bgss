import DOMPurify from "dompurify";
import React, { useEffect, useRef } from "react";
import { toast } from "react-hot-toast";
import { FaCircle } from "react-icons/fa";
import { PhotoProvider, PhotoView } from "react-photo-view";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate, useParams } from "react-router-dom";
import { format } from "timeago.js";
import { BASE_AVATAR, BASE_POSTIMG } from "../../constants/constants";
import { formatPostHTMLWithDiscount } from "../../helpers/helpers";
import { useGetPostDetailsQuery } from "../../redux/api/postApi";
import Loader from "../layout/Loader";
import MetaData from "../layout/MetaData";
import NotFound from "../layout/NotFound";
import ListRatings from "../ratings/ListRatings";
import NewReview from "../ratings/NewRating";

const PostDetails = () => {
  const params = useParams();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { data, isLoading, error, isError } = useGetPostDetailsQuery(
    params?.id
  );

  const post = data?.data;
  const { isAuthenticated, roles } = useSelector((state) => state.auth);
  const ref = useRef();
  const copyToClipboard = (text) => {
    const textarea = document.createElement("textarea");
    textarea.value = text;
    document.body.appendChild(textarea);
    textarea.select();
    document.execCommand("copy");
    document.body.removeChild(textarea);
  };
  useEffect(() => {
    if (isError) {
      toast.error(error?.data?.message);
    }
    if (ref?.current) {
      const discounts = Array.from(
        ref?.current?.querySelectorAll(".allow-discount-copy")
      );
      discounts.forEach((discount) => {
        const value = discount.dataset.value;
        discount.querySelector(".icon").onclick = (e) => {
          // navigator.clipboard.writeText(value);
          copyToClipboard(value);
          toast.success("Copied to Clipboard");
        };
      });
    }
  }, [isError, post]);

  if (isLoading) return <Loader />;

  if (error && error?.status === 404) {
    return <NotFound />;
  }

  return (
    <>
      <MetaData title={post?.title} />
      <div className="d-flex justify-content-around flex-column col col-lg-8 m-auto mt-5">
        <div className="col-12" id="">
          <h3>{post?.title}</h3>
          <div className="d-flex flex-column  align-items-start ">
            <div className="d-flex gap-2 align-items-center ">
              <img
                className="rounded-circle "
                width="50px"
                src={post?.user?.userInfo?.avatarUrl ?? BASE_AVATAR}
                alt=""
              />
              <div>
                {post?.user?.firstName + " " + post?.user?.lastName}
                <p className="small m-0 text-50  ">
                  <FaCircle
                    className="orange position-relative "
                    style={{ width: 10, top: "-1px", right: "5px" }}
                  />
                  {format(post?.createDate)}
                </p>
              </div>
            </div>
          </div>
        </div>

        <div className="col-12 col ">
          {post?.textImg && (
            <div className="p-1">
              <hr />
              <PhotoProvider speed={() => 300}>
                <PhotoView src={post?.textImg || BASE_POSTIMG}>
                  <img
                    className="d-block shadow rounded img-fluid mx-auto mb-3"
                    src={post?.textImg || BASE_POSTIMG}
                    alt={post?.title}
                    style={{ maxWidth: "100%", maxHeight: "300px" }} // Điều chỉnh kích thước tối đa của hình ảnh
                  />
                </PhotoView>
              </PhotoProvider>
            </div>
          )}
          {/* content */}
          <pre
            className="my-2"
            ref={ref}
            dangerouslySetInnerHTML={{
              __html: DOMPurify.sanitize(
                formatPostHTMLWithDiscount(post?.content)
              ),
            }}
          ></pre>
          <hr />
          {isAuthenticated ? (
            <NewReview postId={post?.id} />
          ) : (
            <div className="alert alert-danger" type="alert">
              Please sign in to post your rating.
            </div>
          )}
        </div>
      </div>
      <ListRatings params={params} />
    </>
  );
};

export default PostDetails;
