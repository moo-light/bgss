import 'package:blockchain_mobile/1_controllers/providers/transaction_provider.dart';
import 'package:blockchain_mobile/3_components/dialogs/confirm_dialog.dart';
import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:provider/provider.dart';
import 'package:signature/signature.dart';

class SignContract extends StatefulWidget {
  const SignContract({super.key});

  @override
  State<SignContract> createState() => SignContractState();
}

class SignContractState extends State<SignContract> {
  var publicKeyFocus = FocusNode();
  double signatureSize = 270;
  final _signatureController = SignatureController();
  final _formKey = GlobalKey<FormState>();
  final TextEditingController _publicKeyController = TextEditingController();
  @override
  Widget build(BuildContext context) {
    var transaction =
        context.watch<TransactionProvider>().currentTransaction.result!;
    return Scaffold(
      body: SafeArea(
        child: SingleChildScrollView(
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
                  width: signatureSize,
                  height: signatureSize,
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
                Visibility(
                  visible: transaction.transactionVerification == "UNVERIFIED",
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
                  visible: transaction.transactionVerification == "UNVERIFIED",
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
                        autovalidateMode: AutovalidateMode.onUserInteraction,
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
      ),
    );
  }
}
