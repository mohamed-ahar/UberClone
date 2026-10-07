package com.maharram.uberclone.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class Home {
    @GetMapping("/")
    public Map<String,Object> Home() {
        return Map.of("message", "Hello World!");
    }
}
