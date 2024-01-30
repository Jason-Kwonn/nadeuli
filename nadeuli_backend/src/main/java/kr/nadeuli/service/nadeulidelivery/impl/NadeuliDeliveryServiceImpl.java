package kr.nadeuli.service.nadeulidelivery.impl;

import jakarta.persistence.EntityNotFoundException;
import kr.nadeuli.category.DeliveryState;
import kr.nadeuli.dto.DeliveryNotificationDTO;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.NadeuliDeliveryDTO;
import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.entity.DeliveryNotification;
import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.NadeuliDelivery;
import kr.nadeuli.mapper.DeliveryNotificationMapper;
import kr.nadeuli.mapper.NadeuliDeliveryMapper;
import kr.nadeuli.service.nadeulidelivery.DeliveryNotificationRepository;
import kr.nadeuli.service.nadeulidelivery.NadeuliDeliveryRepository;
import kr.nadeuli.service.nadeulidelivery.NadeuliDeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static kr.nadeuli.category.DeliveryState.ACCEPT_ORDER;

@RequiredArgsConstructor
@Log4j2
@Transactional
@Service
public class NadeuliDeliveryServiceImpl implements NadeuliDeliveryService {

    private final NadeuliDeliveryRepository nadeuliDeliveryRepository;
    private final DeliveryNotificationRepository deliveryNotificationRepository;
    private final NadeuliDeliveryMapper nadeuliDeliveryMapper;
    private final DeliveryNotificationMapper deliveryNotificationMapper;

    @Transactional
    @Override
    public NadeuliDeliveryDTO addOrUpdateDeliveryOrder(NadeuliDeliveryDTO nadeuliDeliveryDTO) throws Exception {
        // 나드리부름 게시물을 등록/수정한다.

        NadeuliDelivery nadeuliDelivery = nadeuliDeliveryMapper.nadeuliDeliveryDTOToNadeuliDelivery(nadeuliDeliveryDTO);

        // NadeuliDelivery 엔티티 저장
        NadeuliDeliveryDTO returnedNadeuliDeliveryDTO = nadeuliDeliveryMapper
                .nadeuliDeliveryToNadeuliDeliveryDTO(nadeuliDeliveryRepository.save(nadeuliDelivery));
        log.info(returnedNadeuliDeliveryDTO);

        return returnedNadeuliDeliveryDTO;
    }

    @Override
    public NadeuliDeliveryDTO getDeliveryOrder(long nadeuliDeliveryId) throws Exception {
        log.info(nadeuliDeliveryId);
        return nadeuliDeliveryRepository.findById(nadeuliDeliveryId)
                .map(nadeuliDeliveryMapper::nadeuliDeliveryToNadeuliDeliveryDTO)
                .orElse(null);
    }

    @Override
    public List<NadeuliDeliveryDTO> getDeliveryOrderList(MemberDTO memberDTO, SearchDTO searchDTO) throws Exception {
        // gu 동일한 값만 받아와야함 controller
        Sort sort = Sort.by(Sort.Direction.DESC, "regDate");
        Pageable pageable = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getPageSize(), sort);
        Page<NadeuliDelivery> deliveryOrderPage;
        String searchKeyword = searchDTO.getSearchKeyword();
        if (searchKeyword == null || searchKeyword.isEmpty()) {
            deliveryOrderPage = nadeuliDeliveryRepository.findAllByGuAndDeliveryState(memberDTO.getGu(), pageable);
        } else {
            deliveryOrderPage = nadeuliDeliveryRepository.findByKeywordAndGuAndDeliveryState(searchKeyword, memberDTO.getGu(), pageable);
        }
        log.info(deliveryOrderPage);
        return deliveryOrderPage
                .map(nadeuliDeliveryMapper::nadeuliDeliveryToNadeuliDeliveryDTO)
                .toList();
    }

//    @Override
//    public NadeuliDeliveryDTO getMyOrderHistory(long nadeuliDeliveryId) throws Exception {
//
//        // 요청 Id
//        log.info(nadeuliDeliveryId);
//        NadeuliDeliveryDTO responseDTO = nadeuliDeliveryRepository.findById(nadeuliDeliveryId)
//                .map(nadeuliDeliveryMapper::nadeuliDeliveryToNadeuliDeliveryDTO)
//                .orElse(null);
//        // 반환 DTO
//        log.info(responseDTO);
//
//        return responseDTO;
//
//    }

    @Override
    public List<NadeuliDeliveryDTO> getMyOrderHistoryList(NadeuliDeliveryDTO nadeuliDeliveryDTO, SearchDTO searchDTO) throws Exception {
        String buyerTag = nadeuliDeliveryDTO.getBuyer().getTag();

        Sort sort = Sort.by(Sort.Direction.DESC, "regDate");
        Pageable pageable = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getPageSize(), sort);
        Page<NadeuliDelivery> myOrderHistoryPage;
        // isEmpty 확인
        if (buyerTag == null || buyerTag.isEmpty()) {
            throw new NullPointerException("구매자를 찾을 수 없습니다." + nadeuliDeliveryDTO);
        } else {
            myOrderHistoryPage = nadeuliDeliveryRepository.findAllByBuyerTag(buyerTag, pageable);
        }
        List<NadeuliDeliveryDTO> returnPage = myOrderHistoryPage
                .map(nadeuliDeliveryMapper::nadeuliDeliveryToNadeuliDeliveryDTO)
                .toList();
        // 반환 DTO
        log.info(returnPage);

        return returnPage;
    }

    @Override
    public List<NadeuliDeliveryDTO> getMyDeliveryHistoryList(NadeuliDeliveryDTO nadeuliDeliveryDTO, SearchDTO searchDTO) throws Exception {
        String deliveryPersonTag = nadeuliDeliveryDTO.getDeliveryPerson().getTag();

        Sort sort = Sort.by(Sort.Direction.DESC, "regDate");
        Pageable pageable = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getPageSize(), sort);
        Page<NadeuliDelivery> myDeliveryHistoryPage;
        if (deliveryPersonTag == null || deliveryPersonTag.isEmpty()) {
            throw new NullPointerException("배송자를 찾을 수 없습니다." + nadeuliDeliveryDTO);
        } else {
            myDeliveryHistoryPage = nadeuliDeliveryRepository.findAllByDeliveryPersonTag(deliveryPersonTag, pageable);
        }
        List<NadeuliDeliveryDTO> returnPage = myDeliveryHistoryPage
                .map(nadeuliDeliveryMapper::nadeuliDeliveryToNadeuliDeliveryDTO)
                .toList();
        // 반환 DTO
        log.info(returnPage);

        return returnPage;
    }

    @Override
    public List<NadeuliDeliveryDTO> getMyAcceptedDeliveryHistoryList(NadeuliDeliveryDTO nadeuliDeliveryDTO, SearchDTO searchDTO) throws Exception {
        String deliveryPersonTag = nadeuliDeliveryDTO.getDeliveryPerson().getTag();

        Sort sort = Sort.by(Sort.Direction.DESC, "orderAcceptDate");
        Pageable pageable = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getPageSize(), sort);
        Page<NadeuliDelivery> myAcceptedDeliveryHistoryPage;
        if (deliveryPersonTag == null || deliveryPersonTag.isEmpty()) {
            throw new NullPointerException("배송자를 찾을 수 없습니다." + nadeuliDeliveryDTO);
        } else {
            myAcceptedDeliveryHistoryPage = nadeuliDeliveryRepository.findAllByDeliveryPersonTagAndDeliveryState(deliveryPersonTag, ACCEPT_ORDER, pageable);
        }
        List<NadeuliDeliveryDTO> returnPage = myAcceptedDeliveryHistoryPage
                .map(nadeuliDeliveryMapper::nadeuliDeliveryToNadeuliDeliveryDTO)
                .toList();
        // 반환 DTO
        log.info(returnPage);
        return returnPage;
    }

    @Override
    public void cancelDeliveryOrder(long nadeuliDeliveryId) throws Exception {
        NadeuliDelivery nadeuliDelivery = nadeuliDeliveryRepository.findById(nadeuliDeliveryId)
                .orElseThrow(()->(new EntityNotFoundException("nadeuliDeliveryId 가 null 입니다.")));

        if (nadeuliDelivery.getDeliveryState().equals(ACCEPT_ORDER) || nadeuliDelivery.getOrderAcceptDate() != null) {
            throw new Exception("주문이 이미 수락되어 주문 취소할 수 없습니다.");
        }

        if (nadeuliDelivery.getDeliveryState().equals(DeliveryState.DELIVERY_ORDER)) {

            nadeuliDelivery.setDeliveryState(DeliveryState.CANCEL_ORDER);
            nadeuliDelivery.setOrderCancelDate(LocalDateTime.now());
            log.info(nadeuliDelivery);

            nadeuliDeliveryRepository.save(nadeuliDelivery);
        } else throw new Exception("잘못된 접근입니다. 현재 주문 상태 : " + nadeuliDelivery.getDeliveryState());
    }

    @Override
    public void acceptDeliveryOrder(NadeuliDeliveryDTO nadeuliDeliveryDTO) throws Exception {
        NadeuliDelivery nadeuliDelivery = nadeuliDeliveryRepository.findById(nadeuliDeliveryDTO.getNadeuliDeliveryId())
                .orElseThrow(() -> new EntityNotFoundException("NadeuliDelivery not found for id: " + nadeuliDeliveryDTO.getNadeuliDeliveryId()));

        if (nadeuliDelivery.getDeliveryState().equals(DeliveryState.CANCEL_ORDER) || nadeuliDelivery.getOrderCancelDate() != null) {
            throw new Exception("주문이 이미 취소되어 주문 수락할 수 없습니다.");
        }
        nadeuliDelivery.setDeliveryState(ACCEPT_ORDER);
        nadeuliDelivery.setOrderAcceptDate(LocalDateTime.now());

        MemberDTO deliveryPersonDTO = nadeuliDeliveryDTO.getDeliveryPerson();

        if (deliveryPersonDTO != null) {
            Member deliveryPerson = nadeuliDeliveryMapper.deliveryPersonDTOToDeliveryPerson(deliveryPersonDTO);
            nadeuliDelivery.setDeliveryPerson(deliveryPerson);
            nadeuliDelivery.setDeliveryPersonNickName(deliveryPersonDTO.getNickname());
        }

        log.info("주문 상태, 주문 수락 시간 설정 후 nadeuliDelivery : " + nadeuliDelivery);
        nadeuliDeliveryRepository.save(nadeuliDelivery);

        DeliveryNotification deliveryNotification = DeliveryNotification.builder()
                .nadeuliDelivery(nadeuliDelivery)
                .notificationContent(nadeuliDelivery.getTitle() + " 이(가) 주문 수락 되었습니다.")
                .build();

        log.info("부름 알림 deliveryNotification : " + deliveryNotification);
        deliveryNotificationRepository.save(deliveryNotification);

    }


    @Override
    public void cancelDelivery(long nadeuliDeliveryId) throws Exception {
        NadeuliDelivery nadeuliDelivery = nadeuliDeliveryRepository.findById(nadeuliDeliveryId)
                .orElseThrow(()->(new EntityNotFoundException("nadeuliDeliveryId 가 null 입니다.")));

        if (nadeuliDelivery.getDeliveryState().equals(ACCEPT_ORDER)) {

            nadeuliDelivery.setDeliveryState(DeliveryState.CANCEL_DELIVERY);
            nadeuliDelivery.setDeliveryCancelDate(LocalDateTime.now());
            log.info(nadeuliDelivery);

            nadeuliDeliveryRepository.save(nadeuliDelivery);
        }

        DeliveryNotification deliveryNotification = DeliveryNotification.builder()
                .nadeuliDelivery(nadeuliDelivery)
                .notificationContent(nadeuliDelivery.getTitle() + " 이(가) 배달 취소 되었습니다.")
                .build();

        log.info("부름 알림 deliveryNotification : " + deliveryNotification);
        deliveryNotificationRepository.save(deliveryNotification);
    }

    @Override
    public void completeDelivery(long nadeuliDeliveryId) throws Exception {
        NadeuliDelivery nadeuliDelivery = nadeuliDeliveryRepository.findById(nadeuliDeliveryId)
                .orElseThrow(()->(new EntityNotFoundException("nadeuliDeliveryId 가 null 입니다.")));

        if (!nadeuliDelivery.getDeliveryState().equals(DeliveryState.CANCEL_DELIVERY) && nadeuliDelivery.getDeliveryState().equals(ACCEPT_ORDER)) {

            nadeuliDelivery.setDeliveryState(DeliveryState.COMPLETE_DELIVERY);
            nadeuliDelivery.setDeliveryCompleteDate(LocalDateTime.now());
            log.info(nadeuliDelivery);

            nadeuliDeliveryRepository.save(nadeuliDelivery);

        } else throw new Exception("주문 상태가 배달취소 거나, 주문수락이 아닙니다.");

        DeliveryNotification deliveryNotification = DeliveryNotification.builder()
                .nadeuliDelivery(nadeuliDelivery)
                .notificationContent(nadeuliDelivery.getTitle() + " 이(가) 배달 완료 되었습니다.")
                .build();

        log.info("부름 알림 deliveryNotification : " + deliveryNotification);
        deliveryNotificationRepository.save(deliveryNotification);
    }

    // 알림 삭제 추가 필요

    //    @Override
//    public List<NadeuliDeliveryDTO> getAcceptedDeliveryLocationList(NadeuliDeliveryDTO nadeuliDeliveryDTO) throws Exception {
//
//        List<NadeuliDeliveryDTO> nadeuliDeliveryLocationList = nadeuliDeliveryRepository.findByDeliveryPersonTagAndState(nadeuliDeliveryDTO.getDeliveryPerson().getTag(), ACCEPT_ORDER)
//                .stream().map(nadeuliDeliveryMapper::nadeuliDeliveryToNadeuliDeliveryDTO)
//                .toList();
//        log.info(nadeuliDeliveryLocationList);
//
//        return nadeuliDeliveryLocationList;
//    }
    @Override
    public List<DeliveryNotificationDTO> getDeliveryNotificationList(DeliveryNotificationDTO deliveryNotificationDTO, SearchDTO searchDTO) throws Exception {
        String buyerTag = deliveryNotificationDTO.getNadeuliDelivery().getBuyer().getTag();

        Sort sort = Sort.by(Sort.Direction.DESC, "regDate");
        Pageable pageable = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getPageSize(), sort);
        Page<DeliveryNotification> deliveryNotificationPage;

        // forEach 로 검증 시 buyerTag null 되는 문제는 test method 에 @Transactional 처리로 해결함.
        deliveryNotificationPage = deliveryNotificationRepository.findAllByBuyerTag(buyerTag, pageable);
        log.info(deliveryNotificationPage);

        List<DeliveryNotificationDTO> returnDeliveryNotificationPage =  deliveryNotificationPage
                .map(deliveryNotificationMapper::deliveryNotificationToDeliveryNotificationDTO)
                .toList();
        log.info(returnDeliveryNotificationPage);

        return returnDeliveryNotificationPage;
    }

    @Override
    public void updateIsRead(long deliveryNotificationId) throws Exception {

        DeliveryNotification deliveryNotification = deliveryNotificationRepository.findById(deliveryNotificationId)
                .orElseThrow(()->(new EntityNotFoundException("해당 부름 알림을 찾을 수 없습니다.")));

        deliveryNotification.setRead(true);

        log.info(deliveryNotification);
        deliveryNotificationRepository.save(deliveryNotification);

    }

    @Override
    public void deleteDeliveryNotification(long deliveryNotificationId) throws Exception {

        log.info(deliveryNotificationId);
        deliveryNotificationRepository.deleteById(deliveryNotificationId);
    }
}
