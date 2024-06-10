import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/Cart.dart';
import 'package:flutter/material.dart';

class CheckoutListItem extends StatelessWidget {
  final List<Cart> carts;

  const CheckoutListItem({super.key, required this.carts});

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(4),
      child: ListView.builder(
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        itemCount: carts.length,
        itemBuilder: (context, index) {
          return Container(
            decoration: BoxDecoration(
              gradient: LinearGradient(
                  begin: Alignment.centerLeft,
                  end: Alignment.centerRight,
                  colors: [
                    Colors.grey.shade100,
                    kBackgroundColor.withHSLlighting(93)
                  ]),
              color: kBackgroundColor.withHSLlighting(93),
              boxShadow: [
                BoxShadow(
                  color: Colors.grey.shade400,
                  blurRadius: 3,
                )
              ],
            ),

            margin: const EdgeInsets.symmetric(
                vertical: 4.0, horizontal: 8.0), // Margin around the container
            child: ListTile(
              leading: AspectRatio(
                  aspectRatio: 1, child: carts[index].product.image),
              title: Text(carts[index].product.title),
              subtitle: Text('${carts[index].quantity}x'),
              trailing: Text(
                  '\$${carts[index].product.secondPrice ?? carts[index].product.price * carts[index].quantity}',
                  style: const TextStyle(
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                      color: kPrimaryColor)),
            ),
          );
        },
      ),
    );
  }
}
