import React, { useState } from "react";
import toast from "react-hot-toast";
import { FaPlus } from "react-icons/fa";
import { useSelector } from "react-redux";
import { useNavigate, useSearchParams } from "react-router-dom";
import "../../css/verifyCi.css";
import {
  useShowSecretKeyQuery,
  useVerifyCiCardImageMutation,
} from "../../redux/api/ciCardApi";
import UserLayout from "../layout/UserLayout";

const VerifyCIForm = () => {
  const [frontImage, setFrontImage] = useState(null);
  const [backImage, setBackImage] = useState(null);
  const [frontKey, setFrontKey] = useState(""); // Key ngẫu nhiên cho ảnh mặt trước
  const [backKey, setBackKey] = useState(""); // Key ngẫu nhiên cho ảnh mặt sau
  const navigate = useNavigate();
  const { user } = useSelector((state) => state.auth);
  const [verifyCiCardImageMutation, { isLoading, isError, error }] =
    useVerifyCiCardImageMutation();

  const {
    isSuccess: hasPublickey,
    isLoading: secretKeyLoading,
    error: secretKeyError,
  } = useShowSecretKeyQuery(user?.userInfo?.id);

  const handleFrontImageChange = (e) => {
    setFrontImage(e.target.files[0]);
    setFrontKey(Date.now()); // Cập nhật key ngẫu nhiên khi chọn ảnh mới
  };

  const handleBackImageChange = (e) => {
    setBackImage(e.target.files[0]);
    setBackKey(Date.now()); // Cập nhật key ngẫu nhiên khi chọn ảnh mới
  };
  const [searchParams] = useSearchParams();

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!frontImage || !backImage) {
      alert("Please select both front and back images.");
      return;
    }
    if (hasPublickey) {
      toast.error(
        "Can't update profile after CiCard are verified and secret key are generated!"
      );
      return;
    }
    const formData = new FormData();
    formData.append("frontImage", frontImage);
    formData.append("backImage", backImage);

    try {
      const result = await verifyCiCardImageMutation({
        frontImage,
        backImage: backImage,
      }).unwrap();
      console.log(result);
      if (result) {
        toast.success("Images uploaded successfully.");
        Object.keys(result).forEach((key) => {
          searchParams.set(key, result[key]);
        });
        navigate("/me/update_profile?" + searchParams.toString());
      }
    } catch (error) {
      console.error("Error uploading images:", error);
      if (error.message === "Bad Request to the vision API")
        return toast.error("Bad Image! Please select your Card Identification");
      toast.error("Failed to upload images. Please try again.");
    }
  };
  const isDisabled = !secretKeyError;
  return (
    <UserLayout>
      <div className="mt-5">
        <h2 className="text-center">Verify Citizen Card Information</h2>
        <form onSubmit={handleSubmit} className="row my-5">
          <div className="col-md-6 text-center">
            <div className="mb-3">
              <label
                htmlFor="frontImage"
                className=" form-control hover active user-select-none  "
              >
                <FaPlus className="position-relative" style={{ bottom: 2 }} />{" "}
                Select Front Image
                <input
                  type="file"
                  id="frontImage"
                  accept="image/*"
                  disabled={isDisabled}
                  className="d-none"
                  key={frontKey} // Đặt key ngẫu nhiên
                  onChange={handleFrontImageChange}
                />
              </label>
            </div>
            {frontImage && (
              <div className="mb-3">
                <label className="form-label">Front Image Preview:</label>
                <div className="image-preview aspect-ratio-16-9">
                  <img
                    src={URL.createObjectURL(frontImage)}
                    alt="Front"
                    className="img-fluid"
                  />
                </div>
                <button
                  className="btn btn-danger mt-2"
                  onClick={() => setFrontImage(null)}
                >
                  Remove Front Image
                </button>
              </div>
            )}
          </div>
          <div className="col-md-6 text-center">
            <div className="mb-3">
              <label
                htmlFor="backImage"
                className="form-control hover active user-select-none "
              >
                <FaPlus className="position-relative" style={{ bottom: 2 }} />{" "}
                Select Back Image
                <input
                  type="file"
                  id="backImage"
                  accept="image/*"
                  disabled={isDisabled}
                  className="d-none"
                  key={backKey} // Đặt key ngẫu nhiên
                  onChange={handleBackImageChange}
                />
              </label>
            </div>
            {backImage && (
              <div className="mb-3">
                <label className="form-label">Back Image Preview:</label>
                <div className="image-preview aspect-ratio-16-9">
                  <img
                    src={URL.createObjectURL(backImage)}
                    alt="Back"
                    className="img-fluid"
                  />
                </div>
                <button
                  className="btn btn-danger mt-2"
                  onClick={() => setBackImage(null)}
                >
                  Remove Back Image
                </button>
              </div>
            )}
          </div>
          <div className="col-12 text-center mt-5">
            <button
              type="submit"
              className="btn btn-primary"
              disabled={isLoading || !frontImage || !backImage}
            >
              {isLoading ? "Uploading..." : "Upload Images"}
            </button>
            {isError && <div className="text-danger">{error.message}</div>}
          </div>
          <div className="small text-center text-50 fw-light mt-3 ">
            Can't update profile after CiCard are verified and secret key are
            generated!
          </div>
        </form>
      </div>
    </UserLayout>
  );
};

export default VerifyCIForm;
