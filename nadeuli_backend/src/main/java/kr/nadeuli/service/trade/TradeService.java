package kr.nadeuli.service.trade;

import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.dto.TradeReviewDTO;
import kr.nadeuli.dto.TradeScheduleDTO;

import java.util.List;

public interface TradeService {
    void addTradeReview(TradeReviewDTO tradeReviewDTO);

    void updateTradeReview(TradeReviewDTO tradeReviewDTO);

    List<TradeReviewDTO> getTradeReviewList(String tag, SearchDTO searchDTO);

    String deleteTradeReivew(Long tradeReviewId);

    void addTradeSchedule(TradeScheduleDTO tradeScheduleDTO);

    TradeScheduleDTO getTradeSchedule(Long tradeScheduleId);

    void updateTradeSchedule(TradeScheduleDTO tradeScheduleDTO);

    List<TradeScheduleDTO> getTradeScheduleList(String tag, SearchDTO searchDTO);

    void deleteTradeSchedule(Long tradeScheduleId);
}
