package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.service.LottoAPIService;

@Controller
@RequestMapping("/")
public class LottoController {

	@Autowired
	LottoAPIService lottoAPIService;
	
    @GetMapping({"/",""})
    public String Lotto() {
        System.out.println("LottoController - Lotto");
        return "home";
    }

    @GetMapping("/getLotto")
    public String LottoInsert() {
        System.out.println("LottoController - getLotto");

        lottoAPIService.LottoInit();

        return "redirect:/";
    }
}