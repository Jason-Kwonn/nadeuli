package kr.nadeuli.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnsQuestionDTO {

    private Long ansQuestionId;
    private String content;
    private OrikkiriDTO orikkiri;
    private OriScheMemChatFavDTO oriScheMemChatFav;

}
