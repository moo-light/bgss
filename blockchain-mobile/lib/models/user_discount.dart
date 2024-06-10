import 'package:blockchain_mobile/models/discount.dart';

class UserDiscount {
  final int id;
  final bool available;
  final Discount discount;
  final int quantityDefault;

  // Constructor for the UserDiscount class
  UserDiscount({
    required this.id,
    required this.available,
    required this.discount,
    required this.quantityDefault,
  });

  // Factory constructor to create an instance from JSON
  factory UserDiscount.fromJson(Map<String, dynamic> json) {
    return UserDiscount(
      id: json['id'],
      available: json['available'],
      discount: Discount.fromJson(json['discount']),
      quantityDefault: json['quantity_default'],
    );
  }

  // Override equality operator to compare instances
  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is UserDiscount &&
        (other.id == id || other.discount.code == discount.code);
  }

  // Override hashCode for consistent hashing
  @override
  int get hashCode => Object.hash(id, discount.code, available, quantityDefault);
}
