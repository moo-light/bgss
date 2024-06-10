import React from "react";
import toast from "react-hot-toast";
import { useDispatch, useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { BASE_PRODUCTIMG } from "../../constants/constants";
import { getServerImgUrl } from "../../helpers/image-handler";
import {
  useRemoveCartItemMutation,
  useUpdateQuantityMutation,
} from "../../redux/api/cartApi";
import { setCartItem } from "../../redux/features/cartSlice";
import MetaData from "../layout/MetaData";

const Cart = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const { cartItems } = useSelector((state) => state.cart);
  // Mutation hooks
  const [updateQuantity, { isLoading }] = useUpdateQuantityMutation();
  const [removeCartItem, { isLoading: isRemoving }] =
    useRemoveCartItemMutation();
  // Memoized handler functions
  const handleQuantityChange = async (cartItemId, quantity) => {
    try {
      await updateQuantity({ cartItemId, quantity }).unwrap();
    } catch (error) {
      console.error("Error updating quantity:", error);
    }
  };

  const removeCartItemHandler = async (cartId) => {
    try {
      const result = await removeCartItem(cartId).unwrap();
      if (result?.data === true) {
        toast.success("Remove from cart success");
      }
    } catch (error) {
      console.error("Error removing cart item:", error);
    }
  };
  // const { currentOrder } = useSelector((state) => state.order);
  // if (currentOrder != null && cartItems.length !== 0)
  //   return <Navigate to={"/payment_method"} />;
  const handleBlur = (item) => (e) => {
    let value = Number(e.target.value);
    if (value === item.quantity) return;
    if (value <= 0) value = 1;
    if (value > item.stock) value = item?.stock;
    dispatch(setCartItem({ ...item, quantity: e.target.value }));
    handleQuantityChange(item.id, Number(e.target.value));
  };

  const handleChange = (item) => (e) => {
    dispatch(setCartItem({ ...item, quantity: e.target.value }));
  };

  const checkoutHandler = () => {
    navigate("/confirm_order");
  };

  return (
    <>
      <MetaData title={"Your Cart"} />
      {cartItems?.length === 0 ? (
        <h2 className="mt-5">Your Cart is Empty</h2>
      ) : (
        <>
          <h2 className="mt-5">
            Your Cart: <b>{cartItems?.length} items</b>
          </h2>

          <div className="row d-flex justify-content-between">
            <div className="col-12 col-lg-8">
              {cartItems?.map((item) => (
                <>
                  <hr />
                  <div className="cart-item" data-key="product1">
                    <div className="d-flex row-gap-2">
                      <div className="col-2 col-lg-3 d-flex align-items-center gap-2">
                        <img
                          className="bg-white rounded"
                          src={getServerImgUrl(
                            item?.product?.productImages[0]?.imgUrl,
                            BASE_PRODUCTIMG
                          )}
                          name={item?.name}
                          alt=""
                          height="50"
                          width="60"
                        />
                      </div>
                      <div className="col-3 col-lg-3 align-items-center my-auto">
                        <Link to={`/product/${item?.product?.id}`}>
                          {" "}
                          {item?.name}
                          {""}
                        </Link>
                      </div>
                      <div className="col-3 col-lg-2 m-auto mt-lg-1">
                        <div className="stockCounter d-inline-flex btn-group align-items-center ">
                          <button
                            className="btn btn-secondary minus"
                            onClick={() =>
                              handleQuantityChange(item.id, item.quantity - 1)
                            }
                            disabled={item.quantity <= 1}
                          >
                            {" "}
                            -{" "}
                          </button>
                          <input
                            type="number"
                            className="form-control count d-inline"
                            value={item?.quantity}
                            onChange={handleChange(item)}
                            onBlur={handleBlur(item)}
                            disabled={!item}
                          />
                          <button
                            className="btn btn-primary plus"
                            onClick={() =>
                              handleQuantityChange(item.id, item.quantity + 1)
                            }
                            disabled={item.quantity >= item.stock}
                          >
                            +
                          </button>
                        </div>
                      </div>
                      <div className="col-2 col-lg-2 my-auto text-center ">
                        <b id="card_item_price">${item?.amount}</b>
                      </div>
                      <div className="col-1 col-lg-1 my-auto">
                        <i
                          id="delete_cart_item"
                          className={`fa fa-trash btn btn-danger ${
                            isRemoving && "disabled"
                          } `}
                          onClick={() => removeCartItemHandler(item?.id)}
                        ></i>
                      </div>
                    </div>
                  </div>
                  <hr />
                </>
              ))}
            </div>

            <div className="col-12 col-lg-4 col-xl-3 my-4">
              <div id="order_summary">
                <h4>Order Summary</h4>
                <hr />
                <p>
                  Units:{" "}
                  <span className="order-summary-values">
                    {cartItems?.reduce((acc, item) => acc + item?.quantity, 0)}{" "}
                    (Units)
                  </span>
                </p>
                <p>
                  Est. total:{" "}
                  <span className="order-summary-values">
                    $
                    {cartItems
                      ?.reduce(
                        (acc, item) => acc + item?.quantity * item.price,
                        0
                      )
                      .toFixed(2)}
                  </span>
                </p>
                <hr />
                <button
                  id="checkout_btn"
                  className="btn btn-primary w-100"
                  onClick={checkoutHandler}
                >
                  Check out
                </button>
              </div>
            </div>
          </div>
        </>
      )}
    </>
  );
};

export default Cart;
