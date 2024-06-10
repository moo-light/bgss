import 'package:blockchain_mobile/1_controllers/providers/cart_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/order_provider.dart';
import 'package:blockchain_mobile/2_screens/checkout/checkout_screen.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../constants.dart';

class CheckoutCard extends StatelessWidget {
  final num total;

  const CheckoutCard({super.key, required this.total});

  @override
  Widget build(BuildContext context) {
    final carts = context.watch<CartProvider>().carts;
    return Container(
      padding: const EdgeInsets.symmetric(
        vertical: 16,
        horizontal: 20,
      ),
      // height: 174,
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: const BorderRadius.only(
          topLeft: Radius.circular(30),
          topRight: Radius.circular(30),
        ),
        boxShadow: [
          BoxShadow(
            offset: const Offset(0, -15),
            blurRadius: 20,
            color: const Color(0xFFDADADA).withOpacity(0.15),
          )
        ],
      ),
      child: SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Row(
              children: [
                // Container(
                //   padding: const EdgeInsets.all(10),
                //   height: 40,
                //   width: 40,
                //   decoration: BoxDecoration(
                //     color: const Color(0xFFF5F6F9),
                //     borderRadius: BorderRadius.circular(10),
                //   ),
                //   child: SvgPicture.asset("assets/icons/receipt.svg"),
                // ),
                // const Spacer(),
                // const Text("Add voucher code"),
                // const SizedBox(width: 8),
                // const Icon(
                //   Icons.arrow_forward_ios,
                //   size: 12,
                //   color: kTextColor,
                // )
              ],
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: Text.rich(
                    TextSpan(
                      text: "Temp price:\n",
                      children: [
                        TextSpan(
                          text: "\$${total.toStringAsFixed(2)}",
                          style: const TextStyle(
                            fontSize: 16,
                            color: kTextColor,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),
                Expanded(
                  child: ElevatedButton(
                    onPressed: () {
                      if (carts.isNotEmpty) {
                        context.read<OrderProvider>().prepareOrder(context);
                        context.read<OrderProvider>().currentDiscount = null;
                        Navigator.pushNamed(context, CheckOutScreen.routeName);
                      }
                    },
                    child: const Text("Check Out"),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
