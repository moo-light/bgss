import 'package:flutter/material.dart';

import '../../../3_components/image_view.dart';
import '../../../constants.dart';
import '../../../models/Product.dart';

class ProductImages extends StatefulWidget {
  const ProductImages({
    super.key,
    required this.product,
  });

  final Product product;

  @override
  _ProductImagesState createState() => _ProductImagesState();
}

class _ProductImagesState extends State<ProductImages> {
  int selectedImage = 0;

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    var displayImage = Product.displayImage(null);
    try {
      displayImage =
          Product.displayImage(widget.product.productImages![selectedImage]);
    } catch (e) {}
    return Column(
      children: [
        SizedBox(
          width: 238,
          child: AspectRatio(
            aspectRatio: 1,
            child: GestureDetector(
              child: Container(
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(8),
                  image: DecorationImage(
                    image: displayImage.image,
                    fit: BoxFit.cover,
                  ),
                ),
              ),
              onTap: () => Navigator.push(
                this.context,
                MaterialPageRoute(
                  builder: (context) => ImageView(
                    image: displayImage.image,
                  ),
                ),
              ),
            ),
          ),
        ),
        const SizedBox(height: 24),
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            ...List.generate(
              widget.product.productImages?.length ?? 0,
              (index) => SmallProductImage(
                isSelected: index == selectedImage,
                press: () {
                  setState(() {
                    selectedImage = index;
                  });
                },
                image:
                    Product.displayImage(widget.product.productImages![index]),
              ),
            ),
            Visibility(
              visible: (widget.product.productImages?.length ?? 0) == 0,
              child: SmallProductImage(
                isSelected: true,
                press: () {},
                image: displayImage,
              ),
            )
          ],
        )
      ],
    );
  }
}

class SmallProductImage extends StatefulWidget {
  const SmallProductImage(
      {super.key,
      required this.isSelected,
      required this.press,
      required this.image});

  final bool isSelected;
  final VoidCallback press;
  final Image? image;

  @override
  State<SmallProductImage> createState() => _SmallProductImageState();
}

class _SmallProductImageState extends State<SmallProductImage>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    _controller = AnimationController(
      duration: const Duration(seconds: 10),
      vsync: this,
    )..forward();
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: widget.press,
      child: Transform.scale(
        scale: widget.isSelected ? 1.15 : 1,
        child: AnimatedContainer(
          duration: defaultDuration,
          margin: const EdgeInsets.symmetric(horizontal: 8),
          padding: const EdgeInsets.all(8),
          height: 48,
          width: 48,
          decoration: BoxDecoration(
            color: Colors.white,
            borderRadius: BorderRadius.circular(10),
            border: Border.all(
                color: kPrimaryColor.withOpacity(widget.isSelected ? 1 : 0)),
          ),
          child: widget.image,
        ),
      ),
    );
  }
}
