import 'package:blockchain_mobile/1_controllers/providers/reviews_provider.dart';
import 'package:blockchain_mobile/2_screens/user-review/user_review_screen.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_button.dart';
import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:provider/provider.dart';

class ReviewAction extends StatefulWidget {
  final bool? data;

  final int productId;

  const ReviewAction({super.key, required this.data, required this.productId});

  @override
  State<ReviewAction> createState() => _ReviewActionState();
}

class _ReviewActionState extends State<ReviewAction> {
  late bool isUpdate = false;
  late bool canReview;
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    var canReview = Provider.of<ReviewProvider>(context, listen: false)
        .canReviewResult
        .result;
    isUpdate = canReview == false;
    return IsLoadingButton(
      isLoading: false,
      child: ElevatedButton.icon(
        onPressed: () async {
          if (isUpdate == true) {
            await context
                .read<ReviewProvider>()
                .getCurrentReview(widget.productId);
          }
          if (context.mounted) {
            Navigator.pushNamed(
              context,
              UserReviewScreen.routeName,
              arguments: widget.productId,
            );
          }
        },
        style: ElevatedButton.styleFrom(
          backgroundColor: kBackgroundColor.withHSLlighting(90),
          foregroundColor: Colors.black54,
          minimumSize: const Size(double.infinity, 48),
          shape: const BeveledRectangleBorder(),
        ),
        icon: const FaIcon(
          FontAwesomeIcons.plus,
          size: 18,
        ),
        label: Text(isUpdate ? "Update your Review" : "Submit your review"),
      ),
    );
  }
}
