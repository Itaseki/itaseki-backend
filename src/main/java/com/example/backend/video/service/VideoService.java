package com.example.backend.video.service;

import com.example.backend.customHashtag.CustomHashtag;
import com.example.backend.customHashtag.CustomHashtagRepository;
import com.example.backend.user.domain.User;
import com.example.backend.video.domain.*;
import com.example.backend.video.dto.*;
import com.example.backend.video.repository.*;
import com.example.backend.video.service.VideoCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final VideoCommentService videoCommentService;

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

    private List<String> getHashtagKeywordStringInVideo(Video video){
        List<VideoHashtag> videoHashtags = video.getVideoHashtags();
        List<CustomHashtag> customHashtags = video.getCustomHashtags();
        //두 개의 list 를 각각 string stream 으로 변경한 후, 두 stream을 하나로 합친 list를 반환
        return Stream.concat(videoHashtags.stream().map(videoHashtag -> videoHashtag.getHashtag().getHashtagName())
                        ,customHashtags.stream().map(CustomHashtag::getCustomHashtagName))
                .collect(Collectors.toList());
    }

    public DetailVideoResponse getDetailVideoResponse(Video video, Long loginId){
        User videoWriter = video.getUser();
        Long videoWriterId=videoWriter.getUserId();
        List<VideoComment> parentComments = video.getVideoComments()
                .stream()
                .filter(comment -> comment.getIsParentComment().equals(true)) //부모댓글만 넘김
                .collect(Collectors.toList());
        List<VideoCommentsResponse> videoCommentResponses = videoCommentService.getVideoCommentResponses(parentComments, loginId, videoWriterId);
        return DetailVideoResponse.fromEntity(video,videoCommentResponses,loginId,getHashtagKeywordStringInVideo(video));
    }

    public void updateVideoViewCount(Video video){
        video.updateVideoViewCount();
        videoRepository.save(video);
    }

    public AllVideoResponseWithPageCount getAllVideosResponse(Pageable pageable, String tag, String nickname, String q){
        List<String> tags=null;
        if(tag!=null){
            tags = Arrays.stream(tag.split(",")).collect(Collectors.toList());
        }
        Page<Video> videoPage = videoRepository.findAll(pageable, tags, nickname, null);
        List<AllVideoResponse> allVideoResponses = toAllResponse(videoPage.getContent());
        return new AllVideoResponseWithPageCount(allVideoResponses,getTotalPageCount(videoPage.getTotalElements()));
    }

    private int getTotalPageCount(long pages){
        //total video count 를 기준으로 한 페이지는 4개 -> 다음페이지는 8개로 나뉜다는걸 생각해서 전체 페이지 수 반환
        //데이터 13개 (의도: 3페이지, 잘못된 연산: 2페이지) 넣어놓고 체크해보면 될듯
        return (int) (1+Math.ceil((pages-4)/(double)8));
    }

    private List<AllVideoResponse> toAllResponse(List<Video> videos){
        return videos.stream()
                .map(AllVideoResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<AllVideoResponse> getBestVideos(){
        return toAllResponse(videoRepository.findBestVideos());

    }

    public void deleteVideo(Video video){
        video.setStatus(false);
        videoRepository.save(video);
    }

    public List<Series> findSeriesNameByQuery(String q){
        return seriesRepository.findBySeriesNameContains(q);
    }

}
