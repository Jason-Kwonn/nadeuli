package kr.nadeuli.nadeulidelivery;

import jakarta.persistence.EntityNotFoundException;
import kr.nadeuli.dto.*;
import kr.nadeuli.entity.DeliveryNotification;
import kr.nadeuli.entity.NadeuliDelivery;
import kr.nadeuli.service.nadeulidelivery.NadeuliDeliveryService;
import kr.nadeuli.service.nadeulidelivery.DeliveryNotificationRepository;
import kr.nadeuli.service.nadeulidelivery.NadeuliDeliveryRepository;
import kr.nadeuli.service.trade.TradeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static kr.nadeuli.category.DeliveryState.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NadeuliDeliveryApplicationTests {

    @Autowired
    NadeuliDeliveryService nadeuliDeliveryService;
    @Autowired
    NadeuliDeliveryRepository nadeuliDeliveryRepository;
    @Autowired
    DeliveryNotificationRepository deliveryNotificationRepository;
    @Autowired
    TradeService tradeService;

    @Value("${pageSize}")
    private int pageSize;

    @Test
    public void getAddOrUpdateUsedDeliveryOrder() throws Exception {
        String tag = "WVU3";
        SearchDTO searchDTO = SearchDTO.builder()
                .currentPage(0)
                .pageSize(pageSize)
                .build();
        List<TradeScheduleDTO> tradeScheduleDTOList = tradeService.getTradeScheduleList(tag, searchDTO);
        System.out.println(tradeScheduleDTOList);

        assertNotNull(tradeScheduleDTOList);
    }

    @Test
    public void testAddOrUpdateDeliveryOrder() throws Exception {
        // 요청 값 설정
        List<String> imageList = new ArrayList<>();
        imageList.add("갈치.jpg");
        imageList.add("갈치3.jpg");
        imageList.add("갈치33.jpg");

//        ProductDTO product = ProductDTO.builder()
//                .productId(4L)
//                .build();

        NadeuliDeliveryDTO nadeuliDeliveryDTO = NadeuliDeliveryDTO.builder()
                .title("이번엔 갈치")
                .content("갈치 3박스 부탁해요")
                .productName("갈치")
                .productPrice(200000L)
                .productNum(15L)
                .deliveryFee(20000L)
                .deposit(220000L)
                .departure("서울시 관악구 장군봉5길 33")
                .arrival("서울시 관악구 청룡동 927-11")
                .deliveryState(DELIVERY_ORDER)
                .buyer(MemberDTO.builder().tag("Bss3").nickname("NicknameBss3").build())
                .images(imageList)
//                .product(product)
//                .regDate(LocalDateTime.now())
                .build();

        // method 실행
        nadeuliDeliveryService.addOrUpdateDeliveryOrder(nadeuliDeliveryDTO);

        // 엔티티 조회
//        NadeuliDelivery responseEntity = nadeuliDeliveryRepository.findByProductName(nadeuliDeliveryDTO.getProductName());

        // 검증
//        assertEquals(nadeuliDeliveryDTO.getProductName(), responseEntity.getProductName());
//        assertNotNull(responseEntity.getProductNum(), "productNum 은 반드시 있어야 함.");
//        assertEquals(product.getProductId(), responseEntity.getProduct().getProductId());
//        assertNotNull(responseEntity.getImageName(), "imageName 은 반드시 있어야 함.");

    }

//    @Test
//    public void testUpdateDeliveryOrder() throws Exception {
//        // 요청 값 설정
//        List<String> updateImageList = new ArrayList<>();
//        updateImageList.add("갈갈치.jpg");
//        updateImageList.add("갈갈치2.jpg");
//        updateImageList.add("갈갈치5.jpg");
//
//        ProductDTO product = ProductDTO.builder()
//                .productId(3L)
//                .build();
//
//        NadeuliDeliveryDTO nadeuliDeliveryDTO = NadeuliDeliveryDTO.builder()
//                .nadeuliDeliveryId(22L)
//                .title("갈갈치 배달 8282")
//                .content("갈갈치 3박스 인데, 상하기 직전입니다. 빨리 부탁합니다.")
//                .productName("갈갈치")
//                .productPrice(100000L)
//                .productNum(5L)
//                .deliveryFee(10000L)
//                .deposit(110000L)
//                .departure("서울시 관악구 장군봉5길 33")
//                .arrival("서울특별시 관악구 청룡동 927-11")
//                .deliveryState(DELIVERY_ORDER)
//                .buyer(MemberDTO.builder().tag("Bss3").nickname("NicknameBss3").build())
//                .product(product)
//                .images(updateImageList)
//                .build();
//
//        // method 실행
//        nadeuliDeliveryService.updateDeliveryOrder(nadeuliDeliveryDTO);
//    }

    @Test
    public void testGetDeliveryOrder() throws Exception{
        // 요청 값 설정
        long nadeulideliveryId = 5L;

        // method 실행
        NadeuliDeliveryDTO nadeuliDeliveryDTO = nadeuliDeliveryService.getDeliveryOrder(nadeulideliveryId);
        System.out.println(nadeuliDeliveryDTO);
    }

    @Test
    public void testGetDeliveryOrderList() throws Exception {
        // 요청 값 설정
        System.out.println();
        MemberDTO memberDTO = MemberDTO.builder()
                .gu("강남구")
                .build();

        SearchDTO searchDTO = SearchDTO.builder()
                .currentPage(0)
//                .searchKeyword("치")
                .pageSize(pageSize)
                .build();




        // method 실행
        List<NadeuliDeliveryDTO> nadeuliDeliveryDTOList = nadeuliDeliveryService.getDeliveryOrderList(memberDTO, searchDTO);
        System.out.println("nadeuliDeliveryDTOList size : " + nadeuliDeliveryDTOList.size());
    }

//    @Test
//    public void testGetMyOrderHistory() throws Exception {
//        // 요청 값 설정
//        long nadeuliDeliveryId = 5L;
//
//        // method 실행
//        NadeuliDeliveryDTO responseDTO = nadeuliDeliveryService.getMyOrderHistory(nadeuliDeliveryId);
//
//        // 검증
//        assertNotNull(responseDTO, "responseDTO 값은 null 일 수 없음.");
//        assertEquals(nadeuliDeliveryId,
//                responseDTO.getNadeuliDeliveryId(),
//                "요청 nadeuliDeliveryId 와 응답 nadeuliDeliveryId 값이 같아야 함.");
//        assertNotNull(responseDTO.getImages(), "반환된 List<String> images 값은 null 일 수 없음.");
//
//    }

    @Test
    public void testGetMyOrderHistoryList() throws Exception {
        // 요청 값 설정
        NadeuliDeliveryDTO nadeuliDeliveryDTO = NadeuliDeliveryDTO.builder()
                .buyer(MemberDTO.builder().tag("Bss3").build())
                .build();

        SearchDTO searchDTO = SearchDTO.builder()
                .currentPage(0)
                .pageSize(pageSize)
                .build();

        // method 실행
        List<NadeuliDeliveryDTO> responseDTO = nadeuliDeliveryService.getMyOrderHistoryList(nadeuliDeliveryDTO, searchDTO);

        // 검증
        assertNotNull(responseDTO, "responseDTO 값은 null 일 수 없음.");
        responseDTO.forEach(dto -> {
            assertEquals(
                    nadeuliDeliveryDTO.getBuyer().getTag()
                    , dto.getBuyer().getTag()
                    , "요청 buyer.tag 와 응답 buyer.tag 값이 같아야 함.");
            assertNotNull(dto.getImages(), "반환된 List<String> images 값은 null 일 수 없음.");
        });
        System.out.println("responseDTO.size() = " + responseDTO.size());

    }

//    @Test
//    public void testGetMyDeliveryHistory() throws Exception {
//        // 요청 값 설정
//        long nadeuliDeliveryId = 1L;
//
//        // method 실행
//        NadeuliDeliveryDTO responseDTO = nadeuliDeliveryService.getMyDeliveryHistory(nadeuliDeliveryId);
//
//        // 검증
//        assertNotNull(responseDTO, "responseDTO 값은 null 일 수 없음.");
//        assertEquals(nadeuliDeliveryId,
//                responseDTO.getNadeuliDeliveryId(),
//                "요청 nadeuliDeliveryId 와 응답 nadeuliDeliveryId 값이 같아야 함.");
//        assertNotNull(responseDTO.getImages(), "반환된 List<String> images 값은 null 일 수 없음.");
//
//    }

    @Test
    public void testGetMyDeliveryHistoryList() throws Exception {
        // 요청 값 설정
        NadeuliDeliveryDTO nadeuliDeliveryDTO = NadeuliDeliveryDTO.builder()
                .deliveryPerson(MemberDTO.builder().tag("Bss2").build())
                .build();

        SearchDTO searchDTO = SearchDTO.builder()
                .currentPage(0)
                .pageSize(pageSize)
                .build();

        // method 실행
        List<NadeuliDeliveryDTO> responseDTO = nadeuliDeliveryService.getMyDeliveryHistoryList(nadeuliDeliveryDTO, searchDTO);

        // 검증
        assertNotNull(responseDTO, "responseDTO 값은 null 일 수 없음.");
        responseDTO.forEach(dto -> {
            assertEquals(
                    nadeuliDeliveryDTO.getDeliveryPerson().getTag()
                    , dto.getDeliveryPerson().getTag()
                    , "요청 deliveryPerson.tag 와 응답 deliveryPerson.tag 값이 같아야 함.");
            assertNotNull(dto.getImages(), "반환된 List<String> images 값은 null 일 수 없음.");
        });
        System.out.println("responseDTO.size() = " + responseDTO.size());
    }

    @Test
    public void testGetMyAcceptedDeliveryHistoryList() throws Exception {
        // 요청 값 설정
        NadeuliDeliveryDTO nadeuliDeliveryDTO = NadeuliDeliveryDTO.builder()
                .deliveryPerson(MemberDTO.builder().tag("Bss2").build())
                .build();

        SearchDTO searchDTO = SearchDTO.builder()
                .currentPage(0)
                .pageSize(pageSize)
                .build();

        // method 실행
        List<NadeuliDeliveryDTO> responseDTO = nadeuliDeliveryService.getMyAcceptedDeliveryHistoryList(nadeuliDeliveryDTO, searchDTO);

        // 검증
        assertNotNull(responseDTO, "responseDTO 값은 null 일 수 없음.");
        responseDTO.forEach(dto -> {
            assertEquals(
                    nadeuliDeliveryDTO.getDeliveryPerson().getTag()
                    , dto.getDeliveryPerson().getTag()
                    , "요청 deliveryPerson.tag 와 응답 deliveryPerson.tag 값이 같아야 함.");
            assertNotNull(dto.getImages(), "반환된 List<String> images 값은 null 일 수 없음.");
            assertEquals(ACCEPT_ORDER, dto.getDeliveryState(), "반환 된 주문 상태는 2 : 주문수락 상태 여야 함.");
        });
        System.out.println("responseDTO.size() = " + responseDTO.size());
    }

    @Test
    public void testCancelDeliveryOrder() throws Exception {
        // 요청 값 설정
        long nadeuliDeliveryId = 21L;

        // method 실행
        nadeuliDeliveryService.cancelDeliveryOrder(nadeuliDeliveryId);

        // 엔티티 조회
        NadeuliDelivery nadeuliDelivery = nadeuliDeliveryRepository
                .findById(nadeuliDeliveryId)
                .orElse(null);

        // 검증
        assertNotNull(nadeuliDelivery, "NadeuliDelivery 엔티티는 null 이 아니어야 한다.");
        assertEquals(CANCEL_ORDER, nadeuliDelivery.getDeliveryState(), "deliveryState 는 1 : 주문취소 상태 여야 함.");
        assertNotNull(nadeuliDelivery.getOrderCancelDate(), "주문취소 시간은 null 이 아니어야 한다.");

    }

    @Test
    public void testAcceptDeliveryOrder() throws Exception {
        // 요청 값 설정
        NadeuliDeliveryDTO nadeuliDeliveryDTO = NadeuliDeliveryDTO.builder()
                .nadeuliDeliveryId(20L)
                .deliveryPerson(MemberDTO.builder()
                        .tag("Bss2")
                        .nickname("NicknameBss2").build())
                .build();

        // method 실행
        nadeuliDeliveryService.acceptDeliveryOrder(nadeuliDeliveryDTO);

        // 엔티티 조회
        NadeuliDelivery nadeuliDelivery = nadeuliDeliveryRepository
                .findById(nadeuliDeliveryDTO.getNadeuliDeliveryId())
                .orElse(null);

        // 검증
        assertNotNull(nadeuliDelivery, "NadeuliDelivery 엔티티는 null 이 아니어야 한다.");
        assertEquals(ACCEPT_ORDER, nadeuliDelivery.getDeliveryState(), "deliveryState 는 2 : 주문수락 상태 여야 함.");
        assertNotNull(nadeuliDelivery.getOrderAcceptDate(), "주문수락 시간은 null 이 아니어야 한다.");
    }

    @Test
    public void testCancelDelivery() throws Exception {
        // 요청 값 설정
        long nadeuliDeliveryId = 15L;

        // method 실행
        nadeuliDeliveryService.cancelDelivery(nadeuliDeliveryId);

        // 엔티티 조회
        NadeuliDelivery nadeuliDelivery = nadeuliDeliveryRepository
                .findById(nadeuliDeliveryId)
                .orElse(null);

        // 검증
        assertNotNull(nadeuliDelivery, "NadeuliDelivery 엔티티는 null 이 아니어야 한다.");
        assertEquals(CANCEL_DELIVERY, nadeuliDelivery.getDeliveryState(), "deliveryState 는 3 : 배달취소 상태 여야 함.");
        assertNotNull(nadeuliDelivery.getOrderAcceptDate(), "주문수락 시간은 null 이 아니어야 한다.");
        assertNotNull(nadeuliDelivery.getDeliveryCancelDate(), "배달취소 시간은 null 이 아니어야 한다.");
    }

    @Test
    public void testCompleteDelivery() throws Exception {
        // 요청 값 설정
        long nadeuliDeliveryId = 16L;

        // method 실행
        nadeuliDeliveryService.completeDelivery(nadeuliDeliveryId);

        // 엔티티 조회
        NadeuliDelivery nadeuliDelivery = nadeuliDeliveryRepository
                .findById(nadeuliDeliveryId)
                .orElse(null);

        // 검증
        assertNotNull(nadeuliDelivery, "NadeuliDelivery 엔티티는 null 이 아니어야 한다.");
        assertEquals(COMPLETE_DELIVERY, nadeuliDelivery.getDeliveryState(), "deliveryState 는 4 : 배달완료 상태 여야 함.");
        assertNotNull(nadeuliDelivery.getDeliveryCompleteDate(), "배달완료 시간은 null 이 아니어야 한다.");
    }

//    @Test
//    public void testGetAcceptedDeliveryLocationList() throws Exception {
//        // 요청 값 설정
//        NadeuliDeliveryDTO nadeuliDeliveryDTO = NadeuliDeliveryDTO.builder()
//                .deliveryPerson(MemberDTO.builder().tag("Bss2").build())
//                .build();
//
//        // method 실행
//        List<NadeuliDeliveryDTO> responseDTO = nadeuliDeliveryService.getAcceptedDeliveryLocationList(nadeuliDeliveryDTO);
//
//        // 검증
//        assertNotNull(responseDTO, "responseDTO 값은 null 일 수 없음.");
//        responseDTO.forEach(dto -> {
//            assertEquals(
//                    nadeuliDeliveryDTO.getDeliveryPerson().getTag()
//                    , dto.getDeliveryPerson().getTag()
//                    , "요청 deliveryPerson.tag 와 응답 deliveryPerson.tag 값이 같아야 함.");
//            assertNotNull(dto.getImages(), "반환된 List<String> images 값은 null 일 수 없음.");
//            assertEquals(ACCEPT_ORDER, dto.getDeliveryState(), "반환 된 주문 상태는 2 : 주문수락 상태 여야 함.");
//        });
//        System.out.println("responseDTO.size() = " + responseDTO.size());
//    }

    @Test
    @Transactional
    public void testGetDeliveryNotificationList() throws Exception {
        // 요청 값 설정
        NadeuliDeliveryDTO nadeuliDeliveryDTO = NadeuliDeliveryDTO.builder()
                .buyer(MemberDTO.builder().tag("WVU3").build())
                .build();

        DeliveryNotificationDTO deliveryNotificationDTO = DeliveryNotificationDTO.builder()
                .nadeuliDelivery(nadeuliDeliveryDTO)
                .build();

        SearchDTO searchDTO = SearchDTO.builder()
                .currentPage(0)
                .pageSize(pageSize)
                .build();

        // method 실행
        List<DeliveryNotificationDTO> responseDTO = nadeuliDeliveryService.getDeliveryNotificationList(deliveryNotificationDTO, searchDTO);

        // 검증
        assertNotNull(responseDTO, "responseDTO 는 null 이 될 수 없음.");
        responseDTO.forEach(dto -> {
                assertEquals(deliveryNotificationDTO.getNadeuliDelivery().getBuyer().getTag(),
                        dto.getNadeuliDelivery().getBuyer().getTag(),
                        "요청 buyer.tag 와 응답 buyer.tag 값이 같아야 함.");

        });
        System.out.println("responseDTO Lists size : " + responseDTO.size());
    }

    @Test
    public void testUpdateIsRead() throws Exception {
        // 요청 값 설정
        long deliveryNorificationId = 13L;

        // method 실행
        nadeuliDeliveryService.updateIsRead(deliveryNorificationId);

        // 엔티티 호출
        DeliveryNotification deliveryNotification = deliveryNotificationRepository
                .findById(deliveryNorificationId)
                .orElseThrow();

        // 검증
        assertNotNull(deliveryNotification.getNotificationContent(), "부름 알림 내용은 null 이 될 수 없다.");
        assertTrue(deliveryNotification.isRead());

    }

    @Test
    public void testDeleteDeliveryNotification() throws Exception {
        // 요청 값 설정
        long deliveryNotificationId = 18L;

        // method 실행
        nadeuliDeliveryService.deleteDeliveryNotification(deliveryNotificationId);

    }
}
