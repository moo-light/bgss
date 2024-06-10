// ignore_for_file: non_constant_identifier_names

import 'dart:async';

import 'package:blockchain_mobile/1_controllers/providers/register_provider.dart';
import 'package:blockchain_mobile/2_screens/sign_up/validators.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_button.dart';
import 'package:blockchain_mobile/4_helper/keyboard.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:form_validator/form_validator.dart';
import 'package:provider/provider.dart';

import '../../../3_components/custom_surfix_icon.dart';
import '../../../3_components/form_error.dart';

class SignUpForm extends StatefulWidget {
  const SignUpForm({super.key});

  @override
  _SignUpFormState createState() => _SignUpFormState();
}

class _SignUpFormState extends State<SignUpForm> {
  final _formKey = GlobalKey<FormState>();
  late String username;
  late String password;
  late String confirm_password;
  late String first_name;
  late String last_name;
  late String phone;
  late String email;
  late String address;

  bool remember = false;

  final List<String?> errors = [];
  late final RegisterProvider _registerProvider;

  Either<String, String?>? apiresp;

  bool isLoading = false;

  Timer? _timer;

  @override
  void initState() {
    _registerProvider = Provider.of<RegisterProvider>(context, listen: false);
    username = "";
    email = "";
    password = "";
    confirm_password = "";
    first_name = "";
    last_name = "";
    phone = "";
    address = "";
    if (kDebugMode) {
      username = "user";
      email = "user@gmail.com";
      password = "123456";
      confirm_password = "123456";
      first_name = "firstnameTesting";
      last_name = "last_nameTesting";
      phone = "0979555999";
      address = "User's Address";
      setState(() {});
    }
    super.initState();
    // apiresp = context.watch<RegisterProvider>().api_response;
  }

  void addError({String? error}) {
    if (!errors.contains(error)) {
      setState(() {
        errors.add(error);
      });
    }
  }

  void removeError({String? error}) {
    if (errors.contains(error)) {
      setState(() {
        errors.remove(error);
      });
    }
  }

  void clearError() {
    setState(() {
      errors.clear();
    });
  }

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      physics: const NeverScrollableScrollPhysics(),
      child: Form(
        key: _formKey,
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextFormField(
              initialValue: username,
              keyboardType: TextInputType.emailAddress,
              onSaved: (newValue) => username = newValue!,
              onChanged: (value) {
                // Your onChanged logic here
              },
              validator: validator_username,
              decoration: const InputDecoration(
                labelText: "Username",
                hintText: "Enter your username",
                floatingLabelBehavior: FloatingLabelBehavior.always,
                suffixIcon: CustomSuffixIcon(svgIcon: "assets/icons/User.svg"),
              ),
            ),
            const SizedBox(height: 20),
            TextFormField(
              initialValue: first_name,
              keyboardType: TextInputType.name,
              onSaved: (newValue) => first_name = newValue!,
              onChanged: (value) {
                // Your onChanged logic here
              },
              validator: validator_first_name,
              decoration: const InputDecoration(
                labelText: "First name",
                hintText: "Enter your first name",
                floatingLabelBehavior: FloatingLabelBehavior.always,
                suffixIcon: CustomSuffixIcon(svgIcon: "assets/icons/User.svg"),
              ),
            ),
            const SizedBox(height: 20),
            TextFormField(
              initialValue: last_name,
              keyboardType: TextInputType.name,
              onSaved: (newValue) => last_name = newValue!,
              validator: validator_last_name,
              decoration: const InputDecoration(
                labelText: "Last name",
                hintText: "Enter your last name",
                floatingLabelBehavior: FloatingLabelBehavior.always,
                suffixIcon: CustomSuffixIcon(svgIcon: "assets/icons/User.svg"),
              ),
            ),
            const SizedBox(height: 20),
            TextFormField(
              initialValue: phone,
              keyboardType: TextInputType.name,
              onSaved: (newValue) => phone = newValue!,
              validator: validator_phone,
              decoration: const InputDecoration(
                labelText: "Phone",
                hintText: "Enter your Phone Number",
                floatingLabelBehavior: FloatingLabelBehavior.always,
                suffixIcon: CustomSuffixIcon(
                  svgIcon: "assets/icons/Call.svg",
                ),
              ),
            ),
            const SizedBox(height: 20),
            TextFormField(
              initialValue: email,
              keyboardType: TextInputType.emailAddress,
              onSaved: (newValue) => email = newValue!,
              onChanged: (value) {
                // Your onChanged logic here
              },
              validator: validator_email,
              decoration: const InputDecoration(
                labelText: "Email",
                hintText: "Enter your email",
                floatingLabelBehavior: FloatingLabelBehavior.always,
                suffixIcon: CustomSuffixIcon(svgIcon: "assets/icons/Mail.svg"),
              ),
            ),
            const SizedBox(height: 20),
            TextFormField(
              initialValue: address,
              keyboardType: TextInputType.text,
              onSaved: (newValue) => address = newValue!,
              onChanged: (value) {
                // Your onChanged logic here
              },
              validator: validator_address,
              decoration: const InputDecoration(
                labelText: "Address",
                hintText: "Enter your address",
                floatingLabelBehavior: FloatingLabelBehavior.always,
                suffixIcon: CustomSuffixIcon(
                    svgIcon: "assets/icons/Location point.svg"),
              ),
            ),
            const SizedBox(height: 20),
            TextFormField(
              initialValue: password,
              obscureText: true,
              onSaved: (newValue) => password = newValue!,
              onChanged: (value) {
                // Your onChanged logic here
              },
              validator: validator_password,
              decoration: const InputDecoration(
                labelText: "Password",
                hintText: "Enter your password",
                floatingLabelBehavior: FloatingLabelBehavior.always,
                suffixIcon: CustomSuffixIcon(svgIcon: "assets/icons/Lock.svg"),
              ),
            ),
            const SizedBox(height: 20),
            TextFormField(
              initialValue: confirm_password,
              obscureText: true,
              onSaved: (newValue) => confirm_password = newValue!,
              validator: (value) {
                var validationError =
                    ValidationBuilder(requiredMessage: kPassNullError)
                        .test(value);
                if (validationError != null) return validationError;
                if (value != password) {
                  return "Password not match";
                }
                return null;
                // Your validator logic here
              },
              decoration: const InputDecoration(
                labelText: "Confirm Password",
                hintText: "Re-enter your password",
                floatingLabelBehavior: FloatingLabelBehavior.always,
                suffixIcon: CustomSuffixIcon(svgIcon: "assets/icons/Lock.svg"),
              ),
            ),
            const SizedBox(height: 20),
            IsLoadingButton(
              spannedLoading: true,
              isLoading: isLoading,
              child: ElevatedButton(
                onPressed: () => _doRegister(context),
                style: ElevatedButton.styleFrom(
                    minimumSize: const Size(double.infinity, 48)),
                child: const Text("Sign Up"),
              ),
            ),
            FormError(errors: errors),
          ],
        ),
      ),
    );
  }

  void _doRegister(BuildContext context) async {
    clearError();
    KeyboardUtil.hideKeyboard(context);
    _formKey.currentState!.save();
    if (_formKey.currentState!.validate()) {
      setState(() {
        isLoading = true;
      });
      for (String element in [
        username,
        email,
        password,
        first_name,
        last_name,
        phone,
        address
      ]) {
        element = element.trim();
      }
      setState(() {});
      await _registerProvider.userSignUp(context, username, email, password,
          first_name, last_name, phone, address);
      if (context.mounted) {
        var response =
            Provider.of<RegisterProvider>(context, listen: false).api_response!;
        if (response.isRight) {
          _timer?.cancel();
          addError(error: response.right);
          _timer = Timer(const Duration(seconds: 3), () {
            removeError(error: response.right);
          });
        }
      }
      setState(() {
        isLoading = false;
      });
      // if all are valid then go to success screen
    }
  }
}
