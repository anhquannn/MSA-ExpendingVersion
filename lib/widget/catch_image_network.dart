import 'package:flutter/material.dart';
import 'package:cached_network_image/cached_network_image.dart';

Widget networkImageWidget({
  required String imageUrl,
  required double width,
  required double height,
}) {
  return ClipRect(
    child: Align(
      alignment: Alignment.center,
      child: SizedBox(
        width: width,
        height: height,
        child: CachedNetworkImage(
          imageUrl: imageUrl,
          placeholder: (context, url) => CircularProgressIndicator(),
          errorWidget: (context, url, error) => Icon(Icons.error),
          fit: BoxFit.cover, // Điều chỉnh hình ảnh để bao phủ toàn bộ vùng
        ),
      ),
    ),
  );
}
