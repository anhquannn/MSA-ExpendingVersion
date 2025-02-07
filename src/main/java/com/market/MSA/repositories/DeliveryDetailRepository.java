package com.market.MSA.repositories;

import com.market.MSA.models.DeliveryDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryDetailRepository extends JpaRepository<DeliveryDetail, Long> {
  List<DeliveryDetail> findByDeliveryInfo_DeliveryInfoId(Long deliveryInfoId);
}
