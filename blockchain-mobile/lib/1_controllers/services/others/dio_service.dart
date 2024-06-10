import 'dart:io';

import 'package:blockchain_mobile/1_controllers/repositories/auth_repository.dart';
import 'package:blockchain_mobile/1_controllers/services/others/cookie_service.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/main.dart';
import 'package:dio/dio.dart';
import 'package:dio_cookie_manager/dio_cookie_manager.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:jwt_decoder/jwt_decoder.dart';
import 'package:shared_preferences/shared_preferences.dart';

class DioService {
  static final DioService _dioService = DioService._internal();
  final _dio = Dio();
  late final CookieService _cookieService;
  factory DioService() {
    return _dioService;
  }

  Dio get dio => _dio;
  DioService._internal([String? baseUrl]) {
    _cookieService = CookieService();
    _dio.interceptors.add(CookieManager(_cookieService.cookieJar));

    _init();

    baseUrl ??= dotenv.env["DEFAULT_PATH"]!;
    _dio.options.baseUrl = baseUrl;

    if (_dio.options.baseUrl.contains("10.0.2.2")) {
      logger.i("you can use localhost instead of 10.0.2.2");
    }
    if (_dio.options.baseUrl.contains("localhost") &&
        (Platform.isAndroid || Platform.isIOS)) {
      _dio.options.baseUrl =
          _dio.options.baseUrl.replaceFirst("localhost", "10.0.2.2");
    }
    _dio.options.contentType = 'application/json';
    _dio.options.sendTimeout = const Duration(seconds: 10);
    // _dio.options.connectTimeout = const Duration(seconds: 10);
    // _dio.options.receiveTimeout = const Duration(seconds: 10);
  }
  Future<void> _init() async {
    var cookies = await _cookieService.cookieJar
        .loadForRequest(Uri.parse(refreshTokenPath))
        .then((value) => value);
    logger.d({
      "Initial Cookies List": cookies,
      'Expires': cookies.map((e) {
        final expiresUTC7 = e.expires?.toLocal();
        return expiresUTC7;
      })
    });
    addCookieHeader(cookies);
  }

  set baseUrl(String value) {
    if (value.isNotEmpty && !kIsWeb && Uri.parse(value).host.isEmpty) {
      throw ArgumentError.value(
        value,
        'baseUrl',
        'Must be a valid URL on platforms other than Web.',
      );
    }
    _dio.options.baseUrl = value;
    _tryRefreshToken();
  }

  void saveAuthorization(token, type) async {
    final sf = await SharedPreferences.getInstance();
    _dio.options.headers["Authorization"] = "$type $token";
  }

  void timeoutToken() {
    String? existingCookieHeader = _dio.options.headers["Cookie"];
    List<String>? cookies = existingCookieHeader?.split(';') ?? [];
    String token = cookies
        .firstWhere((element) => (element.split("=").first == 'bezkoder-jwt'))
        .split("=")
        .last;
    final tokenDecoded = JwtDecoder.decode(token);
    final exp = tokenDecoded['exp'];
    final currentTime = DateTime.now().millisecondsSinceEpoch ~/ 1000;

    final delaySeconds = exp - currentTime;

    if (delaySeconds > 0) {
      Future.delayed(Duration(seconds: delaySeconds), () async {
        // final result = await _tryRefreshToken();
        // if (result.isRight)
        notifyTokenExpired();
      });
    } else {
      // Token has already expired, notify immediately
      notifyTokenExpired();
    }
  }

  void addCookieHeader(List<Cookie> cookies) {
    for (var element in cookies) {
      _addCookieHeader(element);
    }
  }

  void _addCookieHeader(Cookie cookie) {
    String? existingCookieHeader = _dio.options.headers["Cookie"];
    List<String>? cookies = existingCookieHeader?.split(';') ?? [];
    bool cookieFound = false;

    for (int i = 0; i < cookies.length; i++) {
      if (cookies[i].startsWith("${cookie.name}=")) {
        cookies[i] = "${cookie.name}=${cookie.value}";
        cookieFound = true;
        break;
      }
    }

    if (!cookieFound) {
      cookies.add("${cookie.name}=${cookie.value}".trim());
    }

    _dio.options.headers["Cookie"] = cookies.join(';');
    logger.d({"existing cookie Header": cookies});
  }

  Future<Either<String, String?>> _tryRefreshToken() async {
    final authRepository = AuthRepository();
    return await authRepository.refreshToken();
  }
}
