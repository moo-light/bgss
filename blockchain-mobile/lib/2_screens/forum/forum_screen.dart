import 'package:blockchain_mobile/1_controllers/providers/post_provider.dart';
import 'package:blockchain_mobile/3_components/failed_information.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_screen.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'components/post_filter_drawer.dart';
import 'components/post_item_card.dart';

class PostsScreen extends StatefulWidget {
  static String routeName = "/forum";
  const PostsScreen({
    super.key,
  });

  @override
  State<PostsScreen> createState() => _PostsScreenState();
}

class _PostsScreenState extends State<PostsScreen> {
  @override
  void initState() {
    super.initState();

  }

  final appbar = AppBar(
    title: const Text("Forum"),
  );

  @override
  Widget build(BuildContext context) {
    final watch = Provider.of<PostProvider>(context);
    if (watch.isLoading == true) {
      return LoadingScreen(
        appBar: appbar,
      );
    }
    if (watch.error != null) {
      return FailedInformation(
        child: Text(watch.error.toString()),
      );
    }
    return Scaffold(
      appBar: appbar,
      body: ListView.builder(
        itemCount: watch.postList.length,
        itemBuilder: (BuildContext context, int index) {
          return PostItemCard(post: watch.postList[index]);
        },
      ),
      endDrawer: const PostFilterDrawer(),
    );
  }
}
