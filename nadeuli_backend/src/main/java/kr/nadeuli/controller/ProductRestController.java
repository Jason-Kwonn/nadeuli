package kr.nadeuli.controller;

import kr.nadeuli.category.TradeType;
import kr.nadeuli.dto.ImageDTO;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.NadeuliPayHistoryDTO;
import kr.nadeuli.dto.PostDTO;
import kr.nadeuli.dto.ProductDTO;
import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.service.image.ImageService;
import kr.nadeuli.service.member.MemberService;
import kr.nadeuli.service.nadeuli_pay.NadeuliPayService;
import kr.nadeuli.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/nadeuli/product")
@RequiredArgsConstructor
@Log4j2
public class ProductRestController {
    private final MemberService memberService;
    private final ProductService productService;
    private final NadeuliPayService nadeuliPayService;
    private final ImageService imageService;

    @Value("${pageSize}")
    private int pageSize;

    @Value("${premiumPricePerHour}")
    private int premiumPricePerHour;

    @GetMapping("/home/{currentPage}/{gu}")
    public List<ProductDTO> getProductList(@PathVariable int currentPage, @PathVariable String gu, @RequestParam(required = false) String keyword) throws Exception {
        SearchDTO searchDTO = SearchDTO.builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .searchKeyword(keyword)
                                       .build();
        return productService.getProductList(gu, searchDTO);
    }

    @GetMapping("/getProduct/{productId}/{tag}")
    public ProductDTO getProduct(@PathVariable Long productId, @PathVariable String tag) throws Exception {
        log.info(productId);
        List<ImageDTO> imageDTOList = imageService.getImageList(productId, SearchDTO.builder()
                            .isProduct(true)
                                                      .build());
        ProductDTO productDTO = productService.getProduct(productId, tag);
        List<String> imageNames = imageDTOList.stream()
                                              .map(ImageDTO::getImageName)
                                              .collect(Collectors.toList());
        productDTO.setImages(imageNames);
        productDTO.setSeller(memberService.getOtherMember(productDTO.getSeller().getTag()));
        return productDTO;
    }

    @PostMapping("/updateProduct")
    public ResponseEntity<String> updateProduct(@ModelAttribute ProductDTO productDTO,@RequestParam("image") List<MultipartFile> images) throws Exception {
        System.out.println("업데이트프로덕트");
        log.info(productDTO);
        ProductDTO beforeProductDTO = productService.getProduct(productDTO.getProductId(), null);
        log.info(beforeProductDTO);
        if(productDTO.getIsPremium()){
            Long premiumTime = 0L;
            if(beforeProductDTO.getIsPremium() == null || !beforeProductDTO.getIsPremium()) {
                premiumTime = productDTO.getPremiumTime();
            } else{
                premiumTime = productDTO.getPremiumTime() - beforeProductDTO.getPremiumTime();
            }
            nadeuliPayService.nadeuliPayPay(beforeProductDTO.getSeller()
                                                      .getTag(), NadeuliPayHistoryDTO.builder()
                                                                                     .productTitle(productDTO.getTitle())
                                                                                     .product(productDTO)
                                                                                     .tradingMoney(premiumTime * premiumPricePerHour)
                                                                                     .build());
        }
        productService.updateProduct(productDTO);
        imageService.deleteProductImage(productDTO.getProductId());

        // 이미지 업로드 및 저장을 위한 ImageDTO 생성
        ImageDTO imageDTO = ImageDTO.builder()
            .product(ProductDTO.builder()
                         .productId(productDTO.getProductId())
                         .build())
            .build();

        // 이미지 업로드 및 저장
        imageService.addImage(images, imageDTO);

        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

    @PostMapping("/addProduct")
    public ResponseEntity<String> addProduct(@ModelAttribute ProductDTO productDTO, @RequestParam("image") List<MultipartFile> images) throws Exception {
        Long productId = productService.addProduct(productDTO);
        productDTO.setProductId(productId);
        if(productDTO.getIsPremium()){
            nadeuliPayService.nadeuliPayPay(productDTO.getSeller().getTag(), NadeuliPayHistoryDTO.builder()
                                     .tradeType(TradeType.PAYMENT)
                                     .productTitle(productDTO.getTitle())
                                     .product(productDTO)
                                     .tradingMoney(productDTO.getPremiumTime() * premiumPricePerHour)
                                                                                                 .build());
        }

        // 이미지 업로드 및 저장을 위한 ImageDTO 생성
        ImageDTO imageDTO = ImageDTO.builder()
            .product(ProductDTO.builder()
                         .productId(productId)
                         .build())
            .build();

        // 이미지 업로드 및 저장
        imageService.addImage(images, imageDTO);

        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

    // type : 0 판매중, type : 1 판매완료, type : 2 구매완료
    @GetMapping("/getMyProductList/{tag}/{currentPage}")
    public List<ProductDTO> getMyProductList(@PathVariable String tag, @PathVariable int currentPage, @RequestParam(defaultValue = "0") int type) throws Exception {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCurrentPage(currentPage);
        searchDTO.setPageSize(pageSize);
        if(type == 1){
            searchDTO.setSold(true);
        }
        if(type == 2){
            searchDTO.setBuyer(true);
        }

        List<ProductDTO> list = productService.getMyProductList(tag, searchDTO);
        for(ProductDTO productDTO : list){
            // ProductDTO의 seller를 설정
            MemberDTO sellerDTO = memberService.getMember(productDTO.getSeller().getTag());
            productDTO.setSeller(sellerDTO);
        }
        return list;
    }

    @GetMapping("/deleteProduct/{productId}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) throws Exception {
        productService.deleteProduct(productId);
        imageService.deleteProductImage(productId);
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

    @PostMapping("/saleCompleted")
    public ResponseEntity<String> saleCompleted(@RequestBody ProductDTO productDTO) throws Exception {
        productService.saleCompleted(productDTO.getProductId());
        return ResponseEntity.status(HttpStatus.OK).body("{\"success\": true}");
    }

}
