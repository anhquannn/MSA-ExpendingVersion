import 'package:flutter/material.dart';
import 'package:msa/feature/domain/entities/branch_model.dart';
import 'package:msa/feature/presentation/customer/branch_list/bloc/branch_list_bloc.dart';
import '../../../../../core/config/base_bloc.dart';
import '../../../../../core/config/constant.dart';
import '../../../../../core/utils/prarse_color.dart';
import '../../../../../widget/custom_widget.dart';
import '../../../../../widget/reuseable_screen_hide_appbar.dart';

class BranchListScreen extends BaseView<BranchListBloc> {
  BranchListScreen({super.key});

  @override
  BranchListBloc createState() => BranchListBloc();

  @override
  BranchListBloc createBloc() => BranchListBloc();

  Widget build(BuildContext context) {
    final bloc = (context as StatefulElement).state as BranchListBloc;
    return CustomScaffold(
      isHide: false,
      centerTitle: true,
      title: Text('Chọn Chi Nhánh', style: TextStyle(color: Colors.white)),
      appBarLeading: iconBack(bloc.viewContext, color: Colors.white),
      bodyBuilder: (controller) {
        return buildBodyContent(bloc: bloc, controller: controller);
      },
      floatActionButton: SizedBox(
        width: MediaQuery.of(context).size.width,
        height: 45, // Chiều cao tùy bạn
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16),
          child: ElevatedButton(
            style: ElevatedButton.styleFrom(
              backgroundColor: toHexToColor(appBarColor),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(10),
              ),
            ),
            onPressed: () {
              // Xử lý khi nhấn nút
            },
            child: const Text(
              'Xác nhận chi nhánh',
              style: TextStyle(fontSize: 16),
            ),
          ),
        ),
      ),

      hideBottomBarOnScroll: true,
    );
  }

  Widget buildBodyContent({
    required BranchListBloc bloc,
    required ScrollController controller,
  }) {
    return CustomScrollView(
      controller: controller,
      slivers: [
        SliverToBoxAdapter(
          child: StreamBuilder<List<BranchModel>>(
            stream: bloc.branchModel.stream,
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.waiting) {
                return const Center(child: CircularProgressIndicator());
              }

              if (snapshot.hasError) {
                return Center(child: Text('Lỗi: ${snapshot.error}'));
              }

              final branches = snapshot.data ?? [];

              return ListView.builder(
                itemCount: branches.length,
                itemBuilder: (context, index) {
                  final model = branches[index];
                  return itemBranch(
                    context,
                    model,
                    selectedBranchId: bloc.selectedBranchId,
                    onChanged: (value) {
                      bloc.onChangeBranch(value ?? 1);
                    },
                  );
                },
              );
            },
          ),
        ),

        const SliverToBoxAdapter(child: SizedBox(height: 5)),
      ],
    );
  }

  Widget itemBranch(
    BuildContext context,
    BranchModel model, {
    required int selectedBranchId,
    required Function(int?) onChanged,
  }) {
    return Card(
      elevation: 3,
      margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
      child: Container(
        padding: const EdgeInsets.all(12),
        width: MediaQuery.sizeOf(context).width,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(10),
          color: Colors.white,
        ),
        child: Row(
          children: [
            // Radio button (chiếm 1 phần)
            Expanded(
              flex: 1,
              child: Radio<int>(
                value: model.branchId ?? -1,
                groupValue: selectedBranchId,
                onChanged: onChanged,
              ),
            ),

            // Thông tin chi nhánh (chiếm 2 phần)
            Expanded(
              flex: 2,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    model.name ?? 'Chi nhánh không tên',
                    style: const TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 6),
                  Text("Số điện thoại: ${model.phone ?? 'Không có'}"),
                  const SizedBox(height: 4),
                  Text(
                    "Địa chỉ: ${[model.street, model.ward, model.district, model.city].where((e) => e != null && e.isNotEmpty).join(', ')}",
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
