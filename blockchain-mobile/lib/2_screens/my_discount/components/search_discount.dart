import 'package:blockchain_mobile/1_controllers/providers/discount_provider.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:provider/provider.dart';

import '../../../models/user_discount.dart';
import '../../discount/discount_card.dart';

class SearchDiscount extends StatefulWidget {
  const SearchDiscount({
    super.key,
  });

  @override
  State<SearchDiscount> createState() => _SearchDiscountState();
}

class _SearchDiscountState extends State<SearchDiscount> {
  final searchController = TextEditingController();
  final formKey = GlobalKey<FormState>();
  @override
  Widget build(BuildContext context) {
    var currentDiscount =
        context.watch<DiscountProvider>().getDiscountCodeResult;
        print(currentDiscount);
    return Container(
      // margin: const EdgeInsets.all(16),
      // padding: const EdgeInsets.all(16),
      // decoration: cardDecoration,
      child: Column(children: [
        // Form(
        //   key: formKey,
        //   child: TextFormField(
        //     decoration: InputDecoration(
        //       labelText: "Search discount",
        //       suffixIcon: GestureDetector(
        //         onTap: () {
        //           if (searchController.text.isNotEmpty) {
        //             context
        //                 .read<DiscountProvider>()
        //                 .getDiscountCode(searchController.text);
        //             return;
        //           }
        //         },
        //         child: FaIconGen(
        //           FontAwesomeIcons.magnifyingGlass,
        //           width: 20,
        //           color:
        //               searchController.text.isNotEmpty ? kPrimaryColor : null,
        //         ),
        //       ),
        //     ),
        //     maxLength: 10,
        //     onChanged: (value) => setState(() {}),
        //     controller: searchController,
        //   ),
        // ),
        LayoutBuilder(
          builder: (context, constraints) {
            if (currentDiscount.isLoading) {
              return const IsLoadingWG(isLoading: true);
            }
            if (currentDiscount.isError) {
              return Text.rich(
                  TextSpan(text: "${currentDiscount.error} ", children: const [
                WidgetSpan(
                    child: FaIcon(
                  FontAwesomeIcons.magnifyingGlass,
                  color: kTextColor,
                  size: 15,
                ))
              ]));
            }
            if (currentDiscount.result == null) {
              return Container();
            }
            return DiscountCard(
              isSearched: true,
              discount: UserDiscount(
                id: 0,
                discount: currentDiscount.result!,
                available: true,
                quantityDefault: 1,
              ),
            );
          },
        ),
      ]),
      // Flatten the List<Widget> to avoid nesting Rows unnecessarily
    );
  }
}
