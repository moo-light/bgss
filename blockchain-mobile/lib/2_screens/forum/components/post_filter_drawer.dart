import 'package:blockchain_mobile/1_controllers/providers/post_provider.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class PostFilterDrawer extends StatelessWidget {
  const PostFilterDrawer({super.key});

  @override
  Widget build(BuildContext context) {
     final watch = Provider.of<PostProvider>(context);
    return const Drawer(
      child: Text(""),
    );
  }
}
