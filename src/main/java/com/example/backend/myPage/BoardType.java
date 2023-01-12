package com.example.backend.myPage;

import java.util.Arrays;

public enum BoardType {
    COMMUNITY("community", "CommunityBoard"),
    VIDEO("video", "Video"),
    PLAYLIST("playlist", "Playlist");

    private final String boardName;
    private final String entityName;

    BoardType(String name, String className) {
        this.boardName = name;
        this.entityName = className;
    }

    public static String findBoardType(Object board) {
        return Arrays.stream(BoardType.values())
                .filter(boardType -> board.getClass().getName().contains(boardType.getEntityName()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("보드 타입 에러"))
                .getBoardName();
    }

    private String getBoardName() {
        return boardName;
    }

    private String getEntityName() {
        return entityName;
    }
}
