package com.example.backend.video.domain;

import com.example.backend.customHashtag.CustomHashtag;
import com.example.backend.playlist.domain.PlaylistVideo;
import com.example.backend.report.Report;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private Long id;

    @Column(nullable = false)
    private String originVideoTitle;

    //20자 제한 주기
    @Column
    private String description;

    @Column(columnDefinition = "TEXT")
    private String thumbnailUrl;

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

    @Column(length = 25)
    private String videoUploader;

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
    private int likeCount = 0;

    @Column(nullable = false)
    private int reportCount = 0;

    @Column(nullable = false)
    private int viewCount = 0;

    @Column(nullable = false, name = "video_status")
    private Boolean status = true;

    @OneToMany(mappedBy = "video", targetEntity = VideoComment.class)
    private List<VideoComment> videoComments;

    @OneToMany(mappedBy = "video", targetEntity = VideoHashtag.class)
    private List<VideoHashtag> videoHashtags;

    @OneToMany(mappedBy = "video", targetEntity = CustomHashtag.class)
    private List<CustomHashtag> customHashtags;

    @OneToMany(mappedBy = "video", targetEntity = Report.class)
    private List<Report> reports;

    @OneToMany(mappedBy = "video", targetEntity = PlaylistVideo.class)
    private List<PlaylistVideo> playlistVideos;

    @Builder
    public Video(String originTitle, String description, String videoUrl, Integer episodeNumber, Series series,
                 User user, String thumbnailUrl, String uploader, int[] time) {
        this.originVideoTitle = originTitle;
        this.description = description;
        this.videoUrl = videoUrl;
        this.episodeNumber = episodeNumber;
        this.series = series;
        this.createdTime = LocalDateTime.now();
        this.user = user;
        this.thumbnailUrl = thumbnailUrl;
        this.videoUploader = uploader;
        this.runtimeHour = time[0];
        this.runtimeMin = time[1];
        this.runtimeSec = time[2];

    }

    public void updateVideoViewCount() {
        this.viewCount++;
    }

    public int updateLikeCount(boolean isLikeCreated) {
        if (isLikeCreated) {
            this.likeCount++;
            return this.likeCount;
        }
        this.likeCount--;
        return this.likeCount;
    }

    public String getConvertedRuntime() {
        String format = "%02d:%02d:%02d";
        return String.format(format, runtimeHour, runtimeMin, runtimeSec);
    }

    public boolean isReportCountOverLimit() {
        return this.reports.size() >= 5;
    }

}
