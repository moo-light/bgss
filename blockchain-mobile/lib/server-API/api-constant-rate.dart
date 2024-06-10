//API ORRDER URL FROM BLOCK-CHAIN-SERVER

const String localhost = 'http://localhost:8080/api/auth';

//Role GUESS
const String getAllRateByUserIdAndPostId = '$localhost/show-all-rate-user-in-post';
const String getAllRate = '$localhost/show-all-rate';
const String getAllRateInPost = '$localhost/show-all-rate-in-post';
const String getRateById = '$localhost/get-rate-by-id/{rateId}';
const String createRate = '$localhost/create-rate';
const String updateRate = '$localhost/update-rate/{rateId}';
const String deleteRate = '$localhost/delete-rate/{rateId}';