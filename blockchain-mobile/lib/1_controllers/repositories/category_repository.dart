import 'package:blockchain_mobile/1_controllers/services/dio_exception_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:blockchain_mobile/models/gold_type.dart';
import 'package:blockchain_mobile/models/product_category.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';

class CategoryRepository {
  final DioService _dioService = DioService();
  final DioExceptionService _dioExceptionService = DioExceptionService();

  Dio get _dio => _dioService.dio;
  factory CategoryRepository() {
    return CategoryRepository._internal();
  }
  CategoryRepository._internal();
  Future<Either<List<ProductCategory>, String?>> getCategories() async {
    try {
      final result = await _dio.get("/api/auth/get-all-category");
      final jsonList = (result.data['data'] ?? []) as List;
      final List<ProductCategory> categories =
          (jsonList).map((json) => ProductCategory.fromJson(json)).toList();
      return Left(categories);
    } on DioException catch (e) {
      return Right(await _dioExceptionService.handleDioException(e));
    } catch (e) {
      return Right(e.toString());
    }
  }

  Future<Either<List<GoldType>, String?>> getTypeGolds() async {
    try {
      final result = await _dio.get("/api/auth/get-all-type-gold");
      final jsonList = (result.data['data'] ?? []) as List;
      final List<GoldType> typeGolds =
          (jsonList).map((json) => GoldType.fromJson(json)).toList();
      return Left(typeGolds);
    } on DioException catch (e) {
      return Right(await _dioExceptionService.handleDioException(e));
    } catch (e) {
      return Right(e.toString());
    }
  }
}
