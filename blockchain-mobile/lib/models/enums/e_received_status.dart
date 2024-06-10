import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';

enum EReceivedStatus {
  UNVERIFIED,
  RECEIVED, // đã nhận
  NOT_RECEIVED // chưa nhận
}

extension ExtensionProcessReceiveProduct on EReceivedStatus {
  Color get color => switch (this) {
        EReceivedStatus.UNVERIFIED => kHintTextColor,
        EReceivedStatus.NOT_RECEIVED => kSecondaryColor,
        EReceivedStatus.RECEIVED => kWinColor,
        _ => kTextColor
      };
}
