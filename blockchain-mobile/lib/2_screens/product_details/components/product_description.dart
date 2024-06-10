import 'package:blockchain_mobile/4_helper/decorations/text_styles.dart';
import 'package:blockchain_mobile/models/enums/gold_unit.dart';
import 'package:flutter/material.dart';
import 'package:flutter_html/flutter_html.dart';
import 'package:gap/gap.dart';
import 'package:html/parser.dart' as htmlparser;

import '../../../constants.dart';
import '../../../models/Product.dart';

class ProductDescription extends StatelessWidget {
  const ProductDescription({
    super.key,
    required this.product,
  });

  final int? maxlines = 4;
  final Product product;

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20),
          child: Text(
            product.title,
            style: Theme.of(context).textTheme.titleLarge,
          ),
        ),
        SizedBox(
          height: 50,
          child: Align(
            alignment: Alignment.centerLeft,
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: Text.rich(
                TextSpan(children: [
                  TextSpan(
                      text: product.secondPrice != null
                          ? "${currencyFormat(product.secondPrice)} "
                          : ""),
                  TextSpan(
                    text: currencyFormat(product.price),
                    style: product.secondPrice != null
                        ? const TextStyle(
                            color: kHintTextColor,
                            decoration: TextDecoration.lineThrough,
                            fontSize: 12)
                        : null,
                  ),
                ]),
                style: Theme.of(context).textTheme.titleMedium!.copyWith(
                    color: kPrimaryColor, fontWeight: FontWeight.bold),
              ),
            ),
          ),
        ),
        SizedBox(
          height: 30,
          child: Align(
            alignment: Alignment.centerLeft,
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20),
              child: Text.rich(
                TextSpan(
                  text: 'Stock: ',
                  style: const TextStyle(fontWeight: FontWeight.bold),
                  children: <TextSpan>[
                    TextSpan(
                      text: '${product.unitOfStock} ',
                      children: [
                        TextSpan(
                          text:
                              "(${product.unitOfStock > 0 ? "In Stock" : "Out Of Stock"})",
                          style: TextStyle(
                              color: product.unitOfStock > 0
                                  ? kWinColor
                                  : kLossColor,
                              fontWeight: FontWeight.bold),
                        )
                      ],
                      style: const TextStyle(fontWeight: FontWeight.normal),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ),
        const Gap(16),
        Align(
          alignment: Alignment.centerLeft,
          child: Container(
            padding: const EdgeInsets.only(
              left: 20,
              right: 64,
            ),
            child: Text.rich(
              TextSpan(text: "Category: ", style: boldTextStyle, children: [
                TextSpan(
                    text: product.category.categoryName,
                    style: const TextStyle(color: kPrimaryColor))
              ]),
            ),
          ),
        ),
        Align(
          alignment: Alignment.centerLeft,
          child: Container(
            padding: const EdgeInsets.only(
              left: 20,
              right: 64,
            ),
            child: Text.rich(
              TextSpan(
                style: boldTextStyle,
                text: "Type Gold: ",
                children: [
                  TextSpan(
                    text: product.typeGold.typeName,
                    style: const TextStyle(color: kPrimaryColor),
                  )
                ],
              ),
            ),
          ),
        ),
        Align(
          alignment: Alignment.centerLeft,
          child: Container(
            padding: const EdgeInsets.only(
              left: 20,
              right: 64,
            ),
            child: Text.rich(
              TextSpan(text: "Weight: ", style: boldTextStyle, children: [
                TextSpan(
                    text:
                        "${numberFormat(product.weight)} ${product.typeGold.goldUnit.symbol}",
                    style: const TextStyle(color: kPrimaryColor))
              ]),
            ),
          ),
        ),
        Description(product: product, maxlines: maxlines),
      ],
    );
  }
}

class Description extends StatefulWidget {
  final int? maxlines;

  const Description({
    super.key,
    required this.product,
    this.maxlines,
  });

  final Product product;

  @override
  State<Description> createState() => _DescriptionState();
}

class _DescriptionState extends State<Description> {
  late int? maxlines = widget.maxlines;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final bool length = widget.product.description.length > 255;
    return Column(
      children: [
        AnimatedContainer(
          duration: Durations.medium4,
          child: Padding(
            padding: const EdgeInsets.only(
              left: 20,
              right: 64,
            ),
            child: Html.fromDom(
              document: htmlparser.parse(widget.product.description),
              style: {"body": Style(maxLines: maxlines ?? 9999)},
            ),
          ),
        ),
        Padding(
          padding: const EdgeInsets.symmetric(
            horizontal: 20,
            vertical: 12,
          ),
          child: Visibility(
            visible: length,
            child: GestureDetector(
              onTap: () {
                setState(() {
                  maxlines =
                      maxlines == widget.maxlines ? 9999 : widget.maxlines;
                });
              },
              child: const Row(
                children: [
                  Text(
                    "See More Detail",
                    style: TextStyle(
                      fontWeight: FontWeight.w600,
                      color: kPrimaryColor,
                    ),
                  ),
                  SizedBox(width: 5),
                  Icon(
                    Icons.arrow_forward_ios,
                    size: 12,
                    color: kPrimaryColor,
                  ),
                ],
              ),
            ),
          ),
        )
      ],
    );
  }
}
