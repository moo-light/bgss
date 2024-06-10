import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/4_helper/decorations/text_styles.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class UserStorage extends StatelessWidget {
  const UserStorage({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    var authProvider = context.watch<AuthProvider>();
    final isCustomer = authProvider.isCustomer;
    if (authProvider.currentUser == null || !isCustomer) return Container();
    const textBold = TextStyle(fontWeight: FontWeight.bold);
    return Visibility(
        visible: isCustomer,
        child: Container(
          margin: const EdgeInsets.only(top: 10),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisSize: MainAxisSize.min,
            children: [
              Text.rich(TextSpan(children: [
                const TextSpan(
                  text: "Balance: ",
                ),
                TextSpan(
                  text: currencyFormat(
                    authProvider.currentUser?.userInfo.balance.amount,
                  ),
                  style: balanceStyle,
                ),
                const TextSpan(
                  text: "\nInventory: ",
                  children: [],
                ),
                TextSpan(
                  text: "${numberFormat(
                    authProvider.currentUser?.userInfo.inventory.totalWeightOz,
                  )} tOz",
                  style: inventoryStyle,
                )
              ]),
              style: const TextStyle(
                fontSize: 16
              )),
              const Divider(),
            ],
          ),
        ));
  }
}
