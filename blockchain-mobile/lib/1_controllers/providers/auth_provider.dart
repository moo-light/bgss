import 'dart:async';

import 'package:blockchain_mobile/1_controllers/repositories/auth_repository.dart';
import 'package:blockchain_mobile/1_controllers/repositories/user_repository.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/2_screens/my_account/my_account_screen.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/dtos/response_object.dart';
import 'package:blockchain_mobile/models/dtos/result_object.dart';
import 'package:blockchain_mobile/models/login_response.dart';
import 'package:blockchain_mobile/models/payment_history.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:image_cropper/image_cropper.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../models/user.dart';

class AuthProvider extends ChangeNotifier {
  AuthProvider() {
    autoLogin().then((v) => {getCurrentUser(), getSecretKey()});
  }
  // AuthProvider.image(AppImageProvider appImageProvider) {
  //   _appImageProvider = appImageProvider;
  // }
  // AppImageProvider? _appImageProvider;
  final AuthRepository _authRepository = AuthRepository();
  final UserRepository _userRepository = UserRepository();

  bool userLogined = false;
  bool isLoading = false;
  bool isAutoLogin = false;
  bool isAvatarSubmitting = false;
  User? currentUser;
  String? roles;

  bool get isCustomer => roles?.contains(RegExp("CUSTOMER")) ?? false;
  bool get isAdminOrStaff => roles?.contains(RegExp("ADMIN|STAFF")) ?? false;
  bool get isGuest => roles == null;
  Future autoLogin([int count = 0]) async {
    isAutoLogin = true;
    try {
      final result = await _authRepository.refreshToken();
      // true if Refresh Success
      userLogined = result.isLeft;
      if (result.isLeft) {
        final pref = await SharedPreferences.getInstance();
        String json = pref.getString(STORAGE_USER)!;
        final loginResponseObject = LoginResponseObject.fromJson(json);
        roles = loginResponseObject.roles.join("|");
      }
      if (result.isRight && count <= 4) {
        await autoLogin(count + 1);
      }
    } finally {
      isAutoLogin = false;
      isLoading = false;
      notifyListeners();
    }
  }

  Future<Either<bool, String?>> userSignOut(BuildContext context) async {
    var ref = await _authRepository.refreshToken();
    ref.either((left) => debugPrint(left), (right) => debugPrint(right));
    // if (kDebugMode && ref.isLeft && context.mounted) {
    //   ToastService.toastSuccess(context, ref.left);
    // }
    Either<bool, String?> result = await _authRepository.logout();
    if (result.isLeft) {
      userLogined = false;
      currentUser = null;
      roles = null;
      notifyListeners();
    }
    if (result.isRight) {
      //never seem to exist so i haven't write anything here
    }

    return result;
  }

  Future<User?> getCurrentUser([bool? isJustLogined]) async {
    // _appImageProvider?.croppedFile = null;
    isLoading = true;
    userLogined = isJustLogined ?? userLogined;
    if (currentUser == null && userLogined) {
      isLoading = true;
      notifyListeners();
    }

    if (kDebugMode) await Future.delayed(Durations.extralong2);
    final result = await _authRepository.getCurrentUserInformation();
    isLoading = false;
    result.either((left) async {
      currentUser = result.left;
      final pref = await SharedPreferences.getInstance();
      String json = pref.getString(STORAGE_USER)!;
      final loginResponseObject = LoginResponseObject.fromJson(json);
      roles = loginResponseObject.roles.join("|");
      userLogined = true;
    }, (right) async {
      final tryRefresh = await _authRepository.refreshToken();
      if (tryRefresh.isLeft) {
        //try again
        final result = await _authRepository.getCurrentUserInformation();
        if (result.isRight) {
          currentUser = null;
        } else {
          currentUser = result.left;
        }
      }
    });

    notifyListeners();
    return currentUser;
  }

  Future<bool> updateUserAvatar(BuildContext context,
      {required CroppedFile avatar}) async {
    isAvatarSubmitting = true;
    notifyListeners();
    var result = await _userRepository
        .updateCurrentUserAvatar(currentUser!.userInfo.id, avatar: avatar);
    isAvatarSubmitting = false;
    if (result.isLeft) {
      await getCurrentUser();

      if (context.mounted) {
        ToastService.toastSuccess(context, "Update User's Avatar Success");
      }
    }
    if (result.isRight) {
      debugPrint(result.right);
      if (context.mounted) {
        ToastService.toastError(context, "Update User's Avatar Failed");
      }
    }
    notifyListeners();
    return result.isLeft;
  }

  Future updateUserSName(BuildContext context,
      {required firstname, required lastname}) async {
    if (currentUser == null) throw const FormatException("No User Found");
    var result = await Future.wait([
      _userRepository.updateUserInfo(currentUser!,
          firstName: firstname, lastName: lastname),
      Future.delayed(Durations.medium4)
    ]).then((value) => value.first);
    if (result.isLeft) {
      currentUser!.userInfo.firstName = firstname;
      currentUser!.userInfo.lastName = lastname;
      notifyListeners();
      if (context.mounted) {
        ToastService.toastSuccess(context, "Update User's name Success");
      }
    }
    if (result.isRight) {
      debugPrint(result.right);
      if (context.mounted) {
        ToastService.toastError(context, "Update User's name Failed");
      }
    }
  }

  Future updateUserPhone(BuildContext context, {required String phone}) async {
    if (currentUser == null) throw const FormatException("No User Found");
    var result = await Future.wait([
      _userRepository.updateUserInfo(currentUser!, phone: phone),
      Future.delayed(Durations.medium4)
    ]).then((value) => value.first);
    if (result.isLeft) {
      currentUser!.userInfo.phoneNumber = phone;
      notifyListeners();
      if (context.mounted) {
        ToastService.toastSuccess(context, "Update Phone Number Success");
      }
    }
    if (result.isRight) {
      debugPrint(result.right);
      if (context.mounted) {
        ToastService.toastError(context, "Update Phone Number Failed");
      }
    }
  }

  Future updateUserAddress(BuildContext context,
      {required String address}) async {
    if (currentUser == null) throw const FormatException("No User Found");
    var result = await Future.wait([
      _userRepository.updateUserInfo(currentUser!, address: address),
      Future.delayed(Durations.medium4)
    ]).then((value) => value.first);
    if (result.isLeft) {
      currentUser!.userInfo.address = address;
      notifyListeners();
      if (context.mounted) {
        ToastService.toastSuccess(context, "Update User Address Success");
      }
    }
    if (result.isRight) {
      debugPrint(result.right);
      if (context.mounted) {
        ToastService.toastError(context, "Update User Address Failed");
      }
    }
  }

  Future updateUserInfo(
    BuildContext context, {
    String? firstName,
    String? lastName,
    String? phone,
    DateTime? dob,
    String? address,
    String? ciCard,
  }) async {
    if (currentUser == null) throw const FormatException("No User Found");
    var result = await Future.wait([
      _userRepository.updateUserInfo(
        currentUser!,
        firstName: firstName,
        lastName: lastName,
        phone: phone,
        dob: dob,
        address: address,
        ciCard: ciCard,
      ),
      Future.delayed(Durations.medium4)
    ]).then((value) => value.first);
    if (result.isLeft) {
      currentUser!.userInfo.address = address;
      notifyListeners();
      if (context.mounted) {
        ToastService.toastSuccess(context, "Update User Infomation Success");
      }
    }
    if (result.isRight) {
      debugPrint(result.right);
      if (context.mounted) {
        ToastService.toastError(context, "Update User Infomation Failed");
      }
    }
  }

  ResultObject submitCardInfomationResult = ResultObject();
  Future<void> submitCardInfomation(
    BuildContext context, {
    required CroppedFile front,
    required CroppedFile back,
    required bool isUpdate,
  }) async {
    submitCardInfomationResult.reset(isLoading: true);
    notifyListeners();
    // call Api
    Either<dynamic, String?> result = await Future.wait([
      _userRepository.verifyCiCard(front: front, back: back),
      Future.delayed(Durations.extralong4)
    ]).then((value) => value.first);
    // dealing with result
    if (result.isLeft) {
      notifyListeners();
      if (context.mounted) {
        submitCardInfomationResult.isSuccess = true;
        submitCardInfomationResult.message =
            "Verify Card Information Successful";
        submitCardInfomationResult.result = result.left;
        ToastService.toastSuccess(context, submitCardInfomationResult.message);
        Navigator.pushReplacementNamed(
          context,
          MyAccountScreen.routeName,
          arguments: result.left,
        );
      }
    }
    if (result.isRight) {
      debugPrint(result.right);
      if (context.mounted) {
        submitCardInfomationResult.isError = true;
        submitCardInfomationResult.error =
            result.right ?? "Verify Card Infomation Failed";
        ToastService.toastError(context, submitCardInfomationResult.error);
      }
    }
    submitCardInfomationResult.isLoading = false;
    notifyListeners();
  }

  /// Secret Keys
  String get publicKey => currentSecretKey.result?["publicKey"] ?? "";
  String get privateKey => (currentSecretKey.result?["privateKey"] ?? "")
      .replaceAll(RegExp(r"(?<=.{6})."), "*");

  final currentSecretKey = ResultObject();
  Future<void> getSecretKey() async {
    currentSecretKey.reset(isLoading: true);
    notifyListeners();
    // call Api
    Either<Map, String?> result = await Future.wait([
      _userRepository.showSecretKey(),
      Future.delayed(Durations.extralong4)
    ]).then((value) => value.first);
    // dealing with result
    result.either((left) {
      currentSecretKey.isSuccess = true;
      currentSecretKey.message = "Get Secret Key Success";
      currentSecretKey.result = result.left;
      // ToastService.toastSuccess(context, currentSecretKey.message);
      // Navigator.pushReplacementNamed(context, MyAccountScreen.routeName,
      // arguments: result.left)
    }, (right) {
      currentSecretKey.isError = true;
      currentSecretKey.error = result.right ?? "Get Secret Key Success Failed";
      debugPrint(currentSecretKey.error);
    });
    currentSecretKey.isLoading = false;

    notifyListeners();
  }

  Future<bool> regenerateSecretKey(BuildContext context) async {
    if (currentUser!.userInfo.ciCardImage.isEmpty && !kDebugMode) {
      ToastService.toastError(context,
          "Please verify your card information before generating secretkey");
      return false;
    }
    currentSecretKey.reset(isLoading: true);
    notifyListeners();
    // call Api
    Either<dynamic, String?> result = await Future.wait([
      _userRepository.regenerateSecretKey(),
      Future.delayed(Durations.extralong4)
    ]).then((value) => value.first);
    // dealing with result
    if (context.mounted) {
      result.either((left) {
        currentSecretKey.isSuccess = true;
        currentSecretKey.message = "Generation Success!";
        ToastService.toastSuccess(context, currentSecretKey.message);
      }, (right) {
        currentSecretKey.isError = true;
        currentSecretKey.error = result.right ?? "Generation Failed!";
        ToastService.toastError(context, currentSecretKey.error);
      });
    }
    currentSecretKey.isLoading = false;
    notifyListeners();
    return currentSecretKey.isSuccess;
  }

  final paymentHistoryResult = ResultObject<List<PaymentHistory>>();

  Future<void> d(BuildContext context) async {
    paymentHistoryResult.reset(isLoading: true);
    notifyListeners();
    // call Api
    Either<ResponseObj<List<PaymentHistory>>, String?> result =
        await Future.wait([
      _userRepository.getPaymentHistory(),
      Future.delayed(Durations.extralong4)
    ]).then((value) => value.first);
    // dealing with result
    if (context.mounted) {
      result.either((left) {
        paymentHistoryResult.isSuccess = true;
        paymentHistoryResult.message = left.message;
        paymentHistoryResult.result = left.data;
      }, (right) {
        paymentHistoryResult.isError = true;
        paymentHistoryResult.error = right ?? "";
      });
    }
    paymentHistoryResult.isLoading = false;
    notifyListeners();
  }
}
