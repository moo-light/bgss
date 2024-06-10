import 'package:blockchain_mobile/1_controllers/providers/order_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/otp_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/transaction_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/withdraw_provider.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/2_screens/transaction_detail/transaction_detail_screen.dart';
import 'package:blockchain_mobile/2_screens/withdraw_detail/withdraw_detail_screen.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_button.dart';
import 'package:blockchain_mobile/models/dtos/result_object.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';

import '../../../constants.dart';

class OtpForm extends StatefulWidget {
  final OtpEType type;

  final int id;

  const OtpForm({
    super.key,
    required this.type,
    required this.id,
  });

  @override
  _OtpFormState createState() => _OtpFormState();
}

class _OtpFormState extends State<OtpForm> {
  List<FocusNode> pinFocusNodes = [];
  List<TextEditingController> controllers = [];
  List<bool> obscureText = [];
  bool isFirstTime = true;
  late ResultObject result;
  @override
  void initState() {
    super.initState();
    for (int i = 0; i < 6; i++) {
      pinFocusNodes.add(FocusNode());
      controllers.add(TextEditingController());
      obscureText.add(true);
    }
  }

  @override
  void dispose() {
    super.dispose();
    for (var node in pinFocusNodes) {
      node.dispose();
    }
    for (var node in controllers) {
      node.dispose();
    }
  }

  void nextField(int currentIndex) {
    final controller = controllers[currentIndex];
    if (controller.text.length >= 6) {
      Future.microtask(() async {
        var value = await Clipboard.getData(Clipboard.kTextPlain);
        for (var e in controllers.indexed) {
          e.$2.text = value!.text![e.$1];
        }
        pinFocusNodes.last.requestFocus();
        submitHandler();
      });
      return;
    }
    String value = controller.text;
    if (value.length > 1) controller.text = value.characters.last;
    if (value.isEmpty && currentIndex > 0) {
      var index =
          currentIndex == 0 ? pinFocusNodes.length - 1 : currentIndex - 1;
      pinFocusNodes[index].requestFocus();
      return;
    }
    if (value.isNotEmpty && currentIndex < pinFocusNodes.length - 1) {
      pinFocusNodes[currentIndex + 1].requestFocus();
    }
    if (isFirstTime == true && currentIndex == pinFocusNodes.length - 1) {
      setState(() {
        submitHandler();
      });
    }
    setState(() {});
  }

  Widget otpField(int index) {
    return StatefulBuilder(
      builder: (context, setState) {
        return SizedBox(
          width: 50,
          height: 50,
          child: TextFormField(
            focusNode:
                index < pinFocusNodes.length ? pinFocusNodes[index] : null,
            autofocus: index == 0,
            inputFormatters: <TextInputFormatter>[
              FilteringTextInputFormatter.digitsOnly
            ],
            style: const TextStyle(fontSize: 20),
            keyboardType: TextInputType.number,
            textAlign: TextAlign.center,
            enabled: !result.isLoading,
            decoration: InputDecoration(
              contentPadding: const EdgeInsets.symmetric(vertical: 8),
              border: outlineInputBorder(),
              focusedBorder: outlineInputBorder(),
              enabledBorder: outlineInputBorder(),
            ),
            controller: controllers[index],
            obscureText: obscureText[index],
            onChanged: (value) {
              setState(() {
                obscureText[index] = false;
              });
              nextField(index);
              Future.delayed(
                Durations.medium1,
                () {
                  setState(() {
                    obscureText[index] = true;
                  });
                },
              );
            },
          ),
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    result = context.watch<OtpProvider>().verifyOtpOrderResult;
    print(result);
    return Form(
      child: Column(
        children: [
          SizedBox(height: MediaQuery.of(context).size.height * 0.15),
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: List.generate(6, (index) => otpField(index)),
          ),
          SizedBox(
            height: MediaQuery.of(context).size.height * 0.15,
            child: Visibility(
              visible: result.isError,
              child: Align(
                alignment: const Alignment(-1, -0.9),
                child: Text(
                  result.error,
                  style: const TextStyle(color: kLossColor),
                ),
              ),
            ),
          ),
          IsLoadingButton(
            spannedLoading: true,
            isLoading: result.isLoading,
            child: ElevatedButton(
              onPressed: controllers.every((element) => element.text.isNotEmpty)
                  ? submitHandler
                  : null,
              style: ElevatedButton.styleFrom(
                minimumSize: const Size(double.infinity, 48),
              ),
              child: const Text("Continue"),
            ),
          ),
        ],
      ),
    );
  }

  void submitHandler() async {
    // widget.stopwatch.stop();
    await context.read<OtpProvider>().verifyOtpOrder(
          id: widget.id,
          type: widget.type,
          otp: controllers.map((e) => e.text).join(),
        );

    if (mounted) setState(() {});
    if (mounted && result.isError) {
      switch (widget.type) {
        case OtpEType.WITHDRAW:
          if (result.error == "This product is currently out of stock" ||
              result.error ==
                  "The gold in inventory is not enough to withdraw") {
            Navigator.pop(context);
            ToastService.toastError(context, result.error);
            await context.read<WithdrawProvider>().getUserWithdraw(widget.id);
            Navigator.pushNamed(context, WithdrawDetailScreen.routeName);
          }
          break;
        default:
          throw UnimplementedError();
      }
    }
    if (mounted && result.isSuccess) {
      Navigator.popUntil(context, (route) => route.isFirst);
      ToastService.toastSuccess(context, result.message);

      if (!context.mounted) return;
      switch (widget.type) {
        case OtpEType.ORDER:
          context
              .read<OrderProvider>()
              .getDetailOrder(context, orderId: widget.id);
          break;
        case OtpEType.TRANSACTION:
          await context.read<TransactionProvider>().getUserTransaction();
          Navigator.pushNamed(context, TransactionDetailScreen.routeName);
          break;
        case OtpEType.WITHDRAW:
          await context.read<WithdrawProvider>().getUserWithdraw(widget.id);
          Navigator.pushNamed(context, WithdrawDetailScreen.routeName);
          break;
        default:
          throw UnimplementedError();
      }
    }
  }
}
