import { MDBDataTable } from "mdbreact";
import { useEffect } from "react";
import toast from "react-hot-toast";
import { Link, useSearchParams } from "react-router-dom";
import { orderTable } from "../../../constants/constants";
import { currencyFormat, myDateFormat } from "../../../helpers/helpers";
import {
  useDeleteOrderMutation,
  useGetAdminOrdersQuery,
} from "../../../redux/api/orderApi";
import Loader from "../../layout/Loader";
import Typography from "@mui/material/Typography";
import Label from "../../../helpers/components/customMUI/label/label";

const ListOrders = ({ params }) => {
  const [searchParams] = useSearchParams();

  const { data, isLoading, error } = useGetAdminOrdersQuery(params);
  const [
    deleteOrder,
    { error: deleteError, isLoading: isDeleteLoading, isSuccess },
  ] = useDeleteOrderMutation();

  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (deleteError) {
      toast.error(deleteError?.data?.message);
    }

    if (isSuccess) {
      toast.success("Order Deleted");
    }
  }, [error, deleteError, isSuccess]);

  const deleteOrderHandler = (id) => {
    deleteOrder(id);
  };
  const setOrders = () => {
    var rows = data?.orders?.map((order) => {
      return {
        id: order?.id,
        statusReceived: (
          // <div
          //   className={`fw-bold
          //       ${
          //         order?.statusReceived?.toUpperCase() === "RECEIVED" &&
          //         "text-success"
          //       }
          //       ${
          //         order?.statusReceived?.toUpperCase() === "NOT_RECEIVED" &&
          //         "gold"
          //       }
          //        `}
          // >
          //   {order?.statusReceived?.toUpperCase()}
          // </div>
          <Label
            variant="soft"
            color={
              (order?.statusReceived === "NOT_RECEIVED" && "warning") ||
              (order?.statusReceived === "RECEIVED" && "success") ||
              (order?.statusReceived === "UNVERIFIED" && "default")
            }
            sx={{ mx: "auto" }}
          >
            {order?.statusReceived}
          </Label>
        ),
        createDate: myDateFormat(order?.createDate),
        totalAmount: currencyFormat(order?.totalAmount),
        discount: (order?.percentageDiscount ?? 0) + " %",
        isConsignment: (
          <div>
            <Label
              variant="soft"
              color={order?.consignment ? "primary" : "secondary"}
              sx={{ mx: "auto" }}
            >
              {order?.consignment ? "Consignment" : "Not Consignment"}
            </Label>
          </div>
        ),
        userConfirm: (
          <div>
            {/* {order?.statusReceived?.toUpperCase()} */}
            <Label
              variant="soft"
              color={
                (order?.userConfirm === "NOT_RECEIVED" && "warning") ||
                (order?.userConfirm === "RECEIVED" && "success")
              }
              sx={{ mx: "auto" }}
            >
              {order?.userConfirm}
            </Label>
          </div>
        ),
        actions: (
          <div className="d-flex gap-2">
            <Link
              to={`/admin/orders/${order?.id}`}
              className="btn btn-outline-primary"
            >
              <i className="fa fa-pencil"></i>
            </Link>

            {/* <button
              aria-label="Remove Order"
              className="btn btn-outline-danger "
              onClick={() => deleteOrderHandler(order?._id)}
              disabled={isDeleteLoading}
            >
              <i className="fa fa-trash"></i>
            </button> */}
          </div>
        ),
      };
    });

    return orderTable(rows);
  };
  if (isLoading) return <Loader />;

  return (
    <>
      <h1 className="my-2 px-5 ">{data?.orders?.length} Orders</h1>

      <MDBDataTable
        data={setOrders()}
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
    </>
  );
};
export default ListOrders;
