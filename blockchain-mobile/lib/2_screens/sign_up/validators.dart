// validations
// ignore_for_file: non_constant_identifier_names

import 'package:blockchain_mobile/constants.dart';
import 'package:form_validator/form_validator.dart';

kTitieNullError(String title) => "$title is Required";
kInvalidTitieError(String title, [String replacement = "is Invalid"]) =>
    "$title $replacement";
kTitieShortError(String title, String replacement) => "$title $replacement";

kTitieLongError(String title, String replacement) => "$title $replacement";
final validator_username =
    ValidationBuilder(requiredMessage: kUsernameNullError)
        .maxLength(20, kUsernameLongError)
        .build();
final validator_email = ValidationBuilder(requiredMessage: kEmailNullError)
    .email(kInvalidEmailError)
    .maxLength(50, kEmailLongError)
    .build();
final validator_phone = ValidationBuilder(requiredMessage: kPhoneNullError)
    .regExp(
        RegExp(r"^0[0-9]{9}$"),
        kTitieShortError(
            "Phone number", "must start with 0 and contain only digits"))
    .maxLength(12, "Phone number must be at most 12 characters")
    .build();
final validator_address =
    ValidationBuilder(requiredMessage: kTitieNullError("Address"))
        .minLength(10, kTitieShortError("Address", "too short"))
        .maxLength(300, kTitieShortError("Address", "is too long"))
        .build();

final validator_first_name = ValidationBuilder(requiredMessage: kNamelNullError)
    .minLength(1)
    .maxLength(20)
    .build();
final validator_last_name = ValidationBuilder(requiredMessage: kNamelNullError)
    .minLength(1)
    .maxLength(50)
    .build();
final validator_password = ValidationBuilder(requiredMessage: kPassNullError)
    .minLength(6, kShortPassError)
    .maxLength(40, kLongPassError)
    .build();

final validator_review =
    ValidationBuilder(requiredMessage: "Please enter your review")
        .maxLength(4000)
        .build();
final validator_rate =
    ValidationBuilder(requiredMessage: "Please enter your rate")
        .maxLength(4000)
        .build();
