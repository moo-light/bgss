import React, { useEffect } from "react";
import toast from "react-hot-toast";
import { useSelector } from "react-redux";
import { useNavigate, useSearchParams } from "react-router-dom";
import Slider from "react-slick";
import "slick-carousel/slick/slick-theme.css";
import "slick-carousel/slick/slick.css";

import {
  useAddToMyDiscountMutation,
  useGetAllRandomDiscountQuery,
  useGetMyDiscountQuery,
} from "../../redux/api/discountApi";
import { useGetProductsQuery } from "../../redux/api/productsApi";

import DiscountCard from "../../helpers/components/discount-card";
import CustomPagination from "../layout/CustomPagination";
import Filters from "../layout/Filters";
import Loader from "../layout/Loader";
import MetaData from "../layout/MetaData";
import Search from "../layout/Search";
import ProductItem from "../product/ProductItem";

const ProductListPage = () => {
  const [searchParams] = useSearchParams();
  const page = searchParams.get("page");
  const keyword = searchParams.get("keyword");
  const min = searchParams.has("min")
    ? Math.min(searchParams.get("min"), 10000000000)
    : "";
  const max = searchParams.has("max")
    ? Math.min(searchParams.get("max"), 10000000000)
    : "";
  const asc = searchParams.get("asc");
  const category = searchParams.getAll("category");
  const typeGold = searchParams.getAll("typeGold");
  const reviews = searchParams.getAll("reviews");
  const { isAuthenticated, roles } = useSelector((s) => s.auth);
  const navigate = useNavigate();

  const params = { page, keyword, asc };
  min !== null && (params.min = isNaN(min) ? "" : min);
  max !== null && (params.max = isNaN(min) ? "" : max);
  category.length && (params.category = category);
  typeGold.length && (params.typeGold = typeGold);
  reviews.length && (params.reviews = reviews);
  
  const { data, isLoading, error, isError } = useGetProductsQuery(params);
  const discountResult = useGetAllRandomDiscountQuery();
  const userDiscountResult = useGetMyDiscountQuery(true);
  const [applyDiscount, applyDiscountResult] = useAddToMyDiscountMutation();

  useEffect(() => {
    if (isError) {
      toast.error(error?.data?.message);
    }
    if (applyDiscountResult.isError) {
      toast.error(applyDiscountResult?.error?.data?.message);
    }
    if (applyDiscountResult.isSuccess) {
      toast.success("Discount Saved!");
    }
  }, [
    isError,
    applyDiscountResult?.isSuccess,
    applyDiscountResult?.isError,
    applyDiscountResult?.error?.data?.message,
    error?.data?.message,
  ]);

  const products = data?.data;
  if (isLoading) return <Loader />;
  let userDiscounts = [];
  if (userDiscountResult.isSuccess) {
    userDiscounts = userDiscountResult?.data?.data?.map((d) => d.discount.id);
  }
  // console.log(userDiscountResult);
  const settings = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 3,
    slidesToScroll: 3,
    responsive: [
      {
        breakpoint: 1024,
        settings: {
          slidesToShow: 2,
          slidesToScroll: 2,
          infinite: true,
          dots: true,
        },
      },
      {
        breakpoint: 600,
        settings: {
          slidesToShow: 1,
          slidesToScroll: 1,
          initialSlide: 1,
        },
      },
    ],
  };
  const handleApplyDiscount = (discount) => {
    if (!isAuthenticated) {
      navigate("/login");
      return;
    }
    const isCustomer = roles?.includes("ROLE_CUSTOMER");
    if (!isCustomer) {
      toast.error("Only customers are allowed to save discounts");
      return;
    }
    applyDiscount(discount.id);
  };
  return (
    <>
      <MetaData title="BGSS" />
      <section className="row mt-3 justify-content-center gap-3 px-5 px-lg-0">
        <Slider {...settings}>
          {discountResult?.isSuccess &&
            discountResult?.data?.data.map((d) => (
              <div key={d.id} className="mx-1">
                <DiscountCard
                  discount={d}
                  isDisabled={
                    d.expire ||
                    d.defaultQuantity === 0 ||
                    userDiscounts.includes(d.id)
                  }
                  applyMessage={userDiscounts.includes(d.id) ? "Saved" : "Save"}
                  onApply={(e) => {
                    handleApplyDiscount(d);
                  }}
                />
              </div>
            ))}
        </Slider>
        {discountResult?.isError && <h5>Couldn't get data</h5>}
        {discountResult?.isLoading && <Loader />}
      </section>

      <div className="row">
        <div className="col-6 col-md-3">
          <Filters />
        </div>
        <div className="col-6 col-md-9">
          <h1
            id="products_heading"
            className="text-secondary border-border-bottom"
          >
            Product List
          </h1>
          <div className="col-12 col-md-6 mt-3">
            <Search />
          </div>
          <section id="products" className="mt-5">
            <div className="row">
              {products?.length ? (
                products.map((product) => (
                  <ProductItem key={product.id} data={product} columnSize={3} />
                ))
              ) : (
                <div className="display-6 text-center my-5 m-auto">
                  {searchParams.size > 0
                    ? "No product found"
                    : "No Product Yet"}
                </div>
              )}
            </div>
          </section>
          <CustomPagination
            resPerPage={data?.resPerPage}
            filteredProductsCount={data?.filteredProductsCount}
          />
        </div>
      </div>
    </>
  );
};

export default ProductListPage;
