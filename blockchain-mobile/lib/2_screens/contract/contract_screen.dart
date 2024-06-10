import 'dart:convert';

import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/transaction_provider.dart';
import 'package:blockchain_mobile/1_controllers/repositories/transaction_repository.dart';
import 'package:blockchain_mobile/3_components/dialogs/confirm_dialog.dart';
import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_button.dart';
import 'package:blockchain_mobile/3_components/loader/loading_floating_dialog.dart';
import 'package:blockchain_mobile/3_components/text_bold.dart';
import 'package:blockchain_mobile/4_helper/decorations/card_decoration.dart';
import 'package:blockchain_mobile/4_helper/decorations/text_styles.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/contract.dart';
import 'package:blockchain_mobile/models/enums/gold_unit.dart';
import 'package:blockchain_mobile/models/enums/transaction_status.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:gap/gap.dart';
import 'package:provider/provider.dart';

import 'components/signature_container.dart';

class ContractScreen extends StatelessWidget {
  static const String routeName = "/transaction-detail/contract";

  const ContractScreen({super.key});

  @override
  Widget build(BuildContext context) {
    var goldTransaction =
        context.watch<TransactionProvider>().currentTransaction.result!;
    var currentSecretKey = context.watch<AuthProvider>().currentSecretKey;
    Contract? contract = goldTransaction.contract;
    if (contract == null) return const Scaffold();
    final json = contract.toJson().entries;
    return Scaffold(
      appBar: AppBar(
        // centerTitle: true,
        title: Text(" Transaction #${goldTransaction.id} Contract"),
      ),
      body: SafeArea(
          child: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            children: [
              Text("Contract", style: Theme.of(context).textTheme.titleLarge),
              const Gap(10),
              Container(
                padding: const EdgeInsets.all(16),
                decoration: cardDecoration.copyWith(
                    borderRadius: BorderRadius.circular(0)),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      "Transaction Information",
                      style: Theme.of(context).textTheme.titleLarge,
                    ),
                    ListTile(
                      title: const Text("Fullname"),
                      subtitle: Text(contract.fullName),
                    ),
                    const Gap(8),
                    ListTile(
                      title: const Text("Address"),
                      subtitle: Text(contract.address),
                    ),
                    const Gap(8),
                    ListTile(
                      title: const Text("Create Date"),
                      subtitle: Text(dateFormat(contract.createdAt)),
                    ),
                  ],
                ),
              ),
              const Divider(),
              Flex(direction: Axis.horizontal, children: [
                const SizedBox(width: 150, child: TextBold("Company Name")),
                const Spacer(),
                Text(contract.confirmingParty)
              ]),
              Flex(
                direction: Axis.horizontal,
                children: [
                  const SizedBox(width: 150, child: TextBold("Status")),
                  Expanded(
                    // Use Expanded instead of Spacer to fill the available space
                    child: Text(
                      goldTransaction.transactionStatus.name,
                      style: boldTextStyle.copyWith(
                        color: goldTransaction.transactionStatus.color,
                      ),
                      softWrap: true,
                      // This will wrap the text onto the next line if needed
                      textAlign: TextAlign.end,
                    ),
                  ),
                ],
              ),
              Flex(
                direction: Axis.horizontal,
                children: [
                  const SizedBox(
                      width: 150, child: TextBold("Transaction Type")),
                  Expanded(
                    child: Text(
                      contract.transactionType,
                      style: boldTextStyle.copyWith(
                        color: goldTransaction.transactionType.color,
                      ),
                      softWrap: true,
                      textAlign: TextAlign.end,
                    ),
                  ),
                ],
              ),
              Flex(
                direction: Axis.horizontal,
                children: [
                  const SizedBox(width: 150, child: TextBold("Quantity")),
                  Expanded(
                    child: Text(
                      "${numberFormat(contract.quantity)} ${goldTransaction.goldUnit.symbol}",
                      softWrap: true,
                      textAlign: TextAlign.end,
                    ),
                  ),
                ],
              ),
              Flex(
                direction: Axis.horizontal,
                children: [
                  const SizedBox(width: 150, child: TextBold("Gold Unit")),
                  Expanded(
                    child: Text(
                      contract.goldUnit,
                      softWrap: true,
                      textAlign: TextAlign.end,
                    ),
                  ),
                ],
              ),
              Flex(
                direction: Axis.horizontal,
                children: [
                  const SizedBox(width: 150, child: TextBold("Amount")),
                  Expanded(
                    child: Text(
                      currencyFormat(contract.pricePerOunce),
                      style: const TextStyle(
                          color: kPrimaryColor, fontWeight: FontWeight.bold),
                      softWrap: true,
                      textAlign: TextAlign.end,
                    ),
                  ),
                ],
              ),
              Flex(
                direction: Axis.horizontal,
                children: [
                  const SizedBox(
                      width: 150, child: TextBold("Status Contract")),
                  Expanded(
                    child: Text(
                      contract.contractStatus,
                      softWrap: true,
                      textAlign: TextAlign.end,
                    ),
                  ),
                ],
              ),
              const Gap(20),
              Row(
                children: [
                  Expanded(
                    child: Column(
                      children: [
                        Text(
                          "Customer's Signature",
                          style: Theme.of(context).textTheme.titleSmall,
                        ),
                        const Gap(4),
                        SignatureContainer(
                          signature: Image.memory(
                            base64Decode(contract.signatureOfActionParty),
                          ),
                        ),
                        const Text.rich(
                          TextSpan(
                            children: [
                              TextSpan(text: "Signature:____________\n"),
                              TextSpan(text: "Date:________________")
                            ],
                          ),
                        ),
                      ],
                    ),
                  ),
                  const VerticalDivider(),
                  Expanded(
                    child: Column(
                      children: [
                        Text(
                          "Company's Signature",
                          style: Theme.of(context).textTheme.titleSmall,
                        ),
                        const Gap(4),
                        SignatureContainer(
                          signature: Image.memory(
                            base64Decode(contract.signatureOfConfirmingParty),
                          ),
                        ),
                        const Text.rich(TextSpan(children: [
                          TextSpan(text: "Signature:____________\n"),
                          TextSpan(text: "Date:________________")
                        ])),
                      ],
                    ),
                  ),
                ],
              ),
              const Gap(20),
              Visibility(
                visible: contract.contractStatus == "DIGITAL_UNSIGNED",
                replacement: const Row(
                  children: [
                    Text("Contract Signed!"),
                    Gap(4),
                    FaIconGen(
                      FontAwesomeIcons.check,
                      width: 18,
                    )
                  ],
                ),
                child: IsLoadingButton(
                  spannedLoading: true,
                  isLoading: false,
                  child: ElevatedButton(
                    onPressed: () {
                      showAdaptiveDialog(
                        context: context,
                        builder: (context) => SimpleConfirmDialog(
                          onConfirm: () {
                            Navigator.pop(context);
                            context.read<TransactionProvider>().verifyContract(
                                  context,
                                  contract.id,
                                  currentSecretKey.result,
                                );

                            showDialog(
                                context: context,
                                builder: (context) {
                                  var verifyContractResult = context
                                      .watch<TransactionProvider>()
                                      .verifyContractResult;
                                  return LoadingFloatingDialog(
                                    text: "Confirming contract...",
                                    onPluse: (timer) {
                                      if (verifyContractResult.isSuccess ||
                                          verifyContractResult.isError) {
                                        verifyContractResult
                                            .handleResult(context);
                                        Navigator.pop(context);
                                        timer.cancel();
                                      }
                                    },
                                  );
                                });
                          },
                          onCancel: () {
                            Navigator.pop(context);
                          },
                          content:
                              "by confirming to this contract you are argee to our terms and services",
                          confirmStyle: TextButton.styleFrom(
                            backgroundColor: kTerinaryColor,
                            foregroundColor: Colors.black87,
                          ),
                          confirmType: EConfirmType.CONFIRM_CANCEL,
                        ),
                      );
                    },
                    style: ElevatedButton.styleFrom(
                        minimumSize: const Size(double.infinity, 48),
                        shape: const BeveledRectangleBorder(),
                        backgroundColor: kTerinaryColor,
                        foregroundColor: Colors.black87),
                    child: const Text("Confirm Contract"),
                  ),
                ),
              ),
            ],
          ),
        ),
      )),
    );
  }
}
