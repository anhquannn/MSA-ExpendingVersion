package com.market.MSA.services.others;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.MSA.constants.OrderStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.others.DeliveryInfo;
import com.market.MSA.models.user.UserAddress;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.others.DeliveryInfoRepository;
import com.market.MSA.repositories.user.UserAddressRepository;
import com.market.MSA.requests.goship.*;
import com.market.MSA.responses.goship.*;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class GoshipService {
  private final EntityFinderService entityFinderService;
  private final OrderRepository orderRepository;
  private final DeliveryInfoRepository deliveryInfoRepository;
  private final UserAddressRepository userAddressRepository;

  @Value("${goship.token}")
  private String TOKEN;

  @Value("${goship.url}")
  private String API_URL;

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  public GoshipService(
      RestTemplate restTemplate,
      ObjectMapper objectMapper,
      DeliveryInfoRepository deliveryInfoRepository,
      EntityFinderService entityFinderService,
      OrderRepository orderRepository,
      UserAddressRepository userAddressRepository) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
    this.entityFinderService = entityFinderService;
    this.orderRepository = orderRepository;
    this.deliveryInfoRepository = deliveryInfoRepository;
    this.userAddressRepository = userAddressRepository;
  }

  // Generic method to call API and parse response
  private <T> T callApi(String url, HttpMethod method, Object body, Class<T> responseType) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "Bearer " + TOKEN);

    try {
      String requestBody = body != null ? objectMapper.writeValueAsString(body) : null;
      HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

      ResponseEntity<String> response =
          restTemplate.exchange(url, method, requestEntity, String.class);

      String responseBody = response.getBody();
      if (responseBody == null || responseBody.trim().isEmpty()) {
        throw new AppException(ErrorCode.PARSE_SHIPPO_RESPONSE_ERROR);
      }

      return objectMapper.readValue(responseBody, responseType);
    } catch (JsonProcessingException e) {
      throw new AppException(ErrorCode.PARSE_SHIPPO_RESPONSE_ERROR);
    }
  }

  public List<CityResponse> getCities() {
    CityApiResponse response =
        callApi(API_URL + "/cities", HttpMethod.GET, null, CityApiResponse.class);
    return response.getData();
  }

  public List<DistrictResponse> getDistricts(int code) {
    DistrictApiResponse response =
        callApi(
            API_URL + "/cities/" + code + "/districts",
            HttpMethod.GET,
            null,
            DistrictApiResponse.class);
    return response.getData();
  }

  public List<WardResponse> getWards(int code) {
    WardApiResponse response =
        callApi(
            API_URL + "/districts/" + code + "/wards", HttpMethod.GET, null, WardApiResponse.class);
    return response.getData();
  }

  public List<RatesResponse> createRates(Long orderId, Long userAddressId) {
    Order order =
        entityFinderService.findByIdOrThrow(orderRepository, orderId, ErrorCode.ORDER_NOT_FOUND);
    UserAddress userAddress =
        entityFinderService.findByIdOrThrow(
            userAddressRepository, userAddressId, ErrorCode.ADDRESS_NOT_FOUND);
    RatesAddressRequest addressFrom =
        RatesAddressRequest.builder()
            .city(order.getBranch().getCityCode())
            .district(order.getBranch().getDistrictCode())
            .ward(order.getBranch().getWardCode())
            .build();
    RatesAddressRequest addressTo =
        RatesAddressRequest.builder()
            .city(userAddress.getCityCode())
            .district(userAddress.getDistrictCode())
            .ward(userAddress.getWardCode())
            .build();
    RatesParcelRequest parcelRequest =
        RatesParcelRequest.builder()
            .cod(order.getGrandTotal() + "")
            .height("15")
            .length("15")
            .width("15")
            .weight("10")
            .build();
    RatesApiRequest apiRequest =
        RatesApiRequest.builder()
            .address_from(addressFrom)
            .address_to(addressTo)
            .parcel(parcelRequest)
            .build();
    RatesRequest request = RatesRequest.builder().shipment(apiRequest).build();

    RatesApiResponse response =
        callApi(API_URL + "/rates", HttpMethod.POST, request, RatesApiResponse.class);
    return response.getData();
  }

  public ShipmentResponse createShipment(ShipmentRequest request, Long orderId) {
    Order order =
        entityFinderService.findByIdOrThrow(orderRepository, orderId, ErrorCode.ORDER_NOT_FOUND);

    DeliveryInfo deliveryInfo =
        DeliveryInfo.builder()
            .street(request.getShipment().getAddress_to().getStreet())
            .city(request.getShipment().getAddress_to().getCity())
            .ward(request.getShipment().getAddress_to().getWard())
            .district(request.getShipment().getAddress_to().getDistrict())
            .cod(request.getShipment().getParcel().getCod())
            .status(OrderStatus.ORDER_STATUS_1.getStatus())
            .deliveryDate(LocalDateTime.now())
            .weight(request.getShipment().getParcel().getWeight())
            .width(request.getShipment().getParcel().getWidth())
            .height(request.getShipment().getParcel().getHeight())
            .length(request.getShipment().getParcel().getLength())
            .order(order)
            .build();

    order.setStatus(OrderStatus.ORDER_STATUS_4.getStatus());

    deliveryInfoRepository.save(deliveryInfo);
    orderRepository.save(order);

    return callApi(API_URL + "/shipments", HttpMethod.POST, request, ShipmentResponse.class);
  }

  public List<ShipmentDetailResponse> getAllShipments() {
    ShipmentListResponse response =
        callApi(API_URL + "/shipments", HttpMethod.GET, null, ShipmentListResponse.class);
    return response.getData();
  }

  public List<ShipmentDetailResponse> searchShipmentsByCode(String code) {
    ShipmentListResponse response =
        callApi(
            API_URL + "/shipments/search?code=" + code,
            HttpMethod.GET,
            null,
            ShipmentListResponse.class);
    return response.getData();
  }

  public List<ShipmentDetailResponse> searchShipmentsByTimeRange(Integer from, Integer to) {
    String url = API_URL + "/shipments";
    if (from != null && to != null) {
      url += "?from=" + from + "&to=" + to;
    }
    ShipmentListResponse response = callApi(url, HttpMethod.GET, null, ShipmentListResponse.class);
    return response.getData();
  }

  public ShipmentResponse createShipmentWithDefaultRate(Long orderId, Long userAddressId) {
    // First, get the rates
    List<RatesResponse> rates = createRates(orderId, userAddressId);
    if (rates == null || rates.isEmpty()) {
      throw new AppException(ErrorCode.RATES_NOT_FOUND);
    }

    // Get the first rate
    RatesResponse firstRate = rates.getFirst();

    // Get order and user address
    Order order =
        entityFinderService.findByIdOrThrow(orderRepository, orderId, ErrorCode.ORDER_NOT_FOUND);
    UserAddress userAddress =
        entityFinderService.findByIdOrThrow(
            userAddressRepository, userAddressId, ErrorCode.ADDRESS_NOT_FOUND);

    // Create address from (branch address)
    AddressRequest addressFrom =
        AddressRequest.builder()
            .name(order.getBranch().getName())
            .phone(order.getBranch().getPhone())
            .street(order.getBranch().getStreet())
            .ward(order.getBranch().getWardCode())
            .district(order.getBranch().getDistrictCode())
            .city(order.getBranch().getCityCode())
            .build();

    // Create address to (user address)
    AddressRequest addressTo =
        AddressRequest.builder()
            .name(userAddress.getUser().getFullName())
            .phone(userAddress.getUser().getPhoneNumber())
            .street(userAddress.getStreet())
            .ward(userAddress.getWardCode())
            .district(userAddress.getDistrictCode())
            .city(userAddress.getCityCode())
            .build();

    // Create parcel
    ParcelRequest parcel =
        ParcelRequest.builder()
            .cod(order.getGrandTotal() + "")
            .height("15")
            .length("15")
            .width("15")
            .weight("10")
            .metadata("Hàng dễ vỡ, xin nhẹ tay")
            .build();

    // Create shipment request
    ShipmentApiRequest shipmentRequest =
        ShipmentApiRequest.builder()
            .rate(firstRate.getId())
            .payer(0)
            .address_from(addressFrom)
            .address_to(addressTo)
            .parcel(parcel)
            .build();

    ShipmentRequest request = ShipmentRequest.builder().shipment(shipmentRequest).build();

    // Create and return the shipment
    return createShipment(request, orderId);
  }
}
