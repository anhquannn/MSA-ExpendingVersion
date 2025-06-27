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
  bool _initialized = false;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    if (!_initialized) {
      _initialized = true;
      final inventory = ModalRoute.of(context)!.settings.arguments as Inventory;
      context.read<InventoryProvider>().fetchProducts(inventory.id);
    }
  }

  @override
  Widget build(BuildContext context) {
    final products = context.watch<InventoryProvider>().products;
    return Scaffold(
      appBar: AppBar(title: const Text('Tồn kho')),
      body: products.isEmpty
          ? const Center(child: CircularProgressIndicator())
          : ListView.separated(
              itemBuilder: (_, i) => ProductTile(product: products[i]),
              separatorBuilder: (_, __) => const Divider(),
              itemCount: products.length,
            ),
    );
  }
}
