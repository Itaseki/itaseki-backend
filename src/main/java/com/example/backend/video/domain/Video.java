package com.example.backend.video.domain;

import com.example.backend.customHashtag.CustomHashtag;
import com.example.backend.user.domain.User;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table
public class Video {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private Long id;

    @Column(nullable = false)
    private String originVideoTitle;

    //20자 제한 주기
    @Column
    private String description;

    //1 영상 - 1 시리즈 / 1 시리즈 - N 영상
    @ManyToOne
    @JoinColumn(name = "series_id")
    private Series series;

    //비디오 업로드한 사용자자
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String videoUrl;

    @Column
    private Integer episodeNumber;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdTime;

    @Column
    private Integer runtimeHour;

    @Column
    private Integer runtimeMin;

    @Column
    private Integer runtimeSec;

    @Column(nullable = false)
    private int likeCount=0;

    @Column(nullable = false)
    private int reportCount=0;

    @Column(nullable = false)
    private int viewCount=0;

    @Column(nullable = false, name = "video_status")
    private Boolean status=true;

    @OneToMany(mappedBy = "video", targetEntity = VideoComment.class)
    private List<VideoComment> videoComments;

    @OneToMany(mappedBy = "video",targetEntity = VideoHashtag.class)
    private List<VideoHashtag> videoHashtags;

    @OneToMany(mappedBy = "video",targetEntity = CustomHashtag.class)
    private List<CustomHashtag> customHashtags;

    @Builder
    public Video (String originTitle, String description, String videoUrl, Integer episodeNumber, Series series,User user){
        this.originVideoTitle=originTitle;
        this.description=description;
        this.videoUrl=videoUrl;
        this.episodeNumber=episodeNumber;
        this.series=series;
        this.createdTime=LocalDateTime.now();
        this.user=user;
    }

    public void setVideoRuntime(int[] times){
        this.runtimeHour=times[0];
        this.runtimeMin=times[1];
        this.runtimeSec=times[2];
    }

    public void updateVideoViewCount(){
        this.viewCount++;
    }

    public Integer updateLikeCount(int likeVal){
        this.likeCount+=likeVal;
        return this.likeCount;
    }

}
