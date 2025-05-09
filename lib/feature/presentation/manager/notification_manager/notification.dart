import 'package:flutter/cupertino.dart';

import '../../admin/admin_notification/ui/admin_notification_screen.dart';

class NotificationManager extends StatelessWidget {
  const NotificationManager({super.key});

  @override
  Widget build(BuildContext context) {
    return AdminNotificationScreen(isManager: false);
  }
}
