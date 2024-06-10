import 'package:blockchain_mobile/1_controllers/providers/discount_provider.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'components/discount_list.dart';

class MyDiscountScreen extends StatefulWidget {
  static const String routeName = "/my-discounts";

  const MyDiscountScreen({super.key});

  @override
  State<MyDiscountScreen> createState() => _MyDiscountScreenState();
}

class _MyDiscountScreenState extends State<MyDiscountScreen> {
  var expire = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('My Discounts'),
      ),
      body: SafeArea(
        child: RefreshIndicator.adaptive(
          onRefresh: () =>
              context.read<DiscountProvider>().getUserDiscount(expire: expire),
          child: const SingleChildScrollView(
            physics: AlwaysScrollableScrollPhysics(),
            child: Column(
              children: [
                // SearchDiscount(),
                // Gap(50),
                DiscountList(),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
