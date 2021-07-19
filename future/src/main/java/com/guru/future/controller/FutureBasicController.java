package com.guru.future.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FutureBasicController {

    @GetMapping(value = "/future/basic/list")
    public Object list() {
        return null;
    }


}
