import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Button as MUIButton,
} from "@mui/material";
import React, { useEffect, useRef, useState } from "react";
import { Button } from "react-bootstrap";
import { FaEdit, FaImage, FaTrash } from "react-icons/fa";
import { SUPPORTED_IMAGE_FORMATS } from "../../constants/constants";
import { getDatabaseImgUrl } from "../image-handler";
import { FormError } from "./form-error";
import { clearErrors } from "./form-input";

function ImageInput({
  name,
  multiple = false,
  label,
  max = null,
  errors = {},
  initialValues,
  values = {},
  touched = {},
  onChange,
  formik = {},

  ...others
}) {
  const isMounted = useRef(false);
  useEffect(() => {
    if (!isMounted.current && initialValues) {
      initialValues?.length > 0 &&
        initialValues.forEach((image, index) => {
          if (typeof image === "string") {
            initialValues[index] = {
              file: getDatabaseImgUrl(image),
              preview: image,
            };
            isMounted.current = true;
          }
        });
      setImages(initialValues);
    }
    if (initialValues == null) {
      isMounted.current = true;
    }
  }, [initialValues]);
  const [images, setImages] = useState(initialValues ?? []);
  const previews = images.map((image) => image?.preview);
  const first = 0;

  const [dialogOpen, setDialogOpen] = useState(false);
  const [selectedImageIndex, setSelectedImageIndex] = useState(null);

  if (values) values[name] = images.map((image) => image?.file);

  const handleImageChange = async (event) => {
    if (multiple) {
      const selectedFiles = event.target.files;
      for (let i = 0; i < selectedFiles.length; i++) {
        const file = selectedFiles[i];
        if (!file) return;
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onloadend = () => {
          const imageUrl = reader.result.replace(/\\/g, "/");
          setImages((prev) => {
            prev = [...prev, { file: file, preview: imageUrl }];
            return prev;
          });
        };
        clearErrors(formik, name, errors);
      }
    } else {
      handleIndexChange(first)(event);
    }
  };

  const handleIndexChange = (index) => (e) => {
    e.stopPropagation();
    const file = e.target.files[0];
    if (!file) return;
    const newImages = images;
    setImages([...newImages]);
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onloadend = () => {
      const imageUrl = reader.result.replace(/\\/g, "/");
      const imgObj = {
        file: file,
        preview: imageUrl,
      };
      newImages[index] = imgObj;
      setImages(newImages);
    };
    clearErrors(formik, name, errors);
  };

  const handleRemove = (index) => (e) => {
    if (multiple) {
      const newImages = [...images];
      newImages.splice(index, 1);
      setImages(newImages);
      clearErrors(formik, name, errors);
    } else {
      handleClear(e);
    }
  };

  const handleClear = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setSelectedImageIndex(null);
    setDialogOpen(true);
  };

  const handleDialogClose = () => {
    setDialogOpen(false);
  };

  const handleDialogConfirm = () => {
    setImages([]);
    setDialogOpen(false);
  };

  return (
    <div className="w-100">
      {/* Hidden file input */}
      <input
        type="file"
        name={name}
        multiple={multiple}
        accept="images/*"
        maxLength={max - previews.length}
        className="d-none"
        id="image-input"
        onChange={handleImageChange}
        {...others}
      />

      {/* Label for file input */}
      <div className="form-label">{label}</div>
      {/* Image previews */}
      <section className="col-lg-10 position-relative col-12 m-auto d-grid">
        <label
          className={`image-input-preview d-flex rounded flex-column justify-content-center m-0 bg-light p-4 col-12`}
          aria-invalid={!!errors[name]}
          style={
            !multiple && images.length > 0
              ? {
                  backgroundImage: `url("${images[first]?.preview}")`,
                  backgroundSize: "cover",
                }
              : null
          }
          htmlFor="image-input"
        >
          {(images.length === 0 || multiple) && (
            <>
              <div className="mx-auto">
                <FaImage className="display-6"></FaImage>
              </div>
              <div className="mx-auto">Select your image{multiple && "s"}</div>
            </>
          )}
          {previews.length !== 0 && multiple && (
            <Button
              variant="danger"
              title="Clear Images"
              className="ms-auto position-absolute bottom-0 m-2 end-0 z-1"
              onClick={handleClear}
            >
              Clear
            </Button>
          )}
          {!multiple && images.length > 0 && (
            <div className="position-absolute inset-0 object-fit-cover shadow">
              <input
                className="d-none"
                type="file"
                id={`image${first}`}
                accept={SUPPORTED_IMAGE_FORMATS.join(",")}
                onChange={handleIndexChange(first)}
              />
              <img
                src={`${images[first].preview}`}
                alt={`Preview ${first}`}
                style={{ aspectRatio: 1 }}
                className="img-fluid w-100 opacity-0 h-100 object-fit-cover rounded-2  m-0"
              />
              <div className="m-1 position-absolute gap-1 top-0 end-0">
                <button
                  alt="remove"
                  type="button"
                  className="btn-outline-danger rounded"
                  onClick={handleClear}
                >
                  <FaTrash
                    className="position-relative text-danger"
                    style={{ top: -3 }}
                  />
                </button>
                <button
                  alt="remove"
                  type="button"
                  onClick={(e) => {
                    e.preventDefault();
                    document.querySelector(`#image${first}`).click();
                  }}
                  className="btn-outline-danger rounded"
                >
                  <FaEdit
                    className="position-relative text-dark pe-auto"
                    style={{ top: -3 }}
                  />
                </button>
              </div>
            </div>
          )}
        </label>

        <div className={`d-flex justify-content-between start-0 end-0 top-0`}>
          <div>
            <FormError name={name} errorData={errors} touched={touched} />
          </div>
          {max && multiple && (
            <div className={`ms-auto ${previews.length > max && "redColor"}`}>
              {previews.length} / {max}
            </div>
          )}
        </div>
        {multiple && (
          <div className="d-flex gap-2 overflow-x-auto pt-2 p-2">
            {previews?.map((preview, index) => (
              <div
                key={index}
                className="position-relative shadow"
                style={{ width: "20%", aspectRatio: 1 }}
              >
                <input
                  className="d-none"
                  type="file"
                  id={`image${index}`}
                  accept="image/*"
                  onChange={handleIndexChange(index)}
                />
                <img
                  src={`${preview}`}
                  alt={`Preview ${index}`}
                  style={{ aspectRatio: 1 }}
                  className="img-fluid object-fit-cover rounded-2 "
                />
                <div className="m-1 position-absolute gap-1 top-0 end-0">
                  <button
                    alt="remove"
                    type="button"
                    className="btn-outline-danger rounded"
                    onClick={handleRemove(index)}
                  >
                    <FaTrash
                      className="position-relative text-danger"
                      style={{ top: -3 }}
                    />
                  </button>
                  <button
                    alt="remove"
                    type="button"
                    onClick={(e) => {
                      e.preventDefault();
                      document.querySelector(`#image${index}`).click();
                    }}
                    className="btn-outline-danger rounded"
                  >
                    <FaEdit
                      className="position-relative text-dark pe-auto"
                      style={{ top: -3 }}
                    />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </section>

      <Dialog open={dialogOpen} onClose={handleDialogClose}>
        <DialogTitle>Confirm Clear</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Do you want to clear all images?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <MUIButton onClick={handleDialogClose} color="inherit">
            No
          </MUIButton>
          <MUIButton onClick={handleDialogConfirm} color="primary">
            Yes
          </MUIButton>
        </DialogActions>
      </Dialog>
    </div>
  );
}

export default ImageInput;
