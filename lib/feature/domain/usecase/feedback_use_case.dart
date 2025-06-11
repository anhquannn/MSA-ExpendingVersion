import 'package:msa/feature/data/model/request/feedback_request_model.dart';
import 'package:msa/feature/domain/entities/feedback.dart';
import 'package:msa/feature/domain/repositories/feedback_repository.dart';

class FeedbackUseCases {
  final GetFeedbackByIdUseCase getFeedbackById;
  final GetAllFeedbacksByProductIdUseCase getAllFeedbacksByProductId;
  final CreateFeedbackUseCase createFeedback;
  final UpdateFeedbackUseCase updateFeedback;
  final DeleteFeedbackUseCase deleteFeedback;

  FeedbackUseCases({
    required this.getFeedbackById,
    required this.getAllFeedbacksByProductId,
    required this.createFeedback,
    required this.updateFeedback,
    required this.deleteFeedback,
  });
}

class GetFeedbackByIdUseCase {
  final IFeedbackRepository repository;

  GetFeedbackByIdUseCase(this.repository);

  Future<FeedbackModel?> call(int id) {
    return repository.getFeedbackById(id);
  }
}

class GetAllFeedbacksByProductIdUseCase {
  final IFeedbackRepository repository;

  GetAllFeedbacksByProductIdUseCase(this.repository);

  Future<List<FeedbackModel>?> call(int productId) {
    return repository.getAllFeedbacksByProductId(productId);
  }
}

class CreateFeedbackUseCase {
  final IFeedbackRepository repository;

  CreateFeedbackUseCase(this.repository);

  Future<FeedbackModel?> call(FeedbackRequest request) {
    return repository.createFeedback(request);
  }
}

class UpdateFeedbackUseCase {
  final IFeedbackRepository repository;

  UpdateFeedbackUseCase(this.repository);

  Future<FeedbackModel?> call(int id, FeedbackRequest request) {
    return repository.updateFeedback(id, request);
  }
}

class DeleteFeedbackUseCase {
  final IFeedbackRepository repository;

  DeleteFeedbackUseCase(this.repository);

  Future<bool> call(int id) {
    return repository.deleteFeedback(id);
  }
}
