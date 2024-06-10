import 'package:blockchain_mobile/1_controllers/repositories/transaction_repository.dart';
import 'package:blockchain_mobile/4_helper/decorations/card_decoration.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/enums/gold_unit.dart';
import 'package:blockchain_mobile/models/gold_transaction.dart';
import 'package:flutter/material.dart';

class TradeInfomation extends StatelessWidget {
  const TradeInfomation({super.key, required this.transaction});

  final GoldTransaction transaction;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(24.0),
      margin: const EdgeInsets.all(16),
      decoration: cardDecoration,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text("Transaction #${transaction.id}",
              style: Theme.of(context).textTheme.titleLarge),
          const SizedBox(height: 8), // Space between text widgets
          Text.rich(
            TextSpan(
              text: 'Created: ',
              style: const TextStyle(fontWeight: FontWeight.bold),
              children: [
                TextSpan(
                  text: dateFormat(transaction.createdAt),
                  style: const TextStyle(fontWeight: FontWeight.normal),
                ),
              ],
            ),
          ),
          const SizedBox(height: 8), // Space between text widgets
          Text.rich(
            TextSpan(
              text: 'Gold Unit: ',
              style: const TextStyle(fontWeight: FontWeight.bold),
              children: [
                TextSpan(
                  text: transaction.goldUnit.name,
                  style: const TextStyle(fontWeight: FontWeight.normal),
                ),
              ],
            ),
          ),
          const SizedBox(height: 8,), // Adjust spacing as needed
          Text.rich(
            TextSpan(
              text: 'Trade Type: ',
              style: const TextStyle(fontWeight: FontWeight.bold),
              children: [
                TextSpan(
                  text: transaction.transactionType.name,
                  style: TextStyle(
                    fontWeight: FontWeight.normal,
                    color: transaction.transactionType == TransactionType.BUY ? kWinColor: kLossColor, // Adjust condition as needed
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(height: 8), // Adjust spacing as needed
          Text.rich(
            TextSpan(
              text: 'Amount: ',
              style: const TextStyle(fontWeight: FontWeight.bold),
              children: [
                TextSpan(
                  text:
                      '${transaction.quantity} ${transaction.getGoldUnit().symbol}',
                  style: const TextStyle(fontWeight: FontWeight.normal),
                ),
              ],
            ),
          ),
          const SizedBox(height: 8), // Adjust spacing as needed
          Text.rich(
            TextSpan(
              text: 'Price: ',
              style: const TextStyle(fontWeight: FontWeight.bold),
              children: [
                TextSpan(
                  text: currencyFormat(transaction.pricePerOunce),
                  style: const TextStyle(fontWeight: FontWeight.normal),
                ),
              ],
            ),
          ),
          const SizedBox(height: 8), // Adjust spacing as needed
          Text.rich(
            TextSpan(
              text: 'Total: ',
              style: const TextStyle(fontWeight: FontWeight.bold),
              children: [
                TextSpan(
                  text: currencyFormat(transaction.totalCostOrProfit),
                  style: TextStyle(
                    fontWeight: FontWeight.bold,
                    color: transaction.transactionType.name == "BUY"
                        ? kLossColor
                        : kWinColor,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
