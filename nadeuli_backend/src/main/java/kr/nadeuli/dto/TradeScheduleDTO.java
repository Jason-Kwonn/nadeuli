package kr.nadeuli.dto;

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
public class TradeScheduleDTO {
    private Long tradeScheduleId;
    private String tradingLocation;
    private LocalDateTime tradingTime;
    private MemberDTO buyer;
    private MemberDTO seller;
    private ProductDTO product;
}
