package kr.nadeuli.dto;

import kr.nadeuli.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long productId;
    private String title;
    private String content;
    private String video;
    private Long viewNum;
    private Boolean isBargain;
    private Long price;
    private Boolean isSold;
    private Boolean isPremium;
    private Long premiumTime;
    private String tradingLocation;
    private String gu;
    private List<String> images;
    private String timeAgo;
    private Long likeNum;
    private Boolean isLike;
    private MemberDTO seller;
    private MemberDTO buyer;
}
