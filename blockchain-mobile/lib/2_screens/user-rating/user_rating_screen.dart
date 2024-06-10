import 'dart:io';

import 'package:blockchain_mobile/1_controllers/providers/image_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/rate_provider.dart';
import 'package:blockchain_mobile/2_screens/sign_up/validators.dart';
import 'package:blockchain_mobile/3_components/dialogs/select_source_option_dialog.dart';
import 'package:blockchain_mobile/4_helper/decorations/text_styles.dart';
import 'package:blockchain_mobile/models/dtos/result_object.dart';
import 'package:blockchain_mobile/models/rate.dart';
import 'package:flutter/material.dart';
import 'package:gap/gap.dart';
import 'package:image_picker/image_picker.dart';
import 'package:provider/provider.dart';

import '../../3_components/image_selector.dart';

class UserRateScreen extends StatefulWidget {
  static const routeName = "/product/details/user-rate";
  static const routeNameUpdate = "/product/details/user-rate/edit";

  static navigateCreate(BuildContext context) =>
      Navigator.pushNamed(context, routeName);
  static navigateUpdate(BuildContext context, int id) =>
      Navigator.pushNamed(context, routeName, arguments: id);

  const UserRateScreen({super.key});
  @override
  State<UserRateScreen> createState() => _UserRateScreenState();
}

class _UserRateScreenState extends State<UserRateScreen> {
  bool isRemoved = false; // only for Update image
  bool isUpdate = false;

  String comment = "";

  XFile? image;
  late ResultObject<Rate> currentRate;

  String? get imgPath => image?.path;

  late int rateId;
  final _formKey = GlobalKey<FormState>();
  @override
  void initState() {
    currentRate = Provider.of<RateProvider>(context, listen: false).currentRate;
    if (currentRate.isSuccess) {
      comment = currentRate.result!.content;
    }
    super.initState();
  }

  @override
  void dispose() {
    _clearImage();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    rateId = ModalRoute.of(context)!.settings.arguments as int? ?? -1;
    if (rateId != -1 && isUpdate == false) {
      isUpdate = true;
    }
    ImageProvider<Object> currentImage = FileImage(File(""));
    var haveImage = false; //this is use to render image
    if (image != null) {
      currentImage = FileImage(File(image!.path)); // set image if changed
      haveImage = true;
    } else if (isUpdate && currentRate.result?.imgUrl != null && !isRemoved) {
      //default Image if Image exist
      currentImage = currentRate.result!.image.image;
      haveImage = true;
    }
    return Scaffold(
        resizeToAvoidBottomInset: false,
        appBar: AppBar(
          title: Text("Rate ${isUpdate ? "#$rateId Update" : "Create"}"),
        ),
        body: RefreshIndicator(
          onRefresh: () async {
            if (isUpdate) {
              await context.read<RateProvider>().getCurrentRate(rateId);
            }
          },
          child: SafeArea(
            child: Builder(
              builder: (context) {
                return Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        "Rate",
                        style: TextStyle(fontSize: 20),
                      ),
                      const Gap(5),
                      Form(
                          key: _formKey,
                          child: TextFormField(
                            initialValue: comment,
                            maxLines: 7,
                            minLines: 4,
                            maxLength: 4000,
                            validator: validator_review,
                            decoration: const InputDecoration(
                              hintText: "Enter your Rate",
                            ),
                            onChanged: (newValue) => {
                              comment = newValue,
                              _formKey.currentState!.validate(),
                            },
                          )),
                      ImageSeletor(
                        selectImage: _selectImage,
                        clearImage: _clearImage,
                        currentImage: currentImage,
                        haveImage: haveImage,
                      ),
                      const Gap(10),
                      Center(
                        child: ElevatedButton(
                          onPressed: _submit,
                          style: confirmTextBtnStyle.copyWith(
                            minimumSize: const MaterialStatePropertyAll(
                                Size(double.infinity, 48)),
                          ),
                          child: Text(isUpdate ? "Update Rate" : "Submit Rate"),
                        ),
                      )
                    ],
                  ),
                );
              },
            ),
          ),
        ));
  }

  void _submit() async {
    if (image != null || _formKey.currentState!.validate()) {
      _formKey.currentState!.save();
      ResultObject<Rate> result = await context
          .read<RateProvider>()
          .submitRate(comment, rateId, isUpdate, isRemoved, imgPath);
      if (context.mounted) result.handleResult(context);
      if (result.isSuccess) {
        Navigator.of(context).pop();
      }
    }
  }

  Future<void> _selectImage() async {
    final source = await selectSourceOption(context);
    if (source != null) {
      image = await context.read<AppImageProvider>().pickImage(source);
    }
    setState(() {});
  }

  void _clearImage() {
    setState(() {
      image = null;
      isRemoved = true;
    });
  }
}
