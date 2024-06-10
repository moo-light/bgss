import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:image_picker/image_picker.dart';

Future<ImageSource?> selectSourceOption(BuildContext context) {
  return showDialog(
    context: context,
    builder: (context) {
      return AlertDialog(
        content: Column(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.center,
          mainAxisSize: MainAxisSize.min,
          children: [
            TextButton.icon(
              label: const Text("From Camera"),
              onPressed: () {
                Navigator.pop(context, ImageSource.camera);
              },
              icon: const FaIcon(FontAwesomeIcons.camera),
            ),
            TextButton.icon(
              label: const Text("From Gallery"),
              onPressed: () {
                Navigator.pop(context, ImageSource.gallery);
              },
              icon: const FaIcon(FontAwesomeIcons.images),
            ),
          ],
        ),
      );
    },
  );
}
