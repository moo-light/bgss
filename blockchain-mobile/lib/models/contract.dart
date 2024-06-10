class Contract {
  int id;
  int actionPartyId;
  String fullName;
  String address;
  double quantity;
  double pricePerOunce;
  double totalCostOrProfit;
  String transactionType;
  DateTime createdAt;
  String confirmingParty;
  String goldUnit;
  String signatureOfActionParty;
  String signatureOfConfirmingParty;
  String contractStatus;

  Contract({
    required this.id,
    required this.actionPartyId,
    required this.fullName,
    required this.address,
    required this.quantity,
    required this.pricePerOunce,
    required this.totalCostOrProfit,
    required this.transactionType,
    required this.createdAt,
    required this.confirmingParty,
    required this.goldUnit,
    required this.signatureOfActionParty,
    required this.signatureOfConfirmingParty,
    required this.contractStatus,
  });

  factory Contract.fromJson(Map<String, dynamic> json) {
    return Contract(
      id: json['id'],
      actionPartyId: json['actionParty'],
      fullName: json['fullName'],
      address: json['address'],
      quantity: json['quantity'].toDouble(),
      pricePerOunce: json['pricePerOunce'].toDouble(),
      totalCostOrProfit: json['totalCostOrProfit'].toDouble(),
      transactionType: json['transactionType'],
      createdAt: DateTime.parse(json['createdAt']),
      confirmingParty: json['confirmingParty'],
      goldUnit: json['goldUnit'],
      signatureOfActionParty: json['signatureActionParty'],
      signatureOfConfirmingParty: json['signatureConfirmingParty'],
      contractStatus: json['contractStatus'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'actionParty': actionPartyId,
      'fullName': fullName,
      'address': address,
      'quantity': quantity,
      'pricePerOunce': pricePerOunce,
      'totalCostOrProfit': totalCostOrProfit,
      'transactionType': transactionType,
      'createdAt': createdAt.toIso8601String(),
      'confirmingParty': confirmingParty,
      'goldUnit': goldUnit,
      'signatureActionParty': signatureOfActionParty,
      'signatureConfirmingParty': signatureOfConfirmingParty,
      'contractStatus': contractStatus,
    };
  }

  @override
  String toString() {
    return 'Contract{id: $id, actionPartyId: $actionPartyId, fullName: $fullName, address: $address, quantity: $quantity, pricePerOunce: $pricePerOunce, totalCostOrProfit: $totalCostOrProfit, transactionType: $transactionType, createdAt: $createdAt, confirmingParty: $confirmingParty, goldUnit: $goldUnit, signatureOfActionParty: $signatureOfActionParty, signatureOfConfirmingParty: $signatureOfConfirmingParty, contractStatus: $contractStatus}';
  }
}