import 'package:blockchain_mobile/4_helper/image.dart';
import 'package:blockchain_mobile/models/Post.dart';
import 'package:flutter/material.dart';

class Rate {
  final int id;
  final String content;
  final DateTime createDate;
  final DateTime? updateDate;
  final String? imgUrl;
  final Post_User user;
  final bool hide;

  Rate({
    required this.id,
    required this.content,
    required this.createDate,
    this.updateDate,
    this.imgUrl,
    required this.user,
    required this.hide,
  });

  factory Rate.fromJson(Map<String, dynamic> json) {
    return Rate(
      id: json['id'],
      content: json['content'],
      createDate: DateTime.parse(json['createDate']),
      updateDate: json['updateDate'] != null
          ? DateTime.parse(json['updateDate'])
          : null,
      imgUrl: json['imgUrl'],
      user: Post_User.fromJson(json['user']),
      hide: json['hide'],
    );
  }
  Image get image => Image.network(ImageHelper.getServerImgUrl(imgUrl));
}
