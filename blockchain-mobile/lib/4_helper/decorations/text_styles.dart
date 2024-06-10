import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';

const balanceStyle =
    TextStyle(color: kSecondaryColor, fontWeight: FontWeight.bold);
const inventoryStyle =
    TextStyle(color: kTextColor, fontWeight: FontWeight.bold);
const boldTextStyle = TextStyle(color: kTextColor, fontWeight: FontWeight.bold);

//
final cancelTextBtnStyle = TextButton.styleFrom(
  backgroundColor: kHintTextColor,
  foregroundColor: Colors.white,
);
final confirmTextBtnStyle = TextButton.styleFrom(
  backgroundColor: kPrimaryColor,
  foregroundColor: Colors.white,
  textStyle: boldTextStyle,
  disabledBackgroundColor: kHintTextColor.withHSLlighting(90),
  disabledForegroundColor: Colors.grey,
);
