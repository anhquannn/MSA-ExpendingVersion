import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

import '../../../../core/config/config.dart';
import '../../../../core/config/constant.dart';
import '../../../../core/utils/prarse_color.dart';
import '../login/ui/login_screen.dart';

class OnboardingScreen extends StatefulWidget {
  @override
  _OnboardingScreenState createState() => _OnboardingScreenState();
}

class _OnboardingScreenState extends State<OnboardingScreen> {
  int currentPage = 0;

  final PageController _controller = PageController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Stack(
        children: [
          Expanded(
            child: PageView(
              controller: _controller,
              onPageChanged: (index) {
                setState(() => currentPage = index);
              },
              children: [
                _buildWelcome1(
                  welcome1,
                  'Hàng ngàn sản phẩm thiết yếu',
                  'Từ rau củ tươi, thịt cá sạch, đến đồ dùng hàng ngày – tất cả đều có tại cửa hàng của bạn.',
                ),
                _buildWelcome2(
                  welcome2,
                  'Mua sắm tại nhà – Giao hàng tận cửa',
                  'Nhanh chóng, an toàn, tiện lợi – chọn món bạn cần, chúng tôi lo phần còn lại.',
                ),
                _buildWelcome2(
                  onLogin: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (context) => LoginScreen()),
                    );
                  },
                  onRegister: () {},
                  isEnd: true,
                  welcome3,
                  'Nhiều ưu đãi hấp dẫn mỗi ngày',
                  'Tích điểm, mã giảm giá và khuyến mãi dành riêng cho bạn.',
                ),
              ],
            ),
          ),

          Positioned(
            bottom: AppSize.h(0.4),
            left: 0,
            right: 0,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: List.generate(3, (index) {
                return Container(
                  margin: const EdgeInsets.symmetric(horizontal: 5),
                  width: 8,
                  height: 8,
                  decoration: BoxDecoration(
                    color:
                        currentPage == index
                            ? toHexToColor(appBarColor)
                            : Colors.grey,
                    shape: BoxShape.circle,
                  ),
                );
              }),
            ),
          ),
        ],
      ),
      floatingActionButton:
          currentPage < 2
              ? FloatingActionButton(
                backgroundColor: toHexToColor(primaryButtonColor),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(40),
                ),
                onPressed: () {
                  if (currentPage < 2) {
                    _controller.nextPage(
                      duration: Duration(milliseconds: 300),
                      curve: Curves.easeIn,
                    );
                  } else {
                    Navigator.push(
                      context,
                      MaterialPageRoute(builder: (context) => LoginScreen()),
                    );
                  }
                },
                child: Icon(Icons.arrow_forward, color: Colors.white),
              )
              : Container(),
    );
  }

  Widget _buildWelcome1(String img, String title, String content) {
    return Container(
      height: AppSize.h(1),
      color: toHexToColor(backgroundColor),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          Align(
            alignment: Alignment.topCenter,
            child: Transform.rotate(
              angle: 0, // Xoay 180 độ
              child: Image.asset(img),
            ),
          ),
          SizedBox(height: 30),
          Center(
            child: AutoSizeText(
              title,
              style: TextStyle(
                color: Colors.black,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),

          Positioned(
            height: AppSize.h(0.3),
            child: Padding(
              padding: AppSize.paddingVerticalMini(context),
              child: Center(
                child: Container(
                  width: AppSize.w(0.6),
                  child: AutoSizeText(
                    content,
                    style: TextStyle(color: Colors.black),
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildWelcome2(
    String img,
    String title,
    String content, {
    bool? isEnd = false,
    VoidCallback? onRegister,
    VoidCallback? onLogin,
  }) {
    return Container(
      height: AppSize.h(1),
      color: toHexToColor(backgroundColor),
      child: Stack(
        alignment: Alignment.center,
        children: [
          // Ảnh cách đáy màn hình 40%
          Positioned(
            bottom: AppSize.h(0.4),
            left: 0,
            right: 0,
            child: Image.asset(img, width: AppSize.w(0.8), fit: BoxFit.contain),
          ),

          // Tiêu đề + nội dung
          Positioned(
            top: AppSize.h(0.65),
            left: 0,
            right: 0,
            child: Column(
              children: [
                AutoSizeText(
                  title,
                  style: const TextStyle(
                    color: Colors.black,
                    fontWeight: FontWeight.bold,
                  ),
                  textAlign: TextAlign.center,
                ),
                Padding(
                  padding: AppSize.paddingVerticalMini(context),
                  child: Container(
                    width: AppSize.w(0.6),
                    child: AutoSizeText(
                      content,
                      style: const TextStyle(color: Colors.black),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ),
              ],
            ),
          ),

          isEnd == true
              ? Positioned(
                bottom: AppSize.h(0.05),
                left: 0,
                right: 0,
                child: Align(
                  alignment: Alignment.center,
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      // Nút Đăng ký
                      Padding(
                        padding: const EdgeInsets.all(10),
                        child: SizedBox(
                          width: AppSize.w(0.4),
                          height: 40,
                          child: InkWell(
                            onTap: onRegister,
                            child: Container(
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(10),
                                color: toHexToColor(backgroundColor),
                                border: Border.all(
                                  color: toHexToColor(appBarColor),
                                ),
                              ),
                              // padding: const EdgeInsets.symmetric(
                              //   horizontal: 20,
                              //   vertical: 10,
                              // ),
                              child: Center(
                                child: Text(
                                  'Đăng ký',
                                  style: TextStyle(
                                    color: toHexToColor(primaryButtonColor),
                                  ),
                                ),
                              ),
                            ),
                          ),
                        ),
                      ),

                      Padding(
                        padding: const EdgeInsets.all(10),
                        child: SizedBox(
                          width: AppSize.w(0.4),
                          height: 40,
                          child: InkWell(
                            onTap: onLogin, // ← sửa lại để gọi hàm đúng
                            child: Container(
                              decoration: BoxDecoration(
                                borderRadius: BorderRadius.circular(10),
                                color: toHexToColor(primaryButtonColor),
                              ),
                              // padding: const EdgeInsets.symmetric(
                              //   horizontal: 20,
                              //   vertical: 10,
                              // ),
                              child: Center(
                                child: Text(
                                  'Đăng nhập',
                                  style: TextStyle(color: Colors.white),
                                ),
                              ),
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              )
              : Container(),
        ],
      ),
    );
  }

  Widget buildPage(String img, String title, String desc) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.start,
      children: [
        SizedBox(height: 30),
        Image.asset(img),
        SizedBox(height: 20),
        Text(title, style: TextStyle(fontWeight: FontWeight.bold)),
        Padding(padding: const EdgeInsets.all(16.0), child: Text(desc)),
      ],
    );
  }
}
