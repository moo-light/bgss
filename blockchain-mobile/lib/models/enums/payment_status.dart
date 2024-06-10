import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';

enum PaymentStatus { SUCCESS, FAILED, CANCEL, NULL }

extension PaymentStatusExt on PaymentStatus {
  Color get color => switch (this) {
        PaymentStatus.SUCCESS => kWinColor,
        PaymentStatus.FAILED => kLossColor,
        PaymentStatus.CANCEL => kHintTextColor,
        PaymentStatus.NULL => kTextColor,
      };
}
