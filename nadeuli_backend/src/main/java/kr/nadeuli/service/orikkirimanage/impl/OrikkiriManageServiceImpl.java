package kr.nadeuli.service.orikkirimanage.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import kr.nadeuli.dto.AnsQuestionDTO;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.OriScheMemChatFavDTO;
import kr.nadeuli.dto.OrikkiriDTO;
import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.entity.AnsQuestion;
import kr.nadeuli.entity.OriScheMemChatFav;
import kr.nadeuli.entity.Orikkiri;
import kr.nadeuli.mapper.AnsQuestionMapper;
import kr.nadeuli.mapper.OriScheMemChatFavMapper;
import kr.nadeuli.mapper.OrikkiriMapper;
import kr.nadeuli.service.orikkiri.OriScheMenChatFavRepository;
import kr.nadeuli.service.orikkirimanage.AnsQuestionRepository;
import kr.nadeuli.service.orikkirimanage.OrikkiriManageRepository;
import kr.nadeuli.service.orikkirimanage.OrikkiriManageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Transactional
@Service("OrikkiriManagerServiceImpl")
public class OrikkiriManageServiceImpl implements OrikkiriManageService {

    private final OrikkiriManageRepository orikkiriManageRepository;
    private final OrikkiriMapper orikkiriMapper;
    private final OriScheMenChatFavRepository oriScheMenChatFavRepository;
    private final OriScheMemChatFavMapper oriScheMemChatFavMapper;

    private final AnsQuestionRepository ansQuestionRepository;
    private final AnsQuestionMapper ansQuestionMapper ;

    @Override
    public List<OrikkiriDTO> allOrikkiri() throws Exception {
        List<Orikkiri> orikkiriList = orikkiriManageRepository.findAll();
        return orikkiriList.stream()
                .map(orikkiriMapper::orikkiriToOrikkiriDTO)
                .collect(Collectors.toList());
    }


    @Override
    public OrikkiriDTO addOrikkiri(OrikkiriDTO orikkiriDTO) throws Exception {
        Orikkiri orikkiri = orikkiriMapper.orikkiriDTOToOrikkiri(orikkiriDTO);
        log.info(orikkiri);

        return orikkiriMapper.orikkiriToOrikkiriDTO(orikkiriManageRepository.save(orikkiri));
    }

    @Override
    public void updateOrikkiri(OrikkiriDTO orikkiriDTO) throws Exception {
        Orikkiri orikkiri = orikkiriMapper.orikkiriDTOToOrikkiri(orikkiriDTO);
        log.info(orikkiri);
        orikkiriManageRepository.save(orikkiri);
    }

    @Override
    public OrikkiriDTO getOrikkiri(long orikkiriId) throws Exception {
        return orikkiriManageRepository.findById(orikkiriId).map(orikkiriMapper::orikkiriToOrikkiriDTO).orElse(null);
    }

    @Override
    public void deleteOrikkiri(long orikkiriId) throws Exception {
        log.info(orikkiriId);
        orikkiriManageRepository.deleteById(orikkiriId);
    }

    @Override
    public AnsQuestionDTO addAnsQuestion(AnsQuestionDTO ansQuestionDTO) throws Exception {
        AnsQuestion ansQuestion = ansQuestionMapper.ansQuestionDTOToAnsQuestion(ansQuestionDTO);
        log.info(ansQuestion);
        AnsQuestionDTO ansQuestionDTO1 = ansQuestionMapper.ansQuestionToAnsQuestionDTO(ansQuestionRepository.save(ansQuestion));
        return ansQuestionDTO1;
    }

    @Override
    public void addAns(AnsQuestionDTO ansQuestionDTO) throws Exception {
        try {
            AnsQuestion ansQuestion = ansQuestionMapper.ansQuestionDTOToAnsQuestion(ansQuestionDTO);
            log.info(ansQuestion);

            if (ansQuestion != null) {
                AnsQuestion savedAnsQuestion = ansQuestionRepository.save(ansQuestion);

                AnsQuestionDTO existAnsQuestion = ansQuestionMapper.ansQuestionToAnsQuestionDTO(savedAnsQuestion);

                if (existAnsQuestion != null) {
                    Optional<OriScheMemChatFav> oriScheMemChatFav = oriScheMenChatFavRepository.findById(existAnsQuestion.getOriScheMemChatFav().getOriScheMemChatFavId());

                    if (oriScheMemChatFav.isPresent()) {
                        log.info("존재하는 oriScheMemChatFav는 {}", oriScheMemChatFav.get());

                        // ArrayList로 변경
                        List<AnsQuestion> ansQuestionsList = new ArrayList<>();
                        ansQuestionsList.add(savedAnsQuestion);

                        // OriScheMemChatFav에 대한 관계 업데이트
                        oriScheMemChatFav.get().setAnsQuestions(ansQuestionsList);
                        savedAnsQuestion.setOriScheMemChatFav(oriScheMemChatFav.get()); // 이 부분 추가

                        log.info("존재하는 Collections {}", ansQuestionsList);

                        oriScheMenChatFavRepository.save(oriScheMemChatFav.get());
                    } else {
                        // oriScheMemChatFav가 null이면 처리할 내용 추가
                        log.error("oriScheMemChatFav is null");
                    }
                } else {
                    // existAnsQuestion이 null이면 처리할 내용 추가
                    log.error("existAnsQuestion is null");
                }
            } else {
                // ansQuestion이 null이면 처리할 내용 추가
                log.error("ansQuestion is null");
            }
        } catch (Exception e) {
            // 예외 처리, 로깅 등 필요한 작업 수행
            log.error("Error while adding Ans", e);
            throw new Exception("Error while adding Ans", e);
        }
    }

    @Override
    public OriScheMemChatFavDTO getOriScheMemChatFavDTO(Long oriScheMemChatFavId) throws Exception {
        Optional<OriScheMemChatFav> optionalOriScheMemChatFav = oriScheMenChatFavRepository.findById(oriScheMemChatFavId);

        if (optionalOriScheMemChatFav.isPresent()) {
            OriScheMemChatFav oriScheMemChatFav = optionalOriScheMemChatFav.get();
            OriScheMemChatFavDTO oriScheMemChatFavDTO = oriScheMemChatFavMapper.oriScheMemChatFavToOriScheMemChatFavDTO(oriScheMemChatFav);

            // 처리 로직 추가 (optional이 비어있지 않을 때 수행할 내용)

            return oriScheMemChatFavDTO;
        } else {
            // 처리 로직 추가 (optional이 비어있을 때 수행할 내용)
            throw new Exception("원하는 oriScheMemChatFav를 찾을 수 없습니다.");
        }
    }


    @Override
    public void updateAnsQuestion(AnsQuestionDTO ansQuestionDTO) throws Exception {
        AnsQuestion ansQuestion = ansQuestionMapper.ansQuestionDTOToAnsQuestion(ansQuestionDTO);
        log.info(ansQuestion);
        ansQuestionRepository.save(ansQuestion);
    }

    @Override
    public AnsQuestionDTO getAnsQuestion(long ansQuestionId) throws Exception {
        return ansQuestionRepository.findById(ansQuestionId).map(ansQuestionMapper::ansQuestionToAnsQuestionDTO).orElse(null);
    }

    @Override
    public List<AnsQuestionDTO> getAnsQuestionList(long orikkiriId, SearchDTO searchDTO) throws Exception {
        Pageable pageable = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getPageSize());
        Page<AnsQuestion> ansQuestionPage;
        ansQuestionPage = ansQuestionRepository.findByOrikkiriAndOriScheMemChatFavIsNull(Orikkiri.builder().orikkiriId(orikkiriId).build(), pageable);
        log.info(ansQuestionPage);
        return ansQuestionPage.map(ansQuestionMapper::ansQuestionToAnsQuestionDTO).toList();
    }

    @Override
    public void deleteAnsQuestion(long ansQuestionId) throws Exception {
        log.info(ansQuestionId);
        ansQuestionRepository.deleteById(ansQuestionId);
    }

    @Override
    public void deleteSignUp(long oriScheMemChatFavId) throws Exception {
        log.info(oriScheMemChatFavId);
        oriScheMenChatFavRepository.deleteById(oriScheMemChatFavId);
    }


}
