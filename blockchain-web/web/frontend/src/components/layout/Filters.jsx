import React, { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { FaLongArrowAltDown, FaLongArrowAltUp } from "react-icons/fa";
import { useNavigate, useSearchParams } from "react-router-dom";
import StarRatings from "react-star-ratings";
import { numberKeyValidator } from "../../helpers/form-validation-helpers";
import { getPriceQueryParams } from "../../helpers/helpers";
import { useGetCategoriesQuery } from "../../redux/api/productsApi";
import { useGetTypeGoldQuery } from "../../redux/api/typeGoldApi";

const Filters = () => {
  const [min, setMin] = useState(null);
  const [max, setMax] = useState(null);
  const [selectedCategories, setSelectedCategories] = useState([]);
  const [selectedTypeGold, setSelectedTypeGold] = useState([]);

  const navigate = useNavigate();
  let [searchParams] = useSearchParams();

  useEffect(() => {
    searchParams.has("min") &&
      setMin(Math.min(searchParams.get("min"), 1000000000));
    searchParams.has("max") &&
      setMax(Math.min(searchParams.get("max"), 1000000000));
  }, [searchParams]);

  // Handle Category & Ratings filter
  const handleClick = (checkbox) => {
    const checkboxes = document.getElementsByName(checkbox.name);

    checkboxes.forEach((item) => {
      if (item !== checkbox) item.checked = false;
    });

    if (checkbox.checked === false) {
      // Delete filter from query
      if (searchParams.has(checkbox.name)) {
        searchParams.delete(checkbox.name);
        const path = window.location.pathname + "?" + searchParams.toString();
        navigate(path);
      }
    } else {
      // Set new filter value if already there
      if (searchParams.has(checkbox.name)) {
        searchParams.set(checkbox.name, checkbox.value);
      } else {
        // Append new filter
        searchParams.append(checkbox.name, checkbox.value);
      }

      const path = window.location.pathname + "?" + searchParams.toString();
      navigate(path);
    }
  };

  // Handle price filter
  const handleButtonClick = (e) => {
    e.preventDefault();
    if (Number(min) > Number(max)) {
      toast.error("min price can't be larger than max price");
      return;
    }
    searchParams = getPriceQueryParams(searchParams, "min", min);
    searchParams = getPriceQueryParams(searchParams, "max", max);

    const path = window.location.pathname + "?" + searchParams.toString();
    navigate(path);
  };

  //CATEGORY
  const defaultCheckHandler = (checkboxType, checkboxValue) => {
    const value = searchParams.get(checkboxType);
    if (checkboxValue === value) return true;
    return false;
  };

  // Update search params when selectedCategories changes
  useEffect(() => {
    // First clear all category search params
    searchParams.delete("category");

    // Then set or append each selected category
    selectedCategories.forEach((category) => {
      searchParams.append("category", category);
    });

    const path = window.location.pathname + "?" + searchParams.toString();
    navigate(path);
  }, [selectedCategories, navigate, searchParams]);

  // Handle Category selection
  const handleCategoryChange = (event) => {
    const value = event.target.value;
    if (event.target.checked) {
      // Add to the array
      setSelectedCategories((prev) => [...prev, value]);
    } else {
      // Remove from the array
      setSelectedCategories((prev) =>
        prev.filter((category) => category !== value)
      );
    }
  };

  // Update search params when selectedTypeGold changes
  useEffect(() => {
    // First clear all typeGold search params
    searchParams.delete("typeGold");

    // Then set or append each selected typeGold
    selectedTypeGold.forEach((typeGold) => {
      searchParams.append("typeGold", typeGold);
    });

    const path = window.location.pathname + "?" + searchParams.toString();
    navigate(path);
  }, [selectedTypeGold, navigate, searchParams]);

  // Handle TypeGold selection
  const handleTypeGoldChange = (event) => {
    const value = event.target.value;
    if (event.target.checked) {
      // Add to the array
      setSelectedTypeGold((prev) => [...prev, value]);
    } else {
      // Remove from the array
      setSelectedTypeGold((prev) =>
        prev.filter((typeGold) => typeGold !== value)
      );
    }
  };

  const handleSorting = (e) => {
    asc === null && searchParams.set("asc", "true");
    asc === "true" && searchParams.set("asc", "false");
    asc === "false" && searchParams.delete("asc");
    navigate("?" + searchParams.toString());
  };

  const { data, isLoading, error, isError } = useGetCategoriesQuery();
  const {
    data: typeGoldData,
    isLoading: isTypeGoldLoading,
    error: typeGoldError,
    isError: isTypeGoldError,
  } = useGetTypeGoldQuery();

  const asc = searchParams.get("asc");

  return (
    <div className="border p-3 filter">
      <div>
        <h3 className="position-relative ">
          Filters
          <button
            aria-label="sort"
            className="position-absolute end-0 top-50 translate-middle-y btn border-0  rounded-circle shadow"
            onClick={(e) => handleSorting(e)}
          >
            {asc === null && <FaLongArrowAltUp />}
            {asc === "true" && <FaLongArrowAltUp className="orange" />}
            {asc === "false" && <FaLongArrowAltDown className="orange" />}
          </button>
        </h3>
      </div>
      <hr />
      <h5 className="filter-heading mb-3">Price</h5>
      <form id="filter_form" onSubmit={handleButtonClick}>
        <div className="row">
          <div className="col">
            <input
              type="number"
              min={0}
              className="form-control"
              placeholder="Min($)"
              name="min"
              maxLength={10}
              value={min}
              onKeyDown={numberKeyValidator}
              onChange={(e) => setMin(e.target.value)}
            />
          </div>
          <div className="col">
            <input
              type="number"
              min={0}
              className="form-control"
              placeholder="Max($)"
              name="max"
              maxLength={10}
              value={max}
              onKeyDown={numberKeyValidator}
              onChange={(e) => setMax(e.target.value)}
            />
          </div>
          <div className="col">
            <button type="submit" className="btn btn-primary">
              GO
            </button>
          </div>
        </div>
      </form>
      <hr />
      <h5 className="mb-3">Category</h5>
      {data && !isLoading ? (
        data?.data?.map((category) => (
          <div className="form-check" key={category.id}>
            <input
              className="form-check-input"
              type="checkbox"
              name="category"
              id={`category_${category.id}`}
              value={category.id}
              checked={selectedCategories.includes(category.id.toString())}
              onChange={handleCategoryChange}
            />
            <label
              className="form-check-label"
              htmlFor={`category_${category.id}`}
            >
              {category.categoryName}
            </label>
          </div>
        ))
      ) : isError ? (
        <div>No categories found</div>
      ) : (
        <div className="loader w-25" />
      )}

      <hr />
      <h5 className="mb-3">Type of Gold</h5>
      {typeGoldData && !isTypeGoldLoading ? (
        typeGoldData?.data?.map((typeGold) => (
          <div className="form-check" key={typeGold.id}>
            <input
              className="form-check-input"
              type="checkbox"
              name="typeGold"
              id={`typeGold_${typeGold.id}`}
              value={typeGold.id}
              checked={selectedTypeGold.includes(typeGold.id.toString())}
              onChange={handleTypeGoldChange}
            />
            <label
              className="form-check-label"
              htmlFor={`typeGold_${typeGold.id}`}
            >
              {typeGold.typeName}
            </label>
          </div>
        ))
      ) : isTypeGoldError ? (
        <div>No type of gold found</div>
      ) : (
        <div className="loader w-25" />
      )}
      <hr />
      <h5 className="mb-3">Ratings</h5>

      {[5, 4, 3, 2, 1].map((reviews) => (
        <div className="form-check">
          <input
            className="form-check-input"
            type="checkbox"
            name="reviews"
            id="check7"
            value={reviews}
            defaultChecked={defaultCheckHandler("ratings", reviews?.toString())}
            onClick={(e) => handleClick(e.target)}
          />
          <label
            className="form-check-label position-relative "
            style={{ bottom: "2px" }}
            htmlFor="check7"
          >
            <StarRatings
              rating={reviews}
              starRatedColor="#ffb829"
              numberOfStars={5}
              name="rating"
              starDimension="21px"
              starSpacing="1px"
            />
          </label>
        </div>
      ))}
    </div>
  );
};

export default Filters;
