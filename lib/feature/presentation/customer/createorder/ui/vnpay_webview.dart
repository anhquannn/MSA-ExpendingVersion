import 'package:flutter/material.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';
import 'package:webview_flutter/webview_flutter.dart';

class VnPayWebViewScreen extends StatefulWidget {
  final String paymentUrl;
  final void Function(bool success) onPaymentResult;

  const VnPayWebViewScreen({
    Key? key,
    required this.paymentUrl,
    required this.onPaymentResult,
  }) : super(key: key);

  @override
  State<VnPayWebViewScreen> createState() => _VnPayWebViewScreenState();
}

class _VnPayWebViewScreenState extends State<VnPayWebViewScreen> {
  late final WebViewController _controller;

  @override
  void initState() {
    super.initState();

    _controller =
        WebViewController()
          ..setJavaScriptMode(JavaScriptMode.unrestricted)
          ..setNavigationDelegate(
            NavigationDelegate(
              onNavigationRequest: (request) {
                final url = request.url;

                if (url.contains('/vnpay/callback')) {
                  // ✅ Phân tích callback để xác định thành công/thất bại
                  final success = url.contains(
                    'vnp_ResponseCode=00',
                  ); // 00 là thành công

                  widget.onPaymentResult(success);
                  Navigator.of(context).pop();

                  return NavigationDecision.prevent;
                }

                return NavigationDecision.navigate;
              },
            ),
          )
          ..loadRequest(Uri.parse(widget.paymentUrl));
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      isHide: false,
      centerTitle: true,
      title: Text('Thanh toán VNPAY', style: TextStyle(color: Colors.white)),
      appBarLeading: iconBack(context, color: Colors.white),
      bodyBuilder: (controller) {
        return WebViewWidget(controller: _controller);
      },
    );
  }
}
