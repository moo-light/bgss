import React, { useEffect, useRef, useState } from "react";
import { toast } from "react-hot-toast";
import * as Yup from "yup";

import { useFormik } from "formik";
import { FaBox } from "react-icons/fa";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import ImageInput from "../../../helpers/components/form-image-input";
import FormInput from "../../../helpers/components/form-input";
import TextInput from "../../../helpers/components/form-quill-input";
import FormSelect from "../../../helpers/components/form-select";
import {
  validateFileSize,
  validateFileType,
} from "../../../helpers/form-validation-helpers";
import { currencyFormat } from "../../../helpers/helpers";
import {
  useGetCategoriesQuery,
  // useCreateProductMutation,
  useGetProductDetailsQuery,
  useUpdateProductMutation,
} from "../../../redux/api/productsApi";
import { useGetTypeGoldQuery } from "../../../redux/api/typeGoldApi";
import AdminLayout from "../../layout/AdminLayout";
import MetaData from "../../layout/MetaData";
const typeOptions = ["AVAILABLE", "CRAFT"];

const UpdateProduct = () => {
  const navigate = useNavigate();
  const params = useParams();
  const [searchParams, setSearchParams] = useSearchParams();

  const [updateProduct, { isLoading, error, isSuccess, isError }] =
    useUpdateProductMutation();

  const { data, isSuccess: hasData } = useGetProductDetailsQuery(params?.id);
  const { data: categoryData } = useGetCategoriesQuery();
  const { data: typeGoldData } = useGetTypeGoldQuery();
  const [initialValues, setInitialData] = useState({
    productName: "",
    price: "",
    description: "",
    totalUnitOfStock: "",
    product_image: null,
    weight: "",
    percentageReduce: "",
    categoryId: null,
    typeGoldId: null,
  });
  const isMounted = useRef(false);
  const productImages = Array.from(data?.data?.productImages ?? []);
  useEffect(() => {
    if (hasData && !isMounted.current) {
      console.log(
        data?.data?.productImages,
        Array.from(data?.data?.productImages)
      );

      isMounted.current = true;
      setInitialData({
        id: params?.id,
        productName: data?.data?.productName,
        description: data?.data?.description,
        processingCost: data?.data?.processingCost,
        totalUnitOfStock: data?.data?.unitOfStock,
        percentageReduce: data?.data?.percentageReduce,
        weight: data?.data?.weight,
        product_image: productImages,
        categoryId: data?.data?.category?.id ?? null,
        typeGoldId: data.data.typeGold?.id ?? null,
        typeOption: data.data.typeGoldOption,
      });
      setSearchParams({ "type-product": data?.data?.typeGoldOption });
      resetForm();
    }
  }, [hasData]);

  useEffect(() => {
    // console.log(data?.data);

    if (isError) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success("Product updated");
      navigate("/admin/products");
    }
  }, [isError, isSuccess]);

  const max = 5;
  const productSchema = Yup.object().shape({
    productName: Yup.string().required("Product name is required"),
    processingCost: Yup.string().when("typeOption", {
      is: (v) => v === "AVAILABLE",
      then: (schema) =>
        schema
          .required("Processing Cost is required")
          .min(1, "Processing Cost must be larger than 0"),
      otherwise: (schema) => schema.nullable(),
    }),
    description: Yup.string()
      .required("Description is required")
      .max(2000, "Description must be smaller than 2000 characters"),
    categoryId: Yup.number()
      .required("Please choose a Category")
      .oneOf(
        categoryData?.data.map((e) => e.id) ?? [],
        "Please choose a Category"
      ),
    typeGoldId: Yup.number()
      .required("Please choose a Type")
      .oneOf(typeGoldData?.data.map((e) => e.id) ?? [], "Please choose a Type"),
    weight: Yup.number()
      .required("Weight is required")
      .min(0, "Please choose Valid Weight Amount"),
    totalUnitOfStock: Yup.number()
      .required("Stock is required")
      .min(0, "Please choose Valid Stock Amount"),
    product_image: Yup.array()
      .of(
        Yup.mixed()
          .test("fileSize", "File is too large", validateFileSize())
          .test("fileType", "Invalid File type", validateFileType())
      )
      .required("Image is required")
      .min(1, "Image is Required")
      .max(max, `Only ${max} images are allowed`),
    typeOption: Yup.string()
      .required("Type Option is required")
      .oneOf(typeOptions, "Invalid Type Option"),
  });
  const {
    values,
    errors,
    touched,
    isSubmitting,
    handleSubmit,
    handleChange,
    resetForm,
    ...formik
  } = useFormik({
    initialValues: initialValues,
    validationSchema: productSchema,
    validateOnBlur: false,
    validateOnMount: false,
    validateOnChange: false,
    enableReinitialize: true,
    onSubmit: (v) => {
      updateProduct({ id: params?.id, body: values });
    },
  });

  const typeGold = typeGoldData?.data?.find((tg) => values.typeGoldId == tg.id);
  return (
    <AdminLayout>
      <MetaData title={"Update Product"} />
      <div className="row wrapper">
        <div className="col-10 col-lg-10 mb-5">
          <form className="shadow rounded bg-body" onSubmit={handleSubmit}>
            <h2 className="mb-4">Update Product</h2>
            <div className="m-auto mb-3">
              <ImageInput
                name="product_image"
                label="Product Images"
                max={max}
                values={values}
                errors={errors}
                initialValues={productImages}
                touched={touched}
                onChange={handleChange}
                formik={formik}
                multiple
              />
            </div>
            <FormInput
              name="productName"
              errors={errors}
              values={values}
              touched={touched}
              label={"Product Name"}
              onChange={handleChange}
            />

            <TextInput
              name="description"
              errors={errors}
              values={values}
              formik={formik}
              touched={touched}
              label={"Description"}
              onChange={handleChange}
            />
            <h4 className="my-3">Product Information </h4>
            <div className="mb-3 col-12 col-lg-6 ">
              <FormSelect
                name="typeOption"
                options={[
                  { value: "AVAILABLE", label: "Available" },
                  { value: "CRAFT", label: "Craft" },
                ]}
                values={values}
                errors={errors}
                touched={touched}
                label={"Type Option"}
                onChange={(e) => {
                  handleChange(e);
                }}
                disabled={true}
              />
            </div>

            <div className="row">
              {values.typeOption === "AVAILABLE" && (
                <>
                  <div className="mb-3 col-12 col-lg-6">
                    <FormInput
                      name="processingCost"
                      type="number"
                      errors={errors}
                      values={values}
                      touched={touched}
                      label={"Processing Cost"}
                      // leading={
                      //   typeGold?.price &&
                      //   "+ " + currencyFormat(typeGold?.price)
                      // }
                      trailing={
                        <span className="orange fw-bold">{typeGold?.price
                          ? "+ " + currencyFormat(typeGold?.price)
                          : "$"}</span>
                      }
                      disabled={values.typeOption === "CRAFT"}
                      onChange={handleChange}
                    />
                  </div>
                  <div className="mb-3 col-12 col-lg-6">
                    <FormInput
                      name="percentageReduce"
                      type="number"
                      placeHolder="(Optional)"
                      errors={errors}
                      values={values}
                      touched={touched}
                      trailing={"%"}
                      label={"Percentage Reduce"}
                      onChange={handleChange}
                    />
                  </div>
                </>
              )}

              <div className="mb-3 col-12 col-lg-6">
                <FormInput
                  name="totalUnitOfStock"
                  type="number"
                  errors={errors}
                  values={values}
                  touched={touched}
                  trailing={<FaBox />}
                  label={"Stock"}
                  onChange={handleChange}
                />
              </div>
              <div className="mb-3 col-12 col-lg-6">
                <FormInput
                  name="weight"
                  type="number"
                  errors={errors}
                  values={values}
                  touched={touched}
                  trailing={typeGold?.goldUnit}
                  label={"Weight"}
                  onChange={handleChange}
                />
              </div>
            </div>
            <div className="row">
              <div className="mb-3 col-12 col-lg-6">
                <FormSelect
                  name="categoryId"
                  options={categoryData?.data?.map((c) => ({
                    value: c.id,
                    label: c.categoryName,
                  }))}
                  values={values}
                  unselect={true}
                  errors={errors}
                  touched={touched}
                  label={"Category"}
                  trailing={<FaBox />}
                  onChange={handleChange}
                  formik={formik}
                />
              </div>
              <div className="mb-3 col-12 col-lg-6">
                <FormSelect
                  name="typeGoldId"
                  options={typeGoldData?.data?.map((c) => ({
                    value: c.id,
                    label: c.typeName,
                  }))}
                  values={values}
                  errors={errors}
                  unselect={true}
                  trailing={<FaBox />}
                  touched={touched}
                  label={"Type Product"}
                  onChange={(e) => {
                    handleChange(e);
                  }}
                  formik={formik}
                  disabled={values.typeOption === "CRAFT"}
                />
              </div>
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

export default UpdateProduct;
