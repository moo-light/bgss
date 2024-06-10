import ClipboardJS from "clipboard";
import React, { useState } from "react";
import Button from "react-bootstrap/Button";
import Modal from "react-bootstrap/Modal";
import { toast } from "react-hot-toast";
import { FaCamera, FaCheck, FaMoon, FaSun, FaTrash } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import "react-toastify/dist/ReactToastify.css";
import { phoneFormat } from "../../helpers/helpers";
import { getServerImgUrl } from "../../helpers/image-handler";
import { useShowSecretKeyQuery } from "../../redux/api/ciCardApi";
import { useUploadAvatarMutation } from "../../redux/api/userApi";
import { toggleDarkTheme } from "../../redux/features/themeSlice";
import MetaData from "../layout/MetaData";
import UserLayout from "../layout/UserLayout";
import UserStorage from "../layout/UserStorage";

const Profile = () => {
  const { user, roles } = useSelector((state) => state.auth);
  const { mode } = useSelector((state) => state.theme);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const toggle = () => {
    dispatch(toggleDarkTheme());
  };
  const [avatarImage, setImage] = useState({
    preview: user?.userInfo?.avatarUrl,
  });
  const [
    uploadAvatar,
    { isLoading: uploadLoading, error: uploadError, isSuccess: uploadSuccess },
  ] = useUploadAvatarMutation();
  const submitHandler = (e) => {
    e.preventDefault();
    const userData = {
      avatar: avatarImage?.file,
    };
    uploadAvatar(userData)
      .unwrap()
      .then(async (result) => {
        navigate("?", {
          replace: false,
        });
        clearImage(
          getServerImgUrl(result.data.imgUrl + "?" + new Date().toISOString())
        );
      });
  };
  const handleSelectImage = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => {
      setImage({ preview: reader.result, file });
    };
    reader.readAsDataURL(file);
  };
  const clearImage = (url) => {
    setImage((prev) => {
      return {
        preview:
          url ?? user?.userInfo?.avatarUrl + `?t=${new Date().toISOString()}`,
      };
    });
  };

  const {
    data: publicKey,
    isLoading: secretKeyLoading,
    error: secretKeyError,
  } = useShowSecretKeyQuery(user?.userInfo?.id);


  const [showModal, setShowModal] = useState(false);
  const [showFullKey, setShowFullKey] = useState(false);

  const handleShowModal = () => {
    setShowModal(true);
    setShowFullKey(false); // Đảm bảo rằng khi modal mở, chế độ xem đầy đủ được đặt lại thành false
  };
  const handleCloseModal = () => setShowModal(false);

  const handleCopyToClipboard = () => {
    if (!publicKey) return;
    const keyToCopy = publicKey.publicKey;
    const clipboard = new ClipboardJS(".copy-btn", {
      text: function () {
        return keyToCopy;
      },
    });
    clipboard.on("success", function (e) {
      toast.success("Public key copied to clipboard!");
      clipboard.destroy();
    });
    clipboard.on("error", function (e) {
      toast.error("Failed to copy public key to clipboard!");
    });
  };

  return (
    <UserLayout>
      <div
        onClick={toggle}
        aria-label="toggle"
        id={mode}
        className="nav-item btn position-absolute end-0 top-0 border-0"
      >
        {mode === "light" ? <FaSun /> : <FaMoon />}
      </div>
      <MetaData title={"Your Profile"} />
      <div className="row justify-content-around mt-4 user-info ">
        <div className="col-auto position-relative">
          <figure className="avatar avatar-profile position-relative shadow user-select-none">
            <img
              className="rounded-circle img-fluid avatar "
              src={avatarImage?.preview}
              alt={user?.username}
            />
            <label
              id="select-image"
              htmlFor="select-image-input"
              className="top-0 start-0 end-0 bottom-0 position-absolute bg-dark rounded-circle d-flex hover"
            >
              <div className="content m-auto d-flex justify-content-center   flex-column text-light ">
                <FaCamera
                  className="m-auto"
                  style={{
                    width: "25%",
                    height: "25%",
                  }}
                />
                <p>Select an Image</p>
              </div>

              <input
                id="select-image-input"
                type="file"
                accept="image/*"
                onChange={handleSelectImage}
                className="d-none"
              />
            </label>
            {avatarImage?.file && (
              <>
                <button
                  type="button"
                  className="btn btn-danger "
                  onClick={clearImage}
                >
                  <FaTrash />
                </button>
                <button
                  type="submit"
                  className="btn btn-primary ms-auto float-end rounded ratio-1x1  "
                  disabled={uploadLoading}
                  onClick={submitHandler}
                >
                  <FaCheck />
                </button>
              </>
            )}
          </figure>
          <hr></hr>

          {roles?.includes("ROLE_CUSTOMER") && (
            <>
              <UserStorage options="true" />
              <hr></hr>
              <button
                type="button"
                className="btn btn-primary"
                style={{ marginLeft: "50px" }}
                onClick={handleShowModal}
              >
                Show Secret Key
              </button>
            </>
          )}

          <hr></hr>
        </div>
        <div className="col-12 col-md-5 ">
          <div className="user-info-container">
            <div className="user-info-section">
              <h4>Full Name</h4>
              <p>{`${user?.userInfo?.firstName} ${user?.userInfo?.lastName}`}</p>
            </div>
            <div className="user-info-section">
              <h4>Email Address</h4>
              <p>{user?.email}</p>
            </div>
            <div className="user-info-section">
              <h4>Phone Number</h4>
              <p>{phoneFormat(user?.userInfo?.phoneNumber)}</p>
            </div>

            <div className="user-info-section">
              <h4>Address</h4>
              <p>{user?.userInfo?.address}</p>
            </div>
            <div className="user-info-section">
              <h4>Date of Birth</h4>
              <p>{user?.userInfo?.doB}</p>
            </div>
            {/* <div className="user-info-section">
              <h4>Cart Number</h4>
              <p>{user?.userInfo?.ciCard}</p>
            </div> */}
          </div>
        </div>
      </div>

      {/* Modal */}
      <Modal show={showModal} onHide={handleCloseModal}>
        <Modal.Header closeButton>
          <Modal.Title>Public Key</Modal.Title>
        </Modal.Header>

        <Modal.Body style={{ maxHeight: "60vh", overflowY: "auto" }}>
          {publicKey ? (
            <>
              {showFullKey ? (
                <p>{publicKey.publicKey}</p>
              ) : (
                <p>{publicKey.publicKey.slice(0, 40)}...</p>
              )}
              {/* {!showFullKey && (
                <Button variant="link" onClick={() => setShowFullKey(true)}>
                  See More
                </Button>
              )} */}
            </>
          ) : (
            <p>
              No public key available. Please verify CICard to get your
              publicKey.
            </p>
          )}
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={handleCloseModal}>
            Close
          </Button>
          {publicKey && (
            <Button
              variant="primary"
              className="copy-btn"
              onClick={handleCopyToClipboard}
            >
              Copy
            </Button>
          )}
        </Modal.Footer>
      </Modal>
    </UserLayout>
  );
};

export default Profile;
