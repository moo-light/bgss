import { MDBDataTable } from "mdbreact";
import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import { Link } from "react-router-dom";
import {
  useDeleteTypeGoldMutation,
  useGetTypeGoldQuery,
} from "../../../redux/api/typeGoldApi";
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

const ListTypeGolds = () => {
  const { data, isLoading, error, refetch } = useGetTypeGoldQuery();

  const [
    deletePost,
    { isLoading: isDeleteLoading, error: deleteError, isSuccess },
  ] = useDeleteTypeGoldMutation();

  const [openDialog, setOpenDialog] = useState(false);
  const [goldIdToDelete, setGoldIdToDelete] = useState(null);

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
    setGoldIdToDelete(id);
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setGoldIdToDelete(null);
  };

  const handleConfirmDelete = () => {
    deletePostHandler(goldIdToDelete);
    handleCloseDialog();
  };

  const setGolds = () => {
    const golds = {
      columns: [
        {
          label: "ID",
          field: "id",
          sort: "asc",
        },
        {
          label: "Type Name",
          field: "typeName",
          sort: "asc",
        },
        {
          label: "Price",
          field: "price",
          sort: "asc",
        },
        {
          label: "Gold Unit",
          field: "goldUnit",
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
    data?.data?.forEach((gold) => {
      golds.rows.push({
        id: gold.id,
        typeName: gold?.typeName,
        price: gold?.price,
        goldUnit: gold?.goldUnit,
        actions: (
          <div className="d-flex">
            <Link
              to={`/admin/typeGolds/${gold?.id}`}
              className="btn btn-outline-primary"
            >
              <i className="fa fa-pencil"></i>
            </Link>
            <button
              aria-label="btn-delete"
              className="btn btn-outline-danger ms-2"
              onClick={() => handleOpenDialog(gold?.id)}
              disabled={isDeleteLoading}
            >
              <i className="fa fa-trash"></i>
            </button>
          </div>
        ),
      });
    });

    return golds;
  };

  if (isLoading) {
    return (
      <AdminLayout>
        <MetaData title={"All Type Golds"} />
        <Loader />
      </AdminLayout>
    );
  }

  return (
    <AdminLayout>
      <MetaData title={"All Type Golds"} />

      <h1 className="my-2 px-5">{data?.data?.length} Type Golds</h1>
      <div className="mb-2 px-5 content mt-5">
        <Link
          to="/admin/typeGolds/new"
          className="btn btn-outline-success me-2"
        >
          <i className="fa fa-plus"></i> Add Type Gold
        </Link>
      </div>
      <MDBDataTable
        className="px-5 content"
        data={setGolds()}
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
            Are you sure you want to delete this Type Gold?
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

export default ListTypeGolds;
