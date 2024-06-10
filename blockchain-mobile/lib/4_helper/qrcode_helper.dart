class QrCodeHelper {
  static String getQrImage(String data, {required EQrType type}) {
    // return Uri.encodeFull(
    //     "https://api.qrserver.com/v1/create-qr-code/?size=100x100&data=${type.symbol}!$data");
    return Uri.encodeFull(
        "https://api.qrserver.com/v1/create-qr-code/?data=${type.symbol}!$data&size=100x100");
  }
}

enum EQrType { Order, Withdraw }

extension EQrTypeExt on EQrType {
  String get symbol => switch (this) {
        EQrType.Order => 'OR',
        EQrType.Withdraw => 'WD',
      };
}
