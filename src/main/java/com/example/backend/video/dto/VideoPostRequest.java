package com.example.backend.video.dto;

import static java.lang.Integer.parseInt;

import com.example.backend.user.domain.User;
import com.example.backend.video.domain.Series;
import com.example.backend.video.domain.Video;
import java.util.Collections;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class VideoPostRequest {
    private String url;
    private String title;
    private String runtime;
    private String description;
    private long series;
    private Integer episode;
    private List<Long> hashtags;
    private List<String> keywords;
    private List<Long> playlists;
    private String thumbnailUrl;
    private String videoUploader;

    public Video toEntityWithUserAndSeries(User user, Series series) {
        return Video.builder()
                .videoUrl(url)
                .description(description)
                .originTitle(title)
                .episodeNumber(episode)
                .user(user)
                .series(series)
                .thumbnailUrl(thumbnailUrl)
                .uploader(videoUploader)
                .time(toHourMinSec(runtime))
                .build();
    }

    public List<String> getKeywords() {
        if (keywords == null) {
            return Collections.emptyList();
        }
        return keywords;
    }

    public List<Long> getHashtags() {
        if (hashtags == null) {
            return Collections.emptyList();
        }
        return hashtags;
    }

    public List<Long> getPlaylists() {
        if (playlists == null) {
            return Collections.emptyList();
        }
        return playlists;
    }

    private int[] toHourMinSec(String runtime) {
        String[] splitTime = runtime.split(":");
        if (splitTime.length == 3) {
            return new int[]{parseInt(splitTime[0]), parseInt(splitTime[1]), parseInt(splitTime[2])};
        }
        if (splitTime.length == 2) {
            return new int[]{0, parseInt(splitTime[0]), parseInt(splitTime[1])};
        }
        return new int[]{0, 0, parseInt(splitTime[0])};
    }
}
