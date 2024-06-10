import 'dart:ui';

import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/enums/e_received_status.dart';

enum EUserConfirm {
  NOT_RECEIVED,RECEIVED, NULL
}
extension ExtensionUserConfirm on EUserConfirm {
  Color get color => switch (this) {
        EReceivedStatus.NOT_RECEIVED => kSecondaryColor,
        EReceivedStatus.RECEIVED => kWinColor,
        _ => kTextColor
      };
}
