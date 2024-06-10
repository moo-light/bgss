import 'package:blockchain_mobile/models/enums/gold_unit.dart';

class GoldType {
  int id;
  String typeName;
  double price;
  GoldUnit goldUnit;
  bool active;

  GoldType({
    required this.id,
    required this.typeName,
    required this.price,
    required this.goldUnit,
    required this.active,
  });

  // Method to create an instance of GoldType from a JSON object
  factory GoldType.fromJson(Map<String, dynamic> json) {
    return GoldType(
      id: json['id'],
      typeName: json['typeName'],
      price: json['price'],
      goldUnit: GoldUnit.values
          .firstWhere((element) => element.name == json['goldUnit']),
      active: json['active'],
    );
  }

  // Method to convert an instance of GoldType to a JSON object
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'typeName': typeName,
      'price': price,
      'goldUnit': goldUnit,
      'active': active,
    };
  }
}
