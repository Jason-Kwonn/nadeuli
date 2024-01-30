package kr.nadeuli.mapper;

import kr.nadeuli.dto.BlockDTO;
import kr.nadeuli.entity.Block;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(builder = @Builder(disableBuilder = true), componentModel = "spring")
public interface BlockMapper {

  @Mapping(target = "blockMember", source = "blockMember")
  BlockDTO blockToBlockDTO(Block block);

  Block blockDTOToBlock(BlockDTO blockDTO);

}
