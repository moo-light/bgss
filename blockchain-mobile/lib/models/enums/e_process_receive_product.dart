// ignore_for_file: constant_identifier_names

import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';

enum EProcessReceiveProduct {
  UNVERIFIED,
  PENDING,
  CONFIRM,
  COMPLETE,
}

extension ExtensionProcessReceiveProduct on EProcessReceiveProduct {
  Color get color => switch (this) {
        EProcessReceiveProduct.UNVERIFIED => kSecondaryColor,
        EProcessReceiveProduct.PENDING => kSecondaryColor,
        EProcessReceiveProduct.CONFIRM => kWinColorLight,
        EProcessReceiveProduct.COMPLETE => kWinColor,
        _ => kTextColor
      };
}
