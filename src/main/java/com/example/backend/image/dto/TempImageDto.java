package com.example.backend.image.dto;

import com.example.backend.image.domain.ImageBoard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TempImageDto {
    private long totalCount;
    private List<ImageBoard> imageBoards;
}
