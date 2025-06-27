import 'package:flutter/material.dart';
import '../models/inventory.dart';
import '../providers/inventory_provider.dart';
import 'package:provider/provider.dart'; // cần để dùng context.read

class InventoryTile extends StatelessWidget {
  const InventoryTile({
    super.key,
    required this.inventory,
    this.onTap, // <--- Thêm dòng này
  });

  final Inventory inventory;
  final VoidCallback? onTap; // <--- Thêm dòng này

  @override
  Widget build(BuildContext context) {
    final scheme = Theme.of(context).colorScheme;
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      color: scheme.secondaryContainer,
      elevation: 3,
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: scheme.primaryContainer,
          foregroundColor: scheme.onPrimaryContainer,
          child: const Icon(Icons.store),
        ),
        title: Text(
          inventory.name,
          style: const TextStyle(fontWeight: FontWeight.w600),
        ),
        subtitle: Text(inventory.address),
        trailing: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Text(
              'Doanh thu: ${inventory.totalRevenue.toStringAsFixed(0)}',
              style: const TextStyle(fontWeight: FontWeight.w500),
            ),
            IconButton(
              tooltip: 'Kiểm kê',
              icon: const Icon(Icons.check_circle_outline),
              color: scheme.primary,
              onPressed: () => _showCheckDialog(context),
            ),
          ],
        ),
        onTap: onTap,
      ),
    );
  }

  void _showCheckDialog(BuildContext context) {
    final noteCtrl = TextEditingController();
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('Xác nhận kiểm kê'),
        content: TextField(
          controller: noteCtrl,
          decoration: const InputDecoration(
            labelText: 'Ghi chú',
            isDense: true,
          ),
        ),
        actions: [
          OutlinedButton.icon(
            icon: const Icon(Icons.close),
            label: const Text('Huỷ'),
            onPressed: () => Navigator.pop(ctx),
          ),
          FilledButton.icon(
            icon: const Icon(Icons.check_circle),
            label: const Text('Xác nhận'),
            onPressed: () async {
              final provider = context.read<InventoryProvider>();
              final success = await provider.saveCheckedHistory(
                inventoryId: inventory.id,
                note: noteCtrl.text.trim(),
              );
              if (success && ctx.mounted) {
                Navigator.pop(ctx);
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('Đã lưu kiểm kê')),
                );
              } else {
                if (ctx.mounted) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Lưu thất bại')),
                  );
                }
              }
            },
          ),
        ],
      ),
    );
  }
}
