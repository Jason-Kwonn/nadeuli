package kr.nadeuli.service.orikkiri.impl;

import jakarta.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import kr.nadeuli.dto.AnsQuestionDTO;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.OriScheMemChatFavDTO;
import kr.nadeuli.dto.OrikkiriScheduleDTO;
import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.entity.*;
import kr.nadeuli.mapper.AnsQuestionMapper;
import kr.nadeuli.mapper.MemberMapper;
import kr.nadeuli.mapper.OriScheMemChatFavMapper;
import kr.nadeuli.mapper.OrikkiriScheduleMapper;
import kr.nadeuli.service.member.MemberService;
import kr.nadeuli.service.orikkiri.OriScheMenChatFavRepository;
import kr.nadeuli.service.orikkiri.OrikkiriScheduleRepository;
import kr.nadeuli.service.orikkiri.OrikkiriService;
import kr.nadeuli.service.orikkirimanage.AnsQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Log4j2
@Transactional
@Service("orikkiriServiceImpl")
public class OrikkiriServiceImpl implements OrikkiriService {
    
    private final EntityManager entityManager;

    private final OriScheMenChatFavRepository oriScheMenChatFavRepository;
    private final AnsQuestionRepository ansQuestionRepository;
    private final AnsQuestionMapper ansQuestionMapper;
    private final OriScheMemChatFavMapper oriScheMemChatFavMapper;
    private final MemberService memberService;
    private final MemberMapper memberMapper;

    private final OrikkiriScheduleRepository orikkiriScheduleRepository;
    private final OrikkiriScheduleMapper orikkiriScheduleMapper;


    @Override
    public OriScheMemChatFavDTO addOrikkrirSignUp(OriScheMemChatFavDTO oriScheMemChatFavDTO) throws Exception {
        OriScheMemChatFav oriScheMemChatFav = oriScheMemChatFavMapper.oriScheMemChatFavDTOToOriScheMemChatFav(oriScheMemChatFavDTO);
        log.info("받은 oriScheMemChatFavDTO는{}",oriScheMemChatFavDTO);
        log.info("변환한 OriScheMemChatFav는{}",oriScheMemChatFav);

        List<AnsQuestion> ansQuestions = oriScheMemChatFavDTO.getAnsQuestions().stream()
            .map(ansQuestionDTO -> entityManager.getReference(AnsQuestion.class, ansQuestionDTO.getAnsQuestionId()))
            .collect(Collectors.toList());
        oriScheMemChatFav.setAnsQuestions(ansQuestions);
        OriScheMemChatFavDTO existOriScheMemChatFavDTO = oriScheMemChatFavMapper.oriScheMemChatFavToOriScheMemChatFavDTO(oriScheMenChatFavRepository.save(oriScheMemChatFav));
        return existOriScheMemChatFavDTO;
    }

    @Override
    public List<Map<String, Object>> getOrikkiriSignUpList(long orikkiriId) throws Exception {
        List<Object[]> signUpList = ansQuestionRepository.findSignUpByOrikkiriId(orikkiriId);
        log.info(signUpList);

        List<Map<String, Object>> result = signUpList.stream()
            .map(objects -> {
                Member member = (Member) objects[0]; // 첫 번째 요소는 Member 엔터티
                AnsQuestion ansQuestion = (AnsQuestion) objects[1]; // 두 번째 요소는 AnsQuestion 엔터티
                OriScheMemChatFav oriScheMemChatFav = (OriScheMemChatFav) objects[2]; // 두 번째 요소는 AnsQuestion 엔터티

                // tag와 content를 맵에 담아서 반환
                Map<String, Object> map = new HashMap<>();
                map.put("tag", member.getTag());
                map.put("content", ansQuestion.getContent());
                map.put("ansQuestionId", ansQuestion.getAnsQuestionId());
                map.put("oriScheMemChatFavId", oriScheMemChatFav.getOriScheMemChatFavId());

                return map;
            })
            .collect(Collectors.toList());

        return result;
    }


    @Override
    public MemberDTO getAnsMember(OriScheMemChatFavDTO oriScheMemChatFavDTO)throws Exception{
        log.info("getAnsMember에서 받은 oriScheMemChatFavDTO: {}",oriScheMemChatFavDTO);
        log.info("getAnsMember에서 받은 memberDTO: {}",memberService.getMember(oriScheMemChatFavDTO.getMember().getTag()));
        return memberService.getMember(oriScheMemChatFavDTO.getMember().getTag());
    }

    @Override
    public List<OriScheMemChatFavDTO> getMyOrikkiriList(String tag, SearchDTO searchDTO) throws Exception {
        Pageable pageable = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getPageSize());
        Page<OriScheMemChatFav> oriScheMemChatFavPage;
        oriScheMemChatFavPage = oriScheMenChatFavRepository
                .findByMemberAndOrikkiriNotNullAndAnsQuestionsNull(Member.builder().tag(tag).build(), pageable);
        log.info(oriScheMemChatFavPage);
        return oriScheMemChatFavPage.map(oriScheMemChatFavMapper::oriScheMemChatFavToOriScheMemChatFavDTO).toList();

    }

    @Override
    public void deleteOrikkiriMember(String tag, long orikkiriId) throws Exception {
        log.info(tag);
        log.info(orikkiriId);

        oriScheMenChatFavRepository.deleteByMemberAndOrikkiri(
                Member.builder().tag(tag).build(),
                Orikkiri.builder().orikkiriId(orikkiriId).build()
        );
    }

    @Override
    public List<Map<String, Object>> getOrikkiriMemberList(long orikkiriId) throws Exception {
        List<Object[]> orikkiriMemberList = ansQuestionRepository.findMembersWithOrikkiriIdAndNoAnsQuestion(orikkiriId);

        List<Map<String, Object>> result = orikkiriMemberList.stream()
                .map(objects -> {
                    Member member = (Member) objects[0]; // 첫 번째 요소는 Member 엔터티

                    Map<String, Object> map = new HashMap<>();
                    map.put("tag", member.getTag());

                    // OriScheMemChatFav 엔터티가 존재한다면 orikkiri 정보를 맵에 추가
                    if(objects.length > 1 && objects[1] instanceof OriScheMemChatFav) {
                        OriScheMemChatFav oriScheMemChatFav = (OriScheMemChatFav) objects[1];
                        map.put("orikkiriId", oriScheMemChatFav.getOrikkiri().getOrikkiriId());
                    }

                    return map;
                })
                .collect(Collectors.toList());

        System.out.println("잘 나오나 보자 : "+result);

        return result;
    }



    @Override
    public void addOrikkiriScheduleMember(OriScheMemChatFavDTO oriScheMemChatFavDTO) throws Exception {
        OriScheMemChatFav oriScheMemChatFav = oriScheMemChatFavMapper.oriScheMemChatFavDTOToOriScheMemChatFav(oriScheMemChatFavDTO);
        log.info(oriScheMemChatFav);
        oriScheMenChatFavRepository.save(oriScheMemChatFav);
    }

    @Override
    public void addOrikkiriMember(OriScheMemChatFavDTO oriScheMemChatFavDTO) throws Exception {
        OriScheMemChatFav oriScheMemChatFav = oriScheMemChatFavMapper.oriScheMemChatFavDTOToOriScheMemChatFav(oriScheMemChatFavDTO);
        log.info(oriScheMemChatFav);
        oriScheMenChatFavRepository.save(oriScheMemChatFav);
    }

    @Override
    public List<OriScheMemChatFavDTO> getOrikkiriScheduleMemberList(long orikkiriScheduleId, SearchDTO searchDTO) throws Exception {
        Pageable pageable = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getPageSize());
        Page<OriScheMemChatFav> oriScheMemChatFavPage;
        oriScheMemChatFavPage = oriScheMenChatFavRepository.findByOrikkiriSchedule(OrikkiriSchedule.builder().orikkiriScheduleId(orikkiriScheduleId).build(), pageable);
        log.info(oriScheMemChatFavPage);
        return oriScheMemChatFavPage.map(oriScheMemChatFavMapper::oriScheMemChatFavToOriScheMemChatFavDTO).toList();
    }

    @Override
    public void addOrikkiriSchedule(OrikkiriScheduleDTO orikkiriScheduleDTO) throws Exception {
        OrikkiriSchedule orikkiriSchedule = orikkiriScheduleMapper.orikkiriScheduleDTOToOrikkiriSchedule(orikkiriScheduleDTO);
        log.info(orikkiriSchedule);
        orikkiriScheduleRepository.save(orikkiriSchedule);
    }

    //스케쥴 구분 하기위한 수정 필요
    @Override
    public List<OrikkiriScheduleDTO> getOrikkiriScheduleList(long orikkiriId, SearchDTO searchDTO) throws Exception {
        Pageable pageable = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getPageSize());
        Page<OrikkiriSchedule> orikkiriSchedulePage;
        orikkiriSchedulePage = orikkiriScheduleRepository.findByOrikkiri(Orikkiri.builder().orikkiriId(orikkiriId).build(), pageable);

        log.info(orikkiriSchedulePage);
        return orikkiriSchedulePage.map(orikkiriScheduleMapper::orikkiriScheduleToOrikkiriScheduleDTO).toList();
    }

    @Override
    public OrikkiriScheduleDTO getOrikkiriSchedule(long orikkiriScheduleId) throws Exception {
        return orikkiriScheduleRepository.findById(orikkiriScheduleId).map(orikkiriScheduleMapper::orikkiriScheduleToOrikkiriScheduleDTO).orElse(null);
    }


    @Override
    public void updateOrikkiriSchedule(OrikkiriScheduleDTO orikkiriScheduleDTO) throws Exception {
        OrikkiriSchedule orikkiriSchedule = orikkiriScheduleMapper.orikkiriScheduleDTOToOrikkiriSchedule(orikkiriScheduleDTO);
        log.info(orikkiriSchedule);
        orikkiriScheduleRepository.save(orikkiriSchedule);
    }

    @Override
    public void deleteOrikkiriSchedule(long orikkiriScheduleId) throws Exception {
        log.info(orikkiriScheduleId);
        orikkiriScheduleRepository.deleteById(orikkiriScheduleId);
    }

//    @Override
//    public void deleteSignUp(long orikkiriScheduleId, ) throws Exception {
//        log.info(orikkiriScheduleId);
//        orikkiriScheduleRepository.deleteById(orikkiriScheduleId);
//    }



}
