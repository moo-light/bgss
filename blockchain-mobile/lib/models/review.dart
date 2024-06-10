import 'package:blockchain_mobile/4_helper/image.dart';
import 'package:blockchain_mobile/models/Post.dart';
import 'package:flutter/material.dart';

class Review {
  int id;
  int numOfReviews;
  String content;
  DateTime createDate;
  DateTime? updateDate; // Make updateDate nullable
  String? imgUrl;
  Post_User? user;

  Review({
    required this.id,
    required this.numOfReviews,
    required this.content,
    required this.createDate,
    this.updateDate, // No need to use 'required' as it's nullable
    this.imgUrl,
    this.user,
  });

  factory Review.fromJson(Map<String, dynamic> json) {
    return Review(
      id: json['id'],
      numOfReviews: json['numOfReviews'],
      content: json['content'],
      createDate: DateTime.parse(json['createDate']).toLocal(),
      updateDate: DateTime.tryParse(json['updateDate'] ?? "")?.toLocal(),
      imgUrl: json['imgUrl'],
      user: json["user"] != null ? Post_User.fromJson(json["user"]) : null,
    );
  }

  // Image? getImage() {
  //   if (imgUrl == null) {
  //     return null;
  //   }
  //   return Image.network(ImageHelper.getServerImgUrl(imgUrl, "")!);
  // }

  Image get image => Image.network(ImageHelper.getServerImgUrl(imgUrl, ""));
}
// {
//   "id": 1,
//   "numOfReviews": 3,
//   "content": "đồ ngon",
//   "createDate": "2024-04-12T08:21:14.646576Z",
//   "updateDate": null,
//   "imgUrl": null
// }