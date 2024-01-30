package kr.nadeuli.service.comment;

import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.entity.Comment;
import kr.nadeuli.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPost(Post post, Pageable pageable);

}
