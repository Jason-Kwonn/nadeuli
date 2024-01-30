package kr.nadeuli.service.image;

import kr.nadeuli.entity.Image;
import kr.nadeuli.entity.NadeuliDelivery;
import kr.nadeuli.entity.Post;
import kr.nadeuli.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ImageRepository extends JpaRepository<Image, Long> {

    Page<Image> findByPost(Post post, Pageable pageable);

    Page<Image> findByProduct(Product product, Pageable pageable);

    Page<Image> findByNadeuliDelivery(NadeuliDelivery nadeuliDelivery, Pageable pageable);


    void deleteByPost(Post post);

    void deleteByProduct(Product product);

    void deleteByNadeuliDelivery(NadeuliDelivery nadeuliDelivery);
}
