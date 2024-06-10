import { useState } from "react";
import { DropdownButton, Form, InputGroup } from "react-bootstrap";
import toast from "react-hot-toast";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import * as Yup from "yup";
import { FormError } from "../../helpers/components/form-error";
import { addErrors, clearErrors } from "../../helpers/form-validation-helpers";
import { currencyFormat } from "../../helpers/helpers";
import { useProcessTransactionMutation } from "../../redux/api/transactionApi";
import { setTradeData } from "../../redux/features/liveRateSlice";
import { weightConverter } from "../../redux/features/transferSlice";

export const BuyNSell = ({ data, selected, marketClosed }) => {
  const { user, roles, isAuthenticated } = useSelector((state) => state.auth);
  const { conversionFactors } = useSelector((state) => state.transfer);
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [quantity, setQuantity] = useState("");
  const [error, setError] = useState("");
  const [searchParams] = useSearchParams();
  const maxKg = 1;
  const minOz = 0.01;
  const rates = (selected === "BUY" ? data?.ask : data?.bid) ?? 0;

  const max = {
    G: (maxKg * conversionFactors["Kg"]?.g).toFixed(2),
    MACE: (maxKg * conversionFactors["Kg"]?.Mace).toFixed(2),
    TAEL: (maxKg * conversionFactors["Kg"]?.Tael).toFixed(2),
    KG: (maxKg * conversionFactors["Kg"]?.Kg).toFixed(2),
    TOZ: (maxKg * conversionFactors["Kg"]?.tOz).toFixed(2),
  };

  const min = {
    G: (minOz * conversionFactors["tOz"]?.g).toFixed(2),
    MACE: (minOz * conversionFactors["tOz"]?.Mace).toFixed(2),
    TAEL: (minOz * conversionFactors["tOz"]?.Tael).toFixed(2),
    KG: (minOz * conversionFactors["tOz"]?.Kg).toFixed(2),
    TOZ: (minOz * conversionFactors["tOz"]?.tOz).toFixed(2),
  };

  const type = searchParams.get("type") ?? "tOz";

  const errorMsg = `Price must in between ${min[type.toUpperCase()]} and ${
    max[type.toUpperCase()]
  }`;
  const validationSchema = Yup.object().shape({
    quantity: Yup.number()
      .required("Price is required")
      .moreThan(0.01, "Please enter a number larger than 0.01")
      .min(Number(min[type.toUpperCase()]), errorMsg)
      .max(Number(max[type.toUpperCase()]), errorMsg),
  });

  const [processTransaction, result] = useProcessTransactionMutation();
  const submitHandler = async (e) => {
    e.preventDefault();
    if (marketClosed) {
      toast.error("Market is closed");
      return;
    }
    clearErrors(document, { quantity: error }, "quantity");

    if (user.balance < Number(quantity)) {
      toast.error("Your don't have enough balance to submit transaction");
      return;
    }
    validate(quantity);
    if (error) return;
    const message = await validate(quantity);
    if (!message) {
      setTradeData({
        goldUnit: type,
        quantity: Number(quantity),
        type: selected,
      });
      // action when submit success
      processTransaction({
        quantityInOz: Number(quantity),
        pricePerOz: Number(rates),
        type: selected,
        goldUnit: type.toUpperCase(),
      })
        .unwrap()
        .then((payload) => {
          // Handle the success case
          toast.success("Transaction successful");
          navigate(`/me/orders?view=transactions`);
          // navigate(`/trade_order?id=${payload.data.id}`);
        })
        .catch((error) => {
          // Handle the error case
          toast.error(error?.data?.message);
        });
    }
  };
  const validate = async (value) => {
    try {
      await validationSchema.validate({ quantity: Number(value) });
      return null;
    } catch (error) {
      addErrors(document, { quantity: error.message });
      setError(error.message);
      return error.message;
    }
  };
  let cost = 0;
  if (rates && quantity)
    cost = rates * weightConverter(quantity, type, "tOz", conversionFactors);

  return (
    <div className="trade-container p-0 mt-2">
      <Form onSubmit={submitHandler}>
        <Form.Group className="col d-flex flex-column align-items-start ">
          <div className="small form-label text d=block">
            Quantity ({`${min[type.toUpperCase()]} ${type}`} -{" "}
            {`${max[type.toUpperCase()]} ${type}`})
          </div>
          <InputGroup
            className={`m-0 p-0 w-100 ${error && "border-danger border"}`}
          >
            <Form.Control
              placeholder={`Quantity`}
              value={quantity}
              name="quantity"
              autoComplete="off"
              disabled={marketClosed}
              onChange={(e) => {
                setError(null);
                setQuantity((p) => e.target.value);
                validate(e.target.value);
              }}
              onBlur={(e) => setQuantity(Number(e.target.value))}
            />
            <DropdownButton
              title={type}
              align="end"
              disabled={marketClosed}
              aria-controls="caret"
              variant={selected === "BUY" ? "success" : "danger"}
            >
              {Object.keys(conversionFactors).map((key) => (
                <Link
                  key={key}
                  className={`dropdown-item ${type === key && "orange"}`}
                  replace="false"
                  onClick={(e) =>
                    setQuantity(
                      weightConverter(quantity, type, key, conversionFactors)
                    )
                  }
                  to={"?type=" + key}
                >
                  {key}
                </Link>
              ))}
            </DropdownButton>
          </InputGroup>
          <FormError name="quantity" errorData={{ quantity: error }} />
          <p className="mt-2">
            Cost: <b>{currencyFormat(cost)}</b>
          </p>
          <button
            type="submit"
            className={`btn w-100 my-2 mx-0 btn-${
              selected === "BUY" ? "success" : "danger"
            }`}
            disabled={marketClosed}
            style={{ minWidth: 200 }}
            onClick={(e) => {
              if (!isAuthenticated) {
                e.preventDefault();
                navigate("/login");
                return;
              }
              if (!roles.includes("CUSTOMER")) {
                e.preventDefault();
                toast.error(
                  "Only customer are allowed to Create transaction order"
                );
                return;
              }
            }}
          >
            {marketClosed ? "Market is Closed" : selected}
          </button>
        </Form.Group>
      </Form>
    </div>
  );
};
