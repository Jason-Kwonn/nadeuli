package kr.nadeuli.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import kr.nadeuli.dto.*;
import kr.nadeuli.entity.Orikkiri;
import kr.nadeuli.service.comment.CommentService;
import kr.nadeuli.service.image.ImageService;
import kr.nadeuli.service.orikkiri.OrikkiriService;
import kr.nadeuli.service.orikkirimanage.OrikkiriManageService;
import kr.nadeuli.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/nadeuli/orikkiri")
@RequiredArgsConstructor
@Log4j2
public class OrikkiriRestController {

    private final OrikkiriService orikkiriService;
    private final PostService postService;
    private final CommentService commentService;
    private final ImageService imageService;
    private final OrikkiriManageService orikkiriManageService;
    private final ObjectMapper objectMapper;

    @Value("${pageSize}")
    private int pageSize;

    @PostMapping("/addOrikkrirSignUp")
    public OriScheMemChatFavDTO addOrikkrirSignUp(@RequestBody Map<String, Object> requestData) throws Exception {
        log.info("받은oriScheMemChatFavDTO는 {}",requestData);
        // MemberDTO 매핑
        MemberDTO memberDTO = objectMapper.convertValue(requestData.get("member"), MemberDTO.class);

        // OrikkiriDTO 매핑
        OrikkiriDTO orikkiriDTO = objectMapper.convertValue(requestData.get("orikkiri"), OrikkiriDTO.class);
        // AnsQuestionDTO 매핑
        List<AnsQuestionDTO> ansQuestionDTOList = objectMapper.convertValue(requestData.get("ansQuestions"), new TypeReference<List<AnsQuestionDTO>>() {});

        log.info("받은oriScheMemChatFavDTO는 {}",memberDTO);
        log.info("받은oriScheMemChatFavDTO는 {}",orikkiriDTO);
        log.info("받은oriScheMemChatFavDTO는 {}",ansQuestionDTOList);

        OriScheMemChatFavDTO oriScheMemChatFavDTO = OriScheMemChatFavDTO.builder()
            .member(memberDTO)
            .orikkiri(orikkiriDTO)
            .ansQuestions(ansQuestionDTOList)
            .build();
        return orikkiriService.addOrikkrirSignUp(oriScheMemChatFavDTO);
    }

    @GetMapping("/getOrikkrirSignupList/{orikkiriId}")
    public List<Map<String, Object>> getOrikkiriSignUpList(@PathVariable long orikkiriId) throws Exception {
        SearchDTO searchDTO = SearchDTO.builder()
            .currentPage(0)
            .pageSize(pageSize)
            .build();

        List<Map<String, Object>> signUpList = orikkiriService.getOrikkiriSignUpList(orikkiriId);
        return signUpList;
    }

    @GetMapping("/getMyOrikkiriList/{tag}/{currentPage}")
    public List<OriScheMemChatFavDTO> getMyOrikkiriList(@PathVariable String tag, @PathVariable int currentPage) throws Exception {
        SearchDTO searchDTO = SearchDTO.builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build();
        return orikkiriService.getMyOrikkiriList(tag, searchDTO);
    }

    @GetMapping("/deleteOrikkiriMember/{tag}/{orikkiriId}")
    public ResponseEntity<String> deleteOrikkiriMember(@PathVariable String tag, @PathVariable long orikkiriId) throws Exception {
        orikkiriService.deleteOrikkiriMember(tag, orikkiriId);
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

    @GetMapping("/getOrikkiriMemberList/{orikkiriId}")
    public List<Map<String, Object>> getOrikkiriMemberList(@PathVariable long orikkiriId) throws Exception {
        SearchDTO searchDTO = SearchDTO.builder()
                .currentPage(0)
                .pageSize(pageSize)
                .build();

        List<Map<String, Object>> orikkiriMemberListList = orikkiriService.getOrikkiriMemberList(orikkiriId);
        return orikkiriMemberListList;
    }

    @PostMapping("/addOrikkiriScheduleMember")
    public ResponseEntity<String> addOrikkiriScheduleMember(@RequestBody OriScheMemChatFavDTO oriScheMemChatFavDTO) throws Exception {
        orikkiriService.addOrikkiriScheduleMember(oriScheMemChatFavDTO);
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

    @PostMapping("/addOrikkiriMember")
    public ResponseEntity<String> addOrikkiriMember(@RequestBody OriScheMemChatFavDTO oriScheMemChatFavDTO) throws Exception {
        orikkiriService.addOrikkiriMember(oriScheMemChatFavDTO);
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

    @GetMapping("/getOrikkiriScheduleMemberList/{orikkiriScheduleId}/{currentPage}")
    public List<OriScheMemChatFavDTO> getOrikkiriScheduleMemberList(@PathVariable long orikkiriScheduleId, @PathVariable int currentPage) throws Exception {
        SearchDTO searchDTO = SearchDTO.builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build();
        return orikkiriService.getOrikkiriScheduleMemberList(orikkiriScheduleId, searchDTO);
    }

    @PostMapping("/addOrikkiriSchedule")
    public ResponseEntity<String> addOrikkiriSchedule(@RequestBody Map<String, Object> requestData) throws Exception {
        OrikkiriScheduleDTO orikkiriScheduleDTO = objectMapper.convertValue(requestData.get("orikkiriScheduleDTO"), OrikkiriScheduleDTO.class);
        log.info("받은 DTO는 {}", orikkiriScheduleDTO);

        // 이미 LocalDateTime 형식이라면 toString()을 사용하지 않고 그대로 사용
        LocalDateTime meetingDateTime = (orikkiriScheduleDTO.getMeetingDay() instanceof LocalDateTime)
            ? (LocalDateTime) orikkiriScheduleDTO.getMeetingDay()
            : LocalDateTime.parse(orikkiriScheduleDTO.getMeetingDay().toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // 변환된 LocalDateTime을 다시 설정
        orikkiriScheduleDTO.setMeetingDay(meetingDateTime);

        orikkiriService.addOrikkiriSchedule(orikkiriScheduleDTO);
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

    @GetMapping("/getOrikkiriScheduleList/{orikkiriId}")
    public List<OrikkiriScheduleDTO> getOrikkiriScheduleList(@PathVariable long orikkiriId) throws Exception {
        SearchDTO searchDTO = SearchDTO.builder()
                .currentPage(0)
                .pageSize(pageSize)
                .build();
        return orikkiriService.getOrikkiriScheduleList(orikkiriId, searchDTO);
    }

    @GetMapping("/getOrikkiriSchedule/{orikkiriScheduleId}")
    public OrikkiriScheduleDTO getOrikkiriSchedule(@PathVariable long orikkiriScheduleId) throws Exception {
        return orikkiriService.getOrikkiriSchedule(orikkiriScheduleId);
    }

    @PostMapping("/updateOrikkiriSchedule")
    public ResponseEntity<String> updateOrikkiriSchedule(@RequestBody OrikkiriScheduleDTO orikkiriScheduleDTO) throws Exception {
        orikkiriService.updateOrikkiriSchedule(orikkiriScheduleDTO);
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");

    }

    @GetMapping("/deleteOrikkiriSchedule/{orikkiriScheduleId}")
    public ResponseEntity<String> deleteOrikkiriSchedule(@PathVariable long orikkiriScheduleId) throws Exception {
        orikkiriService.deleteOrikkiriSchedule(orikkiriScheduleId);
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");

    }

    @PostMapping("/addPost")
    public ResponseEntity<String> addPost(@RequestBody PostDTO postDTO,@RequestParam("images") List<MultipartFile> images) throws Exception {
        Long postId = postService.addPost(postDTO);
        // 이미지 업로드 및 저장을 위한 ImageDTO 생성
        ImageDTO imageDTO = ImageDTO.builder()
                .post(PostDTO.builder().postId(postId).build())
                .build();

        // 이미지 업로드 및 저장
        imageService.addImage(images, imageDTO);
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

    @GetMapping("/getPost/{postId}")
    public PostDTO getPost(@PathVariable long postId) throws Exception {
        List<ImageDTO> imageDTOList =imageService.getImageList(postId, SearchDTO.builder()
                .isPost(true)
                .build());
        PostDTO postDTO = postService.getPost(postId);
        List<String> imageNames = imageDTOList.stream()
                .map(ImageDTO::getImageName)
                .collect(Collectors.toList());
        postDTO.setImages(imageNames);
        return postDTO;
    }

    @GetMapping("/dongNeHome/{currentPage}")
    public List<PostDTO> getPostList(@PathVariable int currentPage, @RequestParam String gu, @RequestParam(required = false) String searchKeyword) throws Exception {
        SearchDTO searchDTO = SearchDTO.builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .searchKeyword(searchKeyword)
                .build();

        return postService.getPostList(gu, searchDTO);
    }

    @PostMapping("/updatePost")
    public ResponseEntity<String> updatePost(@RequestBody PostDTO postDTO,@RequestParam("images") List<MultipartFile> images) throws Exception {
        Long postId = postService.updatePost(postDTO);
        imageService.deletePostImage(postId);

        // 이미지 업로드 및 저장을 위한 ImageDTO 생성
        ImageDTO imageDTO = ImageDTO.builder()
                .post(PostDTO.builder().postId(postId).build())
                .build();

        // 이미지 업로드 및 저장
        imageService.addImage(images, imageDTO);
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

    @GetMapping("/deletePost/{postId}")
    public ResponseEntity<String> deletePost(@PathVariable long postId) throws Exception {
        postService.deletePost(postId);
        imageService.deletePostImage(postId);
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

    @PostMapping("/addComment")
    public ResponseEntity<String> addComment(@RequestBody CommentDTO commentDTO) throws Exception {
        commentService.addComment(commentDTO);
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

    @GetMapping("/getComment/{commentId}")
    public CommentDTO getComment(@PathVariable long commentId) throws Exception {
        return commentService.getComment(commentId);
    }

    @GetMapping("/getCommentList/{postId}")
    public List<CommentDTO> getCommentList(@PathVariable  long postId) throws Exception {
        return commentService.getCommentList(postId);
    }

    @PostMapping("/updateComment")
    public ResponseEntity<String> updateComment(@RequestBody CommentDTO commentDTO) throws Exception {
        commentService.updateComment(commentDTO);
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

    @GetMapping("/deleteComment/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable long commentId) throws Exception {
        commentService.deleteComment(commentId);
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }
}

