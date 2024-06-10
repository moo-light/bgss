import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import { useNavigate, useParams } from "react-router-dom";
import { FormError } from "../../../helpers/components/form-error";
import {
  addErrors,
  clearErrors,
} from "../../../helpers/form-validation-helpers";
import {
  useGetCategoryPostDetailsQuery,
  useUpdateCategoryPostMutation,
  useGetAllCategoryPostQuery,
} from "../../../redux/api/postCategoryApi";

import AdminLayout from "../../layout/AdminLayout";
import MetaData from "../../layout/MetaData";
import { useDispatch } from "react-redux";

const UpdateCategoryPost = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const params = useParams();

  const [post, setPost] = useState({
    categoryName: "",
  });

  const { categoryName } = post;

  const [updatePost, { isLoading, error, isSuccess }] =
    useUpdateCategoryPostMutation();

  const { data, refetch: refetchCategoryDetails } =
    useGetCategoryPostDetailsQuery(params?.id);
  const { refetch: refetchAllCategories } = useGetAllCategoryPostQuery();

  useEffect(() => {
    if (data?.data) {
      setPost({
        categoryName: data?.data?.categoryName,
      });
    }

    if (error) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success("Category name updated");
      refetchAllCategories(); // Refetch all category posts
      navigate("/admin/categoryPosts");
    }
  }, [error, isSuccess, data, refetchAllCategories, navigate]);

  const [errors, setErrors] = useState({});

  const onChange = (e) => {
    const value =
      e.target.type === "checkbox" ? e.target.checked : e.target.value;
    setPost({ ...post, [e.target.name]: value });
    // Optionally reset the error state when the user starts typing
    if (errors[e.target.name]) {
      setErrors({ ...errors, [e.target.name]: undefined });
      clearErrors(document, errors, e.target.name);
    }
  };

  const submitHandler = (e) => {
    e.preventDefault();
    updatePost({ id: params?.id, body: post });
  };

  useEffect(() => {
    addErrors(document, errors);
  }, [errors]);

  return (
    <AdminLayout>
      <MetaData title={"Update Category Post"} />
      <div className="row wrapper">
        <div className="col-10 col-lg-10 mt-5 mt-lg-0">
          <form className="shadow rounded bg-body" onSubmit={submitHandler}>
            <h2 className="mb-4">Update Category Post</h2>

            <div className="mb-3">
              <label htmlFor="categoryName_field" className="form-label">
                Category Name
              </label>
              <input
                type="text"
                id="categoryName_field"
                className="form-control"
                name="categoryName"
                value={categoryName}
                onChange={onChange}
              />
              <FormError name="categoryName" errorData={errors} />
            </div>

            <button
              type="submit"
              className="btn w-100 py-2"
              disabled={isLoading}
            >
              {isLoading ? "Updating..." : "UPDATE"}
            </button>
          </form>
        </div>
      </div>
    </AdminLayout>
  );
};

export default UpdateCategoryPost;
