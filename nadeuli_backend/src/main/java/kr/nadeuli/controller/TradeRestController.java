package kr.nadeuli.controller;

import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.dto.TradeReviewDTO;
import kr.nadeuli.dto.TradeScheduleDTO;
import kr.nadeuli.service.member.MemberService;
import kr.nadeuli.service.trade.TradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nadeuli/trade")
@RequiredArgsConstructor
public class TradeRestController {
    private final TradeService tradeService;
    private final MemberService memberService;

    @Value("${pageSize}")
    private int pageSize;

    @PostMapping("/addTradeReview")
    public ResponseEntity<String> addTradeReview(@RequestBody TradeReviewDTO tradeReviewDTO) throws Exception {
        tradeService.addTradeReview(tradeReviewDTO);
        memberService.updateAffinity(tradeReviewDTO.getTrader().getTag());
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

    @PostMapping("/updateTradeReview")
    public ResponseEntity<String> updateTradeReview(@RequestBody TradeReviewDTO tradeReviewDTO) throws Exception {
        tradeService.updateTradeReview(tradeReviewDTO);
        memberService.updateAffinity(tradeReviewDTO.getTrader().getTag());
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

    @GetMapping("/getTradeReviewList/{tag}/{currentPage}")
    public List<TradeReviewDTO> getTradeReviewList(@PathVariable String tag, @PathVariable int currentPage, @RequestParam(defaultValue = "false") boolean isWriter){
        SearchDTO searchDTO = SearchDTO.builder().currentPage(currentPage).pageSize(pageSize).isWriter(isWriter).build();
        return tradeService.getTradeReviewList(tag, searchDTO);
    }

    @GetMapping("/deleteTradeReview/{tradeReviewId}")
    public ResponseEntity<String> deleteTradeReview(@PathVariable Long tradeReviewId) throws Exception {
        String tag = tradeService.deleteTradeReivew(tradeReviewId);
        memberService.updateAffinity(tag);
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

    @PostMapping("/addTradingSchedule")
    public ResponseEntity<String> addTradingSchedule(@RequestBody TradeScheduleDTO tradeScheduleDTO){
        tradeService.addTradeSchedule(tradeScheduleDTO);
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

    @GetMapping("/getTradingSchedule/{tradeScheduleId}")
    public TradeScheduleDTO getTradingSchedule(@PathVariable Long tradeScheduleId){
        return tradeService.getTradeSchedule(tradeScheduleId);
    }

    @PostMapping("/updateTradingSchedule")
    public ResponseEntity<String> updateTradingSchedule(@RequestBody TradeScheduleDTO tradeScheduleDTO){
        tradeService.updateTradeSchedule(tradeScheduleDTO);
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

    @GetMapping("/getTradingScheduleList/{tag}/{currentPage}")
    public List<TradeScheduleDTO> getTradingScheduleList(@PathVariable String tag, @PathVariable int currentPage){
        SearchDTO searchDTO = SearchDTO.builder().currentPage(currentPage).pageSize(pageSize).build();
        return tradeService.getTradeScheduleList(tag, searchDTO);
    }
}
