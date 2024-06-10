import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:gap/gap.dart';

class IsLoadingButton extends StatefulWidget {
  final bool isLoading;
  final ElevatedButton child;
  final bool spannedLoading;

  const IsLoadingButton({
    super.key,
    required this.isLoading,
    required this.child,
    this.spannedLoading = false,
  });

  @override
  State<IsLoadingButton> createState() => _IsLoadingButtonState();
}

class _IsLoadingButtonState extends State<IsLoadingButton> {
  late Widget loading;
  @override
  Widget build(BuildContext context) {
    final Widget spannedLoading = ElevatedButton(
        onPressed: null,
        style: widget.child.style,
        child: Stack(
          fit: StackFit.loose,
          clipBehavior: Clip.none,
          children: [
            widget.child.child!,
            const Positioned(
              right: -20,
              child: SizedBox(
                  width: 15,
                  height: 15,
                  child: Center(
                      child: CircularProgressIndicator(
                    color: kIconColor,
                    strokeWidth: 2,
                  ))),
            ),
          ],
        ));
    final defaultLoading = ElevatedButton(
        onPressed: null,
        style: widget.child.style,
        child: Row(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: [
            widget.child.child!,
            const Gap(10),
            const SizedBox(
                width: 15,
                height: 15,
                child: Center(
                    child: CircularProgressIndicator(
                  color: kIconColor,
                  strokeWidth: 2,
                ))),
          ],
        ));
    return widget.isLoading
        ? widget.spannedLoading
            ? spannedLoading
            : defaultLoading
        : widget.child;
  }
}
