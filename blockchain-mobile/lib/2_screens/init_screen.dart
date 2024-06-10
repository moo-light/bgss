// ignore_for_file: prefer_const_constructors

import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/discount_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/image_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/post_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/products_provider.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/2_screens/home/home_screen.dart';
import 'package:blockchain_mobile/2_screens/products/products_screen.dart';
import 'package:blockchain_mobile/2_screens/profile/profile_screen.dart';
import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:provider/provider.dart';

import 'forum/forum_screen.dart';

const Color inActiveIconColor = Color(0xFFB6B6B6);

class InitScreen extends StatefulWidget {
  const InitScreen({super.key});

  static String routeName = "/";

  @override
  State<InitScreen> createState() => _InitScreenState();
}

class _InitScreenState extends State<InitScreen> {
  int currentSelectedIndex = 0;
  bool? userLogin;
  static const _homeScreen = 0;
  static const _productScreen = 1;
  static const _postsScreen = 2;
  static const _profileScreen = 3;
  void updateCurrentIndex(int index) {
    //Product Screen
    if (Provider.of<AuthProvider>(context, listen: false).isAdminOrStaff &&
        index != _profileScreen) {
      index = _profileScreen;
      ToastService.toastError(context, "Only user can view all pages",
          alignment: Alignment.topCenter,
          autoCloseDuration: const Duration(seconds: 2));
    }
    if (index == _productScreen) {
      context.read<ProductProvider>().getProductList();
      context.read<DiscountProvider>().getAllDiscount(context);
      context
          .read<DiscountProvider>()
          .getUserDiscount(expire: false, showAll: true);
    }
    if (index == _postsScreen) {
      context.read<PostProvider>().getCategories(context);
      context.read<PostProvider>().getPostList();
    }
    if (index == _profileScreen) {
      context.read<AuthProvider>().getCurrentUser();
      context.read<AuthProvider>().getSecretKey();
    } else {
      context.read<AppImageProvider>().clearImage();
    }
    setState(() {
      currentSelectedIndex = index;
    });
  }

  final pages = [
    const HomeScreen(),
    const ProductsScreen(),
    const PostsScreen(),
    const ProfileScreen()
  ];
  @override
  void initState() {
    // Provider.of<LiveRateProvider>(context,listen: false).requestLatestPrice();

    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    var authProvider = context.watch<AuthProvider>();
    print(authProvider.isLoading || authProvider.isAutoLogin);
    return Scaffold(
      body: authProvider.isLoading || authProvider.isAutoLogin
          ? const Center(child: CircularProgressIndicator())
          : pages[currentSelectedIndex],
      bottomNavigationBar: BottomNavigationBar(
        onTap: updateCurrentIndex,
        currentIndex: currentSelectedIndex,
        showSelectedLabels: false,
        showUnselectedLabels: false,
        type: BottomNavigationBarType.fixed,
        backgroundColor:
            Theme.of(context).bottomNavigationBarTheme.backgroundColor,
        items: [
          // _buildNavBarItem(context, "assets/icons/Home.svg", "Home"),
          // _buildNavBarItem(context, "assets/icons/Shop Icon.svg", "Shop"),
          BottomNavigationBarItem(
            label: "Home",
            icon: FaIconGen(
              FontAwesomeIcons.house,
              width: 25,
            ),
            activeIcon: FaIconGen(
              FontAwesomeIcons.house,
              color: kPrimaryColor,
              width: 25,
            ),
          ),
          BottomNavigationBarItem(
            label: "Shop",
            icon: FaIconGen(
              FontAwesomeIcons.box,
              width: 25,
            ),
            activeIcon: FaIconGen(
              FontAwesomeIcons.box,
              color: kPrimaryColor,
              width: 25,
            ),
          ),
          // _buildNavBarItem(context, "assets/icons/News.svg", "News"),
          BottomNavigationBarItem(
            label: "",
            icon: FaIconGen(
              FontAwesomeIcons.newspaper,
              width: 25,
            ),
            activeIcon: FaIconGen(
              FontAwesomeIcons.solidNewspaper,
              color: kPrimaryColor,
              width: 25,
            ),
          ),
          _buildNavBarItem(context, "assets/icons/User Icon.svg", "User"),
        ],
      ),
      // floatingActionButton: Stack(children: [
      //   Container(height: 50, width: 50, color: Colors.transparent),
      //   AnimatedPositioned(
      //     duration: Durations.medium1,
      //     left: kDebugMode
      //         ? 0
      //         : 50,
      //     child: Badge(
      //       textStyle: const TextStyle(
      //         fontSize: 14,
      //         fontWeight: FontWeight.w400,
      //       ),
      //       label: const Text(""),
      //       child: FloatingActionButton.small(
      //         backgroundColor: kBackgroundColor,
      //         onPressed: () {
      //           context.read<AuthProvider>().userSignOut(context);
      //         },
      //         child: const FaIcon(FontAwesomeIcons.moneyCheck),
      //       ),
      //     ),
      //   ),
      // ]),
    );
  }

  BottomNavigationBarItem _buildNavBarItem(
      BuildContext context, String assetName, label) {
    final icon = SvgPicture.asset(
      assetName,
      colorFilter: ColorFilter.mode(
        Theme.of(context).bottomNavigationBarTheme.unselectedIconTheme?.color ??
            kIconColor,
        BlendMode.srcIn,
      ),
    );
    final activeIcon = SvgPicture.asset(
      assetName,
      colorFilter: ColorFilter.mode(
        Theme.of(context).bottomNavigationBarTheme.selectedIconTheme?.color ??
            kIconActiveColor,
        BlendMode.srcIn,
      ),
    );

    return BottomNavigationBarItem(
      icon: SizedBox(height: 25, child: icon),
      activeIcon: SizedBox(height: 25, child: activeIcon),
      label: label,
    );
  }
}
