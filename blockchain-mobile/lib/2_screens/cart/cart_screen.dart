import 'dart:math';

import 'package:blockchain_mobile/1_controllers/providers/cart_provider.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_screen.dart';
import 'package:blockchain_mobile/models/Cart.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:provider/provider.dart';

import 'components/cart_card.dart';
import 'components/check_out_card.dart';

class CartScreen extends StatefulWidget {
  static String routeName = "/cart";

  const CartScreen({super.key});

  @override
  State<CartScreen> createState() => _CartScreenState();
}

class _CartScreenState extends State<CartScreen> {
  @override
  Widget build(BuildContext context) {
    final carts = context.watch<CartProvider>().carts;
    final isLoading = context.watch<CartProvider>().isLoading;
    final num total = Cart.calculateTotal(carts);
    if (isLoading) return const LoadingScreen();
    return Scaffold(
      appBar: AppBar(
        title: Column(
          children: [
            const Text(
              "Your Cart",
              style: TextStyle(color: Colors.black),
            ),
            Text(
              "${carts.length} items",
              style: Theme.of(context).textTheme.bodySmall,
            ),
          ],
        ),
      ),
      body: Visibility(
        visible: carts.isNotEmpty,
        replacement: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisSize: MainAxisSize.max,
          children: [
            SizedBox(
              width: MediaQuery.of(context).size.width / 2,
              child: SizedBox(
                  width: min(MediaQuery.of(context).size.width / 2,
                      MediaQuery.of(context).size.height / 2),
                  child: Image.asset("assets/images/Default Product.png")),
            ),
            const Center(
                child: Text(
              "No products in cart yet",
              style: TextStyle(fontSize: 16),
            ))
          ],
        ),
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: ListView.builder(
            itemCount: carts.length,
            itemBuilder: (context, index) => Padding(
              padding: const EdgeInsets.symmetric(vertical: 10),
              child: Dismissible(
                key: Key(carts[index].product.id.toString()),
                direction: DismissDirection.endToStart,
                onDismissed: (direction) {
                  context
                      .read<CartProvider>()
                      .removeFromCart(context, carts[index]);
                },
                background: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 20),
                  decoration: BoxDecoration(
                    color: const Color(0xFFFFE6E6),
                    borderRadius: BorderRadius.circular(15),
                  ),
                  child: Row(
                    children: [
                      const Spacer(),
                      SvgPicture.asset("assets/icons/Trash.svg"),
                    ],
                  ),
                ),
                child: CartCard(cart: carts[index]),
              ),
            ),
          ),
        ),
      ),
      bottomNavigationBar: Visibility(
        visible: carts.isNotEmpty,
        child: CheckoutCard(total: total),
      ),
    );
  }
}
