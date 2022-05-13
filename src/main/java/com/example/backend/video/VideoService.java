package com.example.backend.video;

import com.example.backend.user.domain.User;
import com.example.backend.video.domain.*;
import com.example.backend.video.dto.InnerInfoResponse;
import com.example.backend.video.dto.VideoDto;
import com.example.backend.video.dto.VideoUploadInfoResponse;
import com.example.backend.video.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

@Service
@Transactional
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final HashtagRepository hashtagRepository;
    private final SeriesRepository seriesRepository;
    private final VideoHashtagRepository videoTagRepository;
    private final CustomHashtagRepository customHashtagRepository;

    public void saveVideo(VideoDto videoDto, User user){
        //플레이리스트에 영상 저장하는 부분 추가
        Series series=getSeries(videoDto.getSeries());
        Video video=Video.builder()
                .videoUrl(videoDto.getUrl()).description(videoDto.getDescription())
                .originTitle(videoDto.getTitle()).series(series)
                .episodeNumber(videoDto.getEpisode()).user(user)
                .build();
        int[] times = toHourMinSec(video, videoDto.getRuntime());
        video.setVideoRuntime(times);
        List<Hashtag> hashtags = getHashtagsByIds(videoDto.getHashtags());
        if(hashtags!=null){
            saveVideoHashtag(video,hashtags);
        }
        if(videoDto.getKeywords()!=null){
            saveVideoKeywords(videoDto.getKeywords(),video);
        }
        videoRepository.save(video);
    }

    private List<Hashtag> getHashtagsByIds(List<Long> ids){
        if(ids==null){
            return null;
        }
        return ids.stream().map(id -> hashtagRepository.findById(id).get()).collect(Collectors.toList());
    }

    private void saveVideoHashtag(Video video,List<Hashtag> hashtags){
        int order=1;
        for(Hashtag hashtag:hashtags){
            VideoHashtag videoHashtag = VideoHashtag.builder().video(video).hashtag(hashtag).order(order++).build();
            videoTagRepository.save(videoHashtag);
        }
    }

    private void saveVideoKeywords(List<String> keywords,Video video){
        int order=1;
        for(String keyword:keywords){
            CustomHashtag videoKeyword = CustomHashtag.builder().video(video).name(keyword).order(order++).build();
            customHashtagRepository.save(videoKeyword);
        }
    }

    private Series getSeries(Long id){
        Optional<Series> series = seriesRepository.findById(id);
        return series.orElse(null);
    }

    //넘어오는 시간 형태 11:10:1 (시간) 11:10 (분) 00:04 (초)
    private int[] toHourMinSec(Video video, String runtime){
        String[] strings = runtime.split(":");
        int hour,min,sec;
        switch (strings.length){
            case 3:
                hour= parseInt(strings[0]);
                min= parseInt(strings[1]);
                sec= parseInt(strings[2]);
                break;
            case 2:
                hour=0;
                min= parseInt(strings[0]);
                sec= parseInt(strings[1]);
                break;
            default:
                hour=0;
                min=0;
                sec=0;
        }
        return new int[]{hour, min, sec};
    }

    public String checkVideoUrlExistence(String url){
        Video video = videoRepository.findByVideoUrl(url);
        if(video==null)
            return "등록 가능";
        return "등록 불가능";
    }

    private List<InnerInfoResponse> findAllSeries(){
        return seriesRepository.findAll().stream()
                .map(InnerInfoResponse::new)
                .collect(Collectors.toList());
    }

    private List<InnerInfoResponse> findAllHashtags(){
        return hashtagRepository.findAll()
                .stream()
                .map(InnerInfoResponse::new)
                .collect(Collectors.toList());
    }

    private List<InnerInfoResponse> findAllPlayListsOfUser(User user){
        return new ArrayList<>();
    }

    public VideoUploadInfoResponse getPreInfoForVideoUpload(User user){
        return VideoUploadInfoResponse.toInfoResponse(findAllSeries(),findAllHashtags(),findAllPlayListsOfUser(user));
    }

    public Video findVideoEntityById(Long id){
        Optional<Video> video = videoRepository.findById(id);
        if(video.isPresent()&&video.get().getStatus()){
            return video.get();
        }
        return null;
    }
}
