import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/data/model/request/add_to_cart_request_model.dart';
import 'package:msa/feature/data/model/request/cartitem_selection_request_model.dart';
import 'package:msa/feature/data/model/request/category_filter_request.dart';
import 'package:msa/feature/data/model/request/product_filter_request.dart';
import 'package:msa/feature/data/model/request/supplier_filter_request.dart';
import 'package:msa/feature/data/model/response/product_filter_response.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/domain/usecase/cart_item_use_case.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/create_order_screen.dart';
import 'package:msa/feature/presentation/customer/product_detail/ui/product_detail_screen.dart';
import 'package:msa/feature/presentation/customer/product_filter/ui/product_filter_screen.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:msa/widget/custom_loading.dart';
import 'package:rxdart/rxdart.dart';

class ProductFilterBloc extends BaseBloc<ProductFilterScreen> {
  @override
  String get contextKey => 'PersionalScreen';
  @override
  Widget build(BuildContext viewContext) => widget.build(viewContext);
  final CartItemUseCase _cartItemUseCase = GetIt.I<CartItemUseCase>();

  final TextEditingController searchController = TextEditingController();

  ProductFilterResult? listProducts;
  final streamProductModels = BehaviorSubject<ProductFilterResult>();

  List<CategoryModel>? listCategoryModel;
  final streamCategoryModels = BehaviorSubject<List<CategoryModel>>();

  List<SupplierModel>? listSupplyModels;
  final streamSupplyModels = BehaviorSubject<List<SupplierModel>>();

  CategoryModel? categorySelect;
  SupplierModel? supplySelect;

  double? minPrice;
  double? maxPrice;

  Timer? debounce;

  @override
  void onDispose() {
    // TODO: implement onDispose
  }

  @override
  void onInit() {
    // TODO: implement onInit
  }

  @override
  void onReady() {
    Future.wait<void>([onGetCategory(), onGetSupply(), onGetProduct()]);
  }

  @override
  void onResumed() {
    // TODO: implement onResumed
  }

  onSearchChanged() {
    if (debounce?.isActive ?? false) debounce?.cancel();
    debounce = Timer(const Duration(milliseconds: 500), () {
      onGetProduct();
    });
  }

  onSearchSubmitted() async {
    searchController.text = '';
    await onGetProduct();
  }

  onGetProduct() async {
    try {
      ProductFilterRequest filter = ProductFilterRequest(
        keyword: searchController.text,
        categoryId: categorySelect?.categoryId,
        supplierId: supplySelect?.supplierId,
        minPrice: minPrice,
        maxPrice: maxPrice,
        page: 1,
        pageSize: 10,
        branchId: Storage.branchModelGlobal?.branchId,
      );

      ProductFilterResult product = await Repository.onFilterProducts(filter);
      listProducts = product;
      streamProductModels.add(product);
    } catch (e, stack) {
      print('❌ Lỗi khi lấy danh sách sản phẩm: $e');
      print('📛 Stacktrace: $stack');
      streamProductModels.add(ProductFilterResult());
    }
    setState(() {});
  }

  onGetCategory() async {
    try {
      final response = await Repository.onGetAllCategory(
        CategoryFilterRequest(),
      );
      listCategoryModel = response;
      streamCategoryModels.set(response);
    } catch (e) {
      print('Lỗi khi lấy danh sách danh mục: $e');
      streamCategoryModels.add([]); // Fallback nếu có lỗi
    }
  }

  onGetSupply() async {
    try {
      final response = await Repository.getAllSupply(
        SupplierFilterRequest(pageSize: 50),
      );
      listSupplyModels = response;
      streamSupplyModels.set(response);
    } catch (e) {
      print('Lỗi khi lấy danh sách danh mục: $e');
      streamSupplyModels.add([]);
    }
  }

  onTapProductDetail(ProductModel model) {
    Navigator.push(
      viewContext,
      MaterialPageRoute(
        builder: (context) => ProductDetailCustomerScreen(productModel: model),
      ),
    );
  }

  onTapCategory(CategoryModel model, BuildContext ctx) async {
    showFullScreenLoading(ctx);
    listCategoryModel?.forEach((e) {
      if (e.categoryId == model.categoryId) {
        categorySelect = model;
        e.selected = !e.selected;
      } else {
        e.selected = false;
      }
    });
    streamCategoryModels.set(listCategoryModel ?? []);
    if (model.selected == false) {
      categorySelect = null;
    }
    await onGetProduct();
    hideFullScreenLoading(ctx);
  }

  onTapSupply(SupplierModel model, BuildContext ctx) async {
    showFullScreenLoading(ctx);
    listSupplyModels?.forEach((e) {
      if (e.supplierId == model.supplierId) {
        supplySelect = model;
        e.selected = !(e.selected ?? false);
      } else {
        e.selected = false;
      }
    });
    streamSupplyModels.set(listSupplyModels ?? []);
    if (model.selected == false) {
      supplySelect = null;
    }
    await onGetProduct();
    hideFullScreenLoading(ctx);
  }

  onAddToCart(ProductModel model, BuildContext context) async {
    final data = await _cartItemUseCase.addToCart(
      AddToCartRequest(
        userId: Storage.userModelGlobal?.userId ?? 0,
        productId: model.productId ?? 0,
        branchId: Storage.branchModelGlobal?.branchId ?? 0,
        quantity: 1,
      ),
    );
    if (data) {
      // Navigator.pop(context);
      showCustomSuccessError(
        context,
        'Thêm sản phẩm vào giỏ hàng thành công',
        () {
          Navigator.pop(context);
        },
      );
    }else{
    showCustomMessageError(context);

    }
  }

  onBuyNow(ProductModel? model, BuildContext context) async{
    showFullScreenLoading(context);
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
        productId: model?.productId ?? 0,
        branchId: Storage.branchModelGlobal?.branchId ?? 0,
        quantity: 1,
      ),
    );

    print('🛒 Kết quả thêm vào giỏ: $data');

    if (data) {
      hideFullScreenLoading(context);
      Navigator.push(
        context,
        MaterialPageRoute(builder: (context) => CreateOrderScreen()),
      );
    } else {
      hideFullScreenLoading(context);
      print('❌ Không thể thêm sản phẩm vào giỏ hàng.');
    }
  }

  onChangePriceAPI(double min, double max) {
    minPrice = min;
    maxPrice = max;
    onGetProduct();
  }

}
