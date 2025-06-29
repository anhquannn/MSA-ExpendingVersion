import 'dart:developer';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/domain/entities/address_model.dart';
import 'package:msa/feature/domain/entities/goship_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';
import 'package:rxdart/subjects.dart';

class AddressListWidget extends StatefulWidget {
  final UserAddressModel addresses;
  const AddressListWidget({Key? key, required this.addresses})
    : super(key: key);

  @override
  State<AddressListWidget> createState() => _AddressListWidgetState();
}

class _AddressListWidgetState extends State<AddressListWidget> {
  int? selectedIndex;
  final streamAddress = BehaviorSubject<List<UserAddressModel>>();
  List<UserAddressModel> address = [];

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) async {
      await onGetUserAddress();
    });
  }

  onGetUserAddress() async {
    final data = await Repository.getUserAddresses();
    if (data != null) {
      address = data;
      streamAddress.set(data);
      onCheckAddress();
    }
  }

  onCheckAddress() {
    if (address != null) {
      for (UserAddressModel model in address) {
        if (widget.addresses.userAddressId == model.userAddressId) {
          model.primary = true;
          break;
        }
      }
      streamAddress.set(address);
    }
  }

  onChange(int userAddressId) {
    for (UserAddressModel model in address) {
      if (userAddressId == model.userAddressId) {
        Storage.addressModel = model;
        model.primary = true;
        break;
      }
    }
    streamAddress.set(address);
  }

  onSave(UserAddressModel model, BuildContext bContext) async {
    // In dữ liệu đầu vào
    print('[onSave] Input Model: ${model.toJson()}');

    final request = UserAddressRequest(
      city: model.city ?? '',
      district: model.district ?? '',
      street: model.street ?? '',
      ward: model.ward ?? '',
      cityCode: model.cityCode ?? '',
      districtCode: model.districtCode ?? '',
      wardCode: model.wardCode ?? '',
      isPrimary: true,
      userId: Storage.userModelGlobal?.userId ?? 0,
    );

    // In request sẽ gửi đi
    print('[onSave] Sending Request: ${request.toJson()}');

    final data = await Repository.onUpdateUserAddress(
      model.userAddressId ?? 0,
      request,
    );

    // In kết quả từ API
    print('[onSave] Response: $data');

    if (data) {
      Storage.addressModel = model;
      Storage.saveAddress(model);
      print('[onSave] Address updated and saved locally');
      Navigator.pop(bContext, model);
    } else {
      print('[onSave] Update failed - show dialog');
      showCustomDialog(
        bContext,
        AppSize.width(),
        AppSize.width(),
        'Thông báo',
        Text(
          'Cập nhật thông tin thất bại',
          style: TextStyle(color: toHexToColor(primaryTextColor)),
        ),
        true,
        true,
        Icon(Icons.warning, color: toHexToColor(primaryColorGreen)),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      centerTitle: true,
      title: Text(
        'Danh sách địa chỉ',
        style: TextStyle(
          color: Colors.white,
          fontSize: 18,
          fontWeight: FontWeight.w600,
        ),
      ),
      bodyBuilder: (controller) {
        return StreamBuilder<List<UserAddressModel>>(
          stream: streamAddress.stream,
          builder: (context, snapshot) {
            final addresses = snapshot.data ?? [];

            if (addresses.isEmpty) {
              return const Center(child: Text('Không có địa chỉ nào.'));
            }

            return Column(
              children: List.generate(addresses.length, (index) {
                final UserAddressModel data = addresses[index];
                final isSelected = selectedIndex == index;

                return GestureDetector(
                  onTap: () {
                    selectedIndex = index;
                    onChange(data.userAddressId ?? 0);
                  },
                  child: Card(
                    color: Colors.white,
                    margin: const EdgeInsets.symmetric(
                      vertical: 6,
                      horizontal: 10,
                    ),
                    child: Container(
                      padding: const EdgeInsets.all(12),
                      child: Row(
                        children: [
                          // Thông tin địa chỉ
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  '${data.street} ${data.ward} ${data.district} ${data.city}',
                                  style: const TextStyle(
                                    fontSize: 16,
                                    fontWeight: FontWeight.w600,
                                  ),
                                  overflow: TextOverflow.ellipsis,
                                ),
                              ],
                            ),
                          ),
                          const SizedBox(width: 10),
                          // Custom Radio Button
                          Container(
                            width: 22,
                            height: 22,
                            decoration: BoxDecoration(
                              shape: BoxShape.circle,
                              border: Border.all(
                                color: isSelected ? Colors.green : Colors.grey,
                                width: 2,
                              ),
                            ),
                            child:
                                isSelected
                                    ? Center(
                                      child: Container(
                                        width: 10,
                                        height: 10,
                                        decoration: const BoxDecoration(
                                          color: Colors.green,
                                          shape: BoxShape.circle,
                                        ),
                                      ),
                                    )
                                    : null,
                          ),
                          const SizedBox(width: 8),
                          // Nút chỉnh sửa
                          InkWell(
                            onTap: () => onSave(data, context),
                            child: const Icon(
                              Icons.edit,
                              color: Colors.black54,
                            ),
                          ),
                        ],
                      ),
                    ),
                  ),
                );
              }),
            );
          },
        );
      },
    );
  }
}
