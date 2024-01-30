package kr.nadeuli.service.product.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import kr.nadeuli.dto.ProductDTO;
import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.OriScheMemChatFav;
import kr.nadeuli.entity.Product;
import kr.nadeuli.mapper.ProductMapper;
import kr.nadeuli.scheduler.PremiumTimeScheduler;
import kr.nadeuli.service.orikkiri.OriScheMenChatFavRepository;
import kr.nadeuli.service.product.ProductRepository;
import kr.nadeuli.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Log4j2
@Transactional
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final PremiumTimeScheduler premiumTimeScheduler;
    private final OriScheMenChatFavRepository oriScheMenChatFavRepository;

    @Override
    public Long addProduct(ProductDTO productDTO) throws Exception {
        productDTO.setViewNum(0L);
        Product product = productMapper.productDTOToProduct(productDTO);
        log.info(product);
        Product savedProduct = productRepository.save(product);
        if(productDTO.getIsPremium()){
            premiumTimeScheduler.startPremiumTimeScheduler(savedProduct.getProductId());
        }
        log.info(savedProduct);
        return savedProduct.getProductId();
    }

    @Override
    public Long updateProduct(ProductDTO productDTO) throws Exception {
        Product beforeProduct = productRepository.findById(productDTO.getProductId()).orElse(null);
        if(beforeProduct == null){
            throw new NullPointerException("product is null");
        }
        Product product = productMapper.productDTOToProduct(productDTO);
        log.info(product);
        product.setViewNum(beforeProduct.getViewNum());
        product.setLastWriteTime(LocalDateTime.now());
        product.setSeller(beforeProduct.getSeller());
        product.setSold(beforeProduct.isSold());

        Product savedProduct = productRepository.save(product);
        if(productDTO.getIsPremium()){
            premiumTimeScheduler.startPremiumTimeScheduler(savedProduct.getProductId());
        }
        log.info(savedProduct);
        return savedProduct.getProductId();
    }

    @Override
    public ProductDTO getProduct(long productId, String tag) throws Exception {
        Product product = productRepository.findById(productId).orElse(null);
        if(product == null){
            return null;
        }
        product.setViewNum(product.getViewNum()+1);
        productRepository.save(product);
        ProductDTO productDTO = productMapper.productToProductDTO(product);
        productDTO.setLikeNum(oriScheMenChatFavRepository.countByProduct(product));
        if(tag != null) {
            productDTO.setIsLike(oriScheMenChatFavRepository.existsByMemberAndProduct(Member.builder()
                                                                                            .tag(tag)
                                                                                            .build(), product));
        }
        return productDTO;
    }

    @Override
    public List<ProductDTO> getProductList(String gu, SearchDTO searchDTO) throws Exception {
        Sort sort = Sort.by(
            Sort.Order.desc("isPremium"),
            Sort.Order.desc("lastWriteTime")
        );
        Pageable pageable = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getPageSize(), sort);
        Page<Product> productPage;

        // 좋아요 정보를 담을 Map 초기화
        Map<Long, Long> likeCountMap = new HashMap<>();

        // 좋아요 정보를 가져오기
        List<OriScheMemChatFav> oriScheMemChatFavs = oriScheMenChatFavRepository.findAllByOrikkiriScheduleIsNullAndOrikkiriIsNullAndAnsQuestionsIsNull();
        for (OriScheMemChatFav oriScheMemChatFav : oriScheMemChatFavs) {
            // 해당 OriScheMemChatFav의 상품 객체 가져오기
            Product product = oriScheMemChatFav.getProduct();

            // 상품 객체가 null이 아닌 경우에 작업 수행
            if (product != null) {
                Long productId = product.getProductId();
                likeCountMap.put(productId, likeCountMap.getOrDefault(productId, 0L) + 1);
                log.info("OriScheMemChatFav(oriScheMemChatFavId={}), Product(productId={})", oriScheMemChatFav.getOriScheMemChatFavId(), productId);
                // 여기서 productId를 이용한 원하는 작업 수행
            } else {
                // 상품 객체가 null인 경우에 대한 처리
            }
        }

        // 상품 정보 가져오기
        if (searchDTO.getSearchKeyword() == null || searchDTO.getSearchKeyword().isEmpty()) {
            productPage = productRepository.findProductList(gu, pageable);
        } else {
            productPage = productRepository.findProductListByKeyword(searchDTO.getSearchKeyword(), searchDTO.getSearchKeyword(), gu, pageable);
        }

        // ProductDTO 리스트 생성
        List<ProductDTO> productDTOList = productPage.getContent().stream()
            .map(product -> {
                // ProductDTO 생성
                ProductDTO productDTO = productMapper.productToProductDTO(product);

                // Product의 ID를 이용하여 좋아요 개수 가져오기
                Long likeCount = likeCountMap.get(product.getProductId());

                // 좋아요 개수가 존재하는 경우에만 설정
                if (likeCount != null) {
                    productDTO.setLikeNum(likeCount);
                }

                return productDTO;
            })
            .collect(Collectors.toList());

        // 최종 결과인 productDTOList 반환
        log.info(productDTOList);
        return productDTOList;
    }


    @Override
    public List<ProductDTO> getMyProductList(String tag, SearchDTO searchDTO) throws Exception {
        Sort sort = Sort.by(Sort.Direction.DESC, "regDate");
        Pageable pageable = PageRequest.of(searchDTO.getCurrentPage(), searchDTO.getPageSize(), sort);
        Page<Product> productPage;
        log.info(searchDTO);
        log.info(tag);
        if(!searchDTO.isBuyer()){
            productPage = productRepository.findBySellerAndIsSold(Member.builder().tag(tag).build(), searchDTO.isSold(), pageable);
        }else{
            productPage = productRepository.findByBuyer(Member.builder().tag(tag).build(), pageable);
        }
        log.info(productPage);
        return productPage.map(productMapper::productToProductDTO).toList();

    }

    @Override
    public void deleteProduct(long productId) throws Exception {
        log.info(productId);
        productRepository.deleteById(productId);
    }

    @Override
    public void saleCompleted(long productId) throws Exception {
        Product product = productRepository.findById(productId).orElse(null);
        if(product == null){
            throw new NullPointerException();
        }
        product.setSold(true);
        productRepository.save(product);
        log.info(product);
    }

    // false면 프리미엄이 종료, true면 프리미엄 지속
    @Override
    public boolean updatePremiumTime(long productId) throws Exception {
        Product product = productRepository.findById(productId).orElse(null);
        if(product == null){
            throw new NullPointerException();
        }
        long premiumTime = product.getPremiumTime();
        if(product.getPremiumTime() == 0){
            product.setPremium(false);
            productRepository.save(product);
            return false;
        }else{
            product.setPremiumTime(premiumTime-1);
            productRepository.save(product);
            return true;
        }
    }
}
