import 'package:flutter/material.dart';
import '../../admin/dashboard/ui/dashboard_screen.dart';

class DashboardScreenManager extends StatelessWidget {
  const DashboardScreenManager({super.key});

  @override
  Widget build(BuildContext context) {
    return DashBoardScreen(isManager: true);
  }
}
