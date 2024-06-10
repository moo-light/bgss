
import 'package:blockchain_mobile/4_helper/image.dart';

class AvatarData {
  int id;
  String imgUrl;
  AvatarData({
    required this.id,
    required this.imgUrl,
  });

  factory AvatarData.fromJson(Map<String, dynamic> json) {
    return AvatarData(
      id: json['id'],
      imgUrl: ImageHelper.getServerImgUrl(json['imgUrl'] as String)!,
    );
  }
}
