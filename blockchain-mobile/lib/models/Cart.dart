// ignore_for_file: file_names

import 'Product.dart';

class Cart {
  int id;
  final Product product;
  int quantity;

  double? amount;

  Cart({
    required this.product,
    required this.quantity,
    this.amount,
    this.id = 0,
  });

  factory Cart.fromJson(Map<String, dynamic> json) {
    return Cart(
      id: json['id'],
      product: Product.fromJson(json['product']),
      quantity: json['quantity'] as int,
      amount: json['amount'] as double,
    );
  }
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'product': product.toJson(),
      'quantity': quantity,
      "amount": amount
    };
  }

  static num calculateTotal(List<Cart> carts) {
    double total = 0;
    if (carts.isEmpty) return total;
    for (var cartItem in carts) {
      // total += cartItem.product.secondPrice ??
      //     cartItem.product.price * cartItem.quantity;
      total += cartItem.amount ?? 0.0;
    }
    return total;
  }
}

// Demo data for our cart

// List<Cart> demoCarts = [
//   Cart(product: demoProducts[0], quantity: 2),
//   Cart(product: demoProducts[1], quantity: 1),
//   Cart(product: demoProducts[3], quantity: 1),
// ];
