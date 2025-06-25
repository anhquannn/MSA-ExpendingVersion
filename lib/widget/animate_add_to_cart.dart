// import 'package:flutter/material.dart';
// import 'package:msa/core/config/constant.dart';
// import 'package:msa/core/utils/prarse_color.dart';

// class AnimatedAddToCart extends StatefulWidget {
//   final Offset start;
//   final Offset end;
//   final Size imageSize;
//   final VoidCallback onComplete;
//   final String? urlImage;

//   const AnimatedAddToCart({
//     Key? key,
//     required this.start,
//     required this.end,
//     required this.imageSize,
//     required this.onComplete,
//     this.urlImage,
//   }) : super(key: key);

//   @override
//   _AnimatedAddToCartState createState() => _AnimatedAddToCartState();
// }

// class _AnimatedAddToCartState extends State<AnimatedAddToCart>
//     with SingleTickerProviderStateMixin {
//   late AnimationController _controller;
//   late Animation<Offset> _animation;

//   @override
//   void initState() {
//     super.initState();

//     _controller = AnimationController(
//       vsync: this,
//       duration: const Duration(milliseconds: 800),
//     );

//     _animation = Tween<Offset>(begin: widget.start, end: widget.end).animate(
//       CurvedAnimation(parent: _controller, curve: Curves.easeInOut),
//     )..addStatusListener((status) {
//       if (status == AnimationStatus.completed) {
//         widget.onComplete(); // ✅ Gọi remove khi xong
//       }
//     });

//     _controller.forward();
//   }

//   @override
//   void dispose() {
//     _controller.dispose();
//     super.dispose();
//   }

//   @override
//   Widget build(BuildContext context) {
//     return AnimatedBuilder(
//       animation: _animation,
//       builder: (context, child) {
//         return Positioned(
//           left: _animation.value.dx,
//           top: _animation.value.dy,
//           child: child!,
//         );
//       },
//       child:
//           widget.urlImage != null
//               ? ClipRRect(
//                 borderRadius: BorderRadius.circular(10),
//                 child: Image.network(
//                   widget.urlImage ?? '',
//                   height: 30,
//                   width: 30,
//                   fit: BoxFit.cover,
//                 ),
//               )
//               : Icon(
//                 Icons.card_travel_rounded,
//                 size: 30,
//                 color: toHexToColor(appBarColor),
//               ),
//     );
//   }
// }
import 'package:flutter/material.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';

class AnimatedAddToCart extends StatefulWidget {
  final Offset start;
  final Offset end;
  final Size imageSize;
  final VoidCallback onComplete;
  final String? urlImage;

  const AnimatedAddToCart({
    Key? key,
    required this.start,
    required this.end,
    required this.imageSize,
    required this.onComplete,
    this.urlImage,
  }) : super(key: key);

  @override
  _AnimatedAddToCartState createState() => _AnimatedAddToCartState();
}

class _AnimatedAddToCartState extends State<AnimatedAddToCart>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _xAnimation;
  late Animation<double> _yAnimation;

  @override
  void initState() {
    super.initState();

    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 800),
    );

    final double midY =
        (widget.start.dy + widget.end.dy) / 2 - 80; // nhảy lên 80px ở giữa

    _xAnimation = Tween<double>(
      begin: widget.start.dx,
      end: widget.end.dx,
    ).animate(CurvedAnimation(parent: _controller, curve: Curves.easeInOut));

    _yAnimation = TweenSequence<double>([
      TweenSequenceItem(
        tween: Tween<double>(
          begin: widget.start.dy,
          end: midY,
        ).chain(CurveTween(curve: Curves.easeOut)),
        weight: 50,
      ),
      TweenSequenceItem(
        tween: Tween<double>(
          begin: midY,
          end: widget.end.dy,
        ).chain(CurveTween(curve: Curves.easeIn)),
        weight: 50,
      ),
    ]).animate(_controller)..addStatusListener((status) {
      if (status == AnimationStatus.completed) {
        widget.onComplete(); // ✅ Xoá overlay khi kết thúc
      }
    });

    _controller.forward();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _controller,
      builder: (context, child) {
        return Positioned(
          left: _xAnimation.value,
          top: _yAnimation.value,
          child: child!,
        );
      },
      child:
          widget.urlImage != null
              ? ClipOval(
                child: Image.network(
                  widget.urlImage!,
                  height: 30,
                  width: 30,
                  fit: BoxFit.cover,
                ),
              )
              : Icon(
                Icons.card_travel_rounded,
                size: 30,
                color: toHexToColor(appBarColor),
              ),
    );
  }
}
