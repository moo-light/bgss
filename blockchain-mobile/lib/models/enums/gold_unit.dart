// ignore_for_file: constant_identifier_names

enum GoldUnit {
  KILOGRAM,
  GRAM,
  TAEL,
  MACE,
  TROY_OZ,
}

Map<GoldUnit, Map<GoldUnit, double>> conversionMatrix = {
  GoldUnit.KILOGRAM: {
    GoldUnit.GRAM: 1000,
    GoldUnit.TROY_OZ: 1000 / 31.1034768,
    GoldUnit.MACE: 1000 / 3.75,
    GoldUnit.TAEL: 1000 / 37.799364167,
  },
  GoldUnit.GRAM: {
    GoldUnit.KILOGRAM: 1 / 1000,
    GoldUnit.TROY_OZ: 1 / 31.1034768,
    GoldUnit.MACE: 1 / 3.75,
    GoldUnit.TAEL: 1 / 37.799364167,
  },
  GoldUnit.TROY_OZ: {
    GoldUnit.KILOGRAM: 31.1034768 / 1000,
    GoldUnit.GRAM: 31.1034768,
    GoldUnit.MACE: 31.1034768 / 3.75,
    GoldUnit.TAEL: 31.1034768 / 37.799364167,
  },
  GoldUnit.MACE: {
    GoldUnit.KILOGRAM: 3.75 / 1000,
    GoldUnit.GRAM: 3.75,
    GoldUnit.TROY_OZ: 3.75 / 31.1034768,
    GoldUnit.TAEL: 3.75 / 37.799364167,
  },
  GoldUnit.TAEL: {
    GoldUnit.KILOGRAM: 37.799364167 / 1000,
    GoldUnit.GRAM: 37.799364167,
    GoldUnit.TROY_OZ: 37.799364167 / 31.1034768,
    GoldUnit.MACE: 37.799364167 / 3.75,
  },
};

extension GoldUnitExtension on GoldUnit {
  String get symbol {
    switch (this) {
      case GoldUnit.KILOGRAM:
        return 'kg';
      case GoldUnit.GRAM:
        return 'g';
      case GoldUnit.TAEL:
        return 'tael';
      case GoldUnit.MACE:
        return 'mace';
      case GoldUnit.TROY_OZ:
        return 'tOz';
    }
  }

  double convert(double value, GoldUnit toUnit) {
    if (this == toUnit) return value;
    if (!conversionMatrix.containsKey(this) ||
        !conversionMatrix[this]!.containsKey(toUnit)) {
      throw Exception('Conversion not supported for the specified units.');
    }
    return value * conversionMatrix[this]![toUnit]!;
  }
}
