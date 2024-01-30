package kr.nadeuli.service.trade;

import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.TradeReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeReviewRepository extends JpaRepository<TradeReview, Long> {
    Page<TradeReview> findByWriter(Member writer, Pageable pageable);
    Page<TradeReview> findByTrader(Member trader, Pageable pageable);
}
