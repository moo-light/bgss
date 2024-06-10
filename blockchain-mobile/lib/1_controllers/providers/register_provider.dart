// ignore_for_file: non_constant_identifier_names

import 'package:blockchain_mobile/1_controllers/repositories/auth_repository.dart';
import 'package:blockchain_mobile/2_screens/login_success/login_success_screen.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:either_dart/either.dart';
import 'package:flutter/cupertino.dart';

class RegisterProvider extends ChangeNotifier {
  // api response
  Either<String, String?>? api_response;
  final _authRepository = AuthRepository();

  userSignUp(
    BuildContext context,
    String username,
    String email,
    String password,
    String firstName,
    String lastName,
    String phone,
    String address
  ) async {
    var result = await _authRepository.register(
      username,
      email,
      password,
      firstName,
      lastName,
      phone,
      address,
    );
    try {
      if (result.isLeft && context.mounted) {
        Navigator.pushNamed(context, LoginSuccessScreen.registerRouteName,
            arguments: result.left);
      }
      if (result.isRight) {
        notifyListeners();
      }
    } catch (e) {
      logger.e(e);
    }
    notifyListeners();
    api_response = result;
  }

  clearResponse() {
    api_response = null;
  }
}
