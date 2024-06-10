import 'package:blockchain_mobile/1_controllers/services/dio_exception_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';
import 'package:shared_preferences/shared_preferences.dart';

class PaymentRepository {
  final DioService _dioService = DioService();
  final DioExceptionService _dioExceptionService = DioExceptionService();

  Dio get _dio => _dioService.dio;
  factory PaymentRepository() {
    return PaymentRepository._internal();
  }
  PaymentRepository._internal();

  Future<Either<String, String?>> paymentOrder(double price) async {
    try {
      final pref = await SharedPreferences.getInstance();
      final uid = pref.getString(STORAGE_USERID);
      //Call Api
      var queryParameters = {
        'price': price.round(),
        'userInfoId': uid,
        'username': 'user-$uid',
        // 'host': _dio.options.baseUrl.replaceFirst(RegExp(r"/$"), ''),
        'host': Uri(
          scheme: 'BGOldSS',
          host: "www.example.com",
        ),
      };

      final result = await _dio.get(
        '/api/auth/payment',
        queryParameters: queryParameters,
      );
      assert(result.data is String);
      return Left(result.data);
    } catch (e) {
      if (e is DioException) {
        _dioExceptionService.handleDioException(e);
      }
      return Right(e.toString());
    }
  }

  Future<Either<String, String?>> checkValidPay(
      Map<String, dynamic> params) async {
    try {
      final result = await _dio.get(
        '/api/auth/check-valid-pay',
        queryParameters: params,
      );
      return Left(result.data['message']);
    } catch (e) {
      if (e is DioException) {
        _dioExceptionService.handleDioException(e);
      }
      return Right(e.toString());
    }
  }
}
