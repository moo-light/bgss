import 'dart:convert';
import 'dart:typed_data';

import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:image_cropper/image_cropper.dart';
import 'package:image_picker/image_picker.dart';

class AppImageProvider extends ChangeNotifier {
  bool isLoading = true;
  final ImagePicker _picker = ImagePicker();
  // Uint8List? bytes;

  CroppedFile? croppedFile;

  Future<XFile?> pickImage(ImageSource source, [int? quality]) async {
    var image = await _picker.pickImage(source: source, imageQuality: quality);
    // if (_image != null) bytes = await File(_image!.path).readAsBytes();
    logger.i(image?.path);
    notifyListeners();
    return image;
  }

  Future<CroppedFile?> cropImage(XFile? image,
      {CropAspectRatio? aspectRatio,
      String title = "Select Your Avatar"}) async {
    aspectRatio ??= const CropAspectRatio(ratioX: 1, ratioY: 1);
    // action
    croppedFile = await ImageCropper().cropImage(
      sourcePath: image!.path,
      compressFormat: ImageCompressFormat.jpg,
      compressQuality: 100,
      uiSettings: [
        AndroidUiSettings(
            toolbarTitle: title,
            toolbarColor: kPrimaryColor,
            toolbarWidgetColor: Colors.white,
            initAspectRatio: CropAspectRatioPreset.square,
            // lockAspectRatio: true,
            hideBottomControls: true),
        IOSUiSettings(
          title: 'Select Your Avatar',
        ),
      ],
      aspectRatio: aspectRatio,
    );
    logger.i(croppedFile!.path);
    if (croppedFile != null) notifyListeners();
    return croppedFile;
  }

  Future clearImage() async {
    croppedFile = null;
    notifyListeners();
  }
}

class ImageData {
  int id;
  String name;
  String type;
  Uint8List? imageDataBytes; // Change from List<int> to Uint8List

  ImageData({
    required this.id,
    required this.name,
    required this.type,
    required this.imageDataBytes,
  });

  factory ImageData.fromJson(Map<String, dynamic> json) {
    // Decode the base64 string to bytes
    Uint8List? imageDataBytes =
        json['imageData'] != null ? base64Decode(json['imageData']) : null;

    return ImageData(
      id: json['id'],
      name: json['name'],
      type: json['type'],
      imageDataBytes: imageDataBytes,
    );
  }
  Map<String, dynamic> toJson() {
    // Encode the bytes to a base64 string
    String? base64ImageData =
        imageDataBytes != null ? base64Encode(imageDataBytes!) : null;

    return {
      'id': id,
      'name': name,
      'type': type,
      'imageData': base64ImageData,
    };
  }
}
