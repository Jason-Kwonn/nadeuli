package kr.nadeuli.orikkiri;

import kr.nadeuli.dto.*;
import kr.nadeuli.entity.AnsQuestion;
import kr.nadeuli.service.orikkiri.OriScheMenChatFavRepository;
import kr.nadeuli.service.orikkiri.OrikkiriService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class OrikkiriApplicationTests {
    @Autowired
    OrikkiriService orikkiriService;

    @Autowired
    OriScheMenChatFavRepository oriScheMenChatFavRepository;

    @Value("${pageSize}")
    private int pageSize;

    //@Test
    public void testAddOrikkrirSignUp() throws Exception {
        long ansQuestionId = 3L;
        OriScheMemChatFavDTO oriScheMemChatFavDTO = OriScheMemChatFavDTO.builder()
                .member(MemberDTO.builder().tag("Bss3").build())
//                .orikkiri(OrikkiriDTO.builder().orikkiriId(2L).build())
                .orikkiriSchedule(OrikkiriScheduleDTO.builder().orikkiriScheduleId(1L).build())
//                .product(ProductDTO.builder().productId(1L).build())
                .build();

        System.out.println(oriScheMemChatFavDTO);

        orikkiriService.addOrikkrirSignUp(oriScheMemChatFavDTO);
    }


    @Test
    @Transactional
    public void testGetOrikkiriSignUpList() throws Exception {
//        long ansQuestionId = 6L;
//        SearchDTO searchDTO = new SearchDTO();
//        searchDTO.setCurrentPage(0);
//        searchDTO.setPageSize(pageSize);
//        searchDTO.setSearchKeyword("");
//        List<OriScheMemChatFavDTO> orikkiriServiceOrikkiriSignUpList = orikkiriService.getOrikkiriSignUpList(ansQuestionId, searchDTO);
//        System.out.println(orikkiriServiceOrikkiriSignUpList);
    }

//    @Test
//    @Transactional
    public void testgetMyOrikkiriList() throws Exception {
        String tag = "Bss3";
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setPageSize(pageSize);
        searchDTO.setSearchKeyword("");
        List<OriScheMemChatFavDTO> orikkiriServiceMyOrikkiriList = orikkiriService.getMyOrikkiriList(tag, searchDTO);
        System.out.println(orikkiriServiceMyOrikkiriList);
    }

//    @Test
    public void testDeleteOrikkiriMember() throws Exception{
        String tag = "Bss3";
        long orikkiriId = 2L;
        orikkiriService.deleteOrikkiriMember(tag, orikkiriId);
    }

//    @Test
//    @Transactional
//    public void testGetOrikkiriMemberList() throws Exception {
//        long orikkiriId = 2L;
//        SearchDTO searchDTO = new SearchDTO();
//        searchDTO.setCurrentPage(0);
//        searchDTO.setPageSize(pageSize);
//        searchDTO.setSearchKeyword("");
//        List<OriScheMemChatFavDTO> orikkiriServiceOrikkiriMemberList = orikkiriService.getOrikkiriMemberList(orikkiriId, searchDTO);
//        System.out.println(orikkiriServiceOrikkiriMemberList);
//    }

//    @Test
    public void testAddOrikkiriScheduleMember() throws Exception {
        OriScheMemChatFavDTO oriScheMemChatFavDTO = OriScheMemChatFavDTO.builder()
                .member(MemberDTO.builder().tag("pJvS").build())
                .orikkiriSchedule(OrikkiriScheduleDTO.builder().orikkiriScheduleId(3L).build())
                .build();

        System.out.println(oriScheMemChatFavDTO);

        orikkiriService.addOrikkiriScheduleMember(oriScheMemChatFavDTO);
    }
    @Test
    public void testAddOrikkiriMember() throws Exception {
        OriScheMemChatFavDTO oriScheMemChatFavDTO = OriScheMemChatFavDTO.builder()
                .member(MemberDTO.builder().tag("Bss3").build())
                .orikkiri(OrikkiriDTO.builder().orikkiriId(2L).build())
                .build();

        System.out.println(oriScheMemChatFavDTO);

        orikkiriService.addOrikkiriMember(oriScheMemChatFavDTO);
    }

//    @Test
//    @Transactional
    public void testGetOrikkiriScheduleMemberList() throws Exception {
        long orikkiriScheduleId = 3L;
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setPageSize(pageSize);
        searchDTO.setSearchKeyword("");
        List<OriScheMemChatFavDTO> orikkiriScheduleDTOList = orikkiriService.getOrikkiriScheduleMemberList(orikkiriScheduleId, searchDTO);
        System.out.println(orikkiriScheduleDTOList);
    }

//    @Test
    public void testAddOrikkiriSchedule() throws Exception {
        OrikkiriScheduleDTO orikkiriScheduleDTO = OrikkiriScheduleDTO.builder()
                .meetingDay(LocalDateTime.now())
                .meetingDongNe("강동구")
                .scheduleMemberNum(0L)
                .orikkiri(OrikkiriDTO.builder().orikkiriId(2L).build())
                .build();

        System.out.println(orikkiriScheduleDTO);

        orikkiriService.addOrikkiriSchedule(orikkiriScheduleDTO);
    }

//    @Test
//    @Transactional
    public void testGetOrikkiriScheduleList() throws Exception {
        long orikkiriId = 2L;
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setPageSize(pageSize);
        searchDTO.setSearchKeyword("");
        List<OrikkiriScheduleDTO> orikkiriScheduleDTOList = orikkiriService.getOrikkiriScheduleList(orikkiriId, searchDTO);
        System.out.println(orikkiriScheduleDTOList);
    }

//    @Test
    public void testGetOrikkiriSchedule() throws Exception {
        long orikkiriScheduleId = 3L;
        OrikkiriScheduleDTO orikkiriScheduleDTO = orikkiriService.getOrikkiriSchedule(orikkiriScheduleId);
        System.out.println(orikkiriScheduleDTO);
    }

//     @Test
    public void testDeleteOrikkiriSchedule() throws Exception{
        long orikkiriScheduleId = 3L;
        orikkiriService.deleteOrikkiriSchedule(orikkiriScheduleId);
    }


}


