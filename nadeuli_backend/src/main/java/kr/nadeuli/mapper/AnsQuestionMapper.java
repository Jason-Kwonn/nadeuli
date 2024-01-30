package kr.nadeuli.mapper;

import kr.nadeuli.dto.AnsQuestionDTO;
import kr.nadeuli.dto.OriScheMemChatFavDTO;
import kr.nadeuli.dto.OrikkiriDTO;
import kr.nadeuli.entity.AnsQuestion;
import kr.nadeuli.entity.OriScheMemChatFav;
import kr.nadeuli.entity.Orikkiri;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(builder = @Builder(disableBuilder = true), componentModel = "spring")
public interface AnsQuestionMapper {

    @Mapping(source = "orikkiri", target = "orikkiri", qualifiedByName = "orikkiriDTOToOrikkiri")
    @Mapping(source = "oriScheMemChatFav", target = "oriScheMemChatFav", qualifiedByName = "oriScheMemChatFavDTOToOriScheMemChatFav")
    AnsQuestion ansQuestionDTOToAnsQuestion(AnsQuestionDTO ansQuestionDTO);

    @Mapping(source = "orikkiri", target = "orikkiri", qualifiedByName = "orikkiriToOrikkiriDTO")
    @Mapping(source = "oriScheMemChatFav", target = "oriScheMemChatFav", qualifiedByName = "oriScheMemChatFavToOriScheMemChatFavDTO")
    AnsQuestionDTO ansQuestionToAnsQuestionDTO(AnsQuestion ansQuestion);

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
        return OrikkiriDTO.builder().orikkiriId(orikkiri.getOrikkiriId()).build();
    }

    @Named("oriScheMemChatFavDTOToOriScheMemChatFav")
    default OriScheMemChatFav oriScheMemChatFavDTOToOriScheMemChatFav(OriScheMemChatFavDTO oriScheMemChatFavDTO){
        if(oriScheMemChatFavDTO == null){
            return null;
        }
        return OriScheMemChatFav.builder().oriScheMemChatFavId(oriScheMemChatFavDTO.getOriScheMemChatFavId()).build();
    }

    @Named("oriScheMemChatFavToOriScheMemChatFavDTO")
    default OriScheMemChatFavDTO oriScheMemChatFavToOriScheMemChatFavDTO(OriScheMemChatFav oriScheMemChatFav){
        if(oriScheMemChatFav == null){
            return null;
        }
        return OriScheMemChatFavDTO.builder().oriScheMemChatFavId(oriScheMemChatFav.getOriScheMemChatFavId()).build();
    }

}
