import 'package:blockchain_mobile/1_controllers/services/dio_exception_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/dtos/response_object.dart';
import 'package:blockchain_mobile/models/payment_history.dart';
import 'package:blockchain_mobile/models/user.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/material.dart';
import 'package:image_cropper/image_cropper.dart';
import 'package:shared_preferences/shared_preferences.dart';

class UserRepository {
  static final UserRepository _authRepository = UserRepository._internal();
  final DioService _dioService = DioService();

  late final Dio dio;

  final _dioExceptionService = DioExceptionService();

  factory UserRepository() {
    return _authRepository;
  }
  UserRepository._internal() {
    dio = _dioService.dio;
  }

  Future<Either<dynamic, String?>> updateCurrentUserAvatar(userInfoId,
      {required CroppedFile avatar}) async {
    try {
      final sf = await SharedPreferences.getInstance();
      logger.d(dio.options.headers["Cookie"]);
      var uid = int.parse(sf.getString(STORAGE_USERID)!);
      var data = FormData.fromMap({
        'imageData': [
          await MultipartFile.fromFile(avatar.path,
              filename: 'user${uid}_avatar')
        ],
      });
      final result =
          await dio.put('/api/auth/update-avatar/$userInfoId', data: data);
      final json = result.data['data'];
      // final User user = User.fromJson(json);
      return Left(json);
    } catch (e) {
      if (e is! DioException) {
        return Right(e.toString());
      }
      final error = await _dioExceptionService.handleDioException(e);
      return Right(error);
    }
  }

  Future<Either<dynamic, String?>> updateUserInfo(User currentUser,
      {String? firstName,
      String? lastName,
      String? phone,
      DateTime? dob,
      String? address,
      String? ciCard}) async {
    try {
      final uid = await SharedPreferences.getInstance()
          .then((pref) => pref.getString(STORAGE_USERID));
      var data = {
        "userId": uid,
        "firstName": firstName ?? currentUser.userInfo.firstName,
        "lastName": lastName ?? currentUser.userInfo.lastName,
        "phoneNumber": phone ?? currentUser.userInfo.phoneNumber,
        "doB": dob?.toIso8601String() ?? currentUser.userInfo.doB,
        "address": address ?? currentUser.userInfo.address,
        "ciCard": ciCard ?? currentUser.userInfo.ciCard,
      };
      final result =
          await dio.put('api/auth/update-user-info/$uid', data: data);
      final json = result.data['data'];
      // final User user = User.fromJson(json);
      return Left(json);
    } catch (e) {
      if (e is! DioException) {
        return Right(e.toString());
      }
      final error = await _dioExceptionService.handleDioException(e);
      return Right(error);
    }
  }

  Future<Either<dynamic, String?>> verifyCiCard({
    required CroppedFile front, // Replace CroppedFile with PickedFile
    required CroppedFile back,
  }) async {
    try {
      final SharedPreferences prefs = await SharedPreferences.getInstance();
      final String? uid = prefs.getString(STORAGE_USERID);
      FormData formData = FormData.fromMap({}, ListFormat.multi);
      var frontI = await MultipartFile.fromFile(
        front.path,
      );
      var backI = await MultipartFile.fromFile(
        back.path,
      );
      formData.files.addAll([
        MapEntry(
          'frontImage',
          frontI,
        ),
        MapEntry(
          'backImage',
          backI,
        )
      ]);
      // Map<dynamic, dynamic> fakeObj = _fakeObj;
      // return Left(fakeObj);
      final result = await dio.post(
        '/api/auth/verify/ciCard/image-verify/$uid', // Correct the path if it is different
        data: formData,
        options: Options(
          contentType: 'multipart/form-data',
        ),
      );
      return Left(result.data);
    } on DioException catch (e) {
      final error = await _dioExceptionService.handleDioException(e);
      if (error?.contains("Timeout") ?? false) {
        return const Right("can't verify card image on an emulator");
      }
      return Right(error);
    } catch (e) {
      // Handle other exceptions
      return Right(e.toString());
    }
  }

  Future<Either<Map, String?>> showSecretKey() async {
    try {
      final uid = await SharedPreferences.getInstance()
          .then((pref) => pref.getString(STORAGE_USERID));
      final result = await dio.post('api/auth/show-secret-key/$uid');
      return Left(result.data);
    } catch (e) {
      if (e is! DioException) {
        debugPrint(e.toString());
        return Right(e.toString());
      }
      final error = await _dioExceptionService.handleDioException(e);
      return Right(error);
    }
  }

  Future<Either<dynamic, String?>> regenerateSecretKey() async {
    try {
      final uid = await SharedPreferences.getInstance()
          .then((pref) => pref.getString(STORAGE_USERID));
      final result = await dio.post('/api/auth/regenerate-secretkey/$uid');
      return Left(result.data);
    } catch (e) {
      if (e is! DioException) {
        return Right(e.toString());
      }
      final error = await _dioExceptionService.handleDioException(e);
      return Right(error);
    }
  }

  Future<Either<ResponseObj<List<PaymentHistory>>, String?>>
      getPaymentHistory() async {
    try {
      final pref = await SharedPreferences.getInstance();
      final uid = pref.getString(STORAGE_USERID);

      final result = await dio.get(
        '/api/auth/history-deposit/$uid',
      );
      final json = result.data['data'] as List;
      List<PaymentHistory> payments =
          json.map((e) => PaymentHistory.fromJson(e)).toList();
      return Left(ResponseObj.fromJson(result.data, payments));
    } catch (e) {
      if (e is DioException) {
        _dioExceptionService.handleDioException(e);
      }
      return Right(e.toString());
    }
  }

  // final _fakeObj = {
  //   'firstName': 'John',
  //   'lastName': 'Doe',
  //   'dob': '2002-05-09',
  //   'address': '123 Main Street',
  //   'ciCard': '123123123123',
  // };
}
