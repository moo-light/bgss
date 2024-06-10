import 'package:animated_splash_screen/animated_splash_screen.dart';
import 'package:blockchain_mobile/1_controllers/providers/start_screen_provider.dart';
import 'package:blockchain_mobile/2_screens/init_screen.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class StartScreen extends StatelessWidget {
  static const String routeName = '/start';

  const StartScreen({super.key});
  @override
  Widget build(BuildContext context) {
    context.read<StartScreenProdvider>().getSize(context);
    Future.microtask(() async {
      await Future.delayed(!kDebugMode
          ? const Duration(seconds: 5)
          : const Duration(seconds: 0));

      if (context.mounted) {
        if (Navigator.canPop(context) == true) Navigator.pop(context);
        // Navigator.pushReplacementNamed(context, InitScreen.routeName);
      }
    });
    var size = context.watch<StartScreenProdvider>().size;
    return AnimatedSplashScreen.withScreenRouteFunction(
      splash: Image.asset("assets/images/BGSS Logo.gif"),
      splashIconSize: size,
      disableNavigation: true,
      screenRouteFunction: () async {
        return InitScreen.routeName;
      }, // disableNavigation: true,
      splashTransition: null,
      animationDuration: const Duration(seconds: 3),
      // screenRouteFunction: () async {
      //   return InitScreen.routeName;
      // },
    );
  }
}
