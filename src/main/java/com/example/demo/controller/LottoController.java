package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dao.LottoDao;
import com.example.demo.model.Lotto;
import com.example.demo.service.LottoService;

@Controller
@RequestMapping("/lotto")
public class LottoController {

    @Autowired LottoDao lottoDao;
    @Autowired LottoService lottoService;

    // 메인 통계 페이지
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("lottoMaxNo",   lottoDao.getMaxNumber());
        model.addAttribute("numGroup",     lottoDao.numGroup());
        model.addAttribute("coolHot",      lottoDao.getCoolHot());
        model.addAttribute("latestNumber", lottoDao.latestNumber());
        model.addAttribute("timeNumber",   lottoDao.timeNumber());
        return "lotto/list";
    }

    // 회차 입력 페이지
    @GetMapping("/insert")
    public String insertForm() {
        return "lotto/insert";
    }

    // 회차 입력 처리
    @ResponseBody
    @PostMapping("/insert")
    public int insert(Lotto lotto) {
        return lottoService.lottoInsert(lotto);
    }

    // 알고리즘 번호 추천 API
    @ResponseBody
    @GetMapping("/algorithm")
    public Map<String, Object> algorithm() {
        return lottoService.algorithmRecommend();
    }
}