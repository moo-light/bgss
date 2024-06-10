import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/discount_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/order_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/withdraw_provider.dart';
import 'package:blockchain_mobile/1_controllers/services/auth_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/2_screens/init_screen.dart';
import 'package:blockchain_mobile/2_screens/my_account/my_account_screen.dart';
import 'package:blockchain_mobile/2_screens/my_order_list/my_order_list_screen.dart';
import 'package:blockchain_mobile/2_screens/profile/components/profile_user_info.dart';
import 'package:blockchain_mobile/2_screens/sign_in/sign_in_screen.dart';
import 'package:blockchain_mobile/2_screens/withdraw_detail/withdraw_detail_screen.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading.dart';
import 'package:blockchain_mobile/3_components/qr_scanner.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/user.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:gap/gap.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:provider/provider.dart';

import '../../3_components/failed_information.dart';
import '../find_order/find_order_screen.dart';
import '../my_discount/my_discount_screen.dart';
import 'components/profile_menu.dart';

class ProfileScreen extends StatelessWidget {
  static String routeName = "/profile";
  const ProfileScreen({super.key});
  @override
  Widget build(BuildContext context) {
    User? currentUser = context.watch<AuthProvider>().currentUser;
    bool? logined = context.watch<AuthProvider>().userLogined;
    return Scaffold(
      appBar: AppBar(
        title: const Text("Profile"),
      ),
      body: RefreshIndicator(
        onRefresh: () async {
          context.read<AuthProvider>().getCurrentUser();
        },
        child: currentUser != null || context.watch<AuthProvider>().isLoading
            ? IsLoadingWG(
                isLoading: context.watch<AuthProvider>().isLoading,
                child: _buildMainScreen(context, currentUser!))
            : FailedInformation(
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(
                      logined == true
                          ? "Something went wrong! "
                          : "have an account? ",
                      style: const TextStyle(fontSize: 16),
                    ),
                    GestureDetector(
                      onTap: () {
                        Navigator.pushNamed(context, SignInScreen.routeName);
                        if (logined == true) {
                          Provider.of<AuthProvider>(context, listen: false)
                              .userSignOut(context);
                        }
                      },
                      child: Text(
                        logined == true ? "Sign out" : "Sign In",
                        style:
                            const TextStyle(fontSize: 16, color: kPrimaryColor),
                      ),
                    ),
                  ],
                ),
              ),
      ),
    );
  }

  Widget _buildMainScreen(BuildContext context, User currentUser) {
    String? roles = context.watch<AuthProvider>().roles;
    // RegexTest: ROLE_STAFF, ROLE_STAFF
    bool isAdminOrStaff =
        roles?.contains(RegExp('ROLE_STAFF|ROLE_ADMIN')) ?? false;
    return SingleChildScrollView(
      child: Column(
        children: <Widget>[
          Container(
            decoration: const BoxDecoration(
              image: DecorationImage(
                alignment: Alignment.center,
                fit: BoxFit.cover,
                colorFilter: ColorFilter.mode(
                    Color.fromARGB(221, 60, 60, 60), BlendMode.darken),
                image: AssetImage("assets/images/dubai-the-city-of-gold.jpg"),
              ),
            ),
            padding: const EdgeInsets.only(top: 48),
            child: Center(
              child: ProfileUserInfo(
                currentUser: currentUser,
                isAdminOrStaff: isAdminOrStaff,
              ),
            ),
          ),
          const Gap(24),
          ProfileMenu(
            text: "My Account",
            icon: "assets/icons/User Icon.svg",
            press: () async {
              await Navigator.pushNamed(context, MyAccountScreen.routeName);
              if (context.mounted) {
                context.read<AuthProvider>().getCurrentUser();
              }
            },
          ),
          Visibility(
            visible: kDebugMode,
            child: ProfileMenu(
                text: "Widget QR check",
                icon: "assets/icons/Chat bubble Icon.svg",
                press: () async {
                  // showDialog(
                  //   context: context,
                  //   builder: (context) => LoadingFloatingDialog(
                  //     text: "saving your liverate",
                  //     onPluse: (timer) {
                  //       print(timer.tick);
                  //     },
                  //   ),
                  // ),
                  String value = await QrScanner.scan(context);
                  if (value.isNotEmpty) {
                    ToastService.toastSuccess(context, value);
                  }
                } // order Id
                ),
          ),
          Visibility(
              visible: kDebugMode,
              child: ProfileMenu(
                text: "API check",
                icon: "assets/icons/Chat bubble Icon.svg",
                press: () async {
                  context.read<WithdrawProvider>().getWithdraw(82).then(
                        (value) => Navigator.pushNamed(
                            context, WithdrawDetailScreen.routeName),
                      );
                },
              )),
          Visibility(
            visible: isAdminOrStaff,
            child: ProfileMenu(
              text: "Scan Orders",
              icon: "assets/icons/qr_code.svg",
              press: () => _scanOrder(context),
            ),
          ),
          // Visibility(
          //   visible: isAdminOrStaff,
          //   child: ProfileMenu(
          //     text: "Complete Withdraw",
          //     icon: "assets/icons/qr_code.svg",
          //     press: () => _scanWithdraw(context),
          //   ),
          // ),
          Visibility(
            visible: !isAdminOrStaff,
            replacement: ProfileMenu(
              text: "Find Order",
              icon: "assets/icons/Search Icon.svg",
              press: () {
                Navigator.of(context).pushNamed(FindOrderScreen.routeName);
              },
            ),
            child: ProfileMenu(
              text: "My Orders",
              icon: "assets/icons/receipt.svg",
              press: () {
                Provider.of<OrderProvider>(context, listen: false)
                    .getOrderList();
                Navigator.pushNamed(context, MyOrderListScreen.routeName);
              },
            ),
          ),
          Visibility(
            visible: !isAdminOrStaff,
            child: ProfileMenu(
                text: "My Discounts",
                icon: "assets/icons/Discount.svg",
                press: () async => {
                      await context.read<DiscountProvider>().getUserDiscount(),
                      Navigator.pushNamed(context, MyDiscountScreen.routeName),
                    }),
          ),
          ProfileMenu(
            text: "Settings",
            icon: "assets/icons/Settings.svg",
            press: () {},
          ),
          ProfileMenu(
            text: "Help Center",
            icon: "assets/icons/Question mark.svg",
            press: () {},
          ),
          ProfileMenu(
            text: "Log Out",
            icon: "assets/icons/Log out.svg",
            press: () {
              context.read<AuthProvider>().userSignOut(context).then((result) {
                if (result.isLeft) {
                  toastLogoutSuccess(context, result);

                  Navigator.pushReplacementNamed(context, InitScreen.routeName);
                }
                if (result.isRight) {
                  toastLogoutError(context, result);
                }
              });
            },
          ),
        ],
      ),
    );
  }

  void _scanOrder(BuildContext context) async {
    try {
      if (await _notHaveCameraPermission(context)) return;
      QrScanner.scan(context).then((cameraScanResult) async {
        if (context.mounted && cameraScanResult != null) {
          final result =
              await Provider.of<OrderProvider>(context, listen: false)
                  .getDetailOrder(context, code: cameraScanResult);
          if (result) ToastService.toastSuccess(context, "Order Verified!");
        }
      });
    } catch (e) {
      logger.e(e);
    }
  }

  void _scanWithdraw(BuildContext context) async {
    try {
      if (await _notHaveCameraPermission(context)) return;
      QrScanner.scan(context).then((cameraScanResult) {
        if (context.mounted && cameraScanResult != null) {
          Provider.of<WithdrawProvider>(context, listen: false)
              .completeWithdraw(context, cameraScanResult);
        }
      });
    } catch (e) {
      logger.e(e);
    }
  }

  //if status is not granted return true
  Future<bool> _notHaveCameraPermission(BuildContext context) async {
    var status = await Permission.camera.status;
    if (status.isDenied) {
      status = await Permission.camera.request();
    }
    // if (libraryStatus.isDenied) {
    //   libraryStatus = await Permission.manageExternalStorage.request();
    // }
    if (status.isPermanentlyDenied && context.mounted) {
      ToastService.toastError(context, "Camera access is permanently denied!");
    }
    // if (libraryStatus.isPermanentlyDenied && context.mounted) {
    //   ToastService.toastError(
    //       context, "Library status access is permanently denied!");
    // }
    return !status.isGranted;
  }
}
