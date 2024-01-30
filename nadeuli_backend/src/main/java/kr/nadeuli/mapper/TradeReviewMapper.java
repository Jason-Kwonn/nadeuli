package kr.nadeuli.mapper;

import kr.nadeuli.common.CalculateTimeAgo;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.ProductDTO;
import kr.nadeuli.dto.TradeReviewDTO;
import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.Product;
import kr.nadeuli.entity.TradeReview;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;

@Mapper(builder = @Builder(disableBuilder = true), componentModel = "spring")
public interface TradeReviewMapper {

    @Mapping(source = "writer", target = "writer", qualifiedByName = "memberDTOToMember")
    @Mapping(source = "trader", target = "trader", qualifiedByName = "memberDTOToMember")
    @Mapping(source = "product", target = "product", qualifiedByName = "productDTOToProduct")
    TradeReview tradeReviewDTOToTradeReview(TradeReviewDTO tradeReviewDTO);

    @Mapping(source = "regDate", target = "timeAgo", qualifiedByName = "regDateToTimeAgo")
    @Mapping(source = "writer", target = "writer", qualifiedByName = "memberToMemberDTO")
    @Mapping(source = "trader", target = "trader", qualifiedByName = "memberToMemberDTO")
    @Mapping(source = "product", target = "product", qualifiedByName = "productToProductDTO")
    TradeReviewDTO tradeReviewToTradeReviewDTO(TradeReview tradeReview);

    @Named("regDateToTimeAgo")
    default String regDateToTimeAgo(LocalDateTime regDate){
        return CalculateTimeAgo.calculateTimeDifferenceString(regDate);
    }

    @Named("memberDTOToMember")
    default Member memberDTOToMember(MemberDTO memberDTO){
        if(memberDTO == null){
            return null;
        }
        return Member.builder().tag(memberDTO.getTag()).build();
    }

    @Named("memberToMemberDTO")
    default MemberDTO memberToMemberDTO(Member member){
        if(member == null){
            return null;
        }
        return MemberDTO.builder().tag(member.getTag())
                        .build();
    }

    @Named("productDTOToProduct")
    default Product productDTOToProduct(ProductDTO productDTO){
        if(productDTO == null){
            return null;
        }
        return Product.builder().productId(productDTO.getProductId())
                        .build();
    }

    @Named("productToProductDTO")
    default ProductDTO productToProductDTO(Product product){
        if(product == null){
            return null;
        }
        return ProductDTO.builder().productId(product.getProductId())
                      .build();
    }
}
