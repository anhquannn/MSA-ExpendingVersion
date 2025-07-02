import 'package:flutter/material.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/widget/custom_dropdown.dart';

class RatingContent extends StatefulWidget {
  final Function(int rating, String comment) onSubmit;

  const RatingContent({super.key, required this.onSubmit});

  @override
  State<RatingContent> createState() => RatingContentState();
}

class RatingContentState extends State<RatingContent> {
  int selectedRating = 0;
  final TextEditingController commentController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        // Hàng 5 sao
        Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: List.generate(5, (index) {
            return IconButton(
              icon: Icon(
                index < selectedRating ? Icons.star : Icons.star_border,
                color: Colors.amber,
                size: 32,
              ),
              onPressed: () {
                setState(() {
                  selectedRating = index + 1;
                });
              },
            );
          }),
        ),
        const SizedBox(height: 10),
        // TextField nhập nội dung
        Theme(
          data: Theme.of(context).copyWith(
            colorScheme: Theme.of(context).colorScheme.copyWith(
              primary: toHexToColor(primaryButtonColor), // màu khi focus
            ),
          ),
          child: TextField(
            controller: commentController,
            maxLines: 4,
            decoration: InputDecoration(
              hintText: 'Nhập nội dung đánh giá...',
              enabledBorder: OutlineInputBorder(
                borderSide: BorderSide(color: toHexToColor(primaryButtonColor)),
              ),
              focusedBorder: OutlineInputBorder(
                borderSide: BorderSide(
                  color: toHexToColor(primaryButtonColor),
                  width: 2,
                ),
              ),
              errorBorder: OutlineInputBorder(
                borderSide: BorderSide(color: toHexToColor(primaryButtonColor)),
              ),
              focusedErrorBorder: OutlineInputBorder(
                borderSide: BorderSide(
                  color: toHexToColor(primaryButtonColor),
                  width: 2,
                ),
              ),
            ),
          ),
        ),
      ],
    );
  }

  String get comment => commentController.text;

  void callSubmit() {
    widget.onSubmit(selectedRating, comment);
  }
}

class ReturnContent extends StatefulWidget {
  final Function(String reason) onSubmit;

  const ReturnContent({super.key, required this.onSubmit});

  @override
  State<ReturnContent> createState() => ReturnContentState();
}

class ReturnContentState extends State<ReturnContent> {
  final TextEditingController reasonController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Theme(
          data: Theme.of(context).copyWith(
            colorScheme: Theme.of(context).colorScheme.copyWith(
              primary: toHexToColor(primaryButtonColor), // Màu focus
            ),
          ),
          child: TextField(
            controller: reasonController,
            maxLines: 4,
            decoration: InputDecoration(
              hintText: 'Nhập lý do trả hàng...',
              enabledBorder: OutlineInputBorder(
                borderSide: BorderSide(color: toHexToColor(primaryButtonColor)),
              ),
              focusedBorder: OutlineInputBorder(
                borderSide: BorderSide(
                  color: toHexToColor(primaryButtonColor),
                  width: 2,
                ),
              ),
              errorBorder: OutlineInputBorder(
                borderSide: BorderSide(color: toHexToColor(primaryButtonColor)),
              ),
              focusedErrorBorder: OutlineInputBorder(
                borderSide: BorderSide(
                  color: toHexToColor(primaryButtonColor),
                  width: 2,
                ),
              ),
            ),
          ),
        ),
      ],
    );
  }

  void callSubmit() {
    final reason = reasonController.text.trim();
    if (reason.isEmpty) return;
    widget.onSubmit(reason);
  }
}

Future<void> showRatingDialog(BuildContext context) async {
  final width = MediaQuery.of(context).size.width * 0.9;
  final height = width; // Chiều cao = chiều rộng

  final GlobalKey<RatingContentState> contentKey = GlobalKey();

  await showCustomDialog(
    context,
    width,
    height,
    'Đánh giá sản phẩm',
    RatingContent(
      key: contentKey,
      onSubmit: (rating, comment) {
        // Gọi API tại đây
        print('Đánh giá: $rating sao');
        print('Nội dung: $comment');

        // TODO: Gọi API gửi đánh giá tại đây

        Navigator.of(context).pop(); // Đóng dialog sau khi submit
      },
    ),
    false,
    true,
    null,
    onSubmit: () {
      contentKey.currentState?.callSubmit();
    },
    onClose: () {
      Navigator.of(context).pop();
    },
  );
}
