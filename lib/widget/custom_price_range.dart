import 'package:flutter/material.dart';
import 'package:flutter_xlider/flutter_xlider.dart';

class PriceRangeSlider extends StatefulWidget {
  final double min;
  final double max;
  final Function(double minPrice, double maxPrice) onChanged;
  final Function(double minPrice, double maxPrice)? onDragCompleted;
  const PriceRangeSlider({
    Key? key,
    this.min = 0,
    this.max = 10000000,
    required this.onChanged,
    this.onDragCompleted,
  }) : super(key: key);

  @override
  State<PriceRangeSlider> createState() => _PriceRangeSliderState();
}

class _PriceRangeSliderState extends State<PriceRangeSlider> {
  late double _lowerValue;
  late double _upperValue;

  @override
  void initState() {
    super.initState();
    _lowerValue = widget.min;
    _upperValue = widget.max;
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          "Khoảng giá:",
          style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
        ),
        // const SizedBox(height: 10),
        FlutterSlider(
          onDragCompleted: (handlerIndex, lowerValue, upperValue) {
            if (widget.onDragCompleted != null) {
              widget.onDragCompleted!(lowerValue, upperValue);
            }
          },
          values: [_lowerValue, _upperValue],
          rangeSlider: true,
          max: widget.max,
          min: widget.min,
          step: FlutterSliderStep(step: 10000),
          tooltip: FlutterSliderTooltip(
            leftPrefix: const Text('₫'),
            rightSuffix: const Text('đ'),
            textStyle: const TextStyle(fontSize: 14),
          ),
          handler: FlutterSliderHandler(
            decoration: const BoxDecoration(),
            child: const Icon(Icons.circle, color: Colors.blue, size: 16),
          ),
          rightHandler: FlutterSliderHandler(
            decoration: const BoxDecoration(),
            child: const Icon(Icons.circle, color: Colors.blue, size: 16),
          ),
          onDragging: (handlerIndex, lowerValue, upperValue) {
            setState(() {
              _lowerValue = lowerValue;
              _upperValue = upperValue;
            });
            widget.onChanged(_lowerValue, _upperValue);
          },
        ),
        // const SizedBox(height: 10),
        Text(
          "Từ: ${_formatCurrency(_lowerValue)}  →  Đến: ${_formatCurrency(_upperValue)}",
          style: const TextStyle(fontSize: 14),
        ),
      ],
    );
  }

  String _formatCurrency(double value) {
    return '${value.toInt().toString().replaceAllMapped(RegExp(r'(\d{1,3})(?=(\d{3})+(?!\d))'), (m) => '${m[1]}.')}₫';
  }
}
