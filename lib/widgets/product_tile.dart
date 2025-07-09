import 'package:flutter/material.dart';

import '../models/product.dart';


class ProductTile extends StatefulWidget {
  const ProductTile({super.key, required this.product});

  final Product product;

  @override
  State<ProductTile> createState() => _ProductTileState();
}

class _ProductTileState extends State<ProductTile> {
  late final TextEditingController _controller;

  @override
  void initState() {
    super.initState();
    _controller = TextEditingController(text: widget.product.stockNumberChecked?.toString() ?? '');
    _controller.addListener(() {
      final val = int.tryParse(_controller.text);
      widget.product.stockNumberChecked = val;
    });
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: scheme.primaryContainer,
          foregroundColor: scheme.onPrimaryContainer,
          child: Text(
            widget.product.code.substring(0, 1),
            style: const TextStyle(fontWeight: FontWeight.bold),
          ),
        ),
        title: Text(widget.product.code),
        subtitle: Text('Tồn kho: ${widget.product.stockNumber}'),
         trailing: SizedBox(
          width: 90,
          child: TextField(
            controller: _controller,
            keyboardType: TextInputType.number,
            textAlign: TextAlign.center,
            decoration: const InputDecoration(
              hintText: 'Đã kiểm',
              isDense: true,
            ),
          ),
        ),
      ),
    );
  }
}
