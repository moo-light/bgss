import 'dart:math';

import 'package:blockchain_mobile/1_controllers/repositories/cart_repository.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:blockchain_mobile/models/Cart.dart';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';

class CartProvider extends ChangeNotifier {
  final CartRepository _cartRepository = CartRepository();
  List<Cart> carts = [];
  bool isLoading = false;

  var loadCartMessage = "";

  Future<void> addToCart(Cart cartItem) async {
    isLoading = true;
    notifyListeners();
    final pref = await SharedPreferences.getInstance();
    // Add the cart item to the local list
    var check = false;
    final result = await _cartRepository.addProductToCart(
      productId: cartItem.product.id,
      quantity: cartItem.quantity,
      price: cartItem.product.price,
    );
    if (result.isLeft) {
      for (var element in carts) {
        if (element.product.id == cartItem.product.id) {
          element.quantity += cartItem.quantity;
          check = true;
        }
      }
      cartItem = result.left;
      if (check) {
      } else {
        carts.add(cartItem);
      }
    }

    // Convert the updated list of cart items to JSON strings and store in shared preferences
    // _saveCart(pref);
    isLoading = false;
    // Notify listeners that the cart has been updated
    notifyListeners();
  }

  Future<void> removeFromCart(BuildContext context, Cart cartItem) async {
    final pref = await SharedPreferences.getInstance();
    isLoading = true;
    await Future.delayed(Durations.extralong1);
    final removeResult = await _cartRepository.removeCartId(cartItem.id);
    removeResult.fold(
      (message) {
        // If successful, remove the cart item from the local list
        carts.remove(cartItem);

        // Convert the updated list of cart items to JSON strings and store in shared preferences
        // _saveCart(pref);
        ToastService.toastSuccess(
          context,
          "Remove Cart Item Success",
          alignment: Alignment.topCenter,
          autoCloseDuration: const Duration(seconds: 2),
        );
        // Notify listeners that the cart has been updated
      },
      (error) {
        // Handle error if necessary
        debugPrint("Error removing cart item: $error");
      },
    );
    isLoading = false;

    notifyListeners();
  }

  // Future<void> updateCartItem(Cart cartItem, [bool? increaseQuantity]) async {
  //   final pref = await SharedPreferences.getInstance();
  //   if (increaseQuantity == null) {
  //     // Toggle Select
  //     for (var element in _selectedList!.toList()) {
  //       final prefs = element.split("|");
  //       if (prefs[0] == cartItem.id.toString()) {
  //         _selectedList?.remove(element);
  //         _selectedList?.add("${prefs[0]}|${cartItem.selected}");
  //       }
  //     }
  //   } else {
  //     for (var element in carts) {
  //       if (element.product.id == cartItem.product.id) {
  //         late final Either<String, String?> result;
  //         // Increase or decrease the quantity by 1

  //         if (increaseQuantity) {
  //           // Call API to increase quantity
  //           result = await _cartRepository.increaseQuantity(
  //             element.id,
  //           );
  //         } else {
  //           // Call API to decrease quantity
  //           result = await _cartRepository.decreaseQuantity(
  //             element.id,
  //           );
  //         }
  //       }
  //     }
  //   }
  //   // Convert the updated list of cart items to JSON strings and store in shared preferences
  //   await _saveCart(pref);

  //   // Notify listeners that the cart has been updated
  //   notifyListeners();
  // }

  Future<void> updateCartItem(Cart cartItem, [bool? increaseQuantity]) async {
    SharedPreferences pref = await SharedPreferences.getInstance();
    if (increaseQuantity == null) {
      // Toggle Select
    } else {
      for (var element in carts) {
        if (element.product.id == cartItem.product.id) {
          int newQuantity = increaseQuantity
              ? element.quantity + 1
              : max(1, element.quantity - 1);

          final updateResult = increaseQuantity
              ? await _cartRepository.increaseQuantity(element.id, newQuantity)
              : await _cartRepository.decreaseQuantity(element.id, newQuantity);

          updateResult.fold(
            (updatedQuantity) {
              element.quantity =
                  updatedQuantity; // Cập nhật số lượng nếu API thành công
            },
            (error) {
              print("Error updating cart item: $error");
            },
          );
        }
      }
    }

    // Convert the updated list of cart items to JSON strings and store in SharedPreferences
    // await _saveCart(pref);
    // Notify listeners that the cart has been updated
    notifyListeners();
  }

  void loadCarts() async {
    final pref = await SharedPreferences.getInstance();
    final uid = pref.getString(STORAGE_USERID);
    // if user not login dont load cart
    if (uid == null) return;

    final result = await _cartRepository.getCartLists();
    if (result.isLeft) {
      carts = result.left;
      // _saveCart(pref);
    }
    if (result.isRight) {
      loadCartMessage = result.right['message'];
      carts = [];
    }
    notifyListeners();
  }

  // Future<void> _saveCart(SharedPreferences pref) async {
  //   // Convert the list of cart items to JSON strings and store in shared preferences
  //   final uid = pref.getString(STORAGE_USERID);
  //   final cartsJson = carts.map((cart) => jsonEncode(cart.toJson())).toList();
  //   pref.setStringList(STORAGE_CARTS(uid), cartsJson);
  // }
}
