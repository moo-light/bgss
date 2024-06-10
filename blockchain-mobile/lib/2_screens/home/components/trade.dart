import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/live_rate_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/transaction_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/transfer_gold_unit_provider.dart';
import 'package:blockchain_mobile/1_controllers/repositories/transaction_repository.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/2_screens/card_info/card_info_edit_screen.dart';
import 'package:blockchain_mobile/2_screens/sign_in/sign_in_screen.dart';
import 'package:blockchain_mobile/2_screens/transaction_detail/transaction_detail_screen.dart';
import 'package:blockchain_mobile/3_components/dialogs/confirm_dialog.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading.dart';
import 'package:blockchain_mobile/3_components/loader/loading_floating_dialog.dart';
import 'package:blockchain_mobile/4_helper/extensions/string_extension.dart';
import 'package:blockchain_mobile/4_helper/keyboard.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/transfer_gold_unit.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:gap/gap.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:provider/provider.dart';

class Trade extends StatefulWidget {
  final String code;

  const Trade(
    this.code, {
    super.key,
  });

  @override
  State<Trade> createState() => _TradeState();
}

class _TradeState extends State<Trade> {
  int selected = 0;
  final _formKey = GlobalKey<FormState>();
  final _amountController = TextEditingController();

  TransactionType get type => TransactionType.values[selected];

  double bid = 0;
  double ask = 0;
  double get currentPrice => selected == 0 ? ask : bid;
  bool isEmptySecretKey = false;

  List<TransferGoldUnit> conversion = [];
  TransferGoldUnit? goldUnit;
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    ask = context.watch<LiveRateProvider>().liveRate?.ask ?? 0; //buy
    bid = context.watch<LiveRateProvider>().liveRate?.bid ?? 0; //sell
    double limit = double.parse(context
        .read<TransferGoldUnitProvider>()
        .convert(maxx, defaultGoldUnit, goldUnit?.fromUnit ?? "")
        .toStringAsFixed(2));
    double minLimit = double.parse(context
        .read<TransferGoldUnitProvider>()
        .convert(minn, defaultGoldUnit, goldUnit?.fromUnit ?? "")
        .toStringAsFixed(2));
    var cost = currencyFormat((selected == 0 ? ask : bid) *
        context.read<TransferGoldUnitProvider>().convert(
            double.tryParse(_amountController.text) ?? 0,
            goldUnit?.fromUnit ?? "",
            defaultCost_GoldUnit));
    bool isEmptySecretKey = context.watch<AuthProvider>().publicKey.isEmpty;
    conversion =
        context.watch<TransferGoldUnitProvider>().goldConvertValues.result ??
            [];
    if (context.watch<TransferGoldUnitProvider>().goldConvertValues.isLoading) {
      return const IsLoadingWG(isLoading: true);
    }
    goldUnit = context.watch<TransferGoldUnitProvider>().currentGoldUnit;

    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(15.0),
          child: Row(
            children: [
              _expandButton(
                onPressed: () => setState(() {
                  selected = 0;
                }),
                text: "Buy",
                color: selected == 0 ? kWinColor : kIconColor.withOpacity(0.3),
              ),
              const SizedBox(
                width: 10,
              ),
              _expandButton(
                onPressed: () => setState(() {
                  selected = 1;
                }),
                text: "Sell",
                color: selected == 1 ? kLossColor : kIconColor.withOpacity(0.3),
              ),
            ],
          ),
        ),
        const SizedBox(
          height: 10,
        ),
        Padding(
          padding: const EdgeInsets.all(10.0),
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                TextFormField(
                  validator: (value) {
                    value = value?.replaceAll(RegExp(r","), '.');
                    if (value == null || value.isEmpty) {
                      return "please enter a value";
                    }
                    try {
                      var v = double.parse(value);
                      if (v > limit) return "value too large";
                      if (v <= minLimit) {
                        return "value too small ";
                      }
                    } catch (e) {
                      return "invalid value";
                    }
                    return null;
                  },
                  onChanged: (value) {
                    _formKey.currentState?.validate();
                    setState(() {});
                  },
                  onSaved: (newValue) => setState(() {
                    try {
                      _amountController.text =
                          double.parse(newValue!).toStringAsFixed(2);
                    } catch (e) {
                      _amountController.text = "0";
                    }
                  }),
                  onEditingComplete: () => _formKey.currentState?.reset(),
                  keyboardType: const TextInputType.numberWithOptions(
                    decimal: true,
                  ),
                  inputFormatters: [
                    FilteringTextInputFormatter.allow(RegExp(r'[0-9]|,|.')),
                  ],
                  autofocus: false,
                  decoration: InputDecoration(
                    label: Text("Quantity (${goldUnit?.symbol})"),
                    counterText:
                        "$minLimit ${goldUnit?.symbol}-$limit ${goldUnit?.symbol}",
                    suffixIcon: GestureDetector(
                      onTap: () => showDialog(
                        context: context,
                        builder: (context) => _buildGoldUnitOption(),
                      ),
                      child: Container(
                        margin: const EdgeInsets.only(right: 8),
                        child: Row(
                          mainAxisSize: MainAxisSize.min,
                          children: [
                            Text.rich(
                              TextSpan(text: goldUnit?.symbol),
                              style: Theme.of(context)
                                  .textTheme
                                  .titleSmall!
                                  .copyWith(color: Colors.black),
                              textAlign: TextAlign.center,
                            ),
                            const Gap(5),
                            const FaIcon(
                              FontAwesomeIcons.caretDown,
                              size: 15,
                            )
                          ],
                        ),
                      ),
                    ),
                  ),
                  controller: _amountController,
                ),
                const Gap(8),
                Text("${selected == 0 ? "Cost" : "Earn"}: $cost"),
                const Divider(),
                Flex(direction: Axis.horizontal, children: [
                  Expanded(
                    child: ElevatedButton(
                      onPressed: () {
                        KeyboardUtil.hideKeyboard(context);
                        var authProvider =
                            Provider.of<AuthProvider>(context, listen: false);
                        final bool userLogined = authProvider.userLogined;
                        if (authProvider.isAdminOrStaff) {
                          ToastService.toastError(context,
                              "Invalid Action (${authProvider.roles.toString().toCapitalize()})");
                          return;
                        }
                        if (!userLogined) {
                          Navigator.pushNamed(context, SignInScreen.routeName);
                          return;
                        }
                        submitHandler();
                      },
                      style: ElevatedButton.styleFrom(
                        backgroundColor: type == TransactionType.SELL
                            ? kLossColor
                            : kWinColor,
                        shape: const BeveledRectangleBorder(
                            // borderRadius: BorderRadius.circular(10),
                            ),
                      ),
                      child: Column(
                        children: [
                          Text(
                            "Create Order",
                            style: GoogleFonts.openSans(
                              textStyle: const TextStyle(
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            textAlign: TextAlign.center,
                          ),
                        ],
                      ),
                    ),
                  ),
                ])
              ],
            ),
          ),
        )
      ],
    );
  }

  _buildGoldUnitOption() {
    return AlertDialog(
      content: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Text("Select Gold Unit",
              style: GoogleFonts.openSans(
                  textStyle: const TextStyle(
                      fontSize: 24,
                      color: kTextColor,
                      fontWeight: FontWeight.bold))),
          ...conversion
              .where((element) => element.fromUnit == goldUnit?.fromUnit)
              .map((unit) {
            var convertToValue = unit
                .convert(double.tryParse(_amountController.text) ?? 0)
                .toStringAsFixed(2);
            return ListTile(
              title: Text(
                  "${TransferGoldUnit.getSymbol(unit.toUnit)}: $convertToValue"), // Replace with unit.symbol if you have defined symbols for each unit.
              onTap: () {
                _amountController.text = convertToValue;
                context.read<TransferGoldUnitProvider>().setCurrentGoldUnit =
                    unit.toUnit;
                Navigator.of(context).pop();
              },
            );
          })
        ],
      ),
    );
  }

  Widget _expandButton({void Function()? onPressed, String text = "", color}) {
    return Expanded(
      child: MaterialButton(
        color: color,
        onPressed: onPressed,
        child: Text(
          text,
          style: GoogleFonts.openSans(
            textStyle: const TextStyle(
              color: Colors.white,
              fontWeight: FontWeight.bold,
            ),
          ),
          textAlign: TextAlign.center,
        ),
      ),
    );
  }

  void submitHandler() {
    if (isEmptySecretKey) {
      KeyboardUtil.hideKeyboard(context);
      showDialog(
        context: context,
        builder: (context) => SimpleConfirmDialog(
          onConfirm: () {
            Navigator.pushReplacementNamed(
              context,
              CardInfoEditScreen.routeName,
            );
          },
          title: "Requirement",
          content: "Please verify your Card Information before proceeding",
          onCancel: () {
            Navigator.pop(context);
          },
        ),
      );
      return;
    }
    _formKey.currentState!.save();
    if (_formKey.currentState!.validate()) {
      //simulating fake saving liverates
      Provider.of<TransactionProvider>(context, listen: false)
          .processUserTransaction(
        context,
        goldUnit: goldUnit!,
        pricePerOz: currentPrice,
        quantityInOz: double.tryParse(_amountController.text) ?? 0,
        type: type,
      );
      showDialog(
          context: context,
          builder: (context) {
            var processUserTransactionResult =
                Provider.of<TransactionProvider>(context, listen: false)
                    .processUserTransactionResult;

            return LoadingFloatingDialog(
              text: "Saving your liveRate...",
              onPluse: (timer) {
                if (processUserTransactionResult.isSuccess ||
                    processUserTransactionResult.isError) {
                  // pop when api is done
                  Navigator.of(context).pop();
                  if (processUserTransactionResult.isSuccess) {
                    context.read<AuthProvider>().getSecretKey();
                    Navigator.pushNamed(
                        context, TransactionDetailScreen.routeName,
                        arguments: {
                          "goldUnit": goldUnit,
                          "amount": _amountController.text,
                          "currentPrice": currentPrice,
                          'type': type,
                        });
                  }
                  timer.cancel();
                }
              },
            );
          });
    }
  }
}
