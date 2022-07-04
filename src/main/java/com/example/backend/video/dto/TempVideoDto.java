package com.example.backend.video.dto;

import com.example.backend.video.domain.Video;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TempVideoDto {
    private long totalCount;
    private List<Video> videos;
}
