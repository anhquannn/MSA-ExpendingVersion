import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../models/inventory_check_request.dart';
import '../providers/icr_provider.dart';
import '../providers/inventory_provider.dart';
import '../providers/auth_provider.dart';
import '../widgets/icr_tile.dart';

class IcrScreen extends StatefulWidget {
  const IcrScreen({super.key});

  @override
  State<IcrScreen> createState() => _IcrScreenState();
}

class _IcrScreenState extends State<IcrScreen> {
  final _searchCtrl = TextEditingController();
  final _scrollCtrl = ScrollController();
  bool _init = false;

  void _onScroll() {
    if (_scrollCtrl.offset >= _scrollCtrl.position.maxScrollExtent - 200) {
      context.read<IcrProvider>().fetch();
    }
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();
    if (!_init) {
      _init = true;
      _scrollCtrl.addListener(_onScroll);
      context.read<IcrProvider>().fetch();
      _searchCtrl.addListener(() {
        context.read<IcrProvider>().fetch(refresh: true, keyword: _searchCtrl.text.trim());
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final icrs = context.watch<IcrProvider>().items;
    return Scaffold(
      appBar: AppBar(title: const Text('Inventory Check Requests')),
      floatingActionButton: FloatingActionButton.extended(
        icon: const Icon(Icons.add),
        label: const Text('Tạo'),
        onPressed: () async => _showAddDialog(context),
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: TextField(
              controller: _searchCtrl,
              decoration: const InputDecoration(
                prefixIcon: Icon(Icons.search),
                hintText: 'Tìm ICR...',
                border: OutlineInputBorder(),
                isDense: true,
              ),
            ),
          ),
          Expanded(
            child: icrs.isEmpty
                ? const Center(child: CircularProgressIndicator())
                : ListView.separated(
                    controller: _scrollCtrl,
                    itemBuilder: (_, i) => IcrTile(
                      icr: icrs[i],
                      onDelete: () async {
                        final ok = await context.read<IcrProvider>().delete(icrs[i].id);
                        if (mounted) {
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(content: Text(ok ? 'Đã xoá' : 'Xoá thất bại')),
                          );
                        }
                      },
                      onEdit: () => _showEditDialog(context, icrs[i]),
                    ),
                    separatorBuilder: (_, __) => const Divider(),
                    itemCount: icrs.length,
                  ),
          ),
        ],
      ),
    );
  }

  Future<void> _showAddDialog(BuildContext context) async {
    final inventories = context.read<InventoryProvider>().inventories;
    if (inventories.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('Chưa có kho')));
      return;
    }
    int inventoryId = inventories.first.id;
    String note = '';
    DateTime date = DateTime.now();

    await showDialog(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setStateDlg) => AlertDialog(
          title: const Text('Tạo ICR'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              DropdownButton<int>(
                value: inventoryId,
                isExpanded: true,
                items: inventories
                    .map((e) => DropdownMenuItem(value: e.id, child: Text(e.name)))
                    .toList(),
                onChanged: (v) {
                  if (v != null) setStateDlg(() => inventoryId = v);
                },
              ),
              const SizedBox(height: 8),
              GestureDetector(
                onTap: () async {
                  final picked = await showDatePicker(
                    context: ctx,
                    initialDate: date,
                    firstDate: DateTime(2000),
                    lastDate: DateTime(2100),
                  );
                  if (picked != null) setStateDlg(() => date = picked);
                },
                child: InputDecorator(
                  decoration: const InputDecoration(
                    labelText: 'Ngày yêu cầu',
                    border: OutlineInputBorder(),
                  ),
                  child: Text('${date.year}-${date.month.toString().padLeft(2,'0')}-${date.day.toString().padLeft(2,'0')}'),
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
            TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Huỷ')),
            ElevatedButton(
              onPressed: () async {
                final provider = context.read<IcrProvider>();
                final auth = context.read<AuthProvider>();
                final ok = await provider.add(
                  InventoryCheckRequest(
                    id: 0,
                    note: note,
                    requestedDate: date,
                    status: 'PENDING',
                    inventoryId: inventoryId,
                    inventoryName: inventories.firstWhere((e) => e.id == inventoryId).name,
                    surveyorId: auth.userId ?? 0,
                    userId: 0,
                  ),
                );
                if (mounted) {
                  Navigator.pop(ctx);
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text(ok ? 'Đã tạo' : 'Tạo thất bại')),
                  );
                }
              },
              child: const Text('Tạo'),
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _showEditDialog(BuildContext context, InventoryCheckRequest icr) async {
    String note = icr.note;
    String status = icr.status;
    await showDialog(
      context: context,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setStateDlg) => AlertDialog(
          title: Text('Sửa ICR #${icr.id}'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              TextField(
                controller: TextEditingController(text: note),
                decoration: const InputDecoration(labelText: 'Ghi chú'),
                onChanged: (v) => note = v,
              ),
              const SizedBox(height: 8),
              Builder(builder: (context) {
                final List<String> opts = ['PENDING', 'RECEIVED', 'DONE'];
                if (!opts.contains(status)) opts.add(status);
                return DropdownButton<String>(
                  value: status,
                  isExpanded: true,
                  items: opts
                      .map((s) => DropdownMenuItem(value: s, child: Text(s)))
                      .toList(),
                  onChanged: (v) {
                    if (v != null) setStateDlg(() => status = v);
                  },
                );
              }),
            ],
          ),
          actions: [
            TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('Huỷ')),
            ElevatedButton(
              onPressed: () async {
                final ok = await context.read<IcrProvider>().update(icr.id, {
                  'note': note,
                  'status': status,
                });
                if (mounted) {
                  Navigator.pop(ctx);
                  ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text(ok ? 'Đã cập nhật' : 'Cập nhật thất bại')),
                  );
                }
              },
              child: const Text('Lưu'),
            ),
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    _searchCtrl.dispose();
    _scrollCtrl.dispose();
    super.dispose();
  }
}
