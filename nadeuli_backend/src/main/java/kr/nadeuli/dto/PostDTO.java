package kr.nadeuli.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {

    private Long postId;
    private String title;
    private String content;
    private String streaming;
    private String video;
    private String orikkiriName;
    private String orikkiriPicture;
    private Long postCategory;
    private String gu;
    private String dongNe;
    private String timeAgo;
    private MemberDTO writer;
    private OrikkiriDTO orikkiri;
    private List<String> images;



}
