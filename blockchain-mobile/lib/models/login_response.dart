import 'dart:convert';

class LoginResponseObject {
  String token;
  int id;
  String username;
  String email;
  List<String> roles;
  String tokenType;
  String accessToken;

  // Constructor
  LoginResponseObject({
    required this.token,
    required this.id,
    required this.username,
    required this.email,
    required this.roles,
    required this.tokenType,
    required this.accessToken,
  });

  // Factory method to create an instance from JSON
  factory LoginResponseObject.fromJson(String jsonString) {
    Map<String, dynamic> jsonMap = json.decode(jsonString);
    return LoginResponseObject(
      token: jsonMap['token'],
      id: jsonMap['id'],
      username: jsonMap['username'],
      email: jsonMap['email'],
      roles: List<String>.from(jsonMap['roles']),
      tokenType: jsonMap['tokenType'],
      accessToken: jsonMap['accessToken'],
    );
  }

  factory LoginResponseObject.fromMap(Map<String, dynamic> map) {
    return LoginResponseObject(
      token: map['token'] ?? '',
      id: map['id'] ?? 0,
      username: map['username'] ?? '',
      email: map['email'] ?? '',
      roles: (map['roles'] != null) ? List<String>.from(map['roles']) : [],
      tokenType: map['tokenType'] ?? '',
      accessToken: map['accessToken'] ?? '',
    );
  }
  Map<String, dynamic> toJson() {
    return {
      'token': token,
      'id': id,
      'username': username,
      'email': email,
      'roles': roles,
      'tokenType': tokenType,
      'accessToken': accessToken,
    };
  }
}
