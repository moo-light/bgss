import 'package:blockchain_mobile/1_controllers/providers/products_provider.dart';
import 'package:blockchain_mobile/3_components/product_card.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../product_details/product_details_screen.dart';

class FavoriteScreen extends StatelessWidget {
  const FavoriteScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final demoProducts = context.watch<ProductProvider>().productList;
    return SafeArea(
      child: Column(
        children: [
          Text(
            "Favorites",
            style: Theme.of(context).textTheme.titleLarge,
          ),
          Expanded(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: GridView.builder(
                itemCount: demoProducts.length,
                gridDelegate: const SliverGridDelegateWithMaxCrossAxisExtent(
                  maxCrossAxisExtent: 200,
                  childAspectRatio: 0.7,
                  mainAxisSpacing: 20,
                  crossAxisSpacing: 16,
                ),
                itemBuilder: (context, index) => ProductCard(
                  product: demoProducts[index],
                  onPress: () => Navigator.pushNamed(
                    context,
                    ProductDetailsScreen.routeName,
                    arguments:
                        ProductDetailsArguments(product: demoProducts[index],fromCartScreen: false),
                  ),
                ),
              ),
            ),
          )
        ],
      ),
    );
  }
}
