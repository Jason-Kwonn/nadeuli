package kr.nadeuli.mapper;

import kr.nadeuli.common.CalculateTimeAgo;
import kr.nadeuli.dto.MemberDTO;
import kr.nadeuli.dto.OrikkiriDTO;
import kr.nadeuli.dto.PostDTO;
import kr.nadeuli.entity.Image;
import kr.nadeuli.entity.Member;
import kr.nadeuli.entity.Orikkiri;
import kr.nadeuli.entity.Post;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(builder = @Builder(disableBuilder = true), componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "reports", ignore = true)
    @Mapping(target = "regDate", ignore = true)
    @Mapping(source = "writer", target = "writer", qualifiedByName = "memberDTOToMember")
    @Mapping(source = "orikkiri", target = "orikkiri", qualifiedByName = "orikkiriDTOToOrikkiri")
    @Mapping(source = "images", target = "images", qualifiedByName = "stringToImage")
    Post postDTOToPost(PostDTO postDTO);

    @Mapping(source = "writer", target = "writer", qualifiedByName = "memberToMemberDTO")
    @Mapping(source = "orikkiri", target = "orikkiri", qualifiedByName = "orikkiriToOrikkiriDTO")
    @Mapping(source = "images", target = "images", qualifiedByName = "imageToString")
    @Mapping(source = "regDate", target = "timeAgo", qualifiedByName = "regDateToTimeAgo")
    PostDTO postToPostDTO(Post post);

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
                .picture(member.getPicture())
                .nickname(member.getNickname())
                .gu(member.getGu())
                .dongNe(member.getDongNe())
                .build();
    }

    @Named("orikkiriDTOToOrikkiri")
    default Orikkiri orikkiriDTOToOrikkiri(OrikkiriDTO orikkiriDTO){
        if(orikkiriDTO == null){
            return null;
        }
        return Orikkiri.builder().orikkiriId(orikkiriDTO.getOrikkiriId()).build();
    }

    @Named("orikkiriToOrikkiriDTO")
    default OrikkiriDTO orikkiriToOrikkiriDTO(Orikkiri orikkiri){
        if(orikkiri == null){
            return null;
        }
        return OrikkiriDTO.builder().orikkiriId(orikkiri.getOrikkiriId())
                .orikkiriName(orikkiri.getOrikkiriName())
                .orikkiriPicture(orikkiri.getOrikkiriPicture())
                .orikkiriIntroduction(orikkiri.getOrikkiriIntroduction())
                .build();
    }

    @Named("stringToImage")
    default List<Image> stringToImage(List<String> images){
        if(images == null){
            return null;
        }
        return images.stream().map(imageName -> {
            return Image.builder()
                    .imageName(imageName)
                    .build();
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

}
