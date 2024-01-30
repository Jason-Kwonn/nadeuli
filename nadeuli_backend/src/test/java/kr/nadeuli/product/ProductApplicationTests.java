package kr.nadeuli.product;

import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.ProductDTO;
import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.entity.Member;
import kr.nadeuli.service.product.ProductService;
import kr.nadeuli.service.product.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ProductApplicationTests {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Value("${pageSize}")
    private int pageSize;

    @Test
    @Transactional
    public void testAddProduct() throws Exception{
        List<String> imageList = new ArrayList<>();
        imageList.add("당근사진.jpg");
        imageList.add("무슨사진.png");
        imageList.add("사진사진.jpg");
        ProductDTO productDTO = ProductDTO.builder()
//                .productId(3L)
                .title("상품제목목")
                .price(12340L)
                .isPremium(true)
                .premiumTime(3000L)
                .content("싸게 팔아요")
                .images(imageList)
                .video("난비디오.avi")
                .viewNum(0L)
                .tradingLocation("나는거래장소")
                .gu("강남구rnrn")
                .seller(MemberDTO.builder().tag("WVU3").build())
                .build();
        System.out.println(productDTO);

        productService.addProduct(productDTO);
    }

    //@Test
    //@Transactional
    public void testGetProduct() throws Exception{
        long productId = 2L;
        ProductDTO productDTO = productService.getProduct(productId);
        System.out.println(productDTO);
    }

    //@Test
    //@Transactional
    public void testGetProductList() throws Exception{
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setPageSize(pageSize);
        searchDTO.setSearchKeyword("목");
        String gu = "강남구";
        List<ProductDTO> productDTOList = productService.getProductList(gu, searchDTO);
        System.out.println(productDTOList);
    }

    //@Test
    public void testUpdateProduct() throws Exception{
        List<String> imageList = new ArrayList<>();
        imageList.add("당근사진.jpg");
        imageList.add("무슨사진.png");
        imageList.add("사진사진.jpg");
        ProductDTO productDTO = ProductDTO.builder()
                                          .productId(5L)
                                          .title("상품제")
                                          .price(12340L)
                                          .isPremium(false)
                                          .premiumTime(0L)
                                          .content("싸게 팔아요asdfa")
                                          .images(imageList)
                                          .video("난비디오.avi")
                                          .viewNum(0L)
                                          .tradingLocation("나는거래장소")
                                          .gu("강남구asdfa")
                                          .buyer(MemberDTO.builder().tag("WVU3").build())
                                          .build();
        productService.updateProduct(productDTO);
    }

    //@Test
    public void testSaleCompleted() throws Exception{
        productService.saleCompleted(1L);
    }

    //@Test
    //@Transactional
    public void testDeleteProduct() throws Exception{
        productService.deleteProduct(1L);
    }

    //@Test
    //@Transactional
    public void testGetMyProductList() throws Exception{
        String sellerTag = "WVU3";
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(0);
        searchDTO.setPageSize(pageSize);
//        searchDTO.setSold(false);
        searchDTO.setBuyer(true);
        System.out.println(searchDTO.isBuyer());
        List<ProductDTO> productDTOList = productService.getMyProductList(sellerTag, searchDTO);
        System.out.println(productDTOList);
    }


}
