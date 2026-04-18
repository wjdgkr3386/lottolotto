package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dao.LottoDAO;
import com.example.demo.model.Lotto;
import com.example.demo.service.LottoService;

@Controller
@RequestMapping("/lotto")
public class LottoController {

	@Autowired
	LottoDAO lottoDAO;
	@Autowired
	LottoService lottoService;
	
	@GetMapping("/list")
	public String List(Model model) {
	    int lottoMaxNo = lottoDAO.getMaxNumber();
	    List<Map<String, Object>> numGroup = lottoDAO.numGroup();
	    Map<String, Object> coolHot = lottoDAO.getCoolHot();
	    int latestNumber = lottoDAO.latestNumber();
	    List<Integer> timeNumber = lottoDAO.timeNumber();

	    model.addAttribute("lottoMaxNo", lottoMaxNo);
	    model.addAttribute("numGroup", numGroup);
	    model.addAttribute("coolHot", coolHot);
	    model.addAttribute("latestNumber", latestNumber);
	    model.addAttribute("timeNumber", timeNumber);

	    return "lotto/list";
	}
    
    @GetMapping("/insert")
    public String Insert() {
    	return "lotto/insert";
    }
    
    @ResponseBody
    @PostMapping("/insert")
    public int Insert(Lotto lotto) {
    	System.out.println("[POST] LottoController - Insert");
    	int result = lottoService.LottoInsert(lotto);
    	return result;
    }
}