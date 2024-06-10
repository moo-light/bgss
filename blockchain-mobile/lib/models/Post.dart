// ignore_for_file: camel_case_types

import 'package:blockchain_mobile/4_helper/image.dart';
import 'package:blockchain_mobile/models/avatar_data.dart';
import 'package:flutter/material.dart';

class Post {
  final int id;
  final String title;
  final String content;
  final DateTime createDate;
  final DateTime? updateDate;
  final DateTime? deleteDate;
  final String categoryName;
  final int forumId;
  final Post_User user;
  final String? textImg;
  final bool? pinned;
  final bool? hide;
  final int? rateCount;

  Post({
    required this.id,
    required this.title,
    required this.content,
    required this.createDate,
    required this.updateDate,
    required this.deleteDate,
    required this.categoryName,
    required this.forumId,
    required this.user,
    required this.textImg,
    required this.pinned,
    required this.hide,
    required this.rateCount,
  });
  // Image get txtimage => Image.network(ImageHelper.getServerImgUrl(textImg??"", Product.defaultImageUrl));

  factory Post.fromJson(Map<String, dynamic> json) {
    return Post(
      id: json['id'],
      title: json['title'],
      content: json['content'],
      createDate: DateTime.parse(json['createDate']),
      updateDate: json['updateDate'] != null
          ? DateTime.parse(json['updateDate'])
          : null,
      deleteDate: json['deleteDate'] != null
          ? DateTime.parse(json['deleteDate']).toLocal()
          : null,
      categoryName: json['categoryPost']['categoryName'],
      forumId: json['categoryPost']['forums']['id'],
      user: Post_User.fromJson(json['user']),
      textImg: json['textImg'] ?? "",
      pinned: json['pinned'],
      hide: json['hide'],
      rateCount: json['rateCount'],
    );
  }

}

class Post_UserInfo {
  final int userInfoId;
  final String firstName;
  final String lastName;
  final DateTime dob;
  final AvatarData? avatarData;

  Post_UserInfo({
    required this.userInfoId,
    required this.firstName,
    required this.lastName,
    required this.dob,
    required this.avatarData,
  });

  factory Post_UserInfo.fromJson(Map<String, dynamic> json) {
    return Post_UserInfo(
      userInfoId: json['userInfoId'],
      firstName: json['firstName'],
      lastName: json['lastName'],
      dob: DateTime.parse(json['doB']),
      avatarData: json['avatarData'] != null
          ? AvatarData.fromJson(json['avatarData'])
          : null,
    );
  }
  Image get image => avatarData != null
      ? Image.network(ImageHelper.getServerImgUrl(avatarData!.imgUrl))
      : Image.asset("assets/images/Profile Image.png");
}

class Post_User {
  final int userId;
  final List<String> roles;
  final Post_UserInfo userInfo;
  final bool active;

  Post_User({
    required this.userId,
    required this.roles,
    required this.userInfo,
    required this.active,
  });

  factory Post_User.fromJson(Map<String, dynamic> json) {
    return Post_User(
      userId: json['userId'],
      roles: List<String>.from(json['roles'].map((role) => role['name'])),
      userInfo: Post_UserInfo.fromJson(json['userInfo']),
      active: json['active'],
    );
  }
}
