package com.God.sRecords.one.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String home() {
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(){
        return "register";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/task/{id}")
    public String current() {
        return "current";
    }
}