import 'package:blockchain_mobile/1_controllers/providers/1_providers.dart';
import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/cart_provider.dart';
import 'package:blockchain_mobile/1_controllers/services/others/cookie_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/2_screens/init_screen.dart';
import 'package:blockchain_mobile/2_screens/start_screen/start_screen.dart';
import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

import 'routes.dart';
import 'theme.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  SharedPreferences.getInstance().then((pref) => pref.setString("live-rate",
      '{ "endpoint": "live", "ask": 2327.8, "symbol": "XAUUSD", "bid": 2327.46, "mid": 2327.63, "changedPrice":0.0,     "changedPercentage":0.0, "lastUpdated":"23:06:42", "ts": 1717258002 }'));
  await dotenv.load(fileName: ".env");
  await CookieService().init();
  await SystemChrome.setPreferredOrientations([DeviceOrientation.portraitUp]);
  runApp(const MyApp());
}

final GlobalKey globalKey = GlobalKey();
final GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();

void notifyTokenExpired() {
  ToastService.toastError(globalKey.currentContext!, "Token Expired!",
      icon: const FaIconGen(FontAwesomeIcons.warning));
  Navigator.pushReplacementNamed(
      globalKey.currentContext!, InitScreen.routeName);
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  // This widget is the root of your application.
  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      key: navigatorKey,
      providers: providers,
      builder: (context, child) {
        Future.delayed(
            Durations.extralong4,
            () => Provider.of<AuthProvider>(context, listen: false).isCustomer
                ? Provider.of<CartProvider>(context, listen: false).loadCarts()
                : null);
        return child!;
      },
      child: Container(
        key: globalKey,
        child: MaterialApp(
          debugShowCheckedModeBanner: false,
          title: 'Blockchain Gold Selling System ',
          theme: AppTheme.lightTheme(context),
          // darkTheme: AppTheme.darkTheme(context),
          initialRoute: StartScreen.routeName,
          routes: routes,
        ),
      ),
    );
  }
}
