import 'dart:async';

import 'package:blockchain_mobile/1_controllers/repositories/otp_repository.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/models/dtos/result_object.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';

class OtpProvider extends ChangeNotifier {
  final OtpRepository _otpRepository = OtpRepository();
  ResultObject<bool> verifyOtpOrderResult = ResultObject();

  Future<ResultObject<bool>> verifyOtpOrder({
    required String otp,
    required int id,
    required OtpEType type,
  }) async {
    currentOrderId = id;
    verifyOtpOrderResult.reset(isLoading: true);
    notifyListeners();
    Either<String, String?> result = const Right("");

    Future task = _getVerifyTask(type, otp, id);

    await Future.wait([
      task.then((value) => result = value),
      Future.delayed(Durations.extralong4)
    ]);
    verifyOtpOrderResult.isLoading = false;
    result.fold(
        (left) => {
              verifyOtpOrderResult.isSuccess = true,
              verifyOtpOrderResult.message = left,
            },
        (right) => {
              verifyOtpOrderResult.isError = true,
              verifyOtpOrderResult.error = right ?? "Verify OTP failed",
            });
    notifyListeners();
    return verifyOtpOrderResult;
  }

  int currentOrderId = 0;
  ResultObject<bool> resendOtpResult = ResultObject();

  Future<ResultObject<bool>> resendOtp(context,
      {required int id, required OtpEType type}) async {
    currentOrderId = id;
    verifyOtpOrderResult.reset();
    verifyOtpOrderResult.isLoading = true;
    resendOtpResult.reset(isLoading: true);
    notifyListeners();
    late Either<String, String?> result;
    final Future task = _getResendTask(type, id);

    await Future.wait([
      task.then((value) => result = value),
      Future.delayed(kDebugMode ? Duration.zero : Durations.extralong4 * 2)
    ]);
    result.either(
        (left) => {
              resendOtpResult.isSuccess = true,
              resendOtpResult.message = left,
              ToastService.toastSuccess(
                context,
                resendOtpResult.message,
              ),
            },
        (right) => {
              resendOtpResult.isError = true,
              resendOtpResult.error = right ?? "Resend OTP failed",
              ToastService.toastError(context, resendOtpResult.error)
            });

    resendOtpResult.isLoading = false;
    verifyOtpOrderResult.isLoading = false;
    notifyListeners();
    return resendOtpResult;
  }

  Future _getResendTask(OtpEType type, id) {
    switch (type) {
      case OtpEType.ORDER:
        return _otpRepository.resentOtp(id);
      case OtpEType.TRANSACTION:
        return _otpRepository.resendTransactionOtp(id);
      case OtpEType.WITHDRAW:
        return _otpRepository.resendWithdrawOtp(id);
    }
  }

  Future _getVerifyTask(OtpEType type, String otp, int id) {
    switch (type) {
      case OtpEType.ORDER:
        return _otpRepository.verifyOrder(otp, id);
      case OtpEType.TRANSACTION:
        return _otpRepository.verifyTransaction(otp, id);
      case OtpEType.WITHDRAW:
        return _otpRepository.verifyWithdraw(otp, id);
    }
  }
}

enum OtpEType { ORDER, TRANSACTION, WITHDRAW }
