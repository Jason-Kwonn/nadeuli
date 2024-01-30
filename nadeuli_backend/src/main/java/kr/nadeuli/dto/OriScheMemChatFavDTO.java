package kr.nadeuli.dto;


import kr.nadeuli.entity.AnsQuestion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OriScheMemChatFavDTO {

    private Long oriScheMemChatFavId;
    private MemberDTO member;
    private OrikkiriDTO orikkiri;
    private OrikkiriScheduleDTO orikkiriSchedule;
    private ProductDTO product;
    private List<AnsQuestionDTO> ansQuestions;
}
