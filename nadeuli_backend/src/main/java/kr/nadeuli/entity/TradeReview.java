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
@ToString(exclude = {"trader", "product"})
@Table(name = "trade_review")
public class TradeReview extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trade_review_id", nullable = false)
    private Long tradeReviewId;

    @Column(name = "content", nullable = false, length = 5000)
    private String content;

    @Column(name = "affinity_score", nullable = false)
    private Long affinityScore;

    @Column(name = "member_picture", nullable = true, length = 50000)
    private String memberPicture;

    @ManyToOne
    @JoinColumn(name = "writer_tag", nullable = false)
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trader_tag", nullable = false)
    private Member trader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

}