import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/utility.dart';
import 'package:msa/feature/domain/entities/notification_model.dart';

class NotificationDetailBottomSheet extends StatelessWidget {
  final NotificationModel notification;

  const NotificationDetailBottomSheet({required this.notification, super.key});

  @override
  Widget build(BuildContext context) {
    final order = notification.order;
    final branch = order?.branch;

    return Padding(
      padding: const EdgeInsets.all(16),
      child: Wrap(
        children: [
          Center(
            child: Container(
              width: 50,
              height: 5,
              margin: const EdgeInsets.only(bottom: 16),
              decoration: BoxDecoration(
                color: Colors.grey[300],
                borderRadius: BorderRadius.circular(10),
              ),
            ),
          ),
          ListTile(
            leading: CircleAvatar(
              backgroundImage: AssetImage(notification.user?.image ?? avtMen1),
              radius: 24,
            ),
            title: Text(notification.user?.fullName ?? 'Người dùng'),
            subtitle: Text(notification.user?.email ?? ''),
          ),
          const SizedBox(height: 12),
          Text(
            notification.message ?? '',
            style: const TextStyle(fontSize: 16, fontWeight: FontWeight.w600),
          ),
          const SizedBox(height: 12),
          Row(
            children: [
              const Icon(Icons.access_time, size: 18, color: Colors.grey),
              const SizedBox(width: 6),
              Text(
                formatDateString(
                  notification.notificationDate ??
                      formatDateTime(DateTime.now()),
                ),
              ),
            ],
          ),
          const Divider(height: 32),

          if (order != null) ...[
            const Text(
              'Thông tin đơn hàng',
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            infoRow('Mã đơn hàng', '#${order.orderId}'),
            infoRow('Ngày tạo', order.orderDate),
            infoRow('Trạng thái', order.status),
            infoRow('Tổng tiền', '${order.grandTotal} đ'),
            const SizedBox(height: 16),
          ],

          if (branch != null) ...[
            const Text(
              'Chi nhánh xử lý',
              style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            infoRow('Tên chi nhánh', branch.name),
            infoRow('Điện thoại', branch.phone),
            infoRow(
              'Địa chỉ',
              '${branch.street}, ${branch.ward}, ${branch.district}, ${branch.city}',
            ),
          const SizedBox(height: 20),
          ],
          const SizedBox(height: 20),
        ],
      ),
    );
  }

  Widget infoRow(String title, String? value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text('$title: ', style: const TextStyle(fontWeight: FontWeight.w600)),
          Expanded(child: Text(value ?? '')),
        ],
      ),
    );
  }
}
