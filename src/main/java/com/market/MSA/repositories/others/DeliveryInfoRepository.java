package com.market.MSA.repositories.others;

import com.market.MSA.constants.OrderStatus;
import com.market.MSA.models.others.DeliveryInfo;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeliveryInfoRepository extends JpaRepository<DeliveryInfo, Long> {

  @Query(
      "SELECT d FROM DeliveryInfo d WHERE "
          + "(:orderId IS NULL OR d.order.orderId = :orderId) AND "
          + "(:status IS NULL OR d.status = :status) AND "
          + "(:city IS NULL OR d.city = :city) AND "
          + "(:fromDate IS NULL OR d.deliveryDate >= :fromDate) AND "
          + "(:toDate IS NULL OR d.deliveryDate <= :toDate)")
  Page<DeliveryInfo> filterWithPaging(
      @Param("orderId") Long orderId,
      @Param("status") OrderStatus status,
      @Param("city") String city,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Pageable pageable);

  @Query(
      "SELECT d FROM DeliveryInfo d WHERE "
          + "(:orderId IS NULL OR d.order.orderId = :orderId) AND "
          + "(:status IS NULL OR d.status = :status) AND "
          + "(:city IS NULL OR d.city = :city) AND "
          + "(:fromDate IS NULL OR d.deliveryDate >= :fromDate) AND "
          + "(:toDate IS NULL OR d.deliveryDate <= :toDate)")
  List<DeliveryInfo> filter(
      @Param("orderId") Long orderId,
      @Param("status") OrderStatus status,
      @Param("city") String city,
      @Param("fromDate") LocalDateTime fromDate,
      @Param("toDate") LocalDateTime toDate,
      Sort sort);
}
