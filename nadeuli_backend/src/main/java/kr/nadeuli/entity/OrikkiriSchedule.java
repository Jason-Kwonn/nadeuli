package kr.nadeuli.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@ToString(exclude = {"orikkiri"})
@Table(name = "orikkiri_schedule")
public class OrikkiriSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orikkiri_schedule_id")
    private Long orikkiriScheduleId;

    @Column(name = "schedule_member_num", nullable = false)
    private Long scheduleMemberNum;

    @Column(name = "meeting_dong_ne", nullable = false, length = 255)
    private String meetingDongNe;

    @Column(name = "meeting_title", nullable = false, length = 255)
    private String meetingTitle;

    @Column(name = "meeting_day", nullable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime meetingDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orikkiri_id")
    private Orikkiri orikkiri;

    @OneToMany(mappedBy = "orikkiriSchedule", fetch =  FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OriScheMemChatFav> OriScheMemChatFavs;
}
