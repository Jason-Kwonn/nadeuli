package kr.nadeuli.service.member;

import java.util.Optional;
import kr.nadeuli.category.Role;

import kr.nadeuli.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {

  Optional<Member> findByCellphone(String cellphone);

  Optional<Member> findByEmail(String email);

  Optional<Member> findByTag(String tag);
  Optional<Member> findBySocialId(String SocialId);

  Page<Member> findByNicknameContainingOrTagContaining(String nickname, String tag, Pageable pageable);


}