import 'package:dio/dio.dart';
import 'package:flutter/foundation.dart';
import 'package:logger/logger.dart';

class DioExceptionService {
  // static final DioExceptionService _dioExceptionService =
  //     DioExceptionService._internal();

  late Logger logger;

  factory DioExceptionService() {
    return DioExceptionService._internal();
  }
  DioExceptionService._internal() {
    logger = Logger();
  }

  Future<String?> handleDioException(
    DioException dioErr, {
    String? msg404,
    String? msg403,
    String? msg401,
    String? msg400,
    String? msg500,
  }) async {
    if (dioErr.response == null) {
      final logmessage = {
        "path": dioErr.requestOptions.path,
        "query": dioErr.requestOptions.queryParameters,
        "message": dioErr.message
      };
      logger.e(
        logmessage,
        stackTrace: dioErr.stackTrace,
        error: dioErr,
        time: DateTime.now(),
      );
      if (dioErr.message?.contains("took longer than") ?? false) {
        return "Request Timeout";
      }
      if (kDebugMode) {
        print('Error sending request!');
      }
      return 'Error sending request!';
    }

    var message;
    if (dioErr.response?.data is! String) {
      message = dioErr.response?.data?["message"];
    } else {
      message = dioErr.response?.data;
    }
    if (kDebugMode) {
      print('Dio error!');
    }
    final logmessage = {
      "path": dioErr.requestOptions.path,
      "query": dioErr.requestOptions.queryParameters,
      "data": dioErr.response?.data,
    };
    logger.e(
      logmessage,
      stackTrace: dioErr.stackTrace,
      error: dioErr,
      time: DateTime.now(),
    );
    if (dioErr.response!.statusCode == 404) {
      return msg404 ??
          message ??
          'Error send request Fail! ${kDebugMode ? 404 : ""}';
    }
    if (dioErr.response!.statusCode == 403) {
      return msg403 ??
          message ??
          'Error send request Fail! ${kDebugMode ? 403 : ""}';
    }
    if (dioErr.response!.statusCode == 401) {
      if (kDebugMode) {
        return message ?? "Error send request Fail! ${kDebugMode ? 401 : ""}";
      }
      return msg401 ?? message;
    }

    if (dioErr.response!.statusCode == 500) {
      return msg500 ?? (kDebugMode ? message : "Internal Server Error");
    }
    return msg400 ?? message;
  }
}
