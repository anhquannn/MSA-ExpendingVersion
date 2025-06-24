// SỬA: feedback_repository_impl.dart

import 'package:msa/core/config/constant.dart';
import 'package:msa/feature/data/model/request/feedback_request_model.dart';
import 'package:msa/feature/domain/entities/feedback.dart';
import 'package:msa/feature/domain/repositories/feedback_repository.dart';
import 'package:msa/feature/data/datasources/global/http_connection.dart';

class FeedbackRepositoryImpl extends IFeedbackRepository {
  @override
  Future<FeedbackModel?> getFeedbackById(int id) async {
    final response = await HttpConnection.get<FeedbackModel>(
      '$getFeedbackByIdUrl$id',
      fromJsonT: (json) => FeedbackModel.fromJson(json),
    );
    return response.result;
  }

  @override
  Future<List<FeedbackModel>> getAllFeedbacksByProductId(int productId) async {
    final response = await HttpConnection.get<List<FeedbackModel>>(
      '$getAllFeedbacksByProductIdUrl$productId',
      fromJsonT: (json) {
        final List<dynamic> jsonList = json as List<dynamic>;
        return jsonList.map((e) => FeedbackModel.fromJson(e)).toList();
      },
    );
    return response.result ?? [];
  }

  @override
  Future<FeedbackModel?> createFeedback(FeedbackRequest request) async {
    final response = await HttpConnection.post<FeedbackModel>(
      createFeedbackUrl,
      body: request.toJson(),
      fromJsonT: (json) => FeedbackModel.fromJson(json),
    );
    return response.result;
  }

  @override
  Future<FeedbackModel?> updateFeedback(int id, FeedbackRequest request) async {
    final response = await HttpConnection.put<FeedbackModel>(
      '$updateFeedbackUrl$id',
      body: request.toJson(),
      fromJsonT: (json) => FeedbackModel.fromJson(json),
    );
    return response.result;
  }

  @override
  Future<bool> deleteFeedback(int id) async {
    final response = await HttpConnection.delete<dynamic>(
      '$deleteFeedbackUrl$id',
      fromJsonT: (json) => json,
    );
    return response.isSuccess;
  }
}