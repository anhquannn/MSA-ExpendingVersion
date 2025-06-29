import 'package:flutter/material.dart';
import 'package:get_it/get_it.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/feature/data/model/response/branch_response_response.dart';
import 'package:msa/feature/domain/entities/branch_model.dart';
import 'package:msa/feature/domain/usecase/branch_use_case.dart';
import 'package:msa/feature/presentation/customer/branch_list/ui/branch_list_screen.dart';
import 'package:rxdart/rxdart.dart';

import '../../../../data/datasources/local/starage.dart';

class BranchListBloc extends BaseBloc<BranchListScreen> {
  final branchModel = BehaviorSubject<List<BranchModel>>();
  int selectedBranchId = 1;
  final BranchUseCase _branchUseCases = GetIt.I<BranchUseCase>();
  List<BranchModel> listModel = [];

  @override
  String get contextKey => 'CreateOrderScreen';

  @override
  void onInit() {}

  @override
  void onDispose() {}

  @override
  void onReady() {
    onGetAllBranch();
  }

  @override
  void onResumed() {}

  @override
  Widget build(BuildContext viewContext) => widget.build(viewContext);

  onChangeBranch(int index) {
    selectedBranchId = index;
    setState(() {});
  }

  onGetAllBranch() async {
    List<BranchModel> model = [];
    final BranchResponseModel data = await _branchUseCases.getAllBranches(
      page: 1,
      size: 100,
      sortDirection: 'asc',
    );
    if (data.content.isNotEmpty && data.content != []) {
      for (BranchModel b in data.content) {
        model.add(b);
      }
      listModel.addAll(model);
      branchModel.add(model);
    }
  }

  onPopScreen() {
    Storage.branchModelGlobal = listModel.firstWhere(
      (element) => element.branchId == selectedBranchId,
      orElse: () => BranchModel(), 
    );
    Navigator.pop(viewContext, selectedBranchId);
  }
}
