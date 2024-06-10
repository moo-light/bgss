class ProductCategory {
  int id;
  String categoryName;
  ProductCategory(this.id, this.categoryName);
  factory ProductCategory.fromJson(Map json) {
    return ProductCategory(json['id'], json['categoryName']);
  }
  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = <String, dynamic>{};
    data['id'] = id;
    data['categoryName'] = categoryName;
    return data;
  }
}
