import 'dart:async';
import 'dart:convert';

import 'package:blockchain_mobile/1_controllers/repositories/liverate_repository.dart';
import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';

import '../../models/liverates/forex_live_rate.dart';
import '../../models/liverates/live_rate.dart';

class LiveRateProvider with ChangeNotifier {
  // Define the variables
  final _liveRateRepository = LiveRateRepository();
  // late IOWebSocketChannel _channel;

  int _reconnectAttempts = 0;
  ForexSocketLiveRate? liveRate = ForexSocketLiveRate(
      symbol: "XAUUSD",
      timestamp: DateTime.now().microsecond,
      bid: 0,
      mid: 0,
      ask: 0,
      changedPrice: 0,
      changedPercentage: 0);
  final int _maxReconnectAttempts = 1000; // Example value
  final int _reconnectDelayMs = 5000; // Example value
  Timer? _reconnectTimer;
  late StompClient stompClient;

  // get _liveRateUrl => '/api/auth/topic/liverates';
  get _liveRateUrl => '/api/auth/topic/forexsocket';
  get _connectionUri => '${DioService().dio.options.baseUrl}api/auth/ws';
  // Constructor
  LiveRateProvider() {
    getLatestLiveRate();
    connectToWebSocket();
  }

  // Function to connect to WebSocket
  Future<void> connectToWebSocket() async {
    notifyListeners();
    print('Connecting to WebSocket');
    // Establish the WebSocket connection
    stompClient = StompClient(
      config: StompConfig.sockJS(
        url: _connectionUri,
        connectionTimeout: const Duration(seconds: 5),
        onConnect: (StompFrame frame) {
          // print('Connected to WebSocket');
          // Listen to the incoming messages
          if (stompClient.isActive) {
            // stompClient.send(
            //   destination: '/app/request-live-rates-forex',
            //   body: json.encode({'command': 'FETCH_LATEST_PRICE'}),
            // );

            // stompClient.subscribe(
            //     destination: _liveRateUrl,
            //     callback: (frame) {
            //       // Received a frame for this subscription
            //       // {"symbol":"XAUUSD","ts":"1714378381740","bid":2336.75,"ask":2336.92,"mid":2336.835}
            //       // {"id":98,"endpoint":"live","ask":2337.63,"baseCurrency":"XAU","bid":2337.35,"mid":2337.49,"quoteCurrency":"USD","requestedTime":"Sun, 28 Apr 2024 11:45:46 GMT","timestamp":1714304746}
            //       // debugPrint("time: ${frame.body}");
            //       // Future.delayed(Durations.extralong4);
            //       if (frame.body == null) return;
            //       final liveRate =
            //           ForexSocketLiveRate.fromJsonString(frame.body!);
            //       handleNewPrice(liveRate);
            //     });
          }
        },
        onWebSocketError: _onError,
        onStompError: (f) => _onError(f.body),
        onDisconnect: (f) => print("Disconnected"),
        onWebSocketDone: _onDone,
      ),
    );
    stompClient.activate();
  }

  // Handle received messages

  // Handle possible errors
  void _onError(error) {
    print('WebSocket Error: $error');
    _attemptReconnect();
  }

  // Handle the WebSocket closing
  void _onDone() {
    print('Disconnected from WebSocket');
    // Perform any cleanup if needed
    _attemptReconnect();
  }

  // Attempt to reconnect
  void _attemptReconnect() {
    if (_reconnectAttempts < _maxReconnectAttempts) {
      _reconnectTimer?.cancel();
      _reconnectTimer = Timer(Duration(milliseconds: _reconnectDelayMs), () {
        logger.i(
            "Attempting WebSocket reconnection (attempt ${_reconnectAttempts + 1})");
        _reconnectAttempts++;
        connectToWebSocket();
      });
    } else {
      print("Max WebSocket reconnection attempts reached.");
      // Consider notifying users or other parts of the app
    }
  }

  // Handle the live rate data
  void handleNewPrice(ForexSocketLiveRate liveRate) {
    // logger.d('New rate: $liveRate');
    if (this.liveRate == null) {
      this.liveRate = liveRate;
      return;
    }
    if (liveRate.timestamp == this.liveRate?.timestamp) return;

    // this.liveRate?.forEach((key, value) {
    //   debugPrint((key, value).toString());
    // });
    // final double rates = liveRate.mid;
    // final changedPrice = rates - this.liveRate!.mid;
    // final changedPercentage = changedPrice / this.liveRate["rates"];
    // liveRate.addEntries([
    //   MapEntry('changedPrice', changedPrice),
    //   MapEntry('changedPercentage', changedPercentage * 100),
    // ]);
    this.liveRate = ForexSocketLiveRate.changed(this.liveRate!, liveRate);
    SharedPreferences.getInstance().then((pref) {
      return pref.setString(
          STORAGE_LIVE_RATE,
          json.encode(
            this.liveRate?.toJson(),
            toEncodable: (object) {
              // debugPrint(object);
              return object;
            },
          ));
    });
    // Update your UI or other parts of the app with the new rate
    notifyListeners();
  }

  // Dispose method
  @override
  void dispose() {
    _reconnectTimer?.cancel();
    // _channel.sink.close();
    super.dispose();
  }

  bool isChartLoading = true;
  List<LiveRate> _rateHistory = [];
  ChartType type = ChartType._30M;
  Future<ChartType> getTypeFromSharedPreferences() async {
    final sharedPreferences = await SharedPreferences.getInstance();
    final typeName = sharedPreferences.getString("ChartType");
    type = ChartType.values.singleWhere(
      (element) => element.name == (typeName ?? ChartType._30M.name),
      orElse: () => ChartType._30M,
    );
    notifyListeners();
    return type;
  }

  List<LiveRate> get rateHistory {
    final result = List<LiveRate>.from(_rateHistory);
    // if (liveRate["timestamp"].millisecond ==
    //     result.last.timestamp.millisecond) {
    //   return result;
    // }
    // result.add(LiveRate.fromJson(liveRate));
    return result;
  }

  void setType(ChartType type) {
    this.type = type;
    notifyListeners();
    SharedPreferences.getInstance()
        .then((e) => e.setString("ChartType", type.name));
  }

  Future<List<LiveRate>> fetchRateHistory(ChartType type) async {
    if (type == this.type && _rateHistory.isNotEmpty) return _rateHistory;
    isChartLoading = true;
    notifyListeners();
    final result = await _liveRateRepository.getLiveRates(type.name);
    isChartLoading = false;
    result.either((left) {
      _rateHistory = left;
    }, (right) {});
    notifyListeners();
    return _rateHistory;
  }

  Future<void> getLatestLiveRate() async {
    String? lr = await SharedPreferences.getInstance().then((pref) {
       return pref.getString(
        STORAGE_LIVE_RATE,
      );
    });
    if (lr != null) {
      liveRate = ForexSocketLiveRate.fromJsonString(lr);
    }
    final result = await _liveRateRepository.getLatestLiveRate();
    result.either((left) {
      handleNewPrice(left);
    }, (right) {});
    notifyListeners();
  }
}

enum ChartType { _1M, _5M, _15M, _30M, _1H, _2H, _4H, D, W, M }

extension ChartTypeExtension on ChartType {
  String get interval {
    switch (this) {
      case ChartType._1M:
        return "1 minute";
      case ChartType._5M:
        return "5 minutes";
      case ChartType._15M:
        return "15 minutes";
      case ChartType._30M:
        return "30 minutes";
      case ChartType._1H:
        return "1 hour";
      case ChartType._2H:
        return "2 hours";
      case ChartType._4H:
        return "4 hours";
      case ChartType.D:
        return "1 day";
      case ChartType.W:
        return "1 week";
      case ChartType.M:
        return "1 month";
    }
  }
}
