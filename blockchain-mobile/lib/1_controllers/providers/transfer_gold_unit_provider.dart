import 'package:blockchain_mobile/1_controllers/repositories/transfer_gold_unit_repository.dart';
import 'package:blockchain_mobile/models/dtos/result_object.dart';
import 'package:blockchain_mobile/models/enums/gold_unit.dart';
import 'package:blockchain_mobile/models/transfer_gold_unit.dart';
import 'package:flutter/material.dart';

const double maxx = 1; // KG
const double minn = 0.00; // KG

const String defaultGoldUnit = "Kilogram (Kg)";
const String defaultCost_GoldUnit = "Troy Ounce (tOz)";

class TransferGoldUnitProvider extends ChangeNotifier {
  final _transGoldUnitRepository = TransferGoldUnitRepository();
  late TransferGoldUnit currentGoldUnit;
  TransferGoldUnitProvider() {
    currentGoldUnit = TransferGoldUnit(
        id: 0,
        fromUnit: "tOz",
        toUnit: "tOz",
        conversionFactor: "1/1",
        goldUnit: GoldUnit.TROY_OZ);
    _getAllGoldConvertValues();
  }

  final goldConvertValues = ResultObject<List<TransferGoldUnit>>();
  Future<void> _getAllGoldConvertValues() async {
    goldConvertValues.reset(isLoading: true);
    notifyListeners();
    final result = await _transGoldUnitRepository.getAll();
    if (result.isLeft) {
      goldConvertValues.isSuccess = true;
      goldConvertValues.result = result.left.data;
      currentGoldUnit = result.left.data.first;
      goldConvertValues.message = result.left.message;
    }
    if (result.isRight) {
      goldConvertValues.isError = true;
      goldConvertValues.error = result.right ?? "Can't get values.";
      goldConvertValues.result = null;
    }
    goldConvertValues.isLoading = false;
    notifyListeners();
  }

  List<TransferGoldUnit> get convertValues => goldConvertValues.result ?? [];
  set setCurrentGoldUnit(String toUnit) {
    currentGoldUnit = convertValues.firstWhere(
        (cv) => cv.fromUnit == toUnit); // only work if both String are equal
    notifyListeners();
  }

  double convert(double value, String from, String to) {
    final conversionFactor = convertValues
        .firstWhere(
          (element) => element.fromUnit == from && element.toUnit == to,
          orElse: () => TransferGoldUnit(
              id: 0,
              fromUnit: "",
              toUnit: "",
              conversionFactor: "1",
              goldUnit: GoldUnit.TROY_OZ),
        )
        .conversionFactor;
    Future.delayed(
      Durations.long1,
      notifyListeners,
    );
    return TransferGoldUnit.convertStatic(value, conversionFactor);
  }
}
