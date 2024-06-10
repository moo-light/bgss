import 'dart:math';

import 'package:blockchain_mobile/1_controllers/providers/cart_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/order_provider.dart';
import 'package:blockchain_mobile/2_screens/checkout/components/checkout_buyer_information.dart';
import 'package:blockchain_mobile/models/Cart.dart';
import 'package:flutter/material.dart';
import 'package:gap/gap.dart';
import 'package:provider/provider.dart';

import 'components/checkout_list_item.dart';
import 'components/checkout_option.dart';
import 'components/checkout_order.dart';
import 'components/checkout_pricing.dart';

class CheckOutScreen extends StatefulWidget {
  static const routeName = "/checkout";
  const CheckOutScreen({super.key});

  @override
  State<CheckOutScreen> createState() => _CheckOutScreenState();
}

class _CheckOutScreenState extends State<CheckOutScreen> {
  final _formKey = GlobalKey<FormState>();

  @override
  Widget build(BuildContext context) {
    final carts = context.watch<CartProvider>().carts.toList();
    var currentOrder = context.watch<OrderProvider>().currentOrder;
    // calculate total
    var total = Cart.calculateTotal(carts);
    final currentDiscount = context.watch<OrderProvider>().currentDiscount;
    // check max reduce
    var discount = min(
        (currentDiscount?.discount.percentage ?? 0) / 100 * total,
        currentDiscount?.discount.maxReduce ?? 0);
    var tax = 0;
    var TOTAL = (total + tax - discount).toStringAsFixed(2);
    return Scaffold(
      appBar: AppBar(
        // centerTitle: true,
        title: const Text("Confirm Order"),
      ),
      body: Visibility(
        visible: currentOrder == null,
        replacement: const Center(child: CircularProgressIndicator()),
        child: PopScope(
          canPop: true,
          onPopInvoked: (cp) {
            context.read<CartProvider>().loadCarts();
          },
          child: ListView(
            children: <Widget>[
              CheckoutBuyerInformation(formKey: _formKey),
              const Gap(10),
              CheckoutListItem(carts: carts),
              const Gap(10),
              const CheckoutOption(),
              const Gap(10),
              CheckoutPricing(
                TOTAL: TOTAL,
                discount: discount.toStringAsFixed(2),
                tax: tax.toStringAsFixed(2),
                total: total.toStringAsFixed(2),
              ),
              // Checkout Button
            ],
          ),
        ),
      ),
      bottomNavigationBar: CheckoutOrder(
        total: TOTAL,
        subTotal: total,
      ),
    );
  }
}
