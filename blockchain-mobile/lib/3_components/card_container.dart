import 'package:blockchain_mobile/4_helper/decorations/card_decoration.dart';
import 'package:flutter/material.dart';

class CardContainer extends StatelessWidget {
  final Widget? child;

  const CardContainer({super.key, this.child});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16.0),
        margin: const EdgeInsets.all(16),
        decoration: cardDecoration,
        child: child,
    );
  }
}