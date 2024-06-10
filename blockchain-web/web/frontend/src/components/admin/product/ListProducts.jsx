import {
  Autocomplete,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  List,
  ListItem,
  ListItemText,
  TextField,
  Typography,
} from "@mui/material";
import { MDBDataTable } from "mdbreact";
import React, { useEffect, useState } from "react";
import { toast } from "react-hot-toast";
import { useSelector } from "react-redux";
import { Link, useSearchParams } from "react-router-dom";
import { BASE_PRODUCTIMG } from "../../../constants/constants";
import { cleanRequestParams, currencyFormat } from "../../../helpers/helpers";
import {
  useDeleteProductMutation,
  useGetAdminProductsQuery,
  useGetCategoriesQuery,
} from "../../../redux/api/productsApi";
import {
  useGetStatisticProductByTypeGoldIdQuery,
  useGetTypeGoldQuery,
} from "../../../redux/api/typeGoldApi";
import AdminLayout, { AdminLayoutLoader } from "../../layout/AdminLayout";
import MetaData from "../../layout/MetaData";

const ListProducts = () => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [typeOption, setTypeOption] = useState("AVAILABLE"); // Mặc định là AVAILABLE

  const { data, isLoading, error } = useGetAdminProductsQuery({
    typeOptionName: typeOption === "CRAFT" ? "CRAFT" : undefined,
  });

  const categoryResult = useGetCategoriesQuery();
  const typeGoldResult = useGetTypeGoldQuery();
  const { roles } = useSelector((state) => state.auth);
  const [
    deleteProduct,
    { isLoading: isDeleteLoading, error: deleteError, isSuccess },
  ] = useDeleteProductMutation();

  const [selectedCategory, setSelectedCategory] = useState(null);
  const [selectedGoldType, setSelectedGoldType] = useState(null);
  const [openDialog, setOpenDialog] = useState(false);
  const [productIdToDelete, setProductIdToDelete] = useState(null);

  const typeGoldId =
    typeGoldResult?.data?.data?.find((e) => e.typeName === selectedGoldType)
      ?.id ?? 0;
  const typeGoldProductResult = useGetStatisticProductByTypeGoldIdQuery({
    typeGoldId,
    goldOptionType: typeOption,
  });

  useEffect(() => {
    if (error) {
      toast.error(error?.data?.message);
    }

    if (deleteError) {
      toast.error(deleteError?.data?.message);
    }

    if (isSuccess) {
      toast.success("Product Deleted");
    }
  }, [error, deleteError, isSuccess]);

  const deleteProductHandler = (id) => {
    deleteProduct(id);
  };

  const handleOpenDialog = (id) => {
    setProductIdToDelete(id);
    setOpenDialog(true);
  };

  const handleCloseDialog = () => {
    setOpenDialog(false);
    setProductIdToDelete(null);
  };

  const handleConfirmDelete = () => {
    deleteProductHandler(productIdToDelete);
    handleCloseDialog();
  };

  const setProducts = () => {
    const products = {
      columns: [
        {
          label: "ID",
          field: "id",
          sort: "asc",
        },
        {
          label: "Details",
          field: "imgUrl",
          sort: "asc",
        },
        {
          label: "Name",
          field: "name",
          sort: "asc",
        },
        {
          label: "Category",
          field: "category",
          sort: "asc",
        },

        {
          label: "Gold Type",
          field: "typeGold",
          sort: "asc",
        },
        {
          label: "Stock",
          field: "stock",
          sort: "asc",
        },
        {
          label: "Total Sold",
          field: "totalProductSold",
          sort: "asc",
        },
        {
          label: "Actions",
          field: "actions",
          width: 150,
        },
      ],
      rows: [],
    };
    data?.data?.forEach((product) => {
      if (selectedCategory) {
        if (product?.category?.categoryName !== selectedCategory) return;
      }
      if (selectedGoldType) {
        if (product?.typeGold?.typeName !== selectedGoldType) return;
      }
      products.rows.push({
        id: product.id,
        category: product.category.categoryName,
        typeGold: product.typeGold?.typeName,
        name: `${product.productName}`,
        stock: product?.unitOfStock,
        totalProductSold: product?.totalProductSold,
        imgUrl: (
          <img
            className=""
            style={{
              height: "45px",
              maxWidth: "100px",
              aspectRatio: 1,
            }}
            src={product?.productImages[0] || BASE_PRODUCTIMG}
            alt={product?.productImages[0]}
          ></img>
        ),
        actions: (
          <div className="d-flex flex-wrap gap-2">
            {roles?.includes("ROLE_ADMIN") && (
              <>
                <Link
                  to={`/admin/products/${product?.id}`}
                  className="btn btn-outline-success"
                >
                  <i className="fa fa-pencil"></i>
                </Link>
                <Link
                  to={`/admin/products/${product?.id}/upload_images`}
                  className="btn btn-outline-success"
                >
                  <i className="fa fa-image"></i>
                </Link>
                <button
                  aria-label="btn-delete"
                  className="btn btn-outline-danger"
                  onClick={() => handleOpenDialog(product?.id)}
                  disabled={isDeleteLoading}
                >
                  <i className="fa fa-trash"></i>
                </button>
              </>
            )}
          </div>
        ),
      });
    });

    return products;
  };

  const title = "All Products";
  if (isLoading) {
    return <AdminLayoutLoader title={title} />;
  }

  const handleCategoryChange = (event, value) => {
    setSelectedCategory(value);
    updateSearchParams(value, selectedGoldType);
  };

  const handleGoldTypeChange = (event, value) => {
    setSelectedGoldType(value);
    updateSearchParams(selectedCategory, value);
  };

  const handleTypeOptionChange = (event, value) => {
    setTypeOption(value);
    setSelectedGoldType("24k gold");
    updateSearchParams(selectedCategory, selectedGoldType, value);
  };

  const updateSearchParams = (category, goldType, typeOptionName) => {
    setSearchParams(
      cleanRequestParams({
        category: category || null,
        goldType: goldType || null,
        typeOptionName: typeOptionName || typeOption,
      })
    );
  };

  return (
    <AdminLayout>
      <MetaData title={title} />

      <h1 className="my-2 px-5">{data?.data?.length} Products</h1>
      <div className="mb-2 px-5 content mt-5 ">
        <Box sx={{ display: "flex", gap: 2 }}>
          <Autocomplete
            options={categoryResult?.data?.data.map((c) => c.categoryName)}
            value={selectedCategory}
            onChange={handleCategoryChange}
            renderInput={(params) => <TextField {...params} label="Category" />}
            sx={{ width: 300 }}
          />
          <Autocomplete
            options={typeGoldResult?.data?.data.map((c) => c.typeName)}
            value={selectedGoldType}
            onChange={handleGoldTypeChange}
            disabled={typeOption === "CRAFT"}
            renderInput={(params) => (
              <TextField {...params} label="Type Gold" />
            )}
            sx={{ width: 300 }}
          />
          <Autocomplete
            options={["AVAILABLE", "CRAFT"]}
            value={typeOption}
            onChange={handleTypeOptionChange}
            renderInput={(params) => (
              <TextField {...params} label="Type Option" />
            )}
            sx={{ width: 300 }}
          />
        </Box>

        {selectedGoldType && (
          <Box
            sx={{
              maxWidth: 360,
              bgcolor: "background.paper",
              borderRadius: "10px",
              boxShadow: 3,
              p: 2,
              m: 2,
            }}
          >
            <Typography variant="h6" component="div" mb={2}>
              Filtered Products Summary
            </Typography>
            <List
              // component="nav"
              aria-label="filtered-products-summary"
              disablePadding
            >
              <ListItem divider>
                <ListItemText
                  primary={`Total Quantity: ${typeGoldProductResult.data?.data?.totalQuantity}`}
                />
              </ListItem>
              <ListItem divider>
                <ListItemText
                  primary={`Total Weight: ${typeGoldProductResult.data?.data?.totalWeight} MACE`}
                  secondaryTypographyProps={{ component: "div" }}
                />
              </ListItem>
              <ListItem>
                <ListItemText
                  primary={`Total Amount: ${currencyFormat(
                    typeGoldProductResult.data?.data?.totalAmount
                  )}`}
                />
              </ListItem>
            </List>
          </Box>
        )}
        {roles?.includes("ROLE_ADMIN") && (
          <Link
            to="/admin/products/new"
            className="btn btn-outline-success me-2 mt-3"
          >
            <i className="fa fa-plus"></i> Add Product
          </Link>
        )}
      </div>

      <MDBDataTable
        data={setProducts()}
        className="px-5 content"
        bordered
        responsive
        striped
        hover
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
            Do you sure you want to delete this product?
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

export default ListProducts;
