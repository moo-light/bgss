class Balance {
  int id;
  double amount;
  int userInfoId;

  Balance({
    required this.id,
    required this.amount,
    required this.userInfoId,
  });

  factory Balance.fromJson(Map<String, dynamic> json) {
    return Balance(
      id: json['id'],
      amount: json['amount'].toDouble(),
      userInfoId: json['userInfoId'],
    );
  }
}
