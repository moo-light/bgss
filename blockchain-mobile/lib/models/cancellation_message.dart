class CancellationMessage {
  int id;
  String sender;
  String receiver;
  String reason;
  int withdrawalId;

  CancellationMessage({
    required this.id,
    required this.sender,
    required this.receiver,
    required this.reason,
    required this.withdrawalId,
  });

  factory CancellationMessage.fromJson(Map<String, dynamic> json) {
    return CancellationMessage(
      id: json['id'],
      sender: json['sender'],
      receiver: json['receiver'],
      reason: json['reason'],
      withdrawalId: json['withdrawalId'],
    );
  }
}
