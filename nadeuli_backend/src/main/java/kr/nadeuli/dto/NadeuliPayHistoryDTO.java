package kr.nadeuli.dto;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import kr.nadeuli.category.TradeType;
import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NadeuliPayHistoryDTO {
    private Long nadeuliPayHistoryId;
    private Long tradingMoney;
    private String bankName;
    private String bankAccountBackNum;
    private TradeType tradeType;
    private String productTitle;
    private LocalDateTime regDate;
    private String imp_uid;
    private MemberDTO member;
    private ProductDTO product;

}
