package kr.nadeuli.image;

import java.util.Arrays;
import java.util.List;
import kr.nadeuli.dto.*;
import kr.nadeuli.service.image.ImageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
public class ImageApplicationTests {
    @Autowired
    ImageService imageService;


    @Value("${pageSize}")
    private int pageSize;


    @Test
    public void testAddImageList() throws Exception {
        // MockMultipartFile 객체 생성
        MockMultipartFile file1 = new MockMultipartFile("files", "testImg1.jpg", "image/jpeg", "testImg1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "testImg2.jpg", "image/jpeg", "testImg2".getBytes());

        // MultipartFile 리스트에 추가
        List<MultipartFile> multipartFiles = Arrays.asList(file1, file2);

        ImageDTO imageDTO = ImageDTO.builder()
                .imageName("testImg1")
//                .post(PostDTO.builder().postId(2L).build())
                .product(ProductDTO.builder().productId(5L).build())
//                .nadeuliDelivery(NadeuliDeliveryDTO.builder().nadeuliDeliveryId(2L).build())
                .build();
        imageService.addImage(multipartFiles,imageDTO);
    }


//        @Test
//    @Transactional
    public void getImage() throws Exception {
        long imageId = 13L;
        System.out.println(imageService.getImage(imageId));;

    }



//        @Test
//        @Transactional
    public void testGetImagesList() throws Exception {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setPageSize(pageSize);
        long id = 2L;
        System.out.println(imageService.getImageList(id, searchDTO));
    }


//    @Test
    public void testDeleteImage() throws Exception {
        imageService.deleteProductImage(10L);
    }
}
