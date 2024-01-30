package kr.nadeuli.mapper;

import kr.nadeuli.common.CalculateTimeAgo;
import kr.nadeuli.dto.DeliveryNotificationDTO;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.NadeuliDeliveryDTO;
import kr.nadeuli.dto.ProductDTO;
import kr.nadeuli.entity.DeliveryNotification;
import kr.nadeuli.entity.Image;
import kr.nadeuli.entity.NadeuliDelivery;
import kr.nadeuli.entity.Product;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(builder = @Builder(disableBuilder = true), componentModel = "spring")
public interface DeliveryNotificationMapper {

    @Mapping(source = "nadeuliDelivery", target = "nadeuliDelivery", qualifiedByName = "nadeuliDeliveryDTOToNadeuliDelivery")
    DeliveryNotification deliveryNotificationDTOToDeliveryNotification(DeliveryNotificationDTO deliveryNotificationDTO);

    @Mapping(source = "nadeuliDelivery", target = "nadeuliDelivery", qualifiedByName = "nadeuliDeliveryToNadeuliDeliveryDTO")
    @Mapping(source = "regDate", target = "timeAgo", qualifiedByName = "regDateToTimeAgo")
    DeliveryNotificationDTO deliveryNotificationToDeliveryNotificationDTO(DeliveryNotification deliveryNotification);

    @Named("nadeuliDeliveryDTOToNadeuliDelivery")
    default NadeuliDelivery nadeuliDeliveryDTOToNadeuliDelivery(NadeuliDeliveryDTO nadeuliDelivery) {
        if (nadeuliDelivery == null) {
            return null;
        }
        return NadeuliDelivery.builder()
                .nadeuliDeliveryId(nadeuliDelivery.getNadeuliDeliveryId())
                .build();
    }

    @Named("nadeuliDeliveryToNadeuliDeliveryDTO")
    default NadeuliDeliveryDTO nadeuliDeliveryToNadeuliDeliveryDTO(NadeuliDelivery nadeuliDelivery) {
        if (nadeuliDelivery == null) {
            return null;
        }
        return NadeuliDeliveryDTO.builder()
                .nadeuliDeliveryId(nadeuliDelivery.getNadeuliDeliveryId())
                .buyer(MemberDTO.builder().tag(nadeuliDelivery.getBuyer().getTag()).build())
                .build();
    }


    @Named("regDateToTimeAgo")
    default String regDateToTimeAgo(LocalDateTime regDate){

        return CalculateTimeAgo.calculateTimeDifferenceString(regDate);
    }

}
