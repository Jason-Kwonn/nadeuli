package kr.nadeuli.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageDTO {
    private Long imageId;
    private String imageName;
    private PostDTO post;
    private ProductDTO product;
    private NadeuliDeliveryDTO nadeuliDelivery;
}
