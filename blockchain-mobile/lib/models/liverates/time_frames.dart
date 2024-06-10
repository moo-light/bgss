import 'package:blockchain_mobile/models/liverates/live_rate.dart';

class TimeFrames {
  TimeFrames(
    this.x,
    this.y,
  );

  final DateTime x;
  final num? y;

  factory TimeFrames.fromJson(Map<String, dynamic> json) {
    return TimeFrames(
      DateTime.parse(json['x'] as String).toLocal(),
      json['y'] as num?,
    );
  }
  factory TimeFrames.fromLiveRate(LiveRate liveRate) {
    return TimeFrames(
      liveRate.timestamp,
      liveRate.rates,
    );
  }
}
