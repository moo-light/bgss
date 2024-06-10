import 'package:blockchain_mobile/1_controllers/services/dio_exception_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/dtos/response_object.dart';
import 'package:blockchain_mobile/models/review.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

class ReviewRepository {
  final DioService _dioService = DioService();
  final DioExceptionService _dioExceptionService = DioExceptionService();

  Dio get _dio => _dioService.dio;

  factory ReviewRepository() {
    return ReviewRepository._internal();
  }

  ReviewRepository._internal();

  Future<Either<ResponseObj<List<Review>>, String?>> getAllReview(
      int productId) async {
    try {
      final result =
          await _dio.get("/api/auth/product/$productId/get-all-review");
      final json = result.data['data'] as List;
      final reviews = json.map((e) => Review.fromJson(e)).toList();
      return Left(ResponseObj.fromJson(result.data, reviews));
    } on DioException catch (e) {
      var dioErr = await _dioExceptionService.handleDioException(e);
      return Right(dioErr);
    }
  }

  Future<Either<bool?, String?>> checkReview(int productId) async {
    try {
      final uid = await SharedPreferences.getInstance()
          .then((pref) => pref.getString(STORAGE_USERID));
      final result = await _dio
          .get("/api/auth/product/$productId/check-review?userId=$uid");
      return Left(result.data["data"]);
    } on DioException catch (e) {
      var dioErr = await _dioExceptionService.handleDioException(e);
      return Right(dioErr);
    } catch (e) {
      return Right(e.toString());
    }
  }

  Future<Either<ResponseObj<Review>, String?>> getMyReview(
      int productId) async {
    try {
      final uid = await SharedPreferences.getInstance()
          .then((pref) => pref.getString(STORAGE_USERID));
      final result =
          await _dio.get("/api/auth/product/$productId/get-user-review/$uid");
      return Left(ResponseObj.fromJson(
        result.data,
        Review.fromJson(result.data["data"]),
      ));
    } on DioException catch (e) {
      var dioErr = await _dioExceptionService.handleDioException(e);
      return Right(dioErr);
    } catch (e) {
      debugPrint(e.toString());
      return Right(e.toString());
    }
  }

  Future<Either<ResponseObj<Review>, String?>> createReview(
    int productId, {
    required int numOfReviews,
    required String content,
    MultipartFile? image,
  }) async {
    try {
      final uid = await SharedPreferences.getInstance()
          .then((pref) => pref.getString(STORAGE_USERID));
      var data = FormData.fromMap({
        'imgReview': image,
        'numOfReviews': numOfReviews,
        'content': content
      });
      final result = await _dio.post(
          "/api/auth/create-review?userId=$uid&productId=$productId",
          data: data);
      return Left(
        ResponseObj.fromJson(
          result.data,
          Review.fromJson(result.data["data"]),
        ),
      );
    } on DioException catch (e) {
      var dioErr = await _dioExceptionService.handleDioException(e);
      return Right(dioErr);
    } catch (e) {
      return Right(e.toString());
    }
  }

  Future<Either<ResponseObj<Review>, String?>> updateReview(int productId,
      {required int numOfReviews,
      required String content,
      MultipartFile? image,
      required bool isRemove}) async {
    try {
      final uid = await SharedPreferences.getInstance()
          .then((pref) => pref.getString(STORAGE_USERID));
      var data = FormData.fromMap({
        'imgReview': image,
        'numOfReviews': numOfReviews,
        'content': content,
        'removeImg': isRemove
      });
      final result = await _dio.put(
          "/api/auth/update-review?userId=$uid&productId=$productId",
          data: data);
      return Left(
        ResponseObj.fromJson(result.data, Review.fromJson(result.data["data"])),
      );
    } on DioException catch (e) {
      var dioErr = await _dioExceptionService.handleDioException(e);
      return Right(dioErr);
    }
  }

  Future<Either<dynamic, String?>> deleteReview({required int reviewId}) async {
    try {
      final uid = await SharedPreferences.getInstance()
          .then((pref) => pref.getString(STORAGE_USERID));
      final result =
          await _dio.put("/api/auth/delete-review/$reviewId?userId=$uid");
      return Left(result.data);
    } on DioException catch (e) {
      var dioErr = await _dioExceptionService.handleDioException(e);
      return Right(dioErr);
    }
  }
}
