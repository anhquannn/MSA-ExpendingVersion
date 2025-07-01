import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/data/model/request/get_branch_request_model.dart';
import 'package:msa/feature/domain/entities/branch_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/presentation/customer/home_screen/bloc/home_screen_bloc.dart';
import 'package:msa/feature/presentation/customer/persional/ui/persional_screen.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';
import 'package:rxdart/rxdart.dart';

class SelectBranchScreen extends StatefulWidget {
  final int? branchId;
  const SelectBranchScreen({super.key, this.branchId});

  @override
  State<SelectBranchScreen> createState() => _SelectBranchScreenState();
}

class _SelectBranchScreenState extends State<SelectBranchScreen> {
  final streamBranchModel = BehaviorSubject<List<BranchModel>>();
  List<BranchModel>? listBranchModels = [];
  onGetAllBranch() async {
    print('🔍 Bắt đầu gọi API lấy danh sách chi nhánh...');

    final BranchFilterResponse response = await Repository.onGetAllBranch(
      BranchFilterRequest(),
    );
    final list = response.paginatedResult.content;

    for (var i in list) {
      if (i.branchId == widget.branchId) {
        i.isSelect = true;
      }
    }

    listBranchModels = list;
    streamBranchModel.set(listBranchModels ?? []);
    return true;
  }

  onChangeBranch(BranchModel model) {
    listBranchModels =
        listBranchModels?.map((element) {
          element.isSelect = (element.branchId == model.branchId);
          return element;
        }).toList();

    streamBranchModel.set(listBranchModels ?? []);
    Storage.branchModelGlobal = model;
    Storage.saveBranchModel(model);
    Navigator.pushAndRemoveUntil(
      context,
      MaterialPageRoute(builder: (context) => PersionalScreen()),
      (route) => false,
    );
  }

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      await onGetAllBranch();
    });
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
        'Chọn chi nhánh',
        style: TextStyle(
          color: Colors.white,
          fontSize: 18,
          fontWeight: FontWeight.bold,
        ),
      ),
      bodyBuilder: (controller) {
        return Column(
          children: [
            Expanded(
              child: StreamBuilder(
                stream: streamBranchModel.output,
                builder: (context, snapshot) {
                  final data = snapshot.data ?? [];
                  final selectedBranch = data.firstWhere(
                    (element) => element.isSelect == true,
                    orElse: () => BranchModel(),
                  );

                  return MediaQuery.removePadding(
                    context: context,
                    removeTop: true,
                    child: ListView.builder(
                      itemCount: data.length,
                      itemBuilder: (context, index) {
                        final model = data[index];
                        return _buildBranchItem(
                          branch: model,
                          context: context,
                          onChanged: (p0) {
                            onChangeBranch(p0 ?? BranchModel());
                          },
                          selectedBranch: selectedBranch,
                        );
                      },
                    ),
                  );
                },
              ),
            ),

            // InkWell(
            //   onTap: () => onSave,
            //   child: Container(
            //     width: MediaQuery.sizeOf(context).width,
            //     height: 45,
            //     decoration: BoxDecoration(
            //       borderRadius: BorderRadius.circular(10),
            //       color: toHexToColor(primaryColorGreen),
            //     ),
            //     child: Center(
            //       child: Text(
            //         'Đồng ý',
            //         style: TextStyle(
            //           color: Colors.white,
            //           fontWeight: FontWeight.bold,
            //           fontSize: 17,
            //         ),
            //       ),
            //     ),
            //   ),
            // ),
          ],
        );
      },
    );
  }

  Widget _buildBranchItem({
    required BranchModel branch,
    required BranchModel? selectedBranch,
    required Function(BranchModel?) onChanged,
    required BuildContext context,
  }) {
    final isSelected = branch.branchId == selectedBranch?.branchId;

    return InkWell(
      onTap: () => onChanged(branch),
      child: Card(
        margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(10),
          side: BorderSide(
            color: isSelected ? Colors.green : Colors.transparent,
            width: 2,
          ),
        ),
        child: Container(
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(10),
            color:
                isSelected
                    ? Colors.green.withOpacity(0.05)
                    : toHexToColor(backgroundColor1),
          ),
          width: MediaQuery.sizeOf(context).width,
          padding: const EdgeInsets.all(12),
          child: Row(
            children: [
              if (isSelected)
                Icon(Icons.check_circle, color: Colors.green)
              else
                Icon(Icons.circle_outlined, color: Colors.grey),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      branch.name ?? 'Chi nhánh',
                      style: TextStyle(
                        fontWeight: FontWeight.bold,
                        color: isSelected ? Colors.green : null,
                      ),
                    ),
                    Text(
                      '${branch.street}, ${branch.ward}, ${branch.district}, ${branch.city}',
                      style: TextStyle(color: Colors.grey[700]),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
