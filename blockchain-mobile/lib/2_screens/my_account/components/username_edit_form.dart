import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/2_screens/sign_up/validators.dart';
import 'package:blockchain_mobile/3_components/custom_surfix_icon.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_button.dart';
import 'package:blockchain_mobile/4_helper/keyboard.dart';
import 'package:blockchain_mobile/models/user.dart';
import 'package:flutter/material.dart';
import 'package:gap/gap.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';

class UsernameEditForm extends StatefulWidget {
  final User? currentUser;

  const UsernameEditForm({
    super.key,
    required this.currentUser,
  });

  @override
  State<UsernameEditForm> createState() => _UsernameEditFormState();
}

class _UsernameEditFormState extends State<UsernameEditForm> {
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  final TextEditingController firstname = TextEditingController();
  final TextEditingController lastname = TextEditingController();
  final TextEditingController phone = TextEditingController();
  final TextEditingController address = TextEditingController();
  final TextEditingController dob = TextEditingController();
  final TextEditingController ciCard = TextEditingController();
  bool isSubmitting = false;
  final dateFormat = DateFormat("yyyy-MM-dd");
  @override
  void initState() {
    phone.text = widget.currentUser?.userInfo.phoneNumber ?? "";
    firstname.text = widget.currentUser?.userInfo.firstName ?? "";
    lastname.text = widget.currentUser?.userInfo.lastName ?? "";
    address.text = widget.currentUser?.userInfo.address ?? "";
    dob.text = widget.currentUser?.userInfo.doB ?? "";
    super.initState();
  }

  Map<dynamic, dynamic>? data;
  void _init(BuildContext context) {
    if (data != null) return;
    data = ModalRoute.of(context)?.settings.arguments as Map?;
    if (data != null) {
      ciCard.text = data!["ciCard"];
      firstname.text = data!["firstName"];
      lastname.text = data!["lastName"];
      address.text = data!["address"];
      dob.text = data!["dob"];
    } else {
      data = {};
    }
  }

  @override
  Widget build(BuildContext context) {
    _init(context);
    var authProvider = context.read<AuthProvider>();
    return Form(
      key: _formKey,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          Text("User Information",
              style: Theme.of(context).textTheme.headlineSmall),
          const Gap(20),
          TextFormField(
            decoration: const InputDecoration(
              suffixIcon: CustomSuffixIcon(svgIcon: "assets/icons/User.svg"),
              label: Text("First Name"),
              border: OutlineInputBorder(
                  borderRadius: BorderRadius.all(Radius.circular(16))),
            ),
            controller: firstname,
            onSaved: (newValue) => firstname.text = newValue!.trim(),
            validator: (value) => validator_first_name(value),
          ),
          const Gap(10),
          TextFormField(
            decoration: const InputDecoration(
                border: OutlineInputBorder(
                    borderRadius: BorderRadius.all(Radius.circular(16))),
                suffixIcon: CustomSuffixIcon(svgIcon: "assets/icons/User.svg"),
                label: Text("Last Name")),
            onSaved: (value) {
              lastname.text = value?.trim() ?? "";
            },
            onChanged: (value) => _formKey.currentState?.validate(),
            controller: lastname,
            validator: (value) => validator_last_name(value),
          ),
          const Gap(10),
          TextFormField(
            decoration: const InputDecoration(
              suffixIcon: CustomSuffixIcon(svgIcon: "assets/icons/Call.svg"),
              label: Text("Phone Number"),
              border: OutlineInputBorder(
                  borderRadius: BorderRadius.all(Radius.circular(16))),
            ),
            controller: phone,
            onSaved: (value) => phone.text = value!.trim(),
            validator: (value) => validator_phone(value),
          ),
          const Gap(10),
          InkWell(
            onTap: () => showDatePicker(
                    firstDate: DateTime(1800),
                    lastDate: DateTime.now(),
                    context: context,
                    initialDate: DateTime.tryParse(dob.text),
                    fieldLabelText: "Birthdate")
                .then((value) {
              return dob.text = dateFormat.format(value!);
            }),
            child: AbsorbPointer(
              child: TextFormField(
                decoration: const InputDecoration(
                  suffixIcon:
                      CustomSuffixIcon(svgIcon: "assets/icons/Calender.svg"),
                  labelText: "Date of Birth", // Use labelText instead of label
                  hintText: "Provide a birthdate",
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.all(
                      Radius.circular(16),
                    ),
                  ),
                ),
                // maxLines: null,
                readOnly: true,
                onChanged: (value) {
                  _formKey.currentState!.validate();
                },
                onSaved: (value) => {dob.text = value!.trim()},
                controller: dob,
              ),
            ),
          ),
          const Gap(10),
          TextFormField(
            decoration: const InputDecoration(
              suffixIcon: CustomSuffixIcon(svgIcon: "assets/icons/Home.svg"),
              labelText: "Address", // Use labelText instead of label
              border: OutlineInputBorder(
                borderRadius: BorderRadius.all(Radius.circular(16)),
              ),
            ),
            // maxLines: null,
            // keyboardType: TextInputType.multiline, // Allow multiline input
            maxLines: null,
            onChanged: (value) {
              _formKey.currentState!.validate();
            },
            onSaved: (value) => {address.text = value!.trim()},
            controller: address,
            validator: (value) => validator_address(value),
          ),
          const Gap(20),
          Align(
            alignment: Alignment.center,
            child: IsLoadingButton(
              isLoading: isSubmitting,
              child: ElevatedButton(
                onPressed: () async {
                  await _submitHandler(context, authProvider);
                },
                child: const Text("Save Changes"),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _submitHandler(
      BuildContext context, AuthProvider authProvider) async {
    _formKey.currentState!.save();
    KeyboardUtil.hideKeyboard(context);
    if (_formKey.currentState!.validate()) {
      setState(() {
        isSubmitting = true;
      });
      try {
        await authProvider.updateUserInfo(context,
            firstName: firstname.text.trim(),
            lastName: lastname.text.trim(),
            address: address.text.trim(),
            phone: phone.text.trim(),
            dob: DateTime.parse(dob.text),
            ciCard: ciCard.text.trim());
        // ToastService.toastSuccess(context, "Update User's Name");
      } catch (e) {
        if (e is FormatException && context.mounted) {
          ToastService.toastError(context, e.message);
        } else {
          // Handle other exceptions
          debugPrint(e.toString());
        }
      } finally {
        setState(() {
          isSubmitting = false;
        });
      }
    }
  }
}
