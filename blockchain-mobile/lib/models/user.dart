import 'user_info.dart';

class User {
  int userId;
  String username;
  String email;
  UserInfo userInfo;

  User({
    required this.userId,
    required this.username,
    required this.email,
    required this.userInfo,
  });
  String get displayName => "${userInfo.firstName} ${userInfo.lastName}";
  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      userId: json['userId'],
      username: json['username'],
      email: json['email'],
      userInfo: UserInfo.fromJson(json['userInfo']),
    );
  }
}
