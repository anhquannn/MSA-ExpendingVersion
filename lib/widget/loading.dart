import 'package:flutter/material.dart';

/// Hàm show loading dialog với action
Future<void> showLoadingDialog({
  required BuildContext context,
  Future<void>? Function()? action,
}) async {
  final navigator = Navigator.of(context, rootNavigator: true);

  await showDialog(
    context: context,
    barrierDismissible: false,
    barrierColor: Colors.black45,
    builder: (_) => const _FullScreenLoadingDialog(),
  );

  try {
    debugPrint("[LoadingDialog] Action started");
    await action!();
    debugPrint("[LoadingDialog] Action completed");
  } catch (e, stacktrace) {
    debugPrint("[LoadingDialog] Error: $e");
    debugPrint(stacktrace.toString());
  } finally {
    if (navigator.canPop()) {
      debugPrint("[LoadingDialog] Closing dialog");
      navigator.pop();
    } else {
      debugPrint("[LoadingDialog] Cannot pop");
    }
  }
}

/// Widget loading toàn màn hình
class _FullScreenLoadingDialog extends StatelessWidget {
  const _FullScreenLoadingDialog();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black45, // Nền tối mờ
      body: Center(
        child: TweenAnimationBuilder<double>(
          tween: Tween(begin: 0.5, end: 1.0),
          duration: const Duration(seconds: 1),
          curve: Curves.easeInOut,
          builder: (context, value, child) {
            return Opacity(
              opacity: value,
              child: Transform.scale(
                scale: value,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const CircularProgressIndicator(
                      valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                    ),
                    const SizedBox(height: 20),
                    const Text(
                      'Đang tải...',
                      style: TextStyle(
                        fontSize: 18,
                        color: Colors.white,
                        fontWeight: FontWeight.bold,
                        fontStyle: FontStyle.italic,
                      ),
                    ),
                  ],
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}

/// Hiển thị dialog loading đơn giản không có action
Future<void> showLoading({
  required BuildContext context,
}) async {
  showDialog(
    context: context,
    barrierDismissible: false,
    barrierColor: Colors.black45,
    builder: (_) => const _FullScreenLoading(),
  );
}

/// Đóng dialog loading
void hideLoading(BuildContext context) {
  final navigator = Navigator.of(context, rootNavigator: true);
  if (navigator.canPop()) {
    navigator.pop();
  }
}

/// Lớp mới: Loading Controller - Quản lý loading dialog
class LoadingController {
  bool _isLoading = false;
  BuildContext? _context;

  /// Khởi tạo controller với context
  LoadingController({BuildContext? context}) {
    _context = context;
  }

  /// Cập nhật context
  void updateContext(BuildContext context) {
    _context = context;
  }

  /// Kiểm tra xem loading có đang hiển thị không
  bool get isLoading => _isLoading;

  /// Hiển thị loading dialog
  void show({BuildContext? context}) {
    if (_isLoading) return;
    
    final ctx = context ?? _context;
    if (ctx == null) {
      debugPrint("[LoadingController] Context is null, cannot show loading");
      return;
    }
    
    _isLoading = true;
    showDialog(
      context: ctx,
      barrierDismissible: false,
      barrierColor: Colors.black45,
      builder: (_) => const _FullScreenLoading(),
    );
    
    debugPrint("[LoadingController] Loading shown");
  }

  /// Đóng loading dialog
  void hide({BuildContext? context}) {
    if (!_isLoading) return;
    
    final ctx = context ?? _context;
    if (ctx == null) {
      debugPrint("[LoadingController] Context is null, cannot hide loading");
      return;
    }
    
    final navigator = Navigator.of(ctx, rootNavigator: true);
    if (navigator.canPop()) {
      navigator.pop();
      _isLoading = false;
      debugPrint("[LoadingController] Loading hidden");
    } else {
      debugPrint("[LoadingController] Cannot pop, no dialog to hide");
    }
  }

  /// Thực hiện hành động có loading
  Future<void> runWithLoading(
    Future<void> Function() action, {
    BuildContext? context,
    Function(dynamic error)? onError,
  }) async {
    final ctx = context ?? _context;
    if (ctx == null) {
      debugPrint("[LoadingController] Context is null, cannot run with loading");
      return;
    }

    show(context: ctx);
    
    try {
      await action();
    } catch (e, stacktrace) {
      debugPrint("[LoadingController] Error: $e");
      debugPrint(stacktrace.toString());
      if (onError != null) {
        onError(e);
      }
    } finally {
      hide(context: ctx);
    }
  }
}

/// Widget loading toàn màn hình đơn giản
class _FullScreenLoading extends StatelessWidget {
  const _FullScreenLoading();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black45,
      body: Center(
        child: TweenAnimationBuilder<double>(
          tween: Tween(begin: 0.5, end: 1.0),
          duration: const Duration(milliseconds: 600),
          curve: Curves.easeInOut,
          builder: (context, value, child) {
            return Opacity(
              opacity: value,
              child: Transform.scale(
                scale: value,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    const CircularProgressIndicator(
                      valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                      strokeWidth: 4,
                    ),
                    const SizedBox(height: 16),
                    const Text(
                      'Vui lòng chờ...',
                      style: TextStyle(
                        fontSize: 16,
                        color: Colors.white,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ],
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}

/// Tùy chỉnh loading dialog với nội dung linh hoạt
class CustomLoadingDialog extends StatelessWidget {
  final Widget? loadingWidget;
  final String? text;
  final TextStyle? textStyle;
  final Color backgroundColor;
  final double opacity;

  const CustomLoadingDialog({
    super.key,
    this.loadingWidget,
    this.text,
    this.textStyle,
    this.backgroundColor = Colors.black45,
    this.opacity = 0.7,
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: backgroundColor.withOpacity(opacity),
      body: Center(
        child: TweenAnimationBuilder<double>(
          tween: Tween(begin: 0.5, end: 1.0),
          duration: const Duration(milliseconds: 500),
          curve: Curves.easeInOut,
          builder: (context, value, child) {
            return Opacity(
              opacity: value,
              child: Transform.scale(
                scale: value,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    loadingWidget ?? 
                      const CircularProgressIndicator(
                        valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
                        strokeWidth: 4,
                      ),
                    if (text != null) const SizedBox(height: 16),
                    if (text != null)
                      Text(
                        text!,
                        style: textStyle ?? 
                          const TextStyle(
                            fontSize: 16,
                            color: Colors.white,
                            fontWeight: FontWeight.w600,
                          ),
                      ),
                  ],
                ),
              ),
            );
          },
        ),
      ),
    );
  }

  /// Hiển thị custom loading dialog
  static Future<void> show({
    required BuildContext context,
    Widget? loadingWidget,
    String? text,
    TextStyle? textStyle,
    Color backgroundColor = Colors.black45,
    double opacity = 0.7,
    bool barrierDismissible = false,
  }) async {
    return showDialog(
      context: context,
      barrierDismissible: barrierDismissible,
      barrierColor: Colors.transparent,
      builder: (_) => CustomLoadingDialog(
        loadingWidget: loadingWidget,
        text: text,
        textStyle: textStyle,
        backgroundColor: backgroundColor,
        opacity: opacity,
      ),
    );
  }

  /// Đóng custom loading dialog
  static void hide(BuildContext context) {
    final navigator = Navigator.of(context, rootNavigator: true);
    if (navigator.canPop()) {
      navigator.pop();
    }
  }
}