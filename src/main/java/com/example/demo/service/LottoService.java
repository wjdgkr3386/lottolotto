package com.example.demo.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dao.LottoDAO;

@Service
@Transactional
public class LottoService {

	@Autowired
	LottoDAO lottoDAO;
	
	// 각 번호마다 선택된 횟수
	public Map<Integer, Integer> getNumGroup(){
		Map<Integer, Integer> result = lottoDAO.getNumGroup();
		return result;
	}
}
