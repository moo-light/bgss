import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';

class ProductSearch extends StatefulWidget {
  const ProductSearch({
    super.key,
    required this.isOpen,
    required this.onChanged,
  });
  final bool isOpen;
  final Function(String value) onChanged;
  @override
  State<ProductSearch> createState() => _ProductSearchState();
}

class _ProductSearchState extends State<ProductSearch> {
  final TextEditingController search = TextEditingController();
  num lastKeyStroke = double.infinity;
  num waitForInputInterval = 300; //miliseconds
  @override
  Widget build(BuildContext context) {
    const inputDecoration = UnderlineInputBorder(
        borderSide: BorderSide(width: 1, color: Colors.black12));
    return TextFormField(
      autofocus: false,
      key: widget.key,
      controller: search,
      onChanged: widget.onChanged,
      decoration: InputDecoration(
        hintText: "Search...",
        border: inputDecoration,
        enabledBorder: inputDecoration,
        focusedBorder: inputDecoration,
        prefix: null,
        prefixIcon: null,
        fillColor: kBackgroundColor.withHSLlighting(92),
        filled: true,
        // suffixIcon: IconButton(
        //   onPressed: () {
        //     search.text = "";
        //     context.read<ProductProvider>().getProductList(search.text);
        //   },
        //   icon: FaIconGen(FontAwesomeIcons.circleXmark,
        //       color: Theme.of(context).iconTheme.color),
        // ),
      ),
    );
  }
}