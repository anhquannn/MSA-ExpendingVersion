import 'package:flutter/material.dart';
import 'package:lottie/lottie.dart';

class FullScreenLoading extends StatelessWidget {
  const FullScreenLoading({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.black.withOpacity(0.4),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Hiển thị animation Lottie
            SizedBox(
              height: 150,
              child: Lottie.asset('assets/animations/loading.json'),
            ),
            const SizedBox(height: 16),
            const Text(
              'Đang xử lý, vui lòng chờ...',
              style: TextStyle(color: Colors.white, fontSize: 16),
            ),
          ],
        ),
      ),
    );
  }
}

// Hiển thị loading
showFullScreenLoading(BuildContext context) {
  showDialog(
    context: context,
    barrierDismissible: false,
    builder: (_) => const FullScreenLoading(),
  );
}

// Ẩn loading
hideFullScreenLoading(BuildContext context) {
  Navigator.of(context, rootNavigator: true).pop();
}
