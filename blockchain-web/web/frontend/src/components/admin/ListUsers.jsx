import { MDBDataTable } from "mdbreact";
import React, { useEffect } from "react";
import { toast } from "react-hot-toast";
import { Link } from "react-router-dom";
import MetaData from "../layout/MetaData";

import AdminLayout, { AdminLayoutLoader } from "../layout/AdminLayout";

import { BASE_AVATAR } from "../../constants/constants";
import {
  useDeleteUserMutation,
  useGetAdminUsersQuery,
} from "../../redux/api/userApi";

const ListUsers = () => {
  const { data, isLoading, error } = useGetAdminUsersQuery();

  const [
    deleteUser,
    { error: deleteError, isLoading: isDeleteLoading, isSuccess },
  ] = useDeleteUserMutation();

  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (deleteError) {
      toast.error(deleteError?.data?.message);
    }

    if (isSuccess) {
      toast.success("User Deleted");
    }
  }, [error, deleteError, isSuccess]);

  const deleteUserHandler = (id) => {
    deleteUser(id);
  };

  const setUsers = () => {
    const users = {
      columns: [
        {
          label: "ID",
          field: "id",
          sort: "asc",
        },
        {
          label: "Avatar",
          field: "imgUrl",
          sort: "asc",
        },
        {
          label: "Name",
          field: "name",
          sort: "asc",
        },
        {
          label: "Email",
          field: "email",
          sort: "asc",
        },
        {
          label: "Role",
          field: "role",
          sort: "asc",
        },
        {
          label: "Status",
          field: "status",
          sort: "asc",
        },
        {
          label: "Actions",
          field: "actions",
          sort: "asc",
        },
      ],
      rows: [],
    };
    // console.log(data);
    data?.data?.forEach((user) => {
      users.rows.push({
        id: user?.id,
        name: `${user?.userInfo?.firstName} ${user?.userInfo?.lastName}`,
        imgUrl: (
          <img
            className="rounded-circle border-1 "
            style={{ width: 50, aspectRatio: 1 }}
            src={user?.userInfo?.avatarUrl || BASE_AVATAR}
            alt=""
          />
        ),
        email: user?.email,
        role: user?.roles?.map((role) => role.name).join(", "),
        status: user?.active ? "Active" : "Inactive",
        actions: (
          <div className="d-flex">
            <Link
              to={`/admin/users/${user?.id}`}
              className="btn btn-outline-primary"
            >
              <i className="fa fa-pencil"></i>
            </Link>
            {/* <button
              aria-label="delete"
              className="btn btn-outline-danger ms-2"
              onClick={() => deleteUserHandler(user?.id)}
              disabled={isDeleteLoading}
            >
              <i className="fa fa-trash"></i>
            </button> */}
          </div>
        ),
      });
    });

    return users;
  };
  const title = "All Users";
  if (isLoading) {
    return <AdminLayoutLoader title={title} />;
  }

  return (
    <AdminLayout>
      <MetaData title={title} />

      <h1 className="my-2 px-5">{data?.data?.length} Users</h1>

      <MDBDataTable
        data={setUsers()}
        className="px-5 content mt-5"
        bordered
        striped
        hover
        responsive
      />
      <style jsx>{`
        .dataTables_wrapper .dataTables_length,
        .dataTables_wrapper .dataTables_filter,
        .dataTables_wrapper .dataTables_info,
        .dataTables_wrapper .dataTables_paginate {
          margin-bottom: 20px; /* Điều chỉnh giá trị theo ý muốn của bạn */
        }
      `}</style>
    </AdminLayout>
  );
};

export default ListUsers;

// import { MDBDataTable } from "mdbreact";
// import React, { useEffect } from "react";
// import { toast } from "react-hot-toast";
// import { Link } from "react-router-dom";
// import Loader from "../layout/Loader";
// import MetaData from "../layout/MetaData";

// import AdminLayout from "../layout/AdminLayout";

// import {
//   useDeleteUserMutation,
//   useGetAdminUsersQuery,
// } from "../../redux/api/userApi";

// const ListUsers = () => {
//   const { data, isLoading, error } = useGetAdminUsersQuery();

//   const [
//     deleteUser,
//     { error: deleteError, isLoading: isDeleteLoading, isSuccess },
//   ] = useDeleteUserMutation();

//   useEffect(() => {
//     if (error) {
//       toast.error(error?.data?.message);
//     }

//     if (deleteError) {
//       toast.error(deleteError?.data?.message);
//     }

//     if (isSuccess) {
//       toast.success("User Deleted");
//     }
//   }, [error, deleteError, isSuccess]);

//   const deleteUserHandler = (id) => {
//     deleteUser(id);
//   };

//   const setUsers = () => {
//     const users = {
//       columns: [
//         {
//           label: "ID",
//           field: "id",
//           sort: "asc",
//         },
//         {
//           label: "Name",
//           field: "name",
//           sort: "asc",
//         },
//         {
//           label: "Email",
//           field: "email",
//           sort: "asc",
//         },
//         {
//           label: "Role",
//           field: "role",
//           sort: "asc",
//         },
//         {
//           label: "Actions",
//           field: "actions",
//           sort: "asc",
//         },
//       ],
//       rows: [],
//     };

//     data?.data?.forEach((user) => {
//       users.rows.push({
//         id: user?.id,
//         name: `${user?.firstName} ${user?.lastName}`,
//         email: user?.email,
//         role: user?.roles,
//         actions: (
//           <>
//             <Link
//               to={`/admin/users/${user?.id}`}
//               className="btn btn-outline-primary"
//             >
//               <i className="fa fa-pencil"></i>
//             </Link>

//             <button
//               aria-label="delete"
//               className="btn btn-outline-danger ms-2"
//               onClick={() => deleteUserHandler(user?.id)}
//               disabled={isDeleteLoading}
//             >
//               <i className="fa fa-trash"></i>
//             </button>
//           </>
//         ),
//       });
//     });

//     return users;
//   };

//   if (isLoading) return <Loader />;

//   return (
//     <AdminLayout>
//       <MetaData title={"All Users"} />

//       <h1 className="my-5">{data?.users?.length} Users</h1>

//       <MDBDataTable data={setUsers()} className="px-3" bordered striped hover />
//     </AdminLayout>
//   );
// };

// export default ListUsers;
