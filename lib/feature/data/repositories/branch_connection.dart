import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/response/branch_response_response.dart';
import 'package:msa/feature/domain/repositories/branch_repository.dart';

class BranchRepositoryImpl extends IBranchRepository {
  @override
  DeleteBranch(int id) {
    // TODO: implement DeleteBranch
    throw UnimplementedError();
  }

  @override
  GetAllBranch({
    int page = 0,
    int size = 5,
    String sortDirection = 'desc',
  }) async {
    final data = await HttpConnection.get(getAllBranch);
    if (data.isSuccess) {
      return BranchResponseModel.fromJson(data.data);
    } else {
      return null;
    }
  }

  @override
  GetBeanchByRole(String role) {
    // TODO: implement GetBeanchByRole
    throw UnimplementedError();
  }

  @override
  GetBranchById(int id) {
    // TODO: implement GetBranchById
    throw UnimplementedError();
  }

  @override
  GetBranchbyProductId(int productId) {
    // TODO: implement GetBranchbyProductId
    throw UnimplementedError();
  }
}
