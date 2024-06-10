import 'package:blockchain_mobile/1_controllers/repositories/discount_repository.dart';
import 'package:blockchain_mobile/models/discount.dart';
import 'package:flutter/cupertino.dart';

import '../../models/dtos/result_object.dart';
import '../../models/user_discount.dart';

class DiscountProvider extends ChangeNotifier {
  final DiscountRepository _discountRepository = DiscountRepository();
  ResultObject<List<UserDiscount>> getUserDiscountResult = ResultObject();

  Future<void> getUserDiscount(
      {bool expire = false, bool showAll = false}) async {
    getUserDiscountResult.reset(isLoading: true);
    notifyListeners();
    var result =
        await _discountRepository.getAllUserDiscountByUserId(expire, showAll);
    result.either(
        (left) => {
              getUserDiscountResult.isSuccess = true,
              getUserDiscountResult.result = left.data,
              getUserDiscountResult.message = "Request Discount Success!",
            },
        (right) => {
              getUserDiscountResult.isError = true,
              getUserDiscountResult.error = right!,
            });
    getUserDiscountResult.isLoading = false;
    notifyListeners();
  }

  final getDiscountCodeResult = ResultObject<Discount>();
  Future<void> getDiscountCode(String code) async {
    getDiscountCodeResult.reset();
    getDiscountCodeResult.isLoading = true;
    notifyListeners();
    final result = await _discountRepository.getDiscountCodeByCode(code);
    result.either(
        (left) => {
              getDiscountCodeResult.isSuccess = true,
              getDiscountCodeResult.result = left.data,
              getDiscountCodeResult.message = left.message,
            },
        (right) => {
              getDiscountCodeResult.isError = true,
              getDiscountCodeResult.error = right!,
            });
    getDiscountCodeResult.isLoading = false;
    notifyListeners();
  }

  final currentUserDiscount = ResultObject<UserDiscount>();
  Future<void> getUserDiscountById(int id) async {
    currentUserDiscount.reset();
    currentUserDiscount.isLoading = true;
    notifyListeners();
    final result = await _discountRepository.getUserDiscountById(id);
    result.either(
        (left) => {
              currentUserDiscount.isSuccess = true,
              currentUserDiscount.result = left.data,
              currentUserDiscount.message = left.message,
            },
        (right) => {
              currentUserDiscount.isError = true,
              currentUserDiscount.error = right!,
            });
    getUserDiscountResult.isLoading = false;
    notifyListeners();
  }

  Future<void> addUserDiscount(BuildContext context, int id) async {
    currentUserDiscount.reset();
    currentUserDiscount.isLoading = true;
    notifyListeners();
    final result = await _discountRepository.createUserDiscount(id);
    result.either(
        (left) => {
              currentUserDiscount.isSuccess = true,
              currentUserDiscount.result = left.data,
              currentUserDiscount.message = left.message,
              getDiscountCodeResult.reset()
            },
        (right) => {
              currentUserDiscount.isError = true,
              currentUserDiscount.error = right!,
            });
    getUserDiscountResult.isLoading = false;
    currentUserDiscount.handleResult(context, success: "Saved discount!");
    notifyListeners();
  }

  ResultObject<List<Discount>> getAllDiscountResult = ResultObject();

  void getAllDiscount(BuildContext context) async {
    getAllDiscountResult.reset(isLoading: true);
    notifyListeners();
    var result = await _discountRepository.getAllDiscount();
    result.either(
        (left) => {
              getAllDiscountResult.isSuccess = true,
              getAllDiscountResult.result = left.data,
              getAllDiscountResult.message = "Request Discount Success!",
            },
        (right) => {
              getAllDiscountResult.isError = true,
              getAllDiscountResult.error = right!,
            });
    getAllDiscountResult.isLoading = false;
    notifyListeners();
  }
}
