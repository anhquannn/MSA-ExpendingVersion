import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/feature/domain/entities/cart_item.dart';
import 'package:msa/feature/domain/entities/product_model.dart';
import 'package:msa/feature/presentation/customer/home_screen/ui/home_screen.dart';
import 'package:msa/feature/presentation/customer/product_filter/bloc/product_filter_bloc.dart';
import 'package:msa/widget/custom_price_range.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';

class ProductFilterScreen extends BaseView<ProductFilterBloc> {
  final List<CartItemModel>? listCartItemModel;
  const ProductFilterScreen({super.key, this.listCartItemModel});

  @override
  ProductFilterBloc createState() => ProductFilterBloc();

  @override
  ProductFilterBloc createBloc() => ProductFilterBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as ProductFilterBloc;
    return CustomScaffold(
      appBarLeading: InkWell(
        onTap: () {
          Navigator.pushAndRemoveUntil(
            context,
            MaterialPageRoute(builder: (context) => HomeScreen()),
            (route) => false,
          );
        },
        child: Icon(Icons.arrow_back_ios, color: Colors.white, size: 24),
      ),
      centerTitle: false,
      title: buildSearchField(bloc),
      bodyBuilder: (controller) {
        return CustomScrollView(
          slivers: [
            SliverToBoxAdapter(child: _buildFilter(context, bloc)),
            SliverToBoxAdapter(child: _buildBody(context, bloc)),
          ],
        );
      },
    );
  }

  _buildFilter(BuildContext context, ProductFilterBloc bloc) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 16),
      child: Column(
        children: [
          _buildPageRang(bloc),
          _buildTextFormat('Nhà sản xuất'),
          _buildSupplyChips(context, (model) {
            bloc.onTapSupply(model, context);
          }, bloc),
          _buildTextFormat('Loại sản phẩm'),
          _buildCategoryChips(context, (model) {
            bloc.onTapCategory(model, context);
          }, bloc),
          _buildLIstChild(context, bloc, (model) {
            bloc.onTapCategory(model, context);
          }),
          _buildProductSale(bloc, context),
          _buildAllProducts(bloc, context),
        ],
      ),
    );
  }

  _buildBody(BuildContext context, ProductFilterBloc bloc) {}

  Widget buildSearchField(ProductFilterBloc bloc) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(20),
        boxShadow: [
          BoxShadow(color: Colors.black12, blurRadius: 4, offset: Offset(0, 2)),
        ],
      ),
      child: TextField(
        controller: bloc.searchController,
        decoration: InputDecoration(
          hintText: 'Tìm kiếm sản phẩm...',
          prefixIcon: Icon(Icons.search),
          suffixIcon:
              bloc.searchController.text.isNotEmpty
                  ? IconButton(
                    icon: Icon(Icons.clear),
                    onPressed: () {
                      bloc.onSearchSubmitted();
                    },
                  )
                  : null,
          border: InputBorder.none,
          contentPadding: EdgeInsets.symmetric(vertical: 14, horizontal: 16),
        ),
        onChanged: (value) {
          bloc.onSearchChanged();
        },
      ),
    );
  }

  Widget _buildCategoryChips(
    BuildContext context,
    Function(CategoryModel model) onTap,
    ProductFilterBloc bloc,
  ) {
    return StreamBuilder(
      stream: bloc.streamCategoryModels,
      builder: (context, snapshot) {
        if (snapshot.data != null) {
          final List<CategoryModel> list = snapshot.data ?? [];
          return SizedBox(
            height: 50,
            child: ListView.builder(
              physics: AlwaysScrollableScrollPhysics(),
              scrollDirection: Axis.horizontal,
              itemCount: list.length,
              itemBuilder: (context, index) {
                CategoryModel model = list[index];
                return Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: InkWell(
                    onTap: () {
                      onTap(model);
                    },
                    child: Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 12,
                        vertical: 8,
                      ),
                      decoration: BoxDecoration(
                        color:
                            model.selected == true
                                ? toHexToColor(primaryColorPurple)
                                : toHexToColor(primaryButtonColor),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Text(
                        model.name ?? '',
                        style: TextStyle(
                          color:
                              model.selected == true
                                  ? Colors.black
                                  : Colors.white,
                        ),
                      ),
                    ),
                  ),
                );
              },
            ),
          );
        }
        return Center(child: Text('Không tìm thấy loại sản phẩm'));
      },
    );
  }

  Widget _buildLIstChild(
    BuildContext context,
    ProductFilterBloc bloc,
    Function(CategoryModel model) onTap,
  ) {
    return StreamBuilder(
      stream: bloc.streamListCategoryChild.output,
      builder: (context, snapshot) {
        final data = snapshot.data;

        final hasData =
            data != null && data.any((child) => child?.isNotEmpty ?? false);

        if (!hasData) return SizedBox.shrink();

        return Column(
          // ✅ Fix: Không lồng ListView ngang vào ListView dọc
          children:
              data.map((child) {
                if (child != null && child.isNotEmpty) {
                  return _buildCategoryChipsChild(context, onTap, bloc, child);
                } else {
                  return SizedBox.shrink();
                }
              }).toList(),
        );
      },
    );
  }

  Widget _buildCategoryChipsChild(
    BuildContext context,
    Function(CategoryModel model) onTap,
    ProductFilterBloc bloc,
    List<CategoryModel> list,
  ) {
    return SizedBox(
      // ✅ Fix: Ràng buộc width
      width: MediaQuery.sizeOf(context).width,
      height: 50,
      child: ListView.builder(
        physics: const AlwaysScrollableScrollPhysics(),
        scrollDirection: Axis.horizontal,
        itemCount: list.length,
        itemBuilder: (context, index) {
          final data = list[index];
          return Padding(
            padding: const EdgeInsets.all(8.0),
            child: InkWell(
              onTap: () {
                onTap(data);
              },
              child: Container(
                padding: const EdgeInsets.symmetric(
                  horizontal: 12,
                  vertical: 8,
                ),
                decoration: BoxDecoration(
                  color:
                      data.selected == true
                          ? toHexToColor(primaryColorPurple)
                          : toHexToColor(primaryButtonColor),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Text(
                  data.name ?? '',
                  style: TextStyle(
                    color: data.selected == true ? Colors.black : Colors.white,
                  ),
                ),
              ),
            ),
          );
        },
      ),
    );
  }

  Widget _buildSupplyChips(
    BuildContext context,
    Function(SupplierModel model) onTap,
    ProductFilterBloc bloc,
  ) {
    return StreamBuilder(
      stream: bloc.streamSupplyModels,
      builder: (context, snapshot) {
        if (snapshot.data != null) {
          final List<SupplierModel> list = snapshot.data ?? [];
          return SizedBox(
            height: 50,
            child: ListView.builder(
              physics: AlwaysScrollableScrollPhysics(),
              scrollDirection: Axis.horizontal,
              itemCount: list.length,
              itemBuilder: (context, index) {
                SupplierModel model = list[index];
                return Padding(
                  padding: const EdgeInsets.all(8.0),
                  child: InkWell(
                    onTap: () {
                      onTap(model);
                    },
                    child: Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 12,
                        vertical: 8,
                      ),
                      decoration: BoxDecoration(
                        color:
                            model.selected == true
                                ? toHexToColor(primaryColorOrange)
                                : toHexToColor(primaryButtonColor),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Text(
                        model.name ?? '',
                        style: TextStyle(
                          color:
                              model.selected == false
                                  ? Colors.white
                                  : Colors.black,
                        ),
                      ),
                    ),
                  ),
                );
              },
            ),
          );
        }
        return Center(child: Text('Không tìm thấy nhà sản xuất'));
      },
    );
  }

  Widget _buildTextFormat(String text) {
    return SizedBox(
      width: AppSize.width(),
      child: Row(
        children: [
          Text(
            text,
            style: TextStyle(
              color: Colors.blue,
              fontStyle: FontStyle.italic,
              fontSize: 12,
            ),
          ),
          Spacer(),
        ],
      ),
    );
  }

  Widget _buildAllProducts(ProductFilterBloc bloc, BuildContext context) {
    return StreamBuilder(
      stream: bloc.streamProductModels,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        }
        if (snapshot.hasError) {
          return Center(child: Text('Lỗi: ${snapshot.error}'));
        }
        if (!snapshot.hasData || snapshot.data == null) {
          return const Center(child: Text('Không có sản phẩm'));
        }

        final products = snapshot.data?.productsPage?.content;
        final itemCount = (products?.length ?? 0) > 20 ? 20 : products?.length;

        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Padding(
              padding: EdgeInsets.symmetric(horizontal: 8, vertical: 8),
              child: Text(
                'Tất cả sản phẩm',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
            ),
            MediaQuery.removePadding(
              context: context,
              removeTop: true,
              removeBottom: true,
              child: GridView.builder(
                gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 2,
                  crossAxisSpacing: 3,
                  mainAxisSpacing: 3,
                  mainAxisExtent: 300,
                ),
                physics: const NeverScrollableScrollPhysics(),
                shrinkWrap: true,
                itemCount: itemCount,
                itemBuilder: (context, index) {
                  final product = products?[index] ?? ProductModel();
                  return InkWell(
                    key: key,
                    onTap: () {
                      bloc.onTapProductDetail(product);
                    },
                    child: customItemProductCustomer(
                      onBuy: () {
                        bloc.onBuyNow(product, context);
                      },
                      onAddToCart: () {
                        bloc.onAddToCart(product, context);
                      },
                      isDiscount: false,
                      product,
                      AppSize.w(0.4),
                    ),
                  );
                },
              ),
            ),
          ],
        );
      },
    );
  }

  Widget _buildProductSale(ProductFilterBloc bloc, BuildContext context) {
    return StreamBuilder(
      stream: bloc.streamProductModels,
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return const Center(child: CircularProgressIndicator());
        }
        if (snapshot.hasError) {
          return Center(child: Text('Lỗi: ${snapshot.error}'));
        }
        if (!snapshot.hasData || snapshot.data == null) {
          return const Center(child: Text('Không có sản phẩm'));
        }

        final products = snapshot.data?.discountedProductsPage?.content;
        final itemCount = (products?.length ?? 0) > 20 ? 20 : products?.length;

        return Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Padding(
              padding: EdgeInsets.symmetric(horizontal: 8, vertical: 8),
              child: Text(
                'Sản phẩm giảm giá',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
            ),
            MediaQuery.removePadding(
              context: context,
              removeTop: true,
              removeBottom: true,
              child: GridView.builder(
                gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                  crossAxisCount: 2,
                  crossAxisSpacing: 3,
                  mainAxisSpacing: 3,
                  mainAxisExtent: 300,
                ),
                physics: const NeverScrollableScrollPhysics(),
                shrinkWrap: true,
                itemCount: itemCount,
                itemBuilder: (context, index) {
                  final product = products?[index] ?? ProductModel();
                  return InkWell(
                    key: key,
                    onTap: () {
                      bloc.onTapProductDetail(product);
                    },
                    child: customItemProductCustomer(
                      onBuy: () {
                        bloc.onBuyNow(product, context);
                      },
                      onAddToCart: () {
                        bloc.onAddToCart(product, context);
                      },
                      isDiscount: false,
                      product,
                      AppSize.w(0.4),
                    ),
                  );
                },
              ),
            ),
          ],
        );
      },
    );
  }

  Widget _buildText(String text) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 5),
      child: SizedBox(
        width: AppSize.width(),
        child: Row(
          children: [
            Expanded(child: Divider()),
            Text(text, style: TextStyle(color: toHexToColor(primaryTextColor))),
            Expanded(child: Divider()),
          ],
        ),
      ),
    );
  }

  Widget _buildPageRang(ProductFilterBloc bloc) {
    return PriceRangeSlider(
      min: 0,
      max: 10000000,
      onChanged: (minPrice, maxPrice) {
        // bloc.onChangePrice(minPrice, maxPrice);
      },
      onDragCompleted: (minPrice, maxPrice) {
        bloc.onChangePriceAPI(minPrice, maxPrice);
      },
    );
  }
}
