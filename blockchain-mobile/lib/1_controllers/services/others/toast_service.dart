import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:toastification/toastification.dart';

class ToastService {
  static void toastSuccess(
    BuildContext context,
    String? text, {
    type = ToastificationType.success,
    Widget? icon,
    description,
    Duration autoCloseDuration = const Duration(seconds: 5),
    Alignment alignment = Alignment.bottomRight,
  }) {
    icon ??= iconCheck;
    text ??= "Success";
    toastification.show(
      context: context,
      title: Text(text),
      alignment: alignment,
      description: description,
      type: type,
      autoCloseDuration: autoCloseDuration,
      icon: icon,
    );
  }

  static void toastError(
    context,
    String? text, {
    type = ToastificationType.error,
    Widget? icon,
    description,
    Duration autoCloseDuration = const Duration(seconds: 5),
    Alignment alignment = Alignment.bottomRight,
  }) {
    icon ??= iconError;
    text ??= "error";
    toastification.show(
      context: context,
      title: Text(text),
      description: description,
      alignment: alignment,
      type: type,
      autoCloseDuration: autoCloseDuration,
      icon: icon,
    );
  }
}
