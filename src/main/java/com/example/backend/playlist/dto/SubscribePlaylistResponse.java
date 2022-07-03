package com.example.backend.playlist.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubscribePlaylistResponse {
    //한 user 에 대해 하나의 resposne list 존재
    private String userNickname;
    private List<AllPlaylistsResponse> playlistsResponses;

    public SubscribePlaylistResponse(String nickname, List<AllPlaylistsResponse> boards){
        this.userNickname=nickname;
        this.playlistsResponses=boards;
    }
}
