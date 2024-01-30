package kr.nadeuli.service.orikkiri;

import kr.nadeuli.entity.Orikkiri;
import kr.nadeuli.entity.OrikkiriSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrikkiriScheduleRepository extends JpaRepository<OrikkiriSchedule, Long> {

    Page<OrikkiriSchedule> findByOrikkiri(Orikkiri orikkiri, Pageable pageable);

}
