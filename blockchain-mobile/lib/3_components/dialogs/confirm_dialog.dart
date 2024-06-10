import 'package:blockchain_mobile/3_components/text_bold.dart';
import 'package:blockchain_mobile/4_helper/decorations/text_styles.dart';
import 'package:flutter/material.dart';

class SimpleConfirmDialog extends StatelessWidget {
  final String title;

  final String content;
  final VoidCallback? onConfirm;
  final VoidCallback? onCancel;
  final ButtonStyle? confirmStyle;
  final ButtonStyle? cancelStyle;
  
  final EConfirmType confirmType;

  const SimpleConfirmDialog({
    super.key,
    this.title = "Confirm",
    this.content = "Are you sure?",
    this.confirmType = EConfirmType.CONFIRM_CANCEL,
    required this.onConfirm,
    required this.onCancel,
    this.confirmStyle,
    this.cancelStyle,
  });

  @override
  Widget build(BuildContext context) {
    Text cancel = confirmType == EConfirmType.CONFIRM_CANCEL ? const TextBold("Cancel"): const TextBold("No");
    Text confirm = confirmType == EConfirmType.CONFIRM_CANCEL ? const TextBold("Confirm"): const TextBold("Yes");


    return AlertDialog(
      title: Text(title),
      content: Text(content),
      actions: [
        TextButton(
          onPressed: onCancel,
          style: cancelStyle ?? cancelTextBtnStyle,
          child: cancel,
        ),
        TextButton(
          onPressed: onConfirm,
          style: confirmStyle?? confirmTextBtnStyle,
          child: confirm,
        )
      ],
    );
  }
}
enum EConfirmType{
  YES_NO,
  CONFIRM_CANCEL,
}