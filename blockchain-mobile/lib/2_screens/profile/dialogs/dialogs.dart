import 'package:blockchain_mobile/1_controllers/providers/products_provider.dart';
import 'package:blockchain_mobile/2_screens/profile/dialogs/balance_dialog.dart';
import 'package:blockchain_mobile/models/balance.dart';
import 'package:blockchain_mobile/models/inventory.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../inventory/inventory_screen.dart';

class ProfileScreenDialog {
  static balanceDialog(BuildContext context, Balance balance) {
    return showDialog(
      barrierDismissible: false,
      context: context,
      builder: (BuildContext context) {
        return BalanceDialog(
          balance: balance,
        );
      },
    );
  }

  static inventoryDialog(BuildContext context, Inventory inventory) {
    context.read<ProductProvider>().get24kGoldProductList();
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) {
          return InventoryScreen(
            inventory: inventory,
          );
        },
      ),
    );
    // return showDialog(
    //   barrierDismissible: false,
    //   context: context,
    //   builder: (BuildContext context) {
    //     return InventoryScreen(
    //       inventory: inventory,
    //     );
    //   },
    // );
  }
}

