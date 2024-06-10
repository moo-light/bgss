import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import { useNavigate, useParams } from "react-router-dom";
import { FormError } from "../../../helpers/components/form-error";
import {
  addErrors,
  clearErrors,
} from "../../../helpers/form-validation-helpers";
import {
  useGetTypeGoldIdQuery,
  useUpdateTypeGoldMutation,
} from "../../../redux/api/typeGoldApi";

import AdminLayout from "../../layout/AdminLayout";
import MetaData from "../../layout/MetaData";
import { useDispatch } from "react-redux";

const UpdateCategoryGold = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const params = useParams();

  const [post, setPost] = useState({
    typeName: "",
    goldUnit: "",
    price: "",
  });

  const { typeName, goldUnit, price } = post;

  const [updatePost, { isLoading, error, isSuccess }] =
    useUpdateTypeGoldMutation();

  const { data } = useGetTypeGoldIdQuery(params?.id);

  useEffect(() => {
    if (data?.data) {
      setPost({
        typeName: data?.data?.typeName,
        goldUnit: data?.data?.goldUnit,
        price: data?.data?.price,
      });
    }

    if (error) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success("Type Gold updated");
      navigate("/admin/typeGolds");
    }
  }, [error, isSuccess, data]);

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
      <MetaData title={"Update Category Type Name"} />
      <div className="row wrapper">
        <div className="col-10 col-lg-10 mt-5 mt-lg-0">
          <form className="shadow rounded bg-body" onSubmit={submitHandler}>
            <h2 className="mb-4">Update Type Name</h2>

            <div className="mb-3">
              <label htmlFor="typeName_field" className="form-label">
                Type Name
              </label>
              <input
                type="text"
                id="typeName_field"
                className="form-control"
                name="typeName"
                value={typeName}
                onChange={onChange}
                readOnly
              />
              <FormError name="typeName" errorData={errors} />
            </div>

            <div className="mb-3">
              <label htmlFor="goldUnit_field" className="form-label">
                Gold Unit
              </label>
              <input
                type="text"
                id="goldUnit_field"
                className="form-control"
                name="goldUnit"
                value={goldUnit}
                onChange={onChange}
                readOnly
              />
              <FormError name="goldUnit" errorData={errors} />
            </div>

            <div className="mb-3">
              <label htmlFor="price_field" className="form-label">
                Price
              </label>
              <input
                type="number"
                id="price_field"
                className="form-control"
                name="price"
                value={price}
                onChange={onChange}
              />
              <FormError name="price" errorData={errors} />
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

export default UpdateCategoryGold;
