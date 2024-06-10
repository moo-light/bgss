import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/rate_provider.dart';
import 'package:blockchain_mobile/2_screens/post_detail/components/rate_card.dart';
import 'package:blockchain_mobile/2_screens/sign_in/sign_in_screen.dart';
import 'package:blockchain_mobile/2_screens/user-rating/user_rating_screen.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading.dart';
import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class ListRating extends StatelessWidget {
  final int productId;

  const ListRating(
    this.productId, {
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    var reviewLists = context.watch<RateProvider>().getRateListResult;
    final user = context.watch<AuthProvider>().currentUser;

    if (reviewLists.isLoading) return const IsLoadingWG(isLoading: true);
    if (reviewLists.isError) {
      return const Center(
          child: Padding(
        padding: EdgeInsets.all(8.0),
        child: Text(
          "Can't load rate list",
          style: TextStyle(fontSize: 16),
        ),
      ));
    }
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Container(
          color: kBackgroundColor.withHSLlighting(90),
          child: ListTile(
            title: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text("User's Rate "),
                Visibility(
                  visible: user != null,
                  child: IconButton(
                    icon: const Icon(Icons.add),
                    onPressed: user != null
                        ? () {
                            UserRateScreen.navigateCreate(context);
                          }
                        : null,
                  ),
                )
              ],
            ),
            onTap: user != null
                ? () {
                    UserRateScreen.navigateCreate(context);
                  }
                : () {
                    Navigator.pushNamed(context, SignInScreen.routeName);
                  },
          ),
        ),
        ...List.generate(reviewLists.result?.length ?? 0, (index) {
          return RateCard(reviewLists.result![index]);
        })
      ],
    );
  }
}
