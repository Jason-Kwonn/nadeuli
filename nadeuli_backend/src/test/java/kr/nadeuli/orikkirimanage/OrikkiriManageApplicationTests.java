package kr.nadeuli.orikkirimanage;

import kr.nadeuli.dto.*;
import kr.nadeuli.entity.Orikkiri;
import kr.nadeuli.service.orikkiri.OriScheMenChatFavRepository;
import kr.nadeuli.service.orikkiri.OrikkiriScheduleRepository;
import kr.nadeuli.service.orikkiri.OrikkiriService;
import kr.nadeuli.service.orikkirimanage.AnsQuestionRepository;
import kr.nadeuli.service.orikkirimanage.OrikkiriManageRepository;
import kr.nadeuli.service.orikkirimanage.OrikkiriManageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class OrikkiriManageApplicationTests {
    @Autowired
    OrikkiriManageService orikkiriManageService;

    @Autowired
    OrikkiriManageRepository orikkiriManageRepository;
    AnsQuestionRepository ansQuestionRepository;

    @Value("${pageSize}")
    private int pageSize;

    //@Test
    //    @RepeatedTest(3)
    public void testaddOrikkiri() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        OrikkiriDTO orikkiriDTO = OrikkiriDTO.builder()
                .orikkiriId(4L)
                 .dongNe("성동구")
                 .orikkiriName("헬스가자3")
                 .orikkiriPicture("산사진")
                 .orikkiriIntroduction("등산하는 모임입니다.")
                .orikkiriRegistTime(now)
                .build();

        System.out.println(orikkiriDTO);

        orikkiriManageService.addOrikkiri(orikkiriDTO);
        orikkiriManageService.updateOrikkiri(orikkiriDTO);
    }

    //@Test
    public void testGetOrikkiri() throws Exception {
        long orikkiriId = 2L;
        OrikkiriDTO orikkiriDTO = orikkiriManageService.getOrikkiri(orikkiriId);
        System.out.println(orikkiriDTO);
    }


   // @Test
    public void testDeleteOrikkiri() throws Exception{
        long orikkiriId = 1L;
        orikkiriManageService.deleteOrikkiri(orikkiriId);
    }



//    @Test
    public void testGetAnsQuestion() throws Exception {
        long ansQuestionId = 5L;
        AnsQuestionDTO ansQuestionDTO = orikkiriManageService.getAnsQuestion(ansQuestionId);
        System.out.println(ansQuestionDTO);
    }



    @Test
    @Transactional
    public void testGetAnsQuestionList() throws Exception {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setPageSize(pageSize);
        searchDTO.setSearchKeyword("");
        long orikkiriId = 2L;
        List<AnsQuestionDTO> ansQuestionDTOList = orikkiriManageService.getAnsQuestionList(orikkiriId,searchDTO);
        System.out.println(ansQuestionDTOList);
    }

//    @Test
    public void testDeleteAnsQuestion() throws Exception{
        long ansQuestionId = 5L;
        orikkiriManageService.deleteAnsQuestion(ansQuestionId);
    }

}


