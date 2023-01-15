package com.example.backend.playlist.exception;

public class NoSuchPlaylistException extends RuntimeException {
    private static final String MESSAGE = "존재하지 않는 플레이리스트";

    public NoSuchPlaylistException() {
        super(MESSAGE);
    }
}
