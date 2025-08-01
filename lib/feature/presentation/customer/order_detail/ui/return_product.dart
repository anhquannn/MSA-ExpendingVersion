import 'dart:math';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import 'package:lottie/lottie.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/data/model/request/return_order_request.dart';
import 'package:msa/feature/presentation/customer/home_screen/ui/home_screen.dart';
import 'package:msa/widget/custom_textfield.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';
import 'package:rxdart/rxdart.dart';

import '../../../../../core/config/constant.dart';
import '../../../../../core/utils/prarse_color.dart';
import '../../../../../core/utils/upload_image.dart';
import '../../../../../core/utils/utility.dart';
import '../../../../../widget/custom_dropdown.dart';
import '../../../../../widget/custom_loading.dart';
import '../../../../../widget/custom_widget.dart';
import '../../../../data/model/response/order_detail_response_model.dart';
import '../../../../domain/entities/product_model.dart';
import '../../../../domain/repositories/repository.dart';
import '../../product_detail/ui/product_detail_screen.dart';

class ReturnProductScreen extends StatefulWidget {
  final int? orderId;
  const ReturnProductScreen({super.key, this.orderId});

  @override
  State<ReturnProductScreen> createState() => _ReturnProductScreenState();
}

class _ReturnProductScreenState extends State<ReturnProductScreen> {
  List<String> listImage = [];
  final streamImage = BehaviorSubject<List<String>>();

  List<OrderDetailResponse>? orderDetail;
  final streamOrderDetail = BehaviorSubject<List<OrderDetailResponse>>();

  List<ProductModel> listProduct = [];
  final streamListProduct = BehaviorSubject<List<ProductModel>>();

  final Map<int, List<String>> listImages = {};
  final Map<int, BehaviorSubject<List<String>>> imageStreams = {};

  final TextEditingController _reasonController = TextEditingController();

  ReturnOrderRequest? model;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      await onGetOrderDetail();
    });
  }

  onGetOrderDetail() async {
    final List<OrderDetailResponse> response =
        await Repository.onGetOrderDetail(orderId: widget.orderId);
    if (response != null) {
      orderDetail = response;
      streamOrderDetail.set(response);

      for (OrderDetailResponse i in response) {
        if (i.product != null) {
          listProduct.add(i.product ?? ProductModel());
        }
      }
      streamListProduct.set(listProduct);
    }
  }

  Future<bool> onCreateReturnOrder() async {
    List<ReturnOrderItem> items = [];

    for (int i = 0; i < (orderDetail?.length ?? 0); i++) {
      final detail = orderDetail![i];

      // Lấy danh sách ảnh theo index
      final imageUrls = listImages[i] ?? [];

      final List<ImageModel> imageModels =
          imageUrls.map((url) => ImageModel(imageUrl: url)).toList();
      if (detail.isSelectReturn == true) {
        final item = ReturnOrderItem(

          orderDetailId: detail.orderDetailId,
          quantity: detail.quantityReturn,
          reason: detail.returnReason?.text,
          images: imageModels,
        );

        items.add(item);
      }
    }

    final request = ReturnOrderRequest(
      
      orderId: widget.orderId,
      userId: Storage.userModelGlobal?.userId,
      reason: _reasonController.text,
      returnOrderItems: items,
    );

    print('REQUEST JSON: ${request.toJson()}');

    // Gọi API
    try {
      final result = await Repository.createReturnOrder(request);
      if (result != null) {
        return true;
      }
    } catch (e) {}
    return false;
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      appBarLeading: InkWell(
        onTap: () {
          Navigator.pop(context);
        },
        child: Icon(Icons.arrow_back_ios, color: Colors.white, size: 24),
      ),
      centerTitle: true,
      title: Text(
        'Trả hàng',
        style: TextStyle(
          color: Colors.white,
          fontWeight: FontWeight.bold,
          fontSize: 18,
        ),
      ),
      bodyBuilder: (controller) {
        return MediaQuery.removePadding(
          context: context,
          child: ListView(
            // padding: EdgeInsets.symmetric(horizontal: 8),
            children: [
              StreamBuilder(
                stream: streamOrderDetail.output,
                builder: (context, snapshot) {
                  if (snapshot.hasData) {
                    final orderDetail =
                        snapshot.data as List<OrderDetailResponse>;
                    return StreamBuilder(
                      stream: streamListProduct.output,
                      builder: (context, snapshot1) {
                        if (snapshot1.hasData) {
                          final List<ProductModel> productModel =
                              snapshot1.data as List<ProductModel>;
                          return ListView.builder(
                            shrinkWrap: true,
                            itemCount: 1,
                            physics: NeverScrollableScrollPhysics(),
                            itemBuilder: (context, index) {
                              return _listProduct(
                                productModel,
                                orderDetail,
                                context,
                              );
                            },
                          );
                        }
                        return Container();
                      },
                    );
                  }
                  return Container();
                },
              ),
              _buildTextReason(
                controller: _reasonController,
                isMainReason: true,
              ),
              _buildButton(() async {
                showFullScreenLoading(context);
                final result = await onCreateReturnOrder();

                hideFullScreenLoading(context);
                if (result == true) {
                  showCustomDialog(
                    context,
                    AppSize.width(),
                    AppSize.width(),
                    'Thành công',
                    const Text('Yêu cầu trả hàng đã được gửi.'),
                    true,
                    false,
                    const Icon(Icons.check_circle, color: Colors.green),
                    onClose: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (context) => HomeScreen()),
                      );
                    },
                  );
                } else {
                  showCustomDialog(
                    context,
                    AppSize.width(),
                    AppSize.width(),
                    'Thành công',
                    const Text('Lỗi hệ thống.'),
                    true,
                    false,
                    const Icon(Icons.warning, color: Colors.green),
                    onClose: () {
                      Navigator.pop(context);
                    },
                  );
                }
              }),
            ],
          ),
        );
      },
    );
  }

  Widget _listProduct(
    List<ProductModel> model,
    List<OrderDetailResponse> orderModel,
    BuildContext bContext,
  ) {
    return MediaQuery.removePadding(
      removeTop: true,
      removeBottom: true,
      context: bContext,
      child: ListView.builder(
        shrinkWrap: true,
        physics: const NeverScrollableScrollPhysics(),
        itemCount: min(model.length, orderModel.length),

        itemBuilder: (bContext, index) {
          final product = model[index];
          final order = orderModel[index];
          order.returnReason = TextEditingController();
          return InkWell(
            onTap: () {
              order.isSelectReturn = !(order.isSelectReturn ?? false);
              setState(() {});
            },
            child: Card(
              surfaceTintColor: Colors.grey,
              color: Colors.white,
              child: Container(
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(
                    color:
                        order.isSelectReturn == true
                            ? toHexToColor(primaryButtonColor)
                            : Colors.white,
                  ),
                ),
                child: Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: Column(
                    children: [
                      _itemCard(
                        product: product,
                        orderModel: order,
                        bContext: bContext,
                      ),
                      if (order.isSelectReturn == true) ...[
                        _buildListImage(index),
                        // _buildTextReason(controller: order.returnReason),
                        Padding(
                          padding: const EdgeInsets.all(8.0),
                          child: TextField(
                            controller: order.returnReason,
                            minLines: 1,
                            maxLines: 1,
                            decoration: InputDecoration(
                              filled: true,
                              fillColor: Colors.grey[200],
                              contentPadding: EdgeInsets.symmetric(
                                horizontal: 12,
                                vertical: 10,
                              ),
                              enabledBorder: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(8),
                                borderSide: BorderSide(color: Colors.white54),
                              ),
                              focusedBorder: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(8),
                                borderSide: BorderSide(
                                  color: Colors.white54,
                                  width: 1.5,
                                ),
                              ),
                              hintText: 'Nhập lý do trả hàng...',
                              hintStyle: TextStyle(color: Colors.grey),
                            ),
                            style: TextStyle(color: Colors.black),
                            cursorColor: Colors.white54,
                          ),
                        ),
                      ],
                    ],
                  ),
                ),
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _itemCard({
    ProductModel? product,
    OrderDetailResponse? orderModel,
    BuildContext? bContext,
  }) {
    return SizedBox(
      width: AppSize.width(),
      // height: 160,
      child: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 3, vertical: 2),
        child: SizedBox(
          // height: 100,
          child: Padding(
            padding: const EdgeInsets.all(8.0),
            child: Column(
              children: [
                Row(
                  children: [
                    Expanded(
                      flex: 4,
                      child: ClipRRect(
                        borderRadius: BorderRadius.circular(10),
                        child:
                            (product?.image != null)
                                ? CachedNetworkImage(
                                  imageUrl: product!.image!,
                                  placeholder:
                                      (context, url) =>
                                          CircularProgressIndicator(),
                                  errorWidget:
                                      (context, url, error) => Image.asset(
                                        imgBranch,
                                        fit: BoxFit.contain,
                                      ),
                                  width: 100,
                                  height: 100,
                                  fit: BoxFit.contain,
                                )
                                : Image.asset(imgBranch, fit: BoxFit.contain),
                      ),
                    ),
                    Expanded(
                      flex: 6,
                      child: Padding(
                        padding: const EdgeInsets.only(left: 10),
                        child: SizedBox(
                          height: 100,
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.start,
                            mainAxisSize: MainAxisSize.max,
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              customAutoSizeText(
                                14,
                                18,
                                product?.name ?? '',
                                isBold: true,
                                textColor: toHexToColor(primaryTextColor),
                              ),
                              customAutoSizeText(
                                12,
                                16,
                                formatCurrencyVN(orderModel?.totalPrice ?? 0.0),
                                isBold: true,
                                textColor: toHexToColor(primaryButtonColor),
                              ),
                              customAutoSizeText(
                                10,
                                12,
                                '${orderModel?.quantity} x ${formatCurrencyVN(product?.price ?? 0.0)}',
                                isBold: true,
                                // isLine: true,
                                textColor: toHexToColor(primaryTextColor),
                              ),
                              orderModel?.isSelectReturn == true
                                  ? _buildChangeNumber(
                                    orderModel?.quantityReturn ?? 0,
                                    () {
                                      if ((orderModel?.quantityReturn ?? 0) <
                                          (orderModel?.quantity ?? 0)) {
                                        orderModel?.quantityReturn =
                                            (orderModel.quantityReturn ?? 0) +
                                            1;
                                        setState(() {});
                                      }
                                    },
                                    () {
                                      if ((orderModel?.quantityReturn ?? 0) >
                                          0) {
                                        orderModel?.quantityReturn =
                                            (orderModel.quantityReturn ?? 0) -
                                            1;
                                        setState(() {});
                                      }
                                    },
                                  )
                                  : Container(),
                            ],
                          ),
                        ),
                      ),
                    ),
                  ],
                ),

                (orderModel?.freeItems != null &&
                        orderModel!.freeItems!.isNotEmpty)
                    ? Column(
                      children: [
                        _divider('Sản phẩm tặng kèm'),
                        Column(
                          children:
                              (orderModel.freeItems ?? [])
                                  .map((e) => _itemFree(e, context))
                                  .toList(),
                        ),
                      ],
                    )
                    : Container(),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _divider(String text) {
    return customDivider(
      text: Text(text, style: TextStyle(color: toHexToColor(primaryTextColor))),
      color: toHexToColor(borderColor),
    );
  }

  Widget _itemFree(OrderDetailResponse freeItem, BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 3),
      child: Container(
        height: 40,
        width: MediaQuery.sizeOf(context).width,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(8),
          border: Border.all(color: Colors.amber),
        ),
        padding: EdgeInsets.all(4),
        child: Row(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            ClipRRect(
              borderRadius: BorderRadius.circular(10),
              // child: Image.asset(avtWomen6, fit: BoxFit.cover),
              child:
                  (freeItem.product?.image != null)
                      ? CachedNetworkImage(
                        imageUrl: freeItem.product!.image!,
                        placeholder:
                            (context, url) =>
                                Lottie.asset('assets/animations/loading.json'),

                        errorWidget:
                            (context, url, error) =>
                                Image.asset(imgBranch, fit: BoxFit.contain),
                        width: 40,
                        height: 40,
                        fit: BoxFit.contain,
                      )
                      : Image.asset(imgBranch, fit: BoxFit.contain),
            ),
            customAutoSizeText(
              14,
              18,
              freeItem.product?.name ?? '',
              isBold: true,
              textColor: toHexToColor(primaryTextColor),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildListImage(int index) {
    // Nếu chưa có stream và list cho index này, khởi tạo
    imageStreams.putIfAbsent(index, () => BehaviorSubject<List<String>>());
    listImages.putIfAbsent(index, () => []);

    return StreamBuilder<List<String>>(
      stream: imageStreams[index]!.stream,
      initialData: listImages[index],
      builder: (context, snapshot) {
        final images = snapshot.data ?? [];

        return Wrap(
          spacing: 8,
          runSpacing: 8,
          children: List.generate(5, (i) {
            if (i < images.length) {
              return Container(
                width: 60,
                height: 60,
                decoration: BoxDecoration(
                  borderRadius: BorderRadius.circular(8),
                  image: DecorationImage(
                    image: NetworkImage(images[i]),
                    fit: BoxFit.cover,
                  ),
                ),
              );
            } else {
              return GestureDetector(
                onTap: () async {
                  final urls = await ImageUploader.pickAndUploadImages(
                    ImageSource.camera,
                  );
                  if (urls != null && urls.isNotEmpty) {
                    listImages[index]!.add(urls[0]);
                    imageStreams[index]!.add(List.from(listImages[index]!));
                    await ImageUploader.callApiWithImageUrls(urls);
                  }
                },
                child: Container(
                  width: 50,
                  height: 50,
                  decoration: BoxDecoration(
                    color: Colors.grey[300],
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Icon(Icons.add),
                ),
              );
            }
          }),
        );
      },
    );
  }

  Widget _buildTextReason({
    TextEditingController? controller,
    bool? isMainReason = false,
  }) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: TextField(
        controller: controller,
        minLines: isMainReason == true ? 3 : 1,
        maxLines: isMainReason == true ? null : 1,
        decoration: InputDecoration(
          filled: true,
          fillColor: Colors.grey[200],
          contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 10),
          enabledBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8),
            borderSide: BorderSide(color: Colors.white54),
          ),
          focusedBorder: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8),
            borderSide: BorderSide(color: Colors.white54, width: 1.5),
          ),
          hintText: 'Nhập lý do trả hàng...',
          hintStyle: TextStyle(color: Colors.grey),
        ),
        style: TextStyle(color: Colors.black),
        cursorColor: Colors.white54,
      ),
    );
  }

  Widget _buildButton(VoidCallback onTap) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
      child: InkWell(
        onTap: () => onTap(),
        child: Container(
          height: 45,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(8),
            color: toHexToColor(primaryButtonColor),
          ),
          alignment: Alignment.center,
          child: Center(
            child: Text(
              'Trả hàng',
              style: TextStyle(
                color: Colors.white,
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildChangeNumber(
    int count,
    VoidCallback onPlus,
    VoidCallback onMinus,
  ) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Container(
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(4),
        ),
        width: 100,
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            InkWell(
              onTap: () => onMinus(),
              child: Container(
                padding: EdgeInsets.symmetric(horizontal: 14),
                decoration: BoxDecoration(
                  color: toHexToColor(primaryButtonColor),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text('-', style: TextStyle(color: Colors.white)),
              ),
            ),
            Container(width: 20, child: Text('$count')),
            InkWell(
              onTap: () => onPlus(),
              child: Container(
                padding: EdgeInsets.symmetric(horizontal: 14),
                decoration: BoxDecoration(
                  color: toHexToColor(primaryButtonColor),
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text('+', style: TextStyle(color: Colors.white)),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
