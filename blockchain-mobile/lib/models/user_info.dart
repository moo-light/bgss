
import 'avatar_data.dart';
import 'balance.dart';
import 'ci_card_image.dart';
import 'gold_transaction.dart';
import 'inventory.dart';
class UserInfo {
  static const _kDefaultAvt = "assets/images/Profile Image.png";
  int id;
  String firstName;
  String lastName;
  String phoneNumber;
  AvatarData? avatarData;
  String? doB;
  String? address;
  dynamic ciCard;
  List<CiCardImage> ciCardImage;
  Balance balance;
  Inventory inventory;
  List<GoldTransaction>? goldTransactions;

  UserInfo({
    required this.id,
    required this.firstName,
    required this.lastName,
    required this.phoneNumber,
    this.avatarData,
    this.doB,
    this.address,
    this.ciCard,
    required this.ciCardImage,
    required this.balance,
    required this.inventory,
    this.goldTransactions,
  });

  factory UserInfo.fromJson(Map<String, dynamic> json) {
    var avatarData2 = json['avatarData'] != null
        ? AvatarData.fromJson(json['avatarData'])
        : null;

    return UserInfo(
      id: json['id'],
      firstName: json['firstName'],
      lastName: json['lastName'],
      phoneNumber: json['phoneNumber'],
      avatarData: avatarData2,

      // avatarData: json['avatarData'] != null ? AvatarData.fromJson(json['avatarData']) : null,

      doB: json['doB'],
      address: json['address'],
      ciCard: json['ciCard'],
      ciCardImage: json['ciCardImage'] != null
          ? (json['ciCardImage'] as List).map((e) => CiCardImage.fromJson(e)).toList()
          : [],
      balance: Balance.fromJson(json['balance']),
      inventory: Inventory.fromJson(json['inventory']),
      goldTransactions: (json['goldTransactions'] as List<dynamic>?)
              ?.map((x) => GoldTransaction.fromJson(x))
              .toList() ??
          [],
    );
  }
}
