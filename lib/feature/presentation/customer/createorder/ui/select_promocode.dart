import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/feature/presentation/customer/createorder/bloc/create_order_bloc.dart';
import 'package:msa/widget/custom_item_promocode.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';

class SelectPromoCodeScreen extends StatelessWidget {
  final CreateOrderBloc? bloc;

  const SelectPromoCodeScreen({super.key, required this.bloc});

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      appBarLeading: InkWell(
        onTap: () {
          Navigator.pop(context, true);
        },
        child: Icon(Icons.arrow_back_ios, color: Colors.white, size: 24),
      ),
      centerTitle: true,
      title: Text(
        'Danh sách mã giảm giá',
        style: TextStyle(color: Colors.white, fontSize: 18),
      ),
      bodyBuilder: (controller) {
        return Padding(
          padding: const EdgeInsets.all(8),
          child: StreamBuilder(
            stream: bloc?.streamPromoCodeModels.output,
            builder: (context, snapshot) {
              if (snapshot.hasData) {
                final data = snapshot.data;
                return MediaQuery.removePadding(
                  context: context,
                  removeTop: true,
                  child: ListView.builder(
                    itemCount: data?.length,
                    itemBuilder: (context, index) {
                      final promo = data?[index];
                      return InkWell(
                        onTap: () {
                          bloc?.onSelectPromoCode(promo, bContext: context);
                          Navigator.pop(context);
                        },
                        child: SizedBox(
                          height: 170,
                          child: Container(
                            child: customItemPromoCode(
                              promo!,
                              () {},
                              () {},
                              isSelected: promo.selected ?? false,
                            ),
                          ),
                        ),
                      );
                    },
                  ),
                );
              }
              return SizedBox.shrink();
            },
          ),
        );
      },
    );
  }

  Widget _customTextSpan(String label, String value) {
    return RichText(
      text: TextSpan(
        text: label,
        style: const TextStyle(
          fontWeight: FontWeight.bold,
          color: Colors.black,
        ),
        children: [
          TextSpan(
            text: value,
            style: const TextStyle(
              fontWeight: FontWeight.normal,
              color: Colors.black,
            ),
          ),
        ],
      ),
    );
  }
}
