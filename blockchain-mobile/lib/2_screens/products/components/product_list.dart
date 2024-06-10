import 'package:blockchain_mobile/1_controllers/providers/products_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/reviews_provider.dart';
import 'package:blockchain_mobile/2_screens/product_details/product_details_screen.dart';
import 'package:blockchain_mobile/3_components/failed_information.dart';
import 'package:blockchain_mobile/3_components/product_card.dart';
import 'package:blockchain_mobile/models/Product.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class ProductList extends StatelessWidget {
  const ProductList({
    super.key,
    required this.isOpen,
    required this.demoProducts,
  });

  final bool isOpen;
  final List<Product> demoProducts;

  @override
  Widget build(BuildContext context) {
    return Expanded(
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 16),
        child: Visibility(
          visible: demoProducts.isNotEmpty,
          replacement: const FailedInformation(
            child: Text("No Product Yet"),
          ),
          child: GridView.builder(
            itemCount: demoProducts.length,
            // physics: const NeverScrollableScrollPhysics(),
            gridDelegate: const SliverGridDelegateWithMaxCrossAxisExtent(
              maxCrossAxisExtent: 200,
              childAspectRatio: 0.7,
              mainAxisSpacing: 20,
              crossAxisSpacing: 16,
            ),
            itemBuilder: (context, index) => ProductCard(
              product: demoProducts[index],
              onPress: () {
                context
                    .read<ReviewProvider>()
                    .canReview(demoProducts[index].id);
                context
                    .read<ReviewProvider>()
                    .getReviewList(demoProducts[index].id);
                context
                    .read<ProductProvider>()
                    .getProductById(demoProducts[index].id);
                Navigator.pushNamed(
                  context,
                  ProductDetailsScreen.routeName,
                  arguments: ProductDetailsArguments(
                    product: demoProducts[index],
                    fromCartScreen: false,
                  ),
                );
              },
            ),
          ),
        ),
      ),
    );
  }
}
