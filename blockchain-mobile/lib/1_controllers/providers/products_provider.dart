import 'package:blockchain_mobile/1_controllers/repositories/category_repository.dart';
import 'package:blockchain_mobile/1_controllers/repositories/product_repository.dart';
import 'package:blockchain_mobile/models/Product.dart';
import 'package:blockchain_mobile/models/gold_type.dart';
import 'package:blockchain_mobile/models/product_category.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/foundation.dart';

class ProductProvider extends ChangeNotifier {
  Product? _selectedProduct;
  final ProductRepository _productRepository = ProductRepository();
  final CategoryRepository _categoryRepository = CategoryRepository();
  bool isLoading = true;

  String? error;

  //data
  List<Product> productList = [];
  List<ProductCategory> categories = [];
  List<GoldType> typeGolds = [];
  Product? productDetail;

  getProductList({
    String? search,
    bool? asc,
    int? min,
    int? max,
    List<int>? categoryIds,
    List<int>? ratings,
    List<int>? typeGoldIds,
  }) async {
    isLoading = true;
    error = null;
    notifyListeners();
    Either<dynamic, String?> result = await _productRepository.getProductList(
      search: search,
      asc: asc,
      min: min,
      max: max,
      categoryIds: categoryIds,
      typeGoldIds: typeGoldIds,
      ratings: ratings,
    );
    if (result.isLeft) {
      productList = result.left;
    }
    if (result.isRight) {
      error = result.right;
    }
    isLoading = false;
    notifyListeners();
  }

  getProductById(int id) async {
    isLoading = true;
    productDetail = null;
    error = null;
    notifyListeners();
    Either<Product, String?> result = await _productRepository.getById(id);
    if (result.isLeft) {
      productDetail = result.left;
    }
    if (result.isRight) {
      // error = result.right;
    }
    isLoading = false;
    notifyListeners();
  }

  Future getCategories(context) async {
    final result = await _categoryRepository.getCategories();
    if (result.isLeft) {
      categories = result.left;
    }
    if (result.isRight) {}
    notifyListeners();
  }

  Future getTypeGolds(context) async {
    final result = await _categoryRepository.getTypeGolds();
    if (result.isLeft) {
      typeGolds = result.left;
    }
    if (result.isRight) {}
    notifyListeners();
  }

  get24kGoldProductList() async {
    isLoading = true;
    error = null;
    notifyListeners();
    Either<dynamic, String?> result =
        await _productRepository.getProductList24kGold();
    if (result.isLeft) {
      productList = result.left;
    }
    if (result.isRight) {
      error = result.right;
    }
    isLoading = false;
    notifyListeners();
  }

  set selectedProduct(Product? p) {
    _selectedProduct = p;
    notifyListeners();
  }

  Product? get sProduct => _selectedProduct;
}
