// PATH
// export const BASE_HOST = "http://localhost:8080";
// export const BASE_HOST = "http://93.127.198.196:8080";
// export const BASE_HOST = "https://www.bgss-company.tech";
export const BASE_HOST = "https://bgss-company.tech";
export const BASE_PATH = `${BASE_HOST}/api/auth`;
export const BASE_AVATAR = "/images/default_avatar.jpg";
export const BASE_PRODUCTIMG = "/images/default_product.png";
export const BASE_POSTIMG = "/images/default_product.png";

export const PRODUCT_CATEGORIES = [
  "BRACELETS",
  "RINGS",
  "NECKLACES",
  "EARRINGS",
  "ENGAGEMENT RINGS",
  "WEDDING BANDS",
];
export const MAXFILE_SIZE = 1024 * 1024;
export const SUPPORTED_IMAGE_FORMATS = [
  "image/jpg",
  "image/jpeg",
  "image/gif",
  "image/png",
  "image/webp",
];
export const LIVERATE_KEY = "kYUT2SlSwttLZY_ksVXh";
export const orderTable = (rows = []) => {
  return {
    columns: [
      {
        label: "ID",
        field: "id",
        sort: "asc",
      },

      {
        label: "Created Date",
        field: "createDate",
        sort: "asc",
      },
      {
        label: "Total Price",
        field: "price",
        sort: "asc",
      },
      {
        label: "Discount",
        field: "discount",
        sort: "asc",
      },
      {
        label: "Total Amount",
        field: "totalAmount",
        sort: "asc",
      },
      {
        label: "Consignment",
        field: "isConsignment",
        sort: "asc",
      },
      {
        label: "Status",
        field: "statusReceived",
        sort: "asc",
      },
      {
        label: "Payment Status",
        field: "paymentStatus",
        sort: "asc",
      },
      {
        label: "Actions",
        field: "actions",
        sort: "asc",
      },
    ],
    rows: rows,
  };
};
export const discountTable = (rows = []) => {
  return {
    columns: [
      {
        label: "ID",
        field: "id",
        sort: "asc",
      },
      {
        label: "Code",
        field: "code",
        sort: "asc",
      },
      {
        label: "Percentage",
        field: "percentage",
        sort: "asc",
      },
      {
        label: "Min Price",
        field: "minPrice",
        sort: "asc",
      },
      {
        label: "Max Reduce",
        field: "maxReduce",
        sort: "asc",
      },
      {
        label: "Date Created",
        field: "dateCreate",
        sort: "asc",
      },
      {
        label: "Date Expire",
        field: "dateExpire",
        sort: "asc",
      },
      {
        label: "Actions",
        field: "actions",
        sort: "asc",
      },
    ],
    rows: [],
  };
};

export const DISCOUNT_REGEX = /##DISCOUNT\((\w+?)\)/g; //##DISCOUNT(Your_Code)
export const CHART_KEY =
  "Ngo9BigBOggjHTQxAR8/V1NBaF5cXmZCe0x3Q3xbf1x0ZFRHalhXTndYUj0eQnxTdEFjXX5YcndRQGFUVEB1Wg==";
