package com.example.backend.search;

import com.example.backend.search.dto.SearchVideoPageableResponse;
import com.example.backend.playlist.dto.AllPlaylistsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/video")
    public ResponseEntity<SearchVideoPageableResponse> searchVideoBoards(@RequestParam(required = false, defaultValue = EMPTY) String q,
                                                                         @RequestParam(required = false, defaultValue = EMPTY) String tag,
                                                                         @PageableDefault(size = 8, sort = "id", direction = Direction.DESC)
                                                                       Pageable pageable) {
        return new ResponseEntity<>(searchService.getVideoForSearch(q,tag,pageable),HttpStatus.OK);
    }

    @GetMapping("/playlist")
    public ResponseEntity<List<AllPlaylistsResponse>> getMainPlaylistBoards(@RequestParam(required = false, defaultValue = EMPTY) String q,
                                                                            @RequestParam(required = false) String tag,
                                                                            @PageableDefault(size = 8, sort = "id", direction = Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(searchService.getPlaylistsForSearch(q, tag, pageable),HttpStatus.OK);
    }
}
