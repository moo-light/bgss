import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import { useSelector } from "react-redux";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useShowSecretKeyQuery } from "../../redux/api/ciCardApi";
import { useUpdateProfileMutation } from "../../redux/api/userApi";
import MetaData from "../layout/MetaData";
import UserLayout from "../layout/UserLayout";

const UpdateProfile = () => {
  const [firstName, setName] = useState("");
  const [lastName, setLastName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [doB, setDob] = useState("");
  const [address, setAddress] = useState("");
  const [ciCard, setCiCard] = useState("");

  const [searchParams] = useSearchParams();

  const navigate = useNavigate();

  const [updateProfile, { isLoading, error, isSuccess }] =
    useUpdateProfileMutation();

  const { user } = useSelector((state) => state.auth);

  const {
    isSuccess: hasPublickey,
    isLoading: secretKeyLoading,
    error: secretKeyError,
  } = useShowSecretKeyQuery(user?.userInfo?.id);

  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success("User Updated");
      setTimeout(() => {
        navigate("/me/profile");
      }, 3000);
    }
  }, [error, isSuccess]);

  useEffect(() => {
    if (user && !error) {
      setName(user?.userInfo?.firstName);
      setLastName(user?.userInfo?.lastName);
      setPhoneNumber(user?.userInfo?.phoneNumber);
      setDob(user?.userInfo?.doB);
      setAddress(user?.userInfo?.address);
      setCiCard(user?.userInfo?.ciCard);
      if (searchParams.size > 0) {
        setName(searchParams.get("firstName"));
        setLastName(searchParams.get("lastName"));
        // setPhoneNumber(user?.userInfo?.phoneNumber);
        setDob(searchParams.get("dob"));
        setAddress(searchParams.get("address"));
        setCiCard(searchParams.get("ciCard"));
      }
    }
  }, [user]);
  const readOnly = !secretKeyError;
  useEffect(() => {}, [searchParams]);
  const submitHandler = async (e) => {
    if (isSuccess) return;
    if (hasPublickey) {
      toast.error(
        "Can't update profile after CiCard are verified and secret key are generated!"
      );
      return;
    }
    e.preventDefault();

    const userData = {
      firstName,
      lastName,
      phoneNumber,
      doB,
      address,
      ciCard,
    };
    updateProfile(userData);
  };

  return (
    <UserLayout>
      <MetaData title={"Update Profile"} />
      <div className="row wrapper justify-content-center">
        <div className=" col-12 col-lg-8 ">
          <form className="shadow rounded bg-body" onSubmit={submitHandler}>
            <h2 className="mb-4 text-center">Update Profile</h2>

            <div className="mb-3">
              <label htmlFor="name_field" className="form-label">
                {" "}
                First Name{" "}
              </label>
              <input
                type="text"
                id="name_field"
                className="form-control"
                name="name"
                readOnly={readOnly}
                value={firstName}
                onChange={(e) => setName(e.target.value)}
              />
            </div>

            <div className="mb-3">
              <label htmlFor="lastName_field" className="form-label">
                {" "}
                Last Name{" "}
              </label>
              <input
                type="text"
                id="lastName_field"
                className="form-control"
                name="lastName"
                readOnly={readOnly}
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
              />
            </div>

            <div className="mb-3">
              <label htmlFor="phoneNumber_field" className="form-label">
                {" "}
                Phone Number{" "}
              </label>
              <input
                type="tel"
                id="phoneNumber_field"
                className="form-control"
                name="phoneNumber"
                readOnly={readOnly}
                value={phoneNumber}
                onChange={(e) => setPhoneNumber(e.target.value)}
              />
            </div>

            <div className="mb-3">
              <label htmlFor="dob_field" className="form-label">
                {" "}
                Date of Birth{" "}
              </label>
              <input
                type="date"
                required
                id="dob_field"
                className="form-control"
                name="dob"
                readOnly={readOnly}
                value={doB}
                onChange={(e) => setDob(e.target.value)}
              />
            </div>

            <div className="mb-3">
              <label htmlFor="address_field" className="form-label">
                {" "}
                Address{" "}
              </label>
              <input
                type="text"
                id="address_field"
                className="form-control"
                name="address"
                readOnly={readOnly}
                value={address}
                onChange={(e) => setAddress(e.target.value)}
              />
            </div>
            <button
              type="submit"
              className="btn update-btn w-100"
              disabled={isLoading || readOnly}
            >
              {isLoading ? "Updating..." : "Update"}
            </button>
            <div className="small text-end text-50 fw-light mt-3 ">
              Can't update profile after CiCard are verified and secret key are
              generated!
            </div>
          </form>
        </div>
      </div>
    </UserLayout>
  );
};

export default UpdateProfile;
