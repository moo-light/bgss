import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/2_screens/sign_up/validators.dart';
import 'package:blockchain_mobile/3_components/custom_surfix_icon.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_button.dart';
import 'package:blockchain_mobile/4_helper/keyboard.dart';
import 'package:blockchain_mobile/models/user.dart';
import 'package:flutter/material.dart';
import 'package:gap/gap.dart';
import 'package:provider/provider.dart';

class PhoneEditForm extends StatefulWidget {
  final User? currentUser;

  const PhoneEditForm({
    super.key,
    this.currentUser,
  });

  @override
  State<PhoneEditForm> createState() => _PhoneEditFormState();
}

class _PhoneEditFormState extends State<PhoneEditForm> {
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  final TextEditingController phone = TextEditingController();

  bool isSubmitting = false;
  @override
  void initState() {
    phone.text = widget.currentUser?.userInfo.phoneNumber ?? "";
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
          Text("Phone Number",
              style: Theme.of(context).textTheme.headlineSmall),
          const Gap(20),
          TextFormField(
            decoration: const InputDecoration(
              suffixIcon: CustomSuffixIcon(svgIcon: "assets/icons/Call.svg"),
              label: Text("Phone Number"),
              border: OutlineInputBorder(
                  borderRadius: BorderRadius.all(Radius.circular(16))),
            ),
            controller: phone,
            validator: (value) => validator_phone(value),
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
                      await authProvider.updateUserPhone(
                        context,
                        phone: phone.text,
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
