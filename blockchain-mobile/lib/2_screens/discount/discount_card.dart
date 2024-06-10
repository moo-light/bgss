import 'package:blockchain_mobile/1_controllers/providers/cart_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/order_provider.dart';
import 'package:blockchain_mobile/4_helper/decorations/card_decoration.dart';
import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/user_discount.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';

class DiscountCard extends StatefulWidget {
  final UserDiscount discount;

  final bool isSearched;

  final bool validation;

  const DiscountCard({
    super.key,
    required this.discount,
    this.isSearched = false,
    this.validation = false,
  });

  @override
  _DiscountCardState createState() => _DiscountCardState();
}

class _DiscountCardState extends State<DiscountCard> {
  late List<ImageProvider> images;
  late bool showFirstImage;

  @override
  void initState() {
    super.initState();
    images = const [
      AssetImage('assets/images/discount-percent.png'),
      AssetImage('assets/images/discount-off.png'),
    ];
    showFirstImage = true;
    startBlinking();
  }

  void startBlinking() {
    Future.delayed(const Duration(seconds: 1), () {
      if (mounted) {
        setState(() {
          showFirstImage = !showFirstImage;
        });
        startBlinking();
      }
    });
  }

  Color getLightColor(Color color, double lightPercentage) {
    assert(lightPercentage >= 0 && lightPercentage <= 100);
    double lightFactor = lightPercentage / 100;
    return color.withOpacity(lightFactor);
  }

  String formatPercentage(double value) {
    if (value % 1 == 0) {
      return value.toInt().toString();
    } else {
      return value.toStringAsFixed(1);
    }
  }

  @override
  Widget build(BuildContext context) {
    final dateFormat = DateFormat('yyyy-MM-dd HH:mm:ss');
    final currentDiscount = context.watch<OrderProvider>().currentDiscount;

    //min product : check the quantity true if  count is more than min
    var carts = context.watch<CartProvider>().carts;
    final productCount = carts
        .map((c) => c.quantity)
        .reduce((value, element) => value + element)
        .toInt();
    bool checkMinQuantity = productCount >= widget.discount.discount.quantityMin;
    //min price : check the total price true if price is more than min
    final productTotal = carts
        .map((c) => c.quantity * (c.product.secondPrice ?? c.product.price))
        .reduce((value, element) => value + element);
    bool checkMinPrice = productTotal >= widget.discount.discount.minPrice;

    return Card(
      margin: const EdgeInsets.all(12.0),
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: cardDecoration.copyWith(
          border:
              currentDiscount?.discount.code == widget.discount.discount.code
                  ? Border.all(
                      width: 3,
                      strokeAlign: BorderSide.strokeAlignOutside,
                      color: kBackgroundColor.withHSLlighting(0.80),
                    )
                  : null,
        ),
        child: Stack(
          children: [
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  "Discount #${widget.discount.discount.id}",
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                Text(
                    "Minimum allowed product: ${(widget.discount.discount.quantityMin)}",
                    style:
                        TextStyle(color: checkMinQuantity ? null : Colors.red)),
                Text(
                    "Max reduce price: ${currencyFormat(widget.discount.discount.maxReduce)}"),
                Text(
                    "Min allowed price: ${currencyFormat(widget.discount.discount.minPrice)}",
                    style: TextStyle(color: checkMinPrice ? null : Colors.red)),
                const SizedBox(height: 8),
                Text(
                  widget.discount.discount.code,
                  style: const TextStyle(
                    fontSize: 12,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                Text(
                  'Expires on: ${dateFormat.format(widget.discount.discount.dateExpire)}',
                  style: const TextStyle(
                    fontSize: 12,
                    color: Colors.grey,
                  ),
                ),
                const Divider(),
                Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    Text(
                      "${formatPercentage((widget.discount.discount.percentage))}%",
                      style: const TextStyle(
                        fontSize: 24,
                        fontWeight: FontWeight.bold,
                        color: kSecondaryColor,
                      ),
                    ),
                    const Spacer(),
                    ElevatedButton(
                      onPressed: !widget.discount.discount.isExpired &&
                              widget.discount != currentDiscount &&
                              checkMinPrice &&
                              checkMinQuantity
                          ? () async {
                              // Navigator.pop(context);
                              Provider.of<OrderProvider>(context, listen: false)
                                  .selectDiscount(widget.discount);
                            }
                          : null,
                      child: const Text(
                        'Apply',
                      ),
                    ),
                  ],
                ),
                const Divider(),
                // Visibility(
                //   visible: !checkMinQuantity,
                //   child: Text(
                //       "you need to buy at most ${widget.discount.discount.quantityMin} product to apply this discount"),
                // ),
                // Visibility(
                //   visible: !checkMinPrice,
                //   child: Text(
                //       "you need to buy at most ${currencyFormat(widget.discount.discount.minPrice)} to apply this discount"),
                // ),
              ],
            ),
            Visibility(
              visible: currentDiscount == widget.discount,
              child: Positioned(
                top: 0,
                right: 10,
                child: IconButton(
                  onPressed: () {
                    context.read<OrderProvider>().selectDiscount(null);
                  },
                  icon: const FaIcon(
                    FontAwesomeIcons.trash,
                    color: kLossColor,
                    size: 18,
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
