package kr.nadeuli.dto;

import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradeReviewDTO {
    private Long tradeReviewId;
    private String content;
    private Long affinityScore;
    private String memberPicture;
    private String timeAgo;
    private MemberDTO writer;
    private MemberDTO trader;
    private ProductDTO product;
}
