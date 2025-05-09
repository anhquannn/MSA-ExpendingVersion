import 'package:flutter/cupertino.dart';

import '../../admin/order/ui/order_screen.dart';

class OrderManagerScreen extends StatelessWidget {
  const OrderManagerScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return OrderScreen(isManager: true);
  }
}
