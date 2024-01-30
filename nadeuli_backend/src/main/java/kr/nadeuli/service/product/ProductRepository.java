package kr.nadeuli.service.product;

import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.title LIKE %:title% OR p.content LIKE %:content% AND p.gu = :gu AND p.isSold = false")
    Page<Product> findProductListByKeyword(@Param("title") String title, @Param("content") String content, @Param("gu") String gu, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.gu = :gu")
    Page<Product> findProductList(@Param("gu") String gu, Pageable pageable);

    Page<Product> findBySellerAndIsSold(Member seller, boolean isSold, Pageable pageable);
    Page<Product> findByBuyer(Member buyer, Pageable pageable);
}
