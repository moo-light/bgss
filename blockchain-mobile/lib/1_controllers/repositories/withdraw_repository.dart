// ignore_for_file: constant_identifier_names

import 'package:blockchain_mobile/1_controllers/services/dio_exception_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/dtos/response_object.dart';
import 'package:blockchain_mobile/models/withdraw_gold.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';
import 'package:shared_preferences/shared_preferences.dart';

class WithdrawRepository {
  final DioService _dioService = DioService();
  final DioExceptionService _dioExceptionService = DioExceptionService();

  Dio get _dio => _dioService.dio;

  factory WithdrawRepository() {
    return WithdrawRepository._internal();
  }

  WithdrawRepository._internal();

  Future<String> get userInfoId => SharedPreferences.getInstance()
      .then((value) => value.getString(STORAGE_USERID)!);
  Future<Either<List<WithdrawGold>, String?>> getWithdrawList() async {
    try {
      final response = await _dio.get("api/auth/withdraw-list");
      final List<WithdrawGold> withdrawals = List<WithdrawGold>.from(
          response.data['data'].map((json) => WithdrawGold.fromJson(json)));
      if (withdrawals.isEmpty) return const Right("Withdraw List is empty");
      return Left(withdrawals);
    } on DioException catch (e) {
      final errorMessage = await _dioExceptionService.handleDioException(e);
      return Right(errorMessage);
    }
  }

  Future<Either<List<WithdrawGold>, String?>> getUserWithdrawList() async {
    try {
      final String userId = await userInfoId;
      final response =
          await _dio.get("api/auth/withdraw-list-userinfo/$userId");
      final List<WithdrawGold> withdrawals = List<WithdrawGold>.from(
          response.data['data'].map((json) => WithdrawGold.fromJson(json)));
      if (withdrawals.isEmpty) return const Right("Withdraw List is empty");
      return Left(withdrawals);
    } on DioException catch (e) {
      final errorMessage = await _dioExceptionService.handleDioException(e);
      return Right(errorMessage);
    }
  }

  Future<Either<ResponseObj<WithdrawGold>, String?>> cancelWithdraw(
      int withdrawalId, String reason) async {
    try {
      final response = await _dio.post(
        "api/auth/cancel/$withdrawalId",
        queryParameters: {'reason': reason},
      );
      final WithdrawGold withdrawal =
          WithdrawGold.fromJson(response.data["data"]);
      return Left(ResponseObj.fromJson(response.data, withdrawal));
    } on DioException catch (e) {
      final errorMessage = await _dioExceptionService.handleDioException(e);
      return Right(errorMessage);
    }
  }

  Future<Either<ResponseObj<WithdrawGold>, String?>> handleWithdrawal(
      {required int withdrawalId,
      required String actionStr,
      String withdrawQrCode = ""}) async {
    try {
      final response = await _dio.patch(
        "api/auth/handle-withdraw-gold/$withdrawalId",
        queryParameters: {
          'actionStr': actionStr,
          'withdrawQrCode': withdrawQrCode
        },
      );
      final WithdrawGold withdrawal =
          WithdrawGold.fromJson(response.data["data"]);
      return Left(ResponseObj.fromJson(response.data, withdrawal));
    } on DioException catch (e) {
      final String? errorMessage =
          await _dioExceptionService.handleDioException(e);
      return Right(errorMessage);
    } on Error catch (e, st) {
      logger.e('handleWithdrawal', error: e, stackTrace: st);
      return const Right("Error handleWithdrawal");
    }
  }

  Future<Either<ResponseObj, String?>> recievedWithdraw({
    required int withdrawalId,
  }) async {
    try {
      final response = await _dio.put(
        "api/auth/user-confirm-withdraw/$withdrawalId",
      );
      return Left(ResponseObj.fromJson(
        response.data,
      ));
    } on DioException catch (e) {
      final String? errorMessage =
          await _dioExceptionService.handleDioException(e);
      return Right(errorMessage);
    } on Error catch (e, st) {
      logger.e('recievedWithdraw', error: e, stackTrace: st);
      return const Right("Error recievedWithdraw");
    }
  }

  Future<Either<ResponseObj<WithdrawGold>, String?>> requestWithdrawGold(
      double weightToWithdraw,
      String unit,
      int? productId,
      String withdrawRequest) async {
    try {
      final response = await _dio.post(
        "api/auth/request-withdraw-gold/${await userInfoId}",
        queryParameters: {
          'weightToWithdraw': weightToWithdraw,
          'unit': unit,
          'withdrawRequirement': withdrawRequest,
          'productId': productId
        },
      );
      final WithdrawGold withdrawal =
          WithdrawGold.fromJson(response.data["data"]);
      return Left(ResponseObj.fromJson(response.data, withdrawal));
    } on DioException catch (e) {
      final String? errorMessage =
          await _dioExceptionService.handleDioException(e);
      return Right(errorMessage);
    }
  }
}

enum WithdrawAction {
  CONFIRM_WITHDRAWAL,
  COMPLETE_WITHDRAWAL,
}
