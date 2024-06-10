import 'package:blockchain_mobile/1_controllers/providers/otp_provider.dart';
import 'package:blockchain_mobile/4_helper/extensions/string_extension.dart';
import 'package:blockchain_mobile/4_helper/keyboard.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../constants.dart';
import 'components/otp_form.dart';

class OtpScreen extends StatefulWidget {
  static String routeName = "/otp";
  static String routeNameTransaction = "/otp-t";
  static String routeNameWithdraw = "/otp-w";

  final OtpEType type;

  const OtpScreen({super.key, required this.type});

  @override
  State<OtpScreen> createState() => _OtpScreenState();
}

class _OtpScreenState extends State<OtpScreen> {
  // late Stopwatch _stopwatch;
  // late Timer _timer;
  // final Duration _refreshInterval = const Duration(seconds: 1);

  int expiryTime = 180;

  @override
  void initState() {
    Provider.of<OtpProvider>(context, listen: false)
        .verifyOtpOrderResult
        .reset();
    Provider.of<OtpProvider>(context, listen: false).resendOtpResult.reset();
    super.initState();

    // _stopwatch = Stopwatch();
    // _startTimer();
  }

  // void _startTimer() {
  //   _stopwatch.start();
  //   _timer = Timer.periodic(_refreshInterval, (timer) {
  //     if (_stopwatch.isRunning) {
  //       setState(() {}); // Forces the widget to rebuild every second
  //       if (_stopwatch.elapsedMilliseconds ~/ 1000 == expiryTime) {
  //         context.read<OtpProvider>().setTimeOut(context);
  //       }
  //     }
  //   });
  // }

  // String _formattedTime(int milliseconds) {
  //   var second = expiryTime - (milliseconds ~/ 1000);
  //   var minute = (second ~/ 60).toString().padLeft(2, '0');
  //   second = second % 60;
  //   return "$minute:${second.toString().padLeft(2, '0')}";
  // }

  @override
  Widget build(BuildContext context) {
    var watch = context.watch<OtpProvider>();
    var canPop =
        watch.verifyOtpOrderResult.isSuccess || watch.resendOtpResult.isLoading;
    var verifyId = (ModalRoute.of(context)?.settings.arguments ?? 0) as int;
    return Scaffold(
      appBar: AppBar(
        title: Text("${widget.type.name.toCapitalize()} OTP Verification"),
      ),
      body: PopScope(
        canPop: canPop || true,
        child: SizedBox(
          width: double.infinity,
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 20),
            child: SingleChildScrollView(
              child: Column(
                children: [
                  const SizedBox(height: 16),
                  const Text(
                    "OTP Verification",
                    style: headingStyle,
                  ),
                  const Text("We sent your code to your email"),
                  // Row(
                  //   mainAxisAlignment: MainAxisAlignment.center,
                  //   children: [
                  //     const Text("This code will expired in "),
                  //     Text(
                  //       _formattedTime(_stopwatch.elapsedMilliseconds),
                  //       style: TextStyle(
                  //           color: _stopwatch.isRunning
                  //               ? kPrimaryColor
                  //               : kHintTextColor),
                  //     ),
                  //   ],
                  // ),
                  OtpForm(
                    id: verifyId,
                    type: widget.type,
                  ),
                  const SizedBox(height: 20),
                  GestureDetector(
                    onTap: () async {
                      //cancel repeated click
                      if (watch.resendOtpResult.isLoading ||
                          watch.verifyOtpOrderResult.isLoading) return;
                      KeyboardUtil.hideKeyboard(context);

                      // setState(() {
                      //   _stopwatch.stop();
                      // });

                      Provider.of<OtpProvider>(context, listen: false)
                          .resendOtp(context, id: verifyId, type: widget.type);
                      // setState(() {
                      //   if (value.isSuccess) {
                      //     _stopwatch.reset();
                      //     _stopwatch.start();
                      //   }
                      //   if (value.isError) _stopwatch.start();
                      // });
                    },
                    child: Text(
                      "Resend OTP Code",
                      style: TextStyle(
                          decoration: TextDecoration.underline,
                          color: watch.resendOtpResult.isLoading ||
                                  watch.verifyOtpOrderResult.isLoading
                              ? Colors.black26
                              : null),
                    ),
                  )
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }

  @override
  void dispose() {
    // _timer.cancel();
    super.dispose();
  }
}
