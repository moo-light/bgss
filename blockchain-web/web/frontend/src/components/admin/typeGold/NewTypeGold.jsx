import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import * as Yup from "yup";
import { useNavigate } from "react-router-dom";
import { FormError } from "../../../helpers/components/form-error";
import { useCreateTypeGoldMutation } from "../../../redux/api/typeGoldApi";
import AdminLayout from "../../layout/AdminLayout";
import MetaData from "../../layout/MetaData";

const NewTypeGold = () => {
  const navigate = useNavigate();

  const [newTypeGold, setNewTypeGold] = useState({
    typeName: "",
    price: "",
    goldUnit: "MACE", // Default value
  });

  const { typeName, price, goldUnit } = newTypeGold;

  const [createTypeGold, { isLoading, error, isSuccess }] =
    useCreateTypeGoldMutation();

  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success("Type Gold created successfully");
      navigate("/admin/typeGolds");
    }
  }, [error, isSuccess, navigate]);

  const [errors, setErrors] = useState({});

  const handleChange = (e) => {
    setNewTypeGold({
      ...newTypeGold,
      [e.target.name]: e.target.value,
    });
  };

  const typeGoldNameSchema = Yup.object().shape({
    typeName: Yup.string().required("Type name is required"),
    price: Yup.number()
      .required("Price is required")
      .positive("Price must be positive"),
    goldUnit: Yup.string().required("Gold unit is required"),
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    typeGoldNameSchema
      .validate(newTypeGold, { abortEarly: false })
      .then(() => {
        createTypeGold(newTypeGold);
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
      <MetaData title={"Create New Type Gold"} />
      <div className="row wrapper">
        <div className="col-10 col-lg-10 mt-5 mt-lg-0">
          <form className="shadow rounded bg-body" onSubmit={handleSubmit}>
            <h2 className="mb-4">New Type Gold</h2>
            <div className="mb-3">
              <label htmlFor="type_field" className="form-label">
                Type Name
              </label>
              <input
                type="text"
                id="type_field"
                className="form-control"
                name="typeName"
                value={typeName}
                onChange={handleChange}
              />
              <FormError name="typeGoldName" errorData={errors} />
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
                onChange={handleChange}
              />
              <FormError name="price" errorData={errors} />
            </div>
            <div className="mb-3">
              <label htmlFor="gold_unit_field" className="form-label">
                Gold Unit
              </label>
              <input
                type="text"
                id="gold_unit_field"
                className="form-control"
                name="goldUnit"
                value={goldUnit}
                onChange={handleChange}
                defaultValue="MACE"
                readOnly
              />
              <FormError name="goldUnit" errorData={errors} />
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

export default NewTypeGold;
