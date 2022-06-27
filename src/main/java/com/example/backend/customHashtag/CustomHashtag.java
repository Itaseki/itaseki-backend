package com.example.backend.customHashtag;

import com.example.backend.image.domain.ImageBoard;
import com.example.backend.video.domain.Video;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table
@NoArgsConstructor
public class CustomHashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_hashtag_id")
    private Long id;

    @Column(nullable = false, length = 15)
    private String customHashtagName;

    //1 비디오 - N 키워드
    @ManyToOne
    @JoinColumn(name = "video_id")
    private Video video;

    @ManyToOne
    @JoinColumn(name = "imageBoardId")
    private ImageBoard imageBoard;

    @Column(nullable = false)
    private Integer tagOrder;

    @Builder
    public CustomHashtag(Video video, String name, int order, ImageBoard imageBoard){
        this.video=video;
        this.customHashtagName=name;
        this.tagOrder=order;
        this.imageBoard = imageBoard;
    }
}
