import 'package:flutter/material.dart';

extension ColorExtension on Color {
  Color withHSLlighting(double lightness) {
    if (lightness < 0) throw Exception("Invalid value");
    var hsl = HSLColor.fromColor(this);
    if (lightness > 1.0) lightness /= 100;
    return HSLColor.fromAHSL(hsl.alpha, hsl.hue, hsl.saturation, lightness)
        .toColor();
  }

  Color addHSLlighting(double lightness) {
    var hsl = HSLColor.fromColor(this);
    if (-1.0 > lightness || lightness > 1.0) lightness /= 100;
    return HSLColor.fromAHSL(
            hsl.alpha, hsl.hue, hsl.saturation, hsl.lightness + lightness)
        .toColor();
  }
}
