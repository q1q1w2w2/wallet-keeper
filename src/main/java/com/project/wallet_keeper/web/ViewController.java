package com.project.wallet_keeper.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class ViewController {

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/")
    @ResponseBody
    public String homePage() {
        return "home";
    }
}
