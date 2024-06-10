import { Button } from "react-bootstrap";
import { currencyFormat, myDateFormat, numberFormat } from "../helpers";

function DiscountCard({
  discount,
  isDisabled,
  onApply,
  hintMessage = null,
  isApplied,
  applyMessage = "Save",
  ...other
}) {
  return (
    <div
      key={discount?.id}
      className={`card mb-3 mx-2 ${isApplied && "border-3"}`}
      style={{
        transition: "all 0.3s !important",
        border: isApplied ? "solid" : null,
        borderColor: "var(--gold-color)",
      }}
    >
      <main className="card-body d-grid">
        <div className="row my-auto">
          <aside className="col-auto h-100 my-auto small ">
            <span className="yellowColor h5 fw-bold">
              {discount.percentage}% Off
            </span>
            <div className="small text-center ">
              <b>{discount?.code}</b>
            </div>
          </aside>
          <section className=" col small border-start border-black">
            <div className=" ">
              Minimum products amount:{" "}
              <b>{numberFormat(discount.quantityMin)}</b>
            </div>{" "}
            <div className=" ">
              Max reduce price: <b>{currencyFormat(discount.maxReduce)}</b>
            </div>{" "}
            <div className=" ">
              Min price allowed: <b>{currencyFormat(discount.minPrice)}</b>
            </div>
            <div className="small">
              Discount left: <b>{discount.defaultQuantity}</b>
            </div>
            <div className=" text-50 small">
              Valid until: <b>{myDateFormat(discount.dateExpire)}</b>
            </div>
            {!!hintMessage && <small className="small">{hintMessage}</small>}
          </section>
          <div className="gap-2 my-auto d-flex col-auto    ">
            <Button
              type="button "
              variant={`primary`}
              disabled={isDisabled}
              onClick={onApply}
            >
              <span className="">{applyMessage || "Save"}</span>
            </Button>
          </div>
        </div>
      </main>
    </div>
  );
}
export default DiscountCard;
