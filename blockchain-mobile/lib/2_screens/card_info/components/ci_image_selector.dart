import 'dart:io';

import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/ci_card_image.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:image_cropper/image_cropper.dart';

class CiImageSelector extends StatelessWidget {

  const CiImageSelector({
    super.key,
    required this.border,
    required this.initialImage,
    required this.image,
    required this.action,
  });

  final Border border;
  final CiCardImage? initialImage;
  final CroppedFile? image;
  final VoidCallback action;

  @override
  Widget build(BuildContext context) {
    return AspectRatio(
      aspectRatio: 4/3,
      child: Container(
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(16),
          color: kBackgroundColor.withHSLlighting(97),
          border: border,
          image: DecorationImage(
            image: FileImage(
              File(image?.path ?? ""),
            ),
            fit: BoxFit.cover,
          ),
        ),
        child: InkWell(
          onTap: action,
          child: Visibility(
            visible: image == null,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                AbsorbPointer(
                  child: IconButton(
                    onPressed: () {},
                    icon: const FaIconGen(FontAwesomeIcons.plus),
                  ),
                ),
                const Text("Add Image", style: TextStyle(fontSize: 20)),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
