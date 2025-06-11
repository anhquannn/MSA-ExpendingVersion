abstract class IBranchRepository {
  GetAllBranch({int page = 0, int size = 5, String sortDirection = 'desc'});
  GetBranchbyProductId(int productId);
  GetBeanchByRole(String role);
  GetBranchById(int id);
  DeleteBranch(int id);
}
