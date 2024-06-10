import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';

class SignatureContainer extends StatelessWidget {
  const SignatureContainer({
    super.key,
    required this.signature,
  });

  final Image signature;
  @override
  Widget build(BuildContext context) {
    return AspectRatio(
      aspectRatio: 4/3,
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
            color: Colors.white,
            border: Border.all(color: kIconColor, width: 2)),
        child: signature,
      ),
    );
  }
}

