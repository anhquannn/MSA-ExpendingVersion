import 'package:msa/feature/data/model/response/branch_response_response.dart';
import 'package:msa/feature/domain/entities/branch_model.dart';
import 'package:msa/feature/domain/repositories/branch_repository.dart'; // Import IBranchRepository

class BranchUseCase {
  final GetAllBranchesUseCase getAllBranches;
  final GetBranchByProductIdUseCase getBranchByProductId;
  final GetBranchByRoleUseCase getBranchByRole;
  final GetBranchByIdUseCase getBranchById;
  final DeleteBranchUseCase deleteBranch;



  BranchUseCase({
    required this.getAllBranches,
    required this.getBranchByProductId,
    required this.getBranchByRole,
    required this.getBranchById,
    required this.deleteBranch,
  });
}

// Get All Branches
class GetAllBranchesUseCase {
  final IBranchRepository repository;
  GetAllBranchesUseCase(this.repository);

  Future<BranchResponseModel> call({int page = 0, int size = 5, String sortDirection = 'desc'}) {
    return repository.GetAllBranch(page: page, size: size, sortDirection: sortDirection);
  }
}

// Get Branch by Product ID
class GetBranchByProductIdUseCase {
  final IBranchRepository repository;
  GetBranchByProductIdUseCase(this.repository);

  Future<BranchResponseModel?> call(int productId) {
    return repository.GetBranchbyProductId(productId);
  }
}

// Get Branch by Role
class GetBranchByRoleUseCase {
  final IBranchRepository repository;
  GetBranchByRoleUseCase(this.repository);

  Future<List<BranchResponseModel>?> call(String role) {
    return repository.GetBeanchByRole(role);
  }
}

// Get Branch by ID
class GetBranchByIdUseCase {
  final IBranchRepository repository;
  GetBranchByIdUseCase(this.repository);

  Future<BranchResponseModel?> call(int id) {
    return repository.GetBranchById(id);
  }
}

// Delete Branch
class DeleteBranchUseCase {
  final IBranchRepository repository;
  DeleteBranchUseCase(this.repository);

  Future<bool> call(int id) {
    return repository.DeleteBranch(id);
  }
}
