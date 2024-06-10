import 'dart:io';

import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/image_provider.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/3_components/card_container.dart';
import 'package:blockchain_mobile/3_components/dialogs/select_source_option_dialog.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_button.dart';
import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/4_helper/image.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:gap/gap.dart';
import 'package:image_cropper/image_cropper.dart';
import 'package:image_picker/image_picker.dart';
import 'package:provider/provider.dart';

import 'components/ci_image_selector.dart';

class CardInfoEditScreen extends StatefulWidget {
  static const String routeName = "/profile/me/card-info/edit";

  const CardInfoEditScreen({super.key});

  @override
  State<CardInfoEditScreen> createState() => _CardInfoEditScreenState();
}

var border = Border.all(
  width: 6,
  color: kSecondaryColor.withHSLlighting(75),
  strokeAlign: BorderSide.strokeAlignCenter,
);

class _CardInfoEditScreenState extends State<CardInfoEditScreen> {
  bool isUpdate = true;
  final cropAspectRatio = const CropAspectRatio(ratioX: 16, ratioY: 9);
  CroppedFile? image1;
  CroppedFile? image2;

  @override
  Widget build(BuildContext context) {
    var watch = context.watch<AuthProvider>();
    var ciCardImages = watch.currentUser!.userInfo.ciCardImage;
    if (ciCardImages.isEmpty) isUpdate = false;
    return Scaffold(
      appBar: AppBar(
        title: Text(
          !isUpdate ? "Verify citizen card information" : "Card Information",
        ),
      ),
      body: SafeArea(
        child: PopScope(
          onPopInvoked: (didPop) {
            context.read<AuthProvider>().submitCardInfomationResult.reset();
            context.read<AppImageProvider>().clearImage();
          },
          child: RefreshIndicator(
            onRefresh: () async {
              context.read<AuthProvider>().getCurrentUser();
            },
            child: SingleChildScrollView(
              physics: const AlwaysScrollableScrollPhysics(),
              child: Column(children: [
                CardContainer(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      const Text("Front Image",
                          style: TextStyle(
                            fontSize: 20,
                            fontWeight: FontWeight.bold,
                          )),
                      const Gap(5),
                      Stack(children: [
                        CiImageSelector(
                          border: border,
                          initialImage: ciCardImages.firstOrNull,
                          image: image1,
                          action: () {
                            _selectImage(isImage1: true);
                          },
                        ),
                        Visibility(
                          visible: image1 != null,
                          child: Positioned(
                            right: 10,
                            child: IconButton(
                              onPressed: () {
                                setState(() {
                                  image1 = null;
                                });
                              },
                              icon: const Icon(
                                FontAwesomeIcons.trashCan,
                                color: kLossColor,
                              ),
                            ),
                          ),
                        )
                      ]),
                      FutureBuilder(
                        future: ImageHelper.getImageFileSize(
                            File(image1?.path ?? "")),
                        builder: (context, snapshot) {
                          if (snapshot.connectionState ==
                              ConnectionState.done) {
                            return Text(
                                "${((snapshot.data ?? 0) / 1024 / 1024).toStringAsFixed(2)} MB");
                          }
                          return const Text("Size");
                        },
                      ),
                      const Gap(10),
                      const Divider(),
                      const Text("Back Image",
                          style: TextStyle(
                            fontSize: 20,
                            fontWeight: FontWeight.bold,
                          )),
                      const Gap(5),
                      Stack(children: [
                        CiImageSelector(
                          border: border,
                          initialImage: ciCardImages.lastOrNull,
                          image: image2,
                          action: () {
                            _selectImage(isImage1: false);
                          },
                        ),
                        Visibility(
                          visible: image2 != null,
                          child: Positioned(
                            right: 10,
                            child: IconButton(
                              onPressed: () {
                                setState(() {
                                  image2 = null;
                                });
                              },
                              icon: const Icon(
                                FontAwesomeIcons.trashCan,
                                color: kLossColor,
                              ),
                            ),
                          ),
                        )
                      ]),
                      FutureBuilder(
                        future: ImageHelper.getImageFileSize(
                            File(image2?.path ?? "")),
                        builder: (context, snapshot) {
                          if (snapshot.connectionState ==
                              ConnectionState.done) {
                            return Text(
                                "${((snapshot.data ?? 0) / 1024 / 1024).toStringAsFixed(2)} MB");
                          }
                          return const Text("Size");
                        },
                      ),
                      const Divider(),
                      const Gap(20),
                      Align(
                        alignment: Alignment.centerRight,
                        child: IsLoadingButton(
                          isLoading: watch.submitCardInfomationResult.isLoading,
                          child: ElevatedButton.icon(
                            onPressed: image1 != null && image2 != null
                                ? _submit
                                : null,
                            icon: const FaIcon(FontAwesomeIcons.solidIdCard),
                            label: Text(
                                isUpdate ? "Update CiCard" : "Submit CiCard"),
                          ),
                        ),
                      )
                    ],
                  ),
                ),
                const Gap(20),
              ]),
            ),
          ),
        ),
      ),
    );
  }

  Future<void> _selectImage({required bool isImage1}) async {
    ImageSource? source = await selectSourceOption(context);
    if (source == null) return;
    if (!context.mounted) return;
    dynamic image = await context.read<AppImageProvider>().pickImage(source, 50);
    if (image == null && !context.mounted) return;
    image = await context.read<AppImageProvider>().cropImage(
          image,
          aspectRatio: cropAspectRatio,
        );
    if (image == null) return;

    setState(() {
      isImage1 ? image1 = image : image2 = image;
    });
  }

  Future<void> _submit() async {
    if (image1 == null || image2 == null) {
      ToastService.toastError(context, "Please add 2 image before submitting");
      return;
    }
    await context.read<AuthProvider>().submitCardInfomation(
          context,
          isUpdate: isUpdate,
          front: image1!,
          back: image2!,
        );
    await context.read<AppImageProvider>().clearImage();
    context.read<AuthProvider>().getSecretKey();
    context.read<AuthProvider>().getCurrentUser();
  }
}
