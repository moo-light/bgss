import 'package:blockchain_mobile/1_controllers/services/dio_exception_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:blockchain_mobile/models/dtos/response_object.dart';
import 'package:blockchain_mobile/models/transfer_gold_unit.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';

class TransferGoldUnitRepository {
  final DioService _dioService = DioService();
  final DioExceptionService _dioExceptionService = DioExceptionService();

  Dio get _dio => _dioService.dio;

  factory TransferGoldUnitRepository() {
    return TransferGoldUnitRepository._internal();
  }

  TransferGoldUnitRepository._internal();

  Future<Either<ResponseObj<List<TransferGoldUnit>>, String?>> getAll() async {
    try {
      final result = await _dio.get("/api/auth/get-all-information-transfer");
      final json = result.data['data'] as List;
      final values = json.map((e) => TransferGoldUnit.fromJson(e)).toList();
      values.sort((curr, next) => curr.id.compareTo(next.id));
      return Left(ResponseObj.fromJson(result.data, values));
    } on DioException catch (e) {
      var dioErr = await _dioExceptionService.handleDioException(e);
      return Right(dioErr);
    }
  }

  // Future<Either<ResponseObj<Review>, String?>> create(
  //   int productId, {
  //   required int numOfReviews,
  //   required String content,
  //   MultipartFile? image,
  // }) async {
  //   try {
  //     final uid = await SharedPreferences.getInstance()
  //         .then((pref) => pref.getString(STORAGE_USERID));
  //     var data = FormData.fromMap({
  //       'imgReview': image,
  //       'numOfReviews': numOfReviews,
  //       'content': content
  //     });
  //     final result = await _dio.post(
  //         "/api/auth/create-review?userId=$uid&productId=$productId",
  //         data: data);
  //     return Left(
  //       ResponseObj.fromJson(
  //         result.data,
  //         Review.fromJson(result.data["data"]),
  //       ),
  //     );
  //   } on DioException catch (e) {
  //     var dioErr = await _dioExceptionService.handleDioException(e);
  //     return Right(dioErr);
  //   } catch (e) {
  //     return Right(e.toString());
  //   }
  // }

  // Future<Either<ResponseObj<Review>, String?>> update(int productId,
  //     {required int numOfReviews,
  //     required String content,
  //     MultipartFile? image,
  //     required bool isRemove}) async {
  //   try {
  //     final uid = await SharedPreferences.getInstance()
  //         .then((pref) => pref.getString(STORAGE_USERID));
  //     var data = FormData.fromMap({
  //       'imgReview': image,
  //       'numOfReviews': numOfReviews,
  //       'content': content,
  //       'removeImg': isRemove
  //     });
  //     final result = await _dio.put(
  //         "/api/auth/update-review?userId=$uid&productId=$productId",
  //         data: data);
  //     return Left(
  //       ResponseObj.fromJson(result.data, Review.fromJson(result.data["data"])),
  //     );
  //   } on DioException catch (e) {
  //     var dioErr = await _dioExceptionService.handleDioException(e);
  //     return Right(dioErr);
  //   }
  // }

  // Future<Either<dynamic, String?>> delete({required int reviewId}) async {
  //   try {
  //     final uid = await SharedPreferences.getInstance()
  //         .then((pref) => pref.getString(STORAGE_USERID));
  //     final result =
  //         await _dio.put("/api/auth/delete-review/$reviewId?userId=$uid");
  //     return Left(result.data);
  //   } on DioException catch (e) {
  //     var dioErr = await _dioExceptionService.handleDioException(e);
  //     return Right(dioErr);
  //   }
  // }
}
