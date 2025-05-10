package com.market.MSA.services.others;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.MSA.constants.OrderStatus;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.models.order.Order;
import com.market.MSA.models.others.DeliveryInfo;
import com.market.MSA.repositories.order.OrderRepository;
import com.market.MSA.repositories.others.DeliveryInfoRepository;
import com.market.MSA.requests.goship.RatesRequest;
import com.market.MSA.requests.goship.ShipmentRequest;
import com.market.MSA.responses.goship.*;
import java.util.Date;
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
      OrderRepository orderRepository) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
    this.entityFinderService = entityFinderService;
    this.orderRepository = orderRepository;
    this.deliveryInfoRepository = deliveryInfoRepository;
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

  public List<RatesResponse> createRates(RatesRequest request) {
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
            .deliveryDate(new Date())
            .weight(request.getShipment().getParcel().getWeight())
            .width(request.getShipment().getParcel().getWidth())
            .height(request.getShipment().getParcel().getHeight())
            .length(request.getShipment().getParcel().getLength())
            .order(order)
            .build();
    deliveryInfoRepository.save(deliveryInfo);

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
}
