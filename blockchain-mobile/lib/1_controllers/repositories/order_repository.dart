import 'package:blockchain_mobile/1_controllers/services/dio_exception_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/Cart.dart';
import 'package:blockchain_mobile/models/dtos/response_object.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/cupertino.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../models/order.dart';

class OrderRepository {
  final DioService _dioService = DioService();
  final DioExceptionService _dioExceptionService = DioExceptionService();

  Dio get _dio => _dioService.dio;
  factory OrderRepository() {
    return OrderRepository._internal();
  }
  OrderRepository._internal();

  Future<Either<ResponseObj<Order>, String?>> createOrder(
      List<Cart> carts, String? discount, bool isConsignment) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final uid = prefs.getString(STORAGE_USERID);
      final data = {
        "listCartId": carts.map<int>((e) => e.id).toList(), //[1,2,3]
        'discountCode': discount,
        'isConsignment': isConsignment
      };
      // Call Api
      final result = await _dio.post('/api/auth/create-order/$uid', data: data);
      // final result = FAKEOBJ();
      if (result.data["status"] == "OK") {
        final json = result.data['data'];
        Order order = Order.fromJson(json);
        return Left(ResponseObj.fromJson(result.data, order));
      } else {
        return Right(result.data["message"] ?? 'Unknown error');
      }
    } on DioException catch (dioError) {
      final message = await _dioExceptionService.handleDioException(dioError);
      return Right(message);
    } catch (e, st) {
      debugPrint(e.toString());
      return Right(e.toString());
    }
  }

  Future<Either<Order, String?>> updateStatusRecieve(
      int orderId, bool forCustomer) async {
    try {
      // Call Api
      var updateStatusRecieved = '/api/auth/update-status-received/$orderId';
      var updateUserConfirm = '/api/auth/user-confirm/$orderId';
      final result = await _dio
          .put(forCustomer ? updateUserConfirm : updateStatusRecieved);
      // final result = FAKEOBJ();
      if (result.data["status"] == "OK") {
        debugPrint(result.data.toString());
        final json = result.data['data'];
        Order orders = Order.fromJson(json);
        return Left(orders);
      } else {
        return Right(result.data["message"] ?? 'Unknown error');
      }
    } on DioException catch (dioError) {
      final message = await _dioExceptionService.handleDioException(dioError);
      return Right(message);
    } catch (e, st) {
      logger.e(e, stackTrace: st);
      return Right(e.toString());
    }
  }

  Future<Either<Order, String?>> getDetailOrder(int orderId) async {
    try {
      // Call Api
      final result = await _dio.get('/api/auth/get-order-by-id/$orderId');
      // final result = FAKEOBJ();
      if (result.data["status"] == "OK") {
        debugPrint(result.data.toString());
        final json = result.data['data'];
        Order orders = Order.fromJson(json);
        return Left(orders);
      } else {
        return Right(result.data["message"] ?? 'Unknown error');
      }
    } on DioException catch (dioError) {
      final message = await _dioExceptionService.handleDioException(dioError);
      return Right(message);
    } catch (e, st) {
      logger.e(e, stackTrace: st);
      return Right(e.toString());
    }
  }

  Future<Either<Order, String?>> getDetailOrderByQrCode(String code) async {
    try {
      // Call Api
      final result =
          await _dio.get('/api/auth/search-order-by-qr_code?qr_Code=$code');
      // final result = FAKEOBJ();
      if (result.data["status"] == "OK") {
        debugPrint(result.data.toString());
        final json = result.data['data'];
        Order orders = Order.fromJson(json);
        return Left(orders);
      } else {
        return Right(result.data["message"] ?? 'Unknown error');
      }
    } on DioException catch (dioError) {
      final message = await _dioExceptionService.handleDioException(dioError);
      return Right(message);
    } catch (e, st) {
      logger.e(e, stackTrace: st);
      return Right(e.toString());
    }
  }

  Future<Either<List<Order>, String?>> getOrderList() async {
    try {
      // Call Api
      final uid = await SharedPreferences.getInstance().then(
        (value) => value.getString(STORAGE_USERID),
      );
      final queryParameters = {
        'userId': uid,
      };
      final result = await _dio.get(
        '/api/auth/get-order-list',
        queryParameters: queryParameters,
      );
      // final result = FAKEOBJ();
      if (result.data["status"] == "OK") {
        final json = result.data['data'] as List<dynamic>;
        List<Order> orders = json.map((e) => Order.fromJson(e)).toList();
        orders.sort((a, b) => b.id.compareTo(a.id));
        if (orders.isEmpty) return const Right("Order List is empty");
        return Left(orders);
      } else {
        return Right(result.data["message"] ?? 'Unknown error');
      }
    } on DioException catch (dioError) {
      final message = await _dioExceptionService.handleDioException(dioError);
      return Right(message);
    } catch (e, st) {
      logger.e(e, stackTrace: st);
      return Right(e.toString());
    }
  }
}
