import 'package:blockchain_mobile/1_controllers/services/dio_exception_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/cookie_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:blockchain_mobile/models/Product.dart';
import 'package:cookie_jar/cookie_jar.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';

class ProductRepository {
  static final ProductRepository _productRepository =
      ProductRepository._internal();
  late final Dio dio;
  late final CookieJar cookieJar;

  final DioExceptionService _dioExceptionService = DioExceptionService();
  factory ProductRepository() {
    return _productRepository;
  }
  ProductRepository._internal() {
    dio = DioService().dio;
    cookieJar = CookieService().cookieJar;
  }

  Future<Either<List<Product>, String?>> getProductList({
    String? search,
    bool? asc,
    int? min,
    int? max,
    List<int>? categoryIds,
    List<int>? typeGoldIds,
    List<int>? ratings,
  }) async {
    try {
      final query = <String, dynamic>{
        // page: params?.page,
        'search': search ?? "",
        'asc': asc,
        "price_gte": min,
        "price_lte": max,
        'category': categoryIds,
        'typeGold': typeGoldIds,
        "reviews": ratings,
      };
      final result = await dio.get("/api/auth/product/show-list-product",
          queryParameters: query);

      if (result.statusCode == 200) {
        // Parse the response JSON and map it to a list of Product objects
        final List<Product> products = (result.data['data'] as List)
            .map((json) => Product.fromJson(json))
            .toList();
        return Left(products);
      } else {
        // Handle non-200 status codes if necessary
        return Right(
            "Failed to fetch products. Status code: ${result.statusCode}");
      }
    } catch (e) {
      // Handle DioException or any other errors
      if (e is DioException) {
        var message = await _dioExceptionService.handleDioException(e);
        return Right(message);
      } else {
        return Right("An error occurred: $e");
      }
    }
  }

  Future<Either<Product, String?>> getById(int id) async {
    try {
      final result = await dio.get(
        "/api/auth/product/get-product-by-id/$id",
      );

      if (result.statusCode == 200) {
        // Parse the response JSON and map it to a list of Product objects
        final json = result.data['data'];
        final Product product = Product.fromJson(json);
        return Left(product);
      } else {
        // Handle non-200 status codes if necessary
        return Right(
            "Failed to fetch product $id. Status code: ${result.statusCode}");
      }
    } catch (e) {
      // Handle DioException or any other errors
      if (e is DioException) {
        var message = await _dioExceptionService.handleDioException(e);
        return Right(message);
      } else {
        return Right("An error occurred: $e");
      }
    }
  }

  Future<Either<List<Product>, String?>> getProductList24kGold() async {
    try {
      final query = {
        'typeGoldName': "24k gold",
      };
      final result = await dio.get(
          "/api/auth/get-all-product-by-type-gold-name",
          queryParameters: query);

      if (result.statusCode == 200) {
        // Parse the response JSON and map it to a list of Product objects
        final List<Product> products = (result.data['data'] as List)
            .map((json) => Product.fromJson(json))
            .toList();
        return Left(products);
      } else {
        // Handle non-200 status codes if necessary
        return Right(
            "Failed to fetch products. Status code: ${result.statusCode}");
      }
    } catch (e) {
      // Handle DioException or any other errors
      if (e is DioException) {
        var message = await _dioExceptionService.handleDioException(e);
        return Right(message);
      } else {
        return Right("An error occurred: $e");
      }
    }
  }
}
