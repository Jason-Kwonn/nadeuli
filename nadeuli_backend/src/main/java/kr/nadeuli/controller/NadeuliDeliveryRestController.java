package kr.nadeuli.controller;

import kr.nadeuli.dto.*;
import kr.nadeuli.service.image.ImageService;
import kr.nadeuli.service.member.MemberService;
import kr.nadeuli.service.nadeulidelivery.NadeuliDeliveryService;
import kr.nadeuli.service.product.ProductService;
import kr.nadeuli.service.trade.TradeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/nadeuli/nadeulidelivery")
public class NadeuliDeliveryRestController {

    @Value("${pageSize}")
    private int pageSize;

    private final MemberService memberService;
    private final TradeService tradeService;
    private final ProductService productService;
    private final NadeuliDeliveryService nadeuliDeliveryService;
    private final ImageService imageService;

    @Transactional
    @PostMapping("/getAddOrUpdateUsedDeliveryOrder/{tag}")
    public List<TradeScheduleDTO> getAddOrUpdateUsedDeliveryOrder(@PathVariable String tag, @RequestBody SearchDTO searchDTO) throws Exception {
        // 중고상품 선택 시, 등록된 거래 일정 리스트를 호출한다.
        log.info("/nadeulidelivery/getAddUpdateUsedDeliveryOrder : POST");
        log.info(tag);

        searchDTO.setPageSize(pageSize);
        log.info(searchDTO);

        List<TradeScheduleDTO> tradeScheduleDTOList = tradeService.getTradeScheduleList(tag, searchDTO);
        for (TradeScheduleDTO tradeScheduleDTO : tradeScheduleDTOList) {
            Long productId = tradeScheduleDTO.getProduct().getProductId();
            ProductDTO productDTO = productService.getProduct(productId, null);
            tradeScheduleDTO.setProduct(productDTO);
        }

        log.info(tradeScheduleDTOList);

        return tradeScheduleDTOList;
    }


    @Transactional
    @PostMapping("/addDeliveryOrder")
    public ResponseEntity<?> addDeliveryOrder(@RequestPart("nadeuliDeliveryDTO") NadeuliDeliveryDTO nadeuliDeliveryDTO,
                                              @RequestPart("images") List<MultipartFile> images) throws Exception{
        // 배달 주문을 등록한다.
        log.info("/nadeulidelivery/addDeliveryOrder : POST");

        // 나드리페이 잔액을 상품 금액 만큼 뺀다.
        memberService.handleNadeuliPayBalance(
                nadeuliDeliveryDTO.getBuyer().getTag(),
                null,
                nadeuliDeliveryDTO, null
        );

        // 먼저 주문 등록
        NadeuliDeliveryDTO returnedNadeuliDeliveryDTO = nadeuliDeliveryService.addOrUpdateDeliveryOrder(nadeuliDeliveryDTO);
        System.out.println(returnedNadeuliDeliveryDTO);
        ImageDTO imageDTO = ImageDTO.builder()
                    .nadeuliDelivery(returnedNadeuliDeliveryDTO)
                    .build();

        imageService.addImage(images, imageDTO);


        return ResponseEntity.ok("Delivery Order 등록 완료");
    }

    @Transactional
    @PostMapping("/updateDeliveryOrder")
    public ResponseEntity<?> updateDeliveryOrder(@RequestPart("nadeuliDeliveryDTO") NadeuliDeliveryDTO nadeuliDeliveryDTO,
                                                 @RequestPart("images") List<MultipartFile> images) throws Exception{
        // 배달 주문을 수정한다.
        log.info("/nadeulidelivery/updateDeliveryOrder : POST");
        Long nadeuliDeliveryId = nadeuliDeliveryDTO.getNadeuliDeliveryId();

        // beforeDeposit 을 balance 에 더하고 새로 받은 deposit 값 만큼 뺀다.
        Long beforeDeposit = nadeuliDeliveryService.getDeliveryOrder(nadeuliDeliveryDTO.getNadeuliDeliveryId()).getDeposit();
        memberService.handleNadeuliPayBalance(
                nadeuliDeliveryDTO.getBuyer().getTag(),
                null,
                nadeuliDeliveryDTO, beforeDeposit
        );

        // 기존 Images 삭제
        imageService.deleteNadeuliDeliveryImage(nadeuliDeliveryId);

        // 먼저 주문 등록
        nadeuliDeliveryService.addOrUpdateDeliveryOrder(nadeuliDeliveryDTO);

        ImageDTO imageDTO = ImageDTO.builder()
            .nadeuliDelivery(nadeuliDeliveryDTO)
            .build();

        // 이미지 업로드 및 저장
        imageService.addImage(images, imageDTO);

        return ResponseEntity.ok("Delivery Order 수정 완료");
    }



    @GetMapping("/getDeliveryOrder/{nadeuliDeliveryId}")
    public NadeuliDeliveryDTO getDeliveryOrder(@PathVariable long nadeuliDeliveryId) throws Exception {
        // 배달 주문을 조회한다.
        // GetMyOrderHistory, getMyDeliveryHistory 모두 getDeliveryOrder 하나로 가능하며
        // 추후 frontend 에서 본인 tag 가 buyer.tag / deliveryPerson.tag 에 따라 UI(버튼 종류 등)를 바꾸도록 한다.
        log.info("/nadeulidelivery/getDeliveryOrder : GET");
        log.info(nadeuliDeliveryId);

        return nadeuliDeliveryService.getDeliveryOrder(nadeuliDeliveryId);
    }

    @Transactional
    @PostMapping("/getDeliveryOrderList")
    public List<NadeuliDeliveryDTO> getDeliveryOrderList
            (@RequestBody MemberDTO memberDTO, @ModelAttribute SearchDTO searchDTO) throws Exception {
        // 배달 주문을 회원이 소속된 구 로 조회한다.
        log.info("/nadeulidelivery/getDeliveryOrderList : POST");
        log.info(memberDTO);

        searchDTO.setPageSize(pageSize);
        log.info(searchDTO);

        List<NadeuliDeliveryDTO> nadeuliDeliveryDTOList = nadeuliDeliveryService.getDeliveryOrderList(memberDTO, searchDTO);

        // 각 nadeuliDeliveryDTOList 에 담겨있는 nadeuliDeliveryId 로 이미지 리스트를 담는다.
        for (NadeuliDeliveryDTO newDTO : nadeuliDeliveryDTOList) {
            Long nadeuliDeliveryId = newDTO.getNadeuliDeliveryId();
            List<ImageDTO> imageDTOList = imageService.getImageList(nadeuliDeliveryId, searchDTO);
            List<String> images = imageDTOList.stream().map(ImageDTO::getImageName).toList();
            newDTO.setImages(images);
        }

        return nadeuliDeliveryDTOList;
    }

    @Transactional
    @PostMapping("/getMyOrderHistoryList")
    public List<NadeuliDeliveryDTO> getMyOrderHistoryList
            (@RequestBody NadeuliDeliveryDTO nadeuliDeliveryDTO, @ModelAttribute SearchDTO searchDTO) throws Exception {
        // 나의 주문 내역 목록을 조회한다.
        log.info("/nadeulidelivery/getMyOrderHistoryList : POST");
        log.info(nadeuliDeliveryDTO);

        searchDTO.setPageSize(pageSize);
        log.info(searchDTO);

        List<NadeuliDeliveryDTO> nadeuliDeliveryDTOList = nadeuliDeliveryService.getMyOrderHistoryList(nadeuliDeliveryDTO, searchDTO);

        // 각 nadeuliDeliveryDTOList 에 담겨있는 nadeuliDeliveryId 로 이미지 리스트를 담는다.
        for (NadeuliDeliveryDTO newDTO : nadeuliDeliveryDTOList) {
            Long nadeuliDeliveryId = newDTO.getNadeuliDeliveryId();
            List<ImageDTO> imageDTOList = imageService.getImageList(nadeuliDeliveryId, searchDTO);
            List<String> images = imageDTOList.stream().map(ImageDTO::getImageName).toList();
            newDTO.setImages(images);
        }

        return nadeuliDeliveryDTOList;
    }

    @Transactional
    @PostMapping("/getMyDeliveryHistoryList")
    public List<NadeuliDeliveryDTO> getMyDeliveryHistoryList
            (@RequestBody NadeuliDeliveryDTO nadeuliDeliveryDTO, @ModelAttribute SearchDTO searchDTO) throws Exception {
        // 나의 배달 내역 목록을 조회한다.
        log.info("/nadeulidelivery/getMyDeliveryHistoryList : POST");
        log.info(nadeuliDeliveryDTO);

        searchDTO.setPageSize(pageSize);
        log.info(searchDTO);

        List<NadeuliDeliveryDTO> nadeuliDeliveryDTOList = nadeuliDeliveryService.getMyDeliveryHistoryList(nadeuliDeliveryDTO, searchDTO);

        // 각 nadeuliDeliveryDTOList 에 담겨있는 nadeuliDeliveryId 로 이미지 리스트를 담는다.
        for (NadeuliDeliveryDTO newDTO : nadeuliDeliveryDTOList) {
            Long nadeuliDeliveryId = newDTO.getNadeuliDeliveryId();
            List<ImageDTO> imageDTOList = imageService.getImageList(nadeuliDeliveryId, searchDTO);
            List<String> images = imageDTOList.stream().map(ImageDTO::getImageName).toList();
            newDTO.setImages(images);
        }

        return nadeuliDeliveryDTOList;
    }

    @PostMapping("/getMyAcceptedDeliveryHistoryList")
    public List<NadeuliDeliveryDTO> getMyAcceptedDeliveryHistoryList
            (@RequestBody NadeuliDeliveryDTO nadeuliDeliveryDTO, SearchDTO searchDTO) throws Exception {
        // 나의 배달 시 주문 수락 내역을 목록 조회한다.
        log.info("/nadeulidelivery/getMyAcceptedDeliveryHistoryList : POST");
        log.info(nadeuliDeliveryDTO);

        searchDTO.setPageSize(pageSize);
        log.info(searchDTO);

        return nadeuliDeliveryService.getMyAcceptedDeliveryHistoryList(nadeuliDeliveryDTO, searchDTO);
    }

    @Transactional
    @GetMapping("/cancelDeliveryOrder/{nadeuliDeliveryId}")
    public ResponseEntity<?> cancelDeliveryOrder(@PathVariable long nadeuliDeliveryId) throws Exception {
        // 주문을 취소한다.
        log.info("/nadeulidelivery/cancelDeliveryOrder : GET");
        log.info(nadeuliDeliveryId);

        nadeuliDeliveryService.cancelDeliveryOrder(nadeuliDeliveryId);

        // 다시 정보를 가져와서 MemberService 로 나드리페이 잔액을 상품 금액 만큼 다시 추가한다.
        NadeuliDeliveryDTO nadeuliDeliveryDTO = nadeuliDeliveryService.getDeliveryOrder(nadeuliDeliveryId);
        log.info(nadeuliDeliveryDTO);

        memberService.handleNadeuliPayBalance(
                nadeuliDeliveryDTO.getBuyer().getTag(),
                null,
                nadeuliDeliveryDTO, null
        );

        return ResponseEntity.ok("주문 취소 완료");
    }

    @Transactional
    @PostMapping("/acceptDeliveryOrder")
    public ResponseEntity<?> acceptDeliveryOrder(@RequestBody NadeuliDeliveryDTO nadeuliDeliveryDTO) throws Exception {
        // 주문을 수락한다.
        log.info("/nadeulidelivery/acceptDeliveryOrder : POST");
        log.info(nadeuliDeliveryDTO);

        nadeuliDeliveryService.acceptDeliveryOrder(nadeuliDeliveryDTO);

        return ResponseEntity.ok("주문 수락 완료");
    }

    @Transactional
    @GetMapping("/cancelDelivery/{nadeuliDeliveryId}")
    public ResponseEntity<?> cancelDelivery(@PathVariable long nadeuliDeliveryId) throws Exception {
        // 배달을 취소한다.
        log.info("/nadeulidelivery/cancelDelivery : GET");
        log.info(nadeuliDeliveryId);

        nadeuliDeliveryService.cancelDelivery(nadeuliDeliveryId);

        // 다시 정보를 가져와서 MemberService 로 나드리페이 잔액을 상품 금액 만큼 다시 추가한다.
        NadeuliDeliveryDTO nadeuliDeliveryDTO = nadeuliDeliveryService.getDeliveryOrder(nadeuliDeliveryId);
        log.info(nadeuliDeliveryDTO);

        memberService.handleNadeuliPayBalance(
                nadeuliDeliveryDTO.getBuyer().getTag(),
                null,
                nadeuliDeliveryDTO, null
        );

        return ResponseEntity.ok("배달 취소 완료");

    }

    @Transactional
    @GetMapping("/completeDelivery/{nadeuliDeliveryId}")
    public ResponseEntity<?> completeDelivery(@PathVariable long nadeuliDeliveryId) throws Exception {
        // 배달을 완료한다.
        log.info("/nadeulidelivery/completeDelivery : GET");
        log.info(nadeuliDeliveryId);

        nadeuliDeliveryService.completeDelivery(nadeuliDeliveryId);

        // 다시 정보를 가져와서 MemberService 로 나드리페이 잔액을 상품 금액 만큼 다시 추가한다.
        NadeuliDeliveryDTO nadeuliDeliveryDTO = nadeuliDeliveryService.getDeliveryOrder(nadeuliDeliveryId);
        log.info(nadeuliDeliveryDTO);

        memberService.handleNadeuliPayBalance(
                nadeuliDeliveryDTO.getBuyer().getTag(),
                null,
                nadeuliDeliveryDTO, null
        );

        return ResponseEntity.ok("배달 완료");

    }

    @Transactional
    @PostMapping("/getDeliveryNotificationList")
    public List<DeliveryNotificationDTO> getDeliveryNotificationList
            (@RequestBody DeliveryNotificationDTO deliveryNotificationDTO, @ModelAttribute SearchDTO searchDTO) throws Exception {
        // 부름 알림 목록을 조회한다.
        log.info("/nadeulidelivery/getDeliveryNotificationList : POST");
        log.info(deliveryNotificationDTO);

        searchDTO.setPageSize(pageSize);
        log.info(searchDTO);

        // Member 의 isNadeuliDelivery 가 true 여야 알림 받을 수 있음

        return nadeuliDeliveryService.getDeliveryNotificationList(deliveryNotificationDTO, searchDTO);
    }

    @GetMapping("/updateIsRead/{deliveryNotificationId}")
    public ResponseEntity<?> updateIsRead(@PathVariable long deliveryNotificationId) throws Exception {
        // 부름 알림을 읽음 처리한다.
        log.info("/nadeulidelivery/updateIsRead : GET");
        log.info(deliveryNotificationId);

        nadeuliDeliveryService.updateIsRead(deliveryNotificationId);

        return ResponseEntity.ok("부름 알림 읽음 처리 완료");
    }

    @GetMapping("/deleteDeliveryNotification/{deliveryNotificationId}")
    public ResponseEntity<?> deleteDeliveryNotification(@PathVariable long deliveryNotificationId) throws Exception {
        // 부름 알림을 삭제한다.
        log.info("/nadeulidelivery/deleteDeliveryNotification : GET");
        log.info(deliveryNotificationId);

        nadeuliDeliveryService.deleteDeliveryNotification(deliveryNotificationId);

        return ResponseEntity.ok("부름 알림 삭제 완료");
    }


    // 해당 부분은 react 에서 nadeuliDeliveryId, tag 넘겨서 report 로 라우팅 예정
//    @GetMapping("/reportNedauliDelivery")
//    public NadeuliDeliveryDTO reportNedauliDelivery() throws Exception {
//        // 나드리부름 게시물을 신고한다.
//        log.info("/nadeulidelivery/reportNedauliDelivery : GET");
//
//        return null;
//    }

}
