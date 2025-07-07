import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../models/inventory.dart';
import '../providers/inventory_provider.dart';
import '../widgets/product_tile.dart';

class ProductsScreen extends StatefulWidget {
  const ProductsScreen({super.key});

  @override
  State<ProductsScreen> createState() => _ProductsScreenState();
}

class _ProductsScreenState extends State<ProductsScreen> {
  late ScrollController _scrollCtrl;
  bool _initialized = false;
  late Inventory _inventory;

  void _onScroll() {
    if (_scrollCtrl.offset >= _scrollCtrl.position.maxScrollExtent &&
        !_scrollCtrl.position.outOfRange) {
      context.read<InventoryProvider>().fetchProducts(_inventory.id);
    }
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    if (!_initialized) {
      _initialized = true;
      _scrollCtrl = ScrollController()..addListener(_onScroll);
      _inventory = ModalRoute.of(context)!.settings.arguments as Inventory;
      context.read<InventoryProvider>().fetchProducts(_inventory.id);
    }
  }

  @override
  Widget build(BuildContext context) {
    final products = context.watch<InventoryProvider>().products;
    return Scaffold(
      appBar: AppBar(title: const Text('Tồn kho')),
      floatingActionButton: FloatingActionButton.extended(
        icon: const Icon(Icons.save_alt_rounded),
        label: const Text('Lưu lịch sử'),
        onPressed: () async {
          final noteCtrl = TextEditingController();
          await showDialog(
            context: context,
            builder: (ctx) => AlertDialog(
              title: const Text('Lưu lịch sử kiểm kê'),
              content: TextField(
                controller: noteCtrl,
                decoration: const InputDecoration(labelText: 'Ghi chú'),
              ),
              actions: [
                TextButton(
                  onPressed: () => Navigator.pop(ctx),
                  child: const Text('Huỷ'),
                ),
                ElevatedButton(
                  onPressed: () async {
                    final ok = await context.read<InventoryProvider>().saveCheckedHistory(
                          inventoryId: _inventory.id,
                          note: noteCtrl.text.trim(),
                        );
                    if (mounted) {
                      Navigator.pop(ctx);
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(content: Text(ok ? 'Đã lưu lịch sử' : 'Lưu thất bại')),
                      );
                    }
                  },
                  child: const Text('Lưu'),
                ),
              ],
            ),
          );
        },
      ),
      body: products.isEmpty
          ? const Center(child: CircularProgressIndicator())
          : ListView.separated(
              controller: _scrollCtrl,
              itemBuilder: (_, i) => ProductTile(product: products[i]),
              separatorBuilder: (_, __) => const Divider(),
              itemCount: products.length,
            ),
    );
  }

  @override
  void dispose() {
    _scrollCtrl.dispose();
    super.dispose();
  }
}

