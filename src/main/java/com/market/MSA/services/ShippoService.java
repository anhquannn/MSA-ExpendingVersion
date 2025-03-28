package com.market.MSA.services;

import static com.market.MSA.requests.ShippoRequest.getDefaultAddressFrom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market.MSA.exceptions.AppException;
import com.market.MSA.exceptions.ErrorCode;
import com.market.MSA.models.DeliveryInfo;
import com.market.MSA.repositories.DeliveryInfoRepository;
import com.market.MSA.requests.AddressRequest;
import com.market.MSA.requests.ParcelRequest;
import com.market.MSA.requests.ShippoRequest;
import com.market.MSA.responses.ShippoResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ShippoService {
  @Value("${shippo.apikey}")
  private String API_KEY;

  @Value("${shippo.url}")
  private String API_URL;

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final DeliveryInfoRepository deliveryInfoRepository;

  public ShippoService(
      RestTemplate restTemplate,
      ObjectMapper objectMapper,
      DeliveryInfoRepository deliveryInfoRepository) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
    this.deliveryInfoRepository = deliveryInfoRepository;
  }

  public ShippoResponse createShippo(Long deliveryInfoId) {
    DeliveryInfo deliveryInfo =
        deliveryInfoRepository
            .findById(deliveryInfoId)
            .orElseThrow(() -> new AppException(ErrorCode.DELIVERY_INFO_NOT_FOUND));

    ShippoRequest request =
        ShippoRequest.builder()
            .address_to(
                AddressRequest.builder()
                    .name(deliveryInfo.getOrder().getUser().getFullName())
                    .street1(deliveryInfo.getStreet1())
                    .city(deliveryInfo.getCity())
                    .state(deliveryInfo.getState())
                    .zip(deliveryInfo.getZip())
                    .country(deliveryInfo.getCountry())
                    .phone(deliveryInfo.getOrder().getUser().getPhoneNumber())
                    .email(deliveryInfo.getOrder().getUser().getEmail())
                    .build())
            .address_from(getDefaultAddressFrom())
            .parcels(
                List.of(
                    ParcelRequest.builder()
                        .length("1")
                        .width("1")
                        .height("1")
                        .distance_unit("cm")
                        .weight(deliveryInfo.getWeight())
                        .mass_unit("kg")
                        .build()))
            .build();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.set("Authorization", "ShippoToken " + API_KEY);

    HttpEntity<ShippoRequest> requestEntity = new HttpEntity<>(request, headers);
    ResponseEntity<String> response =
        restTemplate.exchange(API_URL, HttpMethod.POST, requestEntity, String.class);

    try {
      String responseBody = response.getBody();
      if(responseBody != null) {
        deliveryInfo.setStatus("shipping");
        deliveryInfoRepository.save(deliveryInfo);
      }
      return objectMapper.readValue(responseBody, ShippoResponse.class);
    } catch (JsonProcessingException e) {
      throw new AppException(ErrorCode.PARSE_SHIPPO_RESPONSE_ERROR);
    }
  }
}
