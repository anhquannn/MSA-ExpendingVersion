import 'package:msa/feature/data/model/request/feedback_request_model.dart';
import 'package:msa/feature/domain/entities/feedback.dart';

abstract class IFeedbackRepository {
  Future<FeedbackModel?> getFeedbackById(int id);

  Future<List<FeedbackModel>?> getAllFeedbacksByProductId(int productId);

  Future<FeedbackModel?> createFeedback(FeedbackRequest feedback);

  Future<FeedbackModel?> updateFeedback(int id, FeedbackRequest feedback);

  Future<bool> deleteFeedback(int id);
}
