import 'dart:typed_data';

import 'package:blockchain_mobile/1_controllers/repositories/transaction_repository.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/models/contract.dart';
import 'package:blockchain_mobile/models/dtos/response_object.dart';
import 'package:blockchain_mobile/models/gold_transaction.dart';
import 'package:blockchain_mobile/models/transfer_gold_unit.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/material.dart';

import '../../models/dtos/result_object.dart';

class TransactionProvider extends ChangeNotifier {
  final TransactionRepository _transactionRepository = TransactionRepository();

  ResultObject<GoldTransaction> processUserTransactionResult =
      ResultObject<GoldTransaction>();
  processUserTransaction(BuildContext context,
      {required double quantityInOz,
      required double pricePerOz,
      required TransactionType type,
      required TransferGoldUnit goldUnit}) async {
    processUserTransactionResult.reset();
    processUserTransactionResult.isLoading = true;
    notifyListeners();

    final result = await _transactionRepository.processUserTransaction(
      quantityInOz: quantityInOz,
      pricePerOz: pricePerOz,
      type: type,
      goldUnit: goldUnit.goldUnit,
    );
    result.either(
      (left) {
        processUserTransactionResult.isSuccess = true;
        processUserTransactionResult.result = left.data;
        currentTransaction.result = left.data;
        // currentTransaction.result?.contract?.contractStatus = "DIGITAL_SIGNED";
        processUserTransactionResult.message = left.message;
        notifyListeners();
      },
      (right) {
        processUserTransactionResult.isError = true;
        processUserTransactionResult.error =
            right ?? "Transaction order Failed!";
        ToastService.toastError(context, processUserTransactionResult.error,
            alignment: Alignment.topCenter);
      },
    );

    processUserTransactionResult.isLoading = false;
    notifyListeners();
  }

  ResultObject<GoldTransaction> processSignatureResult =
      ResultObject<GoldTransaction>();
  processSignature(
    BuildContext context, {
    required int transactionId,
    required Uint8List signature,
    required String secretKey,
    bool? isUpdate,
  }) async {
    processSignatureResult.reset();
    processSignatureResult.isLoading = true;
    notifyListeners();
    Either<ResponseObj<GoldTransaction>, String?> result = await Future.wait(
      [
        _transactionRepository.signatureTransaction(
          signature: signature,
          transactionId: transactionId,
          secretKey: secretKey,
          isUpdate: isUpdate,
        ),
        Future.delayed(Durations.extralong4),
      ],
    ).then((value) => value.first);

    result.either(
      (left) {
        processSignatureResult.isLoading = false;
        processSignatureResult.isSuccess = true;
        processSignatureResult.result = left.data;
        currentTransaction.result = left.data;
        var index = getUserTransactionResult.result
            ?.indexWhere((element) => element.id == left.data.id);
        if (index != null) getUserTransactionResult.result?[index] = left.data;
        processSignatureResult.message = left.message;
      },
      (right) {
        processSignatureResult.isLoading = false;
        processSignatureResult.isError = true;
        processSignatureResult.error = right!;
      },
    );

    if (context.mounted) {
      processSignatureResult.handleResult(context);
    }
    notifyListeners();
  }

  ResultObject<List<GoldTransaction>> getUserTransactionResult = ResultObject();
  ResultObject<GoldTransaction> currentTransaction = ResultObject();
  Future<void> getUserTransaction() async {
    getUserTransactionResult.reset(isLoading: true);
    notifyListeners();
    var result = await _transactionRepository.getUserTransactionList();
    getUserTransactionResult.isLoading = false;
    result.either(
        (left) => {
              getUserTransactionResult.isSuccess = true,
              getUserTransactionResult.result = left,
              getUserTransactionResult.message = "Request transaction Success!",
            },
        (right) => {
              getUserTransactionResult.isError = true,
              getUserTransactionResult.error = right!,
            });
    notifyListeners();
  }

  final verifyContractResult = ResultObject<Contract>();
  Future<void> verifyContract(BuildContext context, int contractId,
      Map<dynamic, dynamic> secretKeys) async {
    verifyContractResult.reset(isLoading: true);
    notifyListeners();

    var result = await _transactionRepository.verifyContract(
      contractId,
      secretKeys["publicKey"],
      secretKeys['privateKey'],
    );
    result.either(
      (left) => {
        verifyContractResult.isSuccess = true,
        verifyContractResult.result = left.data,
        verifyContractResult.message = left.message,
        currentTransaction.result?.contract = left.data,
        currentTransaction.result?.contract?.contractStatus = "DIGITAL_SIGNED",
        notifyListeners()
      },
      (right) => {
        verifyContractResult.isError = true,
        verifyContractResult.error = right ?? "Verify Contract Failed!",
        notifyListeners()
      },
    );
    verifyContractResult.isLoading = false;
    notifyListeners();
    // if(context.mounted) {
    //   verifyContractResult.handleResult(context);
    // }
  }
}
