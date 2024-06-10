/* eslint-disable eqeqeq */
import { format } from "date-fns";
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useGetAllCategoryPostQuery } from "../../redux/api/postCategoryApi";

const Filters = ({ searchParams }) => {
  // const [selectedCategories, setSelectedCategories] = useState([]);

  const navigate = useNavigate();

  const [date, setDate] = useState({
    fromDate: searchParams.get("fromDate") || undefined,
    toDate: searchParams.get("toDate") || format(new Date(), "yyyy-MM-dd"),
  });

  const { data, isLoading, isError } = useGetAllCategoryPostQuery();
  // Handle Category selection
  const handleCategoryChange = (value) => (event) => {
    if (value) searchParams.set("category", value);
    else searchParams.delete("category");
    navigate("?" + searchParams.toString());
  };
  const categories = data
    ? [{ id: false, categoryName: "All" }, ...data?.data]
    : [];
  const selectedCategory = searchParams.get("category") ?? 0;
  // Handle Date Picker
  const handleDateSubmit = (event) => {
    event.preventDefault();
    try {
      searchParams.set("fromDate", date.fromDate);
    } catch (e) {
      searchParams.delete("fromDate");
    }
    try {
      searchParams.set("toDate", date.toDate);
    } catch (e) {
      searchParams.delete("toDate");
    }
    navigate("?" + searchParams.toString());
  };

  return (
    <div className="border p-3 filterForum">
      <h5 className="mb-3">Category</h5>
      {!isLoading && data ? (
        <div id="post_category">
          {categories?.map((category) => (
            <c
              className={`page-item col-12 text-start position-relative card p-2 mb-2 ${
                category.id == selectedCategory && "active"
              }`}
              key={category.id}
              onClick={handleCategoryChange(category.id)}
            >
              <div>{category.categoryName}</div>
            </c>
          ))}
        </div>
      ) : isError ? (
        <div>No categories found</div>
      ) : (
        <div className="loader w-25" />
      )}
      {/* <h5 className="mb-3">Date</h5>
      <form className="form-group" onSubmit={handleDateSubmit}>
        <div className="col col-lg-8 col-md-12 col-sm-8">
          <label for="startDate">From</label>
          <input
            id="startDate"
            className="form-control"
            onChange={(e) => setDate({ ...date, fromDate: e.target.value })}
            value={date.fromDate}
            type="date"
          />
          <span id="startDateSelected"></span>
        </div>
        <div className="col col-lg-8 col-md-12 col-sm-8 ">
          <label for="endDate">To</label>
          <input
            id="endDate"
            className="form-control"
            onChange={(e) => setDate({ ...date, toDate: e.target.value })}
            value={date.toDate}
            type="date"
          />
          <span id="endDateSelected"></span>
        </div>
        <button className="btn mt-2 btn-primary">Filter</button>
      </form> */}
    </div>
  );
};

export default Filters;
