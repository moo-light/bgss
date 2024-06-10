import React, { useEffect, useState } from "react";
import { MDBDataTable } from "mdbreact";
import { toast } from "react-hot-toast";
import { useSelector } from "react-redux";
import { Link } from "react-router-dom";
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Button,
} from "@mui/material";
import {
  useDeleteCategoryProductMutation,
  useGetCategoryProductsQuery,
} from "../../../redux/api/categoryProductApi";
import AdminLayout from "../../layout/AdminLayout";
import Loader from "../../layout/Loader";
import MetaData from "../../layout/MetaData";

const ListCategoryProducts = () => {
  const { data, isLoading, error, refetch } = useGetCategoryProductsQuery();
  const { user, roles } = useSelector((state) => state.auth);
  const [
    deleteCategoryProduct,
    { isLoading: isDeleteLoading, error: deleteError, isSuccess },
  ] = useDeleteCategoryProductMutation();

  const [open, setOpen] = useState(false);
  const [selectedProductId, setSelectedProductId] = useState(null);

  useEffect(() => {
    if (error) {
      toast.error(error.message); // Display error toast
    }

    if (deleteError) {
      toast.error(deleteError.message); // Display delete error toast
    }

    if (isSuccess) {
      toast.success("Category product deleted"); // Display success toast on delete
      refetch(); // Refetch the data after a successful delete
    }
  }, [error, deleteError, isSuccess, refetch]);

  const handleClickOpen = (id) => {
    setSelectedProductId(id);
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setSelectedProductId(null);
  };

  const handleConfirmDelete = () => {
    deleteCategoryProduct(selectedProductId);
    handleClose();
  };

  const setCategoryProducts = () => {
    const categoriess = {
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
    data?.data.forEach((categories) => {
      categoriess.rows.push({
        id: categories.id,
        categoryName: categories.categoryName,
        actions: (
          <>
            {roles?.includes("ROLE_ADMIN") && (
              <div
                className="d-flex align-items-center"
                style={{ maxWidth: "45px" }}
              >
                <div className="me-2">
                  <Link
                    to={`/admin/categoryProducts/${categories?.id}`}
                    className="btn btn-outline-primary"
                  >
                    <i className="fa fa-pencil"></i>
                  </Link>
                </div>
                <div>
                  <button
                    aria-label="delete"
                    className="btn btn-outline-danger"
                    onClick={() => handleClickOpen(categories?.id)}
                    disabled={isDeleteLoading}
                  >
                    <i className="fa fa-trash"></i>
                  </button>
                </div>
              </div>
            )}
          </>
        ),
      });
    });

    return categoriess;
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
      <MetaData title={"All Category Products"} />
      <h1 className="my-2 px-5">{data?.data?.length} Category Products</h1>
      {roles?.includes("ROLE_ADMIN") && (
        <div className="mb-2 px-5 content mt-5">
          <Link
            to="/admin/categoryProducts/new"
            className="btn btn-outline-success me-2"
          >
            <i className="fa fa-plus"></i> Add Category Product
          </Link>
        </div>
      )}
      <MDBDataTable
        data={setCategoryProducts()}
        className="px-5 content"
        bordered
        striped
        hover
      />
      <style jsx>{`
        .dataTables_wrapper .dataTables_length,
        .dataTables_wrapper .dataTables_filter,
        .dataTables_wrapper .dataTables_info,
        .dataTables_wrapper .dataTables_paginate {
          margin-bottom: 20px; /* Adjust the value as desired */
        }
      `}</style>

      <Dialog
        open={open}
        onClose={handleClose}
        aria-labelledby="alert-dialog-title"
        aria-describedby="alert-dialog-description"
      >
        <DialogTitle id="alert-dialog-title">{"Confirm to delete"}</DialogTitle>
        <DialogContent>
          <DialogContentText id="alert-dialog-description">
            Are you sure you want to delete this product?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose} color="primary">
            No
          </Button>
          <Button onClick={handleConfirmDelete} color="primary" autoFocus>
            Yes
          </Button>
        </DialogActions>
      </Dialog>
    </AdminLayout>
  );
};

export default ListCategoryProducts;
