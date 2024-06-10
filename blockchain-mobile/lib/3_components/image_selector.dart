import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/3_components/image_view.dart';
import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';

class ImageSeletor extends StatefulWidget {
  final VoidCallback selectImage;

  final VoidCallback clearImage;

  final ImageProvider<Object> currentImage;

  final bool haveImage;

  const ImageSeletor({
    super.key,
    required this.selectImage,
    required this.clearImage,
    required this.currentImage,
    required this.haveImage,
  });

  @override
  State<ImageSeletor> createState() => _ImageSeletorState();
}

class _ImageSeletorState extends State<ImageSeletor> {
  @override
  Widget build(BuildContext context) {
    return Stack(clipBehavior: Clip.none, children: [
      Align(
        alignment: Alignment.topLeft,
        child: Container(
          width: 150,
          height: 150,
          decoration: BoxDecoration(
              border: Border.all(width: 4, color: kIconColor),
              color: kBackgroundColor.withHSLlighting(93),
              image: DecorationImage(
                image: widget.currentImage,
                fit: BoxFit.cover,
              ),
              borderRadius: BorderRadius.circular(20)),
          child: InkWell(
            onTap: widget.selectImage,
            child: Visibility(
              visible: !widget.haveImage,
              child: const FaIconGen(
                FontAwesomeIcons.plus,
                width: 50,
              ),
            ),
          ),
        ),
      ),
      Visibility(
        visible: widget.haveImage,
        child: Positioned(
          left: 150,
          top: 0,
          bottom: 0,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              IconButton.filled(
                icon: const FaIconGen(
                  FontAwesomeIcons.solidEye,
                  width: 20,
                ),
                style: IconButton.styleFrom(
                  backgroundColor: Colors.white,
                ),
                onPressed: () {
                  ImageView.view(
                    context,
                    widget.currentImage,
                  );
                },
              ),
              IconButton.filled(
                icon: const FaIcon(
                  FontAwesomeIcons.trash,
                  size: 20,
                  color: kLossColor,
                ),
                style: IconButton.styleFrom(
                  backgroundColor: Colors.white,
                ),
                onPressed: widget.clearImage,
              ),
            ],
          ),
        ),
      )
    ]);
  }
}
