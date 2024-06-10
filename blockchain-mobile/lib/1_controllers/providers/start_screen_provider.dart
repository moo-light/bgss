import 'dart:math';

import 'package:flutter/foundation.dart';
import 'package:flutter/widgets.dart';

class StartScreenProdvider extends ChangeNotifier {
  bool done = false;
  double size = 0;
  double getSize(BuildContext context) {
    size = min(MediaQuery.of(context).size.width,
            MediaQuery.of(context).size.height) *
        (2 / 3);
    return size;
  }

  void waitaSecond()async {
    if(!kDebugMode) {
      await Future.delayed(const Duration(seconds: 4));
    }
    done = true;
    notifyListeners();
  }
}
