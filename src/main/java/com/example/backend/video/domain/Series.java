package com.example.backend.video.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table
public class Series {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "series_id")
    private Long id;

    @Column
    private String seriesName;

    @Builder
    public Series(String name) {
        this.seriesName = name;
    }
}
