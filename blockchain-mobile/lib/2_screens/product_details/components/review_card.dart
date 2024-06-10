import 'dart:io';

import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/3_components/image_view.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/review.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:gap/gap.dart';
import 'package:provider/provider.dart';

class ReviewCard extends StatelessWidget {
  final Review review;

  const ReviewCard(
    this.review, {
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    var image = review.user!.userInfo.image;
    var userId = context.watch<AuthProvider>().currentUser?.userId;
    return ListTile(
      titleAlignment: ListTileTitleAlignment.top,
      leading: CircleAvatar(
        backgroundImage: image.image,
      ),
      title: Text.rich(
        TextSpan(
          text:
              "${review.user?.userInfo.firstName} ${review.user?.userInfo.lastName}\n",
          children: [
            TextSpan(
              text: dateFormat(review.createDate),
              style: Theme.of(context).textTheme.bodySmall!.copyWith(
                    color: kHintTextColor,
                  ),
            ),
            WidgetSpan(
              child: Row(
                children: List.generate(
                  5,
                  (jndex) => FaIconGen(
                    FontAwesomeIcons.solidStar,
                    width: 15,
                    margin: const EdgeInsets.symmetric(horizontal: 2),
                    color: jndex < review.numOfReviews ? kSecondaryColor : null,
                  ),
                ),
              ),
            )
          ],
        ),
      ),
      subtitle: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(review.content),
          const Gap(10),
          Visibility(
            visible: review.imgUrl != null,
            child: GestureDetector(
              onTap: () {
                ImageView.view(context, review.image.image);
              },
              child: Container(
                width: 100,
                height: 100,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(8),
                  image: DecorationImage(
                    image: review.image.image ??
                        FileImage(
                          File(""),
                        ),
                    fit: BoxFit.cover,
                  ),
                ),
                // child: review.getImage(),
              ),
            ),
          )
        ],
      ),
      trailing: Visibility(
        visible: userId == review.user?.userId,
        child: IconButton(
          visualDensity: VisualDensity.compact,
          onPressed: () {},
          style: IconButton.styleFrom(
            iconSize: 15,
            backgroundColor: white,
            foregroundColor: Colors.black,
          ),
          icon: const FaIcon(
            FontAwesomeIcons.ellipsis,
          ),
        ),
      ),
    );
  }
}
