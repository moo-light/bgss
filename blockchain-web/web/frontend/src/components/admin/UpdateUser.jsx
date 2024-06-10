import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";

import { useNavigate, useParams } from "react-router-dom";
import MetaData from "../layout/MetaData";

import {
  useGetUserDetailsQuery,
  useLockUserMutation,
} from "../../redux/api/userApi";
import AdminLayout, { AdminLayoutLoader } from "../layout/AdminLayout";

const UpdateUser = () => {
  const [active, setActive] = useState(true);
  const navigate = useNavigate();
  const params = useParams();

  const { data, isLoading } = useGetUserDetailsQuery(params?.id);

  const [lockUser, { error, isSuccess }] = useLockUserMutation();

  useEffect(() => {
    if (data?.data) {
      setActive(data?.data?.active);
    }
  }, [data]);
  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (isSuccess) {
      toast.success("User Updated");
      navigate("/admin/users");
    }
  }, [error, isSuccess, navigate]);

  const submitHandler = (e) => {
    e.preventDefault();
    lockUser({ id: params?.id, body: { isActive: !active } });
  };
  const title = "Update User Status";
  if (isLoading) {
    return <AdminLayoutLoader title={title} />;
  }
  return (
    <AdminLayout>
      <MetaData title={title} />
      <div className="row wrapper">
        <div className="col-10 col-lg-8">
          <form className="shadow-lg   ">
            <h2 className="mb-4 position-relative">
              Update User Status
              <div className="position-absolute top-0 end-0">
                <button
                  aria-label="lockUser"
                  type="submit"
                  title={active ? "Lock user" : "Unlock user"}
                  className={`btn ${active ? "btn-danger" : "btn-success"} m-0`}
                  onClick={submitHandler}
                >
                  <i className={`fas ${active ? "fa-lock" : "fa-unlock"}`} />
                </button>
              </div>
            </h2>

            <div className="mb-3">
              <label htmlFor="name_field" className="form-label">
                Name
              </label>
              <input
                type="text"
                id="name_field"
                className="form-control"
                name="name"
                value={`${data?.data?.userInfo?.firstName} ${data?.data?.userInfo?.lastName}`}
                readOnly
              />
            </div>

            <div className="mb-3">
              <label htmlFor="email_field" className="form-label">
                Email
              </label>
              <input
                type="email"
                id="email_field"
                className="form-control"
                name="email"
                value={data?.data?.email}
                readOnly
              />
            </div>

            <div className="mb-3">
              <label htmlFor="role_field" className="form-label">
                Role
              </label>
              <ul>
                {data?.data?.roles?.map((role, index) => (
                  <li key={index}>{role.name}</li>
                ))}
              </ul>
            </div>
          </form>
        </div>
      </div>
    </AdminLayout>
  );
};

export default UpdateUser;

// import React, { useEffect, useState } from "react";
// import Loader from "../layout/Loader";
// import { toast } from "react-hot-toast";

// import { useNavigate, useParams } from "react-router-dom";
// import MetaData from "../layout/MetaData";

// import AdminLayout from "../layout/AdminLayout";
// import {
//   useGetUserDetailsQuery,
//   useUpdateUserMutation,
// } from "../../redux/api/userApi";

// const UpdateUser = () => {
//   const [name, setName] = useState("");
//   const [email, setEmail] = useState("");
//   const [roles, setRole] = useState("");

//   const navigate = useNavigate();
//   const params = useParams();

//   const { data } = useGetUserDetailsQuery(params?.id);

//   const [updateUser, { error, isSuccess }] = useUpdateUserMutation();
//   console.log(data?.data);
//   useEffect(() => {
//     if (data?.data) {
//       setName(
//         `${data?.data?.userInfo?.firstName} ${data?.data?.userInfo?.lastName}`
//       );
//       setEmail(data?.data?.email);
//       setRole(data?.data?.roles);
//     }
//   }, [data]);

//   useEffect(() => {
//     if (error) {
//       toast.error(error?.data?.message);
//     }

//     if (isSuccess) {
//       toast.success("User Updated");
//       navigate("/admin/users");
//     }
//   }, [error, isSuccess]);

//   const submitHandler = (e) => {
//     e.preventDefault();

//     const userData = {
//       name,
//       email,
//       roles,
//     };

//     updateUser({ id: params?.id, body: userData });
//   };

//   return (
//     <AdminLayout>
//       <MetaData title={"Update User"} />
//       <div className="row wrapper">
//         <div className="col-10 col-lg-8">
//           <form className="shadow-lg" onSubmit={submitHandler}>
//             <h2 className="mb-4">Update User</h2>

//             <div className="mb-3">
//               <label htmlFor="name_field" className="form-label">
//                 Name
//               </label>
//               <input
//                 type="name"
//                 id="name_field"
//                 className="form-control"
//                 name="name"
//                 value={name}
//                 onChange={(e) => setName(e.target.value)}
//               />
//             </div>

//             <div className="mb-3">
//               <label htmlFor="email_field" className="form-label">
//                 Email
//               </label>
//               <input
//                 type="email"
//                 id="email_field"
//                 className="form-control"
//                 name="email"
//                 value={email}
//                 onChange={(e) => setEmail(e.target.value)}
//               />
//             </div>

//             <div className="mb-3">
//               <label htmlFor="role_field" className="form-label">
//                 Role
//               </label>
//               <select
//                 id="role_field"
//                 className="form-select"
//                 name="role"
//                 value={roles}
//                 onChange={(e) => setRole(e.target.value)}
//               >
//                 <option value="ROLE_USER">user</option>
//                 <option value="ROLE_ADMIN">admin</option>
//                 <option value="ROLE_STAFF">staff</option>
//               </select>
//             </div>

//             <button type="submit" className="btn update-btn w-100 py-2">
//               Update
//             </button>
//           </form>
//         </div>
//       </div>
//     </AdminLayout>
//   );
// };

// export default UpdateUser;
