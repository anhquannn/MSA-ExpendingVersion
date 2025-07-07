import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../providers/inventory_provider.dart';
import '../widgets/inventory_tile.dart';
import '../providers/auth_provider.dart';

class InventoriesScreen extends StatefulWidget {
  const InventoriesScreen({super.key});

  @override
  State<InventoriesScreen> createState() => _InventoriesScreenState();
}

class _InventoriesScreenState extends State<InventoriesScreen> {
  final TextEditingController _searchCtrl = TextEditingController();
  late final ScrollController _scrollCtrl;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _scrollCtrl = ScrollController()..addListener(_onScroll);
    _load();
    _searchCtrl.addListener(() {
      // Live search when text changes (debounce could be added)
      context.read<InventoryProvider>().fetchInventories(refresh: true, keyword: _searchCtrl.text.trim());
    });
  }

  Future<void> _load() async {
    await context.read<InventoryProvider>().fetchInventories(keyword: _searchCtrl.text.trim());
    if (mounted) setState(() => _loading = false);
  }

  @override
  Widget build(BuildContext context) {
    final inventories = context.watch<InventoryProvider>().inventories;
    return Scaffold(
      appBar: AppBar(
        title: const Text('Kho'),
        actions: [
          IconButton(
            tooltip: 'Đăng xuất',
            icon: const Icon(Icons.logout),
            onPressed: () async {
              await context.read<AuthProvider>().logout();
              if (mounted) {
                Navigator.of(context).pushNamedAndRemoveUntil('/', (_) => false);
              }
            },
          ),
        ],
      ),
      floatingActionButton: _loading ? null : FloatingActionButton.extended(
        icon: const Icon(Icons.add_task_outlined),
        label: const Text('Tạo ICR'),
        onPressed: () async {
          await _showCreateICRDialog(context);
        },
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : Column(
              children: [
                Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: TextField(
                    controller: _searchCtrl,
                    decoration: const InputDecoration(
                      prefixIcon: Icon(Icons.search),
                      hintText: 'Tìm kho...',
                      border: OutlineInputBorder(),
                      isDense: true,
                    ),
                  ),
                ),
                Expanded(
                  child: ListView.separated(
                    controller: _scrollCtrl,
                    itemBuilder: (_, i) => InventoryTile(
                      inventory: inventories[i],
                      onTap: () => Navigator.of(context).pushNamed(
                        '/products',
                        arguments: inventories[i],
                      ),
                    ),
                    separatorBuilder: (_, __) => const Divider(),
                    itemCount: inventories.length,
                  ),
                ),
              ],
            ),
    );
  }

  Future<void> _showCreateICRDialog(BuildContext context) async {
    final inventories = context.read<InventoryProvider>().inventories;
    if (inventories.isEmpty) return;
    int selectedInventoryId = inventories.first.id;
    String note = '';
    DateTime selectedDate = DateTime.now();
    await showDialog(
      context: context,
      builder: (ctx) {
        return StatefulBuilder(
          builder: (ctx, setStateDialog) {
            return AlertDialog(
          title: const Text('Tạo Inventory Check Request'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              DropdownButton<int>(
                value: selectedInventoryId,
                isExpanded: true,
                items: inventories
                    .map((inv) => DropdownMenuItem<int>(
                          value: inv.id,
                          child: Text(inv.name),
                        ))
                    .toList(),
                onChanged: (v) {
                  if (v != null) {
                    setStateDialog(() => selectedInventoryId = v);
                  }
                },
              ),
              const SizedBox(height: 8),
              GestureDetector(
                onTap: () async {
                  final picked = await showDatePicker(
                    context: ctx,
                    initialDate: selectedDate,
                    firstDate: DateTime(2000),
                    lastDate: DateTime(2100),
                  );
                  if (picked != null) {
                    setStateDialog(() => selectedDate = picked);
                  }
                },
                child: InputDecorator(
                  decoration: const InputDecoration(
                    labelText: 'Ngày yêu cầu',
                    border: OutlineInputBorder(),
                  ),
                  child: Text('${selectedDate.year}-${selectedDate.month.toString().padLeft(2,'0')}-${selectedDate.day.toString().padLeft(2,'0')}'),
                ),
              ),
              const SizedBox(height: 8),
              TextField(
                decoration: const InputDecoration(labelText: 'Ghi chú'),
                onChanged: (v) => note = v,
              ),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.of(ctx).pop(),
              child: const Text('Hủy'),
            ),
            ElevatedButton(
              onPressed: () async {
                final ok = await context.read<InventoryProvider>().createInventoryCheckRequest(
                      inventoryId: selectedInventoryId,
                      note: note,
                      requestedDate: selectedDate,
                    );
                if (mounted) {
                  Navigator.of(ctx).pop();
                }
                ScaffoldMessenger.of(context).showSnackBar(
                  SnackBar(content: Text(ok ? 'Gửi yêu cầu thành công' : 'Gửi yêu cầu thất bại')),
                );
              },
              child: const Text('Gửi'),
            ),
          ],
            );
          },
        );
      },
    );
  }

  void _onScroll() {
    if (!_scrollCtrl.hasClients) return;
    final max = _scrollCtrl.position.maxScrollExtent;
    if (_scrollCtrl.offset >= max - 200) {
      context.read<InventoryProvider>().fetchInventories();
    }
  }

  @override
  void dispose() {
    _searchCtrl.dispose();
    _scrollCtrl.dispose();
    super.dispose();
  }
}

