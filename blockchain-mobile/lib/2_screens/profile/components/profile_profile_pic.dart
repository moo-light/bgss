import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';

class MyAccountProfilePic extends StatelessWidget {
  const MyAccountProfilePic({
    super.key,
    required this.imgUrl,
  });

  final String? imgUrl;
  final defaultAvatar = const CircleAvatar(
    backgroundImage: AssetImage("assets/images/Profile Image.png"),
  );
  @override
  Widget build(BuildContext context) {
    return Container(
      width: 115,
      height: 115,
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(1000),
        boxShadow: [BoxShadow(color: kIconColor.withOpacity(0.3),blurRadius: 10,spreadRadius: 5)]
      ),
      child: imgUrl != null
          ? CircleAvatar(
              backgroundColor: Colors.white,
              backgroundImage: NetworkImage(imgUrl!),
            )
          : defaultAvatar,
    );
  }
}
