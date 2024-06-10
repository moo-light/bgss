import 'dart:convert';

import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/live_rate_provider.dart';
import 'package:blockchain_mobile/2_screens/home/components/trade.dart';
import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../../3_components/user_storage.dart';
import '../../../models/liverates/forex_live_rate.dart';

class GoldDetail extends StatefulWidget {
  const GoldDetail({super.key, required this.name, required this.code});
  final String name;
  final String code;

  @override
  State<GoldDetail> createState() => _GoldDetailState();
}

class _GoldDetailState extends State<GoldDetail> {
  final bool _starred = false;

  ForexSocketLiveRate? _liveRate;
  double get changedPrice => _liveRate?.changedPrice ?? 0;
  double get changedPercentage => _liveRate?.changedPercentage ?? 0;
  double get currentPrice => _liveRate?.mid ?? 0;
  DateTime get lastUpdated =>
      _liveRate?.timeframeDate ?? DateTime.parse("06/01/2024");

  final pages = [];

  int _currentIndex = 0;

  @override
  void initState() {
    SharedPreferences.getInstance().then((value) {
      String? rate = value.getString("live-rate");
      try {
        if (rate != null) _liveRate = json.decode(rate);
      } catch (e) {}
      setState(() {});
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    _liveRate = context.watch<LiveRateProvider>().liveRate;
    final authProvider = context.watch<AuthProvider>();
    final isCustomer = authProvider.isCustomer;
    if (_liveRate == null) {
      return Container(
        constraints: const BoxConstraints(minHeight: 300),
        decoration: BoxDecoration(
          color: kBackgroundColor.withHSLlighting(0.93),
        ),
        child: const Center(child: CircularProgressIndicator()),
      );
    }
    return Consumer<LiveRateProvider>(
      builder: (context, value, child) {
        _liveRate = context.watch<LiveRateProvider>().liveRate;
        return child!;
      },
      child: Container(
        constraints: const BoxConstraints(minHeight: 300),
        decoration:
            BoxDecoration(color: kBackgroundColor.withHSLlighting(0.93)),
        child: Column(
          children: [
            Stack(children: [
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: 10.0),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const UserStorage(),
                    ListTile(
                      contentPadding: const EdgeInsets.symmetric(horizontal: 0),
                      title: Text(widget.name,
                          style: const TextStyle(fontSize: 16)),
                      subtitle: Text(widget.code,
                          style: TextStyle(color: Theme.of(context).hintColor)),
                    ),
                    // Pricing Data
                    _buildPricingData(),
                    DefaultTextStyle(
                      style: const TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                          color: kTextColor),
                      child: Row(
                        // mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Text.rich(
                              style: const TextStyle(color: kWinColor),
                              TextSpan(text: "Ask:", children: [
                                TextSpan(
                                  text: _liveRate?.ask.toStringAsFixed(2),
                                )
                              ])),
                          const VerticalDivider(),
                          Text.rich(
                              style: const TextStyle(color: kLossColor),
                              TextSpan(text: "Bid:", children: [
                                TextSpan(
                                  text: _liveRate?.bid.toStringAsFixed(2),
                                )
                              ])),
                        ],
                      ),
                    ),
                    // End Pricing Data
                    const SizedBox(
                      height: 10,
                    ),
                    // Last Update
                    Text(
                      "Last Update: ${DateFormat("HH:mm:ss").format(lastUpdated)}",
                      style: TextStyle(color: Theme.of(context).hintColor),
                      textAlign: TextAlign.start,
                    ),

                    const SizedBox(
                      height: 10,
                    ),
                  ],
                ),
              ),
              // Positioned(
              //   right: 10,
              //   top: 16,
              //   child: Row(
              //     children: [
              //       GestureDetector(
              //           onTap: () => {Feedback.forTap(context)},
              //           child: const FaIconGen(
              //             FontAwesomeIcons.solidBell,
              //             width: 20,
              //           )),
              //       const SizedBox(
              //         width: 15,
              //       ),
              //       GestureDetector(
              //           onTap: () => setState(() {
              //                 Feedback.forTap(context);
              //                 _starred = !_starred;
              //               }),
              //           child: FaIconGen(
              //             FontAwesomeIcons.star,
              //             color: _starred ? kPrimaryColor : null,
              //             width: 20,
              //           )),
              //     ],
              //   ),
              // ),
            ]),
            // Trade/Information Selection
            Row(children: [
              Expanded(
                child: Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16.0),
                  child: AbsorbPointer(
                    child: ElevatedButton(
                      onPressed: () => setState(() {
                        _currentIndex = 0;
                      }),
                      style: ElevatedButton.styleFrom(
                        shape: const LinearBorder(
                            bottom: LinearBorderEdge(size: 1)),
                        backgroundColor: kBackgroundColor,
                        foregroundColor: Colors.black,
                        elevation: 0,
                      ),
                      child: const Text(
                        "Trade",
                        style: TextStyle(
                            color: Colors.black,
                            fontWeight: FontWeight.bold,
                            fontSize: 20),
                        textAlign: TextAlign.center,
                      ),
                    ),
                  ),
                ),
              ),
            ]),
            // Trade/Information
            Trade(widget.code)
          ],
        ),
      ),
    );
  }

  Row _buildPricingData() {
    return Row(
      children: [
        Text(
          currentPrice.toStringAsFixed(2),
          style: TextStyle(
            color: changedPrice < 0 ? kLossColor : kWinColor,
            fontSize: 32,
            fontWeight: FontWeight.bold,
          ),
        ),
        FaIconGen(
          changedPrice < 0
              ? FontAwesomeIcons.downLong
              : FontAwesomeIcons.upLong,
          width: 20,
          height: 20,
          color: changedPrice < 0 ? kLossColor : kWinColor,
        ),
        Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                SizedBox(
                  width: 10,
                  child: Text(
                    changedPrice < 0 ? '-' : '+',
                    style: TextStyle(
                        color: changedPrice < 0 ? kLossColor : kWinColor,
                        fontSize: 14),
                    textAlign: TextAlign.center,
                  ),
                ),
                Text(
                  changedPrice.abs().toStringAsFixed(2),
                  style: TextStyle(
                      color: changedPrice < 0 ? kLossColor : kWinColor,
                      fontSize: 14),
                ),
              ],
            ),
            Row(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                SizedBox(
                  width: 10,
                  child: Text(
                    changedPercentage < 0 ? '-' : '+',
                    style: TextStyle(
                        color: changedPercentage < 0 ? kLossColor : kWinColor,
                        fontSize: 14),
                    textAlign: TextAlign.center,
                  ),
                ),
                Text(
                  changedPercentage.abs().toStringAsFixed(2),
                  style: TextStyle(
                      color: changedPercentage < 0 ? kLossColor : kWinColor,
                      fontSize: 14),
                ),
                SizedBox(
                  width: 15,
                  child: Text(
                    "%",
                    style: TextStyle(
                        color: changedPercentage < 0 ? kLossColor : kWinColor,
                        fontSize: 14),
                    textAlign: TextAlign.end,
                  ),
                ),
              ],
            ),
          ],
        ),
      ],
    );
  }

  // void repeat() async {
  //   while (true && mounted) {
  //     await Future.delayed(Durations.extralong4);
  //     if (mounted) {
  //       setState(() {
  //         lastUpdated = DateTime.now();
  //       });
  //     } else {
  //       break;
  //     }
  //   }
  // }

  @override
  void dispose() {
    super.dispose();
  }
}
