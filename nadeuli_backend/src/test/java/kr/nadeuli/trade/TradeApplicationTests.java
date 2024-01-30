package kr.nadeuli.trade;

import kr.nadeuli.dto.*;
import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.Product;
import kr.nadeuli.service.trade.TradeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
public class TradeApplicationTests {
    @Autowired
    TradeService tradeService;

    @Value("${pageSize}")
    private int pageSize;


//    @Test
//    @Transactional
    public void testAddTradeReview(){
        TradeReviewDTO tradeReviewDTO = TradeReviewDTO.builder()
                .content("리뷰남겨요")
                .product(ProductDTO.builder().productId(1L).build())
                .affinityScore(30L)
                .trader(MemberDTO.builder().tag("Bsoa").build())
                .memberPicture("프사")
                .writer(MemberDTO.builder().tag("WVU3").build())
                                                      .build();
        tradeService.addTradeReview(tradeReviewDTO);
    }

    //@Test
    public void testUpdateTradeReview(){
        TradeReviewDTO tradeReviewDTO = TradeReviewDTO.builder()
                                                      .tradeReviewId(2L)
                                                      .content("리뷰남겨요요요")
                                                      .product(ProductDTO.builder().productId(1L).build())
                                                      .affinityScore(50L)
                                                      .trader(MemberDTO.builder().tag("Bsoa").build())
                                                      .memberPicture("프사")
                                                      .writer(MemberDTO.builder().tag("WVU3").build())
                                                      .build();
        tradeService.updateTradeReview(tradeReviewDTO);
    }

//    @Test
//    @Transactional
public void testGetTradeReviewList(){
    SearchDTO searchDTO = new SearchDTO();
    searchDTO.setCurrentPage(0);
    searchDTO.setPageSize(pageSize);
    String tag = "Bsoa";
    System.out.println(tradeService.getTradeReviewList(tag, searchDTO));;

}

//    @Test
    public void testDeleteTradeReview(){
        tradeService.deleteTradeReivew(4L);
    }

//    @Test
    public void testAddTradeSchedule(){
        TradeScheduleDTO tradeScheduleDTO = TradeScheduleDTO.builder()
                .tradingLocation("어디서거래하냐")
                .tradingTime(LocalDateTime.now())
                .buyer(MemberDTO.builder()
                        .tag("Bsoa")
                                .build())
                .product(ProductDTO.builder()
                        .productId(1L)
                                   .build())
                .seller(MemberDTO.builder()
                        .tag("WVU3")
                                 .build())
                                                            .build();
        tradeService.addTradeSchedule(tradeScheduleDTO);
    }

//    @Test
//    @Transactional
    public void testGetTradeSchedule(){
        System.out.println(tradeService.getTradeSchedule(1L));
    }

//    @Test
    public void testGetTradeScheduleList(){
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setPageSize(pageSize);
        String tag = "WVU3";
        System.out.println(tradeService.getTradeScheduleList(tag, searchDTO));
    }

//    @Test
    public void testUpdateTradeSchedule(){
        TradeScheduleDTO tradeScheduleDTO = TradeScheduleDTO.builder()
                .tradeScheduleId(1L)
                                                            .tradingLocation("어디서거래하냐1231")
                                                            .tradingTime(LocalDateTime.now())
                                                            .buyer(MemberDTO.builder()
                                                                            .tag("Bsoa")
                                                                            .build())
                                                            .product(ProductDTO.builder()
                                                                               .productId(1L)
                                                                               .build())
                                                            .seller(MemberDTO.builder()
                                                                             .tag("WVU3")
                                                                             .build())
                                                            .build();
        tradeService.updateTradeSchedule(tradeScheduleDTO);
    }

//    @Test
//    @Transactional
    public void testDeleteTradeSchedule(){
        tradeService.deleteTradeSchedule(1L);
    }
}
