import { MDBDataTable } from "mdbreact";
import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import { Link } from "react-router-dom";
import {
  useDeleteCategoryPostMutation,
  useGetAllCategoryPostQuery,
} from "../../../redux/api/postCategoryApi";
import AdminLayout from "../../layout/AdminLayout";
import Loader from "../../layout/Loader";
import MetaData from "../../layout/MetaData";
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Button,
} from "@mui/material";

const ListCategoryPosts = () => {
  const { data, isLoading, error, refetch } = useGetAllCategoryPostQuery();
  const [
    deletePost,
    { isLoading: isDeleteLoading, error: deleteError, isSuccess },
  ] = useDeleteCategoryPostMutation();

  const [openDialog, setOpenDialog] = useState(false);
  const [postIdToDelete, setPostIdToDelete] = useState(null);

  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (deleteError) {
      toast.error(deleteError?.data?.message);
    }

    if (isSuccess) {
      toast.success("Post Deleted");
      refetch(); // Refetch the data after a successful delete
    }
  }, [error, deleteError, isSuccess, refetch]);

  const deletePostHandler = (id) => {
    deletePost(id);
  };

  const handleOpenDialog = (id) => {
    setPostIdToDelete(id);
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setPostIdToDelete(null);
  };

  const handleConfirmDelete = () => {
    deletePostHandler(postIdToDelete);
    handleCloseDialog();
  };

  const setPosts = () => {
    const posts = {
      columns: [
        {
          label: "ID",
          field: "id",
          sort: "asc",
        },
        {
          label: "Category Name",
          field: "categoryName",
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
    data?.data?.forEach((post) => {
      posts.rows.push({
        id: post.id,
        categoryName: post?.categoryName,
        actions: (
          <div className="d-flex">
            <Link
              to={`/admin/categoryPosts/${post?.id}`}
              className="btn btn-outline-primary"
            >
              <i className="fa fa-pencil"></i>
            </Link>
            <button
              aria-label="btn-delete"
              className="btn btn-outline-danger ms-2"
              onClick={() => handleOpenDialog(post?.id)}
              disabled={isDeleteLoading}
            >
              <i className="fa fa-trash"></i>
            </button>
          </div>
        ),
      });
    });

    return posts;
  };

  if (isLoading) {
    return (
      <AdminLayout>
        <MetaData title={"All Category Posts"} />
        <Loader />
      </AdminLayout>
    );
  }

  return (
    <AdminLayout>
      <MetaData title={"All Category Posts"} />

      <h1 className="my-2 px-5">{data?.data?.length} Category Posts</h1>
      <div className="mb-2 px-5 content mt-5">
        <Link
          to="/admin/categoryPost/new"
          className="btn btn-outline-success me-2"
        >
          <i className="fa fa-plus"></i> Add Category Post
        </Link>
      </div>
      <MDBDataTable
        className="px-5 content"
        data={setPosts()}
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

      <Dialog
        open={openDialog}
        onClose={handleCloseDialog}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">{"Confirm Deletion"}</DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            Are you sure you want to delete this CategoryPost?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog} color="primary">
            Cancel
          </Button>
          <Button
            onClick={handleConfirmDelete}
            color="primary"
            autoFocus
            disabled={isDeleteLoading}
          >
            Confirm
          </Button>
        </DialogActions>
      </Dialog>
    </AdminLayout>
  );
};

export default ListCategoryPosts;
