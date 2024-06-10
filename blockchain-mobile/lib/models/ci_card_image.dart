import 'package:blockchain_mobile/4_helper/image.dart';
import 'package:flutter/material.dart';

class CiCardImage {
  final int id;
  final String imgUrl;

  CiCardImage({required this.id, required this.imgUrl});
  factory CiCardImage.fromJson(json) {
    return CiCardImage(id: json['id'], imgUrl: json['imgUrl']);
  }
  Image getImage() {
    return Image.network(ImageHelper.getServerImgUrl(imgUrl)!);
    // return Image.network(
    //     'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQE-iEHnpBv43rA7khToKiTlC-HiVdPSQtlfVXvhkmf-Q&s');
  }
}
