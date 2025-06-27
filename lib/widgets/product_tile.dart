import 'package:flutter/material.dart';

import '../models/product.dart';
import 'package:provider/provider.dart';
import '../providers/inventory_provider.dart';

class ProductTile extends StatefulWidget {
  const ProductTile({super.key, required this.product});

  final Product product;

  @override
  State<ProductTile> createState() => _ProductTileState();
}

class _ProductTileState extends State<ProductTile> {
  late final TextEditingController _controller;
  bool _dirty = false;
  @override
  void initState() {
    super.initState();
    _controller = TextEditingController(text: widget.product.stockNumberChecked?.toString() ?? '');
    _controller.addListener(() {
      final txt = _controller.text;
      final original = widget.product.stockNumberChecked?.toString() ?? '';
      if ((_dirty && txt == original) || (!_dirty && txt != original)) {
        setState(() => _dirty = txt != original);
      }
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
        trailing: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            SizedBox(
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
            const SizedBox(width: 8),
            FilledButton.icon(
              style: FilledButton.styleFrom(minimumSize: const Size(64, 40)),
              icon: const Icon(Icons.save_alt_rounded),
              label: const Text('Lưu'),
              onPressed: _dirty
                  ? () {
                      final val = int.tryParse(_controller.text);
                      if (val != null) {
                        context.read<InventoryProvider>().updateProductChecked(
                              inventoryProductId: widget.product.id,
                              inventoryId: widget.product.inventoryId,
                              productId: widget.product.productId,
                              stockNumber: widget.product.stockNumber,
                              checked: val,
                            ).then((_) {
                          if (mounted) setState(() => _dirty = false);
                        });
                      }
                    }
                  : null,
            ),
          ],
        ),
      ),
    );
  }
}
