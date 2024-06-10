import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';

final cardDecoration = BoxDecoration(
  // color: Colors.transparent,
  color: kBackgroundColor.withHSLlighting(93),
  borderRadius: const BorderRadius.all(Radius.circular(16)),
  boxShadow: [
    BoxShadow(
      color: kShadowColor.withOpacity(0.1),
      spreadRadius: 5,
      blurRadius: 3,
    )
  ],
  // gradient: LinearGradient(
  //   begin: Alignment.topCenter,
  //   end: Alignment.bottomCenter,
  //   colors: [kBackgroundColor.withHSLlighting(90), kBackgroundColor.withHSLlighting(93)],
  // ),
);
