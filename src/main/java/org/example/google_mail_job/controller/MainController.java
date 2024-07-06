package org.example.google_mail_job.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.google_mail_job.config.AppConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("")
@RestController
public class MainController {

    @GetMapping()
    public String home(HttpServletRequest request,
                       HttpServletResponse response) {
        response.setContentType("text/html");
        return AppConstants.APPLICATION_NAME;
    }
}
