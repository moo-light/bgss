import 'package:flutter/material.dart';

class SelectionComponents extends StatelessWidget {
  final bool selected;
  final VoidCallback onPressed;
  final String label;
  const SelectionComponents(
      {super.key,
      required this.selected,
      required this.onPressed,
      required this.label});

  @override
  Widget build(BuildContext context) {
    return TextButton(
      onPressed: onPressed,
      style: TextButton.styleFrom(
        backgroundColor: selected
            ? Theme.of(context).colorScheme.primary
            : Colors.transparent,
        foregroundColor: selected ? Colors.white : Colors.black,
        shape: const RoundedRectangleBorder(
            borderRadius: BorderRadius.all(Radius.circular(16))),
        side: !selected ? const BorderSide(width: 1, color: Colors.grey) : null,
      ),
      child: Text(label),
    );
  }
}
