package kr.nadeuli.mapper;

import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.NadeuliPayHistoryDTO;
import kr.nadeuli.dto.ProductDTO;
import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.NadeuliPayHistory;
import kr.nadeuli.entity.Product;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(builder = @Builder(disableBuilder = true), componentModel = "spring")
public interface NadeuliPayHistoryMapper {
    @Mapping(target = "imp_uid", ignore = true)
    @Mapping(source = "member", target = "member", qualifiedByName = "memberToMemberDTO")
    @Mapping(source = "product", target = "product", qualifiedByName = "productToProductDTO")
    NadeuliPayHistoryDTO nadeuliPayHistoryToNadeuliPayHistoryDTO(NadeuliPayHistory nadeuliPayHistory);

    @Mapping(source = "member", target = "member", qualifiedByName = "memberDTOToMember")
    @Mapping(source = "product", target = "product", qualifiedByName = "productDTOToProduct")
    NadeuliPayHistory nadeuliPayHistoryDTOToNadeuliPayHistory(NadeuliPayHistoryDTO nadeuliPayHistoryDTO);

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
