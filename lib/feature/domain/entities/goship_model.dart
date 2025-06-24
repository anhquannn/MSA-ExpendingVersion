abstract class Nameable {
  String get name;
}

class City implements Nameable {
  final String id;
  final String name;
  final List<String> supportCarriers;

  City({required this.id, required this.name, required this.supportCarriers});

  factory City.fromJson(Map<String, dynamic> json) {
    return City(
      id: json['id'] as String,
      name: json['name'] as String,
      supportCarriers: List<String>.from(json['support_carriers'] ?? []),
    );
  }

  Map<String, dynamic> toJson() {
    return {'id': id, 'name': name, 'support_carriers': supportCarriers};
  }
}

List<City> parseCities(List<dynamic> jsonList) {
  return jsonList.map((json) => City.fromJson(json)).toList();
}

class District implements Nameable {
  final String id;
  final String name;
  final String cityId;
  final List<String> supportCarriers;

  District({
    required this.id,
    required this.name,
    required this.cityId,
    required this.supportCarriers,
  });

  factory District.fromJson(Map<String, dynamic> json) {
    return District(
      id: json['id'] as String,
      name: json['name'] as String,
      cityId: json['city_id'] as String,
      supportCarriers: List<String>.from(json['support_carriers'] ?? []),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'city_id': cityId,
      'support_carriers': supportCarriers,
    };
  }
}

List<District> parseDistricts(List<dynamic> jsonList) {
  return jsonList.map((json) => District.fromJson(json)).toList();
}

class Ward implements Nameable {
  final String id;
  final String name;
  final String districtId;
  final List<String> supportCarriers;

  Ward({
    required this.id,
    required this.name,
    required this.districtId,
    required this.supportCarriers,
  });

  factory Ward.fromJson(Map<String, dynamic> json) {
    return Ward(
      id: json['id'].toString(),
      name: json['name'] as String,
      districtId: json['district_id'] as String,
      supportCarriers: List<String>.from(json['support_carriers'] ?? []),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'district_id': districtId,
      'support_carriers': supportCarriers,
    };
  }
}

List<Ward> parseWards(List<dynamic> jsonList) {
  return jsonList.map((json) => Ward.fromJson(json)).toList();
}

String parseAddressStringFromModel({
  required String cityId,
  required String cityName,
  required String districtId,
  required String districtName,
  required String wardId,
  required String wardName,
  required String address,
}) {
  return "$address, $wardName ($wardId), $districtName ($districtId), $cityName ($cityId)";
}

class AddressModel {
  final String cityId;
  final String cityName;
  final String districtId;
  final String districtName;
  final String wardId;
  final String wardName;
  final String address;

  AddressModel({
    required this.cityId,
    required this.cityName,
    required this.districtId,
    required this.districtName,
    required this.wardId,
    required this.wardName,
    required this.address,
  });
}

AddressModel parseAddressModelFromString(String addressString) {
  try {
    final parts = addressString.split(',').map((e) => e.trim()).toList();

    if (parts.length < 4) {
      throw Exception("Chuỗi địa chỉ không đúng định dạng");
    }

    final address = parts
        .sublist(0, parts.length - 3)
        .join(', '); // hỗ trợ dấu "," trong địa chỉ chi tiết
    final wardMatch = RegExp(
      r'^(.*)\s\((\d+)\)$',
    ).firstMatch(parts[parts.length - 3]);
    final districtMatch = RegExp(
      r'^(.*)\s\((\d+)\)$',
    ).firstMatch(parts[parts.length - 2]);
    final cityMatch = RegExp(
      r'^(.*)\s\((\d+)\)$',
    ).firstMatch(parts[parts.length - 1]);

    if (wardMatch == null || districtMatch == null || cityMatch == null) {
      throw Exception("Không thể parse ward/district/city");
    }

    return AddressModel(
      address: address,
      wardName: wardMatch.group(1)!,
      wardId: wardMatch.group(2)!,
      districtName: districtMatch.group(1)!,
      districtId: districtMatch.group(2)!,
      cityName: cityMatch.group(1)!,
      cityId: cityMatch.group(2)!,
    );
  } catch (e) {
    throw Exception("Lỗi parse địa chỉ: $e");
  }
}
