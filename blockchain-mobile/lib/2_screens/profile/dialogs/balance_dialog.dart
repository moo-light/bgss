import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/order_provider.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/balance.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:gap/gap.dart';
import 'package:provider/provider.dart';

import '../../../3_components/selection_components.dart';

class BalanceDialog extends StatefulWidget {
  const BalanceDialog({super.key, required this.balance});

  final Balance balance;

  @override
  State<BalanceDialog> createState() => _BalanceDialogState();
}

class _BalanceDialogState extends State<BalanceDialog> {
  final balanceController = TextEditingController(text: "");

  final _vnpayFormKey = GlobalKey<FormState>();
  final _manualFormKey = GlobalKey<FormState>();
  final List<Map<String, dynamic>> selections = [
    {'label': "VNPAY"},
    {'label': "Manual"},
  ];
  var selected = 0;

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text("Deposit"),
      content: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text.rich(
            TextSpan(text: "Balance: ", children: [
              TextSpan(
                  text: currencyFormat(
                    widget.balance.amount,
                  ),
                  style: const TextStyle(color: kSecondaryColor))
            ]),
            style: Theme.of(context)
                .textTheme
                .titleMedium!
                .copyWith(fontWeight: FontWeight.bold),
          ),
          const Gap(10),
          SingleChildScrollView(
            child: Wrap(
              spacing: 8,
              children: List.generate(selections.length, (index) {
                return SelectionComponents(
                  selected: selected == index,
                  onPressed: () => setState(() {
                    _manualFormKey.currentState!.reset();
                    _vnpayFormKey.currentState!.reset();
                    selected = index;
                  }),
                  label: selections[index]['label'],
                );
              }),
            ),
          ),
          const Gap(10),
          IndexedStack(
            index: selected,
            children: [
              _buildVNPAYForm(context),
              _buildManualForm(context),
            ],
          )
        ],
      ),
      actions: <Widget>[
        ElevatedButton(
          style: ElevatedButton.styleFrom(
            backgroundColor: Colors.grey,
          ),
          onPressed: () {
            Navigator.of(context).pop();
          },
          child: const Text("Close"),
        ),
        ElevatedButton(
          onPressed:
              balanceController.text.isNotEmpty ? () => submitHandler() : null,
          child: const Text("Ok"),
        ),
      ],
    );
  }

  submitHandler() async {
    _vnpayFormKey.currentState!.save();
    _manualFormKey.currentState!.save();
    bool valid;
    if (selected == 0) {
      valid = _vnpayFormKey.currentState!.validate();
    } else {
      valid = _manualFormKey.currentState!.validate();
    }
    if (valid) {
      if (selected == 0) {
        context
            .read<OrderProvider>()
            .createPayment(
              context,
              amount: double.parse(balanceController.text),
            )
            .then((value) {
          if (context.mounted) {
            Provider.of<OrderProvider>(context, listen: false).paymentDone ==
                    "00"
                ? ToastService.toastSuccess(
                    context,
                    Provider.of<OrderProvider>(context, listen: false)
                        .paymentMessage)
                : ToastService.toastError(
                    context,
                    Provider.of<OrderProvider>(context, listen: false)
                        .paymentMessage);
          }
          context.read<AuthProvider>().getCurrentUser();
          return null;
        });
      }
    }
  }

  Widget _buildVNPAYForm(BuildContext context) {
    return Form(
      key: _vnpayFormKey,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          TextFormField(
            autocorrect: false,
            decoration: const InputDecoration(
              hintText: "Add Your deposit",
              label: Text("Deposit amount"),
              prefixIcon: FaIconGen(FontAwesomeIcons.dollarSign),
              suffix: Text("VND"),
            ),
            validator: (value) {
              double balance;
              try {
                balance = double.parse(balanceController.text);
              } catch (e) {
                return "Please enter a valid number";
              }
              if ((balance < 10000)) {
                return r"only allow more than 10.000 VND";
              }
              return null;
            },
            onChanged: (value) => setState(() {}),
            onSaved: (newValue) {
              try {
                balanceController.text =
                    double.parse(balanceController.text).toString();
              } catch (e) {
                return;
              }
            },
            controller: balanceController,
            keyboardType: TextInputType.number,
          ),
          Text.rich(TextSpan(text: "Temp Price: ", children: [
            TextSpan(
                text: currencyFormat(
                    (double.tryParse(balanceController.text) ?? 0) / 24000))
          ]))
        ],
      ),
    );
  }

  _buildManualForm(BuildContext context) {
    return Form(
      key: _manualFormKey,
      child: TextFormField(
        autocorrect: false,
        decoration: const InputDecoration(
          hintText: "Add Your deposit",
          label: Text("Deposit amount"),
          prefixIcon: FaIconGen(FontAwesomeIcons.dollarSign),
          suffix: Text("USD"),
        ),
        validator: (value) {
          double balance;
          try {
            balance = double.parse(balanceController.text);
          } catch (e) {
            return "Please enter a valid number";
          }
          if ((balance < 10)) {
            return r"only allow deposit more than $10";
          }
          return null;
        },
        controller: balanceController,
        keyboardType: TextInputType.number,
      ),
    );
  }
}
