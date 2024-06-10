import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/withdraw_provider.dart';
import 'package:blockchain_mobile/2_screens/otp/otp_screen.dart';
import 'package:blockchain_mobile/3_components/card_container.dart';
import 'package:blockchain_mobile/3_components/failed_information.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_screen.dart';
import 'package:blockchain_mobile/3_components/text_bold.dart';
import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/4_helper/qrcode_helper.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/enums/gold_unit.dart';
import 'package:blockchain_mobile/models/withdraw_gold.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:gap/gap.dart';
import 'package:provider/provider.dart';

class WithdrawDetailScreen extends StatelessWidget {
  WithdrawDetailScreen({super.key});
  static String routeName = "/withdraw/detail";
  final scaffoldKeyy = GlobalKey<ScaffoldState>();

  @override
  Widget build(BuildContext context) {
    var currentWithdraw = context.watch<WithdrawProvider>().currentWithdraw;
    if (currentWithdraw.isLoading) {
      return const LoadingScreen();
    }
    if (currentWithdraw.isError) {
      return FailedInformation(
        child: Text(currentWithdraw.message),
      );
    }
    WithdrawGold withdraw = currentWithdraw.result!;
    bool isLoading =
        context.watch<WithdrawProvider>().getUserWithdrawResult.isLoading;
    if (isLoading) {
      return const LoadingScreen();
    }
    bool isAdminOrStaff = context
        .watch<AuthProvider>()
        .roles!
        .contains(RegExp("STAFF|ADMIN")); // ROLE_ADMIN , ROLE_STAFF
    return Scaffold(
      key: scaffoldKeyy,
      appBar: AppBar(
        title: const Text(
          "Withdraw Details",
        ),
      ),
      body: SafeArea(
        child: PopScope(
          onPopInvoked: (didPop) {
            context.read<WithdrawProvider>().getUserWithdraw();
          },
          child: RefreshIndicator(
            onRefresh: () async {
              context.read<WithdrawProvider>().getUserWithdraw(withdraw.id);
            },
            child: SingleChildScrollView(
              physics: const AlwaysScrollableScrollPhysics(),
              child: DefaultTextStyle(
                style: const TextStyle(fontSize: 16, color: kTextColor),
                child: Column(
                  children: [
                    _buildInformation(context, withdraw),
                    _buildQrCode(withdraw, context),
                    _buildProductDetail(withdraw, context),
                    _buildCancelMessage(withdraw, context),
                    _buildActions(isAdminOrStaff, withdraw, context),
                    const Gap(50)
                  ],
                ),
              ),
            ),
          ),
        ),
      ),
      bottomNavigationBar: Visibility(
        visible: withdraw.status == "UNVERIFIED",
        child: ElevatedButton(
          onPressed: () {
            Navigator.pushNamed(
              context,
              OtpScreen.routeNameWithdraw,
              arguments: withdraw.id,
            );
          },
          style:
              ElevatedButton.styleFrom(shape: const BeveledRectangleBorder()),
          child: const Text("Verify OTP"),
        ),
      ),
    );
  }

  CardContainer _buildInformation(BuildContext context, WithdrawGold withdraw) {
    return CardContainer(
        child: Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          "Withdraw",
          style: Theme.of(context).textTheme.titleLarge,
        ),
        _buildDetailRow("ID", withdraw.id),
        Visibility(
            visible: withdraw.product != null,
            child: _buildDetailRow("Product Id", withdraw.product?.id ?? 0)),
        _buildDetailRow("Gold Unit", withdraw.goldUnit),
        _buildDetailRow("Amount",
            "${numberFormat(withdraw.amount)} ${withdraw.getGoldUnit().symbol}"), //${withdraw.getGoldUnit().symbol}
        const Gap(16),
        _buildDetailRow(
            "Transaction Date", dateFormat(withdraw.transactionDate)),
        _buildDetailRow("Updated Date", withdraw.formatUpdateDate()),
        // _buildDetailRow("User Id", withdraw.userInfoId),

        _buildDetailRow("Withdraw Type", withdraw.type),
        const Gap(16),
        // _buildDetailRow("User Recieved", withdraw.userConfirm),
        _buildDetailRow(
          "Status",
          withdraw.status,
          style: TextStyle(
              color: withdraw.getStatusColor(), fontWeight: FontWeight.bold),
        ),
      ],
    ));
  }

  Widget _buildProductDetail(WithdrawGold withdraw, BuildContext context) {
    if (withdraw.product == null) return Container();
    return CardContainer(
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        children: [
          const Text(
            "Product Detail",
            style: TextStyle(fontSize: 20),
          ),
          const Gap(16),
          Container(
            decoration: BoxDecoration(
                // gradient: LinearGradient(
                //     begin: Alignment.centerLeft,
                //     end: Alignment.centerRight,
                //     colors: [
                //       Colors.grey.shade100,
                //       kBackgroundColor.withHSLlighting(93)
                //     ]),
                borderRadius: BorderRadius.circular(8),
                color: kBackgroundColor.withHSLlighting(94),
                border: Border.all(width: 2, color: Colors.grey.shade400)),
            child: ListTile(
              leading:
                  AspectRatio(aspectRatio: 1, child: withdraw.product?.image),
              title: Text(withdraw.product!.title),
              subtitle: Text(
                'Weight: ${numberFormat(withdraw.amount)} ${withdraw.product?.typeGold.goldUnit.symbol}',
                style: const TextStyle(
                  fontSize: 16,
                  // fontWeight: FontWeight.bold,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Column _buildQrCode(WithdrawGold withdraw, BuildContext context) {
    return Column(mainAxisSize: MainAxisSize.min, children: [
      const Text(
        "Withdraw Code",
        style: TextStyle(fontSize: 20),
      ),
      const Gap(5),
      Container(
        padding: const EdgeInsets.all(8),
        width: 100,
        height: 100,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(8),
          color: Colors.white,
        ),
        child: Image.network(
          QrCodeHelper.getQrImage(
            "${withdraw.withdrawQrCode}",
            type: EQrType.Withdraw,
          ),
        ),
      ),
      const Gap(5),
      TextButton(
        style: TextButton.styleFrom(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16), // Specify the radius here
            side: const BorderSide(
              width: 1,
              color: kHintTextColor,
            ),
          ),
        ),
        onPressed: () {
          Clipboard.setData(ClipboardData(text: withdraw.withdrawQrCode ?? ""));
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text(
                'Copied to clipboard!',
              ),
            ),
          );
        },
        child: Wrap(
          crossAxisAlignment: WrapCrossAlignment.center,
          children: [
            Text(
              withdraw.withdrawQrCode.toString(),
              style: const TextStyle(color: kTextColor),
            ),
            const SizedBox(width: 5),
            const FaIcon(
              FontAwesomeIcons.copy,
              size: 15,
              color: kTextColor,
            ),
          ],
        ),
      )
    ]);
  }

  Visibility _buildCancelMessage(WithdrawGold withdraw, BuildContext context) {
    return Visibility(
      visible: withdraw.cancellationMessages.isNotEmpty,
      child: CardContainer(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              "Cancelation Messages",
              style: Theme.of(context).textTheme.titleLarge,
            ),
            ...List.generate(withdraw.cancellationMessages.length, (index) {
              final messages = withdraw.cancellationMessages[index];
              return Container(
                margin: const EdgeInsets.only(left: 5),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Visibility(
                      visible: index > 0,
                      child: const Divider(),
                    ),
                    _buildDetailRow("Sender", messages.sender),
                    _buildDetailRow("Reciever", messages.receiver),
                    const TextBold("Reason"),
                    Text("\r${messages.reason}")
                  ],
                ),
              );
            })
          ],
        ),
      ),
    );
  }

  Visibility _buildActions(
      bool isAdminOrStaff, WithdrawGold withdraw, BuildContext context) {
    return Visibility(
      visible: isAdminOrStaff &&
          withdraw.status != "COMPLETED" &&
          withdraw.status != "CANCELED",
      // WITHDRAW FOR USER
      replacement: Container(
        margin: const EdgeInsets.symmetric(vertical: 16, horizontal: 16),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          mainAxisSize: MainAxisSize.min,
          children: [
            Visibility(
              visible: (withdraw.status == "PENDING" ||
                  withdraw.status == "UNVERIFIED" ||
                  withdraw.status == "APPROVED"),
              child: Center(
                child: ElevatedButton(
                  onPressed: () => cancelWithdraw(context, withdraw),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: kLossColor,
                  ),
                  child: const Text("Cancel withdraw"),
                ),
              ),
            ),
            Visibility(
              visible: withdraw.status == "APPROVED" &&
                  withdraw.status != "COMPLETED",
              child: Row(mainAxisSize: MainAxisSize.min, children: [
                const Gap(16),
                ElevatedButton(
                  onPressed: () => updateRecievedWithdraw(context, withdraw),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: kWinColor,
                  ),
                  child: RichText(
                    text: const TextSpan(
                      children: [
                        TextSpan(
                          text: "Recieved! ",
                        ),
                        WidgetSpan(
                          child: FaIcon(FontAwesomeIcons.solidCircleCheck,
                              size: 16),
                        ),
                      ],
                    ),
                  ),
                ),
              ]),
            ),
          ],
        ),
      ),
      //Withdraw for staff
      child: Container(
        margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          mainAxisSize: MainAxisSize.min,
          children: [
            Visibility(
              visible: !withdraw.status
                  .contains(RegExp(r"CONFIRMED|COMPLETED|CANCELLED")),
              child: ElevatedButton(
                onPressed: () => cancelWithdraw(context, withdraw),
                style: ElevatedButton.styleFrom(
                  backgroundColor: kLossColor,
                ),
                child: const Text("Cancel withdraw"),
              ),
            ),
            ElevatedButton(
              onPressed: () => updateConfirmWithdraw(context, withdraw),
              style: ElevatedButton.styleFrom(
                backgroundColor: kWinColor,
              ),
              child: RichText(
                text: TextSpan(
                  children: [
                    TextSpan(
                      text: withdraw.status == "CONFIRMED"
                          ? "Set Confirmed "
                          : "Complete Withdraw ",
                    ),
                    const WidgetSpan(
                      child:
                          FaIcon(FontAwesomeIcons.solidCircleCheck, size: 15),
                    ),
                  ],
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  void updateConfirmWithdraw(BuildContext context, WithdrawGold withdraw) {
    showDialog(
        context: context,
        builder: (context) {
          return AlertDialog(
            title: const Text("Confirm Withdraw"),
            content: Visibility(
              visible: withdraw.status == "CONFIRMED",
              child: const Text(
                  "Note: only update this status when customer recieved gold product"),
            ),
            actions: [
              TextButton(
                onPressed: () {
                  Navigator.of(context).pop();
                },
                child: const Text("Cancel"),
              ),
              TextButton(
                onPressed: () async {
                  Navigator.of(context).pop();
                  Provider.of<WithdrawProvider>(scaffoldKeyy.currentContext!,
                          listen: false)
                      .confirmWithdraw(
                          scaffoldKeyy.currentContext!, withdraw.id);
                  scaffoldKeyy.currentContext!
                      .read<WithdrawProvider>()
                      .getUserWithdraw(withdraw.id);
                },
                child: const Text("Confirm"),
              )
            ],
          );
        });
  }

  void updateRecievedWithdraw(BuildContext context, WithdrawGold withdraw) {
    showDialog(
        context: context,
        builder: (context) {
          return AlertDialog(
            title: const Text("Recieved Withdraw"),
            content: const Text(
                "Note: only update this status when you have recieved the gold."),
            actions: [
              TextButton(
                onPressed: () {
                  Navigator.of(context).pop();
                },
                child: const Text("Cancel"),
              ),
              TextButton(
                onPressed: () async {
                  Navigator.of(context).pop();
                  scaffoldKeyy.currentContext!
                      .read<WithdrawProvider>()
                      .recievedWithdraw(
                          scaffoldKeyy.currentContext!, withdraw.id);
                },
                child: const Text("Confirm"),
              )
            ],
          );
        });
  }

  void cancelWithdraw(BuildContext context, WithdrawGold withdraw) {
    var reason = TextEditingController();
    var formKey = GlobalKey<FormState>();
    String? error;
    showDialog(
        context: context,
        builder: (context) {
          return StatefulBuilder(
            builder: (context, setState) {
              return AlertDialog(
                title: const Text("Cancel Withdraw"),
                content: Form(
                  key: formKey,
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      TextFormField(
                        decoration: const InputDecoration(
                          hintText: "Submit your reason",
                          constraints:
                              BoxConstraints(minWidth: double.maxFinite),
                        ),
                        validator: (value) {
                          if (value!.isEmpty) {
                            setState(
                              () => error = "Please enter a reason",
                            );
                            return "";
                          }
                          return null;
                        },
                        onChanged: (value) {
                          setState(() {
                            error = null;
                            formKey.currentState!.validate();
                          });
                        },
                        maxLength: 1000,
                        maxLines: 5,
                        controller: reason,
                      ),
                      Text(
                        error ?? "",
                        style: const TextStyle(color: kLossColor),
                      )
                    ],
                  ),
                ),
                actions: [
                  TextButton(
                    onPressed: () {
                      Navigator.of(context).pop();
                    },
                    child: const Text("Cancel"),
                  ),
                  TextButton(
                    onPressed: () async {
                      Navigator.of(context).pop();
                      Provider.of<WithdrawProvider>(
                              scaffoldKeyy.currentContext!,
                              listen: false)
                          .cancelWithdraw(scaffoldKeyy.currentContext!,
                              withdraw, reason.text);
                    },
                    child: const Text("Confirm"),
                  )
                ],
              );
            },
          );
        });
  }

  Widget _buildDetailRow(String label, data, {TextStyle? style}) {
    return Container(
      margin: const EdgeInsets.symmetric(vertical: 2),
      child: Row(children: [
        TextBold(label),
        const Spacer(),
        Text(
          data.toString(),
          style: style,
        )
      ]),
    );
  }
}
