import 'package:blockchain_mobile/1_controllers/providers/transaction_provider.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/3_components/dialogs/confirm_dialog.dart';
import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/3_components/text_bold.dart';
import 'package:blockchain_mobile/4_helper/decorations/text_styles.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:provider/provider.dart';
import 'package:signature/signature.dart';

class ContractSignature extends StatefulWidget {
  const ContractSignature({super.key, required this.transactionId});
  final int transactionId;

  @override
  State<ContractSignature> createState() => _ContractSignatureState();
}

class _ContractSignatureState extends State<ContractSignature> {
  final contractSignature = SignatureController();

  final publicKeyFocus = FocusNode();

  final _signatureController = SignatureController();
  final _publicKeyController = TextEditingController();

  double signatureSize = 270;

  final _formKey = GlobalKey<FormState>();

  @override
  Widget build(BuildContext context) {
    return AlertDialog.adaptive(
      title: const Text("Update Signature"),
      content: SingleChildScrollView(
        child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Container(
                decoration: BoxDecoration(
                  color: Colors.white54,
                  border: Border.all(
                    width: 2,
                    color: kSecondaryColor,
                    strokeAlign: BorderSide.strokeAlignInside,
                  ),
                ),
                child: Center(
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
                        icon: const FaIconGen(FontAwesomeIcons.x),
                      ),
                    ),
                  ]),
                ),
              ),
              const SizedBox(
                height: 10,
              ),
              Text(
                "Hint: your public key are located in your profile screen",
                style: Theme.of(context)
                    .textTheme
                    .bodySmall!
                    .copyWith(color: kHintTextColor),
              ),
              const SizedBox(
                height: 10,
              ),
              Form(
                key: _formKey,
                child: FocusScope(
                  onFocusChange: (value) {
                    setState(() {});
                  },
                  child: TextFormField(
                    controller: _publicKeyController,
                    obscureText: true,
                    validator: (value) {
                      if (value == null) {
                        return "Please enter public key";
                      }
                      return null;
                    },
                    focusNode: publicKeyFocus,
                    autovalidateMode: AutovalidateMode.always,
                    decoration: const InputDecoration(
                        border: OutlineInputBorder(),
                        label: Text("Public Key"),
                        hintText: "Enter your public key",
                        suffixIcon: FaIconGen(FontAwesomeIcons.key, width: 18)),
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
            var signature = await _signatureController.toPngBytes();
            if (signature == null) {
              if (context.mounted) {
                ToastService.toastError(context, "Please add your signature");
              }
              return;
            }
            if (!_formKey.currentState!.validate()) return;
            if (this.context.mounted) {
              Navigator.of(this.context).pop(true);
              await this.context.read<TransactionProvider>().processSignature(
                  this.context,
                  transactionId: widget.transactionId,
                  signature: signature,
                  secretKey: _publicKeyController.text,
                  isUpdate: true);
            }
          },
          style: confirmTextBtnStyle,
          child: const TextBold("Confirm"),
        )
      ],
    );
  }
}
