extension ColorExtension on String {
  String toCapitalize() {
    var strings = toLowerCase().split(" ");
    for (int i = 0; i < strings.length; i++) {
      strings[i] =
          strings[i].substring(0, 1).toUpperCase() + strings[i].substring(1);
    }
    return strings.join(
      " ",
    );
  }
}
