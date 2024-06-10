import 'package:blockchain_mobile/1_controllers/providers/image_provider.dart';
import 'package:blockchain_mobile/1_controllers/services/others/toast_service.dart';
import 'package:blockchain_mobile/constants.dart';
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:image_picker/image_picker.dart';
import 'package:mobile_scanner/mobile_scanner.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:provider/provider.dart';

class QrScanner extends StatefulWidget {
  static const String routeName = "/qr-scanner";

  final String title;

  const QrScanner({super.key, this.title = 'Scanning Order'});

  @override
  State<QrScanner> createState() => _QrScannerState();

  static Future<dynamic> scan(BuildContext context) {
    return Navigator.of(context).pushNamed(QrScanner.routeName);
  }
}

class _QrScannerState extends State<QrScanner> {
  MobileScannerController cameraController = MobileScannerController();

  bool barcodeFound = false;
  @override
  Widget build(BuildContext context) {
    var white54 = Colors.black45;
    return Scaffold(
      appBar: AppBar(
        title: Text(
          widget.title,
          style: const TextStyle(color: Colors.white),
        ),
        actions: [
          IconButton(
            color: Colors.white,
            icon: ValueListenableBuilder(
              valueListenable: cameraController.torchState,
              builder: (context, state, child) {
                switch (state) {
                  case TorchState.off:
                    return const Icon(Icons.flash_off, color: Colors.grey);
                  case TorchState.on:
                    return const Icon(Icons.flash_on, color: Colors.yellow);
                }
              },
            ),
            iconSize: 32.0,
            onPressed: () => cameraController.toggleTorch(),
          ),
          IconButton(
              onPressed: () async {
                var result = await _requestStoragePermission();
                if (result == false) {
                  return;
                }
                XFile? image = await showDialog(
                  context: context,
                  builder: (context) {
                    context
                        .read<AppImageProvider>()
                        .pickImage(ImageSource.gallery)
                        .then((value) {
                      Navigator.pop(context, value);
                    });
                    return const Scaffold();
                  },
                );
                if (image != null) {
                  bool result = await cameraController.analyzeImage(image.path);
                  if (!result && context.mounted) {
                    ToastService.toastError(context, 'QR Code not found');
                  }
                }
              },
              icon: const FaIcon(FontAwesomeIcons.images))
        ],
        backgroundColor: Colors.black,
      ),
      body: Stack(children: [
        MobileScanner(
          controller: cameraController,
          onDetect: (capture) {
            final List<Barcode> barcodes = capture.barcodes;
            // final Uint8List? image = capture.image;
            for (final barcode in barcodes) {
              debugPrint('Barcode found! ${barcode.rawValue}');
              if (!barcodeFound) {
                Navigator.pop(context, barcode.rawValue);
              }
              barcodeFound = true;
            }
          },
        ),
        Positioned(
          child: Column(children: [
            Expanded(
              flex: 3,
              child: Container(
                color: white54,
              ),
            ),
            Expanded(
              flex: 2,
              child: Row(
                children: [
                  Expanded(
                    child: Container(
                      color: white54,
                    ),
                  ),
                  const AspectRatio(
                    aspectRatio: 1,
                    child: SizedBox(child: DividerSlideAnimation()),
                  ),
                  Expanded(
                    child: Container(
                      color: white54,
                    ),
                  ),
                ],
              ),
            ),
            Expanded(
              flex: 3,
              child: Container(
                color: white54,
              ),
            ),
          ]),
        )
      ]),
    );
  }

  Future<bool> _requestStoragePermission() async {
    var status = await Permission.storage.status;
    var photo = await Permission.photos.status;
    if (status.isDenied) {
      status = await Permission.storage.request();
    }

    if (photo.isDenied) {
      photo = await Permission.photos.request();
    }
    if ((status.isPermanentlyDenied || photo.isPermanentlyDenied) &&
        context.mounted) {
      ToastService.toastError(context, "Storage access is permanently denied!");
    }

    return !status.isGranted;
  }
}

class DividerSlideAnimation extends StatefulWidget {
  const DividerSlideAnimation({super.key});

  @override
  _DividerSlideAnimationState createState() => _DividerSlideAnimationState();
}

class _DividerSlideAnimationState extends State<DividerSlideAnimation>
    with SingleTickerProviderStateMixin {
  late final AnimationController _controller;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(milliseconds: 5000),
      vsync: this,
    )..repeat(reverse: false);
    // Repeats the animation indefinitely, reversing the direction on repeat cycles.
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (context, child) {
        return AlignTransition(
          alignment: Tween<Alignment>(
            begin: Alignment.bottomCenter,
            end: Alignment.topCenter,
          ).animate(
            CurvedAnimation(
              parent: _controller,
              curve: const Interval(
                0.0,
                0.9,
                curve: Curves.linear,
              ),
              // Use a smooth, linear animation curve
            ),
          ),
          child: child!,
        );
      },
      child: Container(
        padding: const EdgeInsets.all(8.0),
        decoration: BoxDecoration(boxShadow: [
          BoxShadow(color: kPrimaryColor.withOpacity(0.3), blurRadius: 15)
        ]),
        child: Container(
          decoration: BoxDecoration(
            borderRadius: const BorderRadius.all(Radius.elliptical(100, 50)),
            // shape: BoxShape.circle,
            gradient: LinearGradient(
              begin: Alignment.centerLeft,
              end: Alignment.centerRight,
              colors: [
                kPrimaryColor.withOpacity(0.1),
                kPrimaryColor,
                kPrimaryColor.withOpacity(0.1),
              ],
            ),
          ),
          height: 5,
        ),
      ),
    );
  }
}
