//API USER INFO MANAGEMENT URL FROM BLOCK-CHAIN-SERVER
const String localhost = 'http://localhost:8080/api/auth';

//Role ADMIN
const String createUserInfo = '$localhost/create-user-info';
const String updateUserInfo = '$localhost/update-user-info/{userId}';

//Role ADMIN, STAFF, CUSTOMER
const String showUserInfo = '$localhost/show-user-info/{id}';
const String avatarUpdate = '$localhost/update-avatar/{userInfoId}';
const String ciCardUpdate = '$localhost/update-cicard-image/{id}';


const String newapi = '$localhost/...';