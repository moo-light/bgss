import 'package:blockchain_mobile/1_controllers/services/dio_exception_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';

import '../../models/post_category.dart';

class PostCategoryRepository {
  final DioService _dioService = DioService();
  final DioExceptionService _dioExceptionService = DioExceptionService();

  Dio get _dio => _dioService.dio;
  factory PostCategoryRepository() {
    return PostCategoryRepository._internal();
  }
  PostCategoryRepository._internal();

  Future<Either<List<PostCategory>, String?>> getPostCategoryList() async {
    try {
      final result = await _dioService.dio.get(
        "/api/auth/show-all-category-post",
      );

      if (result.statusCode == 200) {
        // Parse the response JSON and map it to a list of PostCategory objects
        final List<PostCategory> products = (result.data['data'] as List)
            .map((json) => PostCategory.fromJson(json))
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
