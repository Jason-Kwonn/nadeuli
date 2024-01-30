package kr.nadeuli.mapper;

import kr.nadeuli.common.CalculateTimeAgo;
import kr.nadeuli.dto.CommentDTO;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.PostDTO;
import kr.nadeuli.entity.Comment;
import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.Post;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;

@Mapper(builder = @Builder(disableBuilder = true), componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "post", target = "post", qualifiedByName = "postDTOToPost")
    @Mapping(source = "writer", target = "writer", qualifiedByName = "memberDTOToMember")
    @Mapping(source = "refComment", target = "refComment", qualifiedByName = "refcommentDTOToRefComment")
    Comment commentDTOToComment(CommentDTO commentDTO);

    @Mapping(source = "post", target = "post", qualifiedByName = "postToPostDTO")
    @Mapping(source = "writer", target = "writer", qualifiedByName = "memberToMemberDTO")
    @Mapping(source = "refComment", target = "refComment", qualifiedByName = "refcommentToRefCommentDTO")
    @Mapping(source = "regDate", target = "timeAgo", qualifiedByName = "regDateToTimeAgo")
    CommentDTO commentToCommentDTO(Comment comment);

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
        return PostDTO.builder().postId(post.getPostId()).build();
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
                .nickname(member.getNickname())
                .picture(member.getPicture())
                .gu(member.getGu())
                .dongNe(member.getDongNe())
                .build();
    }

    @Named("refcommentDTOToRefComment")
    default Comment refcommentDTOToRefComment(CommentDTO commentDTO){
        if(commentDTO == null){
            return null;
        }
        return Comment.builder().commentId(commentDTO.getCommentId()).build();
    }

    @Named("refcommentToRefCommentDTO")
    default CommentDTO refcommentToRefCommentDTO(Comment comment){
        if(comment == null){
            return null;
        }
        return CommentDTO.builder().commentId(comment.getCommentId())
                .build();
    }


    @Named("regDateToTimeAgo")
    default String regDateToTimeAgo(LocalDateTime regDate){
        return CalculateTimeAgo.calculateTimeDifferenceString(regDate);
    }
}
