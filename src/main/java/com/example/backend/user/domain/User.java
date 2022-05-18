package com.example.backend.user.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
public class User {

    /**
     * User Database에 필요한 것들
     * userId
     * nickname
     * email
     * password
     * name
     * profileUrl
     * userDescription
     * userReportCount
     */

    //카카오 컬럼 하나 추가해주기
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user")
    private Long userId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String profileUrl;

    @Column(nullable = true)
    private String userDescription;

    @Column(nullable = false)
    private Integer userReportCount=0;

}


