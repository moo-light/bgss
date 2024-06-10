import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/order_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/transaction_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/withdraw_provider.dart';
import 'package:blockchain_mobile/1_controllers/repositories/transaction_repository.dart';
import 'package:blockchain_mobile/2_screens/transaction_detail/transaction_detail_screen.dart';
import 'package:blockchain_mobile/2_screens/withdraw_detail/withdraw_detail_screen.dart';
import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/3_components/failed_information.dart';
import 'package:blockchain_mobile/3_components/selection_components.dart';
import 'package:blockchain_mobile/3_components/text_bold.dart';
import 'package:blockchain_mobile/4_helper/decorations/text_styles.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/enums/e_received_status.dart';
import 'package:blockchain_mobile/models/enums/gold_unit.dart';
import 'package:blockchain_mobile/models/enums/payment_status.dart';
import 'package:blockchain_mobile/models/enums/transaction_signature.dart';
import 'package:blockchain_mobile/models/gold_transaction.dart';
import 'package:blockchain_mobile/models/order.dart';
import 'package:blockchain_mobile/models/payment_history.dart';
import 'package:blockchain_mobile/models/withdraw_gold.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:gap/gap.dart';
import 'package:provider/provider.dart';

class MyOrderListScreen extends StatefulWidget {
  static const routeName = '/order-list';
  const MyOrderListScreen({super.key});

  @override
  State<MyOrderListScreen> createState() => _MyOrderListScreenState();
}

class _MyOrderListScreenState extends State<MyOrderListScreen> {
  List<Map<String, dynamic>> selectionList = [
    {
      "action": (context) =>
          Provider.of<OrderProvider>(context, listen: false).getOrderList(),
      "label": "Orders",
    },
    {
      "action": (context) => {
            Provider.of<TransactionProvider>(context, listen: false)
                .getUserTransaction(),
          },
      "label": "Transactions",
    },
    {
      "action": (context) => {
            Provider.of<WithdrawProvider>(context, listen: false)
                .getUserWithdraw(),
          },
      "label": "Withdraws",
    },
    {
      "action": (context) => {
            Provider.of<AuthProvider>(context, listen: false)
                .getPaymentHistory(context),
          },
      "label": "Deposits",
    },
  ];
  int selection = 0;
  @override
  void initState() {
    super.initState();
    Future.microtask(() => {
          setState(() {
            selection =
                (ModalRoute.of(context)?.settings.arguments ?? 0) as int;
          })
        });
  }

  @override
  Widget build(BuildContext context) {
    // Getting the provider

    final orderProvider = Provider.of<OrderProvider>(context);
    final transactionProvider = Provider.of<TransactionProvider>(context);
    final withdrawProvider = Provider.of<WithdrawProvider>(context);
    final authProvider = Provider.of<AuthProvider>(context);
    // Otherwise, display the list of orders
    final orders = orderProvider.orders;
    final transactions =
        transactionProvider.getUserTransactionResult.result ?? [];
    final withdraws = withdrawProvider.getUserWithdrawResult.result ?? [];
    final payments = authProvider.paymentHistoryResult.result ?? [];

    return Scaffold(
      appBar: AppBar(
        title: const Text('Order List'),
      ),
      body: SafeArea(
        child: RefreshIndicator(
          onRefresh: () async {
            await selectionList[selection]["action"](context);
          },
          child: SingleChildScrollView(
            physics: const AlwaysScrollableScrollPhysics(),
            child: Column(
              mainAxisSize: MainAxisSize.max,
              children: [
                Container(
                  margin: const EdgeInsets.all(8),
                  child: SingleChildScrollView(
                    scrollDirection: Axis.horizontal,
                    child: Wrap(
                      spacing: 10,
                      crossAxisAlignment: WrapCrossAlignment.center,
                      children: [
                        ...List.generate(
                          selectionList.length,
                          (index) {
                            return SelectionComponents(
                              selected: index == selection,
                              onPressed: () {
                                setState(() {
                                  selection = index;
                                  selectionList[index]["action"](context);
                                });
                              },
                              label: selectionList[index]["label"],
                            );
                          },
                        ),
                      ],
                      // Flatten the List<Widget> to avoid nesting Rows unnecessarily
                    ),
                  ),
                ),
                Visibility(
                  visible: selection == 0,
                  child: _buildOrders(orders, context, orderProvider),
                ),
                Visibility(
                  visible: selection == 1,
                  child: _buildTransactions(
                      transactions, context, transactionProvider),
                ),
                Visibility(
                  visible: selection == 2,
                  child: _buildWithdraws(withdraws, context, withdrawProvider),
                ),
                Visibility(
                  // Check if the current selection is for payment history
                  visible: selection == 3,
                  child: _buildPaymentHistory(payments, context, authProvider),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  var loader = Container(
    constraints: const BoxConstraints(minHeight: 500),
    child: const Center(
      child: CircularProgressIndicator(),
    ),
  );
  Widget _buildOrders(
      List<Order> orders, BuildContext context, OrderProvider provider) {
    if (provider.getListLoading == true) {
      return loader;
    }
    if (provider.getListError.isNotEmpty) {
      return FailedInformation(child: Text(provider.getListError));
    }
    return ListView.builder(
      physics: const NeverScrollableScrollPhysics(),
      shrinkWrap: true,
      itemCount: orders.length,
      itemBuilder: (ctx, i) => Container(
        decoration: const BoxDecoration(
          border: Border(
            top: BorderSide(color: Colors.black),
          ),
        ),
        child: ListTile(
          leading: const Icon(Icons.list),
          title: Text('Order #${orders[i].id}'),
          subtitle:
              Text('Total: \$${orders[i].totalAmount.toStringAsFixed(2)}'),
          trailing: Text(
            orders[i].statusReceived.name,
            style: TextStyle(
              color: orders[i].statusReceived.color,
              fontSize: 12,
              fontWeight: FontWeight.bold,
            ),
          ),
          onTap: () {
            // Perform an action when tapped on an order
            Provider.of<OrderProvider>(context, listen: false)
                .getDetailOrder(context, orderId: orders[i].id);
          },
        ),
      ),
    );
  }

  Widget _buildTransactions(List<GoldTransaction> transactions,
      BuildContext context, TransactionProvider provider) {
    if (provider.getUserTransactionResult.isLoading == true) {
      return loader;
    }
    if (provider.getUserTransactionResult.isError == true) {
      return FailedInformation(
          child: Text(provider.getUserTransactionResult.error));
    }
    return ListView.builder(
      physics: const NeverScrollableScrollPhysics(),
      shrinkWrap: true,
      itemCount: transactions.length,
      itemBuilder: (ctx, i) => Container(
        decoration: const BoxDecoration(
          border: Border(
            top: BorderSide(color: Colors.black),
          ),
        ),
        child: ListTile(
          leading: Column(
            children: [
              Visibility(
                visible: transactions[i].transactionType == TransactionType.BUY,
                replacement:
                    const FaIcon(FontAwesomeIcons.minus, color: kLossColor),
                child: const FaIcon(FontAwesomeIcons.plus, color: kWinColor),
              ),
              TextBold(transactions[i].transactionType.name)
            ],
          ),
          title: Text('Transaction #${transactions[i].id}'),
          subtitle: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text.rich(
                TextSpan(
                  children: <TextSpan>[
                    const TextSpan(
                        text: 'Price: ',
                        style: TextStyle(fontWeight: FontWeight.bold)),
                    TextSpan(
                        text: currencyFormat(transactions[i].pricePerOunce)),
                    const TextSpan(
                        text: '\nUnit: ',
                        style: TextStyle(fontWeight: FontWeight.bold)),
                    TextSpan(
                        text:
                            '${transactions[i].quantity} ${transactions[i].getGoldUnit().symbol}'),
                    const TextSpan(
                        text: '\nParty: ',
                        style: TextStyle(fontWeight: FontWeight.bold)),
                    TextSpan(text: transactions[i].confirmingParty),
                    const TextSpan(
                        text: '\nCreated: ',
                        style: TextStyle(fontWeight: FontWeight.bold)),
                    TextSpan(text: dateFormat(transactions[i].createdAt)),
                  ],
                ),
              ),
              Visibility(
                visible: transactions[i].contract?.contractStatus ==
                    "DIGITAL_SIGNED",
                child: const Row(
                  children: [
                    Text("Contract Signed"),
                    Gap(4),
                    FaIcon(
                      FontAwesomeIcons.check,
                      size: 15,
                    )
                  ],
                ),
              )
            ],
          ),
          trailing: Container(
            constraints: const BoxConstraints(maxWidth: 100),
            child: Wrap(alignment: WrapAlignment.end, children: [
              Text(
                transactions[i].transactionSignature.name,
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                  color: transactions[i].transactionSignature.color,
                ),
              ),
              const Divider(indent: 10),
              Text(
                currencyFormat(transactions[i].totalCostOrProfit),
                style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                    color: transactions[i].transactionType.color),
              ),
            ]),
          ),
          onTap: () {
            provider.currentTransaction.reset(
              result: transactions[i],
              isSuccess: true,
              message: "",
            );
            Navigator.of(context).pushNamed(TransactionDetailScreen.routeName);
          },
        ),
      ),
    );
  }

  Widget _buildWithdraws(List<WithdrawGold> withdraws, BuildContext context,
      WithdrawProvider provider) {
    if (provider.getUserWithdrawResult.isLoading == true) {
      return loader;
    }
    if (provider.getUserWithdrawResult.isError) {
      return FailedInformation(
          child: Text(provider.getUserWithdrawResult.error));
    }
    return ListView.builder(
      physics: const NeverScrollableScrollPhysics(),
      shrinkWrap: true,
      itemCount: withdraws.length,
      itemBuilder: (ctx, i) => Container(
        decoration: const BoxDecoration(
          border: Border(
            top: BorderSide(color: Colors.black),
          ),
        ),
        child: ListTile(
          leading: const FaIcon(FontAwesomeIcons.arrowDownLong),
          title: Text('Withdraw #${withdraws[i].id}'),
          subtitle: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text.rich(
                TextSpan(
                  children: <TextSpan>[
                    const TextSpan(
                        text: 'Quantity: ',
                        style: TextStyle(fontWeight: FontWeight.bold)),
                    TextSpan(
                        text:
                            "${numberFormat(withdraws[i].amount)} ${withdraws[i].getGoldUnit().symbol}"),
                    const TextSpan(
                        text: '\nGold Unit: ',
                        style: TextStyle(fontWeight: FontWeight.bold)),
                    TextSpan(text: withdraws[i].getGoldUnit().name),
                    TextSpan(
                      children: [
                        const TextSpan(
                          text: "\nRequest Type: ",
                          style: TextStyle(fontWeight: FontWeight.bold),
                        ),
                        TextSpan(
                            text: withdraws[i].product == null
                                ? "Available"
                                : "Craft")
                      ],
                    ),
                  ],
                ),
              ),
              Visibility(
                  visible: withdraws[i].product != null,
                  child: const Text.rich(
                    TextSpan(children: [
                      TextSpan(text: "Recieved "),
                      WidgetSpan(
                          child: FaIconGen(
                        FontAwesomeIcons.check,
                        width: 16,
                        height: 16,
                      ))
                    ]),
                  ))
            ],
          ),
          trailing: Text(
            withdraws[i].status,
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
              color: withdraws[i].getStatusColor(),
            ),
          ),
          onTap: () {
            provider.setCurrentWithdraw(withdraws[i]);
            Navigator.of(context).pushNamed(WithdrawDetailScreen.routeName);
          },
        ),
      ),
    );
  }

  Widget _buildPaymentHistory(List<PaymentHistory> payments,
      BuildContext context, AuthProvider provider) {
    if (provider.paymentHistoryResult.isLoading) {
      return loader;
    }

    if (provider.paymentHistoryResult.isError) {
      return FailedInformation(
        child: Text(provider.paymentHistoryResult.error),
      );
    }
    if (provider.paymentHistoryResult.result?.isEmpty ?? false) {
      return FailedInformation(
        child: Text(provider.paymentHistoryResult.message),
      );
    }
    return ListView.builder(
      physics: const NeverScrollableScrollPhysics(),
      shrinkWrap: true,
      itemCount: payments.length,
      itemBuilder: (ctx, i) => Container(
        decoration: const BoxDecoration(
          border: Border(
            top: BorderSide(color: Colors.black),
          ),
        ),
        child: ListTile(
          leading: const FaIcon(FontAwesomeIcons.dollarSign),
          title: Text('Payment #${payments[i].id}'),
          subtitle: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text.rich(
                TextSpan(
                  children: <TextSpan>[
                    // const TextSpan(
                    //   text: 'Amount: ',
                    //   style: TextStyle(fontWeight: FontWeight.bold),
                    // ),
                    const TextSpan(
                      text: '\nBank Code: ',
                      style: TextStyle(fontWeight: FontWeight.bold),
                    ),
                    TextSpan(text: payments[i].bankCode, style: boldTextStyle),
                    const TextSpan(
                      text: '\nPayment Status: ',
                      style: TextStyle(fontWeight: FontWeight.bold),
                    ),
                    TextSpan(
                        text: payments[i].paymentStatus.name,
                        style: TextStyle(
                            color: payments[i].paymentStatus.color,
                            fontWeight: FontWeight.bold)),
                    const TextSpan(
                      text: '\nPay Date: ',
                      style: TextStyle(fontWeight: FontWeight.bold),
                    ),
                    TextSpan(
                      text: dateFormat(payments[i].payDate),
                    ),
                    const TextSpan(
                      text: '\n',
                      style: TextStyle(fontWeight: FontWeight.bold),
                    ),
                    TextSpan(
                        text: payments[i].reason,
                        style: TextStyle(
                          color: payments[i].paymentStatus.color,
                        )),
                  ],
                ),
              )
            ],
          ),
          dense: true,
          trailing: Text(
            payments[i].amount.toStringAsFixed(2),
            style: const TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
            ),
          ),
          // onTap: () {
          //   // Handle onTap action
          // },
        ),
      ),
    );
  }

  Color pickstatusReceivedColor(List<Order> orders, int i) {
    switch (orders[i].statusReceived) {
      case 'NOT_RECEIVED':
        return kSecondaryColor;
      case 'RECEIVED':
        return Colors.green;
      case 'CANCELLED':
        return Colors.red;
      default:
        return Colors.black;
    }
  }
}
