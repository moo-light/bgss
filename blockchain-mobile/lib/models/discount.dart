class Discount {
  final int id;
  final String code;
  final double percentage;
  final double minPrice;
  final double maxReduce;
  final int quantityMin;
  final int defaultQuantity;
  final DateTime dateCreate;
  final DateTime dateExpire;
  final String statusDiscount;
  final bool expire;

  // Constructor for the Discount class
  Discount({
    required this.id,
    required this.code,
    required this.percentage,
    required this.minPrice,
    required this.maxReduce,
    required this.quantityMin,
    required this.defaultQuantity,
    required this.dateCreate,
    required this.dateExpire,
    required this.statusDiscount,
    required this.expire,
  });

  // Factory constructor to create an instance from JSON
  factory Discount.fromJson(Map<String, dynamic> json) {
    return Discount(
      id: json['id'],
      code: json['code'],
      percentage: json['percentage'].toDouble(),
      minPrice: json['minPrice'].toDouble(),
      maxReduce: json['maxReduce'].toDouble(),
      quantityMin: json['quantityMin'],
      defaultQuantity: json['defaultQuantity'],
      dateCreate: DateTime.parse(json['dateCreate']).toLocal(),
      dateExpire: DateTime.parse(json['dateExpire']).toLocal(),
      statusDiscount: json['statusDiscount'],
      expire: json['expire'],
    );
  }

  // Getter to check if the discount has expired
  bool get isExpired => dateExpire.compareTo(DateTime.now()) < 0;
}
