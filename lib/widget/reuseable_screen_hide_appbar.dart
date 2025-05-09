import 'dart:async';
import 'package:curved_labeled_navigation_bar/curved_navigation_bar.dart';
import 'package:curved_labeled_navigation_bar/curved_navigation_bar_item.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import '../core/config/constant.dart';
import '../core/utils/prarse_color.dart';

class CustomScaffold extends StatefulWidget {
  final Widget title;
  final Widget Function(ScrollController controller) bodyBuilder;
  final List<Widget>? appBarActions;
  final Widget? appBarLeading;
  final TextStyle? appBarTextStyle;
  final bool appBarGradient;
  final bool bottomBarGradient;
  final List<BottomBarItem>? bottomBarItems;
  final List<BottomBarItem>? bottomBarItemsCustom;
  final bool hideBottomBarOnScroll;
  final bool centerTitle;
  final Widget? tabBar;
  final Widget? floatActionButton;

  const CustomScaffold({
    super.key,
    this.centerTitle = false,
    required this.title,
    required this.bodyBuilder,
    this.bottomBarItems,
    this.bottomBarItemsCustom,
    this.appBarActions,
    this.appBarLeading,
    this.appBarTextStyle,
    this.appBarGradient = true,
    this.bottomBarGradient = true,
    this.hideBottomBarOnScroll = true,
    this.tabBar,
    this.floatActionButton,
  });

  @override
  State<CustomScaffold> createState() => _CustomScaffoldState();
}

class _CustomScaffoldState extends State<CustomScaffold> {
  final ScrollController _scrollController = ScrollController();
  final ValueNotifier<bool> _isAppBarVisible = ValueNotifier(true);
  final ValueNotifier<bool> _isBottomBarVisible = ValueNotifier(true);
  final ValueNotifier<int> _selectIndex = ValueNotifier(3);
  double _lastOffset = 0;
  Timer? _debounceTimer;

  @override
  void initState() {
    super.initState();
    _scrollController.addListener(_handleScroll);
  }

  void _handleScroll() {
    if (_debounceTimer?.isActive ?? false) return;
    _debounceTimer = Timer(const Duration(milliseconds: 50), () {
      final offset = _scrollController.offset;
      final delta = offset - _lastOffset;

      if (delta.abs() > 10) {
        if (delta > 0 &&
            (_isAppBarVisible.value || _isBottomBarVisible.value)) {
          _isAppBarVisible.value = false;
          if (widget.hideBottomBarOnScroll) {
            _isBottomBarVisible.value = false;
          }
        } else if (delta < 0 &&
            (!_isAppBarVisible.value || !_isBottomBarVisible.value)) {
          _isAppBarVisible.value = true;
          if (widget.hideBottomBarOnScroll) {
            _isBottomBarVisible.value = true;
          }
        }
        _lastOffset = offset;
      }
    });
  }

  void _onBottomItemTapped(int index) {
    _selectIndex.value = index;
    widget.bottomBarItemsCustom?[index].onTap(index);
    setState(() {});
  }

  @override
  void dispose() {
    _debounceTimer?.cancel();
    _scrollController.removeListener(_handleScroll);
    _scrollController.dispose();
    _isAppBarVisible.dispose();
    _isBottomBarVisible.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      extendBody: true,
      body: Container(
        decoration: BoxDecoration(color: toHexToColor(backgroundColor)),
        child: Column(
          children: [
            _buildAnimatedAppBar(),
            widget.tabBar ?? Container(),
            Expanded(child: widget.bodyBuilder(_scrollController)),
          ],
        ),
      ),
      backgroundColor: toHexToColor('#FFFFFF'),
      bottomNavigationBar: ValueListenableBuilder<bool>(
        valueListenable:
            _isBottomBarVisible, // Lắng nghe sự thay đổi của _isBottomBarVisible
        builder: (context, isVisible, _) {
          return ValueListenableBuilder<int>(
            valueListenable:
                _selectIndex, // Lắng nghe sự thay đổi của _selectIndex
            builder: (context, selectedIndex, _) {
              return _isBottomBarVisible.value
                  ? buildCustomBottomBarAnimation(
                    isGradient: widget.bottomBarGradient,
                    items: widget.bottomBarItemsCustom ?? [],
                  )
                  : Container();
            },
          );
        },
      ),
      floatingActionButton: widget.floatActionButton,
    );
  }

  Widget _buildAnimatedAppBar() {
    final double appBarHeight =
        kToolbarHeight + MediaQuery.of(context).padding.top;

    return ValueListenableBuilder<bool>(
      valueListenable: _isAppBarVisible,
      builder: (context, isVisible, child) {
        return AnimatedContainer(
          duration: const Duration(milliseconds: 200),
          curve: Curves.easeInOut,
          height: isVisible ? appBarHeight : 0,
          child:
              _isAppBarVisible.value
                  ? buildAppBar(
                    centerTitle: widget.centerTitle,
                    title: widget.title,
                    leading: widget.appBarLeading,
                    action: widget.appBarActions,
                    textStyle: widget.appBarTextStyle,
                    isGradient: widget.appBarGradient,
                  )
                  : null,
        );
      },
    );
  }

  Widget buildAppBar({
    required bool centerTitle,
    required Widget title,
    Widget? leading,
    List<Widget>? action,
    TextStyle? textStyle,
    required bool isGradient,
  }) {
    return AppBar(
      // backgroundColor: toHexToColor(backgroundColor),
      backgroundColor: Colors.transparent,
      title: title,
      centerTitle: centerTitle,
      leading: leading,
      actions: action,
      titleTextStyle: textStyle,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(
          bottom: Radius.circular(30), // Bo tròn hai cạnh dưới
        ),
      ),
      flexibleSpace:
          isGradient
              ? Container(
                decoration: BoxDecoration(
                  gradient: LinearGradient(
                    colors: [
                      toHexToColor(appBarColor),
                      Colors.blueGrey,
                      // toHexToColor(primaryButtonColor),
                    ],
                    begin: Alignment.topLeft,
                    end: Alignment.bottomRight,
                  ),
                  borderRadius: const BorderRadius.vertical(
                    bottom: Radius.circular(30), // Đồng bộ với shape
                  ),
                ),
              )
              : Container(
                decoration: BoxDecoration(
                  color: toHexToColor(appBarColor),
                  borderRadius: const BorderRadius.vertical(
                    bottom: Radius.circular(30), // Đồng bộ với shape
                  ),
                ),
              ),
    );
  }

  Widget buildCustomBottomBarAnimation({
    required List<BottomBarItem> items,
    required bool isGradient,
  }) {
    return AnimatedContainer(
      duration: const Duration(milliseconds: 100),
      height:
          _isBottomBarVisible.value == true ? kBottomNavigationBarHeight : 0,
      curve: Curves.easeInOut,
      child:
          _isBottomBarVisible.value && items.isNotEmpty
              ? ValueListenableBuilder<int>(
                valueListenable:
                    _selectIndex, // Lắng nghe sự thay đổi của _selectIndex
                builder: (context, selectedIndex, _) {
                  return CurvedNavigationBar(
                    color: toHexToColor(appBarColor),
                    backgroundColor: Colors.transparent,
                    index:
                        selectedIndex, // Cập nhật index khi _selectIndex thay đổi
                    animationDuration: const Duration(milliseconds: 100),
                    items:
                        items
                            .asMap()
                            .entries
                            .map(
                              (entry) => CurvedNavigationBarItem(
                                child: entry.value.icon,
                                label: entry.value.label,
                                labelStyle: const TextStyle(
                                  color: Colors.white,
                                  overflow: TextOverflow.ellipsis,
                                  fontSize: 12,
                                ),
                              ),
                            )
                            .toList(),
                    onTap: (index) {
                      _onBottomItemTapped(index);
                    },
                  );
                },
              )
              : null,
    );
  }
}

class BottomBarItem {
  final Widget icon;
  final String label;
  final Function(int index) onTap;

  BottomBarItem({required this.icon, required this.label, required this.onTap});
}
