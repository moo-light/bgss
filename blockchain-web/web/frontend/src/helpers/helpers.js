import { format } from "date-fns";
import DOMPurify from "dompurify";
import { weightConverter } from "../redux/features/transferSlice";

export const getPriceQueryParams = (searchParams, key, value) => {
  const hasValueInParam = searchParams.has(key);

  if (value && hasValueInParam) {
    searchParams.set(key, value);
  } else if (value) {
    searchParams.append(key, value);
  } else if (hasValueInParam) {
    searchParams.delete(key);
  }

  return searchParams;
};
export const caluclateTradeCost = ({
  rates,
  quantity,
  goldUnit,
  type,
  conversionFactors,
}) => {
  const itemsPrice =
    rates * weightConverter(quantity, goldUnit, "tOz", conversionFactors);
  //shipping
  const shippingPrice = 0;
  //taxes
  const taxPrice = 0;
  // Number((0.1 * itemsPrice).toFixed(2));
  //total
  let totalPrice = itemsPrice + shippingPrice + taxPrice;
  totalPrice = totalPrice.toFixed(2);
  return {
    itemsPrice: Number(itemsPrice?.toFixed(2)),
    taxPrice,
    totalPrice,
  };
};
export const caluclateOrderCost = (cartItems, selectedDiscount) => {
  const itemsPrice = cartItems?.reduce(
    (acc, item) => acc + item.price * item.quantity,
    0
  );
  //shipping
  const shippingPrice = 0;
  // discount
  let reducedPrice = 0;
  if (selectedDiscount) {
    reducedPrice = Math.min(
      ...[(selectedDiscount.percentage / 100) * itemsPrice,
      selectedDiscount.maxReduce,]
    );
  }
  //taxes
  // const taxPrice = Number((0.1 * itemsPrice).toFixed(2));
  const taxPrice = 0;
  //total
  let totalPrice = itemsPrice + shippingPrice + taxPrice - reducedPrice;
  totalPrice = totalPrice.toFixed(2);
  return {
    itemsPrice: Number(itemsPrice?.toFixed(2)),
    reducedPrice: Number(reducedPrice?.toFixed(2)),
    taxPrice,
    totalPrice,
  };
};

export const currencyFormat = (v) => {
  if (v == null) v = 0;
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "USD",
  }).format(v);
};
export const numberFormat = (v) => {
  if (v == null) v = 0;
  return new Intl.NumberFormat("en-US").format(v);
};

export const phoneFormat = (value) => {
  try {
    let phone = value.replace(/\D/g, "");
    const match = phone.match(/^(\d{1,3})(\d{0,3})(\d{0,4})$/);
    if (match) {
      phone = `${match[1]}${match[2] ? " " : ""}${match[2]}${
        match[3] ? "-" : ""
      }${match[3]}`;
    }
    return phone;
  } catch (e) {
    return value;
  }
};
export const myDateFormat = (value) => {
  try {
    const date = format(value, "MM-dd-yyyy HH:mm:ss aa");
    return date;
  } catch (e) {
    return value;
  }
};
export const formatHTMLToText = (html) => {
  return DOMPurify.sanitize(html, {
    ALLOWED_TAGS: [], // Allow no tags
    ALLOWED_ATTR: [], // Allow no attributes
  })
    .toString()
    .trim();
};
export const formatPostHTMLWithDiscount = (html) => {
  function replacement(value) {
    return `<span class="allow-discount-copy" data-value="$1" style="display: inline-flex;">
      <span class="user-select-all">$1 <span class="icon clickable"><i class="fas fa-copy"></i></span></span>
    </span>`;
  }
  return html;
  // .replace(DISCOUNT_REGEX, replacement());
};

// giup xoa di key cua object co value la null
// thich hop de su dung khi truyen param vao API
export function cleanRequestParams(obj) {
  for (var propName in obj) {
    if (
      obj[propName] === null ||
      obj[propName] === undefined ||
      obj[propName] === "" ||
      obj[propName].length === 0
    ) {
      delete obj[propName];
    }
  }
  if (Object.entries(obj).length === 0) return undefined;
  return obj;
}

export function delayMili(millis) {
  return new Promise((resolve, reject) => {
    setTimeout((_) => resolve(), millis);
  });
}
export function clone(v) {
  return Object.assign({}, v);
}
export function isMarketOpen() {
  const now = new Date();
  const day = now.getUTCDay();
  const hour = now.getUTCHours() + 2 + now.getUTCMinutes() / 60;

  const tradingHours = {
    0: [
      [22, 24],
      [0, 20],
      [21, 24],
    ],
    1: [
      [0, 20],
      [21, 24],
    ],
    2: [
      [0, 20],
      [21, 24],
    ],
    3: [
      [0, 20],
      [21, 24],
    ],
    4: [
      [0, 20],
      [21, 24],
    ],
    5: [[0, 20]],
  };

  return (
    tradingHours[day]?.some(([start, end]) => hour >= start && hour < end) ??
    false
  );
}
