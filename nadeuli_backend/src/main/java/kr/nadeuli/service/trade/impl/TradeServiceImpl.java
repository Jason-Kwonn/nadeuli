package kr.nadeuli.service.trade.impl;

import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.dto.TradeReviewDTO;
import kr.nadeuli.dto.TradeScheduleDTO;
import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.TradeReview;
import kr.nadeuli.entity.TradeSchedule;
import kr.nadeuli.mapper.TradeReviewMapper;
import kr.nadeuli.mapper.TradeScheduleMapper;
import kr.nadeuli.service.trade.TradeReviewRepository;
import kr.nadeuli.service.trade.TradeScheduleRepository;
import kr.nadeuli.service.trade.TradeService;
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

@RequiredArgsConstructor
@Log4j2
@Transactional
@Service
public class TradeServiceImpl implements TradeService {
    private final TradeReviewRepository tradeReviewRepository;
    private final TradeReviewMapper tradeReviewMapper;
    private final TradeScheduleRepository tradeScheduleRepository;
    private final TradeScheduleMapper tradeScheduleMapper;

    @Override
    public void addTradeReview(TradeReviewDTO tradeReviewDTO) {
        log.info(tradeReviewDTO);
        log.info(tradeReviewMapper.tradeReviewDTOToTradeReview(tradeReviewDTO));
        tradeReviewRepository.save(tradeReviewMapper.tradeReviewDTOToTradeReview(tradeReviewDTO));
    }

    @Override
    public void updateTradeReview(TradeReviewDTO tradeReviewDTO) {
        log.info(tradeReviewDTO);
        tradeReviewRepository.save(tradeReviewMapper.tradeReviewDTOToTradeReview(tradeReviewDTO));
    }

    @Override
    public List<TradeReviewDTO> getTradeReviewList(String tag, SearchDTO searchDTO) {
        Sort sort = Sort.by(Sort.Direction.DESC, "regDate");
        Pageable pageable = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getPageSize(), sort);
        Page<TradeReview> tradeReviewPage;
        if(searchDTO.isWriter()){
            tradeReviewPage = tradeReviewRepository.findByWriter(Member.builder().tag(tag).build(), pageable);
        }else{
            tradeReviewPage = tradeReviewRepository.findByTrader(Member.builder().tag(tag).build(), pageable);
        }
        log.info(tradeReviewPage);
        return tradeReviewPage.map(tradeReviewMapper::tradeReviewToTradeReviewDTO).toList();
    }

    @Override
    public String deleteTradeReivew(Long tradeReviewId) {
        log.info(tradeReviewId);
        TradeReview tradeReview = tradeReviewRepository.findById(tradeReviewId).orElse(null);
        tradeReviewRepository.deleteById(tradeReviewId);
        if(tradeReview == null){
            return null;
        }
        return tradeReview.getTrader().getTag();
    }

    @Override
    public void addTradeSchedule(TradeScheduleDTO tradeScheduleDTO) {
        log.info(tradeScheduleDTO);
        tradeScheduleRepository.save(tradeScheduleMapper.tradeScheduleDTOToTradeSchedule(tradeScheduleDTO));
    }

    @Override
    public void updateTradeSchedule(TradeScheduleDTO tradeScheduleDTO) {
        log.info(tradeScheduleDTO);
        tradeScheduleRepository.save(tradeScheduleMapper.tradeScheduleDTOToTradeSchedule(tradeScheduleDTO));
    }

    @Override
    public List<TradeScheduleDTO> getTradeScheduleList(String tag, SearchDTO searchDTO) {
        LocalDateTime currentTIme = LocalDateTime.now();
        Sort sort = Sort.by(Sort.Direction.DESC, "tradingTime");
        Pageable pageable = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getPageSize(), sort);
        Page<TradeSchedule> tradeSchedules = tradeScheduleRepository.findTradeScheduleList(Member.builder().tag(tag).build(), currentTIme, pageable);
        log.info(tradeSchedules);
        return tradeSchedules.map(tradeScheduleMapper::tradeScheduleToTradeScheduleDTO).toList();
    }

    @Override
    public TradeScheduleDTO getTradeSchedule(Long tradeScheduleId) {
        return tradeScheduleRepository.findById(tradeScheduleId).map(tradeScheduleMapper::tradeScheduleToTradeScheduleDTO).orElse(null);
    }

    @Override
    public void deleteTradeSchedule(Long tradeScheduleId) {
        log.info(tradeScheduleId);
        tradeScheduleRepository.deleteById(tradeScheduleId);
    }
}
