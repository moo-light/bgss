import 'package:blockchain_mobile/1_controllers/providers/products_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/reviews_provider.dart';
import 'package:blockchain_mobile/2_screens/product_details/product_details_screen.dart';
import 'package:blockchain_mobile/2_screens/products/components/product_discount.dart';
import 'package:blockchain_mobile/3_components/fa_icon_gen.dart';
import 'package:blockchain_mobile/3_components/product_card.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/Product.dart';
import 'package:blockchain_mobile/models/gold_type.dart';
import 'package:blockchain_mobile/models/product_category.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:provider/provider.dart';

import '../../3_components/failed_information.dart';
import 'components/product_search.dart';

class ProductsScreen extends StatefulWidget {
  const ProductsScreen({super.key});

  static String routeName = "/products";

  List<bool?> get ratings =>
      List.generate(5, (index) => false, growable: false);

  @override
  State<ProductsScreen> createState() => _ProductsScreenState();
}

class _ProductsScreenState extends State<ProductsScreen> {
  bool isOpen = false;
  String search = "";
  String min = "";
  String max = "";
  Map<ProductCategory, bool?> categories = {};
  Map<GoldType, bool?> typeGolds = {};
  List<bool?> ratings = [];
  late List<Product> demoProducts;
  late bool isLoading;
  late bool isError;
  int index = 0;
  final List<Map<String, dynamic>> minMaxSelection = [
    {
      "label": "0-max",
      "min": "",
      "max": '500',
    },
    {
      "label": "0-500\$",
      "min": '',
      "max": '500',
    },
    {
      "label": "500-5000\$",
      "min": '500',
      "max": '5000',
    },
    {
      "label": "5000-max",
      "min": '5000',
      "max": "",
    },
  ];

  @override
  void initState() {
    context.read<ProductProvider>().getCategories(context);
    context.read<ProductProvider>().getTypeGolds(context);
    ratings = widget.ratings;
    super.initState();
  }

  final GlobalKey<ScaffoldState> _scaffoldKey = GlobalKey<ScaffoldState>();
  @override
  Widget build(BuildContext context) {
    demoProducts = context.watch<ProductProvider>().productList;
    isLoading = context.watch<ProductProvider>().isLoading;
    isError = context.watch<ProductProvider>().error != null;
    return Consumer<ProductProvider>(
      builder: (context, value, child) {
        final keys = context.watch<ProductProvider>().categories;
        categories = Map.fromIterables(
          keys,
          categories.length != keys.length
              ? keys.map((e) => false)
              : categories.entries.map((e) => e.value),
        );
        final tkeys = context.watch<ProductProvider>().typeGolds;
        typeGolds = Map.fromIterables(
          tkeys,
          typeGolds.length != tkeys.length
              ? tkeys.map((e) => false)
              : typeGolds.entries.map((e) => e.value),
        );
        return child!;
      },
      child: Scaffold(
        key: _scaffoldKey,
        appBar: AppBar(
          title: const Text("Products"),
          actions: [
            IconButton(
              onPressed: () {
                if (_scaffoldKey.currentState!.hasEndDrawer) {
                  _scaffoldKey.currentState!.openEndDrawer();
                }
              },
              icon: const FaIcon(FontAwesomeIcons.filter),
            ),
            IconButton(
                onPressed: toggleSearch,
                icon: const FaIcon(FontAwesomeIcons.magnifyingGlass))
          ],
        ),
        endDrawer: Drawer(
          backgroundColor: kBackgroundColor,
          child: ListView(
            children: <Widget>[
              const ListTile(
                title: Text(
                  "Filter",
                  style: TextStyle(fontWeight: FontWeight.bold, fontSize: 30),
                ),
              ),
              ExpansionTile(
                title: const Text('Price'),
                initiallyExpanded: true,
                children: <Widget>[
                  ListTile(
                    trailing: ElevatedButton(
                      child: const Text("Clear"),
                      onPressed: () {
                        setState(() {
                          index = 0;
                          min = "";
                          max = "";
                          _search();
                        });
                      },
                    ),
                    title: DropdownButton(
                      isExpanded: true,
                      items: List.generate(
                        minMaxSelection.length,
                        (index) {
                          final item = minMaxSelection[index];
                          return DropdownMenuItem(
                            value: index,
                            child: Text(item['label']),
                          );
                        },
                      ),
                      onChanged: (value) {
                        index = value!;
                        min = minMaxSelection[index]['min'];
                        max = minMaxSelection[index]['max'];
                        _search();
                        setState(() {});
                      },
                      value: index,
                    ),
                  ),
                ],
              ),
              ExpansionTile(
                title: const Text('Category'),
                initiallyExpanded: true,
                children: categories.entries.map((entry) {
                  return CheckboxListTile(
                    title: Text(entry.key.categoryName),
                    value: entry.value == true,
                    onChanged: (bool? value) {
                      setState(() {
                        categories[entry.key] = value;
                        _search();
                      });
                    },
                  );
                }).toList(),
              ),
              ExpansionTile(
                title: const Text('Type of Gold'),
                initiallyExpanded: true,
                children: typeGolds.entries.map((entry) {
                  return CheckboxListTile(
                    title: Text(entry.key
                        .typeName), // Assuming typeGolds has a 'typeGoldName' property
                    value: entry.value == true,
                    onChanged: (bool? value) {
                      setState(() {
                        typeGolds[entry.key] = value;
                        _search();
                      });
                    },
                  );
                }).toList(),
              ),
              ExpansionTile(
                title: const Text('Ratings'),
                initiallyExpanded: true,
                children: _buildRating(ratings),
              ),
            ],
          ),
        ),
        body: SafeArea(
          child: RefreshIndicator(
            onRefresh: () async {
              context.read<ProductProvider>().getProductList();
            },
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                AnimatedContainer(
                  duration: Durations.long1,
                  height: isOpen ? 60 : 0,
                  child: ProductSearch(
                    isOpen: isOpen,
                    onChanged: (value) => setState(() {
                      search = value;
                      _search();
                    }),
                  ),
                ),
                const ListDiscount(),
                const Divider(),
                Expanded(
                  child: Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 16.0),
                    child: Visibility(
                      visible: !isLoading,
                      replacement: const Center(
                        child: CircularProgressIndicator(),
                      ),
                      child: Visibility(
                        visible: !isError,
                        replacement: FailedInformation(
                          child: Text(kDebugMode
                              ? context.watch<ProductProvider>().error ?? ""
                              : "No Product Yet"),
                        ),
                        child: CustomScrollView(
                          slivers: [
                            SliverGrid(
                              gridDelegate:
                                  const SliverGridDelegateWithMaxCrossAxisExtent(
                                maxCrossAxisExtent: 200,
                                childAspectRatio: 0.7,
                                mainAxisSpacing: 20,
                                crossAxisSpacing: 16,
                              ),
                              delegate: SliverChildBuilderDelegate(
                                (context, index) => ProductCard(
                                  product: demoProducts[index],
                                  onPress: () {
                                    context
                                        .read<ReviewProvider>()
                                        .canReview(demoProducts[index].id);
                                    context
                                        .read<ReviewProvider>()
                                        .getReviewList(demoProducts[index].id);
                                    context
                                        .read<ProductProvider>()
                                        .getProductById(demoProducts[index].id);
                                    Navigator.pushNamed(
                                      context,
                                      ProductDetailsScreen.routeName,
                                      arguments: ProductDetailsArguments(
                                        product: demoProducts[index],
                                        fromCartScreen: false,
                                      ),
                                    );
                                  },
                                ),
                                childCount: demoProducts.length,
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  void toggleSearch() {
    setState(() {
      isOpen = !isOpen;
    });
  }

  List<Widget> _buildRating(List<bool?> ratings) {
    List<Widget> rate = [];
    ratings.asMap().forEach((index, value) {
      rate.insert(
        0,
        CheckboxListTile(
          title: Row(
            children: List.generate(
              5,
              (jndex) => FaIconGen(
                FontAwesomeIcons.solidStar,
                margin: const EdgeInsets.symmetric(horizontal: 2),
                color: jndex <= index ? kSecondaryColor : null,
              ),
            ),
          ),
          value: value == true,
          onChanged: (bool? value) {
            ratings.asMap().forEach((index, element) {
              ratings[index] = false;
            });

            setState(() {
              ratings[index] = value;
              _search();
            });
          },
        ),
      );
    });
    return rate;
  }

  void _search() async {
    // Extract selected category IDs
    final List<int> categoryIDs = categories.entries
        .where((element) => element.value == true)
        .map((e) => e.key.id)
        .toList();

    // Extract selected typeGold IDs
    final List<int> typeGoldIDs = typeGolds.entries
        .where((element) => element.value == true)
        .map((e) => e.key.id)
        .toList();

    // Extract selected ratings
    final ratings = this
        .ratings
        .where((element) => element == true)
        .indexed
        .map((e) => 5 - e.$1)
        .toList();

    if (mounted) {
      context.read<ProductProvider>().getProductList(
          search: search,
          asc: null,
          categoryIds: categoryIDs,
          typeGoldIds: typeGoldIDs, // Add this line to include typeGold IDs
          max: max.isNotEmpty ? int.parse(max) : null,
          min: min.isNotEmpty ? int.parse(min) : null,
          ratings: ratings);
    }
  }
}
