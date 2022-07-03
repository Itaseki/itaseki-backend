package com.example.backend.playlist.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubscribePlaylistResponseWithPageCount {
    //1 페이지에 1 user- 1 playlist list 존재
    Integer totalPageCount;
    List<SubscribePlaylistResponse> playlistsResponses;

    public SubscribePlaylistResponseWithPageCount(int count, List<SubscribePlaylistResponse> boards){
        this.totalPageCount=count;
        this.playlistsResponses=boards;
    }
}
