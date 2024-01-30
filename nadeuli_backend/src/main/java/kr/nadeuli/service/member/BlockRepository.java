package kr.nadeuli.service.member;

import kr.nadeuli.entity.Block;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockRepository extends JpaRepository<Block, Long> {

  void deleteByBlockMemberTag(String tag);

}
