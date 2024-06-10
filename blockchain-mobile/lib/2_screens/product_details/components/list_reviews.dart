import 'package:blockchain_mobile/1_controllers/providers/reviews_provider.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'review_card.dart';

class ListReviews extends StatelessWidget {
  final int productId;

  const ListReviews(
    this.productId, {
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    var reviewLists = context.watch<ReviewProvider>().getReviewListResult;
    if (reviewLists.isLoading) return const IsLoadingWG(isLoading: true);
    // if (reviewLists.isError) {
    //   return const Center(
    //       child: Padding(
    //     padding: EdgeInsets.all(8.0),
    //     child: Text(
    //       "Can't load review list",
    //       style: TextStyle(fontSize: 16),
    //     ),
    //   ));
    // }
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const ListTile(title: Text("Product Reviews")),
        ...List.generate(reviewLists.result?.length ?? 0, (index) {
          return  ReviewCard(reviewLists.result![index]);
        })
      ],
    );
  }
}
