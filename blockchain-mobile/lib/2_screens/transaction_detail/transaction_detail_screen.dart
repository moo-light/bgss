import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/transaction_provider.dart';
import 'package:blockchain_mobile/1_controllers/repositories/transaction_repository.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/3_components/dialogs/confirm_dialog.dart';
import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_button.dart';
import 'package:blockchain_mobile/3_components/text_bold.dart';
import 'package:blockchain_mobile/4_helper/decorations/text_styles.dart';
import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/enums/transaction_signature.dart';
import 'package:blockchain_mobile/models/gold_transaction.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:provider/provider.dart';
import 'package:signature/signature.dart';

import '../contract/contract_screen.dart';
import 'components/trade_infomation.dart';

class TransactionDetailScreen extends StatefulWidget {
  const TransactionDetailScreen({super.key});
  static const String routeNameConfirm = "/trade-confirm";
  static const String routeName = "/transaction-detail";

  @override
  State<TransactionDetailScreen> createState() =>
      _TransactionDetailScreenState();
}

class _TransactionDetailScreenState extends State<TransactionDetailScreen> {
  final _formKey = GlobalKey<FormState>();
  final _signatureController = SignatureController();
  final _publicKeyController = TextEditingController();

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    super.dispose();
    _signatureController.dispose();
  }

  @override
  Widget build(BuildContext context) {
    var currentTransaction =
        context.watch<TransactionProvider>().currentTransaction;
    final transaction = currentTransaction.result;
    final goldUnit = transaction!.getGoldUnit();
    final amount = transaction.quantity;
    final currentUser = context.watch<AuthProvider>().currentUser;
    final subTotal = transaction.totalCostOrProfit;
    const tax = 0;
    final total = transaction.totalCostOrProfit + tax;
    //check if route is view detail or confirm trade
    var route = ModalRoute.of(context)?.settings.name;
    final isConfirmTrade = route == TransactionDetailScreen.routeNameConfirm;
    var publicKey = context.watch<AuthProvider>().publicKey;
    final bool isEmptySecretKey =
        context.watch<AuthProvider>().publicKey.isEmpty ?? false;
    var userBalance = currentUser?.userInfo.balance.amount ?? 0;

    return Scaffold(
      appBar: AppBar(
        // centerTitle: true,
        title: const Text("Trade Information"),
      ),
      resizeToAvoidBottomInset: false,
      body: Column(
        children: <Widget>[
          // CheckoutBuyerInformation(formKey: _formKey),
          Container(
            constraints: const BoxConstraints(minWidth: double.infinity),
            child: TradeInfomation(
              transaction: transaction,
            ),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 16),
            child: Row(
              children: [
                Column(
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text.rich(
                      TextSpan(
                        text: 'Verification: ',
                        style: const TextStyle(fontWeight: FontWeight.bold),
                        children: [
                          TextSpan(
                            text: transaction.transactionVerification,
                            style: TextStyle(
                                fontWeight: FontWeight.bold,
                                color: transaction.getVerificationColor()),
                          ),
                        ],
                      ),
                    ),
                    Text.rich(
                      TextSpan(
                        text: 'Signature: ',
                        style: const TextStyle(fontWeight: FontWeight.bold),
                        children: [
                          TextSpan(
                            text: transaction.transactionSignature.name,
                            style: TextStyle(
                                fontWeight: FontWeight.bold,
                                color: transaction.transactionSignature.color),
                          ),
                          // WidgetSpan(
                          //     child: Padding(
                          //   padding: const EdgeInsets.only(left: 8.0),
                          //   child: GestureDetector(
                          //     onTap: () {},
                          //     child: const FaIcon(
                          //       FontAwesomeIcons.fileSignature,
                          //       size: 15,
                          //       color: kHintTextColor,
                          //     ),
                          //   ),
                          // ))
                        ],
                      ),
                    ),
                  ],
                ),
                const Spacer(),
                Visibility(
                  visible: transaction.contract != null,
                  child: TextButton.icon(
                    onPressed: () {
                      Navigator.pushNamed(context, ContractScreen.routeName);
                    },
                    style: TextButton.styleFrom(
                        backgroundColor: kTerinaryColor.withHSLlighting(75),
                        foregroundColor: Colors.black54,
                        textStyle: boldTextStyle),
                    label: const Text("View Contract"),
                    icon: const FaIcon(
                      FontAwesomeIcons.signature,
                      size: 18,
                    ),
                  ),
                )
              ],
            ),
          ),
          const Divider(),
          const Spacer(),
          Visibility(
            visible: transaction.transactionSignature ==
                TransactionSignature.UNSIGNED,
            child: Padding(
              padding:
                  const EdgeInsets.symmetric(horizontal: 24.0, vertical: 16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  // Adjust spacing as needed
                  Text("Trade Summary:",
                      style: Theme.of(context).textTheme.titleLarge),
                  Row(
                    children: [
                      const Text("Balance:"),
                      const Spacer(),
                      Text(
                        currencyFormat(userBalance),
                        style: balanceStyle,
                      )
                    ],
                  ),
                  Row(
                    children: [
                      const Text("Inventory:"),
                      const Spacer(),
                      Text(
                        "${numberFormat(currentUser?.userInfo.inventory.totalWeightOz ?? 0)} tOz",
                        style: inventoryStyle,
                      )
                    ],
                  ),
                  const Divider(),
                  Row(
                    children: [
                      const Text("Subtotal"),
                      const Spacer(),
                      Text(currencyFormat(subTotal))
                    ],
                  ),
                  // Row(
                  //   children: [
                  //     const Text("Tax"),
                  //     const Spacer(),
                  //     Text('\$${tax.toStringAsFixed(2)}')
                  //   ],
                  // ),
                  // const Row(
                  //   children: [Text("Discount"), Spacer(), Text('\$0.00')],
                  // ),
                  const Divider(),
                  Align(
                    alignment: Alignment.centerRight,
                    child: Text(
                      "Included tax if applicable",
                      style: Theme.of(context)
                          .textTheme
                          .bodySmall!
                          .copyWith(color: kHintTextColor),
                      textAlign: TextAlign.end,
                    ),
                  ),
                  DefaultTextStyle(
                    style: Theme.of(context).textTheme.bodyLarge!,
                    child: Row(
                      children: [
                        const Text("Total"),
                        const Spacer(),
                        Text(
                          ' ${currencyFormat(total)}',
                          style: boldTextStyle.copyWith(
                              color: transaction.transactionType ==
                                      TransactionType.BUY
                                  ? kLossColor
                                  : kWinColor),
                        ),
                      ],
                    ),
                  ),
                ],
              ),
            ),
          ),
          // Checkout Button
        ],
      ),
      bottomNavigationBar: Visibility(
        visible:
            transaction.transactionSignature != TransactionSignature.SIGNED,
        child: Row(
          children: [
            // Expanded(
            //   child: IsLoadingButton(
            //     isLoading: context
            //         .watch<TransactionProvider>()
            //         .processUserTransactionResult
            //         .isLoading,
            //     child: ElevatedButton(
            //       onPressed: () {},
            //       style: ElevatedButton.styleFrom(
            //         backgroundColor: kLossColor,
            //         shape: const ContinuousRectangleBorder(),
            //       ),
            //       child: const Text("Cancel Order"),
            //     ),
            //   ),
            // ),
            Expanded(
              child: IsLoadingButton(
                isLoading: context
                    .watch<TransactionProvider>()
                    .processSignatureResult
                    .isLoading,
                child: ElevatedButton(
                  onPressed: !isEmptySecretKey
                      ? () async {
                          await _showSignatureDialog(context, transaction);
                          // Navigator.push(context,MaterialPageRoute(builder: (context) {
                          //   return const SignContract();
                          // },) );
                        }
                      : null,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: kPrimaryColor,
                    shape: const ContinuousRectangleBorder(),
                  ),
                  child: Text(
                    isEmptySecretKey &&
                            !context
                                .watch<AuthProvider>()
                                .currentSecretKey
                                .isLoading
                        ? "Please Verify your Card Information to proceed"
                        : "Confirm Transaction",
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Future<bool?> _showSignatureDialog(
      BuildContext context, GoldTransaction transaction) async {
    var publicKeyFocus = FocusNode();
    bool keyboardVisible = false;

    return await showDialog<bool?>(
        context: context,
        builder: (context) {
          double signatureSize = 270;

          return StatefulBuilder(
            builder: (context, setState) => AlertDialog.adaptive(
              title: const Text("Verify Signature"),
              content: SingleChildScrollView(
                child: Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Center(
                        child: Container(
                          width: signatureSize,
                          height: signatureSize,
                          decoration: BoxDecoration(
                            color: Colors.white54,
                            border: Border.all(
                              width: 2,
                              color: kSecondaryColor,
                              strokeAlign: BorderSide.strokeAlignOutside,
                            ),
                          ),
                          child: Stack(children: [
                            AbsorbPointer(
                              absorbing: publicKeyFocus.hasFocus,
                              child: Signature(
                                width: signatureSize,
                                height: signatureSize,
                                controller: _signatureController,
                                backgroundColor: Colors.transparent,
                              ),
                            ),
                            Positioned(
                              right: 0,
                              child: IconButton(
                                onPressed: () async {
                                  bool? result = await showDialog(
                                    context: context,
                                    builder: (context) => SimpleConfirmDialog(
                                      title: "Clear signature",
                                      confirmType: EConfirmType.YES_NO,
                                      onCancel: () {
                                        Navigator.pop(context);
                                      },
                                      onConfirm: () async {
                                        Navigator.pop(context, true);
                                      },
                                    ),
                                  );
                                  if (result == true) {
                                    _signatureController.clear();
                                  }
                                },
                                icon: FaIconGen(
                                  FontAwesomeIcons.x,
                                  color: kLossColor.withHSLlighting(60),
                                ),
                              ),
                            ),
                          ]),
                        ),
                      ),
                      const SizedBox(
                        height: 10,
                      ),
                      Visibility(
                        visible:
                            transaction.transactionVerification == "UNVERIFIED",
                        child: Text(
                          "Hint: your public key are located in your profile screen",
                          style: Theme.of(context)
                              .textTheme
                              .bodySmall!
                              .copyWith(color: kHintTextColor),
                        ),
                      ),
                      const SizedBox(
                        height: 10,
                      ),
                      Visibility(
                        visible:
                            transaction.transactionVerification == "UNVERIFIED",
                        child: Form(
                          key: _formKey,
                          child: FocusScope(
                            onFocusChange: (value) {
                              setState(() {});
                            },
                            child: TextFormField(
                              controller: _publicKeyController,
                              obscureText: true,
                              validator: (value) {
                                if (value == null || value.isEmpty) {
                                  return "Please enter public key";
                                }
                                return null;
                              },
                              focusNode: publicKeyFocus,
                              autovalidateMode:
                                  AutovalidateMode.onUserInteraction,
                              decoration: const InputDecoration(
                                border: OutlineInputBorder(),
                                label: Text("Public Key"),
                                hintText: "Enter your public key",
                                suffixIcon: FaIconGen(
                                  FontAwesomeIcons.key,
                                  width: 18,
                                ),
                              ),
                            ),
                          ),
                        ),
                      ),
                    ]),
              ),
              actions: [
                StatefulBuilder(
                  builder: (context, setState) {
                    return TextButton(
                      onPressed: () async {
                        Navigator.pop(context);
                      },
                      style: cancelTextBtnStyle,
                      child: const TextBold("Cancel"),
                    );
                  },
                ),
                TextButton(
                  onPressed: () async {
                    var isVerified =
                        transaction.transactionVerification == "VERIFIED";
                    var signature = await _signatureController.toPngBytes();
                    if (signature == null) {
                      if (context.mounted) {
                        ToastService.toastError(
                            context, "Please add your signature");
                      }
                      return;
                    }
                    if (!isVerified) {
                      if (!_formKey.currentState!.validate()) return;
                    } else {
                      _publicKeyController.text =
                          Provider.of<AuthProvider>(context, listen: false)
                              .publicKey;
                    }
                    if (this.context.mounted) {
                      Navigator.of(this.context).pop(true);

                      await this
                          .context
                          .read<TransactionProvider>()
                          .processSignature(
                            this.context,
                            transactionId: transaction.id,
                            signature: signature,
                            secretKey: _publicKeyController.text,
                            isUpdate: isVerified,
                          );
                    }
                  },
                  style: confirmTextBtnStyle,
                  child: const TextBold("Confirm"),
                )
              ],
            ),
          );
        });
  }
}
