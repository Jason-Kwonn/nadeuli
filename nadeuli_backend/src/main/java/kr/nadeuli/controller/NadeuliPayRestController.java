package kr.nadeuli.controller;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import kr.nadeuli.category.TradeType;
import kr.nadeuli.config.IamportConfig;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.NadeuliPayHistoryDTO;
import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.service.member.MemberService;
import kr.nadeuli.service.nadeuli_pay.NadeuliPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nadeuli/nadeuliPay")
@Log4j2
public class NadeuliPayRestController {
    private final NadeuliPayService nadeuliPayService;
    private final MemberService memberService;
    private final IamportClient iamportClient;
    private final IamportConfig iamportConfig;

    @Autowired
    public NadeuliPayRestController(NadeuliPayService nadeuliPayService, MemberService memberService, IamportConfig iamportConfig) {
        this.nadeuliPayService = nadeuliPayService;
        this.memberService = memberService;
        this.iamportClient = new IamportClient(iamportConfig.getKey(), iamportConfig.getSecret());
        this.iamportConfig = iamportConfig;
    }



    @Value("${pageSize}")
    private int pageSize;

    @Value("${iamport.api.key}")
    private String apiKey;

    @Value("${iamport.api.secret}")
    private String apiSecret;

    @GetMapping("/getNadeuliPayList/{currentPage}/{tag}")
    public List<NadeuliPayHistoryDTO> getNadeuliPayList(@PathVariable String tag, @RequestParam(required = false) String tradeType, @PathVariable int currentPage){
        SearchDTO searchDTO = SearchDTO.builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                                       .build();
        TradeType convertedTradeType = null;
        if(tradeType != null) {
            convertedTradeType = TradeType.valueOf(tradeType);
        }
        return nadeuliPayService.getNadeuliPayList(tag, convertedTradeType, searchDTO);
    }
    
    // api 사용 로직 필요
    @PostMapping("/nadeuliPayCharge")
    public ResponseEntity<String> nadeuliPayCharge(@RequestBody NadeuliPayHistoryDTO nadeuliPayHistoryDTO) throws Exception {
        IamportResponse<Payment> irsp = iamportClient.paymentByImpUid(nadeuliPayHistoryDTO.getImp_uid());
        nadeuliPayHistoryDTO.setTradingMoney(irsp.getResponse().getAmount().longValueExact());
        nadeuliPayHistoryDTO.setBankName(irsp.getResponse().getName());
        nadeuliPayHistoryDTO.setTradeType(TradeType.CHARGE);
        log.info(nadeuliPayHistoryDTO);
        if(memberService.handleNadeuliPayBalance(nadeuliPayHistoryDTO.getMember().getTag(), nadeuliPayHistoryDTO, null, null)){
            nadeuliPayService.nadeuliPayCharge(nadeuliPayHistoryDTO.getMember().getTag(), nadeuliPayHistoryDTO);
            return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
        }
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": false}");
    }

    @PostMapping("/nadeuliPayWithdraw")
    public ResponseEntity<String> nadeuliPayWithdraw(@RequestBody NadeuliPayHistoryDTO nadeuliPayHistoryDTO) throws Exception {
        nadeuliPayHistoryDTO.setTradeType(TradeType.WITHDRAW);
        if(memberService.handleNadeuliPayBalance(nadeuliPayHistoryDTO.getMember().getTag(), nadeuliPayHistoryDTO, null, null)){
            nadeuliPayService.nadeuliPayWithdraw(nadeuliPayHistoryDTO.getMember().getTag(), nadeuliPayHistoryDTO);
            return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
        }
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": false}");
    }

    @PostMapping("/nadeuliPayPay")
    public ResponseEntity<String> nadeuliPayPay(@RequestBody NadeuliPayHistoryDTO nadeuliPayHistoryDTO) throws Exception {
        nadeuliPayHistoryDTO.setTradeType(TradeType.PAYMENT);
        if(memberService.handleNadeuliPayBalance(nadeuliPayHistoryDTO.getMember().getTag(), nadeuliPayHistoryDTO, null, null)){
            nadeuliPayService.nadeuliPayPay(nadeuliPayHistoryDTO.getMember().getTag(), nadeuliPayHistoryDTO);
            return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
        }
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": false}");
    }

}
