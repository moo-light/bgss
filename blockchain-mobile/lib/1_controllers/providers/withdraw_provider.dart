import 'package:blockchain_mobile/1_controllers/repositories/withdraw_repository.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/2_screens/otp/otp_screen.dart';
import 'package:blockchain_mobile/2_screens/withdraw_detail/withdraw_detail_screen.dart';
import 'package:blockchain_mobile/4_helper/qrcode_helper.dart';
import 'package:blockchain_mobile/models/Product.dart';
import 'package:blockchain_mobile/models/enums/gold_unit.dart';
import 'package:blockchain_mobile/models/order.dart';
import 'package:blockchain_mobile/models/withdraw_gold.dart';
import 'package:flutter/cupertino.dart';

import '../../models/dtos/result_object.dart';

class WithdrawProvider extends ChangeNotifier {
  final WithdrawRepository _withDrawRepository = WithdrawRepository();
  ResultObject<List<WithdrawGold>> getUserWithdrawResult = ResultObject();

  Future<void> getUserWithdraw([int? id]) async {
    getUserWithdrawResult.reset(isLoading: true);
    notifyListeners();
    var result = await _withDrawRepository.getUserWithdrawList();
    result.either(
        (left) => {
              getUserWithdrawResult.isSuccess = true,
              getUserWithdrawResult.result = left,
              getUserWithdrawResult.message = "Request withdraw Success!",
              if (id != null)
                setCurrentWithdraw(
                    left.firstWhere((element) => element.id == id))
            },
        (right) => {
              getUserWithdrawResult.isError = true,
              getUserWithdrawResult.error = right!,
            });
    getUserWithdrawResult.isLoading = false;
    notifyListeners();
  }

  Future<void> getWithdraw([int? id]) async {
    getUserWithdrawResult.reset(isLoading: true);
    notifyListeners();
    var result = await _withDrawRepository.getWithdrawList();
    result.either(
        (left) => {
              getUserWithdrawResult.isSuccess = true,
              getUserWithdrawResult.result = left,
              getUserWithdrawResult.message = "Request withdraw Success!",
              if (id != null)
                setCurrentWithdraw(
                    left.firstWhere((element) => element.id == id))
            },
        (right) => {
              getUserWithdrawResult.isError = true,
              getUserWithdrawResult.error = right!,
            });
    getUserWithdrawResult.isLoading = false;
    notifyListeners();
  }

  ResultObject<WithdrawGold> currentWithdraw = ResultObject();

  void setCurrentWithdraw(WithdrawGold withdraw) {
    currentWithdraw.reset();
    currentWithdraw.isLoading = false;
    currentWithdraw.isSuccess = true;
    currentWithdraw.result = withdraw;
    notifyListeners();
  }

  Future<void> cancelWithdraw(
      BuildContext context, WithdrawGold withdraw, String reason) async {
    currentWithdraw.reset();
    currentWithdraw.isLoading = true;
    final result =
        await _withDrawRepository.cancelWithdraw(withdraw.id, reason);
    result.either((left) {
      currentWithdraw.result = left.data;
      currentWithdraw.isSuccess = true;
      currentWithdraw.message = left.message;
      ToastService.toastSuccess(context, left.message);
    }, (right) {
      ToastService.toastError(context, right);
    });
    notifyListeners();
  }

  Future<void> confirmWithdraw(BuildContext context, int id) async {
    final result = await _withDrawRepository.handleWithdrawal(
      withdrawalId: 0,
      actionStr: WithdrawAction.CONFIRM_WITHDRAWAL.name,
    );
    result.either((left) {
      currentWithdraw.result = left.data;
      currentWithdraw.isSuccess = true;
      currentWithdraw.message = left.message;
      ToastService.toastSuccess(context, left.message);
    }, (right) {
      ToastService.toastError(context, right);
    });
    notifyListeners();
  }

  Future<void> completeWithdraw(BuildContext context, String qrCode) async {
    var split = qrCode.split("!");
    if (split.first == EQrType.Order.symbol) {
      ToastService.toastError(context, Order.errorQrScanMessage);
      return;
    }
    if (split.first != EQrType.Withdraw.symbol) {
      ToastService.toastError(context, "Orcode Type not found");
      return;
    }
    final result = await _withDrawRepository.handleWithdrawal(
      withdrawalId: 0,
      actionStr: WithdrawAction.COMPLETE_WITHDRAWAL.name,
      withdrawQrCode: split.last,
    );

    result.either((left) {
      currentWithdraw.result = left.data;
      currentWithdraw.isSuccess = true;
      currentWithdraw.message = left.message;
      ToastService.toastSuccess(context, left.message);
      Navigator.pushNamed(context, WithdrawDetailScreen.routeName);
    }, (right) {
      ToastService.toastError(context, right);
    });
    notifyListeners();
  }

  Future<void> recievedWithdraw(BuildContext context, int id) async {
    final result = await _withDrawRepository.recievedWithdraw(
      withdrawalId: id,
    );
    result.either(
      (left) {
        // currentWithdraw.result = left.data;
        currentWithdraw.isSuccess = true;
        currentWithdraw.message = left.message;
        ToastService.toastSuccess(context, left.message);
      },
      (right) {
        ToastService.toastError(context, right);
      },
    );
    getUserWithdraw(currentWithdraw.result!.id);
    notifyListeners();
  }

  final createWithdrawOrderResult = ResultObject();
  Future<void> createWithdrawOrder(BuildContext context,
      {required double weightToWithdraw,
      required GoldUnit unit,
      Product? product,
      required String action}) async {
    currentWithdraw.reset();
    createWithdrawOrderResult.reset(isLoading: true);
    final result = await _withDrawRepository.requestWithdrawGold(
      weightToWithdraw,
      unit.name,
      product?.id,
      action,
    );
    result.either((left) {
      currentWithdraw.result = left.data;
      createWithdrawOrderResult.isSuccess = true;
      createWithdrawOrderResult.message = left.message;
      ToastService.toastSuccess(context, left.message);
      Navigator.popUntil(context, (route) => route.isFirst);
      Navigator.pushNamed(context, OtpScreen.routeNameWithdraw,
          arguments: left.data.id);
      notifyListeners();
    }, (right) {
      createWithdrawOrderResult.isError = true;
      createWithdrawOrderResult.error = right!;
      currentWithdraw.isLoading = false;
      ToastService.toastError(context, right);
      notifyListeners();
    });
    createWithdrawOrderResult.isLoading = false;
    notifyListeners();
  }
}
