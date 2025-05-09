import 'package:flutter/cupertino.dart';

import '../../admin/inventory_in/ui/inventory_in_screen.dart';

class InventoryManager extends StatelessWidget {
  const InventoryManager({super.key});

  @override
  Widget build(BuildContext context) {
    return InventoryInScreen(isManager: true);
  }
}
