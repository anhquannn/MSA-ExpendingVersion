import 'dart:developer';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/data/model/request/user_address_request.dart';
import 'package:msa/feature/domain/entities/address_model.dart';
import 'package:msa/feature/domain/entities/goship_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/create_order_screen.dart';
import 'package:msa/feature/presentation/customer/home_screen/ui/home_screen.dart';
import 'package:msa/feature/presentation/customer/persional/ui/persional_screen.dart';
import 'package:msa/widget/custom_dropdown.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';
import 'package:rxdart/subjects.dart';

class AddressListWidget extends StatefulWidget {
  final UserAddressModel addresses;
  final bool isChangePrimary;
  const AddressListWidget({
    Key? key,
    required this.addresses,
    this.isChangePrimary = false,
  }) : super(key: key);

  @override
  State<AddressListWidget> createState() => _AddressListWidgetState();
}

class _AddressListWidgetState extends State<AddressListWidget> {
  int? selectedIndex;
  final streamAddress = BehaviorSubject<List<UserAddressModel>>();
  List<UserAddressModel> address = [];
  int? userAddresId = Storage.addressModel?.userAddressId;

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
    if (address != []) {
      for (UserAddressModel model in address) {
        if (widget.addresses.userAddressId == model.userAddressId) {
          model.primary = true;
        } else {
          model.primary = false;
        }
      }
      streamAddress.set(address);
    }
  }

  onChange(int userAddressId) {
    userAddressId = userAddressId;
    for (UserAddressModel model in address) {
      if (userAddressId == model.userAddressId) {
        Storage.addressModel = model;
        model.primary = true;
      } else {
        model.primary = false;
      }
    }

    streamAddress.set(address);
  }

  onSave(BuildContext bContext) async {
    UserAddressUpdateRequest request = UserAddressUpdateRequest();
    UserAddressModel model = UserAddressModel();
    for (var i in address) {
      if (i.primary == true) {
        model = i;
        request = UserAddressUpdateRequest(
          city: i.city ?? '',
          district: i.district ?? '',
          street: i.street ?? '',
          ward: i.ward ?? '',
          cityCode: i.cityCode ?? '',
          districtCode: i.districtCode ?? '',
          wardCode: i.wardCode ?? '',
          primary: true,
          userId: Storage.userModelGlobal?.userId ?? 0,
        );

        print('[onSave] Sending Request1: ${request.toJson()}');
      }
    }

    // In request sẽ gửi đi

    final data = await Repository.onUpdateUserAddress(
      userAddresId ?? 0,
      request,
    );

    // In kết quả từ API
    print('[onSave] Response2: $data');

    if (data) {
      Storage.addressModel = model;
      Storage.saveAddress(model);
      print('[onSave] Address updated and saved locally');
      if (widget.isChangePrimary == true) {
        Navigator.pushAndRemoveUntil(
          bContext,
          MaterialPageRoute(builder: (bContext) => PersionalScreen()),
          (route) => false,
        );
      } else {
        Navigator.pushAndRemoveUntil(
          bContext,
          MaterialPageRoute(builder: (bContext) => CreateOrderScreen()),
          (route) => false,
        );
      }
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
      appBarLeading: InkWell(
        onTap: () {
          widget.isChangePrimary == true
              ? Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => PersionalScreen()),
              )
              : Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => CreateOrderScreen()),
              );
        },
        child: Icon(Icons.arrow_back_ios, color: Colors.white, size: 24),
      ),
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
              children: [
                Column(
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
                                    color:
                                        isSelected ? Colors.green : Colors.grey,
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
                            ],
                          ),
                        ),
                      ),
                    );
                  }),
                ),

                InkWell(
                  onTap: () {
                    onSave(context);
                  },
                  child: Padding(
                    padding: const EdgeInsets.symmetric(vertical: 12),
                    child: Card(
                      child: Container(
                        height: 45,
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(10),
                          gradient: LinearGradient(
                            colors: [
                              toHexToColor(primaryButtonColor),
                              Colors.blueGrey,
                            ],
                          ),
                        ),
                        child: Center(
                          child: Text(
                            widget.isChangePrimary == true
                                ? 'Đặt làm mặc định'
                                : 'Chỉnh sửa',
                            style: const TextStyle(
                              color: Colors.white,
                              fontSize: 18,
                              fontWeight: FontWeight.w600,
                            ),
                          ),
                        ),
                      ),
                    ),
                  ),
                ),
              ],
            );
          },
        );
      },
    );
  }
}
