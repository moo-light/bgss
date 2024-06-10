import 'package:blockchain_mobile/1_controllers/providers/discount_provider.dart';
import 'package:blockchain_mobile/3_components/failed_information.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading.dart';
import 'package:blockchain_mobile/4_helper/decorations/card_decoration.dart';
import 'package:blockchain_mobile/4_helper/decorations/text_styles.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:provider/provider.dart';

class DiscountList extends StatelessWidget {
  const DiscountList({super.key});

@override
  Widget build(BuildContext context) {
    var userDiscounts = context.watch<DiscountProvider>().getUserDiscountResult;
    return IsLoadingWG(
      isLoading: userDiscounts.isLoading,
      child: Visibility(
        visible: !userDiscounts.isError,
        replacement: FailedInformation(child: Text(userDiscounts.error)),
        child: ListView.builder(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          itemCount: userDiscounts.result?.length,
          itemBuilder: (context, index) {
            final ud = userDiscounts.result![index];
            return Container(
              padding: const EdgeInsets.all(8),
              margin: const EdgeInsets.all(8),
              decoration: cardDecoration.copyWith(
                boxShadow: [
                  const BoxShadow(
                    color: Colors.grey,
                    blurRadius: 10,
                    blurStyle: BlurStyle.normal,
                  )
                ],
                color: ud.discount.expire ? Colors.grey.shade300 : null,
              ),
              child: ListTile(
                dense: true,
                leading: Flex(
                  direction: Axis.vertical,
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    Text(
                      "${ud.discount.percentage.toStringAsFixed(2)}%",
                      style: balanceStyle.copyWith(
                        fontSize: 16,
                      ),
                    ),
                    Text.rich(
                      TextSpan(
                          text: "${ud.discount.code} ", children: const []),
                      style: const TextStyle(
                          fontSize: 12, fontWeight: FontWeight.bold),
                    ),
                  ],
                ),
                title: Text(
                  "Discount #${ud.discount.id} ",
                  style: Theme.of(context).textTheme.titleMedium,
                ),
                subtitle: Column(
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Text("Created: ${dateFormat(ud.discount.dateCreate)}"),
                    Text(
                        "Minimum product: ${(ud.discount.quantityMin)}"),
                    Text(
                        "Max reduce price: ${currencyFormat(ud.discount.maxReduce)}"),
                    Text("Min allow price: ${currencyFormat(ud.discount.minPrice)}"),
                    Text("Expire: ${dateFormat(ud.discount.dateExpire)}"),
                  ],
                ),
                trailing: Visibility(
                  visible: false,
                  child: TextButton(
                    style: TextButton.styleFrom(
                      backgroundColor: kPrimaryColor,
                      foregroundColor: Colors.white,
                      textStyle: const TextStyle(fontSize: 12),
                    ),
                    onPressed: () => {},
                    child: const Text.rich(
                      TextSpan(children: [
                        TextSpan(text: "Apply "),
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
            );
          },
        ),
      ),
    );
  }
}
