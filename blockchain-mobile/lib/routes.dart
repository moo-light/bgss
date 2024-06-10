import 'package:blockchain_mobile/1_controllers/providers/otp_provider.dart';
import 'package:blockchain_mobile/2_screens/card_info/card_info_edit_screen.dart';
import 'package:blockchain_mobile/2_screens/card_info/card_info_screen.dart';
import 'package:blockchain_mobile/2_screens/checkout/checkout_screen.dart';
import 'package:blockchain_mobile/2_screens/init_screen.dart';
import 'package:blockchain_mobile/2_screens/my_account/my_account_screen.dart';
import 'package:blockchain_mobile/2_screens/my_order_list/my_order_list_screen.dart';
import 'package:blockchain_mobile/2_screens/order_success/order_success_screen.dart';
import 'package:blockchain_mobile/2_screens/post_detail/post_detail_screen.dart';
import 'package:blockchain_mobile/2_screens/products/products_screen.dart';
import 'package:blockchain_mobile/2_screens/start_screen/start_screen.dart';
import 'package:blockchain_mobile/2_screens/transaction_detail/transaction_detail_screen.dart';
import 'package:blockchain_mobile/2_screens/user-rating/user_rating_screen.dart';
import 'package:blockchain_mobile/2_screens/user-review/user_review_screen.dart';
import 'package:blockchain_mobile/2_screens/withdraw_detail/withdraw_detail_screen.dart';
import 'package:blockchain_mobile/3_components/qr_scanner.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

import '2_screens/cart/cart_screen.dart';
import '2_screens/complete_profile/complete_profile_screen.dart';
import '2_screens/contract/contract_screen.dart';
import '2_screens/discount/discount_screen.dart';
import '2_screens/find_order/find_order_screen.dart';
import '2_screens/forgot_password/forgot_password_screen.dart';
import '2_screens/home/home_screen.dart';
import '2_screens/login_success/login_success_screen.dart';
import '2_screens/my_discount/my_discount_screen.dart';
import '2_screens/order_detail/order_detail_screen.dart';
import '2_screens/otp/otp_screen.dart';
import '2_screens/product_details/product_details_screen.dart';
import '2_screens/profile/profile_screen.dart';
import '2_screens/sign_in/sign_in_screen.dart';
import '2_screens/sign_up/sign_up_screen.dart';
import '2_screens/splash/splash_screen.dart';

// We use name route
// All our routes will be available here
final Map<String, WidgetBuilder> routes = {
  InitScreen.routeName: (context) => const InitScreen(),
  SplashScreen.routeName: (context) => const SplashScreen(),
  SignInScreen.routeName: (context) => const SignInScreen(),
  StartScreen.routeName: (context) => const StartScreen(),
  ForgotPasswordScreen.routeName: (context) => const ForgotPasswordScreen(),
  LoginSuccessScreen.routeName: (context) => const LoginSuccessScreen(),
  LoginSuccessScreen.registerRouteName: (context) =>
      const LoginSuccessScreen(isRegisterSuccess: true),
  SignUpScreen.routeName: (context) => const SignUpScreen(),
  CompleteProfileScreen.routeName: (context) => const CompleteProfileScreen(),
  OtpScreen.routeName: (context) => const OtpScreen(
        type: OtpEType.ORDER,
      ),
  OtpScreen.routeNameTransaction: (context) => const OtpScreen(
        type: OtpEType.TRANSACTION,
      ),
  OtpScreen.routeNameWithdraw: (context) => const OtpScreen(
        type: OtpEType.WITHDRAW,
      ),
  HomeScreen.routeName: (context) => const HomeScreen(),
  ProductsScreen.routeName: (context) => const ProductsScreen(),
  ProductDetailsScreen.routeName: (context) => const ProductDetailsScreen(),
  CartScreen.routeName: (context) => const CartScreen(),
  PostDetailsScreen.routeName: (context) => const PostDetailsScreen(),
  ProfileScreen.routeName: (context) => const ProfileScreen(),
  MyAccountScreen.routeName: (context) => const MyAccountScreen(),
  CheckOutScreen.routeName: (context) => const CheckOutScreen(),
  OrderSuccessScreen.routeName: (context) => const OrderSuccessScreen(),
  MyOrderListScreen.routeName: (context) => const MyOrderListScreen(),
  OrderDetailScreen.routeName: (context) => const OrderDetailScreen(),
  TransactionDetailScreen.routeName: (context) =>
      const TransactionDetailScreen(),
  FindOrderScreen.routeName: (context) => const FindOrderScreen(),
  WithdrawDetailScreen.routeName: (context) => WithdrawDetailScreen(),
  MyDiscountScreen.routeName: (context) => const MyDiscountScreen(),
  DiscountStorage.routeName: (content) => const DiscountStorage(),
  CardInfoScreen.routeName: (content) => const CardInfoScreen(),
  CardInfoEditScreen.routeName: (content) => const CardInfoEditScreen(),
  ContractScreen.routeName: (content) => const ContractScreen(),
  QrScanner.routeName: (content) => const QrScanner(),
  UserReviewScreen.routeName: (content) => const UserReviewScreen(),
  UserRateScreen.routeName: (content) => const UserRateScreen(),
};
