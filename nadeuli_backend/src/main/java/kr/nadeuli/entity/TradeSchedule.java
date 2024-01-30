package kr.nadeuli.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@ToString(exclude = {"buyer", "seller", "product"})
@Table(name = "trade_schedule")
public class TradeSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_schedule_id", nullable = false)
    private Long tradeScheduleId;

    @Column(name = "trading_location", nullable = false)
    private String tradingLocation;

    @Column(name = "trading_time", nullable = false)
    private LocalDateTime tradingTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_tag", nullable = false)
    private Member buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_tag", nullable = false)
    private Member seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

}