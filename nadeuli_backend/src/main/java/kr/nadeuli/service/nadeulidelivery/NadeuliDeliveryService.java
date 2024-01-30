package kr.nadeuli.service.nadeulidelivery;

import kr.nadeuli.dto.DeliveryNotificationDTO;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.NadeuliDeliveryDTO;
import kr.nadeuli.dto.SearchDTO;

import java.util.List;

public interface NadeuliDeliveryService {

    // 구매자의 주문 등록/수정
    NadeuliDeliveryDTO addOrUpdateDeliveryOrder(NadeuliDeliveryDTO nadeuliDeliveryDTO) throws Exception;

    // 배달 주문 조회
    NadeuliDeliveryDTO getDeliveryOrder(long nadeuliDeliveryId) throws Exception;

    // 배달 주문 목록 조회
    List<NadeuliDeliveryDTO> getDeliveryOrderList(MemberDTO memberDTO, SearchDTO searchDTO) throws Exception;

    // 구매자의 주문 내역 조회
//    NadeuliDeliveryDTO getMyOrderHistory(long nadeuliDeliveryId) throws Exception;

    // 구매자의 주문 내역 목록 조회
    List<NadeuliDeliveryDTO> getMyOrderHistoryList(NadeuliDeliveryDTO nadeuliDeliveryDTO, SearchDTO searchDTO) throws Exception;

    // 배송자의 배달 내역 조회
//    NadeuliDeliveryDTO getMyDeliveryHistory(long nadeuliDeliveryId) throws Exception;

    // 배송자의 배달 내역 목록 조회
    List<NadeuliDeliveryDTO> getMyDeliveryHistoryList(NadeuliDeliveryDTO nadeuliDeliveryDTO, SearchDTO searchDTO) throws Exception;

    // 배송자의 주문 수락 목록 조회
    List<NadeuliDeliveryDTO> getMyAcceptedDeliveryHistoryList(NadeuliDeliveryDTO nadeuliDeliveryDTO, SearchDTO searchDTO) throws Exception;

    // 구매자의 주문 취소
    void cancelDeliveryOrder(long nadeuliDeliveryId) throws Exception;

    // 배송자의 주문 수락
    void acceptDeliveryOrder(NadeuliDeliveryDTO nadeuliDeliveryDTO) throws Exception;

    // 배송자의 배달 취소
    void cancelDelivery(long nadeuliDeliveryId) throws Exception;

    // 배송자의 배달 완료
    void completeDelivery(long nadeuliDeliveryId) throws Exception;

    // 배송자의 주문 수락 장소(출발지, 도착지) 목록 조회
//    List<NadeuliDeliveryDTO> getAcceptedDeliveryLocationList(NadeuliDeliveryDTO nadeuliDeliveryDTO) throws Exception;


    // 부름 알림 서비스
    // 구매자의 부름 알림 목록 조회
    List<DeliveryNotificationDTO> getDeliveryNotificationList(DeliveryNotificationDTO deliveryNotificationDTO, SearchDTO searchDTO) throws Exception;

    // 구매자의 부름 알림 읽음 처리
    void updateIsRead(long deliveryNotificationId) throws Exception;

    // 구매자의 부름 알림 삭제 처리
    void deleteDeliveryNotification(long deliveryNotificationId) throws Exception;
}
