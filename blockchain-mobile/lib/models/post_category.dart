class PostCategory {
  final int id;
  final String categoryName;

  PostCategory({required this.id, required this.categoryName});

  factory PostCategory.fromJson(Map<String, dynamic> json) {
    return PostCategory(
      id: json['id'],
      categoryName: json['categoryName'],
    );
  }
}