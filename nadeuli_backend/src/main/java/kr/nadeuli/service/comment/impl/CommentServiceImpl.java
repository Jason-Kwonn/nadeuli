package kr.nadeuli.service.comment.impl;

import kr.nadeuli.dto.CommentDTO;
import kr.nadeuli.dto.PostDTO;
import kr.nadeuli.dto.SearchDTO;
import kr.nadeuli.entity.Comment;
import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.Post;
import kr.nadeuli.entity.Product;
import kr.nadeuli.mapper.CommentMapper;
import kr.nadeuli.service.comment.CommentRepository;
import kr.nadeuli.service.comment.CommentService;
import kr.nadeuli.service.post.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Log4j2
@Transactional
@Service("commentServiceImpl")
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;


    @Value("${commentNum}")
    int commentNum;

    @Override
    public void addComment(CommentDTO commentDTO) throws Exception {
        Comment comment = commentMapper.commentDTOToComment(commentDTO);
        log.info(comment);
        commentRepository.save(comment);
    }

    @Override
    public CommentDTO getComment(long commentId) throws Exception {
        return commentRepository.findById(commentId).map(commentMapper::commentToCommentDTO).orElse(null);
    }

    @Override
    public List<CommentDTO> getCommentList(long postId) throws Exception {
        Sort sort = Sort.by(Sort.Direction.ASC, "regDate");
        Pageable pageable = PageRequest.of(0, commentNum, sort);
        Page<Comment> commentPage;
        commentPage = commentRepository.findByPost(Post.builder().postId(postId).build(), pageable);
        log.info(commentPage);
        return commentPage.map(commentMapper::commentToCommentDTO).toList();
    }

    @Override
    public void updateComment(CommentDTO commentDTO) throws Exception {
        Comment comment = commentMapper.commentDTOToComment(commentDTO);
        log.info(comment);
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(long commentId) throws Exception {
        log.info(commentId);
        commentRepository.deleteById(commentId);
    }



}
