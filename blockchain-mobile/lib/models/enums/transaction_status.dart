import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';

enum TransactionStatus {
  PENDING,
  CONFIRMED,
  REJECTED,
  COMPLETED,
  NULL,
}
extension TransactionStatusExt on TransactionStatus {
  Color get color => switch (this) {
        TransactionStatus.PENDING => kSecondaryColor,
        TransactionStatus.CONFIRMED => kWinColor.addHSLlighting(10),
        TransactionStatus.COMPLETED => kWinColor,
        TransactionStatus.REJECTED => kLossColor,
        _ => kTextColor
      };
}
