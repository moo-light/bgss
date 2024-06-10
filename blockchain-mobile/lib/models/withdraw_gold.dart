import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/Product.dart';
import 'package:blockchain_mobile/models/enums/gold_unit.dart';
import 'package:flutter/material.dart';

import 'cancellation_message.dart';

class WithdrawGold {
  int id;
  double amount;
  String status;
  DateTime transactionDate;
  int userInfoId;
  DateTime? updateDate;
  List<CancellationMessage> cancellationMessages;
  String? withdrawQrCode;
  String? goldUnit;
  Product? product;
  String? userConfirm;

  static String? errorQrScanMessage = "Please use Complete Withdraw option!";
  WithdrawGold(
      {required this.id,
      required this.amount,
      required this.status,
      required this.transactionDate,
      required this.userInfoId,
      required this.updateDate,
      required this.cancellationMessages,
      required this.withdrawQrCode,
      required this.goldUnit,
      this.product,
      this.userConfirm});

  factory WithdrawGold.fromJson(Map<String, dynamic> json) {
    DateTime? updateDate = json['updateDate'] != null
        ? DateTime.parse(json['updateDate']).toLocal()
        : null;
    final transactionDate = DateTime.parse(json['transactionDate']).toLocal();
    return WithdrawGold(
        id: json['id'],
        amount: json['amount'],
        status: json['status'],
        transactionDate: transactionDate,
        userInfoId: json['userInfoId'] ?? 0,
        updateDate: updateDate,
        cancellationMessages: json['cancellationMessages'] != null
            ? List<CancellationMessage>.from(json['cancellationMessages']
                .map((x) => CancellationMessage.fromJson(x)))
            : [],
        withdrawQrCode: json['withdrawQrCode'],
        goldUnit: json['goldUnit'],
        product: json['productDTO'] != null
            ? Product.fromJson(json['productDTO'])
            : null,
        userConfirm: json['userConfirm']);
  }
  String get type => product == null ? "AVAILABLE" : "CRAFT";
  GoldUnit getGoldUnit() {
    // return GoldUnit.TROY_OZ;
    return GoldUnit.values
        .firstWhere((element) => element.name == goldUnit.toString());
  }

  Color getStatusColor() {
    switch (status) {
      case "PENDING":
        return kSecondaryColor;
      case "APPROVED":
        return kWinColor.addHSLlighting(2);
      case "CONFIRMED":
        return kWinColor.addHSLlighting(5);
      case "UNVERIFIED":
        return Colors.grey;
      case "COMPLETED":
        return kWinColor;
      case "CANCELLED":
      case "REJECTED":
        return kLossColor;
      default:
        return kTextColor;
    }
  }

  String formatUpdateDate() {
    if (updateDate == null) return "";
    return dateFormat(updateDate!);
  }
}
