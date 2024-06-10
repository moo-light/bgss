import { formatDate } from "date-fns";
import { useFormik } from "formik";
import React, { useEffect } from "react";
import { toast } from "react-hot-toast";
import { useNavigate } from "react-router-dom";
import * as Yup from "yup";
import { FormError } from "../../../helpers/components/form-error";
import FormInput from "../../../helpers/components/form-input";
import { useCreateDiscountMutation } from "../../../redux/api/discountApi";
import AdminLayout from "../../layout/AdminLayout";
import MetaData from "../../layout/MetaData";

const NewDiscount = () => {
  const navigate = useNavigate();

  const inittialValues = {
    discountPercentage: "",
    dateExpire: new Date(),
    minPrice: "",
    maxReduce: "",
    quantityMin: "",
    defaultQuantity: "",
  };

  const [createDiscount, { isLoading, error, isSuccess }] =
    useCreateDiscountMutation();

  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success("Discount created successfully");
      navigate("/admin/discounts");
    }
  }, [error, isSuccess]);

  const discountSchema = Yup.object().shape({
    discountPercentage: Yup.number()
      .required("Discount percentage is required")
      .moreThan(0, "Discount percentage must be more than 0")
      .max(50, "Discount percentage must be less than 50"),
    minPrice: Yup.number().required("Please input min price"),
    maxReduce: Yup.number().required("Please input max reduce price"),
    quantityMin: Yup.number().required("Please input minimum quantity"),
    defaultQuantity: Yup.number().required("Please input allowed quantity"),
    dateExpire: Yup.date()
      .required("Expiration date is required")
      .nonNullable("Expiration date is required")
      .min(
        new Date(),
        `Expiration Date can't be less than ${new Date().toLocaleString()}`
      ),
  });

  const { values, errors, touched, isSubmitting, handleSubmit, handleChange } =
    useFormik({
      initialValues: inittialValues,
      validationSchema: discountSchema,
      validateOnBlur: false,
      validateOnMount: false,
      validateOnChange: false,
      onSubmit: (v) => {
        createDiscount(values);
      },
    });
  return (
    <AdminLayout>
      <MetaData title={"Create New Discount"} />
      <div className="row wrapper content">
        <div className="col-10 col-lg-10 mt-5 mt-lg-0">
          <form className="shadow rounded bg-body" onSubmit={handleSubmit}>
            <h2 className="mb-3 ">New Discount</h2>
            <h5>Discount information</h5>
            <div className="mb-3 form-group col-lg-10 m-auto px-3">
              <label htmlFor="expire_field" className="form-label">
                Expiration Date
              </label>
              <input
                type="datetime-local"
                id="expire_field"
                className="form-control"
                name="dateExpire"
                aria-invalid={!!errors["dateExpire"]}
                value={formatDate(values["dateExpire"], "yyyy-MM-dd'T'HH:mm")}
                defaultValue={formatDate(
                  inittialValues["dateExpire"],
                  "yyyy-MM-dd'T'HH:mm"
                )}
                onChange={handleChange}
                min={formatDate(new Date(), "yyyy-MM-dd'T'HH:mm")}
              />
              <FormError
                name="dateExpire"
                errorData={errors}
                touched={touched}
              />
            </div>
            <div className="row col-lg-10 m-auto">
              <FormInput
                label="Discount Percentage"
                name="discountPercentage"
                type="number"
                className="col"
                values={values}
                trailing={"%"}
                touched={touched}
                errors={errors}
                onChange={handleChange}
              />
              <FormInput
                label="Discount amount"
                name="defaultQuantity"
                type="number"
                className="col"
                trailing={"@"}
                touched={touched}
                values={values}
                errors={errors}
                onChange={handleChange}
              />
            </div>
            <h5>Constants</h5>
            <br />
            <div className="row col-lg-10 m-auto">
              <FormInput
                label="Minimum Allowed Price"
                name="minPrice"
                type="number"
                className="col-12 col-lg"
                values={values}
                trailing={"$"}
                errors={errors}
                touched={touched}
                onChange={handleChange}
              />
              <FormInput
                label="Minimum Allowed Quantity"
                name="quantityMin"
                type="number"
                className="col-12 col-lg"
                values={values}
                errors={errors}
                trailing={"@"}
                touched={touched}
                onChange={handleChange}
              />
            </div>
            <div className="row col-lg-10 m-auto">
              <FormInput
                label="Max Price Reduce"
                name="maxReduce"
                type="number"
                className="col-lg"
                trailing={"$"}
                values={values}
                errors={errors}
                touched={touched}
                onChange={handleChange}
              />
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

export default NewDiscount;
