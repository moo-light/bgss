import 'package:blockchain_mobile/models/Cart.dart';
import 'package:flutter/material.dart';

import '../../../3_components/rounded_icon_btn.dart';
import '../../../constants.dart';

class ColorDots extends StatefulWidget {
  const ColorDots({
    super.key,
    required this.cart,
  });
  final Cart cart;

  @override
  State<ColorDots> createState() => _ColorDotsState();
}

class _ColorDotsState extends State<ColorDots> {
  @override
  Widget build(BuildContext context) {
    // Now this is fixed and only for demo
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Row(
            mainAxisSize: MainAxisSize.min,
            children: [
              // ...List.generate(
              //   product.colors.length,
              //   (index) => ColorDot(
              //     color: product.colors[index],
              //     isSelected: index == selectedColor,
              //   ),
              // ),

              const Spacer(),
              RoundedIconBtn(
                icon: Icons.remove,
                press: widget.cart.quantity > 1
                    ? () => setState(() {
                          widget.cart.quantity--;
                        })
                    : null,
              ),
              SizedBox(
                  width: 30,
                  child: Text(
                    widget.cart.quantity.toString(),
                    style: Theme.of(context).textTheme.bodyLarge,
                    textAlign: TextAlign.center,
                  )),
              RoundedIconBtn(
                icon: Icons.add,
                showShadow: true,
                press: () => setState(() {
                  widget.cart.quantity++;
                }),
              ),
            ],
          ),
        ),
      ],
    );
  }
}

class ColorDot extends StatelessWidget {
  const ColorDot({
    super.key,
    required this.color,
    this.isSelected = false,
  });

  final Color color;
  final bool isSelected;

  @override
  Widget build(BuildContext context) {
    return Container(
      // margin: const EdgeInsets.only(right: 2),
      padding: const EdgeInsets.all(8),
      height: 40,
      width: 40,
      decoration: BoxDecoration(
        color: Colors.transparent,
        border:
            Border.all(color: isSelected ? kPrimaryColor : Colors.transparent),
        shape: BoxShape.circle,
      ),
      child: DecoratedBox(
        decoration: BoxDecoration(
          color: color,
          shape: BoxShape.circle,
        ),
      ),
    );
  }
}
