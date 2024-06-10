import 'package:blockchain_mobile/models/enums/gold_unit.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';

import '../constants.dart';
import '../models/Product.dart';

class ProductCard extends StatelessWidget {
  const ProductCard(
      {super.key,
      this.width = 140,
      this.aspectRetio = 1.02,
      required this.product,
      required this.onPress,
      this.showWeight = false});

  final bool showWeight;
  final double width, aspectRetio;
  final Product product;
  final VoidCallback onPress;

  @override
  Widget build(BuildContext context) {
    double ratingAvg = product.avgReview;

    return SizedBox(
      width: width,
      child: GestureDetector(
        onTap: onPress,
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisSize: MainAxisSize.min,
          // mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            AspectRatio(
              aspectRatio: 1,
              child: Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: kSecondaryColor.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(16),
                ),
                child: Center(
                  child: Container(
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(12),
                      image: DecorationImage(
                        image: product.image.image,
                        fit: BoxFit.cover,
                      ),
                    ),
                  ),
                ),
              ),
            ),
            // const SizedBox(height: 8),
            Text(
              product.title,
              style: Theme.of(context).textTheme.bodyMedium,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
            ),
            const Spacer(),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                Text.rich(
                  TextSpan(
                      children: showWeight
                          ? [
                              TextSpan(
                                  text:
                                      "${product.weight} ${product.typeGold.goldUnit.symbol}")
                            ]
                          : [
                              TextSpan(
                                text: currencyFormat(product.price),
                                style: product.secondPrice != null
                                    ? const TextStyle(
                                        color: kHintTextColor,
                                        decoration: TextDecoration.lineThrough,
                                        fontSize: 12)
                                    : null,
                              ),
                              TextSpan(
                                  text: product.secondPrice != null
                                      ? "\n${currencyFormat(product.secondPrice)}"
                                      : "")
                            ]),
                  style: const TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.w600,
                    color: kPrimaryColor,
                  ),
                ),
                // InkWell(
                //   borderRadius: BorderRadius.circular(50),
                //   onTap: () {},
                //   child: Container(
                //     padding: const EdgeInsets.all(6),
                //     height: 24,
                //     width: 24,
                //     decoration: BoxDecoration(
                //       color: product.isFavorite
                //           ? kPrimaryColor.withOpacity(0.15)
                //           : kSecondaryColor.withOpacity(0.1),
                //       shape: BoxShape.circle,
                //     ),
                //     child: SvgPicture.asset(
                //       "assets/icons/Heart Icon_2.svg",
                //       colorFilter: ColorFilter.mode(
                //           product.isFavorite
                //               ? const Color(0xFFFF4848)
                //               : const Color(0xFFDBDEE4),
                //           BlendMode.srcIn),
                //     ),
                //   ),
                // ),
                const Spacer(),
                Container(
                  // margin: const EdgeInsets.only(right: 5),
                  padding:
                      const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                  decoration: BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(14),
                  ),
                  child: Row(
                    children: [
                      Text(
                        ratingAvg.toStringAsFixed(1),
                        style: const TextStyle(
                          fontSize: 10,
                          color: Colors.black,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                      const SizedBox(width: 4),
                      SvgPicture.asset("assets/icons/Star Icon.svg"),
                    ],
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
