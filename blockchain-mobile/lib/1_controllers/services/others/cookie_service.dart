import 'dart:io';

import 'package:blockchain_mobile/constants.dart';
import 'package:cookie_jar/cookie_jar.dart';
import 'package:flutter/foundation.dart';
import 'package:path_provider/path_provider.dart';

class CookieService {
  static final CookieService _cookieService = CookieService._internal();

  late final CookieJar cookieJar;
  factory CookieService() {
    return _cookieService;
  }
  CookieService._internal();
  Future init() async {
    if (kDebugMode) {
      print("Cookie Creation...");
    }
    Directory? dir;
    try {
      if (Platform.isAndroid) {
        dir = await getApplicationDocumentsDirectory();
      } else {
        dir = null;
      }
    } catch (e) {
      if (kIsWeb) {
        logger
            .w("Can't run this application on web please run on windows/phone");
      }
    }
    var path = cookiePath;
    if (dir != null) {
      path = "${dir.path}/$path";
    }
    cookieJar =
        PersistCookieJar(storage: FileStorage(path), ignoreExpires: true);
    if (kDebugMode) {
      print("Cookie Created");
    }
  }

  List<Cookie> decodeCookieStrings(List<String>? cookieStrings) {
    if (cookieStrings == null || cookieStrings.isEmpty) {
      return [];
    }

    List<Cookie> cookies = [];
    for (String cookieString in cookieStrings) {
      cookies.add(_decodeCookieString(cookieString));
    }
    return cookies;
  }

  Cookie _decodeCookieString(String cookieString) {
    List<String> parts = cookieString.split(';');

    String name = '';
    String value = '';

    for (int i = 0; i < parts.length; i++) {
      String part = parts[i].trim();
      if (i == 0) {
        // The first part contains the name and value of the cookie
        List<String> nameValue = part.split('=');
        name = nameValue[0].trim();
        if (nameValue.length > 1) {
          value = nameValue[1].trim();
        }
      } else {
        // Other parts may contain additional attributes like path, domain, etc.
        // You can handle these attributes if needed
        // For simplicity, we're ignoring them in this example
      }
    }

    return Cookie(name, value);
  }
}
