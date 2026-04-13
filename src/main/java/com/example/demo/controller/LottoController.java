package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.dto.InsertLottoDTO;

@Controller
@RequestMapping("/lotto")
public class LottoController {
	
    @GetMapping("/list")
    public String List() {
        return "lotto/list";
    }
    
    @GetMapping("/insert")
    public String Insert() {
    	return "lotto/insert";
    }
    
    @PostMapping("/insert")
    public void Insert(InsertLottoDTO lotto) {
    	System.out.println("[POST] LottoController - Insert");
    	System.out.println(lotto);
    }
}