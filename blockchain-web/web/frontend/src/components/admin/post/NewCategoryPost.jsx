import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import * as Yup from "yup";
import { useNavigate } from "react-router-dom";
import { FormError } from "../../../helpers/components/form-error";
import {
  useCreateCategoryPostMutation,
  useGetAllCategoryPostQuery,
} from "../../../redux/api/postCategoryApi";
import AdminLayout from "../../layout/AdminLayout";
import MetaData from "../../layout/MetaData";

const NewCategoryPost = () => {
  const navigate = useNavigate();
  const { refetch } = useGetAllCategoryPostQuery(); // Lấy refetch từ useGetAllCategoryPostQuery

  const [newCategoryPost, setNewCategoryPost] = useState({
    categoryName: "",
    forumId: "", // Thêm trường Forum ID
  });

  const { categoryName, forumId } = newCategoryPost;

  const [createCategoryPost, { isLoading, error, isSuccess }] =
    useCreateCategoryPostMutation();

  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success("Category post created successfully");
      refetch(); // Gọi lại refetch để cập nhật danh sách sau khi thêm mới
      navigate("/admin/categoryPosts");
    }
  }, [error, isSuccess, navigate, refetch]);

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    setNewCategoryPost({
      ...newCategoryPost,
      [e.target.name]: e.target.value,
    });
  };

  const categoryPostSchema = Yup.object().shape({
    categoryName: Yup.string().required("Category name is required"),
    // forumId: Yup.string().required("Forum ID is required"), // Kiểm tra Forum ID
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    categoryPostSchema
      .validate(newCategoryPost, { abortEarly: false })
      .then(() => {
        createCategoryPost({ categoryName, forumId }); // Sử dụng categoryName và forumId để tạo category post
      })
      .catch((yupError) => {
        const newErrors = {};
        if (yupError.inner) {
          yupError.inner.forEach((error) => {
            newErrors[error.path] = error.message;
          });
        }
        setErrors(newErrors);
        toast.error("Please fix the validation errors.");
      });
  };

  return (
    <AdminLayout>
      <MetaData title={"Create New Category Post"} />
      <div className="row wrapper">
        <div className="col-10 col-lg-10 mt-5 mt-lg-0">
          <form className="shadow rounded bg-body" onSubmit={handleSubmit}>
            <h2 className="mb-4">New Category Post</h2>
            <div className="mb-3">
              <label htmlFor="category_field" className="form-label">
                Category Name
              </label>
              <input
                type="text"
                id="category_field"
                className="form-control"
                name="categoryName"
                value={categoryName}
                onChange={handleChange}
              />
              <FormError name="categoryName" errorData={errors} />
            </div>
            {/* <div className="mb-3">
              <label htmlFor="forum_field" className="form-label">
                Forum ID
              </label>
              <input
                type="text"
                id="forum_field"
                className="form-control"
                name="forumId"
                value={forumId}
                onChange={handleChange}
              />
              <FormError name="forumId" errorData={errors} />
            </div> */}
            <button
              type="submit"
              className="btn w-100 py-2"
              disabled={isLoading}
            >
              {isLoading ? "Creating..." : "CREATE"}
            </button>
          </form>
        </div>
      </div>
    </AdminLayout>
  );
};

export default NewCategoryPost;
