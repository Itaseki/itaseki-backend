package com.example.backend.search;

import com.example.backend.search.dto.SearchPlaylistPageableResponse;
import com.example.backend.search.dto.SearchVideoPageableResponse;
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

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;
    private final static String EMPTY = "";

    @GetMapping("/video")
    public ResponseEntity<SearchVideoPageableResponse> searchVideoBoards(@RequestParam(required = false, defaultValue = EMPTY) String q,
                                                                         @RequestParam(required = false, defaultValue = EMPTY) String tag,
                                                                         @RequestParam(required = false, defaultValue = EMPTY) String series,
                                                                         @PageableDefault(size = 8, sort = "id", direction = Direction.DESC)
                                                                       Pageable pageable) {
        return new ResponseEntity<>(searchService.getVideoForSearch(q,tag, series, pageable),HttpStatus.OK);
    }

    @GetMapping("/playlist")
    public ResponseEntity<SearchPlaylistPageableResponse> getMainPlaylistBoards(@RequestParam(required = false, defaultValue = EMPTY) String q,
                                                                                @RequestParam(required = false, defaultValue = EMPTY) String tag,
                                                                                @RequestParam(required = false, defaultValue = EMPTY) String series,
                                                                                @PageableDefault(size = 8, sort = "id", direction = Direction.DESC) Pageable pageable) {
        return new ResponseEntity<>(searchService.getPlaylistsForSearch(q, tag, series, pageable),HttpStatus.OK);
    }
}
