package kr.nadeuli.service.orikkirimanage;

import kr.nadeuli.entity.Orikkiri;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrikkiriManageRepository extends JpaRepository<Orikkiri, Long> {
}
