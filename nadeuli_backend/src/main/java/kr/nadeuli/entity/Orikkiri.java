package kr.nadeuli.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@ToString
@Table(name = "orikkiri")
public class Orikkiri {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orikkiri_id")
    private Long orikkiriId;

    @Column(name = "dong_ne", nullable = false, length = 255)
    private String dongNe;

    @Column(name = "orikkiri_name", nullable = false, length = 255)
    private String orikkiriName;

    @Column(name = "orikkiri_picture",  length = 10000)
    private String orikkiriPicture;

    @Column(name = "orikkiri_introduction", nullable = false)
    private String orikkiriIntroduction;

    @Column(name = "orikkiri_regist_time", nullable = false)
    private LocalDateTime orikkiriRegistTime;

    @Column(name = "master_tag", nullable = false, length = 20)
    private String masterTag;

    @OneToMany(mappedBy = "orikkiri", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JsonManagedReference
    private List<AnsQuestion> ansQuestions;

    @OneToMany(mappedBy = "orikkiri", fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private List<Post> posts;

    @OneToMany(mappedBy = "orikkiri", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OrikkiriSchedule> orikkiriSchedules;

    @OneToMany(mappedBy = "orikkiri", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<OriScheMemChatFav> oriScheMemChatFavs;

    @PrePersist
    public void prePersist() {
        if (this.orikkiriPicture == null || this.orikkiriPicture.isEmpty()) {
            this.orikkiriPicture = "https://kr.object.ncloudstorage.com/nadeuli/image/empty.jpg";
        }
    }
}
