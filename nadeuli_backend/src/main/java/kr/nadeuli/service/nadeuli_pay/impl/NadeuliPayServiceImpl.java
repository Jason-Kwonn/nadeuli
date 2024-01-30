package kr.nadeuli.service.nadeuli_pay.impl;

import kr.nadeuli.category.TradeType;
import kr.nadeuli.dto.NadeuliPayHistoryDTO;
import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.NadeuliPayHistory;
import kr.nadeuli.mapper.NadeuliPayHistoryMapper;
import kr.nadeuli.service.nadeuli_pay.NadeuliPayHistoryRepository;
import kr.nadeuli.service.nadeuli_pay.NadeuliPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Log4j2
@Transactional
@Service
public class NadeuliPayServiceImpl implements NadeuliPayService {
    private final NadeuliPayHistoryRepository nadeuliPayHistoryRepository;
    private final NadeuliPayHistoryMapper nadeuliPayHistoryMapper;

    @Override
    public List<NadeuliPayHistoryDTO> getNadeuliPayList(String tag, TradeType tradeType, SearchDTO searchDTO) {
        Sort sort = org.springframework.data.domain.Sort.by( "regDate");
        Pageable pageable = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getPageSize(), sort);
        Page<NadeuliPayHistory> nadeuliPayHistoryPage;
        System.out.println(tradeType);
        if(tradeType == null){
            nadeuliPayHistoryPage = nadeuliPayHistoryRepository.findByMember(Member.builder().tag(tag).build(), pageable);
        }else {
            nadeuliPayHistoryPage = nadeuliPayHistoryRepository.findByMemberAndTradeType(Member.builder()
                                                                                               .tag(tag)
                                                                                               .build(), tradeType, pageable);
        }
        return nadeuliPayHistoryPage.map(nadeuliPayHistoryMapper::nadeuliPayHistoryToNadeuliPayHistoryDTO).toList();
    }

    @Override
    public void nadeuliPayCharge(String tag, NadeuliPayHistoryDTO nadeuliPayHistoryDTO) {
        log.info(nadeuliPayHistoryDTO);
        nadeuliPayHistoryRepository.save(nadeuliPayHistoryMapper.nadeuliPayHistoryDTOToNadeuliPayHistory(nadeuliPayHistoryDTO));
    }

    @Override
    public void nadeuliPayPay(String tag, NadeuliPayHistoryDTO nadeuliPayHistoryDTO) {
        log.info(nadeuliPayHistoryDTO);
        nadeuliPayHistoryRepository.save(nadeuliPayHistoryMapper.nadeuliPayHistoryDTOToNadeuliPayHistory(nadeuliPayHistoryDTO));
    }

    @Override
    public void nadeuliPayWithdraw(String tag, NadeuliPayHistoryDTO nadeuliPayHistoryDTO) {
        log.info(nadeuliPayHistoryDTO);
        nadeuliPayHistoryRepository.save(nadeuliPayHistoryMapper.nadeuliPayHistoryDTOToNadeuliPayHistory(nadeuliPayHistoryDTO));
    }
}
