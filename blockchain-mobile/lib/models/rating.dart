import 'package:blockchain_mobile/models/Post.dart';

class Rating {
  final int id;
  final String content;
  final DateTime createDate;
  final DateTime updateDate;
  final bool isHide;
  final String imgUrl;
  final Post_User user;

  Rating({
    required this.id,
    required this.content,
    required this.createDate,
    required this.updateDate,
    required this.isHide,
    required this.imgUrl,
    required this.user,
  });

  factory Rating.fromJson(Map<String, dynamic> json) {
    return Rating(
      id: json['id'] as int,
      content: json['content'] as String,
      createDate: DateTime.parse(json['createDate']).toLocal(),
      updateDate: DateTime.parse(json['updateDate']).toLocal(),
      isHide: json['isHide'] as bool,
      imgUrl: json['imgUrl'] as String,
      user: Post_User.fromJson(json['userDTO']),
    );
  }
}
