// TODO: Nút kéo qua
import 'package:auto_size_text/auto_size_text.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

class customSliableButton extends StatefulWidget {
  final double width;
  final double height;
  final VoidCallback onSwipeComplete;
  final String instructionText;
  final String completeText;
  final Color buttonColor;
  final Color thumbColor;
  final IconData? iconSuccess;
  final double? borderRadius;

  const customSliableButton({
    Key? key,
    required this.width,
    required this.height,
    required this.onSwipeComplete,
    this.instructionText = '',
    this.completeText = '',
    this.buttonColor = Colors.green,
    this.thumbColor = Colors.white,
    this.iconSuccess,
    this.borderRadius,
  }) : super(key: key);

  @override
  _customSliableButtonState createState() => _customSliableButtonState();
}

class _customSliableButtonState extends State<customSliableButton>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  double _dragPosition = 0.0;
  bool _isCompleted = false;
  double _maxDragDistance = 0.0;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      duration: const Duration(milliseconds: 300),
      vsync: this,
    );
    _maxDragDistance = widget.width - widget.height;
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _handleDragUpdate(DragUpdateDetails details) {
    if (_isCompleted) return;

    final newPosition = _dragPosition + details.primaryDelta!;
    setState(() {
      _dragPosition = newPosition.clamp(0.0, _maxDragDistance);
      _controller.value = _dragPosition / _maxDragDistance;
    });
  }

  void _handleDragEnd(DragEndDetails details) {
    if (_isCompleted) return;

    if (_dragPosition >= _maxDragDistance) {
      _completeSwipe();
    } else {
      _resetSwipe();
    }
  }

  void _completeSwipe() {
    _controller.animateTo(1.0).then((_) {
      setState(() => _isCompleted = true);
      widget.onSwipeComplete();

      // Sau 2 giây, quay lại trạng thái ban đầu để cho phép vuốt lại
      Future.delayed(const Duration(seconds: 2), () {
        _resetSwipe();
      });
    });
  }

  void _resetSwipe() {
    _controller.animateBack(0.0).then((_) {
      setState(() {
        _dragPosition = 0.0;
        _isCompleted = false; // Để có thể vuốt lại
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onHorizontalDragUpdate: _handleDragUpdate,
      onHorizontalDragEnd: _handleDragEnd,
      child: Container(
        width: widget.width,
        height: widget.height,
        decoration: BoxDecoration(
          gradient: LinearGradient(
            colors: [widget.buttonColor.withOpacity(0.8), widget.buttonColor],
            begin: Alignment.centerLeft,
            end: Alignment.centerRight,
          ),
          borderRadius: BorderRadius.circular(widget.borderRadius ?? 15),
          boxShadow: [
            BoxShadow(
              color: widget.buttonColor.withOpacity(0.3),
              blurRadius: 10,
              offset: const Offset(0, 5),
            ),
          ],
        ),
        child: Stack(
          children: [
            Center(
              child: Opacity(
                opacity: _isCompleted ? 0.0 : (1.0 - _controller.value),
                child: Center(
                  child: Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      // Icon(
                      //   Icons.arrow_forward_ios_outlined,
                      //   color: Colors.white.withOpacity(0.6),
                      //   size: 20,
                      // ),
                      Icon(
                        Icons.arrow_forward_ios_outlined,
                        color: Colors.white.withOpacity(0.6),
                        size: 20,
                      ),
                      Icon(
                        Icons.arrow_forward_ios_outlined,
                        color: Colors.white.withOpacity(0.6),
                        size: 20,
                      ),
                      Padding(
                        padding: const EdgeInsets.only(left: 5, right: 5),
                        child: AutoSizeText(
                          minFontSize: 16,
                          maxFontSize: 24,
                          widget.instructionText,
                          style: const TextStyle(
                            color: Colors.white,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                      ),
                      Icon(
                        Icons.arrow_forward_ios_outlined,
                        color: Colors.white.withOpacity(0.6),
                        size: 20,
                      ),
                      Icon(
                        Icons.arrow_forward_ios_outlined,
                        color: Colors.white.withOpacity(0.6),
                        size: 20,
                      ),
                      // Icon(
                      //   Icons.arrow_forward_ios_outlined,
                      //   color: Colors.white.withOpacity(0.6),
                      //   size: 20,
                      // ),
                    ],
                  ),
                ),
              ),
            ),
            // Thumb
            AnimatedPositioned(
              duration: const Duration(milliseconds: 100),
              left: _dragPosition,
              child: Container(
                width: widget.height,
                height: widget.height,
                decoration: BoxDecoration(
                  color: widget.thumbColor,
                  borderRadius: BorderRadius.circular(
                    widget.borderRadius ?? 15,
                  ),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.2),
                      blurRadius: 8,
                      offset: const Offset(0, 4),
                    ),
                  ],
                ),
                child: Opacity(
                  opacity: _controller.value.clamp(0.3, 1.0),
                  child: Icon(
                    Icons.arrow_forward_ios_outlined,
                    color: widget.buttonColor,
                    size: 30,
                  ),
                ),
              ),
            ),
            // Success state
            Center(
              child: Opacity(
                opacity: _controller.value.clamp(0.0, 1.0),
                child: Icon(
                  widget.iconSuccess ?? Icons.check_circle,
                  color: Colors.white,
                  size: widget.height - 10,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
