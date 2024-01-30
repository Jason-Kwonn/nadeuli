package kr.nadeuli.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@ToString(exclude = {"member", "ansQuestions", "orikkiri", "orikkiriSchedule", "product"})
@Table(name = "ori_sche_mem_chat_fav")
public class OriScheMemChatFav {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ori_sche_mem_chat_fav_id")
    private Long oriScheMemChatFavId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag")
    @JsonBackReference
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orikkiri_id")
    @JsonBackReference
    private Orikkiri orikkiri;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orikkiri_schedule_id")
    @JsonBackReference
    private OrikkiriSchedule orikkiriSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonBackReference
    private Product product;

    @OneToMany(mappedBy = "oriScheMemChatFav", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<AnsQuestion> ansQuestions;

}
