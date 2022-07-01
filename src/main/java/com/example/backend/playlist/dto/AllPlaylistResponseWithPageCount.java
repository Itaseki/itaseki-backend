package com.example.backend.playlist.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AllPlaylistResponseWithPageCount {
    Integer totalPageCount;
    List<AllPlaylistsResponse> playlistsResponses;

    public AllPlaylistResponseWithPageCount(int count, List<AllPlaylistsResponse> boards){
        this.totalPageCount=count;
        this.playlistsResponses=boards;
    }
}
