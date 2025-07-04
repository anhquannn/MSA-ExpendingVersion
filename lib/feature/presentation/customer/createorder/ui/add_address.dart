import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:msa/core/config/constant.dart';

import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/data/repositories/goship_connection.dart';
import 'package:msa/feature/domain/entities/address_model.dart';
import 'package:msa/feature/domain/entities/goship_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/change_address_screen.dart';
import 'package:msa/feature/presentation/customer/createorder/ui/create_order_screen.dart';
import 'package:msa/feature/presentation/customer/persional/ui/persional_screen.dart';
import 'package:msa/feature/presentation/logins/register/ui/register_screen.dart';
import 'package:msa/widget/custom_textfield.dart';
import 'package:msa/widget/reuseable_screen_hide_appbar.dart';

class AddAddress extends StatefulWidget {
  final bool isAdd;
  final bool? isChangePrimary;
  const AddAddress({super.key, this.isChangePrimary, this.isAdd = false});

  @override
  State<AddAddress> createState() => _AddAddressState();
}

class _AddAddressState extends State<AddAddress> {
  List<City> listCity = [];
  List<District> listDistrict = [];
  List<Ward> listWard = [];

  City? city;
  District? district;
  Ward? ward;

  final provinceController = TextEditingController();
  final districtController = TextEditingController();
  final wardController = TextEditingController();
  final streetController = TextEditingController();

  bool isDefault = false;
  bool isLoading = false;

  // Error states
  bool errProvince = false;
  bool errDistrict = false;
  bool errWard = false;
  bool errStreet = false;

  String? errTextProvince = 'Vui lòng chọn tỉnh/thành phố';
  String? errTextDistrict = 'Vui lòng chọn quận/huyện';
  String? errTextWard = 'Vui lòng chọn phường/xã';
  String? errTextStreet = 'Vui lòng nhập địa chỉ';

  @override
  void initState() {
    super.initState();
    onGetCity();
  }

  Future<void> onGetCity() async {
    try {
      final data = await GoshipRepository.onGetCities();
      if (data.isNotEmpty) {
        setState(() => listCity = data);
      }
    } catch (_) {}
  }

  Future<void> onGetDistrict() async {
    try {
      final data = await GoshipRepository.onGetDistrictsApi(city?.id ?? '0');
      if (data.isNotEmpty) {
        setState(() => listDistrict = data);
      }
    } catch (_) {}
  }

  Future<void> onGetWard() async {
    try {
      final data = await GoshipRepository.onGetWardsApi(district?.id ?? '0');
      if (data.isNotEmpty) {
        setState(() => listWard = data);
      }
    } catch (_) {}
  }

  void showCitySelector() async {
    await onGetCity();
    showDialog(
      context: context,
      builder:
          (_) => SelectorDialog<City>(
            items: listCity,
            title: 'Chọn Thành Phố',
            onConfirm: (dt) {
              setState(() {
                city = dt;
                provinceController.text = dt.name;
                district = null;
                ward = null;
                districtController.clear();
                wardController.clear();
              });
              Navigator.of(context).pop();
            },
          ),
    );
  }

  void showDistrictSelector() async {
    if (city == null) return;
    await onGetDistrict();
    showDialog(
      context: context,
      builder:
          (_) => SelectorDialog<District>(
            items: listDistrict,
            title: 'Chọn Quận/Huyện',
            onConfirm: (dt) {
              setState(() {
                district = dt;
                districtController.text = dt.name;
                ward = null;
                wardController.clear();
              });
              Navigator.of(context).pop();
            },
          ),
    );
  }

  void showWardSelector() async {
    if (district == null) return;
    await onGetWard();
    showDialog(
      context: context,
      builder:
          (_) => SelectorDialog<Ward>(
            items: listWard,
            title: 'Chọn Phường/Xã',
            onConfirm: (dt) {
              setState(() {
                ward = dt;
                wardController.text = dt.name;
              });
              Navigator.of(context).pop();
            },
          ),
    );
  }

  onGetUserAddress() async {
    final data = await Repository.getUserAddresses();
    if (data != null) {
      Storage.addressModel = data;
      Storage.saveAddress(data);
    }
  }

  void _handleSubmit(BuildContext context) async {
    setState(() {
      errProvince = provinceController.text.isEmpty;
      errDistrict = districtController.text.isEmpty;
      errWard = wardController.text.isEmpty;
      errStreet = streetController.text.isEmpty;
    });

    if (errProvince || errDistrict || errWard || errStreet) return;

    final user = Storage.userModelGlobal;
    final address = UserAddressRequest(
      cityCode: city?.id ?? '0',
      city: city?.name ?? '',
      districtCode: district?.id ?? '0',
      district: district?.name ?? '',
      wardCode: ward?.id ?? '0',
      ward: ward?.name ?? '',
      street: streetController.text,
      isPrimary: isDefault,
      userId: user?.userId ?? 0,
    );

    setState(() => isLoading = true);
    final isSuccess = await Repository.onCreateAddress(address);
    setState(() => isLoading = false);

    if (isSuccess) {
      if (isDefault) {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder:
                (context) => AddressListWidget(
                  addresses: Storage.addressModel ?? UserAddressModel(),
                ),
          ),
        );
      } else {
        widget.isChangePrimary == true
            ? Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => PersionalScreen()),
            )
            : Navigator.push(
              context,
              MaterialPageRoute(builder: (context) => CreateOrderScreen()),
            );
      }
    } else {
      ScaffoldMessenger.of(
        context,
      ).showSnackBar(const SnackBar(content: Text('Thêm địa chỉ thất bại')));
    }
  }

  @override
  Widget build(BuildContext context) {
    return CustomScaffold(
      appBarLeading: InkWell(
        onTap: () {
          if (isDefault) {
            Navigator.push(
              context,
              MaterialPageRoute(
                builder:
                    (context) => AddressListWidget(
                      addresses: Storage.addressModel ?? UserAddressModel(),
                    ),
              ),
            );
          } else {
            widget.isChangePrimary == true
                ? Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => PersionalScreen()),
                )
                : Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => CreateOrderScreen()),
                );
          }
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
        return Stack(
          children: [
            SingleChildScrollView(
              padding: const EdgeInsets.all(16).copyWith(bottom: 100),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  InkWell(
                    onTap: showCitySelector,
                    child: _buildField(
                      provinceController,
                      'Tỉnh/Thành phố',
                      'Ho Chi Minh',
                      errProvince,
                      errTextProvince,
                      enable: false,
                    ),
                  ),
                  Row(
                    children: [
                      Expanded(
                        child: InkWell(
                          onTap: showDistrictSelector,
                          child: _buildField(
                            districtController,
                            'Quận/Huyện',
                            'Quận 8',
                            errDistrict,
                            errTextDistrict,
                            enable: false,
                          ),
                        ),
                      ),
                      const SizedBox(width: 10),
                      Expanded(
                        child: InkWell(
                          onTap: showWardSelector,
                          child: _buildField(
                            wardController,
                            'Phường/Xã',
                            'Phường 4',
                            errWard,
                            errTextWard,
                            enable: false,
                          ),
                        ),
                      ),
                    ],
                  ),
                  _buildField(
                    streetController,
                    'Số nhà/địa chỉ',
                    '123 Cao Lỗ',
                    errStreet,
                    errTextStreet,
                  ),
                  const SizedBox(height: 10),
                  Row(
                    children: [
                      Checkbox(
                        activeColor: toHexToColor(primaryButtonColor),
                        value: isDefault,
                        onChanged:
                            (v) => setState(() => isDefault = v ?? false),
                      ),
                      const Text('Đặt làm địa chỉ mặc định'),
                    ],
                  ),
                  const SizedBox(height: 20),
                  SizedBox(
                    width: double.infinity,
                    child: InkWell(
                      onTap: () => _handleSubmit(context),
                      child: Container(
                        height: 45,
                        decoration: BoxDecoration(
                          color: toHexToColor(primaryButtonColor),
                          borderRadius: BorderRadius.circular(5),
                        ),
                        alignment: Alignment.center,
                        child: const Text(
                          'Lưu địa chỉ',
                          style: TextStyle(
                            color: Colors.white,
                            fontSize: 16,
                            fontWeight: FontWeight.w600,
                          ),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
            ),
            if (isLoading) const Center(child: CircularProgressIndicator()),
          ],
        );
      },
    );

    //   body: Stack(
    //     children: [
    //       SingleChildScrollView(
    //         padding: const EdgeInsets.all(16).copyWith(bottom: 100),
    //         child: Column(
    //           crossAxisAlignment: CrossAxisAlignment.start,
    //           children: [
    //             InkWell(
    //               onTap: showCitySelector,
    //               child: _buildField(
    //                 provinceController,
    //                 'Tỉnh/Thành phố',
    //                 'Ho Chi Minh',
    //                 errProvince,
    //                 errTextProvince,
    //                 enable: false,
    //               ),
    //             ),
    //             Row(
    //               children: [
    //                 Expanded(
    //                   child: InkWell(
    //                     onTap: showDistrictSelector,
    //                     child: _buildField(
    //                       districtController,
    //                       'Quận/Huyện',
    //                       'Quận 8',
    //                       errDistrict,
    //                       errTextDistrict,
    //                       enable: false,
    //                     ),
    //                   ),
    //                 ),
    //                 const SizedBox(width: 10),
    //                 Expanded(
    //                   child: InkWell(
    //                     onTap: showWardSelector,
    //                     child: _buildField(
    //                       wardController,
    //                       'Phường/Xã',
    //                       'Phường 4',
    //                       errWard,
    //                       errTextWard,
    //                       enable: false,
    //                     ),
    //                   ),
    //                 ),
    //               ],
    //             ),
    //             _buildField(
    //               streetController,
    //               'Số nhà/địa chỉ',
    //               '123 Cao Lỗ',
    //               errStreet,
    //               errTextStreet,
    //             ),
    //             const SizedBox(height: 10),
    //             Row(
    //               children: [
    //                 Checkbox(
    //                   value: isDefault,
    //                   onChanged: (v) => setState(() => isDefault = v ?? false),
    //                 ),
    //                 const Text('Đặt làm địa chỉ mặc định'),
    //               ],
    //             ),
    //             const SizedBox(height: 20),
    //             SizedBox(
    //               width: double.infinity,
    //               child: ElevatedButton(
    //                 onPressed: _handleSubmit,
    //                 child: const Text('Lưu địa chỉ'),
    //               ),
    //             )
    //           ],
    //         ),
    //       ),
    //       if (isLoading)
    //         const Center(child: CircularProgressIndicator()),
    //     ],
    //   ),
    // );
  }

  Widget _buildField(
    TextEditingController controller,
    String label,
    String hint,
    bool? hasError,
    String? errorText, {
    TextInputType? type,
    bool? enable = true,
  }) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 10),
      child: customTextField(
        typeInput: type,
        enable: enable,
        borderColors: toHexToColor(borderColorGreen),
        controller,
        hintText: hint,
        isBorder: true,
        height: 45,
        textColor: toHexToColor(primaryTextColor),
        labelText: Text(label),
        errorText: hasError == true ? errorText : null,
      ),
    );
  }
}
