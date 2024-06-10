//API ORRDER URL FROM BLOCK-CHAIN-SERVER

const String localhost = 'http://localhost:8080/api/auth';

//Role CUSTOMER
const String createOrder = '$localhost/create-order/{userId}';
const String getOrderList = '$localhost/get-order-list';
const String getOrderDetail = '$localhost/get-order-by-id/{orderId}';
const String searchOrderByCode = '$localhost/search-order-by-qr_code';

const String newapi = "$localhost/...";