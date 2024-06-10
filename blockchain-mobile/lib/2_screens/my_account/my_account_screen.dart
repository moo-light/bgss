import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/2_screens/sign_up/validators.dart';
import 'package:blockchain_mobile/3_components/custom_surfix_icon.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_button.dart';
import 'package:blockchain_mobile/4_helper/extensions/color_extension.dart';
import 'package:blockchain_mobile/4_helper/keyboard.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/user.dart';
import 'package:flutter/material.dart';
import 'package:gap/gap.dart';
import 'package:provider/provider.dart';

import 'components/my_account_edit_avatar.dart';
import 'components/username_edit_form.dart';

class MyAccountScreen extends StatelessWidget {
  const MyAccountScreen({super.key});
  static const String routeName = "/profile/my_account";

  @override
  Widget build(BuildContext context) {
    final User? currentUser = context.watch<AuthProvider>().currentUser;
    bool isLoading = context.watch<AuthProvider>().isLoading;

    return Scaffold(
        appBar: AppBar(
          title: Text("${currentUser?.username}'s Account"),
        ),
        body: SingleChildScrollView(
            child: isLoading
                ? _buildLoading()
                : _buildMain(context, currentUser!)));
  }

  _buildMain(BuildContext context, final User currentUser) {
    debugPrint({"Avatar", currentUser.userInfo.avatarData}.toString());
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 24),
      child: Column(
        children: [
          _container(
            child: MyAccountEditAvatar(
              currentUser: currentUser,
            ),
          ),
          //Gap
          const Gap(32),
          //Change User's name
          _container(
            child: UsernameEditForm(currentUser: currentUser),
          ),
          const Gap(32),
        ],
      ),
    );
  }

  Container _container({Widget? child}) {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 24, horizontal: 16),
      width: double.infinity,
      decoration: BoxDecoration(
        color: kBackgroundColor.withHSLlighting(93),
        boxShadow: const [
          BoxShadow(
              color: Colors.black12, blurRadius: 5, blurStyle: BlurStyle.solid)
        ],
        borderRadius: BorderRadius.circular(16),
      ),
      child: child,
    );
  }

  _buildLoading() {
    return const Center(child: CircularProgressIndicator());
  }
}

class AddressEditForm extends StatefulWidget {
  final User? currentUser;

  const AddressEditForm({
    super.key,
    this.currentUser,
  });

  @override
  State<AddressEditForm> createState() => _AddressEditFormState();
}

class _AddressEditFormState extends State<AddressEditForm> {
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  final TextEditingController address = TextEditingController();

  bool isSubmitting = false;
  @override
  void initState() {
    address.text = widget.currentUser?.userInfo.address ?? "";
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    var authProvider = context.read<AuthProvider>();

    return Form(
      key: _formKey,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          Text("Address", style: Theme.of(context).textTheme.headlineSmall),
          const Gap(20),
          TextFormField(
            decoration: const InputDecoration(
              suffixIcon: CustomSuffixIcon(svgIcon: "assets/icons/Home.svg"),
              labelText: "Address", // Use labelText instead of label
              border: OutlineInputBorder(
                  borderRadius: BorderRadius.all(Radius.circular(16))),
            ),
            // maxLines: null,
            keyboardType: TextInputType.multiline, // Allow multiline input
            maxLines: null,
            onChanged: (value) {
              address.text = value;
              _formKey.currentState!.validate();
            },
            onSaved: (value) => {address.text = value!.trim()},
            controller: address,
            validator: (value) => validator_address(value),
          ),
          const Gap(20),
          Align(
            alignment: Alignment.centerLeft,
            child: IsLoadingButton(
              isLoading: isSubmitting,
              child: ElevatedButton(
                onPressed: () async {
                  _formKey.currentState!.save();
                  KeyboardUtil.hideKeyboard(context);
                  if (_formKey.currentState!.validate()) {
                    setState(() {
                      isSubmitting = true;
                    });
                    try {
                      await authProvider.updateUserAddress(
                        context,
                        address: address.text,
                      );
                    } catch (e) {
                      if (e is FormatException && context.mounted) {
                        ToastService.toastError(context, e.message);
                      } else {
                        // Handle other exceptions
                        debugPrint(e.toString());
                      }
                    }
                  }
                  setState(() {
                    isSubmitting = false;
                  });
                },
                child: const Text("Save Changes"),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
