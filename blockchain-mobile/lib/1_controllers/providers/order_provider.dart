import 'dart:async';

import 'package:blockchain_mobile/1_controllers/repositories/order_repository.dart';
import 'package:blockchain_mobile/1_controllers/repositories/payment_repository.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/2_screens/order_detail/order_detail_screen.dart';
import 'package:blockchain_mobile/2_screens/otp/otp_screen.dart';
import 'package:blockchain_mobile/4_helper/qrcode_helper.dart';
import 'package:blockchain_mobile/models/Cart.dart';
import 'package:blockchain_mobile/models/dtos/result_object.dart';
import 'package:blockchain_mobile/models/order.dart';
import 'package:blockchain_mobile/models/withdraw_gold.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:vnpay_client/vnpay_client.dart';

import '../../models/user_discount.dart';

class OrderProvider extends ChangeNotifier {
  final OrderRepository _orderRepository = OrderRepository();
  final PaymentRepository _paymentRepository = PaymentRepository();
  //for payment
  String? paymentDone;
  String? paymentMessage;
  int newPayment = 0;
  Order? currentOrder;
  bool currentOrderisLoading = true;
  //for order list
  String getListError = "";
  bool getListLoading = true;
  List<Order> orders = [];

  //for order detail
  Uri? url;
  //constants

  UserDiscount? currentDiscount;

  void prepareOrder(BuildContext context) {
    paymentDone = null;
    currentOrder = null;
    url = null;
  }

  ResultObject<Order> createOrderResult = ResultObject();
  bool isConsignment = false;
  set consignment(bool value) => {isConsignment = value, notifyListeners()};
  Future createOrder(
    BuildContext context, {
    required List<Cart> carts,
  }) async {
    if (carts.isEmpty) throw Exception("you haven't buy anything");
    createOrderResult.reset(isLoading: true);
    notifyListeners();
    final result = await _orderRepository.createOrder(
        carts, currentDiscount?.discount.code, isConsignment);
    result.either(
        (success) => {
              createOrderResult.isSuccess = true,
              createOrderResult.message = success.message,
              createOrderResult.result = success.data,
              currentDiscount = null,
              Navigator.popUntil(context, (route) => route.isFirst),
              Navigator.pushNamed(
                context,
                OtpScreen.routeName,
                arguments: success.data.id,
              )
            },
        (error) => {
              createOrderResult.isError = true,
              createOrderResult.error = error ?? "Create Order Failed!",
            });
    isConsignment = false;
    createOrderResult.isLoading = false;
    if (context.mounted) {
      createOrderResult.handleResult(context);
    }
    notifyListeners();
  }

  Future<void> confirmOrder(
      BuildContext context, Order order // Staff and Admin only
      ) async {
    final result = await _orderRepository.getDetailOrderByQrCode(order.qrCode);
    if (!context.mounted) return;
    if (result.isLeft) {
      ToastService.toastSuccess(context, "Confirm Success!");
    }
    if (result.isRight) {
      ToastService.toastError(
          context, kDebugMode ? result.right : "Can't confirm order!");
    }
  }

  void selectDiscount(UserDiscount? userDiscount) {
    currentDiscount = userDiscount;
    notifyListeners();
  }

  Future<void> createPayment(BuildContext context,
      {required double amount}) async {
    paymentDone = null;
    notifyListeners();
    handleParams(params) {
      paymentDone = params['vnp_ResponseCode'];
      paymentMessage = _VnpResponseCode.codes[paymentDone];
      notifyListeners();
    }

    var result = await _paymentRepository.paymentOrder(amount);
    if (result.isLeft) {
      String url = result.left;
      if (context.mounted) {
        await showVNPayScreen(
          context,
          paymentUrl: url,
          onPaymentSuccess: handleParams,
          onPaymentError: handleParams,
        );
        notifyListeners();
      }
    }
  }

  bool isOrderListObtained = false;

  Future getOrderList() async {
    getListError = "";
    getListLoading = true;
    orders = [];
    notifyListeners();
    final result = await _orderRepository.getOrderList();
    result.fold<dynamic>(
      (left) async {
        orders = left;
      },
      (right) {
        getListError = right!;
      },
    );
    getListLoading = false;
    notifyListeners();
  }

  Future<bool> getDetailOrder(BuildContext context,
      {String? code, int? orderId}) async {
    if (code != null && orderId != null) {
      throw "only input 1 of code or orderId!";
    }
    currentOrder = null;
    currentOrderisLoading = true;
    notifyListeners();
    Either<Order?, String?> result;
    if (code != null) {
      var split = code.split("!");
      if (split.first == EQrType.Withdraw.symbol) {
        ToastService.toastError(context, WithdrawGold.errorQrScanMessage);
        return false;
      }
      if (split.first == EQrType.Order.symbol) {
        result = await _orderRepository.getDetailOrderByQrCode(split.last);
      } else {
        ToastService.toastError(context, "Orcode Type not found");
        return false;
      }
    } else {
      result = await _orderRepository.getDetailOrder(orderId!);
    }
    currentOrderisLoading = false;
    result.either((left) {
      currentOrder = left;
      Navigator.pushNamed(context, OrderDetailScreen.routeName);
    },
        (right) => {
              ToastService.toastError(
                  context, kDebugMode ? right : "Can't get Order")
            });
    notifyListeners();
    return result.isLeft;
  }

  Future<void> setRecievedOrder(BuildContext context, Order order,
      {bool forCustomer = false}) async {
    currentOrderisLoading = true;
    notifyListeners();
    final result =
        await _orderRepository.updateStatusRecieve(order.id, forCustomer);

    currentOrderisLoading = false;
    result.either((left) {
      currentOrder = left;
      ToastService.toastSuccess(context, "Order Recieved!");
    }, (right) {
      ToastService.toastError(
          context, kDebugMode ? right : "Order Update Failed!");
      currentOrder = order;
    });
    notifyListeners();
  }
  // Future<List<Order>> _fetchOrderList() async {
  //   getListError = "";
  //   getListLoading = true;
  //   isOrderListObtained = false;
  //   notifyListeners();
  //   List<Order> ordersList = [];
  //   final result = await _orderRepository.getOrderList();
  //   result.fold(
  //     (list) {
  //       ordersList = list;
  //       orders = list;
  //       isOrderListObtained = true;
  //     },
  //     (error) {
  //       getListError = getListError;
  //     },
  //   );
  //   getListLoading = false;
  //   notifyListeners();
  //   return ordersList;
  // }

  void refreshOrderList() {
    getOrderList();
  }
}

class _VnpResponseCode {
  static const Map<String, String> codes = {
    "00": "Transaction successful",
    "07":
        "Transaction suspected of fraud. Please contact customer support for assistance.",
    "09":
        "Transaction unsuccessful. Your card/account is not registered for Internet Banking service. Please contact your bank.",
    "10":
        "Transaction unsuccessful. Please check the information entered and try again.",
    "11":
        "Transaction unsuccessful. Payment waiting period has expired. Please retry the transaction.",
    "12":
        "Transaction unsuccessful. Your card/account is locked. Please contact your bank.",
    "13":
        "Transaction unsuccessful. Please enter the correct transaction authentication password (OTP) and try again.",
    "24": "Transaction canceled by customer.",
    "51": "Transaction unsuccessful. Insufficient balance in your account.",
    "65":
        "Transaction unsuccessful. Your account has exceeded the daily transaction limit.",
    "75":
        "Payment bank is currently under maintenance. Please try again later.",
    "79":
        "Transaction unsuccessful. Please ensure you enter the payment password correctly and try again.",
    "99": "Unknown error occurred.",
  };
  static const String defaultMessage = "Unknown error occurred";
}
