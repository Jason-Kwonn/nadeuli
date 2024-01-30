package kr.nadeuli.service.orikkiri;

import java.util.List;
import kr.nadeuli.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OriScheMenChatFavRepository extends JpaRepository<OriScheMemChatFav, Long> {

    Page<OriScheMemChatFav> findByOrikkiriAndAnsQuestionsNull(Orikkiri orikkiri, Pageable pageable);

    Page<OriScheMemChatFav> findByOrikkiriSchedule(OrikkiriSchedule orikkiriSchedule, Pageable pageable);

    Page<OriScheMemChatFav> findByOrikkiriAndAnsQuestionsNotNull(Orikkiri orikkiri, Pageable pageable);

    Page<OriScheMemChatFav> findByMemberAndOrikkiriNotNullAndAnsQuestionsNull(Member member, Pageable pageable);

    Page<OriScheMemChatFav> findByMemberTagAndOrikkiriScheduleIsNullAndOrikkiriIsNullAndAnsQuestionsIsNullAndProductNotNull(String memberTag, Pageable pageable);

    List<OriScheMemChatFav> findAllByOrikkiriScheduleIsNullAndOrikkiriIsNullAndAnsQuestionsIsNull();

    List<OriScheMemChatFav> findAllByOriScheMemChatFavIdIn(List<Long> oriScheMemChatFavIds);
    void deleteByMemberAndOrikkiri(Member member, Orikkiri orikkiriId);

    void deleteByMemberAndProduct(Member member, Product product);

    boolean existsByMemberAndProduct(Member member, Product product);

    long countByProduct(Product product);

}
