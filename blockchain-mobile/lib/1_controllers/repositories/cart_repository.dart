import 'package:blockchain_mobile/1_controllers/services/dio_exception_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/Cart.dart';
import 'package:blockchain_mobile/server-API/api-constant-cart.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';
import 'package:shared_preferences/shared_preferences.dart';

class CartRepository {
  final DioService _dioService = DioService();
  final DioExceptionService _dioExceptionService = DioExceptionService();

  Dio get _dio => _dioService.dio;
  factory CartRepository() {
    return CartRepository._internal();
  }
  CartRepository._internal();
  Future<Either<List<Cart>, Map<String, dynamic>>> getCartLists() async {
    try {
      final uid = await SharedPreferences.getInstance()
          .then((value) => value.getString(STORAGE_USERID));
      //Call Api
      final result = await _dio.get(
        '/api/auth/get-list-cart-fellow-userid',
        queryParameters: {
          'userId': uid,
        },
      );
      final jsonData = result.data['data'] as List<dynamic>;
      List<Cart> carts = jsonData.map((json) => Cart.fromJson(json)).toList();
      return Left(carts);
    } catch (e) {
      if (e is DioException) {
        final message = await _dioExceptionService.handleDioException(
          e,
          msg404: "No products in cart yet",
        );
        return Right({'message': message, 'status': e.response?.statusCode});
      }
      return Right({'message': e.toString()});
    }
  }

  Future<Either<Cart, String?>> addProductToCart(
      {required int productId, required quantity, required price}) async {
    try {
      final uid = await SharedPreferences.getInstance()
          .then((value) => value.getString(STORAGE_USERID));
      final data = {
        "productId": productId,
        "userId": uid,
        "quantity": quantity,
        "price": price
      };
      //Call Api
      final result = await _dio.post(
        '/api/auth/add-product-to-cart',
        data: data,
      );
      if (result.data["status"] == "OK") {
        final json = result.data['data'];
        Cart cartItem = Cart.fromJson(json);
        return Left(cartItem);
      } else {
        return Right(result.data["message"]);
      }
    } catch (e) {
      if (e is DioException) {
        return Right(await _dioExceptionService.handleDioException(e));
      }
      return Right(e.toString());
    }
  }

  Future<Either<String, String?>> removeCartId(int cartItemId) async {
    try {
      //Call Api
      final result = await _dio.delete(
        '$removeProductFromCart/$cartItemId',
        // queryParameters: {'cartItemId': cartItemId},
      );
      return Left(result.data['message']);
    } catch (e) {
      if (e is DioException) {
        return Right(await _dioExceptionService.handleDioException(e));
      }
      return Right(e.toString());
    }
  }

  // Future<Either<String, String?>> increaseQuantity(int cartItemId) async {
  //   try {
  //     //Call Api
  //     final result = await _dio.put(
  //       '$updateQuantityProductInCart/$cartItemId',
  //     );
  //     return Left(result.data['message']);
  //   } catch (e) {
  //     if (e is DioException) {
  //       _dioExceptionService.handleDioException(e);
  //     }
  //     return Right(e.toString());
  //   }
  // }

  // Future<Either<String, String?>> decreaseQuantity(int cartItemId) async {
  //   try {
  //     //Call Api
  //     final result = await _dio.put(
  //       '$updateQuantityProductInCart/$cartItemId',
  //       // queryParameters: {'cartItemId': cartItemId},
  //     );
  //     return Left(result.data['message']);
  //   } catch (e) {
  //     if (e is DioException) {
  //       _dioExceptionService.handleDioException(e);
  //     }
  //     return Right(e.toString());
  //   }
  // }

  Future<Either<int, String?>> increaseQuantity(
      int cartItemId, int quantity) async {
    try {
      final result = await _dio.put(
        '$updateQuantityProductInCart/$cartItemId',
        queryParameters: {'quantity': quantity},
      );
      if (result.statusCode == 200) {
        // Giả định rằng server trả về số lượng mới trong body dưới key 'newQuantity'
        int newQuantity = result.data['data']['quantity'];
        return Left(newQuantity);
      } else {
        return Right(result.data['message']);
      }
    } catch (e) {
      if (e is DioException) {
        return Right(await _dioExceptionService.handleDioException(e));
      }
      return Right(e.toString());
    }
  }

  Future<Either<int, String?>> decreaseQuantity(
      int cartItemId, int quantity) async {
    try {
      final result = await _dio.put(
        '$updateQuantityProductInCart/$cartItemId',
        queryParameters: {'quantity': quantity},
      );
      if (result.statusCode == 200) {
        // Giả định rằng server trả về số lượng mới trong body dưới key 'newQuantity'
        int newQuantity = result.data['data']['quantity'];
        return Left(newQuantity);
      } else {
        return Right(result.data['message']);
      }
    } catch (e) {
      if (e is DioException) {
        return Right(await _dioExceptionService.handleDioException(e));
      }
      return Right(e.toString());
    }
  }
}
