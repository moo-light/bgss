// ignore_for_file: non_constant_identifier_names, constant_identifier_names

import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:intl/intl.dart';
import 'package:logger/logger.dart';

const kPrimaryLightColor = Color(0xFFFFECDF);
const kPrimaryGradientColor = LinearGradient(
  begin: Alignment.topLeft,
  end: Alignment.bottomRight,
  colors: [Color(0xFFFFDA77), Color(0xffffa45b)],
);
const Color kPrimaryColor = Color(0xfffb8500);
const Color kSecondaryColor = Color(0xffd4af37);
const Color kTerinaryColor = Color(0xFFAEE6E6);
const Color kBackgroundColor = Color(0xffFBF6F0);
const Color kShadowColor = Color(0xff969B9A);
const Color kTextColor = Color(0xff000000);
const Color kHintTextColor = Color(0xff969B9A);
const Color kIconColor = Color(0xff7E7E7E);
const Color kIconActiveColor = kPrimaryColor;
const Color kLossColor = Color(0xffE93543);
const Color kWinColor = Color(0xff0D7967);
final Color kWinColorLight = const Color(0xff0D7967).addHSLlighting(5);
const Color kDisabledButtonColor = Color(0xff969B9A);
// class Constants {
//   static late final Color white;
//   Constants.lightTheme() {
//     kPrimaryColor = const Color(0xffffa45b);
//     kSecondaryColor = const Color(0xFFFFDA77);
//     kTerinaryColor = const Color(0xFFAEE6E6);
//     kBackgroundColor = const Color(0xffFBF6F0);
//     kTextColor = const Color(0xff000000);
//     kHintTextColor = const Color(0xff969B9A);
//     kIconColor = const Color(0xff7E7E7E);
//     white = Colors.white;
//   }
//   Constants.darkTheme() {
//     kPrimaryColor = const Color(0xff290001);
//     kSecondaryColor = const Color(0xFF87431D);
//     kTerinaryColor = const Color(0xFFC87941);
//     kBackgroundColor = const Color(0xffDBCBBD);
//     kTextColor = const Color(0xffffffff);
//     kIconColor = const Color(0xffB5B5B5);
//     kHintTextColor = const Color(0xffC1BDBB);
//     white = Colors.white;
//   }
// }

const defaultDuration = Duration(milliseconds: 250);
const headingStyle = TextStyle(
  fontSize: 24,
  fontWeight: FontWeight.bold,
  color: kTextColor,
  height: 1.5,
);
const kAnimationDuration = Duration(milliseconds: 200);

// Form Error
final RegExp emailValidatorRegExp =
    RegExp(r"^[a-zA-Z0-9.]+@[a-zA-Z0-9]+\.[a-zA-Z]+");
const String kUsernameNullError = "Please Enter your username";
const String kUsernameShortError = "Username must be more than 3 characters";
const String kUsernameLongError = "Username must be less than 20 characters";
const String kEmailNullError = "Please Enter your email";
const String kInvalidEmailError = "Please Enter Valid Email";
const String kEmailLongError = "Email must be less than 50 characters";
const String kPhoneNullError = "Please Enter your phone";
const String kInvalidPhoneError = "Phone is Invalid";
const String kPassNullError = "Please Enter your password";
const String kShortPassError = "Password is too short";
const String kLongPassError = "Pass must be less than 40 characters";
const String kMatchPassError = "Passwords don't match";
const String kNamelNullError = "Please Enter your name";
const String kPhoneNumberNullError = "Please Enter your phone number";
const String kAddressNullError = "Please Enter your address";

final otpInputDecoration = InputDecoration(
  contentPadding: const EdgeInsets.symmetric(vertical: 8),
  border: outlineInputBorder(),
  focusedBorder: outlineInputBorder(),
  enabledBorder: outlineInputBorder(),
);

OutlineInputBorder outlineInputBorder() {
  return OutlineInputBorder(
    borderRadius: BorderRadius.circular(16),
    borderSide: const BorderSide(color: kTextColor),
  );
}

final Logger logger = Logger();

var amber = Colors.amber;
var amberLight = const Color.fromARGB(255, 255, 204, 52);
var white = Colors.white;
// String
const STORAGE_USER = "user";
const STORAGE_USERID = "user-id";
const STORAGE_REFRESHTOKEN = "bezkoder-jwt-refresh";
const STORAGE_LIVE_RATE = "live-rate";
String STORAGE_CARTS(id) => "user-$id-carts";

// String Path
var defaultPath = dotenv.env['DEFAULT_PATH']!;
var basePath = "${defaultPath}api/auth/";
var cookiePath = dotenv.env['COOKIE_PATH']!;
var tokenPath = "${defaultPath}api";
var refreshTokenPath = "${defaultPath}api/auth/refreshToken";
// Icon
var iconCheck = const Icon(Icons.check, color: kWinColor);
var iconError = const Icon(Icons.error, color: kLossColor);
//formatter
String currencyFormat(number) {
  if (number is String) {
    throw const FormatException("currencyFormat need to input String");
  }
  return NumberFormat.currency(locale: "en_US", symbol: "\$").format(number);
}

final String Function(dynamic number) numberFormat =
    NumberFormat.decimalPattern("en_US").format;
final String Function(DateTime date) dateFormat =
    DateFormat("MM-dd-yyyy", "en_US").add_jms().format;
