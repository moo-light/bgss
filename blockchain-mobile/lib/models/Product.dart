import 'package:blockchain_mobile/4_helper/image.dart';
import 'package:blockchain_mobile/models/gold_type.dart';
import 'package:blockchain_mobile/models/product_category.dart';
import 'package:flutter/material.dart';

class Product {
  static const String defaultImageUrl = "assets/images/Default Product.png";

  final int id;
  final String title, description;
  final List<String>? productImages;
  final int unitOfStock;
  final double rating, price, weight;
  final double? secondPrice;
  final double? percentageReduce;
  final bool isFavorite, isPopular;
  final ProductCategory category;
  final GoldType typeGold;
  final double avgReview;

  Product({
    required this.id,
    this.productImages,
    required this.unitOfStock,
    this.rating = 0.0,
    this.isFavorite = false,
    this.isPopular = false,
    required this.title,
    required this.price,
    required this.weight,
    this.secondPrice,
    this.percentageReduce,
    required this.description,
    required this.category,
    required this.typeGold,
    required this.avgReview,
  });

  factory Product.fromJson(Map<String, dynamic> json) {
    List<String> images = json['productImages'] != null
        ? (json['productImages'] as List)
            .map((img) => img['imgUrl'].toString())
            .toList()
        : [];
    final imgUrl = images.firstOrNull;
    return Product(
      id: json['id'] ?? 0,
      title: json['productName'] ?? '',
      productImages: json['productImages'] != null
          ? (json['productImages'] as List<dynamic>)
              .map((img) => ImageHelper.getServerImgUrl(
                  img['imgUrl'] as String, defaultImageUrl))
              .toList()
          : [],
      description: json['description'] ?? '',
      price: (json['priceProduct'] ?? 0.0).toDouble(),
      weight: (json['weight'] ?? 0.0).toDouble(),
      secondPrice: json['secondPrice'],
      percentageReduce: (json['percentageReduce'] ?? 0.0),
      unitOfStock: json['unitOfStock'] ?? 0,
      category: ProductCategory.fromJson(json['category']),
      typeGold: GoldType.fromJson(json['typeGold']),
      avgReview: (json['avgReview'] ?? 0.0),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'productName': title,
      'productImages': productImages,
      'description': description,
      'price': price,
      'weight': weight,
      'secondPrice': secondPrice,
      'percentageReduce': percentageReduce,
      'unitOfStock': unitOfStock,
      'category': category.toJson(),
      'typeGold': typeGold.toJson(),
      'avgReview': avgReview,
    };
  }

  Image get image {
    if (productImages?.isNotEmpty ?? false) {
      return displayImage(productImages?.first);
    }
    return Image.asset(defaultImageUrl, fit: BoxFit.cover);
  }

  static Image displayImage(String? imgUrl) {
    if (imgUrl != null && imgUrl.isNotEmpty) {
      return Image.network(
        imgUrl,
        fit: BoxFit.cover,
      );
    } else {
      return Image.asset(defaultImageUrl, fit: BoxFit.cover);
    }
  }
}
