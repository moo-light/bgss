import 'package:blockchain_mobile/1_controllers/providers/order_provider.dart';
import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:provider/provider.dart';

class FindOrderScreen extends StatefulWidget {
  static const String routeName = "/search-order";

  const FindOrderScreen({super.key});

  @override
  State<FindOrderScreen> createState() => _FindOrderScreenState();
}

class _FindOrderScreenState extends State<FindOrderScreen> {
  List<Map<String, dynamic>> selectionList = [
    {
      "label": "Orders",
    },
    // {
    //   "label": "Withdraws",
    // },
  ];
  int selection = 0;
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Find ${selectionList[selection]["label"]}"),
      ),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Column(
          children: [
            // Container(
            //   margin: const EdgeInsets.all(8),
            //   child: SingleChildScrollView(
            //     scrollDirection: Axis.horizontal,
            //     child: Wrap(
            //       spacing: 10,
            //       crossAxisAlignment: WrapCrossAlignment.center,
            //       children: [
            //         ...List.generate(
            //           selectionList.length,
            //           (index) {
            //             return SelectionComponents(
            //               selected: index == selection,
            //               onPressed: () {
            //                 setState(() {
            //                   selection = index;
            //                 });
            //               },
            //               label: selectionList[index]["label"],
            //             );
            //           },
            //         ),
            //       ],
            //       // Flatten the List<Widget> to avoid nesting Rows unnecessarily
            //     ),
            //   ),
            // ),
            // const SizedBox(height: 10),
            _buildSearchBar(context),
          ],
        ),
      ),
    );
  }

  var search = TextEditingController();

  Widget _buildSearchBar(BuildContext context) {
    return Form(
      child: TextFormField(
        decoration: InputDecoration(
          hintText: "Paste code here",
          suffixIcon: GestureDetector(
            onTap: () {
              if (search.text.isNotEmpty) {
                switch (selection) {
                  case 0:
                    Provider.of<OrderProvider>(context, listen: false)
                        .getDetailOrder(context, code: search.text);
                    return;

                }
              }
            },
            child: FaIconGen(
              FontAwesomeIcons.magnifyingGlass,
              width: 20,
              color: search.text.isNotEmpty ? kPrimaryColor : null,
            ),
          ),
          border: const OutlineInputBorder(
              borderRadius: BorderRadius.all(Radius.circular(100))),
        ),
        onChanged: (value) => setState(() {}),
        controller: search,
        onTapOutside: (event) {
          FocusScope.of(context).unfocus();
        },
      ),
    );
  }
}
