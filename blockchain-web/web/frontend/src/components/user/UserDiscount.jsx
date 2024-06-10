import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from "@mui/material";
import { formatDistanceToNow } from "date-fns";
import { MDBDataTable } from "mdbreact";
import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { discountTable } from "../../constants/constants";
import {
  useDeleteMyDiscountMutation,
  useGetMyDiscountQuery,
} from "../../redux/api/discountApi";
import MetaData from "../layout/MetaData";
import UserLayout from "../layout/UserLayout";

function UserDiscount() {
  const { data: discountData, isLoading: discountIsLoading } =
    useGetMyDiscountQuery();

  const [deleteMyDiscount, { isSuccess: deleted, isError, error }] =
    useDeleteMyDiscountMutation();

  const [dialogOpen, setDialogOpen] = useState(false);
  const [selectedDiscountId, setSelectedDiscountId] = useState(null);

  useEffect(() => {
    if (isError) {
      toast.error(error?.data?.message);
    }

    if (deleted) {
      toast.success("Discount code deleted successfully");
    }
  }, [isError, deleted]);

  const handleDialogClose = () => {
    setDialogOpen(false);
  };

  const handleDelete = () => {
    if (selectedDiscountId) {
      deleteMyDiscount(selectedDiscountId);
    }
    setDialogOpen(false);
  };

  const handleDeleteClick = (id) => {
    setSelectedDiscountId(id);
    setDialogOpen(true);
  };

  const setDiscounts = () => {
    const discounts = discountTable();
    discounts.columns.splice(-2, 0, {
      label: "Valid",
      field: "valid",
      sort: "asc",
    });
    discountData?.data.forEach((data) => {
      // Ensure consistent data handling
      const discount = data.discount;
      discounts.rows.push({
        id: discount.id,
        code: discount.code,
        percentage: (
          <span className="fw-bold orange">{discount.percentage + "%"} </span>
        ),
        dateCreate: discount.dateCreate
          ? formatDistanceToNow(new Date(discount.dateCreate), {
              addSuffix: true,
            })
          : "",
        dateExpire: discount.dateExpire
          ? formatDistanceToNow(new Date(discount.dateExpire), {
              addSuffix: true,
            })
          : "",
        expire: discount.expire ? "Yes" : "No",
        valid: data?.valid ? "No" : "Yes",
        minPrice: discount?.minPrice + " $",
        maxReduce: discount?.maxReduce + " $",
        actions: (
          <div className="d-flex">
            <button
              aria-label="btn-delete"
              className="btn btn-outline-danger ms-2"
              onClick={() => handleDeleteClick(data?.id)}
            >
              <i className="fa fa-trash"></i>
            </button>
          </div>
        ),
      });
    });
    return discounts;
  };

  return (
    <UserLayout>
      <MetaData title={"My Discount"} />
      <div className="row wrapper ">
        <article className="col-10 col-lg-8 ">
          <h2 className=" text-center">My Discounts</h2>
        </article>
        <div className=" mb-3">
          <MDBDataTable
            data={setDiscounts()}
            className="px-3"
            responsive
            bordered
            striped
            hover
          />
        </div>
      </div>

      <Dialog open={dialogOpen} onClose={handleDialogClose}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete this discount?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDialogClose} color="inherit">
            Cancel
          </Button>
          <Button onClick={handleDelete} color="error">
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </UserLayout>
  );
}

export default UserDiscount;
