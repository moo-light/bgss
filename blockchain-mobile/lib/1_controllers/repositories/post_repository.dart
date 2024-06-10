import 'package:blockchain_mobile/1_controllers/services/dio_exception_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:blockchain_mobile/models/post.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';

class PostRepository {
  final DioService _dioService = DioService();
  final DioExceptionService _dioExceptionService = DioExceptionService();

  Dio get _dio => _dioService.dio;

  factory PostRepository() {
    return PostRepository._internal();
  }

  PostRepository._internal();

  Future<Either<List<Post>, String>> getAllPostWithIsPinned() async {
    try {
      final response = await _dio.get('/api/auth/get-all-post-with-pinned');
      if (response.statusCode == 200) {
        final List<dynamic> data = response.data;
        final List<Post> postDTOList =
            data.map((json) => Post.fromJson(json)).toList();
        return Left(postDTOList);
      } else {
        return const Right('Failed to fetch posts');
      }
    } catch (e) {
      if (e is DioException) {
        _dioExceptionService.handleDioException(e);
      }
      return Right(e.toString());
    }
  }

  Future<Either<List<Post>, String>> getAllPost(
      {String search = "",
      String categoryId = "",
      String showAll = "DEFAULT",
      DateTime? fromDate,
      DateTime? toDate,
      bool asc = false}) async {
    try {
      final response =
          await _dio.get('/api/auth/get-all-post', queryParameters: {
        'search': search,
        'categoryId': categoryId,
        'showAll': showAll,
        'fromDate': fromDate?.toString(),
        'toDate': toDate?.toString(),
        'asc': asc.toString()
      });
      if (response.statusCode == 200) {
        final List<dynamic> data = response.data['data'];
        print(data);
        final List<Post> postDTOList =
            data.map((json) => Post.fromJson(json)).toList();
        return Left(postDTOList);
      } else {
        return const Right('Failed to fetch posts');
      }
    } catch (e,st) {
      if (e is DioException) {
        _dioExceptionService.handleDioException(e);
      }
      return Right(e.toString());
    }
  }

  Future<Either<Post, String>> getPostById(int postId) async {
    try {
      final response = await _dio.get('/api/auth/get-post-by-id/$postId');
      if (response.statusCode == 200) {
        final dynamic data = response.data["data"];
        final Post postDTO = Post.fromJson(data);
        return Left(postDTO);
      } else {
        return const Right('Failed to fetch post');
      }
    } catch (e) {
      if (e is DioException) {
        _dioExceptionService.handleDioException(e);
      }
      return Right(e.toString());
    }
  }
}
