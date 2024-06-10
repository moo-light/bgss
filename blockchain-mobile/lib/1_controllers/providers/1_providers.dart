import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/cart_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/discount_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/image_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/live_rate_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/order_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/otp_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/post_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/products_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/rate_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/register_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/reviews_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/start_screen_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/transaction_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/transfer_gold_unit_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/withdraw_provider.dart';
import 'package:provider/provider.dart';

final providers = [
  ChangeNotifierProvider<RegisterProvider>(create: (_) => RegisterProvider()),
  ChangeNotifierProvider<AuthProvider>(
      create: (context) => AuthProvider()),
  ChangeNotifierProvider<AppImageProvider>(create: (_) => AppImageProvider()),
  // ChangeNotifierProxyProvider<AppImageProvider,AuthProvider>(create: (context) => AuthProvider(),
  // update: (context, value, previous) => AuthProvider.image(value),),
  ChangeNotifierProvider<StartScreenProdvider>(
      create: (_) => StartScreenProdvider()),
  ChangeNotifierProvider<ProductProvider>(create: (_) => ProductProvider()),
  ChangeNotifierProvider<PostProvider>(create: (_) => PostProvider()),
  ChangeNotifierProvider<CartProvider>(create: (_) => CartProvider()),
  ChangeNotifierProvider<OrderProvider>(create: (_) => OrderProvider()),
  ChangeNotifierProvider<OtpProvider>(create: (_) => OtpProvider()),
  ChangeNotifierProvider<LiveRateProvider>(create: (_) => LiveRateProvider()),
  ChangeNotifierProvider<DiscountProvider>(create: (_) => DiscountProvider()),
  ChangeNotifierProvider<TransactionProvider>(
      create: (_) => TransactionProvider()),
  ChangeNotifierProvider<WithdrawProvider>(create: (_) => WithdrawProvider()),
  ChangeNotifierProvider<ReviewProvider>(create: (_) => ReviewProvider()),
  ChangeNotifierProvider<RateProvider>(create: (_) => RateProvider()),
  ChangeNotifierProvider<TransferGoldUnitProvider>(
      create: (_) => TransferGoldUnitProvider()),
];
