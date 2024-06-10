import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import * as Yup from "yup";

import { useFormik } from "formik";
import { useNavigate, useParams } from "react-router-dom";
import ImageInput from "../../helpers/components/form-image-input";
import {
  validateFileSize,
  validateFileType,
} from "../../helpers/form-validation-helpers";
import {
  useGetProductDetailsQuery,
  useUploadProductImagesMutation
} from "../../redux/api/productsApi";
import AdminLayout from "../layout/AdminLayout";
import MetaData from "../layout/MetaData";

const UploadImages = () => {
  const params = useParams();
  const navigate = useNavigate();

  const [uploadProductImages, { isLoading, error, isSuccess,isError }] =
    useUploadProductImagesMutation();

  // const [
  //   deleteProductImage,
  //   { isLoading: isDeleteLoading, error: deleteError },
  // ] = useDeleteProductImageMutation();

  const { data, isSuccess: hasData } = useGetProductDetailsQuery(params?.id);
  const [initialValues, setInitialData] = useState({
    image: [],
  });

  useEffect(() => {
    // if (data?.data.i) {
    //   console.log(data.data);
    //   setUploadedImages(data?.data?.imgUrl);
    // }

    if (isError) {
      toast.error(error?.data?.message);
    }

    // if (deleteError) {
    //   toast.error(deleteError?.data?.message);
    // }

    if (isSuccess) {
      toast.success("Images Uploaded");
      navigate("/admin/products");
    }
  }, [isError, isSuccess]);

  const productImages = Array.from(data?.data?.productImages ?? []);
  // useEffect(()=>{
  //   if (hasData && !isMounted.current) {
  //     console.log(
  //       data?.data?.productImages,
  //       Array.from(data?.data?.productImages)
  //     );

  //     isMounted.current = true;
  //     setInitialData({
  //       id: params?.id,
  //       productName: data?.data?.productName,
  //       description: data?.data?.description,
  //       price: data?.data?.price,
  //       totalUnitOfStock: data?.data?.unitOfStock,
  //       percentageReduce: data?.data?.percentageReduce,
  //       weight: data?.data?.weight,
  //       product_image: productImages,
  //       categoryId: data?.data?.category?.id ?? null,
  //       typeGoldId: data.data.typeGold?.id ?? null,
  //     });
  //     resetForm();
  //   }

  // },[hasData])



  const max = 5;
  const productSchema = Yup.object().shape({
    image: Yup.array()
      .of(
        Yup.mixed()
          .test("fileSize", "File is too large", validateFileSize())
          .test("fileType", "Invalid File type", validateFileType())
      )
      .required("Image is required")
      .min(1, "Image is Required")
      .max(max, `Only ${max} images are allowed`),
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
      uploadProductImages({ id: params?.id, body: values });
    },
  });
  console.log(errors,values)
  return (
    <AdminLayout>
      <MetaData title={"Upload Product Images"} />
      <div className="row wrapper">
        <div className="col-10 col-lg-8 mt-5 mt-lg-0">
          <form className="shadow rounded bg-body" onSubmit={handleSubmit}>
            <h2 className="mb-4">Update Product Images</h2>
            <ImageInput
              name="image"
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
            <button
              id="register_button"
              type="submit"
              className="btn w-100 py-2"
              disabled={isLoading }
            >
              {isLoading  ? "Updating..." : "UPDATE"}
            </button>
          </form>
        </div>
      </div>
    </AdminLayout>
  );
};

export default UploadImages;

// {!!data?.data?.imgUrl && (
//   <div className="row mt-4">
//     <div className="col-md-6 mt-2 m-auto">
//       <div className="card">
//         <img
//           src={data?.data?.imgUrl}
//           alt="Card"
//           className="card-img-top p-2"
//           style={{
//             width: "100%",
//             aspectRatio: "9/6",
//             objectFit: "contain",
//           }}
//         />
//       </div>
//     </div>
//   </div>
// )}
// <div className="mb-3">
//   <label htmlFor="customFile" className="form-label">
//     Choose Images
//   </label>

//   <div className="custom-file">
//     <input
//       ref={fileInputRef}
//       type="file"
//       name="product_images"
//       className="form-control"
//       id="customFile"
//       accept="image/*"
//       onChange={onChange}
//       onClick={handleResetFileInput}
//     />
//   </div>

//   {imagesPreview?.length > 0 && (
//     <div className="new-images my-4">
//       {/* <p className="gold">New Images:</p> */}
//       <div className="row mt-4">
//         {imagesPreview?.map((img) => (
//           <div className="col-md-6 mt-2 m-auto">
//             <div className="card">
//               <img
//                 src={img}
//                 alt="Card"
//                 className="card-img-top p-2"
//                 style={{
//                   width: "100%",
//                   aspectRatio: "9/6",
//                   objectFit: "contain",
//                 }}
//               />
//               <button
//                 aria-label="add Images"
//                 style={{
//                   backgroundColor: "#dc3545",
//                   borderColor: "#dc3545",
//                 }}
//                 type="button"
//                 className="btn btn-block btn-danger cross-button mt-1 py-0"
//                 onClick={() => handleImagePreviewDelete(img)}
//               >
//                 <i className="fa fa-times"></i>
//               </button>
//             </div>
//           </div>
//         ))}
//       </div>
//     </div>
//   )}

//   {uploadedImages?.length > 0 && (
//     <div className="uploaded-images my-4">
//       <p className="text-success">Product Uploaded Images:</p>
//       <div className="row mt-1">
//         {uploadedImages?.map((img) => (
//           <div className="col-md-3 mt-2">
//             <div className="card">
//               <img
//                 src={img?.url}
//                 alt="Card"
//                 className="card-img-top p-2"
//                 style={{
//                   width: "100%",
//                   height: "80px",
//                   objectFit: "contain",
//                 }}
//               />
//               <button
//                 aria-label="delete"
//                 style={{
//                   backgroundColor: "#dc3545",
//                   borderColor: "#dc3545",
//                 }}
//                 className="btn btn-block btn-danger cross-button mt-1 py-0"
//                 type="button"
//                 disabled={isLoading || isDeleteLoading}
//                 onClick={() => deleteImage(img?.public_id)}
//               >
//                 <i className="fa fa-trash"></i>
//               </button>
//             </div>
//           </div>
//         ))}
//       </div>
//     </div>
//   )}
// </div>
