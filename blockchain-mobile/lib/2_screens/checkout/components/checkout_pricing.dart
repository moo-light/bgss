// ignore_for_file: non_constant_identifier_names

import 'package:blockchain_mobile/1_controllers/providers/order_provider.dart';
import 'package:blockchain_mobile/3_components/text_bold.dart';
import 'package:blockchain_mobile/3_components/user_storage.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:gap/gap.dart';
import 'package:provider/provider.dart';

class CheckoutPricing extends StatelessWidget {
  final String total;

  final String tax;

  final String discount;

  final String TOTAL;

  const CheckoutPricing(
      {super.key,
      required this.total,
      required this.tax,
      required this.discount,
      required this.TOTAL});
  @override
  Widget build(BuildContext context) {
    bool isConsignment = context.watch<OrderProvider>().isConsignment;
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text("Trade Summary", style: Theme.of(context).textTheme.titleLarge),
          const UserStorage(),
          CheckboxListTile.adaptive(
            value: isConsignment,
            onChanged: (v) => {context.read<OrderProvider>().consignment = v!},
            title: const Text("Keep My Products in Store"),
            checkColor: kPrimaryColor,
            checkboxShape: const CircleBorder(),
          ),
          const Divider(),
          Row(
            children: [const Text("Total"), const Spacer(), Text('\$$total')],
          ),
          // Row(
          //   children: [const Text("Tax"), const Spacer(), Text('\$$tax')],
          // ),
          Row(
            children: [
              const Text("Discount"),
              const Spacer(),
              Text('\$$discount')
            ],
          ),
          const Divider(),
          Align(
            alignment: Alignment.centerRight,
            child: Text(
              "Including tax if applicable",
              style: Theme.of(context)
                  .textTheme
                  .bodySmall!
                  .copyWith(color: kHintTextColor),
              textAlign: TextAlign.end,
            ),
          ),
          DefaultTextStyle(
            style: Theme.of(context).textTheme.bodyLarge!,
            child: Row(
              children: [
                const Text("Total"),
                const Spacer(),
                TextBold('\$$TOTAL'),
              ],
            ),
          ),
          const Gap(50)
        ],
      ),
    );
  }
}
