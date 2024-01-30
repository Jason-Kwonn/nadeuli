package kr.nadeuli.mapper;

import kr.nadeuli.dto.ImageDTO;
import kr.nadeuli.dto.NadeuliDeliveryDTO;
import kr.nadeuli.dto.PostDTO;
import kr.nadeuli.dto.ProductDTO;
import kr.nadeuli.entity.Image;
import kr.nadeuli.entity.NadeuliDelivery;
import kr.nadeuli.entity.Post;
import kr.nadeuli.entity.Product;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true), componentModel = "spring")
public interface ImageMapper {

    @Mapping(source = "post", target = "post", qualifiedByName = "postDTOToPost")
    @Mapping(source = "product", target = "product", qualifiedByName = "productDTOToProduct")
    @Mapping(source = "nadeuliDelivery", target = "nadeuliDelivery", qualifiedByName = "nadeuliDeliveryDTOToNadeuliDelivery")
    Image imageDTOToImage(ImageDTO imageDTO);

    @Mapping(source = "post", target = "post", qualifiedByName = "postToPostDTO")
    @Mapping(source = "product", target = "product", qualifiedByName = "productToProductDTO")
    @Mapping(source = "nadeuliDelivery", target = "nadeuliDelivery", qualifiedByName = "nadeuliDeliveryToNadeuliDeliveryDTO")
    ImageDTO imageToImageDTO(Image image);


    @Named("postDTOToPost")
    default Post postDTOToPost(PostDTO postDTO){
        if(postDTO == null){
            return null;
        }
        return Post.builder().postId(postDTO.getPostId())
                .build();
    }

    @Named("postToPostDTO")
    default PostDTO postToPostDTO(Post post){
        if(post == null){
            return null;
        }
        return PostDTO.builder().postId(post.getPostId())
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

    @Named("nadeuliDeliveryDTOToNadeuliDelivery")
    default NadeuliDelivery nadeuliDeliveryDTOToNadeuliDelivery(NadeuliDeliveryDTO nadeuliDeliveryDTO){
        if(nadeuliDeliveryDTO == null){
            return null;
        }
        return NadeuliDelivery.builder().nadeuliDeliveryId(nadeuliDeliveryDTO.getNadeuliDeliveryId())
                .build();
    }

    @Named("nadeuliDeliveryToNadeuliDeliveryDTO")
    default NadeuliDeliveryDTO nadeuliDeliveryToNadeuliDeliveryDTO(NadeuliDelivery nadeuliDelivery){
        if(nadeuliDelivery == null){
            return null;
        }
        return NadeuliDeliveryDTO.builder().nadeuliDeliveryId(nadeuliDelivery.getNadeuliDeliveryId())
                .build();
    }
}
