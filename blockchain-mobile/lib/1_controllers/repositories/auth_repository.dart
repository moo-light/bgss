import 'dart:convert';

import 'package:blockchain_mobile/1_controllers/services/dio_exception_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/cookie_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/login_response.dart';
import 'package:cookie_jar/cookie_jar.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../models/user.dart';

class AuthRepository {
  final DioService _dioService = DioService();
  final CookieService _cookieService = CookieService();
  final DioExceptionService _dioExceptionService = DioExceptionService();

  Dio get dio => _dioService.dio;
  late final CookieJar cookieJar;
  factory AuthRepository() {
    return AuthRepository._internal();
  }
  AuthRepository._internal() {
    cookieJar = _cookieService.cookieJar;
  }

  Future<Either<LoginResponseObject, String?>> login(
      String username, String password) async {
    try {
      final result = await dio.post(
        '/api/auth/sign-in-v1',
        data: {
          "username": username,
          "password": password,
        },
      );
      logger.i("Login Success");
      //
      List<String>? rawCookie = result.headers['set-cookie'];
      List<Cookie> cookies = _cookieService.decodeCookieStrings(rawCookie);
      logger.d(rawCookie);
      _dioService.addCookieHeader(cookies);

      final LoginResponseObject loginResponse =
          LoginResponseObject.fromMap(result.data);
      // Save important information
      var sf = await SharedPreferences.getInstance();

      var refresh =
          cookies.firstWhere((element) => element.name == STORAGE_REFRESHTOKEN);
      sf.setString(STORAGE_USER, jsonEncode(loginResponse.toJson()));
      sf.setString(STORAGE_USERID, result.data['id'].toString());
      //save refreshToken to session
      sf.setString(STORAGE_REFRESHTOKEN, '${refresh.name}=${refresh.value}');
      return Left(loginResponse);
    } catch (e) {
      if (e is! DioException) {
        return Right(e.toString());
      }
      final error = await _dioExceptionService.handleDioException(e);
      return Right(error);
    }
  }

  Future<Either<User, String?>> getCurrentUserInformation() async {
    try {
      final sf = await SharedPreferences.getInstance();
      var uid = sf.getString(STORAGE_USERID);

      debugPrint({'user_id': uid}.toString());

      final result = await dio.get(
        '/api/auth/show-user-info/$uid',
      );

      if (result.data['data'] == null) {
        return const Right("Data is not available.");
      }

      final json = result.data['data'] as Map<String, dynamic>;
      final User user = User.fromJson(json);
      return Left(user);
    } on DioException catch (e) {
      final error = await _dioExceptionService.handleDioException(e);
      return Right(error);
    } on Error catch (e, st) {
      logger.e(e, stackTrace: st);
      return Right(e.toString());
    }
  }

  Future<Either<String, String?>> register(
    String username,
    String email,
    String password,
    String firstName,
    String lastName,
    String phone,
    String address,
  ) async {
    try {
      var response = await dio.post(
        '/api/auth/sign-up',
        data: {
          "username": username,
          "email": email,
          "password": password,
          "firstName": firstName,
          "lastName": lastName,
          "phoneNumber": phone,
          "address": address,
        },
      );
      return Left(response.data['message']);
    } catch (e) {
      if (e is! DioException) {
        return Right(e.toString());
      }
      final error = await _dioExceptionService.handleDioException(e);

      return Right(error);
    }
  }

  Future<Either<bool, String?>> logout() async {
    try {
      // final results = await _getUserAuthInformation();
      // if (results.isNotEmpty) {
      //   logger.d("${results[0].name}=${results[0].value}");
      //   dio.options.headers["Authorization"] = "Bearer ${results[0].value}";
      //   dio.options.headers["Cookie"] =
      //       "${results[0].name}=${results[0].value}";
      // }
      var post = await dio.post(
        '/api/auth/sign-out',
      );
      assert(post.data is bool);
      if (post.data) {
        final sf = await SharedPreferences.getInstance();
        sf.remove(STORAGE_USERID);
        sf.remove(STORAGE_USER);
        sf.remove(STORAGE_REFRESHTOKEN);
      }
      logger.i("Logout ${post.data ? 'Success' : 'Fail!'}");
      return Left(post.data);
    } catch (e) {
      if (e is DioException) {
        var value = await _dioExceptionService.handleDioException(e);
        return Right(value);
      } else {
        logger.e(e);
      }
      return Right(e.toString());
    }
  }

  Future<Either<String, String?>> refreshToken() async {
    final pref = await SharedPreferences.getInstance();
    try {
      // final results = await getUserAuthInformation();
      // if (results.isNotEmpty) {
      // dio.options.headers["Authorization"] = "Bearer ${results[0].value}";
      String? cookie = dio.options.headers["Cookie"];
      if (cookie != null) {
        var refresh = pref.getString(STORAGE_REFRESHTOKEN);
        if (refresh != null) {
          cookie = cookie.replaceFirst(
              RegExp(r"bezkoder-jwt-refresh=.*(?=;)"), refresh);
          if (!cookie.contains(STORAGE_REFRESHTOKEN)) {
            cookie = refresh + cookie ?? "";
          }
        } else {
          return const Right("No Refresh Token");
        }
        dio.options.headers["Cookie"] = cookie;
      }
      debugPrint("Calling Refresh Token");
      var post = await dio.post(
        '/api/auth/refresh-token',
      );
      debugPrint("Calling Refresh Token Success!");
      List<String>? rawCookie = post.headers['set-cookie'];
      debugPrint(rawCookie.toString());
      assert(rawCookie != null);
      List<Cookie> cookies = _cookieService.decodeCookieStrings(rawCookie);

      //Add cookie to Header
      _dioService.addCookieHeader(cookies);
      _dioService.timeoutToken();
      return Left(post.data["message"]);
    } catch (e, stackTrace) {
      if (e is DioException) {
        var value = await _dioExceptionService.handleDioException(e);
        return Right(value);
      } else {
        logger.e(e, stackTrace: stackTrace);
      }
      return Right(e.toString());
    }
  }
}
