import 'package:blockchain_mobile/1_controllers/services/dio_exception_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/dtos/response_object.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../models/discount.dart';
import '../../models/user_discount.dart';

class DiscountRepository {
  final DioService _dioService = DioService();
  final DioExceptionService _dioExceptionService = DioExceptionService();

  Dio get _dio => _dioService.dio;
  factory DiscountRepository() {
    return DiscountRepository._internal();
  }
  DiscountRepository._internal();
  Future<Either<ResponseObj<Discount>, String?>> getDiscountCodeByCode(
      String code) async {
    final prefs = await SharedPreferences.getInstance();
    try {
      // Call Api
      final userId = prefs.getString(STORAGE_USERID);
      final result = await _dio
          .get('/api/auth/get-discount-code-by-code', queryParameters: {
        'code': code,
        'userId': userId,
      });
      // final result = FAKEOBJ();
      debugPrint(result.data.toString());
      final json = result.data['data'];
      Discount discount = Discount.fromJson(json);
      return Left(ResponseObj.fromJson(result.data, discount));
    } on DioException catch (dioError) {
      final message = await _dioExceptionService.handleDioException(dioError);
      return Right(message);
    } catch (e, st) {
      logger.e(e, stackTrace: st);
      return Right(e.toString());
    }
  }

  Future<Either<ResponseObj<List<UserDiscount>>, String?>>
      getAllUserDiscountByUserId(bool expire, [bool showAll = false]) async {
    final prefs = await SharedPreferences.getInstance();
    try {
      final userId = prefs.getString(STORAGE_USERID);
      final result = await _dio.get(
        '/api/auth/get-all-discount-code-of-user-by-userId',
        queryParameters: {
          'userId': userId,
          'expire': expire,
        },
      );
      final jsonList = result.data['data'] as List<dynamic>;
      if (jsonList.isEmpty) return const Right("You don't have any discount");
      List<UserDiscount> listCodeOfUser = jsonList.map((e) {
        return UserDiscount.fromJson(e);
      }).toList();
      if (!showAll) {
        listCodeOfUser = listCodeOfUser.where((e) => e.available == true).toList();
      }
      return Left(
        ResponseObj.fromJson(
          result.data,
          listCodeOfUser,
        ),
      );
    } on DioException catch (dioError) {
      final message = await _dioExceptionService.handleDioException(dioError);
      return Right(message);
    } catch (e, st) {
      debugPrint('$e \n $st');
      return Right(e.toString());
    }
  }

  Future<Either<ResponseObj<UserDiscount>, String?>> getUserDiscountById(
      int discountCodeOfUserId) async {
    final prefs = await SharedPreferences.getInstance();
    try {
      final userId = prefs.getString(STORAGE_USERID);
      final result = await _dio.get(
        '/api/auth/get-discount-code-of-user-by-id',
        queryParameters: {'userId': userId},
      );
      final json = result.data['data'];
      UserDiscount codeOfUser = UserDiscount.fromJson(json);
      return Left(ResponseObj.fromJson(result.data, codeOfUser));
    } on DioException catch (dioError) {
      final message = await _dioExceptionService.handleDioException(dioError);
      return Right(message);
    } catch (e, st) {
      debugPrint('$e \n $st');
      return Right(e.toString());
    }
  }

  Future<Either<ResponseObj<UserDiscount>, String?>> createUserDiscount(
      int discountId) async {
    final prefs = await SharedPreferences.getInstance();
    try {
      final userId = prefs.getString(STORAGE_USERID);
      final result = await _dio.post(
        '/api/auth/create-discount-code-of-user',
        queryParameters: {'discountId': discountId, 'userId': userId},
      );
      final json = result.data['data'];
      UserDiscount checkCreate = UserDiscount.fromJson(json);
      return Left(ResponseObj.fromJson(result.data, checkCreate));
    } on DioException catch (dioError) {
      final message = await _dioExceptionService.handleDioException(dioError);
      return Right(message);
    } catch (e, st) {
      debugPrint('$e \n $st');
      return Right(e.toString());
    }
  }

  Future<Either<bool, String?>> deleteDiscountCode(
      int userId, int discountCodeOfUserId) async {
    try {
      final result = await _dio.delete(
        '/api/auth/delete-discount-code-of-user/$discountCodeOfUserId',
        queryParameters: {'userId': userId},
      );
      final json = result.data['data'];
      bool checkDelete = json as bool;
      return Left(checkDelete);
    } on DioException catch (dioError) {
      final message = await _dioExceptionService.handleDioException(dioError);
      return Right(message);
    } catch (e, st) {
      debugPrint('$e \n $st');
      return Right(e.toString());
    }
  }

  Future<Either<ResponseObj<List<Discount>>, String?>> getAllDiscount() async {
    try {
      final result = await _dio.get(
        '/api/auth/get-all-discount-code',
      );
      final jsonList = result.data['data'] as List<dynamic>;
      if (jsonList.isEmpty) return const Right("You don't have any discount");
      List<Discount> listCodeOfUser = jsonList.map((e) {
        return Discount.fromJson(e);
      }).toList();
      return Left(
        ResponseObj.fromJson(
          result.data,
          listCodeOfUser,
        ),
      );
    } on DioException catch (dioError) {
      final message = await _dioExceptionService.handleDioException(dioError);
      return Right(message);
    } catch (e, st) {
      debugPrint('$e \n $st');
      return Right(e.toString());
    }
  }
}
