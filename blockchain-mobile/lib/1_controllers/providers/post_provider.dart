import 'package:blockchain_mobile/1_controllers/repositories/post_category_repository.dart';
import 'package:blockchain_mobile/1_controllers/repositories/post_repository.dart';
import 'package:blockchain_mobile/models/post.dart';
import 'package:blockchain_mobile/models/post_category.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/foundation.dart';

class PostProvider extends ChangeNotifier {
  final PostRepository _postRepository = PostRepository();
  final PostCategoryRepository _categoryRepository = PostCategoryRepository();
  bool isLoading = true;

  String? error;

  //data
  List<Post> postList = [];
  List<PostCategory> categories = [];
  Post? postDetail;

  Future<void> getPostList({
    String search = "",
    String categoryId = "",
  }) async {
    isLoading = true;
    error = null;
    notifyListeners();
    Either<dynamic, String?> result = await _postRepository.getAllPost(
      search: search,
      categoryId: categoryId,
    );
    if (result.isLeft) {
      postList = result.left;
    }
    if (result.isRight) {
      error = result.right;
    }
    isLoading = false;
    notifyListeners();
  }

  Future<void> getPostById(int id) async {
    isLoading = true;
    postDetail = null;
    error = null;
    notifyListeners();
    Either<Post, String> result = await _postRepository.getPostById(id);
    if (result.isLeft) {
      postDetail = result.left;
    }
    if (result.isRight) {
      error = result.right;
    }
    isLoading = false;
    notifyListeners();
  }

  Future getCategories(context) async {
    final result = await _categoryRepository.getPostCategoryList();
    if (result.isLeft) {
      categories = result.left;
    }
    if (result.isRight) {}
    notifyListeners();
  }
}
