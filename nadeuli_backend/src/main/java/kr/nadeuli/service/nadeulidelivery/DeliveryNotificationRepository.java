package kr.nadeuli.service.nadeulidelivery;

import kr.nadeuli.entity.DeliveryNotification;
import kr.nadeuli.entity.NadeuliDelivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DeliveryNotificationRepository extends JpaRepository<DeliveryNotification, Long> {

    @Query("SELECT dn FROM DeliveryNotification dn WHERE dn.nadeuliDelivery.buyer.tag = :buyerTag")
    Page<DeliveryNotification> findAllByBuyerTag(String buyerTag, Pageable pageable);
}
