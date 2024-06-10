import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/2_screens/profile/components/profile_profile_pic.dart';
import 'package:blockchain_mobile/2_screens/profile/dialogs/dialogs.dart';
import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/4_helper/decorations/text_styles.dart';
import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/user.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:gap/gap.dart';
import 'package:provider/provider.dart';
import 'package:blockchain_mobile/2_screens/card_info/card_info_edit_screen.dart';

class ProfileUserInfo extends StatefulWidget {
  final User currentUser;

  final bool isAdminOrStaff;

  const ProfileUserInfo(
      {super.key, required this.currentUser, required this.isAdminOrStaff});

  @override
  State<ProfileUserInfo> createState() => _ProfileUserInfoState();
}

class _ProfileUserInfoState extends State<ProfileUserInfo> {
  var screenViewIndex = 0;

  @override
  Widget build(BuildContext context) {
    User currentUser = widget.currentUser;
    var maxIndex = 2;
    if (context.watch<AuthProvider>().isAdminOrStaff) {
      maxIndex = 1;
    }
    return Stack(
      children: [
        Center(
          child: IndexedStack(
            index: screenViewIndex,
            children: [
              Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Center(
                    child: MyAccountProfilePic(
                        imgUrl: currentUser.userInfo.avatarData?.imgUrl),
                  ),
                  const SizedBox(
                    height: 20,
                  ),
                  SizedBox(
                    width: 330,
                    child: Text(
                      currentUser.displayName,
                      textAlign: TextAlign.center,
                      style: Theme.of(context)
                          .textTheme
                          .headlineLarge!
                          .copyWith(
                              fontWeight: FontWeight.bold,
                              fontSize: 24,
                              color: Colors.white),
                    ),
                  ),
                  const SizedBox(
                    height: 10,
                  ),
                  SizedBox(
                    width: 300,
                    child: Text(
                      currentUser.email,
                      textAlign: TextAlign.center,
                      style:
                          Theme.of(context).textTheme.headlineSmall!.copyWith(
                                fontWeight: FontWeight.bold,
                                fontSize: 20,
                                color: kHintTextColor.withHSLlighting(80),
                              ),
                    ),
                  ),
                  const SizedBox(
                    height: 24,
                  ),
                  Visibility(
                    visible: !widget.isAdminOrStaff,
                    child: Container(
                      padding: const EdgeInsets.all(16),
                      margin: const EdgeInsets.all(8),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(16),
                      ),
                      child: SingleChildScrollView(
                        scrollDirection: Axis.horizontal,
                        child: Row(
                          children: [
                            Container(
                              decoration: BoxDecoration(
                                borderRadius:
                                    const BorderRadius.all(Radius.circular(16)),
                                border: Border.all(width: 1),
                              ),
                              padding: const EdgeInsets.all(8),
                              child: Text.rich(
                                TextSpan(text: "Balance: ", children: [
                                  TextSpan(
                                      text: currencyFormat(
                                        currentUser.userInfo.balance.amount,
                                      ),
                                      style: const TextStyle(
                                          color: kSecondaryColor))
                                ]),
                                style: Theme.of(context)
                                    .textTheme
                                    .titleMedium!
                                    .copyWith(fontWeight: FontWeight.bold),
                              ),
                            ),
                            const Gap(5),
                            //Balance Button
                            IconButton(
                              color: kBackgroundColor,
                              style: IconButton.styleFrom(
                                  backgroundColor: kSecondaryColor),
                              onPressed: () async {
                                bool? result =
                                    await ProfileScreenDialog.balanceDialog(
                                  context,
                                  currentUser.userInfo.balance,
                                );
                                if (!context.mounted) return;
                                if (result == true) {
                                  Future.delayed(
                                      Durations.extralong1,
                                      context
                                          .read<AuthProvider>()
                                          .getCurrentUser);
                                  ToastService.toastSuccess(
                                      context, "Transaction Success!");
                                } else if (result == false) {
                                  ToastService.toastError(
                                      context, "Transaction Failed!");
                                }
                              },
                              icon: const Icon(Icons.add, color: Colors.white),
                            ),
                            const Gap(5),
                            Container(
                              decoration: BoxDecoration(
                                borderRadius:
                                    const BorderRadius.all(Radius.circular(16)),
                                border: Border.all(width: 1),
                              ),
                              padding: const EdgeInsets.all(8),
                              child: Text.rich(
                                TextSpan(text: "Inventory: ", children: [
                                  TextSpan(
                                      text: "${numberFormat(
                                        currentUser
                                            .userInfo.inventory.totalWeightOz,
                                      )}tOz",
                                      style:
                                          const TextStyle(color: kPrimaryColor))
                                ]),
                                style: Theme.of(context)
                                    .textTheme
                                    .titleMedium!
                                    .copyWith(fontWeight: FontWeight.bold),
                              ),
                            ),
                            const Gap(5),
                            //Inventory Button
                            IconButton(
                              color: kBackgroundColor,
                              style: IconButton.styleFrom(
                                  backgroundColor: kPrimaryColor),
                              onPressed: () {
                                ProfileScreenDialog.inventoryDialog(
                                    context, currentUser.userInfo.inventory);
                              },
                              icon: const Icon(Icons.arrow_downward,
                                  color: Colors.white),
                            ),
                          ],
                        ),
                      ),
                    ),
                  )
                  // Note: dont use Container here to link with the bottom widget
                  // it gonna be ugly
                  // const TopRoundedContainer(
                  //   color: kBackgroundColor,
                  //   child: SizedBox(),
                  // ),
                ],
              ),
              Container(
                margin: const EdgeInsets.all(16),
                decoration: const BoxDecoration(
                  color: kBackgroundColor,
                  borderRadius: BorderRadius.all(Radius.circular(48)),
                ),
                width: double.infinity,
                alignment: Alignment.center,
                child: Column(
                  children: [
                    const Gap(8),
                    Text(
                      "User Info",
                      style: Theme.of(context)
                          .textTheme
                          .headlineSmall!
                          .copyWith(fontWeight: FontWeight.bold),
                    ),
                    _buildUserInfoListTile(
                      "Fullname",
                      currentUser.displayName,
                    ),
                    _buildUserInfoListTile(
                      "Phone Number",
                      currentUser.userInfo.phoneNumber,
                    ),
                    _buildUserInfoListTile(
                      "Address",
                      currentUser.userInfo.address ?? "",
                    ),
                    _buildUserInfoListTile(
                      "Date of birth",
                      currentUser.userInfo.doB ?? "",
                    ),
                    const Gap(24),
                  ],
                ),
              ),
              const UserSecretKey(),
            ]
                .indexed
                .map((e) => GestureDetector(
                      onHorizontalDragEnd: (DragEndDetails details) {
                        if (details.primaryVelocity! > 0) {
                          // Swiped from left to right (right swipe)
                          print('Swiped right');
                          screenViewIndex--;
                        } else if (details.primaryVelocity! < 0) {
                          // Swiped from right to left (left swipe)
                          print('Swiped left');
                          screenViewIndex++;
                        }
                        if (screenViewIndex > maxIndex) {
                          screenViewIndex = maxIndex;
                        }
                        if (screenViewIndex < 0) screenViewIndex = 0;
                        setState(() {});
                      },
                      child: e.$2,
                    ))
                .toList(),
          ),
        ),
        Positioned(
          right: 12,
          child: Row(
            children: [
              IconButton.filled(
                onPressed: screenViewIndex != 0
                    ? () {
                        setState(() {
                          if (screenViewIndex == 0) return;
                          screenViewIndex -= 1;
                        });
                      }
                    : null,
                color: Colors.grey,
                icon: FaIconGen(
                  FontAwesomeIcons.caretLeft,
                  color: white,
                ),
              ),
              IconButton.filled(
                onPressed: screenViewIndex != maxIndex
                    ? () {
                        setState(() {
                          if (screenViewIndex == maxIndex) return;
                          screenViewIndex += 1;
                        });
                      }
                    : null,
                color: Colors.grey,
                icon: FaIconGen(
                  FontAwesomeIcons.caretRight,
                  color: white,
                ),
              ),
            ],
          ),
        )
      ],
    );
  }

  Widget _buildUserInfoListTile(String leadingText, String titleText) {
    return DefaultTextStyle(
      style: const TextStyle(fontSize: 20),
      child: ListTile(
        leading: Text(
          leadingText,
          style: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
          ),
        ),
        title: Text(
          titleText,
        ),
      ),
    );
  }
}

class UserSecretKey extends StatefulWidget {
  const UserSecretKey({
    super.key,
  });

  @override
  State<UserSecretKey> createState() => _UserSecretKeyState();
}

class _UserSecretKeyState extends State<UserSecretKey> {
  @override
  void initState() {
    super.initState();
  }

  bool ellipsis = true;
  @override
  Widget build(BuildContext context) {
    var watch = context.watch<AuthProvider>();
    var publicKey = watch.publicKey;
    var privateKey = watch.privateKey;
    var currentUser = watch.currentUser;
    return Center(
      child: Container(
        margin: const EdgeInsets.all(16),
        decoration: const BoxDecoration(
          color: kBackgroundColor,
          borderRadius: BorderRadius.all(Radius.circular(48)),
        ),
        width: double.infinity,
        alignment: Alignment.center,
        child: Column(
          children: [
            const Gap(8),
            Text(
              "User Secret key",
              style: Theme.of(context)
                  .textTheme
                  .headlineSmall!
                  .copyWith(fontWeight: FontWeight.bold),
            ),
            ListTile(
              title: Text.rich(
                  style: boldTextStyle,
                  TextSpan(text: "Public Key", children: [
                    WidgetSpan(
                        child: Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 8),
                      child: Visibility(
                        visible: watch.currentSecretKey.isLoading,
                        replacement: Visibility(
                          visible: publicKey.isNotEmpty,
                          child: const FaIconGen(
                            FontAwesomeIcons.copy,
                            width: 18,
                          ),
                        ),
                        child: const SizedBox(
                          width: 18,
                          height: 18,
                          child: CircularProgressIndicator.adaptive(
                            strokeWidth: 3,
                          ),
                        ),
                      ),
                    ))
                  ])),
              subtitle: Visibility(
                visible: !watch.currentSecretKey.isLoading,
                replacement: const Text("Loading..."),
                child: Visibility(
                  visible:
                      (currentUser?.userInfo.ciCardImage.isNotEmpty ?? false) ||
                          publicKey.isNotEmpty,
                  replacement: const Text(
                    "please verify card information before recieving a secret key",
                    overflow: null,
                  ),
                  child: Text(
                    publicKey.toString(),
                    overflow: ellipsis ? TextOverflow.ellipsis : null,
                  ),
                ),
              ),
              trailing: FittedBox(
                alignment: Alignment.topRight,
                child: IconButton(
                  icon: FaIconGen(
                    FontAwesomeIcons.eye,
                    width: 18,
                    color: !ellipsis ? kPrimaryColor : null,
                  ),
                  onPressed: () {
                    setState(() {
                      ellipsis = !ellipsis;
                    });
                  },
                ),
              ),
              onTap: () {
                Clipboard.setData(ClipboardData(text: publicKey));
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(
                    content: Text(
                      'Copied to clipboard!',
                    ),
                  ),
                );
              },
            ),
            ListTile(
              title: Text.rich(
                  style: boldTextStyle,
                  TextSpan(text: "Private Key", children: [
                    WidgetSpan(
                        child: Visibility(
                      visible: watch.currentSecretKey.isLoading,
                      child: const Padding(
                        padding: EdgeInsets.symmetric(horizontal: 8),
                        child: SizedBox(
                          width: 18,
                          height: 18,
                          child: CircularProgressIndicator.adaptive(
                            strokeWidth: 3,
                          ),
                        ),
                      ),
                    ))
                  ])),
              subtitle: Visibility(
                visible: !watch.currentSecretKey.isLoading,
                replacement: const Text("Loading..."),
                child: Visibility(
                  visible:
                      (currentUser?.userInfo.ciCardImage.isNotEmpty ?? false) ||
                          publicKey.isNotEmpty,
                  replacement: const Text(
                    "please verify card information before recieving a secret key",
                    overflow: null,
                  ),
                  child: Text(
                    privateKey.toString(),
                    overflow: TextOverflow.ellipsis,
                  ),
                ),
              ),
              trailing: FittedBox(
                child: Align(
                  alignment: Alignment.topRight,
                  child: IconButton(
                    icon: const FaIconGen(
                      FontAwesomeIcons.arrowsRotate,
                      width: 18,
                    ),
                    onPressed: () async {
                      bool result = await context
                          .read<AuthProvider>()
                          .regenerateSecretKey(context);
                      if (result && context.mounted) {
                        context.read<AuthProvider>().getSecretKey();
                      }
                    },
                  ),
                ),
              ),
            ),
            ListTile(
              title: Text(
                "Update Card Information",
                style: boldTextStyle.copyWith(
                    color: kPrimaryColor,
                    decoration: TextDecoration.underline,
                    decorationColor: kPrimaryColor),
              ),
              onTap: () {
                Navigator.pushNamed(context, CardInfoEditScreen.routeName);
              },
            ),
            const Gap(24),
          ],
        ),
      ),
    );
  }
}
