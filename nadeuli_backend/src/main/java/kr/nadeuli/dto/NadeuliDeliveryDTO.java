package kr.nadeuli.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.nadeuli.category.DeliveryState;
import kr.nadeuli.entity.DeliveryNotification;
import kr.nadeuli.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NadeuliDeliveryDTO {

    private Long nadeuliDeliveryId;
    private String title;
    private String content;
    private String productName;
    private Long productPrice;
    private Long productNum;
    private Long deliveryFee;
    private Long deposit;
    private String departure;
    private String arrival;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderCancelDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderAcceptDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryCancelDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveryCompleteDate;
    private DeliveryState deliveryState;
    private MemberDTO deliveryPerson;
    private MemberDTO buyer;
    private ProductDTO product;
    private List<String> images;
    @JsonIgnore
    private List<DeliveryNotification> deliveryNotifications;
    @JsonIgnore
    private List<Report> reports;
    private String timeAgo;

}
