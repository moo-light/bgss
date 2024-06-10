import 'package:blockchain_mobile/1_controllers/repositories/auth_repository.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/login_response.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

class AuthService {
  static final AuthService _authService = AuthService._internal();
  final AuthRepository _authRepository;

  factory AuthService({AuthRepository? authRepository}) {
    if (authRepository != null) {
      return AuthService._internal(authRepository: authRepository);
    }
    return _authService;
  }
  AuthService._internal({AuthRepository? authRepository})
      : _authRepository = authRepository ?? AuthRepository();

  Future<Either<LoginResponseObject, String?>> userSignIn(username, password,
      [bool retry = false]) async {
    var either = await _authRepository.login(username, password);
    if (either.isLeft) {
      return either;
    }
    if (either.isRight) {
      if (retry) return either;
      //Show error
      final error = either.right;
      if (error?.contains("refresh token already exists") ?? false) {
        final result = await _authRepository.refreshToken();
        if (result.isLeft) {
          await _authRepository.logout();
          return await userSignIn(username, password, true);
        }
      }
      return either;
    }
    throw Exception("Unhandled Error!");
  }

  Future<Either<String, String?>> userSignUp(
      username, email, password, firstName, lastName, phone, address) async {
    var result = await _authRepository.register(
        username, email, password, firstName, lastName, phone, address);
    if (result.isLeft) {
      var loginResult = await _authRepository.login(username, password);
      if (loginResult.isLeft) {
      } else {
        throw Exception(
            "automatically login failed\n please Login manually with your account");
      }
    }
    return result;
  }

  Future<Either<bool, String?>> userSignOut() async {
    final sf = await SharedPreferences.getInstance();
    var ref = await _authRepository.refreshToken();

    Either<bool, String?> result = await _authRepository.logout();
    if (result.isLeft) {
      // if success response = true remove auto login
    }
    if (result.isRight) {}
    return result;
  }

  Future<bool> autoLogin(context) async {
    // await Future.delayed(const Duration(seconds: 3)); //wait 3 sec

    logger.i("Doing Auto Login");
    final result = await _authRepository.refreshToken();
    if (result.isLeft) {
      if (kDebugMode) {
        ToastService.toastSuccess(context, "Auto Login Success");
      }
      return true;
    }
    if (kDebugMode) {
      ToastService.toastError(context, "Auto Login Fail!");
    }
    return false;
  }
}

// UI
void toastLogoutError(context, Either<bool, String?> result) {
  ToastService.toastError(context, "Logout Failed!");
}

void toastLogoutSuccess(context, Either<bool, String?> result) {
  ToastService.toastSuccess(
      context, "Logout ${result.left ? "Success" : "Failed!"}");
}
