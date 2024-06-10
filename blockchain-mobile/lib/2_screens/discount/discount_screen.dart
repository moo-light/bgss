import 'package:blockchain_mobile/1_controllers/providers/cart_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/discount_provider.dart';
import 'package:blockchain_mobile/3_components/failed_information.dart';
import 'package:blockchain_mobile/4_helper/decorations/text_styles.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../models/user_discount.dart';
import 'discount_card.dart';

class DiscountStorage extends StatefulWidget {
  const DiscountStorage({super.key});
  static String routeName = "/discounts";

  @override
  State<DiscountStorage> createState() => _DiscountStorageState();
}

class _DiscountStorageState extends State<DiscountStorage> {
  late Future<List<UserDiscount>> _discountsFuture;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final userDiscounts =
        context.watch<DiscountProvider>().getUserDiscountResult;
    final carts = context.watch<CartProvider>().carts;
    final List<UserDiscount>? discounts = userDiscounts.result;
    final productTotal = carts
        .map((c) => c.quantity * (c.product.secondPrice ?? c.product.price))
        .reduce((value, element) => value + element);
    return Scaffold(
      appBar: AppBar(
        title: const Text('Select Discount'),
        actions: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Text(
              currencyFormat(productTotal),
              style: boldTextStyle.copyWith(
                color: kPrimaryColor,
              ),
            ),
          )
        ],
      ),
      body: Builder(builder: (context) {
        if (userDiscounts.isLoading) {
          return const Center(child: CircularProgressIndicator());
        }

        if (userDiscounts.isError) {
          return FailedInformation(child: Text(userDiscounts.error));
        }

        if (discounts == null || discounts.isEmpty) {
          return const FailedInformation(
              child: Text("No discount available for selection"));
        }
        return Container(
          decoration: const BoxDecoration(),
          child: ListView.builder(
            itemCount: discounts.length,
            itemBuilder: (context, index) {
              return Padding(
                padding: const EdgeInsets.all(8.0),
                child: DiscountCard(discount: discounts[index]),
              );
            },
          ),
        );
      }),
    );
  }
}
