import 'package:blockchain_mobile/1_controllers/providers/post_provider.dart';
import 'package:blockchain_mobile/2_screens/post_detail/components/list_ratings.dart';
import 'package:blockchain_mobile/3_components/failed_information.dart';
import 'package:blockchain_mobile/3_components/text_bold.dart';
import 'package:blockchain_mobile/4_helper/image.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/Product.dart';
import 'package:blockchain_mobile/models/post.dart';
import 'package:flutter/material.dart';
import 'package:flutter_html/flutter_html.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:html/parser.dart' as htmlparser;
import 'package:provider/provider.dart';

class PostDetailsScreen extends StatefulWidget {
  static String routeName = "/forum-detail";
  const PostDetailsScreen({
    super.key,
  });

  @override
  State<PostDetailsScreen> createState() => _PostsScreenState();
}

class _PostsScreenState extends State<PostDetailsScreen> with TickerProviderStateMixin {
  late Post post;
  bool showbtn = false;
  final _scrollController = ScrollController();
  late final AnimationController _controller;

  @override
  void initState() {
    super.initState();

    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 300),
    );

    _scrollController.addListener(() {
      double showoffset = MediaQuery.of(context).size.height*1.5;

      if (_scrollController.offset > showoffset && !showbtn) {
        showbtn = true;
        _controller.forward();
      } else if (_scrollController.offset <= showoffset && showbtn) {
        showbtn = false;
        _controller.reverse();
      }
    });
  }

  @override
  void dispose() {
    _scrollController.dispose();
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    post = ModalRoute.of(context)?.settings.arguments as Post;
    final appbar = AppBar(
      title: Text("Post #${post.id}"),
    );
    final watch = Provider.of<PostProvider>(context);
    if (watch.error != null) {
      return FailedInformation(
        child: Text(watch.error.toString()),
      );
    }
    return Scaffold(
      appBar: appbar,
      body: ListView(controller: _scrollController, children: [
        AspectRatio(
          aspectRatio: 4 / 3,
          child: Container(
            width: double.infinity,
            decoration: BoxDecoration(
              image: DecorationImage(
                image: NetworkImage(
                  ImageHelper.getServerImgUrl(
                    post.textImg ?? "",
                    Product.defaultImageUrl,
                  ),
                ),
                fit: BoxFit.cover,
              ),
            ),
          ),
        ),
        Container(
          padding: const EdgeInsets.all(8),
          child: Text(
            post.title,
            maxLines: 3,
            style: const TextStyle(
              fontWeight: FontWeight.bold,
              fontSize: 24,
              color: kTextColor,
            ),
          ),
        ),
        ListTile(
          leading: CircleAvatar(child: post.user.userInfo.image),
          title: TextBold("${post.user.userInfo.firstName} ${post.user.userInfo.lastName}"),
          subtitle: Text(dateFormat(post.createDate)),
        ),
        Padding(
          padding: const EdgeInsets.all(8.0),
          child: Html.fromDom(
            document: htmlparser.parse(post.content),
            style: {
              "body": Style(padding: HtmlPaddings.symmetric(horizontal: 8))
            },
          ),
        ),
        ListRating(post.id),
      ]),
      floatingActionButton: AnimatedBuilder(
        animation: _controller,
        builder: (context, child) {
          return Transform.translate(
            offset: Offset(0, 100 * (1 - _controller.value)),
            child: child,
          );
        },
        child: FloatingActionButton.small(
          onPressed: () {
            _scrollController.animateTo(
              0, // scroll offset to go
              duration: const Duration(milliseconds: 500), // duration of scroll
              curve: Curves.fastOutSlowIn, // scroll type
            );
          },
          child: const FaIcon(FontAwesomeIcons.caretUp),
        ),
      ),
    );
  }
}
