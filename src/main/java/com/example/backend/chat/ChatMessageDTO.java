package com.example.backend.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {

    private String roomId;
    private String writer;
    private String message;
    private String profileUrl = "http://k.kakaocdn.net/dn/uJ3nC/btrG5qJDRzA/Y8aFVKvPsaIRLsdH1x203K/img_640x640.jpg";
}
