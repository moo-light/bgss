import 'package:blockchain_mobile/1_controllers/providers/discount_provider.dart';
import 'package:blockchain_mobile/3_components/failed_information.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading.dart';
import 'package:blockchain_mobile/4_helper/decorations/card_decoration.dart';
import 'package:blockchain_mobile/4_helper/decorations/text_styles.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:provider/provider.dart';

class ListDiscount extends StatefulWidget {
  const ListDiscount({super.key});

  @override
  State<ListDiscount> createState() => _ListDiscountState();
}

class _ListDiscountState extends State<ListDiscount> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final getAllDiscountResult =
        context.watch<DiscountProvider>().getAllDiscountResult;
    final userDiscountResult =
        context.watch<DiscountProvider>().getUserDiscountResult;
    return Container(
      constraints: const BoxConstraints(minHeight: 200),
      child: IsLoadingWG(
        isLoading: getAllDiscountResult.isLoading,
        child: Visibility(
          visible: !getAllDiscountResult.isError,
          replacement:
              FailedInformation(child: Text(getAllDiscountResult.error)),
          child: SizedBox(
            height: 200,
            child: ListView.builder(
              shrinkWrap: true,
              scrollDirection: Axis.horizontal,
              itemCount: getAllDiscountResult.result?.length,
              itemBuilder: (context, index) {
                final discount = getAllDiscountResult.result![index];
                var check = (userDiscountResult.result?.any(
                          (element) {
                            return element.discount.code == discount.code;
                          },
                        ) ??
                        true) ||
                    discount.defaultQuantity == 0;
                return Container(
                  padding: const EdgeInsets.all(8),
                  margin: const EdgeInsets.all(8),
                  height: 0,
                  width: MediaQuery.of(context).size.width - 16,
                  decoration: cardDecoration.copyWith(
                    boxShadow: [
                      const BoxShadow(
                        color: Colors.grey,
                        blurRadius: 10,
                        blurStyle: BlurStyle.normal,
                      )
                    ],
                    color: discount.expire ? Colors.grey.shade300 : null,
                  ),
                  child: Center(
                    child: ListTile(
                      // dense: true,
                      leading: Flex(
                        direction: Axis.vertical,
                        mainAxisSize: MainAxisSize.min,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          Text(
                            "${discount.percentage.toStringAsPrecision(2)}%",
                            style: balanceStyle.copyWith(
                              fontSize: 20,
                            ),
                          ),
                          Text.rich(
                            TextSpan(
                                text: "${discount.code} ", children: const []),
                            style: const TextStyle(
                                fontSize: 12, fontWeight: FontWeight.bold),
                          ),
                        ],
                      ),
                      title: Text(
                        "Discount #${discount.id} ",
                        style: Theme.of(context).textTheme.titleMedium,
                      ),
                      subtitle: DefaultTextStyle(
                        style: const TextStyle(fontSize: 10, color: kTextColor),
                        child: Column(
                          mainAxisSize: MainAxisSize.min,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            // Text("Created: ${dateFormat(discount.dateCreate)}"),
                            Text(
                                "Minimum product: ${numberFormat(discount.quantityMin)}"),
                            Text(
                                "Max reduce price: ${currencyFormat(discount.maxReduce)}"),
                            Text(
                                "Min allowed price: ${currencyFormat(discount.minPrice)}"),
                            Text(
                              "Expire: ${dateFormat(discount.dateExpire)}",
                            ),
                          ],
                        ),
                      ),
                      trailing: Visibility(
                        visible: true,
                        child: TextButton(
                          style: TextButton.styleFrom(
                            backgroundColor: kPrimaryColor,
                            foregroundColor: Colors.white,
                            disabledBackgroundColor: Colors.grey.shade100,
                            disabledForegroundColor: Colors.grey,
                            textStyle: const TextStyle(fontSize: 12),
                          ),
                          onPressed: check ||
                                  userDiscountResult.isLoading ||
                                  userDiscountResult.isError
                              ? null
                              : () => {
                                    context
                                        .read<DiscountProvider>()
                                        .addUserDiscount(context, discount.id),
                                    context
                                        .read<DiscountProvider>()
                                        .getUserDiscount(
                                            expire: false, showAll: true),
                                  },
                          child: Visibility(
                            visible: discount.defaultQuantity != 0,
                            replacement: const Text("Empty"),
                            child: const Text.rich(
                              TextSpan(children: [
                                TextSpan(text: "Save "),
                                WidgetSpan(
                                  child: FaIcon(
                                    FontAwesomeIcons.check,
                                    size: 12,
                                  ),
                                ),
                              ]),
                            ),
                          ),
                        ),
                      ),
                    ),
                  ),
                );
              },
            ),
          ),
        ),
      ),
    );
  }
}
