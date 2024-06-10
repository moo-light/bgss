import 'package:flutter/material.dart';

class OrderSuccessScreen extends StatelessWidget {
  static String routeName = "/order_success";
  const OrderSuccessScreen({super.key});
  @override
  Widget build(BuildContext context) {
    final agrs =
        ModalRoute.of(context)!.settings.arguments as Map<String, dynamic>;
    Future.microtask(() async {
      await Future.delayed(const Duration(seconds: 2));
      if (context.mounted) {
        Navigator.of(context).popUntil((route) => route.isFirst);
      }
    });
    return Scaffold(
      appBar: AppBar(
        leading: const SizedBox(),
        title: const Text("Login Success"),
      ),
      body: Column(
        mainAxisSize: MainAxisSize.max,
        children: [
          const SizedBox(height: 16),
          Image.asset(
            "assets/images/success.png",
            height: MediaQuery.of(context).size.height * 0.4, //40%
          ),
          const SizedBox(height: 16),
          Text(
            agrs["failed"] != null ? "Order Cancelled" : "Order Success",
            style: const TextStyle(
              fontSize: 30,
              fontWeight: FontWeight.bold,
              color: Colors.black,
            ),
          ),
          const Spacer(),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20),
            child: ElevatedButton(
              onPressed: () {
                if (context.mounted) {
                  Navigator.of(context).popUntil((route) => route.isFirst);
                }
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
