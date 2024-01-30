package kr.nadeuli.post;

import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.PostDTO;
import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.Orikkiri;
import kr.nadeuli.service.post.PostRepository;
import kr.nadeuli.service.post.PostService;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class PostApplicationTests {
    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;


    @Value("${pageSize}")
    private int pageSize;

    //@Test
//    @RepeatedTest(3)
    public void testAddPost() throws Exception {
        List<String> imageList = new ArrayList<>();
        imageList.add("디비테스트1.jpg");
        imageList.add("테스트2.png");
        imageList.add("테스트3 사진.jpg");
        PostDTO postDTO = PostDTO.builder()
                .title("이미지테스트")
                .content("스트리밍 내용")
                .postCategory(3L)
                .video("test")
//                .orikkiri(Orikkiri.builder().orikkiriId().build())
                .orikkiriName("우리끼리1")
                .orikkiriPicture("우리끼리사진1")
                .streaming("먹방")
//                .images(imageList)
                .gu("송파구")
                .dongNe("서울")
                .writer(MemberDTO.builder().tag("Bss3").build())
                .build();
        System.out.println(postDTO);

        postService.addPost(postDTO);
    }

//    @Test
//    @Transactional
    public void testGetPost() throws Exception {
        long postId = 2L;
        PostDTO postDTO = postService.getPost(postId);
        System.out.println(postDTO);
    }

    //@Test
    public void testDeletePost() throws Exception{
        long postId = 1L;
        postService.deletePost(postId);
    }


    // 주소 단위만 테스트 필요
//   @Test
//   @Transactional
    public void testGetPostList() throws Exception {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setPageSize(pageSize);
        searchDTO.setSearchKeyword("");
        String gu = "송파구";
        List<PostDTO> postDTOList = postService.getPostList(gu, searchDTO);
        System.out.println(postDTOList);
    }

    @Test
    public void testUpdatePost() throws Exception {
        List<String> imageList = new ArrayList<>();
        imageList.add("디비에 들어가나 테스트.jpg");
        PostDTO postDTO = PostDTO.builder()
                .postId(1L)
                .title("이미지 테스트용")
                .content("동네 맛집 맛집맛집")
                .postCategory(2L)
                .gu("송파구")
                .dongNe("서울")
                .writer(MemberDTO.builder().tag("Bss3").build())
                .build();
        System.out.println(postDTO);

        postService.updatePost(postDTO);
    }
}