import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import 'constants.dart';

class AppTheme {
  static ThemeData lightTheme(BuildContext context) {
    return ThemeData(
      colorScheme: ColorScheme.fromSeed(
        seedColor: kTextColor,
        primary: kPrimaryColor,
        background: kBackgroundColor,
      ),
      splashColor: Colors.white10,
      scaffoldBackgroundColor: kBackgroundColor,
      appBarTheme: const AppBarTheme(
        color: Colors.white,
        elevation: 0,
        iconTheme: IconThemeData(color: Colors.black),
        // titleTextStyle: TextStyle(color: Colors.black),
      ),
      iconTheme: const IconThemeData(
        color: kIconColor,
      ),
      iconButtonTheme: const IconButtonThemeData(
          style: ButtonStyle(iconColor: MaterialStatePropertyAll(kIconColor))),
      bottomNavigationBarTheme: const BottomNavigationBarThemeData(
        elevation: 0,
        backgroundColor: Color(0xffEBE4DB),
        unselectedIconTheme: IconThemeData(
          color: kIconColor,
        ),
        selectedIconTheme: IconThemeData(
          color: kIconActiveColor,
        ),
      ),
      textTheme: GoogleFonts.publicSansTextTheme(const TextTheme(
        bodyLarge: TextStyle(color: kTextColor),
        bodyMedium: TextStyle(color: kTextColor),
        bodySmall: TextStyle(color: kTextColor),
      )),
      textButtonTheme: TextButtonThemeData(
        style: TextButton.styleFrom(
          foregroundColor: kTextColor,
          backgroundColor: kBackgroundColor,
          elevation: 1,
          shadowColor: kBackgroundColor.withOpacity(0.1),
          // textStyle: const TextStyle(
          //   color: kSecondaryColor,
          // ),
        ),
      ),
      primaryColor: kPrimaryColor,
      progressIndicatorTheme: const ProgressIndicatorThemeData(
        color: kPrimaryColor,
      ),
      checkboxTheme: CheckboxThemeData(
        // overlayColor: MaterialStateProperty.all(Colors.black),
        checkColor: MaterialStateProperty.all(Colors.black),
        fillColor: MaterialStateProperty.resolveWith((states) {
          if (states.contains(MaterialState.selected)) {
            return kBackgroundColor; // Use your background color here
          }
          return Colors.transparent; // Make it transparent if not selected
        }),
      ),
      floatingActionButtonTheme:  const FloatingActionButtonThemeData(
        backgroundColor: kPrimaryColor,
        foregroundColor: kBackgroundColor
      ),
      inputDecorationTheme: const InputDecorationTheme(
        floatingLabelBehavior: FloatingLabelBehavior.always,
        hintStyle: TextStyle(color: kHintTextColor),
        labelStyle: TextStyle(color: kTextColor),
        contentPadding: EdgeInsets.symmetric(horizontal: 42, vertical: 20),
        enabledBorder: outlineInputBorder,
        focusedBorder: outlineInputBorder,
        border: outlineInputBorder,
      ),
      // visualDensity: VisualDensity.adaptivePlatformDensity,
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
            elevation: 0,
            backgroundColor: kPrimaryColor,
            foregroundColor: Colors.white,
            minimumSize: const Size(100, 48),
            shape: const RoundedRectangleBorder(
              borderRadius: BorderRadius.all(Radius.circular(16)),
            ),
            textStyle: const TextStyle(
              // Add this line to make text bold
              fontWeight: FontWeight.bold,
            ),
            disabledBackgroundColor: kDisabledButtonColor,
            disabledForegroundColor: Colors.black54),
      ),
    );
  }

  static ThemeData darkTheme(BuildContext context) {
    return ThemeData(
        scaffoldBackgroundColor:
            const Color(0xFF041C32), // Change background color to black
        fontFamily: "Muli",
        appBarTheme: const AppBarTheme(
          color: Colors.black, // Change app bar color to black
          elevation: 0,
          iconTheme: IconThemeData(
              color: Colors.white), // Change app bar icon color to white
          titleTextStyle: TextStyle(
              color: Colors.white), // Change app bar title text color to white
        ),
        textTheme: const TextTheme(
          bodyLarge:
              TextStyle(color: Colors.white), // Change body text color to white
          bodyMedium: TextStyle(color: Colors.white),
          bodySmall: TextStyle(color: Colors.white),
        ),
        iconButtonTheme: const IconButtonThemeData(
          style: ButtonStyle(
            iconColor: MaterialStatePropertyAll<Color?>(Colors.amber),
          ),
        ),
        iconTheme: const IconThemeData(
          color: Colors.amber,
        ),
        hintColor: Colors.grey,
        inputDecorationTheme: const InputDecorationTheme(
          floatingLabelBehavior: FloatingLabelBehavior.always,
          contentPadding: EdgeInsets.symmetric(horizontal: 42, vertical: 20),
          enabledBorder: outlineInputBorder,
          focusedBorder: outlineInputBorder,
          border: outlineInputBorder,
        ),
        visualDensity: VisualDensity.adaptivePlatformDensity,
        elevatedButtonTheme: ElevatedButtonThemeData(
          style: ElevatedButton.styleFrom(
            elevation: 0,
            backgroundColor: kPrimaryColor, // Change button background color
            foregroundColor: Colors.white, // Change button text color to white
            minimumSize: const Size(double.infinity, 48),
            shape: const RoundedRectangleBorder(
              borderRadius: BorderRadius.all(Radius.circular(16)),
            ),
          ),
        ),
        floatingActionButtonTheme: FloatingActionButtonThemeData(
          backgroundColor: kPrimaryColor,
          foregroundColor: kIconColor,
          elevation: 0,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(16),
          ),
        ),
        radioTheme: RadioThemeData(
            fillColor:
                MaterialStatePropertyAll(kPrimaryColor.withHSLlighting(75))),
        navigationBarTheme:
            const NavigationBarThemeData(backgroundColor: Color(0xff04293A)));
  }
}

const OutlineInputBorder outlineInputBorder = OutlineInputBorder(
  borderRadius: BorderRadius.all(Radius.circular(16)),
  borderSide: BorderSide(color: kTextColor),
  gapPadding: 10,
);
