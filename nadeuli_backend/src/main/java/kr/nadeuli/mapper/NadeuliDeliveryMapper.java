package kr.nadeuli.mapper;

import kr.nadeuli.common.CalculateTimeAgo;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.NadeuliDeliveryDTO;
import kr.nadeuli.dto.ProductDTO;
import kr.nadeuli.entity.Image;
import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.NadeuliDelivery;
import kr.nadeuli.entity.Product;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(builder = @Builder(disableBuilder = true), componentModel = "spring")
public interface NadeuliDeliveryMapper {
    @Mapping(target = "deliveryNotifications", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @Mapping(source = "product", target = "product", qualifiedByName = "ProductDTOToProduct")
    @Mapping(source = "buyer", target = "buyer", qualifiedByName = "buyerDTOToBuyer")
    @Mapping(source = "deliveryPerson", target = "deliveryPerson", qualifiedByName = "deliveryPersonDTOToDeliveryPerson")
    @Mapping(source = "buyer", target = "buyerNickName", qualifiedByName = "buyerDTOToBuyerNickName")
    @Mapping(source = "deliveryPerson", target = "deliveryPersonNickName", qualifiedByName = "deliveryPersonDTOToDeliveryPersonNickName")
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "regDate", qualifiedByName = "createRegDate")
    NadeuliDelivery nadeuliDeliveryDTOToNadeuliDelivery(NadeuliDeliveryDTO nadeuliDeliveryDTO);



    @Mapping(target = "deliveryNotifications", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @Mapping(source = "product", target = "product", qualifiedByName = "ProductToProductDTO")
    @Mapping(source = "buyer", target = "buyer", qualifiedByName = "buyerToBuyerDTO")
    @Mapping(source = "deliveryPerson", target = "deliveryPerson", qualifiedByName = "deliveryPersonToDeliveryPersonDTO")
    @Mapping(source = "images", target = "images", qualifiedByName = "imageToString")
    @Mapping(source = "regDate", target = "timeAgo", qualifiedByName = "regDateToTimeAgo")
    NadeuliDeliveryDTO nadeuliDeliveryToNadeuliDeliveryDTO(NadeuliDelivery nadeuliDelivery);

    @Named("imageToString")
    default List<String> imageToString(List<Image> images){
        if (images == null) {
            return null;
        }
        return images.stream().map(Image::getImageName).collect(Collectors.toList());
    }

    @Named("regDateToTimeAgo")
    default String regDateToTimeAgo(LocalDateTime regDate){
        return CalculateTimeAgo.calculateTimeDifferenceString(regDate);
    }

    @Named("buyerDTOToBuyer")
    default Member buyerDTOToBuyer(MemberDTO buyer){
        if(buyer == null){
            return null;
        }
        return Member.builder()
                .tag(buyer.getTag())
                .build();
    }

    @Named("buyerDTOToBuyerNickName")
    default String buyerDTOToBuyerNickName(MemberDTO buyer){
        if(buyer == null){
            return null;
        }
        return buyer.getNickname();
    }

    @Named("buyerToBuyerDTO")
    default MemberDTO buyerToBuyerDTO(Member buyer){
        if(buyer == null){
            return null;
        }
        return MemberDTO.builder().tag(buyer.getTag())
                .nickname(buyer.getNickname())
                .build();
    }

    @Named("deliveryPersonDTOToDeliveryPerson")
    default Member deliveryPersonDTOToDeliveryPerson(MemberDTO deliveryPerson){
        if(deliveryPerson == null){
            return null;
        }
        return Member.builder()
                .tag(deliveryPerson.getTag())
                .build();
    }

    @Named("deliveryPersonDTOToDeliveryPersonNickName")
    default String deliveryPersonDTOToDeliveryPersonNickName(MemberDTO deliveryPerson){
        if(deliveryPerson == null){
            return null;
        }
        return deliveryPerson.getNickname();
    }

    @Named("deliveryPersonToDeliveryPersonDTO")
    default MemberDTO deliveryPersonToDeliveryPersonDTO(Member deliveryPerson){
        if(deliveryPerson == null){
            return null;
        }
        return MemberDTO.builder()
                .tag(deliveryPerson.getTag())
                .nickname(deliveryPerson.getNickname())
                .build();
    }

    @Named("ProductDTOToProduct")
    default Product ProductDTOToProduct(ProductDTO productDTO) {
        if (productDTO == null) {
            return null;
        }
        return Product.builder().productId(productDTO.getProductId()).build();
    }

    @Named("ProductToProductDTO")
    default ProductDTO ProductToProductDTO(Product product) {
        if (product == null) {
            return null;
        }
        return ProductDTO.builder().productId(product.getProductId()).build();
    }

    @Named("createRegDate")
    default LocalDateTime createRegDate(LocalDateTime regDate) {
        return LocalDateTime.now();
    }

}
