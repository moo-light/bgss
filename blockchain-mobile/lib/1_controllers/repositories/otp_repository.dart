import 'package:blockchain_mobile/1_controllers/services/dio_exception_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

class OtpRepository {
  final DioService _dioService = DioService();
  final DioExceptionService _dioExceptionService = DioExceptionService();

  Dio get _dio => _dioService.dio;
  factory OtpRepository() {
    return OtpRepository._internal();
  }
  OtpRepository._internal();

  Future<Either<String, String?>> verifyOrder(String otp, int orderId) async {
    try {
      // Call Api
      final result =
          await _dio.get('/api/auth/verification-order/${await userInfoId}/$otp/$orderId');
      // final result = FAKEOBJ();
      return Left(result.data?["message"]);
    } on DioException catch (dioError) {
      var message = await _dioExceptionService.handleDioException(dioError);
      if (!kDebugMode && (message?.length ?? 0) > 100) {
        message = "Verify order Unsuccessful";
      }
      print(message);
      return Right(message);
    } catch (e, st) {
      logger.e(e, stackTrace: st);
      return Right(e.toString());
    }
  }

  Future<Either<String, String?>> resentOtp(int orderId) async {
    try {
      // Call Api
      final result = await _dio.get('/api/auth/resent-otp/$orderId');
      return Left(result.data["message"]);
    } on DioException catch (dioError) {
      final message = await _dioExceptionService.handleDioException(dioError);
      return Right(
        message,
      );
    } catch (e, st) {
      logger.e(e, stackTrace: st);
      return Right(e.toString());
    }
  }

  Future<Either<String, String?>> resendTransactionOtp(
      int transactionId) async {
    try {
      // Call Api
      final result = await _dio.get('/resent-otp-transaction/$transactionId');
      return Left(result.data["message"]);
    } on DioException catch (dioError) {
      final message = await _dioExceptionService.handleDioException(dioError);
      return Right(
        message,
      );
    } catch (e, st) {
      logger.e(e, stackTrace: st);
      return Right(e.toString());
    }
  }

  Future<Either<String, String?>> verifyTransaction(
      String otp, int transactionId) async {
    try {
      // Call Api
      final result = await _dio
          .get('/api/auth/verification-transaction/$otp/$transactionId');
      // final result = FAKEOBJ();
      return Left(result.data?["message"]);
    } on DioException catch (dioError) {
      var message = await _dioExceptionService.handleDioException(dioError);
      if (!kDebugMode && (message?.length ?? 0) > 100) {
        message = "Verify transaction Unsuccessful";
      }
      print(message);
      return Right(message);
    } catch (e, st) {
      logger.e(e, stackTrace: st);
      return Right(e.toString());
    }
  }

  Future<Either<String, String?>> resendWithdrawOtp(id) async {
    try {
      // Call Api
      final result = await _dio.get('/api/auth/resent-otp-withdraw/$id');
      return Left(result.data["message"]);
    } on DioException catch (dioError) {
      final message = await _dioExceptionService.handleDioException(dioError);
      return Right(
        message,
      );
    } catch (e, st) {
      logger.e(e, stackTrace: st);
      return Right(e.toString());
    }
  }

  Future<String> get userInfoId async => await SharedPreferences.getInstance()
      .then((value) => value.getString(STORAGE_USERID)!);
  Future<Either<String, String?>> verifyWithdraw(
      String otp, int withdrawId) async {
    try {
      // Call Api
      final result = await _dio.post(
          '/api/auth/request-withdraw-gold/verify/${await userInfoId}/$otp/$withdrawId');
      // final result = FAKEOBJ();
      return Left(result.data?["message"]);
    } on DioException catch (dioError) {
      var message = await _dioExceptionService.handleDioException(dioError);
      if (!kDebugMode && (message?.length ?? 0) > 100) {
        message = "Verify transaction Unsuccessful";
      }
      return Right(message);
    } catch (e, st) {
      logger.e(e, stackTrace: st);
      return Right(e.toString());
    }
  }
}
