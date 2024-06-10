import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import * as Yup from "yup";
import { Link, useNavigate, useParams } from "react-router-dom";
import { FormError } from "../../../helpers/components/form-error";
import {
  useGetCategoryDetailsQuery, // Sử dụng hàm này để lấy thông tin chi tiết của một danh mục sản phẩm
  useUpdateCategoryProductMutation, // Sử dụng hàm này để cập nhật thông tin của một danh mục sản phẩm
} from "../../../redux/api/categoryProductApi"; // Import API của danh mục sản phẩm
import AdminLayout from "../../layout/AdminLayout";
import MetaData from "../../layout/MetaData";

const UpdateCategoryProduct = () => {
  const navigate = useNavigate();
  const params = useParams();

  const [category, setCategory] = useState({
    categoryName: "",
  });

  const { categoryName } = category;

  const [updateCategoryProduct, { isLoading, error, isSuccess }] = // Sử dụng hàm cập nhật danh mục sản phẩm từ API
    useUpdateCategoryProductMutation();

  const { data } = useGetCategoryDetailsQuery(params?.id); // Sử dụng hàm lấy thông tin chi tiết danh mục sản phẩm từ API

  const categorySchema = Yup.object().shape({
    categoryName: Yup.string().required("Category name is required"),
  });

  useEffect(() => {
    if (data?.data) {
      setCategory({
        categoryName: data?.data?.categoryName,
      });
    }

    if (error) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success("Category updated");
      navigate("/admin/categoryProducts");
    }
  }, [error, isSuccess, data]);

  const [errors, setErrors] = useState({});

  const onChange = (e) => {
    setCategory({ ...category, [e.target.name]: e.target.value });
    if (errors[e.target.name]) {
      setErrors({ ...errors, [e.target.name]: undefined });
    }
  };

  const submitHandler = (e) => {
    e.preventDefault();
    categorySchema
      .validate(category, { abortEarly: false })
      .then(() => {
        updateCategoryProduct({ id: params?.id, body: category }); // Gọi hàm cập nhật danh mục sản phẩm từ API
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
      <MetaData title={"Update Category Product"} />
      <div className="row wrapper">
        <div className="col-10 col-lg-10 mt-5 mt-lg-0">
          <form className="shadow rounded bg-body" onSubmit={submitHandler}>
            <h2 className="mb-4">Update Category Product</h2>
            <div className="mb-3">
              <label htmlFor="name_field" className="form-label">
                Category Name
              </label>
              <input
                type="text"
                id="name_field"
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

export default UpdateCategoryProduct;
