import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';

class FaIconGen extends StatelessWidget {
  const FaIconGen(this.icon,
      {super.key, this.color, this.width, this.height, this.margin});
  final Color? color;
  final IconData icon;
  final double? width;
  final double? height;
  final EdgeInsets? margin;

  @override
  Widget build(BuildContext context) {
    final width = this.width ?? 25;
    final height = this.height ?? 25;
    return Container(
      width: (width),
      height: height,
      margin: margin,
      decoration: const BoxDecoration(
      ),
      child: Align(
        alignment: Alignment.center,
        child: Stack(children: [
          FaIcon(
            icon,
            size: width,
            color: color ?? Theme.of(context).iconTheme.color,
          ),
        ]),
      ),
    );
  }
}
