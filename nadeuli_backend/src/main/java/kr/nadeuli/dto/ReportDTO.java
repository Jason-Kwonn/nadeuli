package kr.nadeuli.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDTO {

    private Long reportId;
    private String content;
    private MemberDTO reporter;
    private PostDTO post;
    private ProductDTO product;
    private NadeuliDeliveryDTO nadeuliDelivery;
}
