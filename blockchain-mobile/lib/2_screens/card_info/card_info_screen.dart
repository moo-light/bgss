import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/2_screens/card_info/card_info_edit_screen.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:provider/provider.dart';

import 'components/card_images.dart';

class CardInfoScreen extends StatelessWidget {
  static const String routeName = "/profile/me/card-info/";
  const CardInfoScreen({super.key});

  @override
  Widget build(BuildContext context) {
    var currentUser = context.watch<AuthProvider>().currentUser!;
    return Scaffold(
      appBar: AppBar(
        title: const Text("Card Information"),
      ),
      body: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          Visibility(
            visible: currentUser.userInfo.ciCardImage.isNotEmpty,
            replacement: Padding(
              padding: const EdgeInsets.only(top: 50.0),
              child: Center(
                child: Text(
                  "Your card informations are empty",
                  style: Theme.of(context).textTheme.titleLarge,
                  textAlign: TextAlign.center,
                ),
              ),
            ),
            child: Builder(builder: (context) {
              if (currentUser.userInfo.ciCardImage.isEmpty) return Container();
              return CardImages(currentUser.userInfo.ciCardImage);
            }),
          ),
          Padding(
            padding: const EdgeInsets.symmetric(vertical: 50.0),
            child: Center(
              child: ElevatedButton.icon(
                onPressed: () {
                  Navigator.pushNamed(context, CardInfoEditScreen.routeName);
                },
                label: Text(
                  currentUser.userInfo.ciCardImage.isNotEmpty
                      ? "Update Card Informations"
                      : "Verify Card Informations",
                ),
                style: ElevatedButton.styleFrom(),
                icon: const FaIcon(FontAwesomeIcons.solidIdCard),
              ),
            ),
          )
        ],
      ),
    );
  }
}
