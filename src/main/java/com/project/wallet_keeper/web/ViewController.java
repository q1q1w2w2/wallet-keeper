package com.project.wallet_keeper.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class ViewController {

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "auth/signup";
    }

    @GetMapping("/")
    public String homePage() {
        return "main/home";
    }

    @GetMapping("/calendar")
    public String calendarPage() {
        return "main/calendar";
    }

    @GetMapping("/setting")
    public String settingPage() {
        return "main/setting";
    }
}
