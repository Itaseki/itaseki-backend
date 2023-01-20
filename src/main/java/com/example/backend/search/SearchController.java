package com.example.backend.search;

import com.example.backend.community.dto.AllCommunityBoardsResponse;
import com.example.backend.main.dto.MainImageResponse;
import com.example.backend.search.dto.SearchVideoResponse;
import com.example.backend.playlist.dto.AllPlaylistsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;
    private final static String EMPTY = "";

//    @GetMapping("/community")
//    public ResponseEntity<List<AllCommunityBoardsResponse>> searchCommunityBoards(@RequestParam String sort, @RequestParam(required = false) String q){
//        return new ResponseEntity<>(searchService.getCommunityForSearch(q,sort), HttpStatus.OK);
//    }
//
//    @GetMapping("/image")
//    public ResponseEntity<List<MainImageResponse>> searchImageBoards(@RequestParam String sort, @RequestParam(required = false) String q,
//                                                                      @RequestParam(required = false) String tag){
//        return new ResponseEntity<>(searchService.getImageForSearch(q,tag,sort),HttpStatus.OK);
//    }

    @GetMapping("/video")
    public ResponseEntity<List<SearchVideoResponse>> searchVideoBoards(@RequestParam String sort,
                                                                     @RequestParam(required = false, defaultValue = EMPTY) String q,
                                                                     @RequestParam(required = false, defaultValue = EMPTY) String tag) {
        return new ResponseEntity<>(searchService.getVideoForSearch(q,tag,sort),HttpStatus.OK);
    }

    @GetMapping("/playlist")
    public ResponseEntity<List<AllPlaylistsResponse>> getMainPlaylistBoards(@RequestParam String sort,
                                                                            @RequestParam(required = false, defaultValue = EMPTY) String q,
                                                                            @RequestParam(required = false) String tag) {
        return new ResponseEntity<>(searchService.getPlaylistsForSearch(sort, q, tag),HttpStatus.OK);
    }
}
