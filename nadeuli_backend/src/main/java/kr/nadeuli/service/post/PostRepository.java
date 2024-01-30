package kr.nadeuli.service.post;

import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.title LIKE %:title% OR p.content LIKE %:content% AND p.gu = :gu")
    Page<Post> findPostListByKeyword(@Param("title") String title, @Param("content") String content, @Param("gu") String gu, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.gu = :gu")
    Page<Post> findPostList(@Param("gu") String gu, Pageable pageable);
}
