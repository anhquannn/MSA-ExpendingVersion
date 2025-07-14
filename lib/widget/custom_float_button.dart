import 'package:flutter/material.dart';

class DraggablePointButton extends StatefulWidget {
  final int points;

  const DraggablePointButton({super.key, required this.points});

  @override
  State<DraggablePointButton> createState() => _DraggablePointButtonState();
}

class _DraggablePointButtonState extends State<DraggablePointButton> {
  // Vị trí ban đầu của widget
  Offset position = const Offset(20, 100);

  // Trạng thái để kiểm soát việc hiển thị widget
  bool _isVisible = true;

  @override
  Widget build(BuildContext context) {
    // Nếu _isVisible là false, trả về một widget trống để ẩn nó đi
    if (!_isVisible) {
      return const SizedBox.shrink();
    }

    final screenSize = MediaQuery.of(context).size;
    // Các hằng số kích thước để tính toán giới hạn
    const double buttonHeight = 90; // Chiều cao ước tính của widget
    const double buttonWidth = 140; // Chiều rộng ước tính của widget
    const double verticalMargin = 100; // Khoảng cách giới hạn trên và dưới

    // Tính toán giới hạn di chuyển theo chiều dọc
    final double topLimit = verticalMargin;
    final double bottomLimit =
        screenSize.height - verticalMargin - buttonHeight;

    // Sử dụng Stack để có thể định vị widget một cách tự do
    return Stack(
      children: [
        Positioned(
          top: position.dy,
          left: position.dx,
          child: GestureDetector(
            // Cập nhật vị trí khi người dùng kéo widget
            onPanUpdate: (details) {
              setState(() {
                double newX = position.dx + details.delta.dx;
                double newY = position.dy + details.delta.dy;

                // Giới hạn vị trí trong màn hình
                // Chiều ngang: từ 0 đến (chiều rộng màn hình - chiều rộng widget)
                newX = newX.clamp(0.0, screenSize.width - buttonWidth);
                // Chiều dọc: từ giới hạn trên đến giới hạn dưới
                newY = newY.clamp(topLimit, bottomLimit);

                position = Offset(newX, newY);
              });
            },
            child: Material(
              elevation: 6,
              color: Colors.transparent,
              borderRadius: BorderRadius.circular(30),
              child: Stack(
                // Dùng Stack để đặt nút X lên trên widget chính
                clipBehavior:
                    Clip.none, // Cho phép nút X hiển thị bên ngoài container
                children: [
                  // Widget chính chứa điểm
                  InkWell(
                    borderRadius: BorderRadius.circular(30),
                    onTap: () {
                      ScaffoldMessenger.of(context).showSnackBar(
                        SnackBar(
                          content: Text('Bạn đã tích ${widget.points} điểm!'),
                        ),
                      );
                    },
                    child: Container(
                      width: buttonWidth,
                      padding: const EdgeInsets.symmetric(
                        horizontal: 20,
                        vertical: 12,
                      ),
                      decoration: BoxDecoration(
                        borderRadius: BorderRadius.circular(30),
                        color: Colors.orange.shade100,
                      ),
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          const Icon(
                            Icons.monetization_on,
                            color: Colors.amber,
                            size: 40,
                          ),
                          const SizedBox(height: 4),
                          Text(
                            '${widget.points}',
                            style: const TextStyle(
                              color: Colors.amber,
                              fontWeight: FontWeight.bold,
                              fontSize: 16,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                  // Nút X để ẩn widget
                  Positioned(
                    top: -10,
                    right: -10,
                    child: GestureDetector(
                      onTap: () {
                        setState(() {
                          _isVisible = false;
                        });
                      },
                      child: Container(
                        padding: const EdgeInsets.all(4),
                        decoration: const BoxDecoration(
                          color: Colors.black54,
                          shape: BoxShape.circle,
                        ),
                        child: const Icon(
                          Icons.close,
                          color: Colors.white,
                          size: 18,
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ),
      ],
    );
  }
}
