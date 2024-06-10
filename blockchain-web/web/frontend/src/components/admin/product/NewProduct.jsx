import React, { useEffect } from "react";
import { toast } from "react-hot-toast";
import * as Yup from "yup";

import { useFormik } from "formik";
import { FaBox } from "react-icons/fa";
import { MdOutlineAttachMoney } from "react-icons/md";
import { useNavigate, useSearchParams } from "react-router-dom";
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
  useCreateProductMutation,
  useGetCategoriesQuery,
} from "../../../redux/api/productsApi";
import { useGetTypeGoldQuery } from "../../../redux/api/typeGoldApi";
import AdminLayout from "../../layout/AdminLayout";
import MetaData from "../../layout/MetaData";

const typeOptions = ["AVAILABLE", "CRAFT"];

const NewProduct = () => {
  const navigate = useNavigate();

  const [createProduct, { isLoading, error, isSuccess, isError }] =
    useCreateProductMutation();
  const { data: categoryData } = useGetCategoriesQuery();
  const { data: typeGoldData } = useGetTypeGoldQuery();

  // Find the ID for 24k gold
  const defaultTypeGoldId = typeGoldData?.data.find(
    (gold) => gold.typeName === "24k gold"
  )?.id;

  const [searchParams, setSearchParams] = useSearchParams();

  const initialValues = {
    productName: "",
    processingCost: null,
    description: "",
    totalUnitOfStock: "",
    product_image: null,
    categoryId: null,
    typeGoldId: null,
    typeOption: searchParams.get("type-product") ?? "AVAILABLE",
  };

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
  const formik = useFormik({
    initialValues: initialValues,
    validationSchema: productSchema,
    validateOnBlur: false,
    validateOnMount: false,
    validateOnChange: false,
    onSubmit: (values) => {
      if (values.typeOption === "CRAFT") {
        values.typeGoldId = defaultTypeGoldId;
        values.processingCost = null;
      }
      createProduct(values);
    },
  });
  useEffect(() => {
    setSearchParams({ "type-product": formik.values.typeOption });
    if (formik.values.typeOption === "CRAFT") {
      formik.setFieldValue("processingCost", null);
      formik.setFieldValue("typeGoldId", defaultTypeGoldId);
    }
  }, [formik.values.typeOption]);

  useEffect(() => {
    if (isError) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success("Product created!");
      navigate("/admin/products");
    }
  }, [isError, isSuccess]);

  const typeGold = typeGoldData?.data?.find(
    (tg) => formik.values.typeGoldId == tg.id
  );
  return (
    <AdminLayout>
      <MetaData title={"Create new Product"} />
      <div className="row wrapper">
        <div className="col-10 col-lg-10 mb-5">
          <form
            className="shadow rounded bg-body"
            onSubmit={formik.handleSubmit}
          >
            <h2 className="mb-4">New Product</h2>

            <div className="m-auto">
              <ImageInput
                name="product_image"
                label="Product Images"
                max={max}
                formik={formik}
                values={formik.values}
                errors={formik.errors}
                touched={formik.touched}
                onChange={formik.handleChange}
                multiple
              />
            </div>
            <FormInput
              name="productName"
              errors={formik.errors}
              values={formik.values}
              touched={formik.touched}
              label={"Product Name"}
              onChange={formik.handleChange}
            />
            <TextInput
              name="description"
              errors={formik.errors}
              values={formik.values}
              formik={formik}
              touched={formik.touched}
              label={"Description"}
              onChange={formik.handleChange}
            />
            <h4 className="my-3">Product Information </h4>
            <div className="mb-3 col-12 col-lg-6 ">
              <FormSelect
                name="typeOption"
                options={[
                  { value: "AVAILABLE", label: "Available" },
                  { value: "CRAFT", label: "Craft" },
                ]}
                values={formik.values}
                errors={formik.errors}
                touched={formik.touched}
                label={"Type Option"}
                onChange={(e) => {
                  formik.handleChange(e);
                }}
              />
            </div>
            {formik.values.typeOption === "AVAILABLE" && (
              <div className="mb-3 col-12 col-lg-6">
                <FormInput
                  name="processingCost"
                  type="number"
                  errors={formik.errors}
                  values={formik.values}
                  touched={formik.touched}
                  label={"Processing Cost"}
                  trailing={<MdOutlineAttachMoney />}
                  // trailing={
                  //   <span className="orange fw-bold">
                  //     {typeGold?.price
                  //       ? "+ " + currencyFormat(typeGold?.price)
                  //       : "$"}
                  //   </span>
                  // }
                  disabled={formik.values.typeOption === "CRAFT"}
                  onChange={formik.handleChange}
                />
              </div>
            )}
            <div className="row">
              <div className="mb-3 col-12 col-lg-6">
                <FormInput
                  name="totalUnitOfStock"
                  type="number"
                  errors={formik.errors}
                  values={formik.values}
                  touched={formik.touched}
                  trailing={<FaBox />}
                  label={"Stock"}
                  onChange={formik.handleChange}
                />
              </div>
              <div className="mb-3 col-12 col-lg-6">
                <FormInput
                  name="weight"
                  type="number"
                  errors={formik.errors}
                  values={formik.values}
                  touched={formik.touched}
                  trailing={typeGold?.goldUnit}
                  label={"Weight"}
                  onChange={formik.handleChange}
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
                  values={formik.values}
                  unselect={true}
                  errors={formik.errors}
                  touched={formik.touched}
                  label={"Category"}
                  trailing={<FaBox />}
                  onChange={formik.handleChange}
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
                  values={formik.values}
                  errors={formik.errors}
                  unselect={true}
                  trailing={<FaBox />}
                  touched={formik.touched}
                  label={"Type Product"}
                  onChange={(e) => {
                    formik.handleChange(e);
                  }}
                  formik={formik}
                  disabled={formik.values.typeOption === "CRAFT"}
                />
              </div>
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

export default NewProduct;
