import React, { useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";

const Search = () => {
  const [search, setSearch] = useState("");
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();

  const submitHandler = (e) => {
    e.preventDefault();

    // Construct a new URLSearchParams object with the existing search params
    const newSearchParams = new URLSearchParams(searchParams.toString());


    // Set the new value for search. If there's no keyword, delete the param.
    if (search?.trim()) {
      newSearchParams.set("keyword", search);
    } else {
      newSearchParams.delete("keyword");
    }

    // Set the new search params which will replace the existing ones in the URL.
    setSearchParams(newSearchParams);
    navigate(`/forums?${newSearchParams.toString()}`);
  };

  return (
    <form className="d-flex" onSubmit={submitHandler}>
      <div className="input-group mx-auto">
        <input
          type="text"
          id="search_field"
          aria-describedby="search_btn"
          className="form-control"
          placeholder="Title..."
          name="search"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <button
          aria-label="search-btn"
          id="search_btn"
          className="btn"
          type="submit"
        >
          <i className="fa fa-search" aria-hidden="true"></i>
        </button>
      </div>
    </form>
  );
};

export default Search;
