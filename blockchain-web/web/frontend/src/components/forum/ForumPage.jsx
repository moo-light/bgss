import { Carousel, CarouselItem } from "react-bootstrap";
import { FaAngleLeft, FaAngleRight, FaThumbtack } from "react-icons/fa";
import { useSearchParams } from "react-router-dom";
import { useGetAllPostQuery } from "../../redux/api/postApi";
import Loader from "../layout/Loader";
import MetaData from "../layout/MetaData";
import Filters from "./Filters";
import PostItem from "./PostItem";
import Search from "./Search";

function ForumPage() {
  const [searchParams] = useSearchParams();
  const fromDate = searchParams.get("fromDate");
  const toDate = searchParams.get("toDate");
  const params = {
    categoryId: searchParams.get("category") ?? "",
    search: searchParams.get("keyword") ?? "",
    // fromDate: fromDate ? new Date(fromDate).toISOString() : "",
    // toDate: toDate ? new Date(toDate).toISOString() : "",
  };
  const { data, isLoading } = useGetAllPostQuery(params);

  return (
    <>
      <MetaData title={"Forums"} />
      <div className="row">
        <div className="col-4 col-md-3 mt-5">
          {<Filters searchParams={searchParams} />}
        </div>
        {!isLoading ? (
          <div className="col-8 col-md-9">
            <div className="col-12 col-md-6 mt-4">
              <Search />
            </div>
            <h1
              id="products_heading"
              className="text-secondary border-border-bottom "
            >
              Forums
            </h1>
            {(
              params?.categoryId &&
              !params.search &&
              data?.data.filter((p) => p.pinned === true).length > 0
            ) && (
              <div className="position-relative col my-5">
                <div className="position-absolute end-0 mx-5 z-3 ">
                  <FaThumbtack
                    className="text-danger"
                    style={{ rotate: "45deg", scale: "1.5" }}
                  />
                </div>

                <Carousel
                  data-bs-theme="light"
                  variant="dark"
                  prevIcon={<FaAngleLeft className="orange display-6 " />}
                  nextIcon={<FaAngleRight className="orange display-6 " />}
                  index
                >
                  {data?.data?.length > 0 ? (
                    data?.data
                      .filter((p) => p.pinned === true)
                      ?.map((i) => (
                        <CarouselItem>
                          <PostItem
                            className="col-10 col-md-8 m-auto"
                            data={i}
                            hidePinned={false}
                          />
                        </CarouselItem>
                      ))
                  ) : (
                    <p className="mt-5 text-center">No Post Found</p>
                  )}
                </Carousel>
              </div>
            )}

            <section id="posts">
              <div className="row">
                {data?.data?.length > 0 ? (
                  data?.data?.map((i) => (
                    <PostItem data={i} hidePinned={false} />
                  ))
                ) : (
                  <p className="mt-5 text-center">No Post Found</p>
                )}
              </div>
            </section>

            {/* <CustomPagination
            resPerPage={data?.resPerPage}
            filteredProductsCount={data?.filteredProductsCount}
          /> */}
          </div>
        ) : (
          <Loader />
        )}
      </div>
    </>
  );
}

export default ForumPage;
