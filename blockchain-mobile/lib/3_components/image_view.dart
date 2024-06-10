import 'package:flutter/material.dart';
import 'package:photo_view/photo_view.dart';

class ImageView extends StatelessWidget {
  final ImageProvider<Object>? image;

  const ImageView({super.key, required this.image});
  static view(context, ImageProvider<Object> image) {
    Navigator.push(context,
        MaterialPageRoute(builder: (context) => ImageView(image: image)));
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.black,
        leading: IconButton(
          onPressed: () => Navigator.pop(context),
          icon: const Icon(Icons.arrow_back),
        ),
      ),
      body: PhotoView(
        imageProvider: image,
      ),
    );
  }
}
