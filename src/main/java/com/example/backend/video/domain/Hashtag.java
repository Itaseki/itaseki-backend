package com.example.backend.video.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Table
@NoArgsConstructor
public class Hashtag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hashtag_id")
    private Long id;

    @Column
    private String hashtagName;

}
