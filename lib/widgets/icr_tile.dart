import 'package:flutter/material.dart';

import '../models/inventory_check_request.dart';

class IcrTile extends StatelessWidget {
  const IcrTile({super.key, required this.icr, this.onDelete, this.onEdit});

  final InventoryCheckRequest icr;
  final VoidCallback? onDelete;
  final VoidCallback? onEdit;

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      color: scheme.secondaryContainer,
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: scheme.primaryContainer,
          foregroundColor: scheme.onPrimaryContainer,
          child: const Icon(Icons.content_paste_search_outlined),
        ),
        title: Text(
          'ICR #${icr.id} - ${icr.inventoryName}',
          style: const TextStyle(fontWeight: FontWeight.w600),
        ),
        subtitle: Text('${icr.requestedDate.year}-${icr.requestedDate.month.toString().padLeft(2,'0')}-${icr.requestedDate.day.toString().padLeft(2,'0')}\n${icr.note}'),
        trailing: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            IconButton(
              icon: const Icon(Icons.edit),
              tooltip: 'Sửa',
              onPressed: onEdit,
            ),
            IconButton(
              icon: const Icon(Icons.delete_outline),
              tooltip: 'Xoá',
              onPressed: () async {
                if (onDelete == null) return;
                final ok = await showDialog<bool>(
                      context: context,
                      builder: (ctx) => AlertDialog(
                        title: const Text('Xoá ICR?'),
                        content: const Text('Bạn có chắc muốn xoá mục này?'),
                        actions: [
                          TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('Huỷ')),
                          ElevatedButton(onPressed: () => Navigator.pop(ctx, true), child: const Text('Xoá')),
                        ],
                      ),
                    ) ??
                    false;
                if (ok) onDelete!();
              },
            ),
          ],
        ),
      ),
    );
  }
}
