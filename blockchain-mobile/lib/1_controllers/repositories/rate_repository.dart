import 'package:blockchain_mobile/1_controllers/services/dio_exception_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/dtos/response_object.dart';
import 'package:blockchain_mobile/models/rate.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

class RateRepository {
  final DioService _dioService = DioService();
  final DioExceptionService _dioExceptionService = DioExceptionService();

  Dio get _dio => _dioService.dio;

  factory RateRepository() {
    return RateRepository._internal();
  }

  RateRepository._internal();

  Future<Either<ResponseObj<List<Rate>>, String?>> getAllRate(
      int postId) async {
    try {
      final result = await _dio
          .get("/api/auth/show-all-rate?postId=$postId&showHiding=false");
      final json = result.data['data'] as List;
      final rates = json.map((e) => Rate.fromJson(e)).toList();
      return Left(ResponseObj.fromJson(result.data, rates));
    } on DioException catch (e) {
      var dioErr = await _dioExceptionService.handleDioException(e);
      return Right(dioErr);
    }
  }

  Future<Either<bool?, String?>> checkRate(int postId) async {
    try {
      final uid = await SharedPreferences.getInstance()
          .then((pref) => pref.getString(STORAGE_USERID));
      final result =
          await _dio.get("/api/auth/product/$postId/check-rate?userId=$uid");
      return Left(result.data["data"]);
    } on DioException catch (e) {
      var dioErr = await _dioExceptionService.handleDioException(e);
      return Right(dioErr);
    } catch (e) {
      return Right(e.toString());
    }
  }

  Future<Either<ResponseObj<Rate>, String?>> getMyRate(int rateId) async {
    try {
      final result = await _dio.get("/api/auth/get-rate-by-id/$rateId");
      return Left(ResponseObj.fromJson(
        result.data,
        Rate.fromJson(result.data["data"]),
      ));
    } on DioException catch (e) {
      var dioErr = await _dioExceptionService.handleDioException(e);
      return Right(dioErr);
    } catch (e) {
      debugPrint(e.toString());
      return Right(e.toString());
    }
  }

  Future<Either<ResponseObj<Rate>, String?>> createRate(
    int postId, {
    required String content,
    MultipartFile? image,
  }) async {
    try {
      final uid = await SharedPreferences.getInstance()
          .then((pref) => pref.getString(STORAGE_USERID));
      var data = FormData.fromMap({'imgRate': image, 'content': content});
      final result = await _dio
          .post("/api/auth/create-rate?userId=$uid&postId=$postId", data: data);
      return Left(
        ResponseObj.fromJson(
          result.data,
          Rate.fromJson(result.data["data"]),
        ),
      );
    } on DioException catch (e) {
      var dioErr = await _dioExceptionService.handleDioException(e);
      return Right(dioErr);
    } catch (e) {
      return Right(e.toString());
    }
  }

  Future<Either<ResponseObj<Rate>, String?>> updateRate(int rateId,
      {required String content,
      MultipartFile? image,
      required bool isRemove}) async {
    try {
      var data = FormData.fromMap(
          {'imgRate': image, 'content': content, 'removeImg': isRemove});
      final result = await _dio
          .put("/api/auth/update-rate/$rateId", data: data);
      return Left(
        ResponseObj.fromJson(result.data, Rate.fromJson(result.data["data"])),
      );
    } on DioException catch (e) {
      var dioErr = await _dioExceptionService.handleDioException(e);
      return Right(dioErr);
    }
  }

  Future<Either<dynamic, String?>> deleteRate({required int rateId}) async {
    try {
      final uid = await SharedPreferences.getInstance()
          .then((pref) => pref.getString(STORAGE_USERID));
      final result =
          await _dio.put("/api/auth/delete-rate/$rateId?userId=$uid");
      return Left(result.data);
    } on DioException catch (e) {
      var dioErr = await _dioExceptionService.handleDioException(e);
      return Right(dioErr);
    }
  }
}
