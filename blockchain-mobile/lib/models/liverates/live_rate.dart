import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';

class LiveRate {
  final int id;
  final double? rates;
  final String? base;
  final DateTime timestamp;

  final double? changedPrice;

  final double? changedPercentage;

  LiveRate({
    required this.id,
    required this.rates,
    required this.base,
    required this.timestamp,
    this.changedPrice,
    this.changedPercentage,
  });

  factory LiveRate.fromJson(Map<String, dynamic> json) {
    final timeStamp = json['timestamp'];
    if (timeStamp is String) {
      json['timestamp'] = DateTime.parse(timeStamp).toLocal();
    }
    return LiveRate(
      id: json['id'] as int,
      rates: (json['rates']),
      base: (json['base']),
      timestamp: json['timestamp'] as DateTime,
      changedPrice: (json['changedPrice'] ?? 0.0),
      changedPercentage: (json['changedPercentage'] ?? 0.0),
    );
  }

  Map<String, dynamic> toJson() => {
        'id': id,
        'rates': rates,
        'base': base,
        'timestamp': timestamp.toIso8601String(),
        'changedPrice': changedPrice,
        'changedPercentage': changedPercentage,
      };
  Color getColor() {
    return (changedPrice ?? 1) > 0 ? kWinColor : kLossColor;
  }

  @override
  String toString() =>
      'LiveRate{id: $id, rates: $rates, base: $base, timestamp: $timestamp, changedPrice: $changedPrice, changedPercentage: $changedPercentage}';
}
