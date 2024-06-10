import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/2_screens/user-rating/user_rating_screen.dart';
import 'package:blockchain_mobile/3_components/image_view.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/rate.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:gap/gap.dart';
import 'package:provider/provider.dart';

class RateCard extends StatelessWidget {
  final Rate rate;

  const RateCard(
    this.rate, {
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    var image = rate.user.userInfo.image;
    var userId = context.watch<AuthProvider>().currentUser?.userId;
    var user = context.watch<AuthProvider>().currentUser;
    var userLogined = context.watch<AuthProvider>().userLogined;
    return ListTile(
      titleAlignment: ListTileTitleAlignment.top,
      leading: CircleAvatar(
        backgroundImage: image.image,
      ),
      title: Text.rich(
        TextSpan(
          text:
              "${rate.user.userInfo.firstName} ${rate.user.userInfo.lastName}\n",
          children: [
            TextSpan(
              text: dateFormat(rate.createDate),
              style: Theme.of(context).textTheme.bodySmall!.copyWith(
                    color: kHintTextColor,
                  ),
            ),
          ],
        ),
      ),
      onTap: rate.user.userId == user?.userId
          ? () => UserRateScreen.navigateUpdate(context, rate.id)
          : null,
      subtitle: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(rate.content),
          const Gap(10),
          Visibility(
            visible: rate.imgUrl != null,
            child: GestureDetector(
              onTap: () {
                ImageView.view(context, rate.image.image);
              },
              child: rate.image != null
                  ? Container(
                      width: 100,
                      height: 100,
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(8),
                        image: DecorationImage(
                          image: rate.image.image,
                          fit: BoxFit.cover,
                        ),
                      ),
                    )
                  : null,
            ),
          )
        ],
      ),
      trailing: Visibility(
        visible: userId == rate.user.userId,
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
