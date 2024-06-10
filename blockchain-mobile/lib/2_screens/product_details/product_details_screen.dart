import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/cart_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/products_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/reviews_provider.dart';
import 'package:blockchain_mobile/2_screens/cart/cart_screen.dart';
import 'package:blockchain_mobile/2_screens/product_details/components/color_dots.dart';
import 'package:blockchain_mobile/2_screens/product_details/components/product_description.dart';
import 'package:blockchain_mobile/2_screens/product_details/components/product_images.dart';
import 'package:blockchain_mobile/2_screens/product_details/components/review_action.dart';
import 'package:blockchain_mobile/2_screens/sign_in/sign_in_screen.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_screen.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/Cart.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:gap/gap.dart';
import 'package:provider/provider.dart';

import '../../models/Product.dart';
import 'components/list_reviews.dart';
import 'components/top_rounded_container.dart';

class ProductDetailsScreen extends StatelessWidget {
  static String routeName = "/product/details";

  const ProductDetailsScreen({super.key});
  @override
  Widget build(BuildContext context) {
    final ProductDetailsArguments agrs =
        ModalRoute.of(context)!.settings.arguments as ProductDetailsArguments;

    if (context.watch<ProductProvider>().productDetail == null) {
      return const LoadingScreen();
    }

    final product = context.watch<ProductProvider>().productDetail!;
    final fromCartScreen = agrs.fromCartScreen;
    final userLogined = context.watch<AuthProvider>().userLogined;
    final cart = Cart(product: product, quantity: 1);
    final double ratingAvg = cart.product.avgReview;

    var canReview = context.watch<ReviewProvider>().canReviewResult;
    return Scaffold(
      extendBody: true,
      extendBodyBehindAppBar: true,
      backgroundColor: const Color(0xFFF5F6F9),
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        leading: Padding(
          padding: const EdgeInsets.all(8.0),
          child: ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
            },
            style: ElevatedButton.styleFrom(
              shape: const CircleBorder(),
              padding: EdgeInsets.zero,
              elevation: 0,
              backgroundColor: Colors.white,
            ),
            child: const Icon(
              Icons.arrow_back_ios_new,
              color: Colors.black,
              size: 20,
            ),
          ),
        ),
        actions: [
          Row(
            children: [
              Container(
                margin: const EdgeInsets.only(right: 5),
                padding:
                    const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(14),
                ),
                child: Row(
                  children: [
                    Text(
                      ratingAvg.toStringAsFixed(1),
                      style: const TextStyle(
                        fontSize: 14,
                        color: Colors.black,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    const SizedBox(width: 4),
                    SvgPicture.asset("assets/icons/Star Icon.svg"),
                  ],
                ),
              ),
              // Padding(
              //   padding: const EdgeInsets.symmetric(horizontal: 4.0),
              //   child: IconButton(
              //     onPressed: () {},
              //     style: IconButton.styleFrom(backgroundColor: Colors.white),
              //     icon: SvgPicture.asset(
              //       "assets/icons/Heart Icon.svg",
              //       colorFilter:
              //           const ColorFilter.mode(Colors.grey, BlendMode.srcIn),
              //     ),
              //   ),
              // ),
            ],
          ),
        ],
      ),
      body: ListView(
        children: [
          ProductImages(product: product),
          TopRoundedContainer(
            color: Colors.white,
            child: Column(
              children: [
                ProductDescription(
                  product: product,
                ),
                Visibility(
                  visible: !fromCartScreen,
                  child: TopRoundedContainer(
                    color: const Color(0xFFF6F7F9),
                    child: Column(
                      children: [
                        ColorDots(
                          cart: cart,
                        ),
                        const Divider(height: 20),
                        Visibility(
                          visible: !userLogined,
                          replacement: Builder(
                            builder: (context) {
                              return ReviewAction(
                                data: canReview.result,
                                productId: product.id,
                              );
                            },
                          ),
                          child: AlertDialog(
                            backgroundColor: Colors.red.shade100,
                            alignment: Alignment.bottomCenter,
                            content: const Text(
                              "Please Sign in to write your Review.",
                            ),
                          ),
                        )
                      ],
                    ),
                  ),
                ),
              ],
            ),
          ),
          _buildBottomView(fromCartScreen, context, product),
        ],
      ),
      bottomNavigationBar: !fromCartScreen
          ? TopRoundedContainer(
              color: Colors.white,
              child: SafeArea(
                child: Padding(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 20, vertical: 12),
                  child: ElevatedButton(
                    style: ElevatedButton.styleFrom(
                      minimumSize: const Size(double.infinity, 48),
                    ),
                    onPressed: () {
                      context.read<CartProvider>().addToCart(cart).then(
                          (v) => context.read<CartProvider>().loadCarts());
                      if (userLogined) {
                        Navigator.pushNamed(context, CartScreen.routeName);
                      } else {
                        Navigator.pushNamed(context, SignInScreen.routeName);
                      }
                    },
                    child: const Text("Add To Cart"),
                  ),
                ),
              ),
            )
          : null,
    );
  }

  Visibility _buildBottomView(
      bool fromCartScreen, BuildContext context, Product product) {
    return Visibility(
      visible: fromCartScreen,
      replacement: ListReviews(product.id),
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 50),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              "This is a view only product screen",
              style: TextStyle(color: kHintTextColor),
            ),
            const Gap(20),
            Center(
              child: TextButton(
                onPressed: () {
                  context.read<ReviewProvider>().canReview(product.id);
                  context.read<ReviewProvider>().getReviewList(product.id);
                  Navigator.popUntil(context, (route) => route.isFirst);
                  Navigator.pushNamed(context, ProductDetailsScreen.routeName,
                      arguments: ProductDetailsArguments(product: product));
                },
                child: const Text("View product"),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class ProductDetailsArguments {
  final Product product;
  final bool fromCartScreen;

  ProductDetailsArguments({required this.product, this.fromCartScreen = false});
}
