import React, { useEffect, useState } from "react";
import { MDBDataTable } from "mdbreact";
import { Modal, OverlayTrigger, Tooltip } from "react-bootstrap";
import { toast } from "react-hot-toast";
import { Link } from "react-router-dom";
import { discountTable } from "../../../constants/constants";
import DiscountCard from "../../../helpers/components/discount-card";
import { myDateFormat } from "../../../helpers/helpers";
import {
  useDeleteDiscountMutation,
  useGetDiscountsQuery,
} from "../../../redux/api/discountApi";
import AdminLayout from "../../layout/AdminLayout";
import MetaData from "../../layout/MetaData";
import {
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Button,
} from "@mui/material";

const ListDiscounts = () => {
  const { data, isLoading, error, refetch } = useGetDiscountsQuery();
  const [
    deleteDiscount,
    { isLoading: isDeleteLoading, error: deleteError, isSuccess },
  ] = useDeleteDiscountMutation();

  const [show, setShow] = useState(false);
  const [discount, setDiscount] = useState({});
  const [dialogOpen, setDialogOpen] = useState(false);
  const [discountIdToDelete, setDiscountIdToDelete] = useState(null);

  useEffect(() => {
    if (error) {
      toast.error(error.message); // Display error message
    }

    if (deleteError) {
      toast.error(deleteError.message); // Display delete error message
    }

    if (isSuccess) {
      toast.success("Discount Deleted"); // Display success message when discount is deleted
      handleDialogClose();
      refetch(); // Refetch the data to update the list
    }
  }, [error, deleteError, isSuccess, refetch]);

  const handleDialogOpen = (id) => {
    setDiscountIdToDelete(id);
    setDialogOpen(true);
  };

  const handleDialogClose = () => {
    setDialogOpen(false);
    setDiscountIdToDelete(null);
  };

  const handleDelete = () => {
    deleteDiscount(discountIdToDelete);
  };

  const setDiscounts = () => {
    const discounts = discountTable();
    data?.data.forEach((discount) => {
      // Ensure consistent data handling
      discounts.rows.push({
        id: discount.id,
        code: discount.code,
        percentage: discount.percentage,
        defaultQuantity: discount.defaultQuantity,
        dateCreate: myDateFormat(new Date(discount.dateCreate)),
        maxReduce: discount.minPrice,
        minPrice: discount.maxReduce,
        dateExpire: (
          <OverlayTrigger
            placement="right-end"
            overlay={<Tooltip>{discount.expire ? "Expired" : "Valid"}</Tooltip>}
          >
            <span className={discount.expire && "redColor"}>
              {myDateFormat(new Date(discount.dateExpire))}
            </span>
          </OverlayTrigger>
        ),
        actions: (
          <div className="d-flex">
            <button
              aria-label="btn-view"
              className="btn btn-outline-primary ms-2"
              onClick={() => {
                setShow(true);
                setDiscount(discount);
              }}
            >
              <i className="fa fa-info-circle"></i>
            </button>
            <button
              aria-label="btn-delete"
              className="btn btn-outline-danger ms-2"
              onClick={() => handleDialogOpen(discount.id)}
              disabled={isDeleteLoading}
            >
              <i className="fa fa-trash"></i>
            </button>
          </div>
        ),
      });
    });

    return discounts;
  };

  const title = "All Discounts";

  return (
    <AdminLayout>
      <MetaData title={title} />

      <h1 className="my-2 px-5">{data?.data?.length} Discounts</h1>
      <div className="mb-2 px-5 mt-5 content">
        <Link to="/admin/discount/new" className="btn btn-outline-success me-2">
          <i className="fa fa-plus"></i> Add Discount
        </Link>
      </div>
      <MDBDataTable
        data={setDiscounts()}
        className="px-5 content"
        bordered
        striped
        responsive
        hover
      />
      <Modal show={show} onHide={() => setShow(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Discount #{discount.id}</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <DiscountCard
            discount={discount}
            isDisabled={true}
            onApply={() => {}}
          />
        </Modal.Body>
      </Modal>
      <Dialog open={dialogOpen} onClose={handleDialogClose}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete this discount?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDialogClose} color="primary">
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

export default ListDiscounts;
