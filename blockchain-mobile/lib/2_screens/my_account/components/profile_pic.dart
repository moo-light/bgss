import 'dart:io';

import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/image_provider.dart';
import 'package:blockchain_mobile/3_components/dialogs/select_source_option_dialog.dart';
import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:image_picker/image_picker.dart';
import 'package:provider/provider.dart';

class ProfileScreenPic extends StatelessWidget {
  const ProfileScreenPic({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    final croppedFile = context.watch<AppImageProvider>().croppedFile;
    final currentUser = context.watch<AuthProvider>().currentUser;
    // print(currentUser?.avatarData?.imageDataBytes);
    const defaultAvatar = CircleAvatar(
      // child: croppedFile != null
      //     ? Image.file(File(croppedFile.path),fit: BoxFit.cover,)
      //     : Image.asset("assets/images/Profile Image.png"),
      backgroundImage: AssetImage("assets/images/Profile Image.png"),
    );

    return Container(
      height: 115,
      width: 115,
      decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(1000),
          boxShadow: [
            BoxShadow(
                color: kIconColor.withOpacity(0.3),
                blurRadius: 10,
                spreadRadius: 5)
          ]),
      child: Stack(
        fit: StackFit.expand,
        clipBehavior: Clip.none,
        children: [
          croppedFile != null
              ? CircleAvatar(
                  // child: croppedFile != null
                  //     ? Image.file(File(croppedFile.path),fit: BoxFit.cover,)
                  //     : Image.asset("assets/images/Profile Image.png"),
                  backgroundColor: Colors.white,
                  backgroundImage: FileImage(File(croppedFile.path)),
                )
              : currentUser!.userInfo.avatarData != null
                  ? CircleAvatar(
                      backgroundColor: Colors.white,
                      backgroundImage:
                          NetworkImage(currentUser.userInfo.avatarData!.imgUrl),
                    )
                  : defaultAvatar,
          Positioned(
            right: -16,
            bottom: 0,
            child: SizedBox(
              height: 46,
              width: 46,
              child: TextButton(
                style: TextButton.styleFrom(
                  foregroundColor: Colors.white,
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(50),
                    side: const BorderSide(color: Colors.white),
                  ),
                  backgroundColor: const Color(0xFFF5F6F9),
                ),
                onPressed: () async {
                  ImageSource? source = await selectSourceOption(context);
                  if (source == null) return;
                  var image =
                      await context.read<AppImageProvider>().pickImage(source);
                  if (context.mounted) {
                    await context.read<AppImageProvider>().cropImage(image);
                  }
                },
                child: SvgPicture.asset("assets/icons/Camera Icon.svg"),
              ),
            ),
          ),
          Visibility(
            visible: croppedFile != null,
            child: Positioned(
              right: -16,
              top: 0,
              child: SizedBox(
                height: 46,
                width: 46,
                child: TextButton(
                  style: TextButton.styleFrom(
                    foregroundColor: Colors.white,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(50),
                      side: const BorderSide(color: Color(0xffdc3545)),
                    ),
                    backgroundColor: const Color(0xffdc3545),
                  ),
                  onPressed: () => {confirmRemove(context)},
                  child: const FaIconGen(FontAwesomeIcons.trash,
                      width: 18, color: Colors.white),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Future<dynamic> confirmRemove(BuildContext context) {
    return showDialog(
      context: context,
      builder: (context) {
        Widget okButton = TextButton(
          child: const Text("OK"),
          onPressed: () async {
            await context.read<AppImageProvider>().clearImage();
            if (context.mounted) Navigator.of(context).pop();
          },
        );
        Widget cancelButton = TextButton(
          child: const Text("Cancel"),
          onPressed: () {
            Navigator.of(context).pop();
          },
        );
        return AlertDialog(
          title: const Text("Remove Image"),
          content: const Text("Are you sure you want to remove your image?"),
          actions: [cancelButton, okButton],
        );
      },
    );
  }
}
