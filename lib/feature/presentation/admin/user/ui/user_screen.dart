import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/widget/build_avatar.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:msa/widget/custom_textfield.dart';
import 'package:msa/widget/custom_widget.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';

import '../../../../../widget/custom_dropshadow.dart';
import '../../../../../widget/custom_item_product.dart';
import '../../../../../widget/icon_wrap.dart';
import '../bloc/user_bloc.dart';

class UserScreen extends BaseView<UserBloc> {
  const UserScreen({super.key});

  @override
  UserBloc createState() => UserBloc();

  Widget build(BuildContext context) {
    final _bloc = (context as StatefulElement).state as UserBloc;
    return CustomScaffold(
      appBarGradient: false,
      appBarLeading: iconBack(size: 25),
      centerTitle: true,
      title: customTextField(
        node: _bloc.searchFocusNode,
        borderRadius: 30,
        _bloc.searchController,
        prefixIcon: Icon(Icons.search, color: toHexToColor(iconColor)),
      ),
      bodyBuilder: (controller) {
        return Container(
          width: AppSize.width(),
          color: toHexToColor(backgroundColor),
          child: CustomScrollView(
            controller: controller,
            slivers: [
              SliverToBoxAdapter(child: _customItemUser(bloc: _bloc)),
              SliverToBoxAdapter(child: _customItemUser(bloc: _bloc)),
              SliverToBoxAdapter(child: _customItemUser(bloc: _bloc)),
              SliverToBoxAdapter(child: _customItemUser(bloc: _bloc)),
              SliverToBoxAdapter(child: _customItemUser(bloc: _bloc)),
              SliverToBoxAdapter(child: _customItemUser(bloc: _bloc)),
              SliverToBoxAdapter(child: _customItemUser(bloc: _bloc)),
              SliverToBoxAdapter(child: _customItemUser(bloc: _bloc)),
              SliverToBoxAdapter(child: _customItemUser(bloc: _bloc)),
              SliverToBoxAdapter(child: _customItemUser(bloc: _bloc)),
              SliverToBoxAdapter(child: _customItemUser(bloc: _bloc)),
              SliverToBoxAdapter(child: _customItemUser(bloc: _bloc)),
              SliverToBoxAdapter(child: _customItemUser(bloc: _bloc)),
              SliverToBoxAdapter(child: _customItemUser(bloc: _bloc)),
              SliverToBoxAdapter(child: _customItemUser(bloc: _bloc)),
            ],
          ),
        );
      },
      hideBottomBarOnScroll: true,
    );
  }

  Widget _customItemUser({required UserBloc bloc}) {
    ProductModel model =
        ProductModel(); // Cập nhật theo dữ liệu thật từ Bloc hoặc Model
    final width = MediaQuery.of(context).size.width * 0.98;
    final height = MediaQuery.of(context).size.height * 0.2;

    return Padding(
      padding: const EdgeInsets.only(bottom: 5),
      child: SizedBox(
        width: width,
        child: Card(
          color: Colors.white,
          child: Column(
            children: [
              Row(
                children: [
                  buildAvatar(imagePath: avtWomen4, size: width * 0.35),
                  Spacer(),
                  SizedBox(
                    width: width * 0.55,
                    child: Column(
                      children: [
                        buildInfoContainer(
                          label: Text('Nguyen Van A'),
                          color: toHexToColor(secondaryColorOrange),
                          width: width * 0.55,
                        ),
                        buildInfoContainer(
                          label: Text('070 898 3437'),
                          color: toHexToColor(secondaryColorPurple),
                          width: width * 0.55,
                        ),
                        buildInfoContainer(
                          label: Text('minhquang03082003@gmail.com'),
                          color: toHexToColor(secondaryColorPurple),
                          width: width * 0.55,
                        ),
                      ],
                    ),
                  ),
                ],
              ),
              _buildBottomBar(width: width, bloc: bloc),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildBottomBar({required double width, required UserBloc bloc}) {
    return Container(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.only(
          bottomRight: Radius.circular(10),
          bottomLeft: Radius.circular(10),
        ),
        color: Colors.blueGrey,
      ),
      width: width,
      child: Row(
        children: [
          _buildDropdownButton(bloc: bloc, width: width),
          Spacer(),
          buildIconButton(onTap: () {}, icon: Icons.call, color: Colors.green),
          buildIconButton(
            onTap: () {},
            icon: Icons.edit,
            color: toHexToColor(primaryColorPurple),
          ),
          buildIconButton(
            onTap: () {},
            icon: Icons.delete_outline,
            color: toHexToColor(primaryErrorColor),
          ),
          SizedBox(width: 10),
        ],
      ),
    );
  }

  Widget _buildDropdownButton({required UserBloc bloc, required double width}) {
    return SizedBox(
      width: width * 0.5,
      height: 50,
      child: Padding(
        padding: const EdgeInsets.all(10.0),
        child: customDropdownButton(
          items: bloc.mockItems,
          hint: 'Select',
          selectedItem: bloc.selectedValue,
          onChanged: (value) => bloc.onDropdownChanged(value),
        ),
      ),
    );
  }

  @override
  UserBloc createBloc() => UserBloc();
}

class ProductModel {
  final String image =
      'https://pixabay.com/illustrations/draw-nature-landscape-free-image-3583548/';
  final String name = 'Dưa hấu';
  final String price = '1.000.000đ';
  final String sale = '10000';
  final String stock = '100';
  final String stockLevel = 'Low';
}
