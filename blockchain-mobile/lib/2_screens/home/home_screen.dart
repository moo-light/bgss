import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/cart_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/live_rate_provider.dart';
import 'package:blockchain_mobile/2_screens/cart/cart_screen.dart';
import 'package:blockchain_mobile/2_screens/home/components/gold_detail.dart';
import 'package:blockchain_mobile/2_screens/home/components/price_charts.dart';
import 'package:blockchain_mobile/2_screens/sign_in/sign_in_screen.dart';
import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:provider/provider.dart';

class HomeScreen extends StatefulWidget {
  static String routeName = "/home";
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  final bool _searching = false;
  final _priceCharts = const TradingViewChart();
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    final carts = context.watch<CartProvider>().carts;
    final authProvider = context.watch<AuthProvider>();
    final roles = authProvider.roles;
    var liveRateProvider = Provider.of<LiveRateProvider>(
      context,
    );
    return Scaffold(
      appBar: AppBar(
        centerTitle: true,
        backgroundColor: const Color(0xffEBE4DB),
        title: Stack(
          fit: StackFit.loose,
          children: [
            Image.asset(
              "assets/images/BGSS_Logo_Large.png",
              fit: BoxFit.cover,
              width: 100,
            ),
          ],
        ),
        // leading: Center(
        //   child: IconButton(
        //     onPressed: () => {
        //     },
        //     icon: const FaIcon(FontAwesomeIcons.bars),
        //   ),
        // ),
        actions: !authProvider.isAdminOrStaff
            ? [
                // Text(Localizations.localeOf(context).languageCode),
                // IconButton(
                //   icon: const SizedBox(
                //     width: 25,
                //     height: 25,
                //     child:
                //         FaIcon(FontAwesomeIcons.solidSun, color: Color(0xffA26413)),
                //   ),
                //   onPressed: () {},
                // ),
                Visibility(
                  visible: authProvider.userLogined,
                  child: IconButton(
                    icon: Badge.count(
                      isLabelVisible: carts.isNotEmpty,
                      offset: const Offset(8, -7),
                      count: carts.length,
                      child: SvgPicture.asset(
                        "assets/icons/Cart Icon.svg",
                        width: 22,
                        height: 22,
                        colorFilter:
                            const ColorFilter.mode(kIconColor, BlendMode.srcIn),
                      ),
                    ),
                    onPressed: () {
                      if (roles?.contains("CUSTOMER") ?? false) {
                        context.read<CartProvider>().loadCarts();
                      }
                      Navigator.pushNamed(context, CartScreen.routeName);
                    },
                  ),
                ),
                Visibility(
                  visible: authProvider.userLogined,
                  replacement: IconButton(
                    icon: const SizedBox(
                      height: 25,
                      width: 25,
                      child: FaIconGen(
                        FontAwesomeIcons.solidCircleUser,
                      ),
                    ),
                    onPressed: () {
                      Navigator.pushNamed(context, SignInScreen.routeName);
                    },
                  ),
                  child: IconButton(
                    icon: const SizedBox(
                      height: 25,
                      width: 25,
                      child: FaIconGen(
                        FontAwesomeIcons.bell,
                      ),
                    ),
                    onPressed: () {},
                  ),
                )
              ]
            : null,
      ),
      body: SingleChildScrollView(
        child: Column(
          children: [
            Column(
              children: [
                _priceCharts,
                // Row(
                //   children: [
                //     TextButton(
                //       style: confirmTextBtnStyle,
                //       onPressed: () {
                //         showDialog(
                //           context: context,
                //           builder: (context) {
                //             return AlertDialog(
                //               title: const Text("Select Date"),
                //               content: Column(
                //                 mainAxisSize: MainAxisSize.min,
                //                 children: [
                //                   ...ChartType.values.map((ChartType unit) {
                //                     return ListTile(
                //                       // Replace with unit.symbol if you have defined symbols for each unit.
                //                       title: Text(unit.name),
                //                       onTap: () {
                //                         liveRateProvider
                //                             .fetchRateHistory(unit);
                //                         liveRateProvider.setType(unit);
                //                         Navigator.of(context).pop();
                //                       },
                //                     );
                //                   })
                //                 ],
                //               ),
                //             );
                //           },
                //         );
                //       },
                //       child: Text(
                //         liveRateProvider.type.interval,
                //       ),
                //     ),
                //     const Spacer(),
                //     // IconButton(
                //     //     onPressed: () {},
                //     //     icon: const FaIcon(FontAwesomeIcons.magnifyingGlassPlus)),
                //     // IconButton(
                //     //     onPressed: () {},
                //     //     icon: const FaIcon(FontAwesomeIcons.magnifyingGlassMinus)),
                //     // IconButton(
                //     //     onPressed: () {},
                //     //     icon: const FaIcon(FontAwesomeIcons.arrowsRotate))
                //   ],
                // )
              ],
            ),
            const SizedBox(height: 10),
            const Padding(
              padding: EdgeInsets.all(10.0),
              child: GoldDetail(name: "Gold", code: "USD/XAU"),
            )
          ],
        ),
      ),
    );
  }
}
