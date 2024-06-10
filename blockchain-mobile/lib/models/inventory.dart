import 'withdraw_gold.dart';

class Inventory {
  int id;
  double totalWeightOz;
  List<WithdrawGold> withdrawGold;

  Inventory({
    required this.id,
    required this.totalWeightOz,
    required this.withdrawGold,
  });

  factory Inventory.fromJson(Map<String, dynamic> json) {
    return Inventory(
      id: json['id'],
      totalWeightOz: json['totalWeightOz'].toDouble(),
      withdrawGold: List<WithdrawGold>.from(json['withdrawGold'].map((x) => WithdrawGold.fromJson(x))),
    );
  }
}
