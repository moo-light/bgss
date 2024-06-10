import { MDBDataTable } from "mdbreact";
import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import { FaStar } from "react-icons/fa"; // Import biểu tượng sao

import { myDateFormat } from "../../../helpers/helpers";
import {
  useDeleteReviewMutation,
  useLazyGetAdminProductReviewsQuery,
} from "../../../redux/api/productsApi";
import AdminLayout, { AdminLayoutLoader } from "../../layout/AdminLayout";
import MetaData from "../../layout/MetaData";

const ProductReviews = () => {
  const [productId, setProductId] = useState("");

  const [getProductReviews, { data, isLoading, error }] =
    useLazyGetAdminProductReviewsQuery();

  const [
    deleteReview,
    { error: deleteError, isLoading: isDeleteLoading, isSuccess },
  ] = useDeleteReviewMutation();

  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (deleteError) {
      toast.error(deleteError?.data?.message);
    }

    if (isSuccess) {
      toast.success("Review Deleted");
    }
  }, [error, deleteError, isSuccess]);

  useEffect(() => {
    getProductReviews(productId);
  }, [productId]); // Gọi getProductReviews khi productId thay đổi

  const deleteReviewHandler = (id) => {
    deleteReview({ productId, id });
  };

  const setReviews = () => {
    const reviews = {
      columns: [
        {
          label: "ID",
          field: "id",
          sort: "asc",
        },
        {
          label: "Rating",
          field: "rating",
          sort: "asc",
        },
        {
          label: "Comment",
          field: "comment",
          sort: "asc",
        },
        {
          label: "User",
          field: "user",
          sort: "asc",
        },
        {
          label: "CreateDate",
          field: "createDate",
          sort: "asc",
        },
      ],
      rows: [],
    };

    data?.data?.forEach((review) => {
      // Chuyển đổi số lượng đánh giá thành biểu tượng sao
      const ratingStars = Array.from(
        { length: review?.numOfReviews },
        (_, index) => <FaStar key={index} color="#ffc107" />
      );

      reviews.rows.push({
        id: review?.id,
        rating: ratingStars, // Sử dụng biểu tượng sao thay vì số lượng đánh giá
        comment: review?.content,
        user:
          review?.user?.userInfo?.firstName +
          " " +
          review?.user?.userInfo?.lastName,
        createDate: myDateFormat(review?.createDate),
        actions: (
          <>
            <button
              aria-label="Delete"
              className="btn btn-outline-danger ms-2"
              onClick={() => deleteReviewHandler(review?.id)}
              disabled={isDeleteLoading}
            >
              <i className="fa fa-trash"></i>
            </button>
          </>
        ),
      });
    });

    return reviews;
  };
  const title = "All Reviews";
  if (isLoading) return <AdminLayoutLoader title />;

  return (
    <AdminLayout>
      <MetaData title={title} />
      <h1 className="my-2 px-5">{data?.data?.length} Review</h1>
      {data?.data?.length > 0 ? (
        <div className="content mt-5">
          <style jsx>{`
            .dataTables_wrapper .dataTables_length,
            .dataTables_wrapper .dataTables_filter,
            .dataTables_wrapper .dataTables_info,
            .dataTables_wrapper .dataTables_paginate {
              margin-bottom: 20px; /* Điều chỉnh giá trị theo ý muốn của bạn */
            }
          `}</style>
          <MDBDataTable
            data={setReviews()}
            className="px-5"
            bordered
            responsive
            striped
            hover
          />
        </div>
      ) : (
        <p className="mt-5 text-center">No Reviews</p>
      )}
    </AdminLayout>
  );
};

export default ProductReviews;
