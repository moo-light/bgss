import 'package:flutter/material.dart';

class IsLoadingWG extends StatelessWidget {
  final bool isLoading;
  final Widget? child;

  final Widget? loading;

  const IsLoadingWG(
      {super.key, required this.isLoading, this.child, this.loading});

  @override
  Widget build(BuildContext context) {
    const defaultLoading = Center(
      child: CircularProgressIndicator(),
    );
    return isLoading ? loading ?? defaultLoading : child?? Container();
  }
}
