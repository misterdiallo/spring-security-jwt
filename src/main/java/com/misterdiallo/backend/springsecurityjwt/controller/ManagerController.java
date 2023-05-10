package com.misterdiallo.backend.springsecurityjwt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/manager")
@RequiredArgsConstructor
public class ManagerController {

    @GetMapping
    public ResponseEntity<String> get() {
        return ResponseEntity.ok("GET:: manager controller");
    }

    @PostMapping
    public ResponseEntity<String> post() {
        return ResponseEntity.ok("POST:: manager controller");
    }

    @PutMapping
    public ResponseEntity<String> put() {
        return ResponseEntity.ok("PUT:: manager controller");
    }

    @DeleteMapping
    public ResponseEntity<String> delete() {
        return ResponseEntity.ok("DELETE:: manager controller");
    }
}
