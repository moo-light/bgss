import 'package:blockchain_mobile/1_controllers/services/dio_exception_service.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:blockchain_mobile/models/liverates/forex_live_rate.dart';
import 'package:dio/dio.dart';
import 'package:either_dart/either.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

import '../../models/liverates/live_rate.dart';
import '../../models/liverates/time_frames.dart';

class LiveRateRepository {
  final DioService _dioService = DioService();
  final DioExceptionService _dioExceptionService = DioExceptionService();

  Dio get _dio => _dioService.dio;
  factory LiveRateRepository() {
    return LiveRateRepository._internal();
  }
  LiveRateRepository._internal();

  Future<Either<List<LiveRate>, String?>> getLiveRates(String type) async {
    try {
      //Call Api
      final result = await _dio.get(
        '/api/auth/gold/live-rate',
        queryParameters: {'type': type},
      );
      final List json = result.data;
      final List<LiveRate> liveRates =
          json.map((json) => LiveRate.fromJson(json)).toList();
      return Left(liveRates);
    } catch (e) {
      if (e is DioException) {
        final message = await _dioExceptionService.handleDioException(e);
        return Right(message);
      }
      return Right(e.toString());
    }
  }

  Future<Either<List<TimeFrames>, String?>> getTimeframes(String type) async {
    try {
      //Call Api
      final result = await _dio.get(
        '/api/auth/gold/live-rate',
        queryParameters: {'type': type},
      );
      final List json = result.data;
      final List<TimeFrames> timeframes =
          json.map((json) => TimeFrames.fromJson(json)).toList();
      return Left(timeframes);
    } catch (e) {
      if (e is DioException) {
        final message = await _dioExceptionService.handleDioException(e);
        return Right(message);
      }
      return Right(e.toString());
    }
  }

  Future<Either<ForexSocketLiveRate, String?>> getLatestLiveRate() async {
    try {
      //Call Api
      var liverateKey = dotenv.env['LIVERATE_KEY'];
      final response = await _dio.get(
        'https://marketdata.tradermade.com/api/v1/live',
        queryParameters: {
          'api_key': liverateKey,
          'currency': 'XAUUSD',
        },
        // options: Options(
        //   receiveTimeout: 10000, // 10 seconds
        //   sendTimeout: 10000,
        // ),
      );
      final data = response.data;
      // final data = {
      //   "endpoint": "live",
      //   "quotes": [
      //     {
      //       "ask": 2342.26,
      //       "base_currency": "XAU",
      //       "bid": 2342.15,
      //       "mid": 2342.205,
      //       "quote_currency": "USD"
      //     },
      //   ],
      //   "requested_time": "Fri, 31 May 2024 14:29:57 GMT",
      //   "timestamp": 1717165797
      // };
      final quote = (data['quotes'] as List<Map>).first;
      Map<String, dynamic> newObj = {
        "symbol": "XAUUSD",
        ...quote,
        "ts": data["timestamp"].toString()
      };
      // final ForexSocketLiveRate liveRate = result.data;
      final ForexSocketLiveRate liveRate = ForexSocketLiveRate.fromJson(newObj);
      return Left(liveRate);
    } catch (e) {
      if (e is DioException) {
        final message = await _dioExceptionService.handleDioException(e);
        return Right(message);
      }
      return Right(e.toString());
    }
  }
}
