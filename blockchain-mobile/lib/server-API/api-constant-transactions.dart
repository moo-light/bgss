//API TRANSACTIONS URL FROM BLOCK-CHAIN-SERVER

import 'package:blockchain_mobile/constants.dart';


//ROLE CUSTOMER

//Gold Inventory of USER
final String goldInventory = '$basePath/gold-inventory';

//Transactions
final String transaction = '$basePath/transactions/{userInfoId}';
final String requestWithdrawGold = '$basePath/request-withdraw-gold/{userInfoId}';
final String cancelWithdrawal = '$basePath/cancel/{withdrawalId}';
final String transactionListUser = '$basePath/user-transaction-list/{userInfoId}';
final String withdraws = '$basePath/withdraw-list-userinfo/{userInfoId}';
