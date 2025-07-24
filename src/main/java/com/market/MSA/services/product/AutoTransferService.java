package com.market.MSA.services.product;

import com.market.MSA.constants.ProductStatus;
import com.market.MSA.models.product.InventoryProduct;
import com.market.MSA.models.product.Transfer;
import com.market.MSA.repositories.product.InventoryProductRepository;
import com.market.MSA.repositories.product.TransferRequestRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Service that scans inventory and auto-creates transfer requests when stock is low. */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AutoTransferService {

  InventoryProductRepository inventoryProductRepository;
  TransferRequestRepository transferRequestRepository;

  @Transactional
  public void processLowStock() {
    List<InventoryProduct> lowStocks =
        inventoryProductRepository
            .filter(
                null,
                null,
                null,
                true,
                null,
                null,
                null,
                org.springframework.data.domain.Sort.unsorted())
            .stream()
            .filter(ip -> ip.getStockNumber() < ip.getMinThreshold())
            .toList();

    for (InventoryProduct ip : lowStocks) {
      // Create transfer request if not exists pending for product
      Transfer transfer = new Transfer();
      transfer.setFromInventory(ip.getInventory());
      transfer.setToInventory(ip.getInventory()); // placeholder logic
      transfer.setStatus(ProductStatus.PENDING);
      transfer.setCreatedAt(LocalDateTime.now());
      transfer.setUpdatedAt(LocalDateTime.now());
      transferRequestRepository.save(transfer);
      log.info("Auto-transfer request created for product {}", ip.getProduct().getName());
    }
  }
}
