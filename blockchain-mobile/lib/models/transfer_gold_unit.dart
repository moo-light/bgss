import 'package:blockchain_mobile/models/enums/gold_unit.dart';

class TransferGoldUnit {
  final int id;
  final String fromUnit;
  final String toUnit;
  final GoldUnit goldUnit;
  final String conversionFactor;
  TransferGoldUnit(
      {required this.id,
      required this.fromUnit,
      required this.toUnit,
      required this.conversionFactor,
      required this.goldUnit});

  factory TransferGoldUnit.fromJson(Map<String, dynamic> json) {
    return TransferGoldUnit(
      id: json['id'],
      fromUnit: json['fromUnit'],
      toUnit: json['toUnit'],
      conversionFactor: json['conversionFactor'],
      goldUnit: GoldUnit.values
          .firstWhere((element) => json['goldUnit'] == element.name),
    );
  }

  double convert(double value) {
    return convertStatic(value, conversionFactor);
  }

  static double convertStatic(double value, String conversionFactor) {
    final conversionExpression = conversionFactor.split('/');
    if (conversionExpression.length == 2) {
      final numerator = double.tryParse(conversionExpression[0]) ?? 1;
      final denominator = double.tryParse(conversionExpression[1]) ?? 1;
      return value * numerator / denominator;
    } else {
      return value * (double.tryParse(conversionFactor) ?? 1);
    }
  }

  String get symbol => getSymbol(fromUnit);

  static String getSymbol(String goldUnit) {
    var split = goldUnit.split("(");
    if (split.length > 1) {
      return split[1].substring(0, split[1].length - 1);
    } // gram (g) // Trow Ounces (tOz)
    return split[0];
  }
}
