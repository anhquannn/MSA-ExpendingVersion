import 'dart:async';
import 'package:curved_labeled_navigation_bar/curved_navigation_bar.dart';
import 'package:curved_labeled_navigation_bar/curved_navigation_bar_item.dart';
import 'package:flutter/material.dart';
import '../core/config/constant.dart';
import '../core/utils/prarse_color.dart';

class CustomScaffold extends StatefulWidget {
  final Widget? title;
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
  final bool? isHide;

  const CustomScaffold({
    super.key,
    this.isHide = false,
    this.centerTitle = false,
    this.title,
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
  final ValueNotifier<int> _selectIndex = ValueNotifier(0);
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
    final double topPadding = MediaQuery.of(context).padding.top;

    return Scaffold(
      extendBodyBehindAppBar: widget.isHide ?? false,
      extendBody: true,
      backgroundColor: Colors.white,
      body: Container(
        // decoration: BoxDecoration(color: toHexToColor(backgroundColor)),
        // decoration: BoxDecoration(color: Colors.green.withOpacity(0.1)),
        decoration: BoxDecoration(color: Colors.white),
        child: Stack(
          children: [
            // Main body content (dưới AppBar)
            ValueListenableBuilder<bool>(
              valueListenable: _isAppBarVisible,
              builder: (context, isVisible, _) {
                final double topPaddingValue =
                    (isVisible && !(widget.isHide ?? false))
                        ? kToolbarHeight + topPadding
                        : 0.0;
                return Padding(
                  padding: EdgeInsets.only(top: topPaddingValue),
                  child: Column(
                    children: [
                      if (widget.tabBar != null) widget.tabBar!,
                      Expanded(child: widget.bodyBuilder(_scrollController)),
                    ],
                  ),
                );
              },
            ),
            // AppBar (luôn ở trên cùng)
            Positioned(
              top: 0,
              left: 0,
              right: 0,
              child: _buildAnimatedAppBar(isHide: widget.isHide ?? false),
            ),
          ],
        ),
      ),
      bottomNavigationBar:
          (widget.bottomBarItems != null && widget.bottomBarItems!.isNotEmpty)
              ? ValueListenableBuilder<bool>(
                valueListenable: _isBottomBarVisible,
                builder: (context, isVisible, _) {
                  return ValueListenableBuilder<int>(
                    valueListenable: _selectIndex,
                    builder: (context, selectedIndex, _) {
                      return isVisible
                          ? customBottomBar(
                            onTap: (index) {
                              _selectIndex.value = index;
                              widget.bottomBarItems![index].onTap(index);
                            },
                            items: widget.bottomBarItems!,
                          )
                          : const SizedBox.shrink();
                    },
                  );
                },
              )
              : ValueListenableBuilder<bool>(
                valueListenable: _isBottomBarVisible,
                builder: (context, isVisible, _) {
                  return ValueListenableBuilder<int>(
                    valueListenable: _selectIndex,
                    builder: (context, selectedIndex, _) {
                      return isVisible
                          ? buildCustomBottomBarAnimation(
                            isGradient: widget.bottomBarGradient,
                            items: widget.bottomBarItemsCustom ?? [],
                          )
                          : const SizedBox.shrink();
                    },
                  );
                },
              ),
      floatingActionButton: widget.floatActionButton,
    );
  }

  Widget customBottomBar({
    required ValueChanged<int> onTap,
    required List<BottomBarItem> items,
    Color backgroundColor = Colors.white,
  }) {
    return Container(
      height: 50,
      decoration: BoxDecoration(
        color: toHexToColor(appBarColor),
        boxShadow: [BoxShadow(color: Colors.black26, blurRadius: 4)],
      ),
      child: Row(
        children: List.generate(items.length, (index) {
          final item = items[index];
          return Expanded(
            child: GestureDetector(
              onTap: () {
                onTap(index);
              },
              child: Container(
                alignment: Alignment.center,
                child: Text(
                  item.label,
                  textAlign: TextAlign.center,
                  style: TextStyle(fontSize: 14, color: Colors.white),
                ),
              ),
            ),
          );
        }),
      ),
    );
  }

  Widget _buildAnimatedAppBar({required bool isHide}) {
    final double appBarHeight =
        isHide ? 0 : kToolbarHeight + MediaQuery.of(context).padding.top;

    return ValueListenableBuilder<bool>(
      valueListenable: _isAppBarVisible,
      builder: (context, isVisible, child) {
        return AnimatedSwitcher(
          duration: const Duration(milliseconds: 200),
          switchInCurve: Curves.easeInOut,
          switchOutCurve: Curves.easeInOut,
          child:
              (isVisible && !isHide)
                  ? SizedBox(
                    height: appBarHeight,
                    child: buildAppBar(
                      isHide: isHide,
                      centerTitle: widget.centerTitle,
                      title: widget.title ?? const SizedBox(width: 200),
                      leading: widget.appBarLeading,
                      action: widget.appBarActions,
                      textStyle: widget.appBarTextStyle,
                      isGradient: widget.appBarGradient,
                    ),
                  )
                  : const SizedBox.shrink(),
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
    required bool isHide,
  }) {
    return PreferredSize(
      preferredSize:
          isHide == false ? const Size.fromHeight(kToolbarHeight) : Size.zero,
      child: AppBar(
        backgroundColor:
            isHide ? Colors.transparent : toHexToColor(appBarColor),
        elevation: 0,
        centerTitle: centerTitle,
        leading: leading,
        actions: action,
        title: isHide ? Container() : title,
        titleTextStyle: isHide ? null : textStyle,
        toolbarHeight: kToolbarHeight,
        flexibleSpace:
            isHide
                ? const SizedBox.shrink()
                : Container(
                  decoration: BoxDecoration(
                    gradient:
                        isGradient
                            ? LinearGradient(
                              colors: [
                                toHexToColor(appBarColor),
                                Colors.blueGrey,
                              ],
                              begin: Alignment.topLeft,
                              end: Alignment.bottomRight,
                            )
                            : null,
                    color: isGradient ? null : toHexToColor(appBarColor),
                    borderRadius: const BorderRadius.vertical(
                      bottom: Radius.circular(30),
                    ),
                  ),
                ),
        shape:
            isHide
                ? null
                : const RoundedRectangleBorder(
                  borderRadius: BorderRadius.vertical(
                    bottom: Radius.circular(30),
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
                valueListenable: _selectIndex,
                builder: (context, selectedIndex, _) {
                  return CurvedNavigationBar(
                    color: toHexToColor(appBarColor),
                    backgroundColor: Colors.transparent,
                    index: selectedIndex,
                    animationDuration: const Duration(milliseconds: 100),
                    items:
                        items
                            .asMap()
                            .entries
                            .map(
                              (entry) => CurvedNavigationBarItem(
                                child: entry.value.icon!,
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
  final Widget? icon;
  final String label;
  final Function(int index) onTap;
  final bool isSelected;
  final GlobalKey? key;

  BottomBarItem({
    this.icon,
    required this.label,
    required this.onTap,
    this.isSelected = false,
    this.key,
  });
}
