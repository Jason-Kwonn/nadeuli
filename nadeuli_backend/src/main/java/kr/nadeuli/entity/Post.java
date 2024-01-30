package kr.nadeuli.entity;

import jakarta.persistence.*;

import java.util.List;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicUpdate
@ToString(exclude = {"writer", "orikkiri", "comments", "reports", "images"})
@Table(name = "post")
public class Post extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "content", length = 5000)
    private String content;

    @Column(name = "video", length = 50000)
    private String video;

    @Column(name = "streaming", length = 50000)
    private String streaming;

    @Column(name = "orikkiri_name", length = 255)
    private String orikkiriName;

    @Column(name = "orikkiri_picture", length = 50000)
    private String orikkiriPicture;

    @Column(name = "post_category", nullable = false)
    private Long postCategory;

    @Column(name = "gu", length = 255, nullable = false)
    private String gu;

    @Column(name = "dong_ne", length = 255, nullable = false)
    private String dongNe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_tag")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orikkiri_id")
    private Orikkiri orikkiri;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL,  fetch = FetchType.LAZY)
//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Comment> comments;


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Report> reports;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Image> images;
}

