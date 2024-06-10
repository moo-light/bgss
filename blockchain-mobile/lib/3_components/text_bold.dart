import 'package:flutter/material.dart';

class TextBold extends Text {
  const TextBold(super.data, {super.key});
  @override
  TextStyle get style =>  const TextStyle(
      fontWeight: FontWeight.bold,
    );
}