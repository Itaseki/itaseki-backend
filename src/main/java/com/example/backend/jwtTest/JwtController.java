package com.example.backend.jwtTest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * JWT + Spring Security 테스트 컨트롤러 (나중에 지울 예정)
 */

@RequestMapping("/jwt")
@RestController
public class JwtController {

    @GetMapping("")
    public String jwtTest(){
        return "스프링 시큐리티 테스트";
    }
}
