import 'package:blockchain_mobile/2_screens/sign_in/sign_in_screen.dart';
import 'package:flutter/material.dart';

class LoginSuccessScreen extends StatelessWidget {
  static String routeName = "/login_success";
  static String registerRouteName = "/register_success";

  final bool isRegisterSuccess;

  const LoginSuccessScreen({super.key, this.isRegisterSuccess = false});
  @override
  Widget build(BuildContext context) {
    if (!isRegisterSuccess) {
      Future.microtask(() async {
        await Future.delayed(const Duration(seconds: 2));
        if (context.mounted) {
          Navigator.of(context).popUntil((route) => route.isFirst);
        }
      });
    }
    final data = isRegisterSuccess ? "Sign up Success" : "Sign in Success";
    final String? message =
        (ModalRoute.of(context)?.settings.arguments as String?);
    return Scaffold(
      appBar: AppBar(
        leading: const SizedBox(),
        title: Text(data),
      ),
      body: Column(
        mainAxisSize: MainAxisSize.max,
        children: [
          const SizedBox(height: 16),
          Container(
            height: MediaQuery.of(context).size.height * 0.4,
            decoration: BoxDecoration(
              shape: BoxShape.rectangle,
              image: DecorationImage(
                  image: Image.asset(
                "assets/images/success.png",
              ).image),
            ),
          ),
          const SizedBox(height: 16),
          Text(
            data,
            style: const TextStyle(
              fontSize: 30,
              fontWeight: FontWeight.bold,
              color: Colors.black,
            ),
          ),
          Expanded(
            child: Visibility(
              visible: isRegisterSuccess,
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 50.0),
                child: Text(message.toString(),
                    style: const TextStyle(fontSize: 18),
                    textAlign: TextAlign.center),
              ),
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20),
            child: ElevatedButton(
              style: ElevatedButton.styleFrom(
                minimumSize: const Size(double.infinity, 48),
              ),
              onPressed: () => {
                Navigator.of(context).popUntil((route) => route.isFirst),
                if (isRegisterSuccess)
                  Navigator.of(context).pushNamed(SignInScreen.routeName),
              },
              child: const Text("Back to home"),
            ),
          ),
          const Spacer(),
        ],
      ),
    );
  }
}
