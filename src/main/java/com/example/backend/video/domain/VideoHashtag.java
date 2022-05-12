package com.example.backend.video.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table
@Data
@NoArgsConstructor
public class VideoHashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_hashtag_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @Column(nullable = false)
    private int hashtagOrder;

    @Column(nullable = false)
    private Boolean status=true; //이 비디오-해시태그가 매핑되어 있다면 1, 수정 등으로 인해 매핑 해제시 0\

    @Builder
    public VideoHashtag(Video video, Hashtag hashtag, int order){
        this.video=video;
        this.hashtag=hashtag;
        this.hashtagOrder=order;
    }
}
