
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';
import '../../../../../widget/build_tabbar_section.dart';
import '../../../../../widget/custom_notification.dart';
import '../bloc/admin_notification_bloc.dart';

class AdminNotificationScreen extends BaseView<AdminNotificationBloc> {
  final bool? isManager;

  const AdminNotificationScreen({super.key, this.isManager});

  @override
  AdminNotificationBloc createBloc() => AdminNotificationBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as AdminNotificationBloc;

    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(bloc.viewContext,size: 25),
      centerTitle: true,
      title: Text(
        'Thông báo',
        style: TextStyle(
          color: Colors.white,
          fontWeight: FontWeight.bold,
          fontSize: 18,
        ),
      ),

      bodyBuilder: (controller) {
        return Container(
          width: AppSize.width(),
          color: toHexToColor(backgroundColor),
          child: buildTabSection(
            tabs: [
              if (isManager == false) ...[Tab(text: 'Xuất kho')],
              Tab(text: 'Nhập kho'),
              Tab(text: 'Đơn hàng'),
              Tab(text: 'Tồn kho'),
            ],
            tabBarView: TabBarView(
              children: [
                if (isManager == false) ...[_buildGridItems()],
                _buildGridItems(),
                _buildGridItems(),
                _buildGridItems(),
              ],
            ),
          ),
        );
      },

      hideBottomBarOnScroll: true,
    );
  }

  Widget _buildGridItems() {
    return SizedBox(
      width: AppSize.w(0.95),
      child: SingleChildScrollView(
        child: ListView.builder(
          shrinkWrap: true,
          // Giúp ListView không chiếm quá nhiều không gian
          physics: NeverScrollableScrollPhysics(),
          // Tắt scroll của ListView vì đã có SingleChildScrollView
          itemCount: 10,
          // Chỉ có một item
          itemBuilder: (BuildContext context, int index) {
            return buildItemNotification(
              'Bạn có đơn hàng mới',
              'Đơn hàng của bạn sẽ được giao sau 2 ngày. Vui lòng chú ý điện thoại.',
              isRead: false,
            );
          },
        ),
      ),
    );
  }
}

class _SliverTabBarDelegate extends SliverPersistentHeaderDelegate {
  final PreferredSizeWidget
  _tabBar; // Sử dụng PreferredSizeWidget thay vì Widget

  _SliverTabBarDelegate(this._tabBar);

  @override
  double get minExtent => _tabBar.preferredSize.height;

  @override
  double get maxExtent => _tabBar.preferredSize.height;

  @override
  Widget build(
    BuildContext context,
    double shrinkOffset,
    bool overlapsContent,
  ) {
    return _tabBar; // Trả về TabBar bọc trong PreferredSize
  }

  @override
  bool shouldRebuild(covariant SliverPersistentHeaderDelegate oldDelegate) {
    return false;
  }
}
