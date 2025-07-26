package com.market.MSA.services.others;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.MSA.constants.OrderStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.others.DeliveryInfo;
import com.market.MSA.models.product.Branch;
import com.market.MSA.models.user.UserAddress;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.others.DeliveryInfoRepository;
import com.market.MSA.repositories.product.BranchRepository;
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
  private final BranchRepository branchRepository;

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
      UserAddressRepository userAddressRepository,
      BranchRepository branchRepository) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
    this.entityFinderService = entityFinderService;
    this.orderRepository = orderRepository;
    this.deliveryInfoRepository = deliveryInfoRepository;
    this.userAddressRepository = userAddressRepository;
    this.branchRepository = branchRepository;
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
      log.info(responseBody.toString());
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

  public List<RatesResponse> createRates(Long branchId, Long userAddressId, double grandTotal) {
    Branch branch =
        entityFinderService.findByIdOrThrow(branchRepository, branchId, ErrorCode.BRANCH_NOT_FOUND);
    UserAddress userAddress =
        entityFinderService.findByIdOrThrow(
            userAddressRepository, userAddressId, ErrorCode.ADDRESS_NOT_FOUND);
    RatesAddressRequest addressFrom =
        RatesAddressRequest.builder()
            .city(branch.getCityCode())
            .district(branch.getDistrictCode())
            .ward(branch.getWardCode())
            .build();
    RatesAddressRequest addressTo =
        RatesAddressRequest.builder()
            .city(userAddress.getCityCode())
            .district(userAddress.getDistrictCode())
            .ward(userAddress.getWardCode())
            .build();
    RatesParcelRequest parcelRequest =
        RatesParcelRequest.builder()
            .cod(grandTotal + "")
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

  public ShipmentResponse createShipment(Long orderId, Long userAddressId, String rate) {
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
            .rate(rate)
            .payer(0)
            .address_from(addressFrom)
            .address_to(addressTo)
            .parcel(parcel)
            .build();

    ShipmentRequest request = ShipmentRequest.builder().shipment(shipmentRequest).build();

    DeliveryInfo deliveryInfo =
        DeliveryInfo.builder()
            .street(request.getShipment().getAddress_to().getStreet())
            .city(request.getShipment().getAddress_to().getCity())
            .ward(request.getShipment().getAddress_to().getWard())
            .district(request.getShipment().getAddress_to().getDistrict())
            .cod(request.getShipment().getParcel().getCod())
            .status(OrderStatus.PENDING)
            .deliveryDate(LocalDateTime.now())
            .weight(request.getShipment().getParcel().getWeight())
            .width(request.getShipment().getParcel().getWidth())
            .height(request.getShipment().getParcel().getHeight())
            .length(request.getShipment().getParcel().getLength())
            .order(order)
            .build();

    order.setStatus(OrderStatus.PENDING);

    deliveryInfoRepository.save(deliveryInfo);
    orderRepository.save(order);

    ShipmentResponse shipmentResponse =
        callApi(API_URL + "/shipments", HttpMethod.POST, request, ShipmentResponse.class);

    // Lưu mã vận đơn để phục vụ webhook update
    if (shipmentResponse != null) {
      String shipmentCode = String.valueOf(shipmentResponse.getCode());
      deliveryInfo.setShipmentCode(shipmentCode);
      deliveryInfoRepository.save(deliveryInfo);
    }

    return shipmentResponse;
  }

  // Xử lý webhook từ Goshipvoid
  public boolean processWebhook(String payload) {
    try {
      JsonNode root = objectMapper.readTree(payload);
      String shipmentCode = root.path("code").asText();
      int statusCode = root.path("status").asInt();

      DeliveryInfo deliveryInfo =
          deliveryInfoRepository
              .findByShipmentCode(shipmentCode)
              .orElseThrow(() -> new AppException(ErrorCode.DELIVERY_INFO_NOT_FOUND));

      OrderStatus newStatus = mapGoshipStatusCodeToOrderStatus(statusCode);
      deliveryInfo.setStatus(newStatus);
      deliveryInfoRepository.save(deliveryInfo);

      Order order = deliveryInfo.getOrder();
      order.setStatus(newStatus);
      if (order.getOrderDetails() != null) {
        order.getOrderDetails().forEach(od -> od.setStatus(newStatus));
      }
      orderRepository.saveAndFlush(order);
      return true;
    } catch (JsonProcessingException e) {
      throw new AppException(ErrorCode.PARSE_SHIPPO_RESPONSE_ERROR);
    }
  }

  // Ánh xạ mã trạng thái số của Goship sang OrderStatus
  OrderStatus mapGoshipStatusCodeToOrderStatus(int code) {
    return switch (code) {
      case 901, 902 -> OrderStatus.PENDING;
      case 903, 904, 907, 908 -> OrderStatus.DELIVERING;
      case 910 -> OrderStatus.SHIPPED;
      case 905, 906 -> OrderStatus.CANCELLED;
      default -> OrderStatus.PENDING;
    };
  }

  // Hàm ánh xạ trạng thái Goship sang OrderStatus
  OrderStatus mapGoshipStatusToOrderStatus(String goshipStatus) {
    return switch (goshipStatus.toUpperCase()) {
      case "PENDING" -> OrderStatus.PENDING;
      case "ACCEPTED" -> OrderStatus.DELIVERING;
      case "IN_TRANSIT" -> OrderStatus.DELIVERING;
      case "DELIVERED" -> OrderStatus.SHIPPED;
      case "CANCELED" -> OrderStatus.CANCELLED;
      default -> OrderStatus.PENDING; // Trạng thái mặc định
    };
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
    log.info(response.toString());
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
}
