import 'dart:async';

import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/cart_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/discount_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/order_provider.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/2_screens/discount/discount_screen.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_button.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';
import 'package:gap/gap.dart';
import 'package:provider/provider.dart';

class CheckoutOrder extends StatefulWidget {
  final String total;

  final num subTotal;

  const CheckoutOrder({super.key, required this.total, required this.subTotal});

  @override
  State<CheckoutOrder> createState() => _CheckoutOrderState();
}

class _CheckoutOrderState extends State<CheckoutOrder> {
  StreamSubscription? _sub;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final selectedCarts = context.watch<CartProvider>().carts;
    final currentDiscount = context.watch<OrderProvider>().currentDiscount;
    final userBalance =
        context.watch<AuthProvider>().currentUser?.userInfo.balance.amount ?? 0;
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
            InkWell(
              onTap: () {
                context.read<DiscountProvider>().getUserDiscount(expire: false);
                Navigator.pushNamed(context, DiscountStorage.routeName);
              },
              child: Row(
                children: [
                  Container(
                    padding: const EdgeInsets.all(10),
                    height: 40,
                    width: 40,
                    decoration: BoxDecoration(
                      color: const Color(0xFFF5F6F9),
                      borderRadius: BorderRadius.circular(10),
                    ),
                    child: SvgPicture.asset("assets/icons/receipt.svg"),
                  ),
                  const Gap(10),
                  Row(
                    children: [
                      Text(
                        currentDiscount?.discount.code ??
                            "Select your discount",
                        style: const TextStyle(
                          color: kTextColor,
                        ),
                      ),
                      const SizedBox(width: 8),
                      const Icon(
                        Icons.arrow_forward_ios,
                        size: 12,
                        color: kTextColor,
                      ),
                    ],
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: Text.rich(
                    TextSpan(
                      text: "Total price:\n",
                      children: [
                        TextSpan(
                          text: "\$${widget.total}",
                          style: const TextStyle(
                              fontSize: 16,
                              color: kTextColor,
                              fontWeight: FontWeight.bold),
                        ),
                      ],
                    ),
                  ),
                ),
                Expanded(
                  child: IsLoadingButton(
                    isLoading: context
                        .watch<OrderProvider>()
                        .createOrderResult
                        .isLoading,
                    child: ElevatedButton(
                      onPressed: () async {
                        if (userBalance >
                            (double.tryParse(widget.total) ?? 0)) {
                          await context.read<OrderProvider>().createOrder(
                                context,
                                carts: selectedCarts,
                              );
                        } else {
                          ToastService.toastError(context,
                              "Total amount exceeds your balance. Please add your balance.");
                        }
                      },
                      child: const Text("Confirm Order"),
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    // Don't forget to dispose the subscription when done
    _sub?.cancel();
    super.dispose();
  }
}
