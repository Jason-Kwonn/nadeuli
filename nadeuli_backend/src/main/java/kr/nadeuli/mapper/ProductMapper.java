package kr.nadeuli.mapper;

import kr.nadeuli.common.CalculateTimeAgo;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.ProductDTO;
import kr.nadeuli.entity.Image;
import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.Product;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(builder = @Builder(disableBuilder = true), componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "tradeReviews", ignore = true)
    @Mapping(target = "tradeSchedules", ignore = true)
    @Mapping(target = "nadeuliPayHistories", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "oriScheMemChatFavs", ignore = true)
    @Mapping(target = "regDate", ignore = true)
    @Mapping(source = "isBargain", target = "bargain")
    @Mapping(source = "isSold", target = "sold")
    @Mapping(source = "isPremium", target = "premium")
    @Mapping(source = "images", target = "images", qualifiedByName = "stringToImage")
    @Mapping(source = "seller", target = "seller", qualifiedByName = "memberDTOToMember")
    @Mapping(source = "buyer", target = "buyer", qualifiedByName = "memberDTOToMember")
    Product productDTOToProduct(ProductDTO productDTO);

    @Mapping(target = "likeNum", ignore = true)
    @Mapping(target = "isLike", ignore = true)
    @Mapping(source = "bargain", target = "isBargain")
    @Mapping(source = "sold", target = "isSold")
    @Mapping(source = "premium", target = "isPremium")
    @Mapping(source = "images", target = "images", qualifiedByName = "imageToString")
    @Mapping(source = "lastWriteTime", target = "timeAgo", qualifiedByName = "regDateToTimeAgo")
    @Mapping(source = "seller", target = "seller", qualifiedByName = "memberToMemberDTO")
    @Mapping(source = "buyer", target = "buyer", qualifiedByName = "memberToMemberDTO")
    ProductDTO productToProductDTO(Product product);

    @Named("stringToImage")
    default List<Image> stringToImage(List<String> images){
        if(images == null){
            return null;
        }
        return images.stream().map(imageName -> {
            return Image.builder().imageName(imageName).build();
        }).collect(Collectors.toList());
    }

    @Named("imageToString")
    default List<String> imageToString(List<Image> images){
        if(images == null){
            return null;
        }
        return images.stream().map(Image::getImageName).collect(Collectors.toList());
    }

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

}
