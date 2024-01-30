package kr.nadeuli.mapper;

import kr.nadeuli.dto.OrikkiriDTO;
import kr.nadeuli.dto.OrikkiriScheduleDTO;
import kr.nadeuli.entity.Orikkiri;
import kr.nadeuli.entity.OrikkiriSchedule;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(builder = @Builder(disableBuilder = true), componentModel = "spring")
public interface OrikkiriScheduleMapper {

    @Mapping(target = "oriScheMemChatFavs", ignore = true)
    @Mapping(source = "orikkiri", target = "orikkiri", qualifiedByName = "orikkiriDTOToOrikkiri")
    OrikkiriSchedule orikkiriScheduleDTOToOrikkiriSchedule(OrikkiriScheduleDTO orikkiriScheduleDTO);


    @Mapping(source = "orikkiri", target = "orikkiri", qualifiedByName = "orikkiriToOrikkiriDTO")
    OrikkiriScheduleDTO orikkiriScheduleToOrikkiriScheduleDTO(OrikkiriSchedule orikkiriSchedule);

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

}
