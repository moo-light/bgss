import 'package:blockchain_mobile/1_controllers/providers/cart_provider.dart';
import 'package:blockchain_mobile/2_screens/product_details/product_details_screen.dart';
import 'package:blockchain_mobile/3_components/dialogs/confirm_dialog.dart';
import 'package:blockchain_mobile/3_components/rounded_icon_btn.dart';
import 'package:flutter/material.dart';
import 'package:gap/gap.dart';
import 'package:provider/provider.dart';

import '../../../constants.dart';
import '../../../models/Cart.dart';

class CartCard extends StatefulWidget {
  const CartCard({
    super.key,
    required this.cart,
  });

  final Cart cart;

  @override
  State<CartCard> createState() => _CartCardState();
}

class _CartCardState extends State<CartCard> {
  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        // Checkbox(
        //   value: widget.cart.selected,
        //   onChanged: (value) {
        //     setState(() {
        //       widget.cart.selected = value!;
        //     });
        //     context.read<CartProvider>().updateCartItem(widget.cart);
        //   },
        // ),
        GestureDetector(
          onTap: () {
            Navigator.pushNamed(
              context,
              ProductDetailsScreen.routeName,
              arguments: ProductDetailsArguments(
                product: widget.cart.product,
                fromCartScreen: true,
              ),
            );
          },
          child: SizedBox(
            width: 88,
            child: AspectRatio(
              aspectRatio: 0.88,
              child: Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: const Color(0xFFF5F6F9),
                  borderRadius: BorderRadius.circular(15),
                  boxShadow: const [
                    BoxShadow(blurRadius: 5, color: kIconColor)
                  ],
                ),
                child: widget.cart.product.image,
              ),
            ),
          ),
        ),
        const SizedBox(width: 20),
        Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            SizedBox(
              width: 200,
              child: Text(
                widget.cart.product.title,
                style: const TextStyle(color: Colors.black, fontSize: 16),
                maxLines: 2,
              ),
            ),
            const SizedBox(height: 8),
            // Text.rich(
            //   TextSpan(
            //     // text: "\$${widget.cart.product.price}",
            //     children: [
            //       TextSpan(
            //           text: widget.cart.product.secondPrice != null
            //               ? "${currencyFormat(widget.cart.product.secondPrice)} "
            //               : ""),
            //       // const TextSpan(text: "\n"),
            //       TextSpan(
            //         text: currencyFormat(widget.cart.product.price),
            //         style: widget.cart.product.secondPrice != null
            //             ? const TextStyle(
            //                 color: kHintTextColor,
            //                 decoration: TextDecoration.lineThrough,
            //                 fontSize: 12)
            //             : null,
            //       ),
            //     ],
            //     style: const TextStyle(
            //         fontWeight: FontWeight.w600, color: kPrimaryColor),
            //   ),
            // ),
            Text(
              currencyFormat(widget.cart.amount),
              style: const TextStyle(
                  fontWeight: FontWeight.w600, color: kPrimaryColor),
            ),
            const Gap(10),
            Row(
              children: [
                RoundedIconBtn(
                    icon: Icons.remove,
                    press: () async {
                      if (widget.cart.quantity > 1) {
                        context
                            .read<CartProvider>()
                            .updateCartItem(widget.cart, false);
                      } else {
                        bool result = await showDialog(
                            context: context,
                            builder: (context) => SimpleConfirmDialog(
                                  onConfirm: () {
                                    Navigator.pop(context, true);
                                  },
                                  title: "Remove from Cart",
                                  content: "Do you want to remove this item?",
                                  onCancel: () {
                                    Navigator.pop(context, false);
                                  },
                                  confirmType: EConfirmType.YES_NO,
                                ));
                        if (result == true) {
                          context
                              .read<CartProvider>()
                              .removeFromCart(context, widget.cart);
                        }
                      }
                    }),
                SizedBox(
                    width: 30,
                    child: Text(
                      widget.cart.quantity.toString(),
                      style: Theme.of(context).textTheme.bodyLarge,
                      textAlign: TextAlign.center,
                    )),
                RoundedIconBtn(
                    icon: Icons.add,
                    press: () {
                      context
                          .read<CartProvider>()
                          .updateCartItem(widget.cart, true);
                    }),
              ],
            ),
          ],
        ),
        const Spacer(),
        //   IconButton(
        //     icon: const Icon(Icons.delete, color: Colors.red),
        //     onPressed: () {
        //       // Thêm hộp thoại xác nhận trước khi xóa
        //       showDialog(
        //         context: context,
        //         builder: (context) => AlertDialog(
        //           title: const Text('Confirm Deletion'),
        //           content: const Text('Do you really want to delete this item from the cart?'),
        //           actions: <Widget>[
        //             TextButton(
        //               onPressed: () => Navigator.of(context).pop(),
        //               child: const Text('Cancel'),
        //             ),
        //             TextButton(
        //               onPressed: () {
        //                 context.read<CartProvider>().removeFromCart(widget.cart);
        //                 Navigator.of(context).pop();
        //               },
        //               child: const Text('Delete'),
        //             ),
        //           ],
        //         ),
        //       );
        //     },
        //   ),
      ],
    );
  }
}
