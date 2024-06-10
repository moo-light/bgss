import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/order_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/products_provider.dart';
import 'package:blockchain_mobile/2_screens/otp/otp_screen.dart';
import 'package:blockchain_mobile/2_screens/product_details/product_details_screen.dart';
import 'package:blockchain_mobile/3_components/card_container.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_screen.dart';
import 'package:blockchain_mobile/3_components/text_bold.dart';
import 'package:blockchain_mobile/4_helper/decorations/card_decoration.dart';
import 'package:blockchain_mobile/4_helper/decorations/text_styles.dart';
import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/4_helper/qrcode_helper.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/enums/e_process_receive_product.dart';
import 'package:blockchain_mobile/models/enums/e_received_status.dart';
import 'package:blockchain_mobile/models/enums/e_user_confirm.dart';
import 'package:blockchain_mobile/models/order.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:gap/gap.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';

class OrderDetailScreen extends StatelessWidget {
  const OrderDetailScreen({super.key});
  static String routeName = "/order/details";
  @override
  Widget build(BuildContext context) {
    bool? isLoading = context.watch<OrderProvider>().currentOrderisLoading;
    if (isLoading) return const LoadingScreen();

    Order order = context.watch<OrderProvider>().currentOrder!;

    bool isAdminOrStaff = context
        .watch<AuthProvider>()
        .roles!
        .contains(RegExp("STAFF|ADMIN")); // ROLE_ADMIN , ROLE_STAFF

    bool isUnverified = order.statusReceived == EReceivedStatus.UNVERIFIED;
    if (isUnverified) order.qrCode = "";
    return Scaffold(
      appBar: AppBar(
        title: const Text(
          "Order Details",
        ),
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          child: DefaultTextStyle(
            style: const TextStyle(fontSize: 16, color: kTextColor),
            child: Column(
              children: [
                CardContainer(
                    child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      "Order",
                      style: Theme.of(context).textTheme.titleLarge,
                    ),
                    _buildDetailRow("ID", order.id),
                    _buildDetailRow("Order Date",
                        DateFormat.yMEd().add_jms().format(order.createDate)),
                  ],
                )),
                Column(mainAxisSize: MainAxisSize.min, children: [
                  const Text(
                    "Order Code",
                    style: TextStyle(fontSize: 20),
                  ),
                  const Gap(5),
                  Container(
                    padding: const EdgeInsets.all(8),
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(8),
                      color: Colors.white,
                    ),
                    child: Container(
                      width: 100,
                      height: 100,
                      decoration: BoxDecoration(
                        image: DecorationImage(
                          image: NetworkImage(
                            QrCodeHelper.getQrImage(
                              order.qrCode,
                              type: EQrType.Order,
                            ),
                          ),
                        ),
                      ),
                      child: Container(
                        color:
                            order.statusReceived == EReceivedStatus.UNVERIFIED
                                ? kHintTextColor.withHSLlighting(90)
                                : null,
                      ),
                    ),
                  ),
                  const Gap(5),
                  Visibility(
                    visible: !isUnverified,
                    replacement: Text(isAdminOrStaff
                        ? "Order isn't verified"
                        : "Please Verify your Order for Qr code"),
                    child: TextButton(
                      style: TextButton.styleFrom(
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(
                              16), // Specify the radius here
                          side: const BorderSide(
                            width: 1,
                            color: kHintTextColor,
                          ),
                        ),
                      ),
                      onPressed: () {
                        Clipboard.setData(ClipboardData(text: order.qrCode));
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
                            order.qrCode.toString(),
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
                    ),
                  )
                ]),
                CardContainer(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        "User Information",
                        style: Theme.of(context).textTheme.titleLarge,
                      ),
                      const Gap(15),
                      Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            const TextBold("Name"),
                            const Spacer(
                              flex: 1,
                            ),
                            Expanded(
                                flex: 2,
                                child: Text(
                                  "${order.firstName} ${order.lastName}",
                                  textAlign: TextAlign.end,
                                ))
                          ]),
                      const Gap(8),
                      Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            const TextBold("Email"),
                            const Spacer(
                              flex: 1,
                            ),
                            Expanded(
                                flex: 2,
                                child: Text(
                                  order.email,
                                  textAlign: TextAlign.end,
                                ))
                          ]),
                      const Gap(8),
                      Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            const TextBold("Phone No"),
                            const Spacer(
                              flex: 1,
                            ),
                            Expanded(
                                flex: 2,
                                child: Text(
                                  order.phoneNumber,
                                  textAlign: TextAlign.end,
                                ))
                          ]),
                      const Gap(8),
                      Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            const TextBold("Address"),
                            const Spacer(
                              flex: 1,
                            ),
                            Expanded(
                                flex: 2,
                                child: Text(
                                  order.address.toString(),
                                  textAlign: TextAlign.end,
                                ))
                          ]),
                    ],
                  ),
                ),
                CardContainer(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        "${order.orderDetails.length} Order items",
                        style: Theme.of(context).textTheme.titleLarge,
                      ),
                      ...order.orderDetails.map((e) {
                        return Container(
                          margin: const EdgeInsets.symmetric(vertical: 8),
                          padding: const EdgeInsets.all(4),
                          decoration: cardDecoration,
                          child: InkWell(
                            onTap: () {
                              Provider.of<ProductProvider>(context,
                                      listen: false)
                                  .getProductById(e.product.id);
                              Navigator.pushNamed(
                                context,
                                ProductDetailsScreen.routeName,
                                arguments: ProductDetailsArguments(
                                    product: e.product, fromCartScreen: true),
                              );
                            },
                            child: Row(
                              mainAxisSize: MainAxisSize.max,
                              children: [
                                Container(
                                  decoration: cardDecoration.copyWith(
                                      color: Colors.white),
                                  width: 70,
                                  height: 70,
                                  child: e.product.image,
                                ),
                                const SizedBox(
                                    width:
                                        10), // Use SizedBox for spacing instead of Gap
                                Expanded(
                                  // Use Expanded to make the Column take up remaining space
                                  child: Column(
                                    mainAxisAlignment: MainAxisAlignment.center,
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                      Text.rich(
                                        TextSpan(
                                          text: e.product.title,
                                          style: const TextStyle(
                                            color: kHintTextColor,
                                            decoration:
                                                TextDecoration.underline,
                                          ),
                                        ),
                                        maxLines: 2,
                                        overflow: TextOverflow.ellipsis,
                                      ),
                                      Text.rich(
                                        TextSpan(children: [
                                          TextSpan(
                                            text: currencyFormat(e.price),
                                            style: boldTextStyle,
                                          ),
                                          const TextSpan(text: " x "),
                                          TextSpan(
                                            text: e.quantity.toString(),
                                          ),
                                          const TextSpan(text: " = "),
                                          TextSpan(
                                            text: currencyFormat(e.amount),
                                            style: boldTextStyle.copyWith(
                                                color: kPrimaryColor),
                                          ),
                                        ]),
                                      ),
                                      Text(
                                        e.processReceiveProduct.name,
                                        style: boldTextStyle.copyWith(
                                            color:
                                                e.processReceiveProduct.color),
                                      )
                                      // Visibility(
                                      //   visible: e.processReceiveProduct !=
                                      //       "PENDING",
                                      //   child: const Row(
                                      //     children: [
                                      //       Text("Order Confirmed "),
                                      //       FaIcon(
                                      //         FontAwesomeIcons.check,
                                      //         size: 16,
                                      //       )
                                      //     ],
                                      //   ),
                                      // ),
                                    ],
                                  ),
                                ),
                              ],
                            ),
                          ),
                        );
                      })
                    ],
                  ),
                ),
                CardContainer(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        "Payment Info",
                        style: Theme.of(context).textTheme.titleLarge,
                      ),
                      Row(children: [
                        const TextBold("Status"),
                        const Spacer(),
                        Text(
                          order.statusReceived.name,
                          style: TextStyle(
                            color: order.statusReceived.color,
                            fontWeight: FontWeight.bold,
                          ),
                        )
                      ]),
                      Row(children: [
                        const TextBold("User Confirm"),
                        const Spacer(),
                        Text(
                          order.userConfirm.name,
                          style: TextStyle(
                            color: order.userConfirm.color,
                            fontWeight: FontWeight.bold,
                          ),
                        )
                      ]),
                      Row(children: [
                        const TextBold("Discount"),
                        const Spacer(),
                        Text(order.discountCode ?? "NO")
                      ]),
                      Row(
                        children: [
                          const TextBold("Amount"),
                          const Spacer(),
                          Text(
                            currencyFormat(order.totalAmount),
                            style: const TextStyle(
                                color: kPrimaryColor,
                                fontWeight: FontWeight.bold),
                          )
                        ],
                      ),
                      Row(
                        children: [
                          const TextBold("Consignment"),
                          const Spacer(),
                          TextBold(order.consignment ? "YES" : "NO")
                        ],
                      ),
                    ],
                  ),
                ),
                Container(
                  margin: const EdgeInsets.symmetric(horizontal: 16),
                  child: Row(
                    children: [
                      // Visibility(
                      //   visible: order.orderDetails.any((element) =>
                      //       element.processReceiveProduct == "PENDING"),
                      //   child: ElevatedButton(
                      //     onPressed: () => confirmOrder(context, order),
                      //     style: ElevatedButton.styleFrom(
                      //         backgroundColor: kWinColor),
                      //     child: const Row(
                      //       children: [
                      //         Text("Confirm Order "),
                      //         FaIcon(
                      //           FontAwesomeIcons.solidCircleCheck,
                      //           size: 16,
                      //         )
                      //       ],
                      //     ),
                      //   ),
                      // ),
                      const Spacer(),
                      Visibility(
                        visible:
                            order.statusReceived == EReceivedStatus.RECEIVED &&
                                order.userConfirm == EUserConfirm.NOT_RECEIVED,
                        child: Align(
                          alignment: Alignment.centerRight,
                          child: ElevatedButton(
                            style: ElevatedButton.styleFrom(
                                foregroundColor: kWinColor),
                            onPressed: () =>
                                updateRecieveOrderCustomer(context, order),
                            child: const Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Text("Order Recieved "),
                                FaIcon(
                                  FontAwesomeIcons.circleCheck,
                                  size: 16,
                                )
                              ],
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                const Gap(50)
              ],
            ),
          ),
        ),
      ),
      bottomNavigationBar: Visibility(
        visible: !isAdminOrStaff && order.statusReceived.name == "UNVERIFIED",
        child: ElevatedButton(
          onPressed: () async {
            Navigator.pushNamed(
              context,
              OtpScreen.routeName,
              arguments: order.id,
            );
          },
          style:
              ElevatedButton.styleFrom(shape: const BeveledRectangleBorder()),
          child: const Text("Verify OTP"),
        ),
      ),
    );
  }

  void updateRecieveOrder(BuildContext context, Order order) {
    showDialog(
        context: context,
        builder: (context) {
          return AlertDialog(
            title: const Text("Recieve Order"),
            content: const Text(
                "Note: only update this status when customer recieved the products"),
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
                  Provider.of<OrderProvider>(context, listen: false)
                      .setRecievedOrder(context, order);
                },
                child: const Text("Confirm"),
              )
            ],
          );
        });
  }

  void updateRecieveOrderCustomer(BuildContext context, Order order) {
    showDialog(
        context: context,
        builder: (context) {
          return AlertDialog(
            title: const Text("Recieved Order"),
            content: const Text(
                "Note: only update this status when   recieved the products"),
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
                  Provider.of<OrderProvider>(context, listen: false)
                      .setRecievedOrder(context, order, forCustomer: true);
                },
                child: const Text("Confirm"),
              )
            ],
          );
        });
  }

  void confirmOrder(BuildContext context, Order order) {
    showDialog(
        context: context,
        builder: (context) {
          return AlertDialog(
            title: const Text("Confirm Order"),
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
                  Provider.of<OrderProvider>(context, listen: false)
                      .confirmOrder(context, order);
                },
                child: const Text("Confirm"),
              )
            ],
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
          data.toString().substring(0),
          style: style,
          maxLines: 2,
        )
      ]),
    );
  }
}
