import 'package:blockchain_mobile/1_controllers/repositories/rate_repository.dart';
import 'package:blockchain_mobile/models/dtos/response_object.dart';
import 'package:blockchain_mobile/models/dtos/result_object.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/material.dart';

import '../../models/rate.dart';

class RateProvider extends ChangeNotifier {
  final RateRepository _rateRepository = RateRepository();

  final getRateListResult = ResultObject<List<Rate>>();
  Future<ResultObject<List<Rate>>> getRateList(int productid) async {
    getRateListResult.reset(isLoading: true);
    notifyListeners();

    // Prepare the image for uploading
    Either<ResponseObj<List<Rate>>, String?> result =
        await _rateRepository.getAllRate(productid);
    if (result.isLeft) {
      getRateListResult.isSuccess = true;
      getRateListResult.result = result.left.data;
      getRateListResult.message = result.left.message;
    }
    if (result.isRight) {
      getRateListResult.isError = true;
      getRateListResult.error = result.right ?? "Cannot get rate";
      getRateListResult.result = null;
    }
    getRateListResult.isLoading = false;
    notifyListeners();
    return getRateListResult;
  }

  final currentRate = ResultObject<Rate>();
  Future<ResultObject<Rate>> submitRate(
    String content,
    int rateId,
    bool isUpdate,
    bool isRemove, [
    String? imagePath,
  ]) async {
    currentRate.reset(isLoading: true);
    notifyListeners();

    // Prepare the image for uploading
    MultipartFile? imageFile = imagePath != null
        ? await MultipartFile.fromFile(imagePath, filename: "rateImage.jpg")
        : null;
    Either<ResponseObj<Rate>, String?> result;
    if (isUpdate) {
      result = await _rateRepository.updateRate(
        rateId,
        content: content,
        image: imageFile,
        isRemove: isRemove,
      );
    } else {
      result = await _rateRepository.createRate(
        rateId,
        content: content,
        image: imageFile,
      );
    }
    if (result.isLeft) {
      currentRate.isSuccess = true;
      currentRate.message = result.left.message;
      currentRate.result = result.left.data;
      if (isUpdate) {
        final rate = getRateListResult.result
            ?.firstWhere((element) => element.id == currentRate.result?.id);
        getRateListResult.result?.remove(rate);
      }
      getRateListResult.result?.add(currentRate.result!);
    }
    if (result.isRight) {
      currentRate.isError = true;
      currentRate.error = result.right ?? "Cannot rate";
      currentRate.result = null;
    }
    currentRate.isLoading = false;
    notifyListeners();
    return currentRate;
  }

  Future<ResultObject<Rate>> getCurrentRate(int rateId) async {
    currentRate.reset(isLoading: true);
    notifyListeners();

    // Prepare the image for uploading
    Either<ResponseObj<Rate>, String?> result =
        await _rateRepository.getMyRate(rateId);
    if (result.isLeft) {
      currentRate.isSuccess = true;
      currentRate.result = result.left.data;
      currentRate.message = result.left.message;
    }
    if (result.isRight) {
      currentRate.isError = true;
      currentRate.error = result.right ?? "Cannot get rate";
      currentRate.result = null;
    }
    currentRate.isLoading = false;
    notifyListeners();
    return currentRate;
  }
}
