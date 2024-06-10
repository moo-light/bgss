import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import * as Yup from "yup";
import { useNavigate } from "react-router-dom";
import { FormError } from "../../../helpers/components/form-error";
import {
  useCreateCategoryProductMutation,
  useGetCategoryProductsQuery,
} from "../../../redux/api/categoryProductApi";
import AdminLayout from "../../layout/AdminLayout";
import MetaData from "../../layout/MetaData";

const NewCategoryProduct = () => {
  const navigate = useNavigate();
  const { refetch } = useGetCategoryProductsQuery();

  const [newCategoryProduct, setNewCategoryProduct] = useState({
    categoryName: "",
  });

  const { categoryName } = newCategoryProduct;

  const [createCategoryProduct, { isLoading, error, isSuccess }] =
    useCreateCategoryProductMutation();

  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success("Category product created successfully");
      refetch();
      navigate("/admin/categoryProducts");
    }
  }, [error, refetch, isSuccess]);

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    setNewCategoryProduct({
      ...newCategoryProduct,
      [e.target.name]: e.target.value,
    });
  };

  const categoryProductSchema = Yup.object().shape({
    categoryName: Yup.string().required("Category name is required"),
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    categoryProductSchema
      .validate(newCategoryProduct, { abortEarly: false })
      .then(() => {
        createCategoryProduct(newCategoryProduct.categoryName);
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
      <MetaData title={"Create New Category Product"} />
      <div className="row wrapper">
        <div className="col-10 col-lg-10 mt-5 mt-lg-0">
          <form className="shadow rounded bg-body" onSubmit={handleSubmit}>
            <h2 className="mb-4">New Category Product</h2>
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

export default NewCategoryProduct;
