package com.example.backend.user.domain;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table
@NoArgsConstructor
public class Subscribe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscribe_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "subscribe_target_id")
    private User subscribeTarget;

    @Column(nullable = false, name = "subscribe_status")
    private Boolean status=true;

    @Column
    private LocalDateTime lastModified;

    @Builder
    public Subscribe(User user, User subscribeTarget, LocalDateTime lastModified){
        this.user=user;
        this.subscribeTarget=subscribeTarget;
        this.lastModified=lastModified;
    }
}
