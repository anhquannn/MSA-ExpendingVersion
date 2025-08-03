// import 'dart:io';

// import 'package:http/http.dart' as http;
// import 'package:image_picker/image_picker.dart';
// import 'package:supabase_flutter/supabase_flutter.dart';

// class ImageUploader {
//   // Khởi tạo Supabase client
//   static final supabase = Supabase.instance.client;
//   static const String bucketName = 'msa'; // Thay bằng tên bucket thực tế của bạn

//   static Future<List<String>?> pickAndUploadImages(ImageSource source) async {
//     try {
//       final ImagePicker picker = ImagePicker();
//       List<XFile> images = [];

//       if (source == ImageSource.camera) {
        
//             print('Chụp một ảnh từ camera');
//         // Chụp một ảnh từ camera
//         final XFile? image = await picker.pickImage(source: ImageSource.camera);
//         if (image != null) {
//           images.add(image);
//         }
//       } else {
//             print('CChọn nhiều ảnh từ thư viện');
//         // Chọn nhiều ảnh từ thư viện
//         final List<XFile> selectedImages = await picker.pickMultiImage();
//         images.addAll(selectedImages);
//       }

//       if (images.isEmpty) {
//             print('Người dùng hủy chọn ảnh');
//         // Người dùng hủy chọn ảnh
//         return null;
//       }

//       // Danh sách URL công khai
//       List<String> publicUrls = [];

//       // Upload từng ảnh lên Supabase Storage
//       for (var image in images) {
//         final File imageFile = File(image.path);
//         // Tạo tên file duy nhất (timestamp + tên file)
//         final String fileName = '${DateTime.now().millisecondsSinceEpoch}_${image.name}';

//         // Upload file
//         await supabase.storage.from(bucketName).upload(fileName, imageFile);

//         // Lấy public URL
//         final String publicUrl = supabase.storage.from(bucketName).getPublicUrl(fileName);
//         publicUrls.add(publicUrl);
//       }

//       return publicUrls;
//     } catch (e) {
//       print('Lỗi khi upload ảnh: $e');
//       return null;
//     }
//   }

//   /// Hàm gọi API với danh sách URL ảnh
//   /// [imageUrls] là danh sách URL công khai của ảnh
//   /// Thay thế bằng logic gọi API của bạn
//   static Future<void> callApiWithImageUrls(List<String> imageUrls) async {
//     try {
//       // Ví dụ: Gửi danh sách imageUrls đến API của bạn
//       final response = await http.post(
//         Uri.parse('https://lmtqwglnnbgsrxhelpxz.storage.supabase.co/storage/v1/s3'),
//         body: {'image_urls': imageUrls},
//       );
//       print('Gọi API với danh sách URL: $imageUrls');
//       // Xử lý response từ API tại đây
//     } catch (e) {
//       print('Lỗi khi gọi API: $e');
//     }
//   }
// }
import 'dart:convert'; // Thêm import cho jsonEncode
import 'dart:io';
import 'package:http/http.dart' as http;
import 'package:image_picker/image_picker.dart';
import 'package:supabase_flutter/supabase_flutter.dart';

class ImageUploader {
  // Khởi tạo Supabase client
  static final supabase = Supabase.instance.client;
  static const String bucketName = 'msa'; // Tên bucket của bạn

  /// Chọn và upload nhiều hình ảnh
  static Future<List<String>?> pickAndUploadImages(ImageSource source) async {
    try {
      final ImagePicker picker = ImagePicker();
      List<XFile> images = [];

      if (source == ImageSource.camera) {
        print('Chụp một ảnh từ camera');
        // Chụp một ảnh từ camera
        final XFile? image = await picker.pickImage(source: ImageSource.camera);
        if (image != null) {
          images.add(image);
        }
      } else {
        print('Chọn nhiều ảnh từ thư viện');
        // Chọn nhiều ảnh từ thư viện
        final List<XFile> selectedImages = await picker.pickMultiImage();
        images.addAll(selectedImages);
      }

      if (images.isEmpty) {
        print('Người dùng hủy chọn ảnh');
        return null;
      }

      // Danh sách URL công khai
      List<String> publicUrls = [];

      // Upload từng ảnh lên Supabase Storage
      for (var image in images) {
        final File imageFile = File(image.path);
        // Tạo tên file duy nhất (timestamp + tên file)
        final String fileName = '${DateTime.now().millisecondsSinceEpoch}_${image.name}';

        // Upload file
        await supabase.storage.from(bucketName).upload(fileName, imageFile);

        // Lấy public URL
        final String publicUrl = supabase.storage.from(bucketName).getPublicUrl(fileName);
        publicUrls.add(publicUrl);
        print('Uploaded image URL: $publicUrl'); // In URL của từng ảnh
      }

      print('Danh sách URL công khai: $publicUrls'); // In danh sách URL
      return publicUrls;
    } catch (e) {
      print('Lỗi khi upload ảnh: $e');
      return null;
    }
  }

  /// Hàm gọi API với danh sách URL ảnh
  static Future<void> callApiWithImageUrls(List<String> imageUrls) async {
    try {
      // Encode danh sách URL thành chuỗi JSON
      final String jsonBody = jsonEncode({'image_urls': imageUrls});
      print('JSON body gửi đi: $jsonBody'); // In JSON body

      // Gửi yêu cầu POST tới API của bạn
      final response = await http.post(
        Uri.parse('https://lmtqwglnnbgsrxhelpxz.storage.supabase.co/storage/v1/s3'), // Thay bằng endpoint API thực tế
        headers: {
          'Content-Type': 'application/json', // Đặt header là JSON
        },
        body: jsonBody,
      );

      print('API Response Status: ${response.statusCode}'); // In mã trạng thái
      print('API Response Body: ${response.body}'); // In nội dung response

      if (response.statusCode == 200) {
        print('Gọi API thành công với danh sách URL: $imageUrls');
      } else {
        print('Gọi API thất bại với mã trạng thái: ${response.statusCode}');
      }
    } catch (e) {
      print('Lỗi khi gọi API: $e');
    }
  }
}