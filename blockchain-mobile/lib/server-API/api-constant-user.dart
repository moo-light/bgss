//API USER MANAGEMENT URL FROM BLOCK-CHAIN-SERVER
const String localhost = 'http://localhost:8080/api/auth';

//Role ADMIN
const String listUser =  '$localhost/show-list-user';
const String getUser = '$localhost/user/get-user/{id}';
const String lockOrActiveUser = "$localhost/user/lock-user/{id}";


//Role ADMIN, STAFF, CUSTOMER
const String changePassword = '$localhost/user/change-password/{id}';
const String newapi = '$localhost/...';
