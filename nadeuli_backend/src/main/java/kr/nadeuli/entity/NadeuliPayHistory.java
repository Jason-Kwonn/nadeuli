package kr.nadeuli.entity;

import jakarta.persistence.*;
import kr.nadeuli.category.TradeType;
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
@ToString(exclude = {"member", "product"})
@Table(name = "nadeuli_pay_history")
public class NadeuliPayHistory extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nadeuli_pay_history_id", nullable = false)
    private Long nadeuliPayHistoryId;

    @Column(name = "trading_money", nullable = false)
    @Builder.Default
    private Long tradingMoney = 0L;

    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_account_back_num")
    private String bankAccountBackNum;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "trade_type", nullable = false)
    private TradeType tradeType;

    @Column(name = "product_title")
    private String productTitle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

}