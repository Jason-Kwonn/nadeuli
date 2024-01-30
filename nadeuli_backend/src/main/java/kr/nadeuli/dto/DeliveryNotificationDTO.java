package kr.nadeuli.dto;

import kr.nadeuli.entity.NadeuliDelivery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryNotificationDTO {

    private Long deliveryNotificationId;
    private String notificationContent;
    private boolean isRead;
    private NadeuliDeliveryDTO nadeuliDelivery;
    private String timeAgo;
}
