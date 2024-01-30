package kr.nadeuli.mapper;

import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.NadeuliDeliveryDTO;
import kr.nadeuli.dto.PostDTO;
import kr.nadeuli.dto.ProductDTO;
import kr.nadeuli.dto.ReportDTO;
import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.NadeuliDelivery;
import kr.nadeuli.entity.Post;
import kr.nadeuli.entity.Product;
import kr.nadeuli.entity.Report;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(builder = @Builder(disableBuilder = true), componentModel = "spring")
public interface ReportMapper {


  @Mappings({
      @Mapping(target = "reporter", source = "reporter",qualifiedByName = "memberToMemberDTO"),
      @Mapping(target = "post", source = "post",qualifiedByName = "postToPostDTO"),
      @Mapping(target = "product", source = "product",qualifiedByName = "productToProductDTO"),
      @Mapping(target = "nadeuliDelivery", source = "nadeuliDelivery",qualifiedByName = "nadeuliDeliveryToNadeuliDeliveryDTO")
  })
  ReportDTO reportToReportDTO(Report report);

  @Mappings({
      @Mapping(target = "reporter", source = "reporter",qualifiedByName = "memberDTOToMember"),
      @Mapping(target = "post", source = "post",qualifiedByName = "postDTOToPost"),
      @Mapping(target = "product", source = "product",qualifiedByName = "productDTOToProduct"),
      @Mapping(target = "nadeuliDelivery", source = "nadeuliDelivery",qualifiedByName = "nadeuliDeliveryDTOToNadeuliDelivery")
  })
  Report reportDTOToReport(ReportDTO reportDTO);
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

  @Named("postDTOToPost")
  default Post postDTOToPost(PostDTO postDTO){
    if(postDTO == null){
      return null;
    }
    return Post.builder().postId(postDTO.getPostId()).build();
  }

  @Named("postToPostDTO")
  default PostDTO postToPostDTO(Post post){
    if(post == null){
      return null;
    }
    return PostDTO.builder()
        .postId(post.getPostId())
        .build();
  }

  @Named("nadeuliDeliveryDTOToNadeuliDelivery")
  default NadeuliDelivery nadeuliDeliveryDTOToNadeuliDelivery(NadeuliDeliveryDTO nadeuliDeliveryDTO){
    if(nadeuliDeliveryDTO == null){
      return null;
    }
    return NadeuliDelivery.builder().nadeuliDeliveryId(nadeuliDeliveryDTO.getNadeuliDeliveryId()).build();
  }

  @Named("nadeuliDeliveryToNadeuliDeliveryDTO")
  default NadeuliDeliveryDTO nadeuliDeliveryToNadeuliDeliveryDTO(NadeuliDelivery nadeuliDelivery){
    if(nadeuliDelivery == null){
      return null;
    }
    return NadeuliDeliveryDTO.builder().nadeuliDeliveryId(nadeuliDelivery.getNadeuliDeliveryId()).build();
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