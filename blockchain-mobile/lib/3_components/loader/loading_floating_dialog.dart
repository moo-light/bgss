import 'dart:async';

import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';

class LoadingFloatingDialog extends StatefulWidget {
  final String? text;

  final Function(Timer timer) onPluse;

  const LoadingFloatingDialog({super.key, this.text, required this.onPluse});

  @override
  State<LoadingFloatingDialog> createState() => _LoadingFloatingDialogState();
}

class _LoadingFloatingDialogState extends State<LoadingFloatingDialog> {
  late final Timer timer;
  @override
  void initState() {
    timer = Timer.periodic(Durations.long4, (timer) => widget.onPluse(this.timer));
    super.initState();
  }

  @override
  void dispose() {
    timer.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    double constan = 200;
    return Scaffold(
      backgroundColor: Colors.black12,
      body: Center(
        child: Container(
          constraints: BoxConstraints(
              maxHeight: constan,
              maxWidth: constan,
              minHeight: constan,
              minWidth: constan),
          color: kBackgroundColor,
          child: Stack(
            children: [

              const Center(
                child: CircularProgressIndicator(
                  valueColor: AlwaysStoppedAnimation<Color>(kPrimaryColor),
                ),
              ),
              Align(
                alignment: Alignment.bottomCenter,
                child: Visibility(
                  visible: widget.text != null,
                  child: Container(
                    margin: const EdgeInsets.symmetric(vertical: 24),
                    child: Text("${widget.text}..."),
                  ),
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}
