import React, { useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";

const Search = () => {
  const [keyword, setKeyword] = useState("");
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const submitHandler = (e) => {
    e.preventDefault();

    // Construct a new URLSearchParams object with the existing search params
    const newSearchParams = new URLSearchParams(searchParams.toString());

    // Set the new value for keyword. If there's no keyword, delete the param.
    if (keyword?.trim()) {
      newSearchParams.set("keyword", keyword);
    } else {
      newSearchParams.delete("keyword");
    }

    // Set the new search params which will replace the existing ones in the URL.
    setSearchParams(newSearchParams);
    navigate(`/products?${newSearchParams.toString()}`);
  };

  return (
    <form className="d-flex" onSubmit={submitHandler}>
      <div className="input-group mx-0 shadow w-100 ">
        <input
          type="text"
          id="search_field"
          aria-describedby="search_btn"
          className="form-control rounded-0"
          placeholder="Enter Product Name ..."
          name="keyword"
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
        />
        <button
          aria-label="search-btn"
          id="search_btn"
          className="btn rounded-0"
          type="submit"
        >
          <i className="fa fa-search" aria-hidden="true"></i>
        </button>
      </div>
    </form>
  );
};

export default Search;
