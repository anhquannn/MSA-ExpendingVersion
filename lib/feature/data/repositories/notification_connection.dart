import 'package:msa/feature/data/datasources/global/http_connection.dart';
import 'package:msa/feature/data/model/response/notification_request_model.dart';
import 'package:msa/feature/domain/entities/notification_model.dart';

class NotificationConnection {
  static Future<List<NotificationModel>?> getNotification(NotificationFilterRequest request) async {
    final response =
        await HttpConnection.post<PaginatedResult<NotificationModel>>(
          'notification/paging',
          fromJsonT:
              (json) => PaginatedResult.fromJson(
                json,
                (itemJson) => NotificationModel.fromJson(itemJson),
              ),
          body: request.toJson(),
        );
    if (response.isSuccess) {
      return response.result?.content;
    }
    return null;
  }

  
}
