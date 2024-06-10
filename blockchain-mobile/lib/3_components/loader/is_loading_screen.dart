import 'package:blockchain_mobile/3_components/loader/is_loading.dart';
import 'package:flutter/material.dart';

class LoadingScreen extends StatelessWidget {

  const LoadingScreen({super.key,this.appBar});
  final PreferredSizeWidget? appBar;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: appBar,
      body: const IsLoadingWG(isLoading: true),
    );
  }
}
