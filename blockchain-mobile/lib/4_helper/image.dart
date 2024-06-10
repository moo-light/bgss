import 'dart:io';

import 'package:blockchain_mobile/1_controllers/services/others/dio_service.dart';

class ImageHelper {
  static String getServerImgUrl(String? data, [String defaultIfNull = ""]) {
    final DioService dioService = DioService();
    var baseHost = "${dioService.dio.options.baseUrl}api/auth";
    if (data == null) return defaultIfNull;
    // return the same if already formatted
    if (data.contains(baseHost)) return data;
    // Construct and return the URL with a timestamp to prevent caching
    return '$baseHost/$data';
  }

  static Future<int> getImageFileSize(File file) async {
    int fileSize = file.lengthSync();
    print("File Size is: $fileSize");
    return fileSize;
  }
}
