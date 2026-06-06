/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nitefox.cloudattic.controller;

import com.nitefox.cloudattic.entity.ShareLink;
import com.nitefox.cloudattic.service.ShareLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 *
 * @author NiTeFox
 */

@Slf4j
@Controller
@RequiredArgsConstructor
public class PublicController {

    private final ShareLinkService shareLinkService;

    @GetMapping("/public/{token}")
    public String publicFile(@PathVariable String token, Model model) {
        log.info("Public file view requested with token={}", token);
        ShareLink link = shareLinkService.findByToken(token);
        model.addAttribute("file", link.getFile());
        model.addAttribute("token", link.getToken());
        return "public-file";
    }
}