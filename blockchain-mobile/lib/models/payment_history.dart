import 'enums/payment_status.dart';

class PaymentHistory {
  final int id;
  final String orderCode;
  final double amount;
  final String bankCode;
  final PaymentStatus paymentStatus;
  final DateTime payDate;
  final String reason;
  final int userInfoId;
  final double balance;

  PaymentHistory({
    required this.id,
    required this.orderCode,
    required this.amount,
    required this.bankCode,
    required this.paymentStatus,
    required this.payDate,
    required this.reason,
    required this.userInfoId,
    required this.balance,
  });

  factory PaymentHistory.fromJson(Map<String, dynamic> json) {
    return PaymentHistory(
      id: json['id'] as int,
      orderCode: json['orderCode'],
      amount: json['amount'] as double,
      bankCode: json['bankCode'] as String,
      paymentStatus: PaymentStatus.values.firstWhere(
          (element) => element.name == json["paymentStatus"],
          orElse: () => PaymentStatus.NULL),
      payDate: DateTime.parse(json['payDate'] as String),
      reason: json['reason'] as String,
      userInfoId: json['userInfoId'] as int,
      balance: (json['balance']?? 0),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'orderCode': orderCode,
      'amount': amount,
      'bankCode': bankCode,
      'paymentStatus': paymentStatus.name,
      'payDate': payDate.toIso8601String(),
      'reason': reason,
      'userInfoId': userInfoId,
      'balance': balance,
    };
  }

  @override
  String toString() {
    return 'PaymentHistory{id: $id, orderCode: $orderCode, amount: $amount, bankCode: $bankCode, paymentStatus: $paymentStatus, payDate: $payDate, reason: $reason, userInfoId: $userInfoId, balance: $balance}';
  }
}
