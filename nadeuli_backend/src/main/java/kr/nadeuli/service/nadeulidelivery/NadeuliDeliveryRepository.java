package kr.nadeuli.service.nadeulidelivery;

import kr.nadeuli.category.DeliveryState;
import kr.nadeuli.entity.NadeuliDelivery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NadeuliDeliveryRepository extends JpaRepository<NadeuliDelivery, Long> {

    @Query("SELECT nd FROM NadeuliDelivery nd WHERE nd.buyer.gu = :gu AND nd.deliveryState = 0L AND (nd.title LIKE %:keyword% OR nd.content LIKE %:keyword%)")
    Page<NadeuliDelivery> findByKeywordAndGuAndDeliveryState(String keyword, String gu, Pageable pageable);

    Page<NadeuliDelivery> findAllByBuyerTag(String buyerTag, Pageable pageable);

    Page<NadeuliDelivery> findAllByDeliveryPersonTag(String deliveryPersonTag, Pageable pageable);

    Page<NadeuliDelivery> findAllByDeliveryPersonTagAndDeliveryState(String deliveryPersonTag, DeliveryState deliveryState, Pageable pageable);

    @Query("SELECT nd FROM NadeuliDelivery nd WHERE nd.buyer.gu = :gu AND nd.deliveryState = 0L")
    Page<NadeuliDelivery> findAllByGuAndDeliveryState(String gu, Pageable pageable);

}
