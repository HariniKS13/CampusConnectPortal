package com.campusconnect.campusconnect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/leave")
    public String leave() {
        return "leave";
    }

    @PostMapping("/leave")
    public String submitLeave() {
        return "success";
    }

    @GetMapping("/bonafide")
    public String bonafide() {
        return "bonafide";
    }

    @GetMapping("/notices")
    public String notices() {
        return "notices";
    }

    @GetMapping("/success")
    public String success() {
        return "success";
    }
    @PostMapping("/bonafide")
    public String submitBonafide() {
    return "success";
    }
}