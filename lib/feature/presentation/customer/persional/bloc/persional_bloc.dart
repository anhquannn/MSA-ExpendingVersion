import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/src/widgets/framework.dart';
import 'package:get_it/get_it.dart';
import 'package:intl/intl.dart';
import 'package:msa/core/config/base_bloc.dart';
import 'package:msa/core/config/config.dart';
import 'package:msa/core/config/constant.dart';
import 'package:msa/core/config/global.dart';
import 'package:msa/core/utils/prarse_color.dart';
import 'package:msa/core/utils/utility.dart';
import 'package:msa/feature/data/datasources/local/starage.dart';
import 'package:msa/feature/data/model/request/user_address_request.dart';
import 'package:msa/feature/data/model/request/user_update_request.dart';
import 'package:msa/feature/data/repositories/goship_connection.dart';
import 'package:msa/feature/domain/entities/address_model.dart';
import 'package:msa/feature/domain/entities/goship_model.dart';
import 'package:msa/feature/domain/entities/user_model.dart';
import 'package:msa/feature/domain/repositories/repository.dart';
import 'package:msa/feature/domain/usecase/user_use_case.dart';
import 'package:msa/feature/presentation/customer/persional/ui/change_password.dart';
import 'package:msa/feature/presentation/customer/persional/ui/persional_screen.dart';
import 'package:msa/feature/presentation/logins/login/ui/login_screen.dart';
import 'package:msa/feature/presentation/logins/register/ui/register_screen.dart';
import 'package:msa/widget/custom_dropdown.dart';

class PersionalBloc extends BaseBloc<PersionalScreen> {
  @override
  String get contextKey => 'PersionalScreen';
  GoshipRepository? goshipRepo;
  TextEditingController nameController = TextEditingController();
  TextEditingController emailController = TextEditingController();
  TextEditingController phoneNumberController = TextEditingController();
  TextEditingController passwordController = TextEditingController();
  TextEditingController validPasswordController = TextEditingController();
  TextEditingController provinceController = TextEditingController(); //tp
  TextEditingController districtController = TextEditingController(); //quan
  TextEditingController wardController = TextEditingController(); //xa
  TextEditingController streetController = TextEditingController(); //duong
  TextEditingController birthDayController = TextEditingController(); //duong
  TextEditingController branchController = TextEditingController();

  bool? obscurePassword;
  bool? obscureValidPassword;

  bool? errName;
  bool? errEmail;
  bool? errPhoneNumber;
  bool? errPassword;
  bool? errValidPassword;
  bool? errProvince;
  bool? errorBirthDay;
  bool? errorBranch;
  bool? errDistrict;
  bool? errStreet;
  bool? errWard;

  String? errTextName;
  String? errTextEmail;
  String? errTextPhoneNumber;
  String? errTextPassword;
  String? errTextValidPassword;
  String? errTextProvince;
  String? errTextBirthDay;
  String? errTextBranch;
  String? errTextDistrict;
  String? errTextStreet;
  String? errTextWard;

  List<Ward> listWard = [];
  List<District> listDistrict = [];
  List<City> listCity = [];

  Ward? ward;
  District? district;
  City? city;

  final UserUseCases _userUseCases = GetIt.I<UserUseCases>();

  @override
  void onDispose() {
    nameController.dispose();
    emailController.dispose();
    phoneNumberController.dispose();
    passwordController.dispose();
    validPasswordController.dispose();
    provinceController.dispose();
    districtController.dispose();
    wardController.dispose();
    streetController.dispose();
  }

  Future<void> register() async {
    validateFields();
  }

  @override
  void onInit() {
    obscurePassword = true;
    obscureValidPassword = true;

    errName = false;
    errEmail = false;
    errPhoneNumber = false;
    errPassword = false;
    errValidPassword = false;
    errProvince = false;
    errorBirthDay = false;
    errDistrict = false;
    errStreet = false;

    errTextName = '';
    errTextEmail = '';
    errTextPhoneNumber = '';
    errTextPassword = '';
    errTextValidPassword = '';
    errTextProvince = '';
    errTextDistrict = '';
    errTextStreet = '';

    streetController.text = '';
    districtController.text = '';
    provinceController.text = '';
    wardController.text = '';
    nameController.text = '';
    emailController.text = '';
    phoneNumberController.text = '';
    passwordController.text = '';
    validPasswordController.text = '';

    initData();
  }

  @override
  void onReady() async {
    // await onGetCity();
  }

  @override
  void onResumed() {}

  onUpdate(BuildContext bContext) async {
    final request = UserUpdateRequest(
      birthday: birthDayController.text,
      fullName: nameController.text,
      phoneNumber: phoneNumberController.text,
    );

    final addressRequest = UserAddressUpdateRequest(
      city: city?.name,
      cityCode: city?.id,
      district: district?.name,
      districtCode: district?.id,
      primary: true,
      street: streetController.text,
      userId: Storage.userModelGlobal?.userId,
      ward: ward?.name,
      wardCode: ward?.id,
    );

    final addressResponse = await Repository.onUpdateUserAddress(
      Storage.addressModel?.userAddressId ?? 0,
      addressRequest,
    );

    final userResponse = await Repository.onUpdateInfo(request);

    if (addressResponse && userResponse) {
      showCustomDialog(
        bContext,
        AppSize.w(0.9),
        AppSize.w(0.9),
        'Thông báo',
        Padding(
          padding: const EdgeInsets.all(16),
          child: Text(
            'Cập nhật thông tin thành công',
            style: TextStyle(color: toHexToColor(primaryTextColor)),
          ),
        ),
        true,
        false,
        Icon(Icons.check_circle, color: toHexToColor(primaryColorGreen)),
      );
    } else if (!addressResponse) {
      showCustomDialog(
        bContext,
        AppSize.w(0.9),
        AppSize.w(0.9),
        'Thông báo',
        Padding(
          padding: const EdgeInsets.all(16),
          child: Text(
            'Cập nhật địa chỉ thất bại',
            style: TextStyle(color: toHexToColor(primaryTextColor)),
          ),
        ),
        true,
        false,
        Icon(Icons.check_circle, color: toHexToColor(primaryColorGreen)),
      );
    } else {
      showCustomDialog(
        bContext,
        AppSize.w(0.9),
        AppSize.w(0.9),
        'Thông báo',
        Padding(
          padding: const EdgeInsets.all(16),
          child: Text(
            'Cập nhật thông tin thất bại',
            style: TextStyle(color: toHexToColor(primaryTextColor)),
          ),
        ),
        true,
        false,
        Icon(Icons.check_circle, color: toHexToColor(primaryColorGreen)),
      );
    }
  }

  initData() {
    UserModel model = Storage.userModelGlobal ?? UserModel();
    nameController.text = model.fullName ?? '';
    emailController.text = model.email ?? '';
    phoneNumberController.text = model.phoneNumber ?? '';
    passwordController.text = model.password ?? '';
    provinceController.text = Storage.addressModel?.city ?? '';
    districtController.text = Storage.addressModel?.district ?? '';
    wardController.text = Storage.addressModel?.ward ?? '';
    streetController.text = Storage.addressModel?.street ?? '';
    birthDayController.text = model.birthday ?? '';
    branchController.text = Storage.branchModelGlobal?.name ?? '';
  }

  onUpdateUser() {}

  onLogout(BuildContext context) async {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (context) => const LoginScreen()),
    );
    Storage.onLogout();

    // Navigator.pushAndRemoveUntil(
    //   context,
    //   MaterialPageRoute(builder: (context) => const LoginScreen()),
    //   (route) => false,
    // );
  }

  onChangePassword(BuildContext context) async {
    final isSuccess = await showDialog(
      context: context,
      builder: (context) => const ChangePasswordDialog(),
    );
    return isSuccess;
  }

  onGetCity() async {
    try {
      final data = await GoshipRepository.onGetCities();
      if (data != []) {
        listCity = data;
      }
      setState(() {});
    } catch (e) {}
  }

  onGetDistrict() async {
    try {
      final data = await GoshipRepository.onGetDistrictsApi(city?.id ?? '1');
      if (data != []) {
        listDistrict = data;
      }

      setState(() {});
    } catch (e) {}
  }

  onGetWard() async {
    try {
      final data = await GoshipRepository.onGetWardsApi(district?.id ?? '1');
      if (data != []) {
        listWard = data;
      }
      setState(() {});
    } catch (e) {}
  }

  changObscurePassword(bool value) {
    obscurePassword = value;
    setState(() {});
  }

  changValidObscurePassword(bool value) {
    obscureValidPassword = value;
    setState(() {});
  }

  showPicker(BuildContext context) async {
    final result = await pickDateTime(context);
    if (result != null) {
      birthDayController.text = result;
      print('Ngày giờ đã chọn: $result');
      // Ví dụ: 2023-06-08 15:30:00
    }
  }

  pickDateTime(BuildContext context) async {
    // Chọn ngày
    final DateTime? date = await showDatePicker(
      context: context,
      initialDate: DateTime.now(),
      firstDate: DateTime(2000),
      lastDate: DateTime(2100),
    );

    if (date == null) return null;

    // Chọn giờ
    final TimeOfDay? time = await showTimePicker(
      context: context,
      initialTime: TimeOfDay.now(),
    );

    if (time == null) return null;

    // Kết hợp ngày và giờ thành DateTime
    final DateTime dateTime = DateTime(
      date.year,
      date.month,
      date.day,
      time.hour,
      time.minute,
    );

    // Chuyển thành chuỗi theo định dạng yyyy-MM-dd HH:mm:ss
    final String formatted = DateFormat('yyyy-MM-dd HH:mm:ss').format(dateTime);

    return formatted;
  }

  showWardSelector(BuildContext context) async {
    await onGetWard();
    showDialog(
      context: context,
      builder:
          (_) => SelectorDialog<Ward>(
            items: listWard,
            title: 'Chọn Phường/Xã',
            onConfirm: (wardS) {
              ward = wardS;
              wardController.text = ward?.name ?? '';
              Navigator.of(context).pop();
              print('Đã chọn: ${wardS.name}');
            },
          ),
    );
    setState(() {});
  }

  showCitySelector(BuildContext context) async {
    await onGetCity();
    showDialog(
      context: context,
      builder:
          (_) => SelectorDialog<City>(
            items: listCity,
            title: 'Chọn Thành Phố',
            onConfirm: (dt) {
              city = dt;
              provinceController.text = city?.name ?? '';
              Navigator.of(context).pop();
              print('Đã chọn: ${dt.name}');
            },
          ),
    );
    setState(() {});
  }

  showDistrictSelector(BuildContext context) async {
    await onGetDistrict();
    showDialog(
      context: context,
      builder:
          (_) => SelectorDialog<District>(
            items: listDistrict,
            title: 'Chọn Quận/Huyện',
            onConfirm: (distric) {
              district = distric;
              districtController.text = district?.name ?? '';
              Navigator.of(context).pop();
              print('Đã chọn: ${distric.name}');
            },
          ),
    );
    await onGetWard();
    setState(() {});
  }

  validateFields() {
    bool isValid = true;
    bool validateField({
      required TextEditingController controller,
      required String errorText,
      required bool Function(String) validate,
      required ValueSetter<String> onError,
    }) {
      final text = controller.text.trim();
      if (text.isEmpty) {
        onError(errorText);
        return false;
      } else if (!validate(text)) {
        onError('Dữ liệu không hợp lệ');
        return false;
      }
      onError('');
      return true;
    }

    // Validate Name
    isValid &= validateField(
      controller: nameController,
      errorText: 'Vui lòng nhập họ tên',
      validate: (text) => text.isNotEmpty,
      onError: (error) {
        errName = error.isNotEmpty;
        errTextName = error;
      },
    );

    // Validate Email
    isValid &= validateField(
      controller: emailController,
      errorText: 'Vui lòng nhập email',
      validate:
          (text) => RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(text),
      onError: (error) {
        errEmail = error.isNotEmpty;
        errTextEmail = error;
      },
    );

    // Validate Phone Number
    isValid &= validateField(
      controller: phoneNumberController,
      errorText: 'Vui lòng nhập số điện thoại',
      validate: (text) => RegExp(r'^\d{10,11}$').hasMatch(text),
      onError: (error) {
        errPhoneNumber = error.isNotEmpty;
        errTextPhoneNumber = error;
      },
    );

    // Validate Password
    isValid &= validateField(
      controller: passwordController,
      errorText: 'Vui lòng nhập mật khẩu',
      validate: (text) => text.length >= 6,
      onError: (error) {
        errPassword = error.isNotEmpty;
        errTextPassword = error;
      },
    );

    // Validate Valid Password (confirm password)
    isValid &= validateField(
      controller: validPasswordController,
      errorText: 'Vui lòng nhập lại mật khẩu',
      validate: (text) => text == passwordController.text,
      onError: (error) {
        errValidPassword = error.isNotEmpty;
        errTextValidPassword = error;
      },
    );

    // Validate Province
    isValid &= validateField(
      controller: provinceController,
      errorText: 'Vui lòng chọn tỉnh/thành phố',
      validate: (text) => text.isNotEmpty,
      onError: (error) {
        errProvince = error.isNotEmpty;
        errTextProvince = error;
      },
    );
    isValid &= validateField(
      controller: birthDayController,
      errorText: 'Vui lòng chọn ngày sinh',
      validate: (text) => text.isNotEmpty,
      onError: (error) {
        errorBirthDay = error.isNotEmpty;
        errTextBirthDay = error;
      },
    );
    // Validate District
    isValid &= validateField(
      controller: districtController,
      errorText: 'Vui lòng chọn quận/huyện',
      validate: (text) => text.isNotEmpty,
      onError: (error) {
        errDistrict = error.isNotEmpty;
        errTextDistrict = error;
      },
    );

    // Validate Street
    isValid &= validateField(
      controller: streetController,
      errorText: 'Vui lòng nhập địa chỉ đường/số nhà',
      validate: (text) => text.isNotEmpty,
      onError: (error) {
        errStreet = error.isNotEmpty;
        errTextStreet = error;
      },
    );

    // Validate Ward
    isValid &= validateField(
      controller: wardController,
      errorText: 'Vui lòng nhập địa chỉ đường/số nhà',
      validate: (text) => text.isNotEmpty,
      onError: (error) {
        errWard = error.isNotEmpty;
        errTextWard = error;
      },
    );

    setState(() {}); // Cập nhật lại UI

    return isValid;
  }

  @override
  Widget build(BuildContext viewContext) => widget.build(viewContext);
}
