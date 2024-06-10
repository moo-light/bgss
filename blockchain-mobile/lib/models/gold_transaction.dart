import 'package:blockchain_mobile/1_controllers/repositories/transaction_repository.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/enums/gold_unit.dart';
import 'package:flutter/cupertino.dart';

import 'contract.dart';
import 'enums/transaction_signature.dart';
import 'enums/transaction_status.dart';

class GoldTransaction {
  int id;
  double quantity;
  double pricePerOunce;
  double totalCostOrProfit;
  TransactionType transactionType;
  DateTime createdAt;
  String confirmingParty;
  GoldUnit goldUnit;
  int? actionPartyId;
  String transactionVerification;
  TransactionSignature transactionSignature;
  TransactionStatus transactionStatus;
  Contract? contract;

  GoldTransaction({
    required this.id,
    required this.quantity,
    required this.pricePerOunce,
    required this.totalCostOrProfit,
    required this.transactionType,
    required this.createdAt,
    required this.confirmingParty,
    required this.goldUnit,
    this.actionPartyId,
    required this.transactionVerification,
    required this.transactionSignature,
    required this.transactionStatus,
    required this.contract,
  });

  factory GoldTransaction.fromJson(Map<String, dynamic> json) {
    var transactionSignature = json['transactionSignature'];
    var transactionStatus = json['transactionStatus'];
    return GoldTransaction(
      id: json['id'],
      quantity: json['quantity'].toDouble(),
      pricePerOunce: json['pricePerOunce'].toDouble(),
      totalCostOrProfit: json['totalCostOrProfit'].toDouble(),
      transactionType: TransactionType.values
          .firstWhere((element) => element.name == json['transactionType']),
      createdAt: DateTime.parse(json['createdAt']).toLocal(),
      confirmingParty: json['confirmingParty'],
      goldUnit: GoldUnit.values
          .firstWhere((element) => element.name == json['goldUnit']),
      actionPartyId: json['actionPartyId'],
      transactionVerification: json['transactionVerification'] ?? "",
      transactionSignature: TransactionSignature.values.firstWhere(
        (element) => element.name == transactionSignature,
        orElse: () => TransactionSignature.NULL,
      ),
      transactionStatus: TransactionStatus.values.firstWhere(
        (element) => element.name == transactionStatus,
        orElse: () => TransactionStatus.NULL,
      ),
      contract:
          json['contract'] != null ? Contract.fromJson(json['contract']) : null,
    );
  }
  GoldUnit getGoldUnit() {
    return goldUnit;
  }

  TransactionType getTransactionType() {
    return transactionType;
  }

  Color getVerificationColor() {
    if (transactionVerification == "UNVERIFIED") {
      return kHintTextColor;
    } else {
      return kWinColor;
    }
  }
}
