package com.example.backend.video;

import com.example.backend.video.exception.WrongVideoUrlException;
import java.util.Arrays;

public enum VideoUrlType {
    BASIC("youtube.com/watch?v="),
    SHARE("youtu.be/");

    private final String url;
    VideoUrlType(String url) {
        this.url = url;
    }

    public static String extractVideoId(String url) {
        url = removeParams(url);
        String urlType = findUrlType(url);
        return url.substring(url.indexOf(urlType) + urlType.length());
    }

    private static String findUrlType(String inputUrl) {
        return Arrays.stream(values())
                .filter(urlType -> inputUrl.contains(urlType.url))
                .findAny()
                .map(VideoUrlType::getUrl)
                .orElseThrow(() -> new WrongVideoUrlException());
    }

    private static String removeParams(String url) {
        if (url.contains("&")) {
            url = url.substring(0, url.indexOf("&"));
        }
        if (url.contains("?t")) {
            return url.substring(0, url.indexOf("?t"));
        }
        return url;
    }

    private String getUrl() {
        return this.url;
    }
}
