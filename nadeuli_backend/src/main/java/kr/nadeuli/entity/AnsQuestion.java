package kr.nadeuli.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@ToString(exclude = {"orikkiri", "oriScheMemChatFav"})
@Table(name = "ans_question")
public class AnsQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ans_question_id")
    private Long ansQuestionId;

    @Column(name = "content", length = 5000, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orikkiri_id")
    @JsonBackReference
    private Orikkiri orikkiri;

    @ManyToOne(fetch = FetchType.LAZY )
    @JoinColumn(name = "ori_sche_mem_chat_fav_id")
    @JsonBackReference
    private OriScheMemChatFav oriScheMemChatFav;
}
