import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/data/model/request/feedback_request_model.dart';
import 'package:msa/feature/domain/entities/feedback.dart';
import 'package:msa/feature/domain/repositories/feedback_repository.dart';

import '../datasources/global/http_connection.dart';

class FeedbackRepositoryImpl extends IFeedbackRepository {
  @override
  Future<FeedbackModel?> getFeedbackById(int id) async {
    final data = await HttpConnection.get('$getFeedbackByIdUrl$id');
    return data.isSuccess ? FeedbackModel.fromJson(data.data) : null;
  }

  @override
  Future<List<FeedbackModel>?> getAllFeedbacksByProductId(int productId) async {
    final data = await HttpConnection.get(
      '$getAllFeedbacksByProductIdUrl$productId',
    );
    if (data.isSuccess) {
      return (data.data as List).map((e) => FeedbackModel.fromJson(e)).toList();
    }
    return null;
  }

  @override
  Future<FeedbackModel?> createFeedback(FeedbackRequest request) async {
    final data = await HttpConnection.post(
      createFeedbackUrl,
      body: request.toJson(),
    );
    return data.isSuccess ? FeedbackModel.fromJson(data.data) : null;
  }

  @override
  Future<FeedbackModel?> updateFeedback(int id, FeedbackRequest request) async {
    final data = await HttpConnection.put(
      '$updateFeedbackUrl$id',
      body: request.toJson(),
    );
    return data.isSuccess ? FeedbackModel.fromJson(data.data) : null;
  }

  @override
  Future<bool> deleteFeedback(int id) async {
    final data = await HttpConnection.delete('$deleteFeedbackUrl$id');
    return data.isSuccess;
  }
}
