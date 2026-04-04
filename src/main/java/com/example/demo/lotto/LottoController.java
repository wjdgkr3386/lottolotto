package com.example.demo.lotto;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class LottoController {

	@GetMapping({"/",""})
	public String Lotto() {
		System.out.println("LottoController - Lotto");
		return "home";
	}
	
	@GetMapping("/getLotto")
	public String LottoInsert() {
		System.out.println("LottoController - getLotto");
		return "redirect:/";
	}
}
