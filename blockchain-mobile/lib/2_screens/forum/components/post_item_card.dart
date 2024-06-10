import 'package:blockchain_mobile/1_controllers/providers/post_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/rate_provider.dart';
import 'package:blockchain_mobile/2_screens/post_detail/post_detail_screen.dart';
import 'package:blockchain_mobile/4_helper/decorations/card_decoration.dart';
import 'package:blockchain_mobile/4_helper/image.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/Product.dart';
import 'package:blockchain_mobile/models/post.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class PostItemCard extends StatelessWidget {
  final Post? post;

  const PostItemCard({super.key, required this.post});

  @override
  Widget build(BuildContext context) {
    if (post == null) return Container();
    return InkWell(
      onTap: () {
        context.read<PostProvider>().getPostById(post!.id);
        context.read<RateProvider>().getRateList(post!.id);
        Navigator.pushNamed(context, PostDetailsScreen.routeName,
            arguments: post);
      },
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Stack(children: [
          AspectRatio(
            aspectRatio: 4 / 3,
            child: Container(
              width: double.infinity,
              clipBehavior: Clip.hardEdge,
              constraints: const BoxConstraints(maxHeight: 244, minHeight: 244),
              decoration: cardDecoration.copyWith(
                image: DecorationImage(
                  image: NetworkImage(
                    (ImageHelper.getServerImgUrl(
                      post?.textImg ?? "",
                      Product.defaultImageUrl,
                    )),
                  ),
                  fit: BoxFit.cover,
                ),
                boxShadow: [
                  const BoxShadow(
                    color: kIconColor,
                    blurRadius: 16,
                  )
                ],
                borderRadius: BorderRadius.circular(16),
              ),
              child: SizedBox(
                height: 30,
                child: Container(
                  decoration: const BoxDecoration(
                      gradient: LinearGradient(
                    begin: Alignment.topCenter,
                    end: Alignment.bottomCenter,
                    stops: [0.3, 0.6, 1],
                    colors: [
                      Colors.black12,
                      Colors.black45,
                      Colors.black,
                    ],
                  )),
                ),
              ),
            ),
          ),
          Positioned(
            bottom: 0,
            left: 0,
            right: 0,
            child: Container(
              padding: const EdgeInsets.all(8),
              child: Text(
                post!.title,
                maxLines: 3,
                style: TextStyle(
                  fontWeight: FontWeight.bold,
                  fontSize: 16,
                  color: white,
                ),
              ),
            ),
          ),
        ]),
      ),
    );
  }
}
