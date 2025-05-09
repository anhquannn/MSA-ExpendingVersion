import 'package:flutter/material.dart';

/// Hàm show loading dialog
Future<void> showLoadingDialog({
  required BuildContext context,
  required Future<void> Function() action,
}) async {
  final navigator = Navigator.of(context, rootNavigator: true);

  showDialog(
    context: context,
    barrierDismissible: false,
    barrierColor: Colors.black45,
    builder: (_) => const _FullScreenLoadingDialog(),
  );

  try {
    debugPrint("[LoadingDialog] Action started");
    await action();
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
