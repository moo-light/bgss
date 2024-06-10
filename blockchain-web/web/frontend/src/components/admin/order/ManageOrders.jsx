import React, { useEffect, useState } from "react";
import { Button, Form, InputGroup, Spinner } from "react-bootstrap";
import toast from "react-hot-toast";
import { FaSearch } from "react-icons/fa";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { useLazyFindOrderByQrCodeQuery } from "../../../redux/api/orderApi";
import AdminLayout from "../../layout/AdminLayout";
import MetaData from "../../layout/MetaData";
import ListOrders from "./ListOrders";
import ListTransactions from "./ListTransactions";
import ListWithdraws from "./ListWithdraws";
const ManageOrders = () => {
  const [searchParams] = useSearchParams();
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const { roles } = useSelector((state) => state.auth);
  const view = searchParams.get("view");
  const params = {
    userId: searchParams.get("userId") || "",
  };
  const [findOrder, { data, isLoading, isError, isSuccess, error }] =
    useLazyFindOrderByQrCodeQuery();
  const [search, setSearch] = useState("");

  const handleSearch = (e) => {
    setSearch(e.target.value);
  };
  useEffect(() => {
    if (isError) {
      toast.error(error?.data?.message);
    }
    if (isSuccess) {
      toast.success("Order found!");
    }
    // if (added) {
    //   toast.success("Discount code added successfully");
    // }
  }, [isSuccess, isError]);

  const submitHandler = (e) => {
    e.preventDefault();
    findOrder(search?.trim());
    // .then((v) => navigate(`./${v.id}`));
  };
  const render = (view) => {
    switch (view) {
      case "orderss":
        return <ListOrders params={params} />;
      case "transactions":
        return <ListTransactions params={params} />;
      case "withdraws":
        return <ListWithdraws params={params} />;
      default:
        return <ListOrders params={params} />;
    }
  };
  return (
    <AdminLayout>
      <MetaData title={"All Orders"} />
      <div className="d-flex ">
        <nav className="navbar navbar-expand mt-2 d-block mx-auto rounded ">
          <div className="container-fluid">
            <div className="navbar-nav gap-2">
              <Link
                className={`nav-link ${
                  view === "orders" || !view ? "active" : ""
                }`}
                to="?view=orders"
              >
                Orders
              </Link>
              {roles?.includes("ROLE_ADMIN") ? (
                <Link
                  className={`nav-link ${
                    view === "transactions" ? "active" : ""
                  }`}
                  to="?view=transactions"
                >
                  Transactions
                </Link>
              ) : null}
              <Link
                className={`nav-link ${view === "withdraws" ? "active" : ""}`}
                to="?view=withdraws"
              >
                Withdraws
              </Link>
            </div>
          </div>
        </nav>
      </div>
      {/* Search Order Qr Code */}
      {(view === "orders" || !view) && (
        <search className="row wrapper mx-auto" style={{ maxWidth: "50em" }}>
          <div className="col-10 m-auto ">
            <Form className="shadow rounded bg-body " onSubmit={submitHandler}>
              <Form.Group className="d-flex flex-column">
                <InputGroup className="m-0 w-100">
                  <Form.Control
                    type="text"
                    placeholder="Search Order Code"
                    value={search}
                    onChange={handleSearch}
                  />
                  <Button type="submit" className="m-0">
                    <FaSearch />
                  </Button>
                </InputGroup>
                <hr className="mt-3"></hr>
                {isLoading ? (
                  <div className="col-1 d-inline-block mx-auto">
                    <Spinner className="orange small"></Spinner>
                  </div>
                ) : data?.data ? (
                  <div>
                    <div className="d-flex gap-2 align-items-center ">
                      <div className="" readOnly>
                        Order: <b>{data.data?.qrCode}</b>
                      </div>
                      <div className="mx-2">{data.data?.statusReceived}</div>
                      <Link
                        to={"./" + data.data.id}
                        type="button"
                        className="ms-auto p-2 btn  "
                      >
                        <span className="">
                          View <FaSearch />
                        </span>
                      </Link>
                    </div>
                  </div>
                ) : (
                  <div className="text-center">
                    No order found <FaSearch />
                  </div>
                )}
              </Form.Group>
            </Form>
          </div>
        </search>
      )}
      {/* List Orders */}
      {render(view)}
    </AdminLayout>
  );
};

export default ManageOrders;
