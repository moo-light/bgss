import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import * as Yup from "yup";

import DOMPurify from "dompurify";
import { Button, Modal } from "react-bootstrap";
import { FaEye } from "react-icons/fa";
import ReactQuill from "react-quill";
import { useNavigate } from "react-router-dom";
import { BASE_POSTIMG, DISCOUNT_REGEX } from "../../../constants/constants";
import { FormError } from "../../../helpers/components/form-error";
import {
  addErrors,
  clearErrors,
} from "../../../helpers/form-validation-helpers";
import {
  formatHTMLToText,
  formatPostHTMLWithDiscount,
} from "../../../helpers/helpers";
import { useCreatePostMutation } from "../../../redux/api/postApi";
import { useGetAllCategoryPostQuery } from "../../../redux/api/postCategoryApi";
import AdminLayout from "../../layout/AdminLayout";
import MetaData from "../../layout/MetaData";

const NewPost = () => {
  const navigate = useNavigate();

  const [imageFile, setImageFile] = useState(null);
  const [newPost, setNewPost] = useState({
    title: "",
    content: "",
    textImg: null,
    categoryPostId: null,
    pinned: false,
  });

  const { title, content, textImg, categoryPostId, pinned } = newPost;

  const [createPost, { isLoading, error, isSuccess }] = useCreatePostMutation();
  const { data: categoryData, isSuccess: haveCategory } =
    useGetAllCategoryPostQuery();

  useEffect(() => {
    if (haveCategory) {
      console.log(categoryData?.data[0].id);
      setNewPost({ ...newPost, categoryPostId: categoryData?.data[0].id });
    }
  }, [haveCategory]);

  const postSchema = Yup.object().shape({
    title: Yup.string().required("Title is required"),
    content: Yup.string()
      .required("Content is required")
      .test(
        "HTML validation",
        "Content is Required",
        (html) => formatHTMLToText(html).length > 0
      ),
    textImg: Yup.string().required("Image is required"),
    categoryPostId: Yup.string().required("Category is required"),
  });

  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success("Post created successfully");
      navigate("/admin/posts");
    }
  }, [error, isSuccess]);

  const [errors, setErrors] = useState({});

  useEffect(() => {
    addErrors(document, errors);
  }, [errors]);

  const submitHandler = (e) => {
    e.preventDefault();
    const postRequest = { ...newPost, textImg: imageFile };
    postSchema
      .validate(postRequest, { abortEarly: false })
      .then(() => {
        createPost({
          ...postRequest,
          content: content,
        });
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
  const onChange = (e) => {
    const value =
      e.target.type === "checkbox" ? e.target.checked : e.target.value;
    setNewPost({ ...newPost, [e.target.name]: value });
    // Optionally reset the error state when the user starts typing
    if (errors[e.target.name]) {
      setErrors({ ...errors, [e.target.name]: undefined });
    }
    clearErrors(document, errors, e.target.name);
  };
  const onChangeImage = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setErrors({ ...errors, textImg: null });
    const reader = new FileReader();
    reader.onload = () => {
      const imageUrl = reader.result.replace(/\\/g, "/"); // Chuẩn hóa URL của ảnh
      setNewPost({ ...newPost, textImg: imageUrl }); // Cập nhật state với URL được chuẩn hóa
      setImageFile(file); // Cập nhật state với URL được chuẩn hóa
    };

    reader.readAsDataURL(file);
  };
  const [discounts, setDiscounts] = useState([]);
  const onChangeContent = (html) => {
    let newContent = html;
    setErrors({ ...errors, content: undefined });
    setDiscounts(newContent?.match(DISCOUNT_REGEX));
    setNewPost({
      ...newPost,
      content: html,
    });
  };
  const removeImage = () => {
    setNewPost({ ...newPost, textImg: null });
    setImageFile(undefined);
  };
  //Modal Box
  const [show, setShow] = useState(false);
  const handleClose = () => setShow(false);

  return (
    <AdminLayout>
      <MetaData title={"Update Post"} />
      <div className="row wrapper">
        <div className="col-10 col-lg-10 mt-5 mt-lg-0">
          <form className="shadow rounded bg-body" onSubmit={submitHandler}>
            <h2 className="mb-4">New Post</h2>
            <div className="mb-3 d-grid gap-2 place-items-center">
              <label htmlFor="textImg_field" className="form-label">
                Text Image
              </label>
              <div className="mb-2">
                {textImg?.length > 0 ? (
                  <>
                    <img
                      src={textImg}
                      alt="txtimg"
                      style={{
                        display: "block",
                        margin: "auto",
                        maxWidth: "40%",
                        height: "auto",
                      }}
                    />
                    <button
                      type="button"
                      className="btn btn-danger btn-sm my-2"
                      onClick={removeImage}
                      style={{
                        display: "block",
                        margin: "auto",
                        maxWidth: "40%",
                        height: "auto",
                      }}
                    >
                      Remove Image
                    </button>
                  </>
                ) : (
                  <></>
                )}
              </div>
              <input
                type="file"
                id="textImg_field"
                className="form-control"
                name="textImg"
                onChange={onChangeImage}
              />
              <FormError name="textImg" errorData={errors} />
            </div>

            <div className="mb-3">
              <label htmlFor="title_field" className="form-label">
                Title
              </label>
              <input
                type="text"
                id="title_field"
                className="form-control"
                name="title"
                value={title}
                onChange={onChange}
              />
              <FormError name="title" errorData={errors} />
            </div>

            <div className="mb-3">
              <label htmlFor="content_field" className="form-label">
                Content:
              </label>

              <ReactQuill
                id="content_field"
                rows="8"
                className={errors?.content && "input-error"}
                name="content"
                placeholder="Add Some Text"
                value={content}
                onChange={onChangeContent}
              />

              <div className="d-flex justify-content-end align-items-center gap-2 mt-2 ">
                <div>Discount: {discounts?.length ?? 0} </div>
                {formatHTMLToText(content).length > 0 && (
                  <button
                    type="button"
                    className="btn m-0"
                    onClick={(e) => setShow(true)}
                  >
                    <FaEye></FaEye>
                  </button>
                )}
              </div>
              <FormError name="content" errorData={errors} />
            </div>
            <div className="row">
              <div className="mb-3 col">
                <label htmlFor="category_field" className="form-label">
                  {" "}
                  Category{" "}
                </label>
                <select
                  className="form-select"
                  id="categoryPostId_field"
                  name="categoryPostId"
                  value={categoryPostId}
                  onChange={onChange}
                >
                  {categoryData?.data?.map((categoryPost, index) => (
                    <option key={index} value={categoryPost.id}>
                      {categoryPost.categoryName}
                    </option>
                  ))}
                </select>
                <FormError name="categoryId" errorData={errors} />
              </div>
            </div>

            <div className="mb-3 form-check">
              <input
                type="checkbox"
                className="form-check-input"
                id="pinned_field"
                name="pinned"
                checked={pinned}
                onChange={onChange}
              />
              <label className="form-check-label" htmlFor="pinned_field">
                Pinned
              </label>
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
        <Modal show={show} onHide={handleClose} size={"xl"}>
          <Modal.Header closeButton>
            <Modal.Title>Preview</Modal.Title>
          </Modal.Header>
          <div className="modal-body">
            {textImg?.length > 0 && (
              <div className="p-1">
                <hr />
                <img
                  className="d-block shadow col-12 m-auto col-md-8 col-lg-6"
                  src={textImg || BASE_POSTIMG}
                  alt={title}
                />
              </div>
            )}
            {/* content */}
            <pre
              className="my-2 d-block "
              dangerouslySetInnerHTML={{
                __html: DOMPurify.sanitize(formatPostHTMLWithDiscount(content)),
              }}
            ></pre>
          </div>
          <Modal.Footer>
            <Button variant="secondary" onClick={handleClose}>
              Close
            </Button>
          </Modal.Footer>
        </Modal>
      </div>
    </AdminLayout>
  );
};

export default NewPost;
