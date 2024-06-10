import 'package:blockchain_mobile/models/Product.dart';
import 'package:blockchain_mobile/models/enums/e_received_status.dart';

import 'enums/e_process_receive_product.dart';
import 'enums/e_user_confirm.dart';

class Order {
  int id;
  DateTime createDate;
  EReceivedStatus statusReceived;
  EUserConfirm userConfirm;
  String qrCode;
  dynamic discountCode;
  double totalAmount;
  String firstName;
  String lastName;
  String email;
  String phoneNumber;
  String? address;
  List<OrderDetail> orderDetails;
  bool consignment;

  static String? errorQrScanMessage = "This is an Order qr Code";

  Order({
    required this.id,
    required this.createDate,
    required this.statusReceived,
    required this.qrCode,
    required this.discountCode,
    required this.totalAmount,
    required this.firstName,
    required this.lastName,
    required this.email,
    required this.phoneNumber,
    required this.address,
    required this.userConfirm,
    required this.orderDetails,
    required this.consignment,
  });

  factory Order.fromJson(Map<String, dynamic> json) {
    String statusReceived = json['statusReceived'];
    return Order(
      id: json['id'],
      createDate: DateTime.parse(json['createDate']).toLocal(),
      statusReceived: EReceivedStatus.values
          .firstWhere((element) => element.name == statusReceived),
      qrCode: json['qrCode'],
      discountCode: json['discountCode'],
      totalAmount: json['totalAmount'].toDouble(),
      firstName: json['firstName'],
      lastName: json['lastName'],
      email: json['email'],
      phoneNumber: json['phoneNumber'],
      address: json['address'] ?? "",
      orderDetails: List<OrderDetail>.from(
          json['orderDetails'].map((x) => OrderDetail.fromJson(x))),
      userConfirm: EUserConfirm.values.firstWhere(
          (element) => element.name == json['userConfirm'],
          orElse: () => EUserConfirm.NULL),
      consignment: json['consignment'],
    );
  }

  @override
  String toString() {
    return 'Order{id: $id, createDate: $createDate, statusReceived: $statusReceived, qrCode: $qrCode, discountCode: $discountCode, totalAmount: $totalAmount, firstName: $firstName, lastName: $lastName, email: $email, phoneNumber: $phoneNumber, address: $address, orderDetails: $orderDetails, consignment: $consignment}';
  }
}

class OrderDetail {
  int id;
  Product product;
  int quantity;
  double price;
  double amount;
  EProcessReceiveProduct processReceiveProduct;

  OrderDetail({
    required this.id,
    required this.product,
    required this.quantity,
    required this.price,
    required this.amount,
    required this.processReceiveProduct,
  });

  factory OrderDetail.fromJson(Map<String, dynamic> json) {
    var processReceiveProduct = json['processReceiveProduct'];
    return OrderDetail(
      id: json['id'],
      product: Product.fromJson(json['product']),
      quantity: json['quantity'],
      price: json['price'].toDouble(),
      amount: json['amount'].toDouble(),
      processReceiveProduct: EProcessReceiveProduct.values
          .firstWhere((element) => element.name == processReceiveProduct),
    );
  }
  @override
  String toString() {
    return 'OrderDetail{id: $id, product: $product, quantity: $quantity, price: $price, amount: $amount, processReceiveProduct: $processReceiveProduct}';
  }
}
