/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
/**
 *
 * @author NiTeFox
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


@Slf4j
@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        log.debug("Home page requested");
        return "home";
    }

    @GetMapping("/about")
    public String about() {
        log.debug("About page requested");
        return "about";
    }

    @GetMapping("/contacts")
    public String contacts() {
        log.debug("Contacts page requested");
        return "contacts";
    }

    @GetMapping("/login")
    public String login() {
        log.debug("Login page requested");
        return "login";
    }
    
    @GetMapping("/register")
    public String register() {
        log.debug("Register page requested");
        return "register";
    }
    
    @GetMapping("/find")
    public String find() {
        log.debug("Find page requested");
        return "find";
    }
    
    @GetMapping("/link-expired")
    public String linkExpired() {
        log.info("Link expired page shown");
        return "expired_link";
    }
}