import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';

class CustomSuffixIcon extends StatefulWidget {
  const CustomSuffixIcon({
    super.key,
    this.svgIcon,
    this.faIcon,
    this.icon,
    this.color,
  });

  final String? svgIcon;
  final IconData? faIcon;
  final IconData? icon;
  final Color? color;

  @override
  State<CustomSuffixIcon> createState() => _CustomSuffixIconState();
}

class _CustomSuffixIconState extends State<CustomSuffixIcon> {
  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(20),
      child: getIcon(),
    );
  }

  Widget getIcon() {
    if (widget.faIcon != null) {
      return Icon(
        widget.icon!,
        color: widget.color,
      );
    }
    if (widget.icon != null) {
      return FaIcon(
        widget.faIcon!,
        color: widget.color,
      );
    }
    if (widget.svgIcon != null) {
      return SvgPicture.asset(
        widget.svgIcon!,
        height: 16,
        // width: 16,
        colorFilter: ColorFilter.mode(widget.color ?? kIconColor,BlendMode.modulate),
      );
    }
    throw Exception("Please Add Icon");
  }
}
