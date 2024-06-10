import 'dart:convert';

class ForexLiveRate {
  final int id;
  final String endpoint;
  final double ask;
  final String baseCurrency;
  final double bid;
  final double mid;
  final String quoteCurrency;
  final String requestedTime;
  final int timestamp;
  final double changedPrice;
  final double changedPercentage;

  ForexLiveRate({
    required this.id,
    required this.endpoint,
    required this.ask,
    required this.baseCurrency,
    required this.bid,
    required this.mid,
    required this.quoteCurrency,
    required this.requestedTime,
    required this.timestamp,
    this.changedPrice = 0,
    this.changedPercentage = 0,
  });

  factory ForexLiveRate.fromJson(Map<String, dynamic> json) {
    return ForexLiveRate(
      id: json['id'],
      endpoint: json['endpoint'],
      ask: json['ask'],
      baseCurrency: json['baseCurrency'],
      bid: json['bid'],
      mid: json['mid'],
      quoteCurrency: json['quoteCurrency'],
      requestedTime: json['requestedTime'],
      timestamp: json['timestamp'],
      changedPrice: json['changedPrice'] ?? 0,
      changedPercentage: json['changedPercentage'] ?? 0,
    );
  }
  factory ForexLiveRate.fromJsonString(String data) {
    return ForexLiveRate.fromJson(json.decode(data));
  }
  factory ForexLiveRate.changed(ForexLiveRate oldData, ForexLiveRate newData) {
    return ForexLiveRate(
        id: newData.id,
        endpoint: newData.endpoint,
        ask: newData.ask,
        baseCurrency: newData.baseCurrency,
        bid: newData.bid,
        mid: newData.mid,
        quoteCurrency: newData.quoteCurrency,
        requestedTime: newData.requestedTime,
        timestamp: newData.timestamp,
        changedPrice: newData.mid - oldData.mid,
        changedPercentage: (newData.mid - oldData.mid) / oldData.mid);
  }
  DateTime get timeframeDate =>
      DateTime.fromMillisecondsSinceEpoch(timestamp * 1000);
}
//  {"symbol":"XAUUSD","ts":"1714378381740","bid":2336.75,"ask":2336.92,"mid":2336.835}

class ForexSocketLiveRate {
  final String symbol;
  final int timestamp;
  final double bid;
  final double mid;
  final double ask;
  final double changedPrice;
  final double changedPercentage;

  factory ForexSocketLiveRate.fromJson(Map<String, dynamic> json) {
    return ForexSocketLiveRate(
      symbol: json['symbol'],
      ask: json['ask'],
      bid: json['bid'],
      mid: json['mid'],
      timestamp: int.parse(json['ts'].toString()),
      changedPrice: json['changedPrice'] ?? 0,
      changedPercentage: json['changedPercentage'] ?? 0,
    );
  }
  ForexSocketLiveRate(
      {required this.symbol,
      required this.timestamp,
      required this.bid,
      required this.mid,
      required this.ask,
      required this.changedPrice,
      required this.changedPercentage});
  factory ForexSocketLiveRate.fromJsonString(String data) {
    return ForexSocketLiveRate.fromJson(json.decode(data));
  }
  factory ForexSocketLiveRate.changed(
      ForexSocketLiveRate oldData, ForexSocketLiveRate newData) {
    return ForexSocketLiveRate(
        symbol: newData.symbol,
        ask: newData.ask,
        bid: newData.bid,
        mid: newData.mid,
        timestamp: newData.timestamp,
        changedPrice: newData.mid - oldData.mid,
        changedPercentage: (newData.mid - oldData.mid) / oldData.mid);
  }
  Map<String, dynamic> toJson() {
    return {
      'symbol': symbol,
      'ask': ask,
      'bid': bid,
      'mid': mid,
      'ts': timestamp,
    };
  }

  DateTime get timeframeDate => DateTime.fromMillisecondsSinceEpoch(timestamp*1000);
}
