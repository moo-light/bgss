import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/products_provider.dart';
import 'package:blockchain_mobile/1_controllers/providers/withdraw_provider.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/3_components/card_container.dart';
import 'package:blockchain_mobile/3_components/dialogs/confirm_dialog.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_button.dart';
import 'package:blockchain_mobile/3_components/selection_components.dart';
import 'package:blockchain_mobile/4_helper/decorations/text_styles.dart';
import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/4_helper/keyboard.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/Product.dart';
import 'package:blockchain_mobile/models/enums/gold_unit.dart';
import 'package:blockchain_mobile/models/inventory.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:form_validator/form_validator.dart';
import 'package:gap/gap.dart';
import 'package:provider/provider.dart';

import 'components/products_screen.dart';

class InventoryScreen extends StatefulWidget {
  final Inventory inventory;

  const InventoryScreen({super.key, required this.inventory});

  @override
  State<InventoryScreen> createState() => _InventoryDialogState();
}

class _InventoryDialogState extends State<InventoryScreen> {
  List<GoldUnit> selectionList = [...GoldUnit.values];
  List<GoldUnit> smallList = [GoldUnit.MACE];
  GoldUnit selected = GoldUnit.TROY_OZ;
  GoldUnit smallSelected = GoldUnit.MACE;
  final _controller = TextEditingController();

  final formKey = GlobalKey<FormState>();
  final withDrawValidator =
      ValidationBuilder().required("Withdraw is required").build();
  final withdrawController = TextEditingController();

  var selections = [
    "AVAILABLE",
    // "CRAFT",
  ];
  var selectedSeletion = 0;

  Product? selectedProduct;

  @override
  Widget build(BuildContext context) {
    final provider = context.read<WithdrawProvider>();
    var maxx = GoldUnit.TROY_OZ
        .convert(widget.inventory.totalWeightOz, smallSelected)
        .round();
    selectedProduct = context.watch<ProductProvider>().sProduct;
    return PopScope(
      canPop: !provider.createWithdrawOrderResult.isLoading,
      onPopInvoked: (didPop) {
        if (!provider.createWithdrawOrderResult.isLoading) {
          Provider.of<WithdrawProvider>(context, listen: false)
              .currentWithdraw
              .reset();
          Provider.of<ProductProvider>(context, listen: false).selectedProduct =
              null;
        }
      },
      child: Scaffold(
        appBar: AppBar(
          title: const Text("Your Inventory"),
        ),
        resizeToAvoidBottomInset: false,
        body: SafeArea(
          child: Column(
            children: [
              Align(
                alignment: Alignment.topCenter,
                child: Container(
                  padding: const EdgeInsets.all(32),
                  child: Row(
                    mainAxisSize: MainAxisSize.max,
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Text.rich(
                        TextSpan(text: "Inventory: ", children: [
                          TextSpan(
                            text: numberFormat(
                              GoldUnit.TROY_OZ.convert(
                                  widget.inventory.totalWeightOz, selected),
                            ),
                            style: inventoryStyle,
                          )
                        ]),
                        style:
                            Theme.of(context).textTheme.titleMedium!.copyWith(),
                      ),
                      const Gap(10),
                      DropdownButton(
                        items: List.generate(
                          selectionList.length,
                          (index) => DropdownMenuItem(
                            value: selectionList[index],
                            enabled: selectionList[index] != selected,
                            child: Text(selectionList[index].symbol),
                          ),
                        ),
                        onChanged: (v) {
                          setState(() {
                            selected = v!;
                          });
                        },
                        value: selected,
                      )
                    ],
                  ),
                ),
              ),
              const Gap(20),
              CardContainer(
                  child: Form(
                key: formKey,
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const Gap(12),
                    Text(
                      "Withdraw Gold",
                      style: Theme.of(context).textTheme.titleLarge,
                    ),
                    SingleChildScrollView(
                      child: Wrap(
                        spacing: 12,
                        children: List.generate(selections.length, (index) {
                          return SelectionComponents(
                            selected: selectedSeletion == index,
                            onPressed: () => setState(() {
                              selectedSeletion = index;
                            }),
                            label: selections[index],
                          );
                        }),
                      ),
                    ),
                    const Gap(12),
                    selectedProduct == null
                        ? ElevatedButton.icon(
                            onPressed: () {
                              navigateToSelectProduct(context);
                            },
                            style: ElevatedButton.styleFrom(
                              backgroundColor: kBackgroundColor,
                              foregroundColor: kTextColor,
                              shape: const LinearBorder(),
                            ),
                            icon: const FaIcon(FontAwesomeIcons.box),
                            label: const Text("Select A Product"),
                          )
                        : Container(
                            decoration: BoxDecoration(
                              gradient: LinearGradient(
                                  begin: Alignment.centerLeft,
                                  end: Alignment.centerRight,
                                  colors: [
                                    Colors.grey.shade100,
                                    kBackgroundColor.withHSLlighting(93)
                                  ]),
                              color: kBackgroundColor.withHSLlighting(93),
                              boxShadow: [
                                BoxShadow(
                                  color: Colors.grey.shade400,
                                  blurRadius: 3,
                                )
                              ],
                            ),

                            margin: const EdgeInsets.symmetric(
                                vertical: 4.0,
                                horizontal: 8.0), // Margin around the container
                            child: ListTile(
                              onTap: () => {navigateToSelectProduct(context)},
                              leading: Container(
                                margin: const EdgeInsets.all(4),
                                child: AspectRatio(
                                    aspectRatio: 1,
                                    child: selectedProduct?.image),
                              ),
                              title: Text(selectedProduct?.title ?? ""),
                              trailing: Text(
                                '${selectedProduct?.weight} Mace',
                                style: const TextStyle(
                                    fontSize: 12,
                                    fontWeight: FontWeight.bold,
                                    color: kPrimaryColor),
                              ),
                            ),
                          ),
                    const Gap(15),
                    Visibility(
                      visible: selectedSeletion == 1,
                      child: TextFormField(
                        keyboardType: const TextInputType.numberWithOptions(),
                        inputFormatters: <TextInputFormatter>[
                          FilteringTextInputFormatter.digitsOnly
                        ],
                        validator: (value) {
                          final message = withDrawValidator(value);
                          if (message?.isNotEmpty ?? false) return message;
                          try {
                            var parse = int.parse(value!);
                            if (parse > maxx) {
                              return "Withdraw amount too big";
                            }
                            if (parse <= 0) {
                              return "Withdraw amount must be larger than zero";
                            }
                          } catch (e) {
                            return "Number too large";
                          }
                          return message;
                        },
                        onTapOutside: (event) {
                          KeyboardUtil.hideKeyboard(context);
                        },
                        controller: withdrawController,
                        onSaved: (value) => formKey.currentState!.validate(),
                        onChanged: (value) => formKey.currentState!.validate(),
                        decoration: InputDecoration(
                          counterText:
                              "0 ${smallSelected.symbol}- $maxx ${smallSelected.symbol}",
                          labelText: "Amount to withdraw",
                          suffixIcon: DropdownButton(
                            items: List.generate(
                              smallList.length,
                              (index) => DropdownMenuItem(
                                value: smallList[index],
                                child: Text(smallList[index].symbol),
                              ),
                            ),
                            onChanged: (v) {
                              setState(() {
                                smallSelected = v!;
                              });
                            },
                            value: smallSelected,
                          ),
                        ),
                      ),
                    ),
                    const Gap(10),
                    IsLoadingButton(
                      isLoading: provider.createWithdrawOrderResult.isLoading,
                      spannedLoading: true,
                      child: ElevatedButton(
                        onPressed: () {
                          formKey.currentState!.save();
                          if (selectedSeletion == 0 &&
                              selectedProduct == null) {
                            ToastService.toastError(
                                context, "Please Select a Product");
                            return;
                          }
                          if (selectedSeletion == 1 &&
                              !formKey.currentState!.validate()) return;
                          showConfirmDialog(context, provider);
                        },
                        style: ElevatedButton.styleFrom(
                            minimumSize: const Size(double.infinity, 48)),
                        child: const Text("Withdraw Gold"),
                      ),
                    )
                  ],
                ),
              ))
            ],
          ),
        ),
      ),
    );
  }

  Future<dynamic> showConfirmDialog(
      BuildContext context, WithdrawProvider provider) {
    return showDialog(
      context: context,
      builder: (context) {
        return SimpleConfirmDialog(
          content: "do you want to create this withdraw order?",
          onConfirm: () {
            Navigator.pop(context);
            _handleSubmit(provider);
          },
          onCancel: () {
            Navigator.pop(context);
          },
        );
      },
    );
  }

  void navigateToSelectProduct(BuildContext context) {
    context.read<ProductProvider>().get24kGoldProductList();
    Navigator.push(
        context,
        MaterialPageRoute(
          builder: (context) => const ProductsScreen(),
        ));
  }

  _handleSubmit(WithdrawProvider provider) {
    KeyboardUtil.hideKeyboard(context);
    provider
        .createWithdrawOrder(
          context,
          weightToWithdraw: selectedSeletion == 1
              ? double.tryParse(withdrawController.text) ?? 0
              : 0,
          unit: smallSelected,
          product: selectedProduct,
          action: selections[selectedSeletion],
        )
        .then((value) => context.read<AuthProvider>().getCurrentUser());
  }

  @override
  void dispose() {
    super.dispose();
  }
}
