class City {
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

class District {
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

class Ward {
  final int id;
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
      id: json['id'] as int,
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
