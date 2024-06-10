import 'package:blockchain_mobile/1_controllers/repositories/review_repository.dart';
import 'package:blockchain_mobile/models/dtos/response_object.dart';
import 'package:blockchain_mobile/models/dtos/result_object.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/material.dart';

import '../../models/review.dart';

class ReviewProvider extends ChangeNotifier {
  final ReviewRepository _reviewRepository = ReviewRepository();

  var canReviewResult = ResultObject();
  Future<void> canReview(int id) async {
    canReviewResult.reset(isLoading: true);
    notifyListeners();
    Either<bool?, String?> result = await _reviewRepository.checkReview(id);
    if (result.isLeft) {
      canReviewResult.isSuccess = true;
      canReviewResult.result = result.left;
    }
    if (result.isRight) {
      canReviewResult.isError = true;
      canReviewResult.result = null;
      canReviewResult.error = result.right ?? "Cannot review";
    }
    canReviewResult.isLoading = false;
    notifyListeners();
  }

  final getReviewListResult = ResultObject<List<Review>>();
  Future<ResultObject<List<Review>>> getReviewList(int productid) async {
    getReviewListResult.reset(isLoading: true);
    notifyListeners();

    // Prepare the image for uploading
    Either<ResponseObj<List<Review>>, String?> result =
        await _reviewRepository.getAllReview(productid);
    if (result.isLeft) {
      getReviewListResult.isSuccess = true;
      getReviewListResult.result = result.left.data;
      getReviewListResult.message = result.left.message;
    }
    if (result.isRight) {
      getReviewListResult.isError = true;
      getReviewListResult.error = result.right ?? "Cannot get review";
      getReviewListResult.result = null;
    }
    getReviewListResult.isLoading = false;
    notifyListeners();
    return getReviewListResult;
  }

  final currentReview = ResultObject<Review>();
  Future<ResultObject<Review>> submitReview(
    int numOfReviews,
    String content,
    int productid,
    bool isUpdate,
    bool isRemove, [
    String? imagePath,
  ]) async {
    currentReview.reset(isLoading: true);
    notifyListeners();

    // Prepare the image for uploading
    MultipartFile? imageFile = imagePath != null
        ? await MultipartFile.fromFile(imagePath, filename: "reviewImage.jpg")
        : null;
    Either<ResponseObj<Review>, String?> result;
    if (isUpdate) {
      result = await _reviewRepository.updateReview(
        productid,
        content: content,
        numOfReviews: numOfReviews,
        image: imageFile,
        isRemove: isRemove,
      );
    } else {
      result = await _reviewRepository.createReview(
        productid,
        content: content,
        numOfReviews: numOfReviews,
        image: imageFile,
      );
    }
    if (result.isLeft) {
      currentReview.isSuccess = true;
      currentReview.message = result.left.message;
      currentReview.result = result.left.data;
      if (isUpdate) {
        final review = getReviewListResult.result
            ?.firstWhere((element) => element.id == currentReview.result?.id);
        getReviewListResult.result?.remove(review);
      }
      getReviewListResult.result?.add(currentReview.result!);
    }
    if (result.isRight) {
      currentReview.isError = true;
      currentReview.error = result.right ?? "Cannot review";
      currentReview.result = null;
    }
    currentReview.isLoading = false;
    notifyListeners();
    return currentReview;
  }

  Future<ResultObject<Review>> getCurrentReview(int productid) async {
    currentReview.reset(isLoading: true);
    notifyListeners();

    // Prepare the image for uploading
    Either<ResponseObj<Review>, String?> result =
        await _reviewRepository.getMyReview(productid);
    if (result.isLeft) {
      currentReview.isSuccess = true;
      currentReview.result = result.left.data;
      currentReview.message = result.left.message;
    }
    if (result.isRight) {
      currentReview.isError = true;
      currentReview.error = result.right ?? "Cannot get review";
      currentReview.result = null;
    }
    currentReview.isLoading = false;
    notifyListeners();
    return currentReview;
  }
}
