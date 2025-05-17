import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:msa/core/config/base_bloc.dart';
import '../../../../../core/config/config.dart';
import '../../../../../core/config/constant.dart';
import '../../../../../core/utils/prarse_color.dart';
import '../bloc/verify_otp_bloc.dart';

class VerifyOtpScreen extends BaseView<VerifyOtpBloc> {
  const VerifyOtpScreen({super.key});

  @override
  VerifyOtpBloc createState() => VerifyOtpBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as VerifyOtpBloc;
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        leading: InkWell(onTap: () {
        context.pop();
      },
      child: Icon(Icons.arrow_back_ios_new,color: Colors.white,),
      ),),
      backgroundColor: toHexToColor(actionColor),
      body: Center(
        child: SingleChildScrollView(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              SizedBox(height: 35),
              Center(
                child: Container(
                  decoration: BoxDecoration(
                    border: Border.all(
                      color: toHexToColor(borderColor),
                      width: 10,
                    ),
                    color: Colors.white,
                    borderRadius: BorderRadius.circular(200),
                  ),

                  width: AppSize.w(0.8),
                  height: AppSize.w(0.8),
                  child: ClipRRect(
                    borderRadius: BorderRadius.circular(AppSize.w(0.85) / 2),
                    child: Image.asset(imgOtp, fit: BoxFit.cover),
                  ),
                ),
              ),
              Center(
                child: AutoSizeText(
                  minFontSize: 18,
                  maxLines: 24,
                  'Xác thực OTP',
                  style: TextStyle(color: Colors.white),
                ),
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: List.generate(6, (index) {
                  return Padding(
                    padding: const EdgeInsets.all(5.0),
                    child: Container(
                      decoration: BoxDecoration(
                        color: toHexToColor(borderColor),
                        borderRadius: BorderRadius.circular(5),
                        border: Border.all(
                          color:
                              bloc.isValid == true
                                  ? toHexToColor(titleDialogErrorColor)
                                  : toHexToColor(borderColor),
                        ),
                      ),
                      width: AppSize.w(0.12),
                      height: 55,
                      child: Center(
                        child: TextField(
                          showCursor: false,
                          style: TextStyle(
                            color: toHexToColor(primaryTextColor),
                            fontWeight: FontWeight.bold,
                          ),
                          controller: bloc.controllers[index],
                          focusNode: bloc.focusNodes[index],
                          keyboardType: TextInputType.none,
                          maxLength: 1,
                          textAlign: TextAlign.center,
                          decoration: InputDecoration(
                            counterText: '',
                            border: OutlineInputBorder(
                              borderSide: BorderSide.none,
                              borderRadius: BorderRadius.circular(5),
                            ),
                          ),
                          onTap: () {
                            bloc.onTapTextField(index);
                          },
                          onChanged: (value) {
                            if (value.length == 1 && index < 5) {
                              FocusScope.of(context).nextFocus();
                            } else if (value.isEmpty && index > 0) {
                              FocusScope.of(context).previousFocus();
                            }
                          },
                        ),
                      ),
                    ),
                  );
                }),
              ),
              SizedBox(height: 10),
              bloc.isValid == true
                  ? Center(
                    child: Text(
                      'Mã OTP phải đủ 6 chữ số',
                      style: TextStyle(
                        color: toHexToColor(primaryErrorColor),
                        fontSize: 12,
                      ),
                    ),
                  )
                  : Container(),
              SizedBox(height: 20),

              if (bloc.isKeyboardVisible) ...[
                Container(
                  width: AppSize.w(0.85),
                  // height: 300,
                  // height: 300,
                  decoration: BoxDecoration(
                    color: Colors.grey.shade50,
                    borderRadius: BorderRadius.circular(10),
                    boxShadow: [
                      // Bóng ở phía trên
                      BoxShadow(
                        color: Colors.black.withOpacity(
                          0.1,
                        ), // Độ mờ nhẹ, màu đen
                        offset: Offset(0, -2), // Bóng đổ lên trên
                        blurRadius: 5,
                        spreadRadius: 1,
                      ),
                      // Bóng ở phía dưới
                      BoxShadow(
                        color: Colors.black.withOpacity(
                          0.1,
                        ), // Độ mờ nhẹ, màu đen
                        offset: Offset(0, 2), // Bóng đổ xuống dưới
                        blurRadius: 5,
                        spreadRadius: 1,
                      ),
                      // Bóng ở phía trái
                      BoxShadow(
                        color: Colors.black.withOpacity(
                          0.1,
                        ), // Độ mờ nhẹ, màu đen
                        offset: Offset(-2, 0), // Bóng đổ sang trái
                        blurRadius: 5,
                        spreadRadius: 1,
                      ),
                      // Bóng ở phía phải
                      BoxShadow(
                        color: Colors.black.withOpacity(
                          0.1,
                        ), // Độ mờ nhẹ, màu đen
                        offset: Offset(2, 0), // Bóng đổ sang phải
                        blurRadius: 1,
                        spreadRadius: 1,
                      ),
                    ],
                  ),
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: List.generate(3, (index) {
                          return Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: InkWell(
                              onTap: () {
                                bloc.addToOtp((index + 1).toString());
                              },
                              child: Container(
                                width: AppSize.w(0.22),
                                height: AppSize.w(0.15),
                                decoration: BoxDecoration(
                                  color: toHexToColor(borderColor),
                                  borderRadius: BorderRadius.circular(5),
                                ),
                                child: Center(
                                  child: Text(
                                    (index + 1).toString(),
                                    style: TextStyle(
                                      color: toHexToColor(primaryTextColor),

                                      fontWeight: FontWeight.bold,
                                    ),
                                  ),
                                ),
                              ),
                            ),
                          );
                        }),
                      ),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: List.generate(3, (index) {
                          return Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: InkWell(
                              onTap: () {
                                bloc.addToOtp((index + 4).toString());
                              },
                              child: Container(
                                width: AppSize.w(0.22),
                                height: AppSize.w(0.15),
                                decoration: BoxDecoration(
                                  color: toHexToColor(borderColor),
                                  borderRadius: BorderRadius.circular(5),
                                ),
                                child: Center(
                                  child: Text(
                                    (index + 4).toString(),
                                    style: TextStyle(
                                      color: toHexToColor(primaryTextColor),
                                      fontWeight: FontWeight.bold,
                                    ),
                                  ),
                                ),
                              ),
                            ),
                          );
                        }),
                      ),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: List.generate(3, (index) {
                          return Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: InkWell(
                              onTap: () {
                                bloc.addToOtp((index + 7).toString());
                              },
                              child: Container(
                                width: AppSize.w(0.22),
                                height: AppSize.w(0.15),
                                decoration: BoxDecoration(
                                  color: toHexToColor(borderColor),
                                  borderRadius: BorderRadius.circular(5),
                                ),
                                child: Center(
                                  child: Text(
                                    (index + 7).toString(),
                                    style: TextStyle(
                                      color: toHexToColor(primaryTextColor),
                                      fontWeight: FontWeight.bold,
                                    ),
                                  ),
                                ),
                              ),
                            ),
                          );
                        }),
                      ),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: InkWell(
                              onTap: () {
                                bloc.onDelete();
                              },
                              child: Container(
                                width: AppSize.w(0.22),
                                height: AppSize.w(0.15),
                                decoration: BoxDecoration(
                                  color: toHexToColor(borderColor),
                                  borderRadius: BorderRadius.circular(5),
                                ),
                                child: Center(
                                  child: Icon(
                                    Icons.backspace,
                                    color: toHexToColor(primaryTextColor),
                                    size: 24,
                                  ),
                                ),
                              ),
                            ),
                          ),

                          Padding(
                            padding: const EdgeInsets.all(8.0),
                            child: InkWell(
                              onTap: () {
                                bloc.addToOtp('0');
                              },
                              child: Container(
                                width: AppSize.w(0.22),
                                height: AppSize.w(0.15),
                                decoration: BoxDecoration(
                                  color: toHexToColor(borderColor),
                                  borderRadius: BorderRadius.circular(5),
                                ),
                                child: Center(
                                  child: Text(
                                    '0',
                                    style: TextStyle(
                                      color: toHexToColor(primaryTextColor),
                                      fontWeight: FontWeight.bold,
                                    ),
                                  ),
                                ),
                              ),
                            ),
                          ),

                          Builder(builder: (ctx) {
                            return Padding(
                              padding: const EdgeInsets.all(8.0),
                              child: InkWell(
                                onTap: () async{
                                  await bloc.onOtpSubmit(ctx);
//                                   bool isSuccess=false;
//                                   bloc.onHide();
//                                   await showLoadingDialog(context: context,action: () async {
//                                     isSuccess=await bloc.onOtpSubmit();
//                                   },);
// //minhquang03082003@gmail.com
//                                   if(isSuccess){
//                                     Navigator.pop(ctx);
//                                     context.go('/login');
//                                   }else{
//                                     showDialog(
//                                       context: ctx,
//                                       builder: (_) => AlertDialog(
//                                         title: const Text('Đăng nhập thất bại'),
//                                         content: const Text('Email hoặc mật khẩu không đúng.'),
//                                         actions: [
//                                           TextButton(
//                                             onPressed: () => Navigator.of(ctx).pop(),
//                                             child: const Text('OK'),
//                                           ),
//                                         ],
//                                       ),
//                                     );
//                                   }
                                },
                                child: Container(
                                  width: AppSize.w(0.22),
                                  height: AppSize.w(0.15),
                                  decoration: BoxDecoration(
                                    color: toHexToColor(borderColor),
                                    borderRadius: BorderRadius.circular(100),
                                  ),
                                  child: Center(
                                    child: Icon(
                                      Icons.arrow_forward,
                                      color: toHexToColor(primaryTextColor),
                                      size: 24,
                                    ),
                                  ),
                                ),
                              ),
                            );
                          },),

                        ],
                      ),
                    ],
                  ),
                ),
                SizedBox(height: 20),
              ],
            ],
          ),
        ),
      ),
    );
  }

  @override
  VerifyOtpBloc createBloc() => VerifyOtpBloc();

  void _showCustomKeyboard(BuildContext context, VerifyOtpBloc bloc) {
    showModalBottomSheet(
      context: context,
      builder: (BuildContext context) {
        return Container(
          padding: const EdgeInsets.all(20.0),
          height: 300, // Đặt chiều cao của BottomSheet
          child: Column(
            children: [
              // Dòng đầu tiên của bàn phím (3 nút số)
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: List.generate(3, (index) {
                  return Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: InkWell(
                      onTap: () {
                        bloc.addToOtp((index + 4).toString());
                      },
                      child: Container(
                        width: AppSize.w(0.25),
                        height: 70,
                        decoration: BoxDecoration(
                          color: toHexToColor(borderColor),
                          borderRadius: BorderRadius.circular(5),
                          border: Border.all(color: toHexToColor(borderColor)),
                        ),
                        child: Center(
                          child: Text(
                            (index + 1).toString(),
                            style: TextStyle(
                              color: toHexToColor(appBarColor),
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                      ),
                    ),
                  );
                }),
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: List.generate(3, (index) {
                  return Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: InkWell(
                      onTap: () {
                        bloc.addToOtp((index + 4).toString());
                      },
                      child: Container(
                        width: AppSize.w(0.25),
                        height: 70,
                        decoration: BoxDecoration(
                          color: toHexToColor(borderColor),
                          borderRadius: BorderRadius.circular(5),
                          border: Border.all(color: toHexToColor(borderColor)),
                        ),
                        child: Center(
                          child: Text(
                            (index + 4).toString(),
                            style: TextStyle(
                              color: toHexToColor(appBarColor),
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                      ),
                    ),
                  );
                }),
              ),
              // Dòng thứ hai của bàn phím (xóa, nút 0 và ẩn bàn phím)
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: InkWell(
                      onTap: () {
                        bloc.onDelete(); // Xóa ký tự
                      },
                      child: Container(
                        width: AppSize.w(0.25),
                        height: 70,
                        decoration: BoxDecoration(
                          color: toHexToColor(borderColor),
                          borderRadius: BorderRadius.circular(5),
                          border: Border.all(color: toHexToColor(borderColor)),
                        ),
                        child: Center(
                          child: Icon(
                            Icons.backspace,
                            color: toHexToColor(appBarColor),
                            size: 24,
                          ),
                        ),
                      ),
                    ),
                  ),
                  Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: InkWell(
                      onTap: () {
                        bloc.addToOtp('0'); // Thêm số 0 vào OTP
                      },
                      child: Container(
                        width: AppSize.w(0.25),
                        height: 70,
                        decoration: BoxDecoration(
                          color: toHexToColor(borderColor),
                          borderRadius: BorderRadius.circular(5),
                          border: Border.all(color: toHexToColor(borderColor)),
                        ),
                        child: Center(
                          child: Text(
                            '0',
                            style: TextStyle(
                              color: toHexToColor(appBarColor),
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ),
                      ),
                    ),
                  ),
                  Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: InkWell(
                      onTap: () {
                        bloc.onHide(); // Ẩn bàn phím
                        Navigator.pop(context); // Đóng BottomSheet
                      },
                      child: Container(
                        width: AppSize.w(0.25),
                        height: 70,
                        decoration: BoxDecoration(
                          color: toHexToColor(borderColor),
                          borderRadius: BorderRadius.circular(100),
                          border: Border.all(color: toHexToColor(borderColor)),
                        ),
                        child: Center(
                          child: Icon(
                            Icons.navigate_next,
                            color: toHexToColor(appBarColor),
                            size: 24,
                          ),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ],
          ),
        );
      },
    );
  }
}
