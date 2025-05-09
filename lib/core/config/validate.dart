class Validator {
  static String? validateName(String value) {
    if (value.trim().isEmpty) {
      return 'Tên không được để trống';
    }
    return null;
  }

  static String? validateEmail(String value) {
    if (value.trim().isEmpty) {
      return 'Email không được để trống';
    }
    if (!RegExp(r'^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$').hasMatch(value)) {
      return 'Email không hợp lệ';
    }
    return null;
  }

  static String? validatePhoneNumber(String value) {
    if (value.trim().isEmpty) {
      return 'Số điện thoại không được để trống';
    }
    if (!RegExp(r'^[0-9]{9,11}$').hasMatch(value)) {
      return 'Số điện thoại không hợp lệ';
    }
    return null;
  }

  static String? validatePassword(String value) {
    if (value.trim().isEmpty) {
      return 'Mật khẩu không được để trống';
    }
    if (value.length < 6) {
      return 'Mật khẩu phải từ 6 ký tự trở lên';
    }
    return null;
  }

  static String? validateValidPassword(String password, String validPassword) {
    if (validPassword.trim().isEmpty) {
      return 'Vui lòng nhập lại mật khẩu';
    }
    if (validPassword != password) {
      return 'Mật khẩu xác nhận không khớp';
    }
    return null;
  }

  static String? validateProvince(String value) {
    if (value.trim().isEmpty) {
      return 'Tỉnh/Thành phố không được để trống';
    }
    return null;
  }

  static String? validateDistrict(String value) {
    if (value.trim().isEmpty) {
      return 'Quận/Huyện không được để trống';
    }
    return null;
  }

  static String? validateWard(String value) {
    if (value.trim().isEmpty) {
      return 'Phường/xã không được để trống';
    }
    return null;
  }

  static String? validateStreet(String value) {
    if (value.trim().isEmpty) {
      return 'Địa chỉ/Số nhà không được để trống';
    }
    return null;
  }
}
