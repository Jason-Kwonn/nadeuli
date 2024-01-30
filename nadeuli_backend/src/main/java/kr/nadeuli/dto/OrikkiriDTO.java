package kr.nadeuli.dto;

import kr.nadeuli.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrikkiriDTO {

    private Long orikkiriId;
    private String dongNe;
    private String orikkiriName;
    private String orikkiriPicture;
    private String orikkiriIntroduction;
    private LocalDateTime orikkiriRegistTime;
    private String masterTag;

}
