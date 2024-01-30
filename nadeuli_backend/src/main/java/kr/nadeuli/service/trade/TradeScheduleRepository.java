package kr.nadeuli.service.trade;

import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.TradeSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface TradeScheduleRepository extends JpaRepository<TradeSchedule, Long> {
    @Query("SELECT t FROM TradeSchedule t WHERE (t.buyer = :member) AND t.tradingTime > :currentTime")
    Page<TradeSchedule> findTradeScheduleList(@Param("member") Member member, @Param("currentTime") LocalDateTime currentTime, Pageable pageable);
}
