// ignore_for_file: constant_identifier_names

import 'dart:typed_data';

import 'package:blockchain_mobile/1_controllers/services/dio_exception_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/contract.dart';
import 'package:blockchain_mobile/models/dtos/response_object.dart';
import 'package:blockchain_mobile/models/gold_transaction.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/cupertino.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../models/enums/gold_unit.dart';

class TransactionRepository {
  final DioService _dioService = DioService();
  final DioExceptionService _dioExceptionService = DioExceptionService();

  Dio get _dio => _dioService.dio;

  factory TransactionRepository() {
    return TransactionRepository._internal();
  }

  TransactionRepository._internal();

  Future<String> get userInfoId => SharedPreferences.getInstance()
      .then((value) => value.getString(STORAGE_USERID)!);

  Future<Either<ResponseObj<GoldTransaction>, String?>> processUserTransaction({
    required double quantityInOz,
    required double pricePerOz,
    required TransactionType type,
    required GoldUnit goldUnit,
  }) async {
    try {
      var userId = await userInfoId;
      final response = await _dio.put(
        "api/auth/transactions/$userId",
        queryParameters: {
          'quantityInOz': quantityInOz,
          'pricePerOz': pricePerOz,
          'type': type.name,
          'goldUnit': goldUnit.name,
        },
      );
      var goldTransaction = GoldTransaction.fromJson(response.data['data']);
      return Left(ResponseObj.fromJson(response.data, goldTransaction));
    } on DioException catch (e) {
      final String? errorMessage =
          await _dioExceptionService.handleDioException(e);
      return Right(errorMessage);
    } catch (e, st) {
      debugPrintStack(stackTrace: st, label: 'processUserTransaction');
      return Right(e.toString());
    }
  }

  Future<Either<ResponseObj<GoldTransaction>, String?>> updateSignature({
    required int transactionId,
    required String publickey,
    required Uint8List signature,
  }) async {
    try {
      FormData formData = FormData();
      formData.files.add(
        MapEntry(
          'signature',
          MultipartFile.fromBytes(signature,
              filename: "signature-$transactionId"),
        ),
      );
      final response = await _dio.put(
        "/api/auth/transactions-update/signature-image/mobile/$transactionId?publicKey=$publickey",
        data: formData,
      );
      var goldTransaction = GoldTransaction.fromJson(response.data['data']);
      return Left(ResponseObj.fromJson(response.data, goldTransaction));
    } on DioException catch (e) {
      final String? errorMessage =
          await _dioExceptionService.handleDioException(e);
      return Right(errorMessage);
    } catch (e, st) {
      debugPrintStack(stackTrace: st, label: 'processUserTransaction');
      return Right(e.toString());
    }
  }

  Future<Either<ResponseObj<GoldTransaction>, String?>> signatureTransaction({
    required int transactionId,
    required Uint8List signature,
    required String secretKey,
    bool? isUpdate,
  }) async {
    try {
      FormData formData = FormData();

      formData.files.add(
        MapEntry(
          isUpdate == true ? "signature" : 'signatureImage',
          MultipartFile.fromBytes(
            signature,
            filename: "signature-$transactionId",
          ),
        ),
      );
      Response response;
      if (isUpdate == true) {
        response = await _dio.put(
          "api/auth/transactions-update/signature-image/mobile/$transactionId",
          queryParameters: {'publicKey': secretKey},
          data: formData,
        );
      } else {
        response = await _dio.put(
          "api/auth/transactions-accepted/mobile/$transactionId",
          queryParameters: {'publicKey': secretKey},
          data: formData,
        );
      }
      var goldTransaction = GoldTransaction.fromJson(response.data['data']);
      return Left(ResponseObj.fromJson(response.data, goldTransaction));
    } on DioException catch (e) {
      final String? errorMessage =
          await _dioExceptionService.handleDioException(e);
      return Right(errorMessage);
    } catch (e, st) {
      debugPrintStack(stackTrace: st, label: 'signatureTransaction');
      return Right(e.toString());
    }
  }

  Future<Either<List<GoldTransaction>, String?>> getTransactionList() async {
    try {
      final response = await _dio.get("api/auth/transaction-list");
      final List<GoldTransaction> transactions = List<GoldTransaction>.from(
          response.data.map((json) => GoldTransaction.fromJson(json)));
      return Left(transactions);
    } on DioException catch (e) {
      final errorMessage = await _dioExceptionService.handleDioException(e);
      return Right(errorMessage);
    }
  }

  // API call to get the list of transactions for a specific user
  Future<Either<List<GoldTransaction>, String?>>
      getUserTransactionList() async {
    try {
      var userId = await userInfoId;
      final response = await _dio.get("/api/auth/user-transaction-list/$userId");
      final List<GoldTransaction> transactions = List<GoldTransaction>.from(
        response.data["data"].map((json) => GoldTransaction.fromJson(json)),
      );
      if (transactions.isEmpty) return const Right("Transaction list is empty");
      return Left(transactions);
    } on DioException catch (e) {
      final String? errorMessage =
          await _dioExceptionService.handleDioException(e);
      return Right(errorMessage);
    }
  }

  Future<Either<ResponseObj<Contract>, String?>> verifyContract(
      int contractId, String publicKey, String privateKey) async {
    try {
      // var data =
      //     FormData.fromMap({'publicKey': publicKey, 'privateKey': privateKey});

      final response = await _dio.post("/api/auth/verify/contract/$contractId",
          data: {'publicKey': publicKey, 'privateKey': privateKey});
      final Contract contract = Contract.fromJson(response.data["data"]);
      // if (contract.isEmpty) return const Right("Transaction list is empty");
      return Left(ResponseObj.fromJson(response.data, contract));
    } on DioException catch (e) {
      final String? errorMessage =
          await _dioExceptionService.handleDioException(e);
      return Right(errorMessage);
    }
  }
}

enum TransactionType {
  BUY,
  SELL;
}

extension TransactionTypeExtensions on TransactionType {
  Color get color => this == TransactionType.BUY ? kWinColor : kLossColor;
}
