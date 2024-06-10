import 'package:blockchain_mobile/3_components/card_container.dart';
import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/ci_card_image.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';

class CardImages extends StatefulWidget {
  final List<CiCardImage> ciCardImage;

  const CardImages(
    this.ciCardImage, {
    super.key,
  });

  @override
  State<CardImages> createState() => _CardImageState();
}

class _CardImageState extends State<CardImages> {
  int _index = 0;

  int get maxIndex => widget.ciCardImage.length - 1;

  @override
  Widget build(BuildContext context) {
    return Stack(children: [
      IndexedStack(
        index: _index,
        children: [
          CardContainer(
              child: AspectRatio(
            aspectRatio: 4 / 3,
            child: Container(
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(16),
                // color: kBackgroundColor.withHSLlighting(97),
                border: Border.all(
                  width: 6,
                  color: kPrimaryColor.withHSLlighting(75),
                  strokeAlign: BorderSide.strokeAlignCenter,
                ),
                image: DecorationImage(
                  image: widget.ciCardImage.first.getImage().image,
                  fit: BoxFit.cover,
                ),
              ),
            ),
          )),
          CardContainer(
              child: AspectRatio(
            aspectRatio: 4/3,
            child: Container(
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(16),
                // color: kBackgroundColor.withHSLlighting(97),
                border: Border.all(
                  width: 6,
                  color: kPrimaryColor.withHSLlighting(75),
                  strokeAlign: BorderSide.strokeAlignCenter,
                ),
                image: DecorationImage(
                  image: widget.ciCardImage.first.getImage().image,
                  fit: BoxFit.cover,
                ),
              ),
            ),
          )),
        ],
      ),
      Positioned(
        right: 12,
        child: Row(
          children: [
            IconButton.filled(
              onPressed: _index != 0
                  ? () {
                      setState(() {
                        if (_index == 0) return;
                        _index -= 1;
                      });
                    }
                  : null,
              color: Colors.grey,
              icon: FaIconGen(
                FontAwesomeIcons.caretLeft,
                color: white,
              ),
            ),
            IconButton.filled(
              onPressed: _index != maxIndex
                  ? () {
                      setState(() {
                        if (_index == maxIndex) return;
                        _index += 1;
                      });
                    }
                  : null,
              color: Colors.grey,
              icon: FaIconGen(
                FontAwesomeIcons.caretRight,
                color: white,
              ),
            ),
          ],
        ),
      )
    ]);
  }
}
