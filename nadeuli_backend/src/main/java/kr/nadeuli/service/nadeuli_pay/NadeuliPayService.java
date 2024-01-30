package kr.nadeuli.service.nadeuli_pay;

import kr.nadeuli.category.TradeType;
import kr.nadeuli.dto.NadeuliPayHistoryDTO;
import kr.nadeuli.dto.SearchDTO;

import java.util.List;

public interface NadeuliPayService {
    List<NadeuliPayHistoryDTO> getNadeuliPayList(String tag, TradeType tradeType, SearchDTO searchDTO);

    void nadeuliPayCharge(String tag, NadeuliPayHistoryDTO nadeuliPayHistoryDTO);

    void nadeuliPayWithdraw(String tag, NadeuliPayHistoryDTO nadeuliPayHistoryDTO);

    void nadeuliPayPay(String tag, NadeuliPayHistoryDTO nadeuliPayHistoryDTO);
}
