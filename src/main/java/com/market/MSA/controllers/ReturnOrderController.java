package com.market.MSA.controllers;

import com.market.MSA.requests.ReturnOrderRequest;
import com.market.MSA.responses.ReturnOrderResponse;
import com.market.MSA.services.ReturnOrderService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/returnorder")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReturnOrderController {
  ReturnOrderService returnOrderService;

  @PostMapping
  public ResponseEntity<ReturnOrderResponse> createReturnOrder(
      @RequestBody ReturnOrderRequest request) {
    return ResponseEntity.ok(returnOrderService.createReturnOrder(request));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ReturnOrderResponse> updateReturnOrder(
      @PathVariable Long id, @RequestBody ReturnOrderRequest request) {
    return ResponseEntity.ok(returnOrderService.updateReturnOrder(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteReturnOrder(@PathVariable Long id) {
    returnOrderService.deleteReturnOrder(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<ReturnOrderResponse> getReturnOrderById(@PathVariable Long id) {
    return ResponseEntity.ok(returnOrderService.getReturnOrderById(id));
  }

  @GetMapping
  public ResponseEntity<List<ReturnOrderResponse>> getAllReturnOrders() {
    return ResponseEntity.ok(returnOrderService.getAllReturnOrders());
  }
}
