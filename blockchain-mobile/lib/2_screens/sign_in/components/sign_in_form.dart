import 'dart:async';

import 'package:blockchain_mobile/1_controllers/providers/auth_provider.dart';
import 'package:blockchain_mobile/1_controllers/services/auth_service.dart';
import 'package:blockchain_mobile/3_components/loader/is_loading_button.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../../3_components/custom_surfix_icon.dart';
import '../../../3_components/form_error.dart';
import '../../../4_helper/keyboard.dart';
import '../../../constants.dart';
import '../../forgot_password/forgot_password_screen.dart';
import '../../login_success/login_success_screen.dart';

class SignInForm extends StatefulWidget {
  const SignInForm({super.key});
  @override
  _SignInFormState createState() => _SignInFormState();
}

class _SignInFormState extends State<SignInForm> {
  final authService = AuthService();

  final _formKey = GlobalKey<FormState>();
  String? email;
  String? password;
  bool? remember = false;
  final List<String?> errors = [];
  bool isLoading = false;

  Timer? _timer;
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

  void removeAllError() {
    if (errors.isNotEmpty) {
      setState(() {
        errors.removeWhere(
          (element) => true,
        );
      });
    }
  }

  @override
  void initState() {
    if (kDebugMode) {
      email = "user";
      password = "123456";
      setState(() {});
    }
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Form(
      key: _formKey,
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          TextFormField(
            initialValue: email,
            keyboardType: TextInputType.name,
            onSaved: (newValue) => email = newValue,
            onChanged: (value) {
              if (value.isNotEmpty) {
                removeError(error: kEmailNullError);
              } else if (emailValidatorRegExp.hasMatch(value)) {
                removeError(error: kInvalidEmailError);
              }
              return;
            },
            validator: (value) {
              if (value!.isEmpty) {
                addError(error: kEmailNullError);
                return "";
              }
              return null;
            },
            decoration: const InputDecoration(
              labelText: "Username",
              hintText: "Enter your username",
              // If  you are using latest version of flutter then lable text and hint text shown like this
              // if you r using flutter less then 1.20.* then maybe this is not working properly
              floatingLabelBehavior: FloatingLabelBehavior.always,
              suffixIcon: CustomSuffixIcon(svgIcon: "assets/icons/User.svg"),
            ),
          ),
          const SizedBox(height: 20),
          TextFormField(
            initialValue: password,
            obscureText: true,
            onSaved: (newValue) => password = newValue,
            onChanged: (value) {
              if (value.isNotEmpty) {
                removeError(error: kPassNullError);
              }
              return;
            },
            validator: (value) {
              if (value!.isEmpty) {
                addError(error: kPassNullError);
                return "";
              }
              return null;
            },
            decoration: const InputDecoration(
              labelText: "Password",
              hintText: "Enter your password",
              // If  you are using latest version of flutter then lable text and hint text shown like this
              // if you r using flutter less then 1.20.* then maybe this is not working properly
              floatingLabelBehavior: FloatingLabelBehavior.always,
              suffixIcon: CustomSuffixIcon(svgIcon: "assets/icons/Lock.svg"),
            ),
          ),
          const SizedBox(height: 20),
          Row(
            children: [
              // Checkbox(
              //   value: remember,
              //   activeColor: kPrimaryColor,
              //   onChanged: (value) {
              //     setState(() {
              //       remember = value;
              //     });
              //   },
              // ),
              // const Text("Remember me"),
              const Spacer(),
              GestureDetector(
                onTap: () => Navigator.pushNamed(
                    context, ForgotPasswordScreen.routeName),
                child: const Text(
                  "Forgot Password",
                  style: TextStyle(decoration: TextDecoration.underline),
                ),
              )
            ],
          ),
          const SizedBox(height: 16),
          IsLoadingButton(
            spannedLoading: true,
            isLoading: isLoading,
            child: ElevatedButton(
              onPressed: submitHandler,
              style: ElevatedButton.styleFrom(
                  minimumSize: const Size(double.infinity, 48)),
              child: const Text("Continue"),
            ),
          ),
          FormError(errors: errors),
        ],
      ),
    );
  }

  void submitHandler() async {
    if (_formKey.currentState!.validate()) {
      _formKey.currentState!.save();
      setState(() {
        isLoading = true;
      });
      var result = await authService.userSignIn(email, password);
      setState(() {
        isLoading = false;
      });
      if (result.isLeft) {
        // if all are valid then go to success screen
        if (!mounted) return;
        context.read<AuthProvider>().getCurrentUser(true);
        KeyboardUtil.hideKeyboard(context);
        Navigator.pushNamed(context, LoginSuccessScreen.routeName);
      } else {
        _timer?.cancel();
        addError(error: result.right);
        _timer = Timer(const Duration(seconds: 3), () {
          removeError(error: result.right);
        });
      }
    }
  }
}
