import DOMPurify from "dompurify";
import { MDBDataTable } from "mdbreact";
import React, { useState, useEffect } from "react";
import { toast } from "react-hot-toast";
import { FaThumbtack } from "react-icons/fa";
import { Link } from "react-router-dom";
import {
  formatPostHTMLWithDiscount,
  myDateFormat,
} from "../../../helpers/helpers";
import { getServerImgUrl } from "../../../helpers/image-handler";
import {
  useDeletePostMutation,
  useGetAllPostQuery,
} from "../../../redux/api/postApi";
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

const ListPosts = () => {
  const { data, isLoading, error } = useGetAllPostQuery();
  const [
    deletePost,
    { isLoading: isDeleteLoading, error: deleteError, isSuccess },
  ] = useDeletePostMutation();

  const [open, setOpen] = useState(false);
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
      handleClose();
    }
  }, [error, deleteError, isSuccess]);

  const handleClickOpen = (id) => {
    setPostIdToDelete(id);
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setPostIdToDelete(null);
  };

  const handleDelete = () => {
    deletePost(postIdToDelete);
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
          label: "Image",
          field: "image",
        },
        {
          label: "Title",
          field: "title",
          sort: "asc",
        },
        {
          label: "Content",
          field: "content",
          sort: "asc",
        },
        {
          label: "Category",
          field: "categoryName",
          sort: "asc",
          width: 100,
        },
        {
          label: "Pinned",
          field: "pinned",
          sort: "asc",
        },
        {
          label: "Created At",
          field: "createDate",
          sort: "asc",
        },
        {
          label: "Updated At",
          field: "updateDate",
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
        image: (
          <img
            className="img-fluid object-fit-contain"
            style={{ aspectRatio: 4 / 3 }}
            width="75"
            alt="Post"
            src={getServerImgUrl(post.textImg)}
          />
        ),
        title: (
          <div
            style={{
              maxWidth: "200px",
            }}
            className="line-clamp-3"
          >
            {post.title}
          </div>
        ),
        pinned: post.pinned && (
          <>
            <FaThumbtack className="text-danger d-block m-auto" />
          </>
        ),
        content: (
          <div
            className="line-clamp-4"
            style={{ wordBreak: "break-all", maxWidth: "250px" }}
          >
            {DOMPurify.sanitize(formatPostHTMLWithDiscount(post?.content), {
              ALLOWED_TAGS: [], // Allow no tags
              ALLOWED_ATTR: [], // Allow no attributes
            }).toString()}
          </div>
        ),
        categoryName: post?.categoryPost?.categoryName,
        createDate: myDateFormat(post.createDate),
        updateDate: myDateFormat(post.updateDate),
        deleteDate: myDateFormat(post.deleteDate),
        actions: (
          <div className="d-flex">
            <Link
              to={`/admin/posts/${post?.id}`}
              className="btn btn-outline-primary"
            >
              <i className="fa fa-pencil"></i>
            </Link>
            <button
              aria-label="btn-delete"
              className="btn btn-outline-danger ms-2"
              onClick={() => handleClickOpen(post?.id)}
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
      <>
        <AdminLayout>
          <MetaData title={"Admin Products"} />
          <Loader />
        </AdminLayout>
      </>
    );
  }

  return (
    <AdminLayout>
      <MetaData title={"All Posts"} />

      <h1 className="my-2 px-5">{data?.data?.length} Posts</h1>
      <div className="mb-2 px-5 mt-5 content">
        <Link to="/admin/posts/new" className="btn btn-outline-success me-2">
          <i className="fa fa-plus"></i> Add Post
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
          margin-bottom: 20px;
        }
      `}</style>
      <Dialog open={open} onClose={handleClose}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete this post?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} color="primary">
            Cancel
          </Button>
          <Button
            onClick={handleDelete}
            color="primary"
            disabled={isDeleteLoading}
          >
            Confirm
          </Button>
        </DialogActions>
      </Dialog>
    </AdminLayout>
  );
};

export default ListPosts;
