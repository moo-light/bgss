import 'package:blockchain_mobile/1_controllers/providers/products_provider.dart';
import 'package:blockchain_mobile/3_components/product_card.dart';
import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/Product.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class ProductsScreen extends StatelessWidget {
  const ProductsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final products = context.watch<ProductProvider>().productList;
    Product? selectedProduct = context.watch<ProductProvider>().sProduct;
    return Scaffold(
      appBar: AppBar(
        title: const Text("Select a Product"),
      ),
      body: GridView.builder(
        itemCount: products.length,
        // physics: const NeverScrollableScrollPhysics(),
        gridDelegate: const SliverGridDelegateWithMaxCrossAxisExtent(
          maxCrossAxisExtent: 200,
          childAspectRatio: 0.7,
          mainAxisSpacing: 20,
          crossAxisSpacing: 16,
        ),
        padding: const EdgeInsets.all(16),
        itemBuilder: (context, index) => Container(
          decoration: selectedProduct?.id == products[index].id
              ? BoxDecoration(boxShadow: [
                  BoxShadow(
                      blurRadius: 0,
                      color: kPrimaryColor.withHSLlighting(95),
                      spreadRadius: 10)
                ])
              : null,
          child: GestureDetector(
            onDoubleTap: () {
              context.read<ProductProvider>().selectedProduct = products[index];
              Navigator.pop(context);
            },
            onTap: () {
              context.read<ProductProvider>().selectedProduct = products[index];
            },
            child: AbsorbPointer(
              child: ProductCard(
                product: products[index],
                showWeight: true,
                onPress: () {
                  context.read<ProductProvider>().selectedProduct =
                      products[index];
                },
              ),
            ),
          ),
        ),
      ),
    );
  }
}
