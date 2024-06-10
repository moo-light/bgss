import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';

class CheckoutOption extends StatefulWidget {
  const CheckoutOption({super.key});

  @override
  _CheckoutOptionState createState() => _CheckoutOptionState();
}
class _PaymentType {
  final String title;
  final String assetImg;

  _PaymentType({required this.title, required this.assetImg});
}

class _CheckoutOptionState extends State<CheckoutOption> {
  late String? selectedPaymentMethod = paymentMethods.first.title;
  final TextEditingController discountController = TextEditingController();

  final List<_PaymentType> paymentMethods = [
    _PaymentType(title: 'Vnpay', assetImg: "assets/images/vnpay.png"),
    _PaymentType(title: 'Debit Card', assetImg: "assets/images/visa.png"),
    _PaymentType(title: 'PayPal', assetImg: "assets/images/paypal.png"),
    _PaymentType(title: 'Apple Pay', assetImg: "assets/images/apple-pay.png"),
    _PaymentType(title: 'Your Balance', assetImg: "assets/images/dollar.png")
  ];

  @override
  void dispose() {
    discountController.dispose();
    super.dispose();
  }

 

  @override
  Widget build(BuildContext context) {
    return const Form(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          // Padding(
          //   padding: const EdgeInsets.all(8.0),
          //   child: Text(
          //     "Payment",
          //     style: Theme.of(context).textTheme.titleLarge,
          //   ),
          // ),
          // // Payment Method Selection
          // // ...paymentMethods.map<Widget>((method) {
          // //   return _buildRadioListTile(method);
          // // }),
          // const SizedBox(height: 20), // for spacing
        ],
      ),
    );
  }
   Widget _buildRadioListTile(_PaymentType paymentType) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        border: Border.all(width: 1, color: Colors.grey),
      ),
      child: ListTile(
        // Placeholder for product image
        title: Text(paymentType.title),
        trailing: SizedBox(
          width: 50,
          child: Image.asset(paymentType.assetImg),
        ),
        leading: Radio<String>(
          activeColor: kPrimaryColor,
          value: paymentType.title,
          groupValue: selectedPaymentMethod,
          onChanged: (String? value) {
            setState(() {
              selectedPaymentMethod = value;
            });
          },
        ),
      ),
    );
  }
}