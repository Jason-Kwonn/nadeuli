package kr.nadeuli.controller;

import kr.nadeuli.dto.*;
import kr.nadeuli.service.comment.CommentService;
import kr.nadeuli.service.image.ImageService;
import kr.nadeuli.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/nadeuli/dongNe")
@RequiredArgsConstructor
@Log4j2
public class DongNeRestController {

    private final PostService postService;
    private final ImageService imageService;
    private final CommentService commentService;



    @Value("${pageSize}")
    int pageSize;

    @PostMapping("/addPost")
    public ResponseEntity<String> addPost(@RequestPart PostDTO postDTO, @RequestParam(value = "images", required = false) List<MultipartFile> images) throws Exception {
        Long postId = postService.addPost(postDTO);

        // 이미지가 제공되었는지 확인
        if (images != null && !images.isEmpty()) {
            // 이미지 업로드 및 저장을 위한 ImageDTO 생성
            ImageDTO imageDTO = ImageDTO.builder()
                    .post(PostDTO.builder().postId(postId).build())
                    .build();

            // 이미지 업로드 및 저장
            imageService.addImage(images, imageDTO);
        }
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true, \"postId\": %d}\", postId}");
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
        System.out.println(gu);
        System.out.println(searchKeyword);
        SearchDTO searchDTO = SearchDTO.builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .searchKeyword(searchKeyword)
                .build();

        return postService.getPostList(gu, searchDTO);
    }

    @PostMapping("/updatePost")
    public ResponseEntity<String> updatePost(@RequestPart PostDTO postDTO, @RequestParam(value= "images", required = false) List<MultipartFile> images) throws Exception {
        Long postId = postService.updatePost(postDTO);
        imageService.deletePostImage(postId);

        // 이미지가 제공되었는지 확인
        if (images != null && !images.isEmpty()) {
            // 이미지 업로드 및 저장을 위한 ImageDTO 생성
            ImageDTO imageDTO = ImageDTO.builder()
                    .post(PostDTO.builder().postId(postId).build())
                    .build();

            // 이미지 업로드 및 저장
            imageService.addImage(images, imageDTO);
        }
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

    @PostMapping("/addChannel")
    public StreamingDTO addStreaming(@ModelAttribute StreamingDTO streamingDTO) throws Exception {
        return postService.addStreamingChannel(streamingDTO);
    }

    @GetMapping("/getChannel/{channelId}")
    public StreamingDTO getStreamingChannel(@PathVariable String channelId) throws Exception {
        return postService.getStreamingChannel(channelId);
    }

    @GetMapping("/deleteChannel/{channelId}")
    public ResponseEntity<String> deleteComment(@PathVariable String channelId) throws Exception {
        postService.deleteStreamingChannel(channelId);
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }
}
