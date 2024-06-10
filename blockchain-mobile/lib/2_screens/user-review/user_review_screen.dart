import 'dart:io';

import 'package:blockchain_mobile/1_controllers/providers/image_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/reviews_provider.dart';
import 'package:blockchain_mobile/2_screens/sign_up/validators.dart';
import 'package:blockchain_mobile/3_components/dialogs/select_source_option_dialog.dart';
import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/4_helper/decorations/text_styles.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/dtos/result_object.dart';
import 'package:blockchain_mobile/models/review.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:gap/gap.dart';
import 'package:image_picker/image_picker.dart';
import 'package:provider/provider.dart';

import '../../3_components/image_selector.dart';

class UserReviewScreen extends StatefulWidget {
  static const routeName = "/product/details/user-review";
  const UserReviewScreen({super.key});

  @override
  State<UserReviewScreen> createState() => _UserReviewScreenState();
}

class _UserReviewScreenState extends State<UserReviewScreen> {
  int starSelected = -1;
  bool isStarError = false;
  bool isRemoved = false; // only for Update image
  String comment = "";

  XFile? image;
  late ResultObject<Review> currentReview;

  String? get imgPath => image?.path;

  late int productid;
  late bool isUpdate;
  final _formKey = GlobalKey<FormState>();
  @override
  void initState() {
    currentReview =
        Provider.of<ReviewProvider>(context, listen: false).currentReview;
    if (currentReview.isSuccess) {
      comment = currentReview.result!.content ?? "";
      starSelected = currentReview.result!.numOfReviews ?? -1;
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
    var canReview = context.watch<ReviewProvider>().canReviewResult.result;
    ImageProvider<Object> currentImage = FileImage(File(""));
    if (canReview == false) {
      isUpdate = canReview == false;
    } else {
      isUpdate = false;
    }
    var haveImage = false; //this is use to render image
    if (image != null) {
      currentImage = FileImage(File(image!.path)); // set image if changed
      haveImage = true;
    } else if (isUpdate && currentReview.result?.imgUrl != null && !isRemoved) {
      //default Image if Image exist
      currentImage = currentReview.result!.image.image;
      haveImage = true;
    }
    productid = ModalRoute.of(context)!.settings.arguments as int;
    return Scaffold(
        resizeToAvoidBottomInset: false,
        appBar: AppBar(
          title: Text("Review Product #$productid ${isUpdate ? "Update" : ""}"),
        ),
        body: RefreshIndicator(
          onRefresh: () async {
            await context.read<ReviewProvider>().canReview(productid);
          },
          child: SafeArea(
            child: Builder(
              builder: (context) {
                if (canReview == null) {
                  return const Center(
                      child: Text(
                    "User can review only after this product is purchased",
                    style: TextStyle(fontSize: 20),
                    textAlign: TextAlign.center,
                  ));
                }
                final isUpdate = !canReview;
                return Padding(
                  padding: const EdgeInsets.all(16.0),
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Text(
                        "Star Rating",
                        style: TextStyle(fontSize: 20),
                      ),
                      const Gap(5),
                      Row(
                        children: List.generate(
                          5,
                          (jndex) => GestureDetector(
                            onTap: () {
                              setState(() {
                                starSelected = jndex + 1;
                              });
                            },
                            child: FaIconGen(
                              FontAwesomeIcons.solidStar,
                              margin: const EdgeInsets.symmetric(horizontal: 2),
                              color:
                                  jndex < starSelected ? kSecondaryColor : null,
                            ),
                          ),
                        ),
                      ),
                      Visibility(
                          visible: isStarError && starSelected == -1,
                          child: Padding(
                            padding: const EdgeInsets.only(top: 4.0),
                            child: Text(
                              "Please select a rating",
                              style: Theme.of(context)
                                  .textTheme
                                  .bodySmall!
                                  .copyWith(color: kLossColor),
                            ),
                          )),
                      const Gap(10),
                      const Text(
                        "Review",
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
                              hintText: "Enter your Review",
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
                          child: Text(
                              isUpdate ? "Update Review" : "Submit Review"),
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
    if (starSelected == -1) {
      setState(() {
        isStarError = true;
      });
      return;
    }

    if (image != null || _formKey.currentState!.validate()) {
      _formKey.currentState!.save();
      ResultObject<Review> result = await context
          .read<ReviewProvider>()
          .submitReview(
              starSelected, comment, productid, isUpdate, isRemoved, imgPath);
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
