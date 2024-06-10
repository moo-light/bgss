import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/4_helper/decorations/card_decoration.dart';
import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/user.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class CheckoutBuyerInformation extends StatefulWidget {
  const CheckoutBuyerInformation({super.key, required this.formKey});
  final GlobalKey<FormState> formKey;

  @override
  State<CheckoutBuyerInformation> createState() =>
      _CheckoutBuyerInformationState();
}

class _CheckoutBuyerInformationState extends State<CheckoutBuyerInformation> {
  late final _formKey = widget.formKey;

  final username = TextEditingController();
  final email = TextEditingController();
  final phone = TextEditingController();
  final address = TextEditingController();
  @override
  void initState() {
    context.read<AuthProvider>().getCurrentUser();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    User? currentUser = context.watch<AuthProvider>().currentUser;
    username.text = currentUser?.displayName ?? "";
    email.text = currentUser?.email ?? "";
    phone.text = currentUser?.userInfo.phoneNumber ?? "";
    address.text = currentUser?.userInfo.address ?? "";
    return Container(
      decoration: BoxDecoration(
        gradient: LinearGradient(
          begin: Alignment.topLeft,
          end: Alignment.centerRight,
          colors: [
            kBackgroundColor.withHSLlighting(93),
            kBackgroundColor,
            kBackgroundColor,
            kBackgroundColor.withHSLlighting(93),
          ],
        ),
      ),
      child: Container(
        padding: const EdgeInsets.all(24.0),
        margin: const EdgeInsets.all(16),
        decoration: cardDecoration,
        child: ListView(
          physics:
              const NeverScrollableScrollPhysics(), // Prevents the SingleChildScrollView from being scrollable
          shrinkWrap:
              true, // Ensures that the ListView only occupies the space it needs
          children: [
            Text("User Information",
                style: Theme.of(context).textTheme.titleLarge),
            ListTile(
              title: const Text("User Name"),
              subtitle: Text(username.text), // Assuming "username" is a TextEditingController
              dense: true, // Reduces the height of the ListTile
            ),
            ListTile(
              title: const Text("Email"),
              subtitle: Text(email.text),
              dense: true,
            ),
            ListTile(
              title: const Text("Phone Number"),
              subtitle: Text(phone.text),
              dense: true,
            ),
            ListTile(
              title: const Text("Address"),
              subtitle: Text(address.text),
              dense: true,
            ),
          ],
        ),
      ),
    );
  }
}
