package com.misterdiallo.backend.springsecurityjwt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/demo-controller")
@RequiredArgsConstructor
public class DemoController {

    @GetMapping
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok("Hello from secured endpoint");
    }
}
