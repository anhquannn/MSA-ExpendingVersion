import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/data/model/request/add_to_cart_request_model.dart';
import 'package:msa/feature/data/model/request/cartitem_selection_request_model.dart';
import 'package:msa/feature/data/model/request/product_filter_request.dart';
import 'package:msa/feature/data/model/response/product_filter_response.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/domain/usecase/cart_item_use_case.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/create_order_screen.dart';
import 'package:msa/feature/presentation/customer/home_screen/ui/home_screen.dart';
import 'package:msa/feature/presentation/customer/product_detail/ui/product_list_detail_screen.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:msa/widget/custom_loading.dart';
import 'package:rxdart/subjects.dart';

class ProductListDetailBloc extends BaseBloc<ProductListDetailScreen> {
  List<ProductModel> listProducts = [];
  final streamProductModels = BehaviorSubject<List<ProductModel>>();

  final CartItemUseCase _cartItemUseCase = GetIt.I<CartItemUseCase>();

  @override
  String get contextKey => 'ProductListDetailScreen';

  @override
  void onInit() {
    listProducts = widget.productList ?? [];
    streamProductModels.set(listProducts);
  }

  @override
  void onDispose() {}

  @override
  void onReady() async {
    if (widget.category != null) {
      await onGetProduct();
    }
  }

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext context) => widget.build(context);

  onGetProduct() async {
    try {
      final filter = ProductFilterRequest(
        page: 1,
        pageSize: 20,
        categoryId: [(widget.category?.categoryId) ?? 0],
        branchId: Storage.branchModelGlobal?.branchId,
      );

      final result = await Repository.onFilterProducts(filter);

      // Tuỳ thuộc logic, bạn dùng page hay list
      final products = result.productsPage?.content ?? [];

      listProducts = products;
      streamProductModels.add(products);
      setState(() {});
    } catch (e, stack) {
      print('❌ Lỗi khi lấy danh sách sản phẩm: $e');
      streamProductModels.add([]);
    }
  }

  onBuyNow(ProductModel model, BuildContext bContext) async {
    showFullScreenLoading(bContext);
    List<int> cartIds = [];
    print('🛒 1111111111Danh sách cartItemIds: $cartIds');
    widget.listCartItemModel?.forEach((element) {
      print(element.cartItemId);
      cartIds.add(element.cartItemId ?? 0);
    });

    print('🛒 Danh sách cartItemIds: $cartIds');

    final CartItemSelectionRequest request = CartItemSelectionRequest(
      cartId: Storage.cartModelGlobal?.cartId ?? 0,
      cartItemIds: cartIds,
    );

    print('📦 Request cập nhật cart selection: ${request.toJson()}');

    final updateResponse = await Repository.onUpdateCartItemsSelectionAPI(
      request,
      false,
    );

    print('✅ Kết quả cập nhật cart selection: $updateResponse');

    final data = await _cartItemUseCase.addToCart(
      AddToCartRequest(
        userId: Storage.userModelGlobal?.userId ?? 0,
        productId: model.productId ?? 0,
        branchId: Storage.branchModelGlobal?.branchId ?? 0,
        quantity: 1,
      ),
    );

    print('🛒 Kết quả thêm vào giỏ: $data');

    if (data) {
      hideFullScreenLoading(bContext);
      Navigator.push(
        bContext,
        MaterialPageRoute(builder: (bContext) => CreateOrderScreen()),
      );
    } else {
      hideFullScreenLoading(bContext);
      print('❌ Không thể thêm sản phẩm vào giỏ hàng.');
    }
  }

  onAddToCart(ProductModel model, BuildContext bcontext) async {
    showFullScreenLoading(bcontext);
    final data = await _cartItemUseCase.addToCart(
      AddToCartRequest(
        userId: Storage.userModelGlobal?.userId ?? 0,
        productId: model.productId ?? 0,
        branchId: Storage.branchModelGlobal?.branchId ?? 0,
        quantity: 1,
      ),
    );
    if (data) {
      hideFullScreenLoading(bcontext);
      showCustomSuccessError(bcontext, 'Thêm vào giỏ hàng thành công', () {
        Navigator.pushAndRemoveUntil(
          context,
          MaterialPageRoute(builder: (context) => HomeScreen()),
          (route) => false,
        );
      });
      // Navigator.pop(bcontext);
    } else {
      showCustomMessageError(bcontext);
    }
  }
}
