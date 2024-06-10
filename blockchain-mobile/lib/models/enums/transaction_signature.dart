import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/cupertino.dart';

enum TransactionSignature { UNSIGNED, SIGNED,NULL }

extension TransactionSignatureExt on TransactionSignature {
  Color get color => switch (this) {
        TransactionSignature.UNSIGNED => kHintTextColor,
        TransactionSignature.SIGNED => kWinColor,
        _ => kTextColor
      };
}
