import 'package:flutter/material.dart';

class FailedInformation extends StatelessWidget {
  final Widget child;

  const FailedInformation({super.key, required this.child});

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        const SizedBox(
          width: 200,
          height: 200,
          child: CircleAvatar(
            backgroundImage: AssetImage("assets/images/BGSS Logo.png"),
            backgroundColor: Colors.transparent,
          ),
        ),
        const SizedBox(
          height: 40,
        ),
        Center(
          child: child,
        )
      ],
    );
  }
}
