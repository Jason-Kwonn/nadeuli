package kr.nadeuli.service.nadeuli_pay;

import kr.nadeuli.category.TradeType;
import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.NadeuliPayHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NadeuliPayHistoryRepository extends JpaRepository<NadeuliPayHistory, Long> {
    Page<NadeuliPayHistory> findByMember(Member member, Pageable pageable);

    Page<NadeuliPayHistory> findByMemberAndTradeType(Member member, TradeType tradeType, Pageable pageable);
}
